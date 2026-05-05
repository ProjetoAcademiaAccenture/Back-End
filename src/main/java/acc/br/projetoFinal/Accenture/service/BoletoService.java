package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.response.BoletoResponseDTO;
import acc.br.projetoFinal.Accenture.enums.StatusBoleto;
import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.model.Boleto;
import acc.br.projetoFinal.Accenture.model.Pedido;
import acc.br.projetoFinal.Accenture.repository.BoletoRepository;
import acc.br.projetoFinal.Accenture.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class BoletoService {

    private final BoletoRepository boletoRepository;
    private final PedidoRepository pedidoRepository;
    private final ContaService contaService;

    @Transactional
    public BoletoResponseDTO gerar(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado"));

        if (pedido.getStatus() != StatusPedido.RESERVADO)
            throw new IllegalArgumentException("Pedido deve estar RESERVADO para gerar boleto");

        // Gera código de barras único (44 dígitos)
        String codigoBarras = gerarCodigoBarras();

        Boleto boleto = Boleto.builder()
                .pedido(pedido)
                .codigoBarras(codigoBarras)
                .valor(pedido.getValorTotal())
                .dataVencimento(LocalDate.now().plusDays(3)) // vencimento em 3 dias
                .status(StatusBoleto.PENDENTE)
                .build();

        Boleto boletoSalvo = boletoRepository.save(boleto);
        return BoletoResponseDTO.fromEntity(boletoSalvo);
    }

    public BoletoResponseDTO buscarPorId(Long id) {
        Boleto boleto = boletoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Boleto não encontrado"));
        return BoletoResponseDTO.fromEntity(boleto);
    }

    public BoletoResponseDTO buscarPorPedidoId(Long pedidoId) {
        Boleto boleto = boletoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Boleto não encontrado para este pedido"));
        return BoletoResponseDTO.fromEntity(boleto);
    }

    @Transactional
    public BoletoResponseDTO pagarBoleto(Long boletoId) {
        Boleto boleto = boletoRepository.findById(boletoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Boleto não encontrado"));

        if (boleto.getStatus() == StatusBoleto.PAGO)
            throw new IllegalArgumentException("Boleto já foi pago");

        if (boleto.getStatus() == StatusBoleto.CANCELADO)
            throw new IllegalArgumentException("Boleto está cancelado");

        // Paga através do ContaService (debita cliente, credita empresa)
        contaService.transferir(boleto.getPedido().getCliente().getId(), boleto.getValor(), boleto.getPedido());

        // Atualiza status do boleto
        boleto.setStatus(StatusBoleto.PAGO);
        Boleto boletoPago = boletoRepository.save(boleto);

        // Atualiza status do pedido para PAGO
        Pedido pedido = boleto.getPedido();
        pedido.setStatus(StatusPedido.PAGO);
        pedidoRepository.save(pedido);

        return BoletoResponseDTO.fromEntity(boletoPago);
    }

    @Transactional
    public void cancelarBoleto(Long boletoId) {
        Boleto boleto = boletoRepository.findById(boletoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Boleto não encontrado"));

        if (boleto.getStatus() == StatusBoleto.CANCELADO)
            throw new IllegalArgumentException("Boleto já está cancelado");

        boleto.setStatus(StatusBoleto.CANCELADO);
        boletoRepository.save(boleto);
    }

    // Gera código de barras com 44 dígitos (simulado)
    private String gerarCodigoBarras() {
        Random random = new Random();
        StringBuilder codigo = new StringBuilder();
        for (int i = 0; i < 44; i++) {
            codigo.append(random.nextInt(10));
        }
        return codigo.toString();
    }
}
