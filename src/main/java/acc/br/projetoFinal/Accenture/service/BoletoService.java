package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.response.BoletoResponseDTO;
import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
import acc.br.projetoFinal.Accenture.enums.StatusBoleto;
import acc.br.projetoFinal.Accenture.enums.StatusPagamento;
import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.exception.SaldoInsuficienteException;
import acc.br.projetoFinal.Accenture.model.Boleto;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.model.Pagamento;
import acc.br.projetoFinal.Accenture.model.Pedido;
import acc.br.projetoFinal.Accenture.repository.BoletoRepository;
import acc.br.projetoFinal.Accenture.repository.PagamentoRepository;
import acc.br.projetoFinal.Accenture.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class BoletoService {

    private static final BigDecimal DESCONTO_BOLETO = new BigDecimal("0.05");
    private static final BigDecimal MULTA_ATRASO = new BigDecimal("0.02");

    private final BoletoRepository boletoRepository;
    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;
    private final ContaService contaService;

    @Transactional
    public BoletoResponseDTO gerar(Long pagamentoId) {
        Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Pagamento não encontrado"));

        Pedido pedido = pagamento.getPedido();

        if (pedido.getStatus() != StatusPedido.RESERVADO) {
            throw new IllegalArgumentException("Pedido deve estar RESERVADO para gerar boleto");
        }

        if (boletoRepository.findByPagamentoId(pagamentoId).isPresent()) {
            throw new IllegalArgumentException("Já existe boleto para este pagamento");
        }

        BigDecimal desconto = pagamento.getValorBruto()
            .multiply(DESCONTO_BOLETO)
            .setScale(2, RoundingMode.HALF_UP);

        BigDecimal valorBoleto = pagamento.getValorBruto()
            .subtract(desconto)
            .setScale(2, RoundingMode.HALF_UP);

        pagamento.setMetodo(MetodoPagamento.BOLETO);
        pagamento.setDesconto(desconto);
        pagamento.setValorFinal(valorBoleto);
        pagamento.setStatus(StatusPagamento.PENDENTE);
        pagamentoRepository.save(pagamento);

        Boleto boleto = Boleto.builder()
            .pagamento(pagamento)
            .codigoBarras(gerarCodigoBarras())
            .valor(valorBoleto)
            .dataVencimento(LocalDate.now().plusDays(3))
            .status(StatusBoleto.PENDENTE)
            .build();

        Boleto salvo = boletoRepository.save(boleto);
        pagamento.setBoleto(salvo);
        pagamentoRepository.save(pagamento);

        return BoletoResponseDTO.fromEntity(salvo);
    }

    public BoletoResponseDTO buscarPorId(Long id) {
        Boleto boleto = boletoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Boleto não encontrado"));
        return BoletoResponseDTO.fromEntity(boleto);
    }

    public BoletoResponseDTO buscarPorPagamentoId(Long pagamentoId) {
        Boleto boleto = boletoRepository.findByPagamentoId(pagamentoId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Boleto não encontrado para este pagamento"));
        return BoletoResponseDTO.fromEntity(boleto);
    }

    @Transactional
    public BoletoResponseDTO pagarBoleto(Long boletoId, String senhaTransacao) {
        Boleto boleto = boletoRepository.findById(boletoId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Boleto não encontrado"));

        if (boleto.getStatus() == StatusBoleto.PAGO) {
            throw new IllegalArgumentException("Boleto já foi pago");
        }
        if (boleto.getStatus() == StatusBoleto.CANCELADO) {
            throw new IllegalArgumentException("Boleto está cancelado");
        }

        Pagamento pagamento = boleto.getPagamento();
        Pedido pedido = pagamento.getPedido();

        Conta contaCliente = contaService.buscarContaDoCliente(pedido.getCliente().getId());
        Conta contaEmpresa = contaService.buscarContaEmpresa();

        contaService.validarSenhaTransacao(contaCliente, senhaTransacao);

        BigDecimal multa = BigDecimal.ZERO;
        if (boleto.estaAtrasado()) {
            multa = boleto.getValor()
                .multiply(MULTA_ATRASO)
                .setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal valorTotal = boleto.getValor().add(multa).setScale(2, RoundingMode.HALF_UP);

        if (contaCliente.getSaldo().compareTo(valorTotal) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente para pagar o boleto");
        }

        String descricao = boleto.estaAtrasado()
            ? "Pagamento de boleto com atraso - pedido #" + pedido.getId()
            : "Pagamento de boleto - pedido #" + pedido.getId();

        contaService.debitarSaldo(contaCliente, valorTotal, pedido, pagamento, descricao);
        contaService.creditarSaldo(contaEmpresa, valorTotal, pedido, pagamento, descricao);

        boleto.pagar();
        boletoRepository.save(boleto);

        pagamento.setStatus(StatusPagamento.APROVADO);
        pagamento.setMetodo(MetodoPagamento.BOLETO);
        pagamento.setValorFinal(valorTotal);
        pagamento.setDataConclusao(java.time.LocalDateTime.now());
        pagamentoRepository.save(pagamento);

        pedido.setStatus(StatusPedido.PAGO);
        pedidoRepository.save(pedido);

        return BoletoResponseDTO.fromEntity(boleto);
    }

    @Transactional
    public void cancelarBoleto(Long boletoId) {
        Boleto boleto = boletoRepository.findById(boletoId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Boleto não encontrado"));

        boleto.validarCancelamento();
        boleto.cancelar();
        boletoRepository.save(boleto);

        Pagamento pagamento = boleto.getPagamento();
        if (pagamento.getStatus() == StatusPagamento.PENDENTE) {
            pagamento.setStatus(StatusPagamento.CANCELADO);
            pagamento.setDataConclusao(java.time.LocalDateTime.now());
            pagamentoRepository.save(pagamento);
        }
    }

    private String gerarCodigoBarras() {
        Random random = new Random();
        StringBuilder codigo = new StringBuilder();
        for (int i = 0; i < 44; i++) {
            codigo.append(random.nextInt(10));
        }
        return codigo.toString();
    }
}