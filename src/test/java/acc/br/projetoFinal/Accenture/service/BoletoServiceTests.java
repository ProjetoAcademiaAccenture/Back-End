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
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.repository.BoletoRepository;
import acc.br.projetoFinal.Accenture.repository.PagamentoRepository;
import acc.br.projetoFinal.Accenture.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BoletoService - Testes Unitários")
class BoletoServiceTest {

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

    // ─── fixtures ────────────────────────────────────────────────────────────

    private Cliente cliente;
    private Pedido pedido;
    private Pagamento pagamento;
    private Boleto boleto;
    private Conta contaCliente;
    private Conta contaEmpresa;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);

        pedido = new Pedido();
        pedido.setId(10L);
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.RESERVADO);

        pagamento = new Pagamento();
        pagamento.setId(100L);
        pagamento.setPedido(pedido);
        pagamento.setValorBruto(new BigDecimal("200.00"));
        pagamento.setStatus(StatusPagamento.PENDENTE);

        boleto = Boleto.builder()
                .id(1000L)
                .pagamento(pagamento)
                .codigoBarras("12345678901234567890123456789012345678901234")
                .valor(new BigDecimal("190.00"))
                .dataVencimento(LocalDate.now().plusDays(3))
                .status(StatusBoleto.PENDENTE)
                .build();

        contaCliente = new Conta();
        contaCliente.setId(1L);
        contaCliente.setSaldo(new BigDecimal("1000.00"));

        contaEmpresa = new Conta();
        contaEmpresa.setId(2L);
        contaEmpresa.setSaldo(BigDecimal.ZERO);
    }

    // =========================================================================
    // gerar()
    // =========================================================================
    @Nested
    @DisplayName("gerar()")
    class GerarTests {

        @Test
        @DisplayName("deve gerar boleto com desconto de 5% quando pedido está RESERVADO")
        void deveGerarBoletoComSucesso() {
            when(pagamentoRepository.findById(100L)).thenReturn(Optional.of(pagamento));
            when(boletoRepository.findByPagamentoId(100L)).thenReturn(Optional.empty());
            when(boletoRepository.save(any(Boleto.class))).thenAnswer(inv -> {
                Boleto b = inv.getArgument(0);
                b.setId(1000L);
                return b;
            });
            when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamento);

            BoletoResponseDTO dto = boletoService.gerar(100L);

            // desconto = 200 * 0.05 = 10.00 → valor = 190.00
            assertThat(dto).isNotNull();
            assertThat(dto.getValor()).isEqualByComparingTo("190.00");

            ArgumentCaptor<Pagamento> pagCaptor = ArgumentCaptor.forClass(Pagamento.class);
            verify(pagamentoRepository, times(2)).save(pagCaptor.capture());
            Pagamento pagSalvo = pagCaptor.getAllValues().get(0);
            assertThat(pagSalvo.getMetodo()).isEqualTo(MetodoPagamento.BOLETO);
            assertThat(pagSalvo.getDesconto()).isEqualByComparingTo("10.00");
            assertThat(pagSalvo.getValorFinal()).isEqualByComparingTo("190.00");
            assertThat(pagSalvo.getStatus()).isEqualTo(StatusPagamento.PENDENTE);

            ArgumentCaptor<Boleto> boletoCaptor = ArgumentCaptor.forClass(Boleto.class);
            verify(boletoRepository).save(boletoCaptor.capture());
            Boleto bSalvo = boletoCaptor.getValue();
            assertThat(bSalvo.getStatus()).isEqualTo(StatusBoleto.PENDENTE);
            assertThat(bSalvo.getDataVencimento()).isEqualTo(LocalDate.now().plusDays(3));
            assertThat(bSalvo.getCodigoBarras()).hasSize(44);
        }

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException quando pagamento não existe")
        void deveLancarExcecaoQuandoPagamentoNaoEncontrado() {
            when(pagamentoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> boletoService.gerar(999L))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Pagamento não encontrado");

            verify(boletoRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar IllegalArgumentException quando pedido não está RESERVADO")
        void deveLancarExcecaoQuandoPedidoNaoReservado() {
            pedido.setStatus(StatusPedido.PAGO);
            when(pagamentoRepository.findById(100L)).thenReturn(Optional.of(pagamento));

            assertThatThrownBy(() -> boletoService.gerar(100L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("RESERVADO");

            verify(boletoRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar IllegalArgumentException quando já existe boleto para o pagamento")
        void deveLancarExcecaoQuandoBoletoJaExiste() {
            when(pagamentoRepository.findById(100L)).thenReturn(Optional.of(pagamento));
            when(boletoRepository.findByPagamentoId(100L)).thenReturn(Optional.of(boleto));

            assertThatThrownBy(() -> boletoService.gerar(100L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Já existe boleto");

            verify(boletoRepository, never()).save(any());
        }
    }

    // =========================================================================
    // buscarPorId()
    // =========================================================================
    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorIdTests {

        @Test
        @DisplayName("deve retornar DTO quando boleto existe")
        void deveRetornarDTOQuandoBoletoExiste() {
            when(boletoRepository.findById(1000L)).thenReturn(Optional.of(boleto));

            BoletoResponseDTO dto = boletoService.buscarPorId(1000L);

            assertThat(dto).isNotNull();
        }

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException quando boleto não existe")
        void deveLancarExcecaoQuandoBoletoNaoExiste() {
            when(boletoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> boletoService.buscarPorId(999L))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Boleto não encontrado");
        }
    }

    // =========================================================================
    // buscarPorPagamentoId()
    // =========================================================================
    @Nested
    @DisplayName("buscarPorPagamentoId()")
    class BuscarPorPagamentoIdTests {

        @Test
        @DisplayName("deve retornar DTO quando boleto existe para o pagamento")
        void deveRetornarDTOQuandoBoletoExiste() {
            when(boletoRepository.findByPagamentoId(100L)).thenReturn(Optional.of(boleto));

            BoletoResponseDTO dto = boletoService.buscarPorPagamentoId(100L);

            assertThat(dto).isNotNull();
        }

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException quando boleto não existe para o pagamento")
        void deveLancarExcecaoQuandoBoletoNaoExiste() {
            when(boletoRepository.findByPagamentoId(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> boletoService.buscarPorPagamentoId(999L))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Boleto não encontrado para este pagamento");
        }
    }

    // =========================================================================
    // buscarPorPedidoId()
    // =========================================================================
    @Nested
    @DisplayName("buscarPorPedidoId()")
    class BuscarPorPedidoIdTests {

        @Test
        @DisplayName("deve retornar DTO quando boleto existe para o pedido")
        void deveRetornarDTOQuandoBoletoExiste() {
            when(boletoRepository.findByPagamentoPedidoId(10L)).thenReturn(Optional.of(boleto));

            BoletoResponseDTO dto = boletoService.buscarPorPedidoId(10L);

            assertThat(dto).isNotNull();
        }

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException quando boleto não existe para o pedido")
        void deveLancarExcecaoQuandoBoletoNaoExiste() {
            when(boletoRepository.findByPagamentoPedidoId(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> boletoService.buscarPorPedidoId(999L))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Boleto não encontrado para este pedido");
        }
    }

    // =========================================================================
    // pagarBoleto()
    // =========================================================================
    @Nested
    @DisplayName("pagarBoleto()")
    class PagarBoletoTests {

        @Test
        @DisplayName("deve pagar boleto em dia sem multa")
        void devePagarBoletoEmDia() {
            boleto.setDataVencimento(LocalDate.now().plusDays(1)); // não está atrasado

            when(boletoRepository.findById(1000L)).thenReturn(Optional.of(boleto));
            when(contaService.buscarContaDoCliente(1L)).thenReturn(contaCliente);
            when(contaService.buscarContaEmpresa()).thenReturn(contaEmpresa);
            doNothing().when(contaService).validarSenhaTransacao(any(), anyString());
            doNothing().when(contaService).debitarSaldo(any(), any(), any(), any(), anyString());
            doNothing().when(contaService).creditarSaldo(any(), any(), any(), any(), anyString());
            when(boletoRepository.save(any())).thenReturn(boleto);
            when(pagamentoRepository.save(any())).thenReturn(pagamento);
            when(pedidoRepository.save(any())).thenReturn(pedido);

            BoletoResponseDTO dto = boletoService.pagarBoleto(1000L, "senha123");

            assertThat(dto).isNotNull();

            // verifica debitarSaldo com valor sem multa (190.00)
            ArgumentCaptor<BigDecimal> valorCaptor = ArgumentCaptor.forClass(BigDecimal.class);
            verify(contaService).debitarSaldo(eq(contaCliente), valorCaptor.capture(), any(), any(), contains("pedido #10"));
            assertThat(valorCaptor.getValue()).isEqualByComparingTo("190.00");

            // pedido deve ficar PAGO
            ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
            verify(pedidoRepository).save(pedidoCaptor.capture());
            assertThat(pedidoCaptor.getValue().getStatus()).isEqualTo(StatusPedido.PAGO);

            // pagamento deve ficar APROVADO
            ArgumentCaptor<Pagamento> pagCaptor = ArgumentCaptor.forClass(Pagamento.class);
            verify(pagamentoRepository).save(pagCaptor.capture());
            assertThat(pagCaptor.getValue().getStatus()).isEqualTo(StatusPagamento.APROVADO);
            assertThat(pagCaptor.getValue().getMetodo()).isEqualTo(MetodoPagamento.BOLETO);
        }

        @Test
        @DisplayName("deve pagar boleto atrasado aplicando multa de 2%")
        void devePagarBoletoAtrasadoComMulta() {
            // estaAtrasado() retorna true quando dataVencimento < hoje
            boleto.setDataVencimento(LocalDate.now().minusDays(1));

            when(boletoRepository.findById(1000L)).thenReturn(Optional.of(boleto));
            when(contaService.buscarContaDoCliente(1L)).thenReturn(contaCliente);
            when(contaService.buscarContaEmpresa()).thenReturn(contaEmpresa);
            doNothing().when(contaService).validarSenhaTransacao(any(), anyString());
            doNothing().when(contaService).debitarSaldo(any(), any(), any(), any(), anyString());
            doNothing().when(contaService).creditarSaldo(any(), any(), any(), any(), anyString());
            when(boletoRepository.save(any())).thenReturn(boleto);
            when(pagamentoRepository.save(any())).thenReturn(pagamento);
            when(pedidoRepository.save(any())).thenReturn(pedido);

            boletoService.pagarBoleto(1000L, "senha123");

            // multa = 190.00 * 0.02 = 3.80 → total = 193.80
            ArgumentCaptor<BigDecimal> valorCaptor = ArgumentCaptor.forClass(BigDecimal.class);
            verify(contaService).debitarSaldo(eq(contaCliente), valorCaptor.capture(), any(), any(), contains("atraso"));
            assertThat(valorCaptor.getValue()).isEqualByComparingTo("193.80");
        }

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException quando boleto não existe")
        void deveLancarExcecaoQuandoBoletoNaoExiste() {
            when(boletoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> boletoService.pagarBoleto(999L, "senha"))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Boleto não encontrado");
        }

        @Test
        @DisplayName("deve lançar IllegalArgumentException quando boleto já foi pago")
        void deveLancarExcecaoQuandoBoletoJaPago() {
            boleto.setStatus(StatusBoleto.PAGO);
            when(boletoRepository.findById(1000L)).thenReturn(Optional.of(boleto));

            assertThatThrownBy(() -> boletoService.pagarBoleto(1000L, "senha"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("já foi pago");
        }

        @Test
        @DisplayName("deve lançar IllegalArgumentException quando boleto está cancelado")
        void deveLancarExcecaoQuandoBoletoEstaCancel() {
            boleto.setStatus(StatusBoleto.CANCELADO);
            when(boletoRepository.findById(1000L)).thenReturn(Optional.of(boleto));

            assertThatThrownBy(() -> boletoService.pagarBoleto(1000L, "senha"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cancelado");
        }

        @Test
        @DisplayName("deve lançar SaldoInsuficienteException quando saldo é insuficiente")
        void deveLancarExcecaoQuandoSaldoInsuficiente() {
            boleto.setDataVencimento(LocalDate.now().plusDays(1));
            contaCliente.setSaldo(new BigDecimal("10.00")); // saldo menor que 190.00

            when(boletoRepository.findById(1000L)).thenReturn(Optional.of(boleto));
            when(contaService.buscarContaDoCliente(1L)).thenReturn(contaCliente);
            when(contaService.buscarContaEmpresa()).thenReturn(contaEmpresa);
            doNothing().when(contaService).validarSenhaTransacao(any(), anyString());

            assertThatThrownBy(() -> boletoService.pagarBoleto(1000L, "senha"))
                    .isInstanceOf(SaldoInsuficienteException.class)
                    .hasMessageContaining("Saldo insuficiente");

            verify(contaService, never()).debitarSaldo(any(), any(), any(), any(), anyString());
        }
    }

    // =========================================================================
    // cancelarBoleto()
    // =========================================================================
    @Nested
    @DisplayName("cancelarBoleto()")
    class CancelarBoletoTests {

        @Test
        @DisplayName("deve cancelar boleto PENDENTE e marcar pagamento como CANCELADO")
        void deveCancelarBoletoPendente() {
            pagamento.setStatus(StatusPagamento.PENDENTE);
            boleto.setStatus(StatusBoleto.PENDENTE);
            boleto.setPagamento(pagamento);

            when(boletoRepository.findById(1000L)).thenReturn(Optional.of(boleto));
            doNothing().when(boletoRepository).save(any()); // void-like via save
            when(boletoRepository.save(any())).thenReturn(boleto);
            when(pagamentoRepository.save(any())).thenReturn(pagamento);

            boletoService.cancelarBoleto(1000L);

            verify(boletoRepository).save(boleto);

            ArgumentCaptor<Pagamento> pagCaptor = ArgumentCaptor.forClass(Pagamento.class);
            verify(pagamentoRepository).save(pagCaptor.capture());
            assertThat(pagCaptor.getValue().getStatus()).isEqualTo(StatusPagamento.CANCELADO);
            assertThat(pagCaptor.getValue().getDataConclusao()).isNotNull();
        }

        @Test
        @DisplayName("não deve alterar pagamento já aprovado ao cancelar boleto")
        void naoDeveAlterarPagamentoAprovadoAoCancelar() {
            pagamento.setStatus(StatusPagamento.APROVADO);
            boleto.setStatus(StatusBoleto.PENDENTE);
            boleto.setPagamento(pagamento);

            when(boletoRepository.findById(1000L)).thenReturn(Optional.of(boleto));
            when(boletoRepository.save(any())).thenReturn(boleto);

            boletoService.cancelarBoleto(1000L);

            verify(pagamentoRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException quando boleto não existe")
        void deveLancarExcecaoQuandoBoletoNaoExiste() {
            when(boletoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> boletoService.cancelarBoleto(999L))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Boleto não encontrado");
        }

        @Test
        @DisplayName("deve lançar exceção quando validarCancelamento falhar")
        void deveLancarExcecaoQuandoCancelamentoInvalido() {
            boleto.setStatus(StatusBoleto.PAGO); // não pode cancelar boleto PAGO
            when(boletoRepository.findById(1000L)).thenReturn(Optional.of(boleto));

            // validarCancelamento() deve lançar exceção para boleto PAGO
            assertThatThrownBy(() -> boletoService.cancelarBoleto(1000L))
                    .isInstanceOf(Exception.class);

            verify(boletoRepository, never()).save(any());
        }
    }
}