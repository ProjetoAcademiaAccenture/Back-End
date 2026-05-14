package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.enums.StatusBoleto;
import acc.br.projetoFinal.Accenture.enums.StatusPagamento;
import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.model.Boleto;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.Pagamento;
import acc.br.projetoFinal.Accenture.model.Pedido;
import acc.br.projetoFinal.Accenture.repository.BoletoRepository;
import acc.br.projetoFinal.Accenture.repository.PagamentoRepository;
import acc.br.projetoFinal.Accenture.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes negativos - BoletoService")
class BoletoServiceNegativeTests {

    @Mock
    private BoletoRepository boletoRepository;

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ContaService contaService;

    @InjectMocks
    private BoletoService boletoService;

    private Cliente cliente;
    private Pedido pedido;
    private Pagamento pagamento;
    private Boleto boleto;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1L)
                .build();

        pedido = Pedido.builder()
                .id(1L)
                .status(StatusPedido.RESERVADO)
                .valorTotal(new BigDecimal("150.00"))
                .cliente(cliente)
                .build();

        pagamento = Pagamento.builder()
                .id(1L)
                .pedido(pedido)
                .valorBruto(new BigDecimal("150.00"))
                .status(StatusPagamento.PENDENTE)
                .build();

        boleto = Boleto.builder()
                .id(1L)
                .pagamento(pagamento)
                .codigoBarras("12345678901234567890123456789012345678901234")
                .valor(new BigDecimal("150.00"))
                .dataVencimento(LocalDate.now().plusDays(3))
                .status(StatusBoleto.PENDENTE)
                .build();
    }

    // =========================================================
    // gerar()
    // =========================================================

    @Test
    @DisplayName("gerar: deve lançar exception quando pagamento não encontrado")
    void gerar_deveLancarException_quandoPagamentoNaoEncontrado() {
        when(pagamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boletoService.gerar(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Pagamento não encontrado");
    }

    @Test
    @DisplayName("gerar: deve lançar exception quando pedido não está RESERVADO (PAGO)")
    void gerar_deveLancarException_quandoPedidoNaoEstaReservado() {
        pedido.setStatus(StatusPedido.PAGO);
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));

        assertThatThrownBy(() -> boletoService.gerar(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("RESERVADO");
    }

    @Test
    @DisplayName("gerar: deve lançar exception quando pedido está CANCELADO")
    void gerar_deveLancarException_quandoPedidoEstaCancelado() {
        pedido.setStatus(StatusPedido.CANCELADO);
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));

        assertThatThrownBy(() -> boletoService.gerar(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("RESERVADO");
    }

    @Test
    @DisplayName("gerar: deve lançar exception quando já existe boleto para o pagamento")
    void gerar_deveLancarException_quandoBoletoJaExiste() {
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));
        when(boletoRepository.findByPagamentoId(1L)).thenReturn(Optional.of(boleto));

        assertThatThrownBy(() -> boletoService.gerar(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Já existe boleto para este pagamento");
    }

    // =========================================================
    // buscarPorId()
    // =========================================================

    @Test
    @DisplayName("buscarPorId: deve lançar exception quando boleto não encontrado")
    void buscarPorId_deveLancarException_quandoBoletoNaoEncontrado() {
        when(boletoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boletoService.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Boleto não encontrado");
    }

    // =========================================================
    // buscarPorPagamentoId()
    // =========================================================

    @Test
    @DisplayName("buscarPorPagamentoId: deve lançar exception quando boleto não encontrado")
    void buscarPorPagamentoId_deveLancarException_quandoBoletoNaoEncontrado() {
        when(boletoRepository.findByPagamentoId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boletoService.buscarPorPagamentoId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Boleto não encontrado para este pagamento");
    }

    // =========================================================
    // buscarPorPedidoId()
    // =========================================================

    @Test
    @DisplayName("buscarPorPedidoId: deve lançar exception quando boleto não encontrado")
    void buscarPorPedidoId_deveLancarException_quandoBoletoNaoEncontrado() {
        when(boletoRepository.findByPagamentoPedidoId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boletoService.buscarPorPedidoId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Boleto não encontrado para este pedido");
    }

    // =========================================================
    // pagarBoleto()
    // =========================================================

    @Test
    @DisplayName("pagarBoleto: deve lançar exception quando boleto não encontrado")
    void pagarBoleto_deveLancarException_quandoBoletoNaoEncontrado() {
        when(boletoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boletoService.pagarBoleto(99L, "senha"))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Boleto não encontrado");
    }

    @Test
    @DisplayName("pagarBoleto: deve lançar exception quando boleto já foi pago")
    void pagarBoleto_deveLancarException_quandoBoletoJaFoiPago() {
        boleto.setStatus(StatusBoleto.PAGO);
        when(boletoRepository.findById(1L)).thenReturn(Optional.of(boleto));

        assertThatThrownBy(() -> boletoService.pagarBoleto(1L, "senha"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("já foi pago");
    }

    @Test
    @DisplayName("pagarBoleto: deve lançar exception quando boleto está cancelado")
    void pagarBoleto_deveLancarException_quandoBoletoEstaCancelado() {
        boleto.setStatus(StatusBoleto.CANCELADO);
        when(boletoRepository.findById(1L)).thenReturn(Optional.of(boleto));

        assertThatThrownBy(() -> boletoService.pagarBoleto(1L, "senha"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cancelado");
    }

    // =========================================================
    // cancelarBoleto()
    // =========================================================

    @Test
    @DisplayName("cancelarBoleto: deve lançar exception quando boleto não encontrado")
    void cancelarBoleto_deveLancarException_quandoBoletoNaoEncontrado() {
        when(boletoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boletoService.cancelarBoleto(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Boleto não encontrado");
    }

    @Test
    @DisplayName("cancelarBoleto: deve lançar exception quando boleto já está cancelado")
    void cancelarBoleto_deveLancarException_quandoBoletoJaEstaCancelado() {
        boleto.setStatus(StatusBoleto.CANCELADO);
        when(boletoRepository.findById(1L)).thenReturn(Optional.of(boleto));

        assertThatThrownBy(() -> boletoService.cancelarBoleto(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("já está cancelado");
    }

    @Test
    @DisplayName("cancelarBoleto: deve lançar exception quando boleto já foi pago")
    void cancelarBoleto_deveLancarException_quandoBoletoJaFoiPago() {
        boleto.setStatus(StatusBoleto.PAGO);
        when(boletoRepository.findById(1L)).thenReturn(Optional.of(boleto));

        assertThatThrownBy(() -> boletoService.cancelarBoleto(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("já foi pago");
    }
}