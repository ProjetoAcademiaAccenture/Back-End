package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ContaRequestDTO;
import acc.br.projetoFinal.Accenture.enums.TipoExtrato;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.exception.SaldoInsuficienteException;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.model.Pagamento;
import acc.br.projetoFinal.Accenture.model.Pedido;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.repository.ContaRepository;
import acc.br.projetoFinal.Accenture.repository.ExtratoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContaService {

    private static final String NUMERO_CONTA_EMPRESA = "1234567-8";
    private static final BigDecimal LIMITE_INICIAL = new BigDecimal("1000.00");

    private final ContaRepository contaRepository;
    private final ExtratoRepository extratoRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final ExtratoService extratoService;
    private final EmailService emailService;

    @Transactional
    public Conta criarEntidade(ContaRequestDTO dto) {
        var cliente = clienteRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));

        if (contaRepository.findByClienteId(dto.getClienteId()).isPresent()) {
            throw new IllegalArgumentException("Cliente já possui uma conta");
        }

      BigDecimal saldoInicial = gerarValorAleatorio(100.00, 500.00);
      BigDecimal limiteInicial = gerarValorAleatorio(1000.00, 5000.00);

        Conta conta = Conta.builder()
            .numeroConta(gerarNumeroConta())
            .senhaTransacao(passwordEncoder.encode(dto.getSenhaTransacao()))
            .saldo(BigDecimal.ZERO)
            .limiteCreditoDisponivel(BigDecimal.ZERO)
            .tipo(dto.getTipoConta())
            .cliente(cliente)
            .build();

        conta = contaRepository.save(conta);

        creditarLimiteCredito(
            conta,
            LIMITE_INICIAL,
            null,
            null,
            "Limite inicial da conta"
        );
      log.info("Conta criada com sucesso. ID: {}", conta.getId());

      // Registro inicial no extrato
      extratoService.registrar(conta, TipoExtrato.CREDITO, saldoInicial, BigDecimal.ZERO,
          saldoInicial, "Saldo inicial de abertura", null, null);

      // Integração do EmailService do HEAD
      emailService.enviarDadosConta(
          cliente.getEmail(),
          cliente.getNome(),
          conta.getNumeroConta(),
          conta.getTipo().name()
          );

        return conta;
    }

    public Conta buscarPorId(Long id) {
        return contaRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));
    }

    public Conta buscarContaDoCliente(Long clienteId) {
        return contaRepository.findByClienteId(clienteId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));
    }

    public Conta buscarContaEmpresa() {
        return contaRepository.findByNumeroConta(NUMERO_CONTA_EMPRESA)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Conta empresa não encontrada"));
    }

    public void validarSenhaTransacao(Conta conta, String senhaDigitada) {
        if (!passwordEncoder.matches(senhaDigitada, conta.getSenhaTransacao())) {
            throw new IllegalArgumentException("Senha de transação inválida");
        }
    }

    @Transactional
    public Conta depositar(Long contaId, BigDecimal valor) {
        Conta conta = buscarPorId(contaId);

        BigDecimal saldoAntes = conta.getSaldo();
        BigDecimal saldoDepois = saldoAntes.add(valor);

        conta.setSaldo(saldoDepois);
        contaRepository.save(conta);

        extratoService.registrar(
            conta,
            TipoExtrato.CREDITO,
            valor,
            saldoAntes,
            saldoDepois,
            "Depósito em conta",
            null,
            null
        );

        return conta;
    }

    @Transactional
    public void debitarSaldo(Conta conta, BigDecimal valor, Pedido pedido, Pagamento pagamento, String descricao) {
        BigDecimal saldoAntes = conta.getSaldo();

        if (saldoAntes.compareTo(valor) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente");
        }

        BigDecimal saldoDepois = saldoAntes.subtract(valor);
        conta.setSaldo(saldoDepois);
        contaRepository.save(conta);

        extratoService.registrar(
            conta,
            TipoExtrato.DEBITO,
            valor,
            saldoAntes,
            saldoDepois,
            descricao,
            pedido,
            pagamento
        );
    }

    @Transactional
    public void creditarSaldo(Conta conta, BigDecimal valor, Pedido pedido, Pagamento pagamento, String descricao) {
        BigDecimal saldoAntes = conta.getSaldo();
        BigDecimal saldoDepois = saldoAntes.add(valor);

        conta.setSaldo(saldoDepois);
        contaRepository.save(conta);

        extratoService.registrar(
            conta,
            TipoExtrato.CREDITO,
            valor,
            saldoAntes,
            saldoDepois,
            descricao,
            pedido,
            pagamento
        );
    }

    public void debitarLimiteCredito(Conta conta, BigDecimal valor, Pedido pedido, Pagamento pagamento, String descricao) {
        BigDecimal antes = conta.getLimiteCreditoDisponivel();

        if (antes.compareTo(valor) < 0) {
            throw new SaldoInsuficienteException("Limite de crédito insuficiente");
        }

        BigDecimal depois = antes.subtract(valor);
        conta.setLimiteCreditoDisponivel(depois);
        contaRepository.save(conta);

        extratoService.registrar(
            conta,
            TipoExtrato.DEBITO,
            valor,
            antes,
            depois,
            descricao,
            pedido,
            pagamento
        );
    }

    @Transactional
    public Conta creditarLimiteCredito(Long contaId, BigDecimal valor) {
        Conta conta = buscarPorId(contaId);
        BigDecimal limiteAntes = conta.getLimiteCreditoDisponivel();
        BigDecimal limiteDepois = limiteAntes.add(valor);
        conta.setLimiteCreditoDisponivel(limiteDepois);
        contaRepository.save(conta);
        extratoService.registrar(
            conta,
            TipoExtrato.CREDITO,
            valor,
            limiteAntes,
            limiteDepois,
            "Limite de crédito adicionado",
            null,
            null
        );

        return conta;
    }

    private BigDecimal gerarValorAleatorio(double min, double max) {
        double randomDouble = ThreadLocalRandom.current().nextDouble(min, max);
        return BigDecimal.valueOf(randomDouble).setScale(2, RoundingMode.HALF_UP);
    }

  @Transactional
  public void creditarLimiteCredito(Conta conta, BigDecimal valor, Pedido pedido, Pagamento pagamento, String descricao) {
    BigDecimal antes = conta.getLimiteCreditoDisponivel();
    BigDecimal depois = antes.add(valor);

    conta.setLimiteCreditoDisponivel(depois);
    contaRepository.save(conta);

    extratoService.registrar(conta, TipoExtrato.CREDITO, valor, antes, depois, descricao, pedido, pagamento);
  }

  private String gerarNumeroConta() {
    return String.format("%07d-%d",
        ThreadLocalRandom.current().nextInt(1000000, 9999999),
        ThreadLocalRandom.current().nextInt(0, 9));
  }
}
