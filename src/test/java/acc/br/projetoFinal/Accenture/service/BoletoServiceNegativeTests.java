package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.enums.StatusBoleto;
import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.model.Boleto;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.Pedido;
import acc.br.projetoFinal.Accenture.repository.BoletoRepository;
import acc.br.projetoFinal.Accenture.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
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
class BoletoServiceNegativeTests {

    @Mock
    private BoletoRepository boletoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ContaService contaService;

    @InjectMocks
    private BoletoService boletoService;

    private Pedido pedido;
    private Boleto boleto;
    private Cliente cliente;

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

        boleto = Boleto.builder()
                .id(1L)
                .pedido(pedido)
                .codigoBarras("12345678901234567890123456789012345678901234")
                .valor(new BigDecimal("150.00"))
                .dataVencimento(LocalDate.now().plusDays(3))
                .status(StatusBoleto.PENDENTE)
                .build();
    }

    @Test
    void gerar_deveLancarException_quandoPedidoNaoEncontrado() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boletoService.gerar(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Pedido não encontrado");
    }

    @Test
    void gerar_deveLancarException_quandoPedidoNaoEstaReservado() {
        pedido.setStatus(StatusPedido.PAGO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> boletoService.gerar(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("RESERVADO");
    }

    @Test
    void gerar_deveLancarException_quandoPedidoEstaCancelado() {
        pedido.setStatus(StatusPedido.CANCELADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> boletoService.gerar(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("RESERVADO");
    }

    @Test
    void buscarPorId_deveLancarException_quandoBoletoNaoEncontrado() {
        when(boletoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boletoService.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Boleto não encontrado");
    }

    @Test
    void buscarPorPedidoId_deveLancarException_quandoBoletoNaoEncontrado() {
        when(boletoRepository.findByPedidoId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boletoService.buscarPorPedidoId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Boleto não encontrado para este pedido");
    }

    @Test
    void pagarBoleto_deveLancarException_quandoBoletoNaoEncontrado() {
        when(boletoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boletoService.pagarBoleto(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Boleto não encontrado");
    }

    @Test
    void pagarBoleto_deveLancarException_quandoBoletoJaFoiPago() {
        boleto.setStatus(StatusBoleto.PAGO);
        when(boletoRepository.findById(1L)).thenReturn(Optional.of(boleto));

        assertThatThrownBy(() -> boletoService.pagarBoleto(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("já foi pago");
    }

    @Test
    void pagarBoleto_deveLancarException_quandoBoletoEstaCancelado() {
        boleto.setStatus(StatusBoleto.CANCELADO);
        when(boletoRepository.findById(1L)).thenReturn(Optional.of(boleto));

        assertThatThrownBy(() -> boletoService.pagarBoleto(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cancelado");
    }

    @Test
    void cancelarBoleto_deveLancarException_quandoBoletoNaoEncontrado() {
        when(boletoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boletoService.cancelarBoleto(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Boleto não encontrado");
    }

    @Test
    void cancelarBoleto_deveLancarException_quandoBoletoJaEstaCancelado() {
        boleto.setStatus(StatusBoleto.CANCELADO);
        when(boletoRepository.findById(1L)).thenReturn(Optional.of(boleto));

        assertThatThrownBy(() -> boletoService.cancelarBoleto(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("já está cancelado");
    }
}