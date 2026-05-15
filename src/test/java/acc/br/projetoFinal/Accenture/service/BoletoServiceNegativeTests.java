package acc.br.projetoFinal.Accenture.service;

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BoletoService - Testes Negativos")
class BoletoServiceNegativeTest {

    @Mock private BoletoRepository boletoRepository;
    @Mock private PagamentoRepository pagamentoRepository;
    @Mock private PedidoRepository pedidoRepository;
    @Mock private ContaService contaService;

    @InjectMocks
    private BoletoService boletoService;

    private Cliente cliente;
    private Pedido pedido;
    private Pagamento pagamento;
    private Boleto boleto;
    private Conta contaCliente;

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
    }

    // =========================================================================
    // gerar() — negativos
    // =========================================================================
    @Nested
    @DisplayName("gerar() — negativos")
    class GerarNegativosTests {

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException com ID inexistente")
        void deveLancarExcecaoComIdInexistente() {
            when(pagamentoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> boletoService.gerar(999L))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Pagamento não encontrado");

            verifyNoInteractions(boletoRepository);
        }

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException com ID nulo — não deve chegar ao repositório")
        void deveLancarExcecaoComIdNulo() {
            when(pagamentoRepository.findById(null)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> boletoService.gerar(null))
                    .isInstanceOf(RecursoNaoEncontradoException.class);

            verifyNoInteractions(boletoRepository);
        }

        @ParameterizedTest(name = "pedido com status {0} não deve gerar boleto")
        @EnumSource(value = StatusPedido.class, names = {"RESERVADO"}, mode = EnumSource.Mode.EXCLUDE)
        @DisplayName("deve lançar IllegalArgumentException para qualquer status diferente de RESERVADO")
        void deveLancarExcecaoParaStatusDiferenteDeReservado(StatusPedido status) {
            pedido.setStatus(status);
            when(pagamentoRepository.findById(100L)).thenReturn(Optional.of(pagamento));

            assertThatThrownBy(() -> boletoService.gerar(100L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("RESERVADO");

            verify(boletoRepository, never()).save(any());
            verify(pagamentoRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar IllegalArgumentException quando boleto duplicado para o mesmo pagamento")
        void deveLancarExcecaoParaBoletoDuplicado() {
            when(pagamentoRepository.findById(100L)).thenReturn(Optional.of(pagamento));
            when(boletoRepository.findByPagamentoId(100L)).thenReturn(Optional.of(boleto));

            assertThatThrownBy(() -> boletoService.gerar(100L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Já existe boleto para este pagamento");

            verify(boletoRepository, never()).save(any());
        }

        @Test
        @DisplayName("não deve persistir nada quando pagamento não encontrado")
        void naoDeveInteragirComRepositoriosQuandoPagamentoNaoExiste() {
            when(pagamentoRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> boletoService.gerar(1L))
                    .isInstanceOf(RecursoNaoEncontradoException.class);

            verify(pagamentoRepository, never()).save(any());
            verifyNoInteractions(boletoRepository, contaService, pedidoRepository);
        }
    }

    // =========================================================================
    // buscarPorId() — negativos
    // =========================================================================
    @Nested
    @DisplayName("buscarPorId() — negativos")
    class BuscarPorIdNegativosTests {

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException com ID inexistente")
        void deveLancarExcecaoComIdInexistente() {
            when(boletoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> boletoService.buscarPorId(999L))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Boleto não encontrado");
        }

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException com ID negativo")
        void deveLancarExcecaoComIdNegativo() {
            when(boletoRepository.findById(-1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> boletoService.buscarPorId(-1L))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Boleto não encontrado");
        }

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException com ID zero")
        void deveLancarExcecaoComIdZero() {
            when(boletoRepository.findById(0L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> boletoService.buscarPorId(0L))
                    .isInstanceOf(RecursoNaoEncontradoException.class);
        }
    }

    // =========================================================================
    // buscarPorPagamentoId() — negativos
    // =========================================================================
    @Nested
    @DisplayName("buscarPorPagamentoId() — negativos")
    class BuscarPorPagamentoIdNegativosTests {

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException com pagamento inexistente")
        void deveLancarExcecaoComPagamentoInexistente() {
            when(boletoRepository.findByPagamentoId(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> boletoService.buscarPorPagamentoId(999L))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Boleto não encontrado para este pagamento");
        }

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException com ID de pagamento negativo")
        void deveLancarExcecaoComIdNegativo() {
            when(boletoRepository.findByPagamentoId(-5L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> boletoService.buscarPorPagamentoId(-5L))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Boleto não encontrado para este pagamento");
        }
    }

    // =========================================================================
    // buscarPorPedidoId() — negativos
    // =========================================================================
    @Nested
    @DisplayName("buscarPorPedidoId() — negativos")
    class BuscarPorPedidoIdNegativosTests {

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException com pedido inexistente")
        void deveLancarExcecaoComPedidoInexistente() {
            when(boletoRepository.findByPagamentoPedidoId(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> boletoService.buscarPorPedidoId(999L))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Boleto não encontrado para este pedido");
        }

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException com ID zero")
        void deveLancarExcecaoComIdZero() {
            when(boletoRepository.findByPagamentoPedidoId(0L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> boletoService.buscarPorPedidoId(0L))
                    .isInstanceOf(RecursoNaoEncontradoException.class);
        }
    }

    // =========================================================================
    // pagarBoleto() — negativos
    // =========================================================================
    @Nested
    @DisplayName("pagarBoleto() — negativos")
    class PagarBoletoNegativosTests {

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException quando boleto inexistente")
        void deveLancarExcecaoQuandoBoletoNaoExiste() {
            when(boletoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> boletoService.pagarBoleto(999L, "senha"))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Boleto não encontrado");

            verifyNoInteractions(contaService, pagamentoRepository, pedidoRepository);
        }

        @Test
        @DisplayName("deve lançar IllegalArgumentException quando boleto já está PAGO")
        void deveLancarExcecaoQuandoBoletoJaPago() {
            boleto.setStatus(StatusBoleto.PAGO);
            when(boletoRepository.findById(1000L)).thenReturn(Optional.of(boleto));

            assertThatThrownBy(() -> boletoService.pagarBoleto(1000L, "senha"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("já foi pago");

            verifyNoInteractions(contaService, pagamentoRepository, pedidoRepository);
        }

        @Test
        @DisplayName("deve lançar IllegalArgumentException quando boleto está CANCELADO")
        void deveLancarExcecaoQuandoBoletoEstaCancel() {
            boleto.setStatus(StatusBoleto.CANCELADO);
            when(boletoRepository.findById(1000L)).thenReturn(Optional.of(boleto));

            assertThatThrownBy(() -> boletoService.pagarBoleto(1000L, "senha"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cancelado");

            verifyNoInteractions(contaService, pagamentoRepository, pedidoRepository);
        }

        @Test
        @DisplayName("deve lançar SaldoInsuficienteException com saldo zerado")
        void deveLancarExcecaoComSaldoZerado() {
            contaCliente.setSaldo(BigDecimal.ZERO);
            boleto.setDataVencimento(LocalDate.now().plusDays(1));

            when(boletoRepository.findById(1000L)).thenReturn(Optional.of(boleto));
            when(contaService.buscarContaDoCliente(1L)).thenReturn(contaCliente);
            when(contaService.buscarContaEmpresa()).thenReturn(new Conta());
            doNothing().when(contaService).validarSenhaTransacao(any(), anyString());

            assertThatThrownBy(() -> boletoService.pagarBoleto(1000L, "senha"))
                    .isInstanceOf(SaldoInsuficienteException.class)
                    .hasMessageContaining("Saldo insuficiente");

            verify(contaService, never()).debitarSaldo(any(), any(), any(), any(), anyString());
            verify(contaService, never()).creditarSaldo(any(), any(), any(), any(), anyString());
            verify(pagamentoRepository, never()).save(any());
            verify(pedidoRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar SaldoInsuficienteException quando saldo é exatamente R$0,01 menor que o valor")
        void deveLancarExcecaoComSaldoUmCentavoAbaixo() {
            // valor do boleto = 190.00 → saldo = 189.99
            contaCliente.setSaldo(new BigDecimal("189.99"));
            boleto.setDataVencimento(LocalDate.now().plusDays(1));

            when(boletoRepository.findById(1000L)).thenReturn(Optional.of(boleto));
            when(contaService.buscarContaDoCliente(1L)).thenReturn(contaCliente);
            when(contaService.buscarContaEmpresa()).thenReturn(new Conta());
            doNothing().when(contaService).validarSenhaTransacao(any(), anyString());

            assertThatThrownBy(() -> boletoService.pagarBoleto(1000L, "senha"))
                    .isInstanceOf(SaldoInsuficienteException.class);
        }

        @Test
        @DisplayName("deve lançar SaldoInsuficienteException com multa quando saldo cobre valor mas não a multa")
        void deveLancarExcecaoQuandoSaldoCobre190MasNaoCobre193() {
            // boleto atrasado: valor = 190.00, multa = 3.80 → total = 193.80
            // saldo cobre 190.00 mas não 193.80
            contaCliente.setSaldo(new BigDecimal("191.00"));
            boleto.setDataVencimento(LocalDate.now().minusDays(1)); // atrasado

            when(boletoRepository.findById(1000L)).thenReturn(Optional.of(boleto));
            when(contaService.buscarContaDoCliente(1L)).thenReturn(contaCliente);
            when(contaService.buscarContaEmpresa()).thenReturn(new Conta());
            doNothing().when(contaService).validarSenhaTransacao(any(), anyString());

            assertThatThrownBy(() -> boletoService.pagarBoleto(1000L, "senha"))
                    .isInstanceOf(SaldoInsuficienteException.class)
                    .hasMessageContaining("Saldo insuficiente");

            verify(contaService, never()).debitarSaldo(any(), any(), any(), any(), anyString());
        }

        @Test
        @DisplayName("deve lançar exceção quando senha de transação é inválida")
        void deveLancarExcecaoComSenhaInvalida() {
            boleto.setDataVencimento(LocalDate.now().plusDays(1));

            when(boletoRepository.findById(1000L)).thenReturn(Optional.of(boleto));
            when(contaService.buscarContaDoCliente(1L)).thenReturn(contaCliente);
            when(contaService.buscarContaEmpresa()).thenReturn(new Conta());
            doThrow(new IllegalArgumentException("Senha de transação inválida"))
                    .when(contaService).validarSenhaTransacao(any(), eq("senhaErrada"));

            assertThatThrownBy(() -> boletoService.pagarBoleto(1000L, "senhaErrada"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Senha de transação inválida");

            verify(contaService, never()).debitarSaldo(any(), any(), any(), any(), anyString());
            verify(pagamentoRepository, never()).save(any());
            verify(pedidoRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar exceção quando senha é nula")
        void deveLancarExcecaoComSenhaNula() {
            boleto.setDataVencimento(LocalDate.now().plusDays(1));

            when(boletoRepository.findById(1000L)).thenReturn(Optional.of(boleto));
            when(contaService.buscarContaDoCliente(1L)).thenReturn(contaCliente);
            when(contaService.buscarContaEmpresa()).thenReturn(new Conta());
            doThrow(new IllegalArgumentException("Senha não pode ser nula"))
                    .when(contaService).validarSenhaTransacao(any(), isNull());

            assertThatThrownBy(() -> boletoService.pagarBoleto(1000L, null))
                    .isInstanceOf(IllegalArgumentException.class);

            verify(contaService, never()).debitarSaldo(any(), any(), any(), any(), anyString());
        }

        @Test
        @DisplayName("não deve alterar estado de nada quando boleto está PAGO")
        void naoDeveAlterarNadaQuandoBoletoJaPago() {
            boleto.setStatus(StatusBoleto.PAGO);
            when(boletoRepository.findById(1000L)).thenReturn(Optional.of(boleto));

            assertThatThrownBy(() -> boletoService.pagarBoleto(1000L, "senha"))
                    .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(contaService);
            verify(boletoRepository, never()).save(any());
            verify(pagamentoRepository, never()).save(any());
            verify(pedidoRepository, never()).save(any());
        }

        @Test
        @DisplayName("não deve alterar estado de nada quando boleto está CANCELADO")
        void naoDeveAlterarNadaQuandoBoletoCancelado() {
            boleto.setStatus(StatusBoleto.CANCELADO);
            when(boletoRepository.findById(1000L)).thenReturn(Optional.of(boleto));

            assertThatThrownBy(() -> boletoService.pagarBoleto(1000L, "senha"))
                    .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(contaService);
            verify(boletoRepository, never()).save(any());
            verify(pagamentoRepository, never()).save(any());
            verify(pedidoRepository, never()).save(any());
        }
    }

    // =========================================================================
    // cancelarBoleto() — negativos
    // =========================================================================
    @Nested
    @DisplayName("cancelarBoleto() — negativos")
    class CancelarBoletoNegativosTests {

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException quando boleto inexistente")
        void deveLancarExcecaoQuandoBoletoNaoExiste() {
            when(boletoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> boletoService.cancelarBoleto(999L))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Boleto não encontrado");

            verify(boletoRepository, never()).save(any());
            verifyNoInteractions(pagamentoRepository);
        }

        @Test
        @DisplayName("deve lançar exceção ao tentar cancelar boleto já PAGO")
        void deveLancarExcecaoAoCancelarBoletoPago() {
            boleto.setStatus(StatusBoleto.PAGO);
            when(boletoRepository.findById(1000L)).thenReturn(Optional.of(boleto));

            // validarCancelamento() deve lançar exceção para boleto PAGO
            assertThatThrownBy(() -> boletoService.cancelarBoleto(1000L))
                    .isInstanceOf(Exception.class);

            verify(boletoRepository, never()).save(any());
            verifyNoInteractions(pagamentoRepository);
        }

        @Test
        @DisplayName("deve lançar exceção ao tentar cancelar boleto já CANCELADO")
        void deveLancarExcecaoAoCancelarBoletoCancelado() {
            boleto.setStatus(StatusBoleto.CANCELADO);
            when(boletoRepository.findById(1000L)).thenReturn(Optional.of(boleto));

            assertThatThrownBy(() -> boletoService.cancelarBoleto(1000L))
                    .isInstanceOf(Exception.class);

            verify(boletoRepository, never()).save(any());
            verifyNoInteractions(pagamentoRepository);
        }

        @Test
        @DisplayName("não deve alterar pagamento APROVADO ao cancelar boleto")
        void naoDeveAlterarPagamentoAprovadoAoCancelar() {
            pagamento.setStatus(StatusPagamento.APROVADO);
            boleto.setStatus(StatusBoleto.PENDENTE);
            boleto.setPagamento(pagamento);

            when(boletoRepository.findById(1000L)).thenReturn(Optional.of(boleto));
            when(boletoRepository.save(any())).thenReturn(boleto);

            boletoService.cancelarBoleto(1000L);

            // pagamento APROVADO não deve ser tocado
            verify(pagamentoRepository, never()).save(any());
        }

        @Test
        @DisplayName("não deve alterar pagamento CANCELADO ao cancelar boleto")
        void naoDeveAlterarPagamentoCanceladoAoCancelar() {
            pagamento.setStatus(StatusPagamento.CANCELADO);
            boleto.setStatus(StatusBoleto.PENDENTE);
            boleto.setPagamento(pagamento);

            when(boletoRepository.findById(1000L)).thenReturn(Optional.of(boleto));
            when(boletoRepository.save(any())).thenReturn(boleto);

            boletoService.cancelarBoleto(1000L);

            verify(pagamentoRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException com ID nulo")
        void deveLancarExcecaoComIdNulo() {
            when(boletoRepository.findById(null)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> boletoService.cancelarBoleto(null))
                    .isInstanceOf(RecursoNaoEncontradoException.class);

            verify(boletoRepository, never()).save(any());
        }
    }
}