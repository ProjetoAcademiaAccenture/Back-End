package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ContaRequestDTO;
import acc.br.projetoFinal.Accenture.enums.TipoConta;
import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.exception.SaldoInsuficienteException;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.model.Pagamento;
import acc.br.projetoFinal.Accenture.model.Pedido;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.repository.ContaRepository;
import acc.br.projetoFinal.Accenture.repository.ExtratoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContaService - Testes Negativos")
class ContaServiceNegativeTests {

    @Mock private ContaRepository contaRepository;
    @Mock private ExtratoRepository extratoRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private ExtratoService extratoService;
    @Mock private EmailService emailService;

    @InjectMocks
    private ContaService contaService;

    private Cliente cliente;
    private Conta contaComSaldoBaixo;
    private Pedido pedido;
    private Pagamento pagamento;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1L)
                .nome("Cliente Teste")
                .email("cliente@email.com")
                .build();

        contaComSaldoBaixo = Conta.builder()
                .id(1L)
                .numeroConta("123456-1")
                .saldo(new BigDecimal("100.00"))
                .limiteCreditoDisponivel(new BigDecimal("200.00"))
                .senhaTransacao("$2a$hash_senha_correta")
                .build();

        pedido = Pedido.builder()
                .id(1L)
                .valorBruto(new BigDecimal("1000.00"))
                .desconto(BigDecimal.ZERO)
                .valorFinal(new BigDecimal("1000.00"))
                .status(StatusPedido.RESERVADO)
                .build();

        pagamento = new Pagamento();
    }

    // =========================================================================
    // criarEntidade() — negativos
    // =========================================================================
    @Nested
    @DisplayName("criarEntidade() — negativos")
    class CriarEntidadeNegativosTests {

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException quando cliente não existe")
        void deveLancarExcecaoQuandoClienteNaoExiste() {
            ContaRequestDTO dto = new ContaRequestDTO();
            dto.setClienteId(99L);

            when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> contaService.criarEntidade(dto))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Cliente não encontrado");

            verify(clienteRepository).findById(99L);
            verify(contaRepository, never()).save(any());
            verifyNoInteractions(extratoService, emailService, passwordEncoder);
        }

        @Test
        @DisplayName("deve lançar IllegalArgumentException quando cliente já possui conta")
        void deveLancarExcecaoQuandoClienteJaPossuiConta() {
            ContaRequestDTO dto = new ContaRequestDTO();
            dto.setClienteId(1L);
            dto.setSenhaTransacao("senha123");
            dto.setTipoConta(TipoConta.CORRENTE);

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(contaRepository.findByClienteId(1L)).thenReturn(Optional.of(contaComSaldoBaixo));

            assertThatThrownBy(() -> contaService.criarEntidade(dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("já possui uma conta");

            verify(contaRepository, never()).save(any());
            verifyNoInteractions(extratoService, emailService);
        }

        @Test
        @DisplayName("não deve interagir com extrato nem e-mail quando criação falhar")
        void naoDeveInteragirComExtratoNemEmailQuandoFalhar() {
            ContaRequestDTO dto = new ContaRequestDTO();
            dto.setClienteId(99L);
            when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> contaService.criarEntidade(dto))
                    .isInstanceOf(RecursoNaoEncontradoException.class);

            verifyNoInteractions(extratoService, emailService, contaRepository);
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
            when(contaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> contaService.buscarPorId(99L))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Conta não encontrada");

            verify(contaRepository).findById(99L);
        }

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException com ID negativo")
        void deveLancarExcecaoComIdNegativo() {
            when(contaRepository.findById(-1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> contaService.buscarPorId(-1L))
                    .isInstanceOf(RecursoNaoEncontradoException.class);
        }
    }

    // =========================================================================
    // buscarContaDoCliente() — negativos
    // =========================================================================
    @Nested
    @DisplayName("buscarContaDoCliente() — negativos")
    class BuscarContaDoClienteNegativosTests {

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException quando cliente inexistente")
        void deveLancarExcecaoQuandoClienteInexistente() {
            when(contaRepository.findByClienteId(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> contaService.buscarContaDoCliente(99L))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Conta não encontrada");

            verify(contaRepository).findByClienteId(99L);
        }
    }

    // =========================================================================
    // buscarContaEmpresa() — negativos
    // =========================================================================
    @Nested
    @DisplayName("buscarContaEmpresa() — negativos")
    class BuscarContaEmpresaNegativosTests {

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException quando conta empresa não cadastrada")
        void deveLancarExcecaoQuandoContaEmpresaNaoExiste() {
            when(contaRepository.findByNumeroConta("1234567-8")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> contaService.buscarContaEmpresa())
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Conta empresa não encontrada");

            verify(contaRepository).findByNumeroConta("1234567-8");
        }
    }

    // =========================================================================
    // validarSenhaTransacao() — negativos
    // =========================================================================
    @Nested
    @DisplayName("validarSenhaTransacao() — negativos")
    class ValidarSenhaNegativosTests {

        @Test
        @DisplayName("deve lançar IllegalArgumentException com senha errada")
        void deveLancarExcecaoComSenhaErrada() {
            when(passwordEncoder.matches("senhaErrada", "$2a$hash_senha_correta")).thenReturn(false);

            assertThatThrownBy(() -> contaService.validarSenhaTransacao(contaComSaldoBaixo, "senhaErrada"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Senha de transação inválida");

            verify(passwordEncoder).matches("senhaErrada", "$2a$hash_senha_correta");
        }

        @Test
        @DisplayName("deve lançar IllegalArgumentException com senha vazia")
        void deveLancarExcecaoComSenhaVazia() {
            when(passwordEncoder.matches("", "$2a$hash_senha_correta")).thenReturn(false);

            assertThatThrownBy(() -> contaService.validarSenhaTransacao(contaComSaldoBaixo, ""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Senha de transação inválida");
        }

        @Test
        @DisplayName("deve lançar IllegalArgumentException com senha nula")
        void deveLancarExcecaoComSenhaNula() {
            when(passwordEncoder.matches(null, "$2a$hash_senha_correta")).thenReturn(false);

            assertThatThrownBy(() -> contaService.validarSenhaTransacao(contaComSaldoBaixo, null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // =========================================================================
    // depositar() — negativos
    // =========================================================================
    @Nested
    @DisplayName("depositar() — negativos")
    class DepositarNegativosTests {

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException ao depositar em conta inexistente")
        void deveLancarExcecaoQuandoContaInexistente() {
            when(contaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> contaService.depositar(99L, new BigDecimal("500.00")))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Conta não encontrada");

            verify(contaRepository).findById(99L);
            verify(contaRepository, never()).save(any());
            verifyNoInteractions(extratoService);
        }

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException com ID negativo")
        void deveLancarExcecaoComIdNegativo() {
            when(contaRepository.findById(-5L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> contaService.depositar(-5L, new BigDecimal("100.00")))
                    .isInstanceOf(RecursoNaoEncontradoException.class);

            verify(contaRepository, never()).save(any());
        }
    }

    // =========================================================================
    // debitarSaldo() — negativos
    // =========================================================================
    @Nested
    @DisplayName("debitarSaldo() — negativos")
    class DebitarSaldoNegativosTests {

        @Test
        @DisplayName("deve lançar SaldoInsuficienteException quando saldo insuficiente")
        void deveLancarExcecaoComSaldoInsuficiente() {
            // saldo = 100.00, tentando debitar 500.00
            assertThatThrownBy(() ->
                    contaService.debitarSaldo(contaComSaldoBaixo, new BigDecimal("500.00"),
                            pedido, pagamento, "Pagamento"))
                    .isInstanceOf(SaldoInsuficienteException.class)
                    .hasMessageContaining("Saldo insuficiente");

            verify(contaRepository, never()).save(any());
            verifyNoInteractions(extratoService);
        }

        @Test
        @DisplayName("deve lançar SaldoInsuficienteException com R$0,01 acima do saldo — boundary")
        void deveLancarExcecaoComUmCentavoAcimaDoSaldo() {
            // saldo = 100.00, tentando debitar 100.01
            assertThatThrownBy(() ->
                    contaService.debitarSaldo(contaComSaldoBaixo, new BigDecimal("100.01"),
                            pedido, pagamento, "Pagamento"))
                    .isInstanceOf(SaldoInsuficienteException.class);

            verify(contaRepository, never()).save(any());
            verifyNoInteractions(extratoService);
        }

        @Test
        @DisplayName("não deve alterar saldo da conta quando lançar exceção")
        void naoDeveAlterarSaldoQuandoLancarExcecao() {
            BigDecimal saldoAntes = contaComSaldoBaixo.getSaldo();

            assertThatThrownBy(() ->
                    contaService.debitarSaldo(contaComSaldoBaixo, new BigDecimal("999.00"),
                            pedido, pagamento, "Pagamento"))
                    .isInstanceOf(SaldoInsuficienteException.class);

            // saldo não deve ter sido modificado
            assertThat(contaComSaldoBaixo.getSaldo()).isEqualByComparingTo(saldoAntes);
        }
    }

    // =========================================================================
    // debitarLimiteCredito() — negativos
    // =========================================================================
    @Nested
    @DisplayName("debitarLimiteCredito() — negativos")
    class DebitarLimiteCreditoNegativosTests {

        @Test
        @DisplayName("deve lançar SaldoInsuficienteException quando limite insuficiente")
        void deveLancarExcecaoComLimiteInsuficiente() {
            // limiteCreditoDisponivel = 200.00, tentando debitar 1000.00
            assertThatThrownBy(() ->
                    contaService.debitarLimiteCredito(contaComSaldoBaixo, new BigDecimal("1000.00"),
                            pedido, pagamento, "Compra crédito"))
                    .isInstanceOf(SaldoInsuficienteException.class)
                    .hasMessageContaining("Limite de crédito insuficiente");

            verify(contaRepository, never()).save(any());
            verifyNoInteractions(extratoService);
        }

        @Test
        @DisplayName("deve lançar SaldoInsuficienteException com R$0,01 acima do limite — boundary")
        void deveLancarExcecaoComUmCentavoAcimaDoLimite() {
            // limiteCreditoDisponivel = 200.00, tentando debitar 200.01
            assertThatThrownBy(() ->
                    contaService.debitarLimiteCredito(contaComSaldoBaixo, new BigDecimal("200.01"),
                            pedido, pagamento, "Compra crédito"))
                    .isInstanceOf(SaldoInsuficienteException.class);

            verify(contaRepository, never()).save(any());
            verifyNoInteractions(extratoService);
        }

        @Test
        @DisplayName("não deve alterar limite quando lançar exceção")
        void naoDeveAlterarLimiteQuandoLancarExcecao() {
            BigDecimal limiteAntes = contaComSaldoBaixo.getLimiteCreditoDisponivel();

            assertThatThrownBy(() ->
                    contaService.debitarLimiteCredito(contaComSaldoBaixo, new BigDecimal("999.00"),
                            pedido, pagamento, "Compra crédito"))
                    .isInstanceOf(SaldoInsuficienteException.class);

            assertThat(contaComSaldoBaixo.getLimiteCreditoDisponivel()).isEqualByComparingTo(limiteAntes);
        }
    }

    // =========================================================================
    // creditarLimiteCredito(Long, BigDecimal) — negativos
    // =========================================================================
    @Nested
    @DisplayName("creditarLimiteCredito(Long, BigDecimal) — negativos")
    class CreditarLimiteCreditoPorIdNegativosTests {

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException quando conta não encontrada por ID")
        void deveLancarExcecaoQuandoContaNaoExiste() {
            when(contaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> contaService.creditarLimiteCredito(99L, new BigDecimal("500.00")))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Conta não encontrada");

            verify(contaRepository, never()).save(any());
            verifyNoInteractions(extratoService);
        }

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException com ID zero")
        void deveLancarExcecaoComIdZero() {
            when(contaRepository.findById(0L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> contaService.creditarLimiteCredito(0L, new BigDecimal("100.00")))
                    .isInstanceOf(RecursoNaoEncontradoException.class);
        }
    }
}