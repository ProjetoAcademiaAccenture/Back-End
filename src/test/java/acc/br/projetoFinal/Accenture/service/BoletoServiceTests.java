
package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.response.BoletoResponseDTO;
import acc.br.projetoFinal.Accenture.enums.StatusBoleto;
import acc.br.projetoFinal.Accenture.enums.StatusPedido;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoletoServiceTests {

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
    void gerar_deveRetornarBoletoResponseDTO_quandoPedidoReservado() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(boletoRepository.save(any(Boleto.class))).thenReturn(boleto);

        BoletoResponseDTO resultado = boletoService.gerar(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getValor()).isEqualByComparingTo(new BigDecimal("150.00"));
        verify(boletoRepository, times(1)).save(any(Boleto.class));
    }

    @Test
    void gerar_deveDefinirVencimentoEmTresDias() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(boletoRepository.save(any(Boleto.class))).thenReturn(boleto);

        BoletoResponseDTO resultado = boletoService.gerar(1L);

        assertThat(resultado.getDataVencimento()).isEqualTo(LocalDate.now().plusDays(3));
    }

    @Test
    void gerar_deveDefinirStatusPendente() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(boletoRepository.save(any(Boleto.class))).thenReturn(boleto);

        BoletoResponseDTO resultado = boletoService.gerar(1L);

    assertThat(resultado.getStatus()).isEqualTo("PENDENTE");    }

    @Test
    void buscarPorId_deveRetornarBoleto_quandoExistir() {
        when(boletoRepository.findById(1L)).thenReturn(Optional.of(boleto));

        BoletoResponseDTO resultado = boletoService.buscarPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    void buscarPorPedidoId_deveRetornarBoleto_quandoExistir() {
        when(boletoRepository.findByPedidoId(1L)).thenReturn(Optional.of(boleto));

        BoletoResponseDTO resultado = boletoService.buscarPorPedidoId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getValor()).isEqualByComparingTo(new BigDecimal("150.00"));
    }

    @Test
    void pagarBoleto_deveAtualizarStatusParaPago() {
        when(boletoRepository.findById(1L)).thenReturn(Optional.of(boleto));
        when(boletoRepository.save(any(Boleto.class))).thenReturn(boleto);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        doNothing().when(contaService).transferir(anyLong(), any(BigDecimal.class), any(Pedido.class));

        BoletoResponseDTO resultado = boletoService.pagarBoleto(1L);

        assertThat(resultado).isNotNull();
        verify(contaService, times(1)).transferir(anyLong(), any(BigDecimal.class), any(Pedido.class));
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void pagarBoleto_deveAtualizarStatusDoPedidoParaPago() {
        when(boletoRepository.findById(1L)).thenReturn(Optional.of(boleto));
        when(boletoRepository.save(any(Boleto.class))).thenReturn(boleto);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        doNothing().when(contaService).transferir(anyLong(), any(BigDecimal.class), any(Pedido.class));

        boletoService.pagarBoleto(1L);

        assertThat(pedido.getStatus()).isEqualTo(StatusPedido.PAGO);
    }

    @Test
    void cancelarBoleto_deveCancelarBoleto_quandoPendente() {
        when(boletoRepository.findById(1L)).thenReturn(Optional.of(boleto));
        when(boletoRepository.save(any(Boleto.class))).thenReturn(boleto);

        boletoService.cancelarBoleto(1L);

        assertThat(boleto.getStatus()).isEqualTo(StatusBoleto.CANCELADO);
        verify(boletoRepository, times(1)).save(boleto);
    }
}