package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ContaRequestDTO;
import acc.br.projetoFinal.Accenture.enums.TipoExtrato;
import acc.br.projetoFinal.Accenture.enums.TipoConta;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.exception.SaldoInsuficienteException;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.model.Extrato;
import acc.br.projetoFinal.Accenture.model.Pedido;
import acc.br.projetoFinal.Accenture.repository.ContaRepository;
import acc.br.projetoFinal.Accenture.repository.ExtratoRepository;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;
    private final ExtratoRepository extratoRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    // Helper interno: grava uma linha no extrato
    private void registrarExtrato(Conta conta, TipoExtrato tipo, BigDecimal valor,
                                  BigDecimal saldoAntes, String descricao, Pedido pedido) {
        extratoRepository.save(Extrato.builder()
                .conta(conta)
                .tipo(tipo)
                .valor(valor)
                .saldoAntes(saldoAntes)
                .saldoDepois(conta.getSaldo())
                .descricao(descricao)
                .pedido(pedido)
                .dataHora(LocalDateTime.now())
                .build());
    }

    @Transactional
    public Conta criarEntidade(ContaRequestDTO dto) {
        return salvarConta(dto);
    }

    private Conta salvarConta(ContaRequestDTO dto) {
        // Verify client exists and doesn't already have an account of this type
        var clienteExistente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));

        var contaExistente = contaRepository.findByClienteId(dto.getClienteId());
        if (contaExistente.isPresent()) {
            throw new IllegalArgumentException("Cliente já possui uma conta");
        }

        Conta newConta = new Conta();
        newConta.setSaldo(gerarSaldo(100.00, 500.00));
        newConta.setLimiteCredito(gerarLimiteCredito());
        newConta.setNumeroConta(gerarNumeroConta());
        newConta.setTipo(dto.getTipoConta());
        newConta.setSenhaTransacao(
            passwordEncoder.encode(dto.getSenhaTransacao())
        );
        newConta.setCliente(clienteExistente);

        return contaRepository.save(newConta);
    }

    @Transactional
    public void depositar(Long contaId, BigDecimal valor) {
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));
        BigDecimal saldoAntes = conta.getSaldo();
        conta.setSaldo(saldoAntes.add(valor));
        contaRepository.save(conta);
        registrarExtrato(conta, TipoExtrato.CREDITO, valor, saldoAntes,
                "Depósito em conta", null);
    }

    @Transactional
    public void transferir(Long clienteId, BigDecimal valor, Pedido pedido) {
        Conta contaCliente = contaRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));
        Conta contaEmpresa = contaRepository.findByTipo(TipoConta.JURIDICA)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta empresa não encontrada"));

        if (contaCliente.getSaldo().compareTo(valor) < 0)
            throw new SaldoInsuficienteException("Saldo insuficiente: " + contaCliente.getSaldo());

        BigDecimal antesCliente = contaCliente.getSaldo();
        BigDecimal antesEmpresa = contaEmpresa.getSaldo();

        contaCliente.setSaldo(antesCliente.subtract(valor));
        contaEmpresa.setSaldo(antesEmpresa.add(valor));
        contaRepository.save(contaCliente);
        contaRepository.save(contaEmpresa);

        String desc = "Pagamento pedido #" + pedido.getId();
        registrarExtrato(contaCliente, TipoExtrato.DEBITO, valor, antesCliente, desc, pedido);
        registrarExtrato(contaEmpresa, TipoExtrato.CREDITO, valor, antesEmpresa, desc, pedido);
    }

    @Transactional
    public void estornarComMulta(Long clienteId, BigDecimal estorno, BigDecimal multa, Pedido pedido) {
        Conta contaCliente = contaRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));
        Conta contaEmpresa = contaRepository.findByTipo(TipoConta.JURIDICA)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta empresa não encontrada"));

        BigDecimal antesCliente = contaCliente.getSaldo();
        BigDecimal antesEmpresa = contaEmpresa.getSaldo();

        contaCliente.setSaldo(antesCliente.add(estorno));
        contaEmpresa.setSaldo(antesEmpresa.subtract(estorno));
        contaRepository.save(contaCliente);
        contaRepository.save(contaEmpresa);

        String descEstorno = "Estorno pedido #" + pedido.getId();
        String descMulta = "Multa cancelamento pedido #" + pedido.getId();
        registrarExtrato(contaCliente, TipoExtrato.ESTORNO, estorno, antesCliente, descEstorno, pedido);
        registrarExtrato(contaEmpresa, TipoExtrato.ESTORNO, estorno, antesEmpresa, descEstorno, pedido);
        registrarExtrato(contaEmpresa, TipoExtrato.MULTA, multa, contaEmpresa.getSaldo(), descMulta, pedido);
    }

    private String gerarNumeroConta() {
        StringBuilder sb = new StringBuilder();
        int[] conta = new int[6];
        for (int i = 0; i < conta.length; i++) {
            conta[i] = (int) (Math.random() * 100);
            sb.append(conta[i]);
        }
        sb.append("-").append((int) (Math.random() * 10));
        return sb.toString();
    }

    private BigDecimal gerarSaldo(double min, double max) {
        // Gera um double aleatório no intervalo
        double randomDouble = ThreadLocalRandom.current().nextDouble(min, max);

        // Converte para BigDecimal com 2 casas decimais e arredondamento padrão
        return BigDecimal.valueOf(randomDouble)
            .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal gerarLimiteCredito() {
        return gerarSaldo (1000.00, 5000.00);
    }
}
