package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ContaRequestDTO;
import acc.br.projetoFinal.Accenture.enums.TipoConta;
import acc.br.projetoFinal.Accenture.enums.TipoExtrato;
import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
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
import org.mockito.ArgumentCaptor;
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
@DisplayName("ContaService - Testes Positivos")
class ContaServiceTests {

    @Mock private ContaRepository contaRepository;
    @Mock private ExtratoRepository extratoRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private ExtratoService extratoService;
    @Mock private EmailService emailService;

    @InjectMocks
    private ContaService contaService;

    private Cliente cliente;
    private Conta conta;
    private Conta contaEmpresa;
    private Pedido pedido;
    private Pagamento pagamento;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1L)
                .nome("Cliente Teste")
                .email("cliente@email.com")
                .build();

        conta = Conta.builder()
                .id(1L)
                .numeroConta("1234567-1")
                .saldo(new BigDecimal("5000.00"))
                .limiteCreditoDisponivel(new BigDecimal("2000.00"))
                .senhaTransacao("$2a$hash_senha")
                .cliente(cliente)
                .tipo(TipoConta.CORRENTE)
                .build();

        contaEmpresa = Conta.builder()
                .id(2L)
                .numeroConta("1234567-8")
                .saldo(new BigDecimal("10000.00"))
                .limiteCreditoDisponivel(BigDecimal.ZERO)
                .tipo(TipoConta.JURIDICA)
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
    // criarEntidade()
    // =========================================================================
    @Nested
    @DisplayName("criarEntidade()")
    class CriarEntidadeTests {

        @Test
        @DisplayName("deve criar conta com sucesso, registrar extrato e enviar e-mail")
        void deveCriarContaComSucesso() {
            ContaRequestDTO dto = new ContaRequestDTO();
            dto.setClienteId(1L);
            dto.setSenhaTransacao("senha123");
            dto.setTipoConta(TipoConta.CORRENTE);

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(contaRepository.findByClienteId(1L)).thenReturn(Optional.empty());
            when(passwordEncoder.encode("senha123")).thenReturn("$2a$hash_gerado");
            when(contaRepository.save(any(Conta.class))).thenAnswer(inv -> {
                Conta c = inv.getArgument(0);
                c.setId(10L);
                return c;
            });

            Conta resultado = contaService.criarEntidade(dto);

            // campos básicos
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(10L);
            assertThat(resultado.getTipo()).isEqualTo(TipoConta.CORRENTE);
            assertThat(resultado.getNumeroConta()).matches("\\d{7}-\\d"); // formato gerarNumeroConta()

            // 2 saves: 1 da conta + 1 do creditarLimiteCredito interno
            verify(contaRepository, atLeast(1)).save(any(Conta.class));

            // extrato chamado pelo menos 2x: creditarLimiteCredito + saldo inicial
            verify(extratoService, atLeast(2)).registrar(
                    any(), any(), any(), any(), any(), anyString(), any(), any());

            // e-mail disparado com os dados corretos
            verify(emailService).enviarDadosConta(
                    eq("cliente@email.com"),
                    eq("Cliente Teste"),
                    anyString(),
                    eq(TipoConta.CORRENTE.name())
            );
        }

        @Test
        @DisplayName("deve gerar numero de conta no formato correto (7 dígitos + hífen + 1 dígito)")
        void deveGerarNumeroContaNoFormatoCorreto() {
            ContaRequestDTO dto = new ContaRequestDTO();
            dto.setClienteId(1L);
            dto.setSenhaTransacao("abc");
            dto.setTipoConta(TipoConta.POUPANCA);

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(contaRepository.findByClienteId(1L)).thenReturn(Optional.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("hash");
            when(contaRepository.save(any(Conta.class))).thenAnswer(inv -> {
                Conta c = inv.getArgument(0);
                c.setId(99L);
                return c;
            });

            Conta resultado = contaService.criarEntidade(dto);

            assertThat(resultado.getNumeroConta()).matches("\\d{7}-\\d");
        }

        @Test
        @DisplayName("deve codificar a senha de transação antes de persistir")
        void deveCodificarSenhaTransacao() {
            ContaRequestDTO dto = new ContaRequestDTO();
            dto.setClienteId(1L);
            dto.setSenhaTransacao("minhasenha");
            dto.setTipoConta(TipoConta.CORRENTE);

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(contaRepository.findByClienteId(1L)).thenReturn(Optional.empty());
            when(passwordEncoder.encode("minhasenha")).thenReturn("$2a$hash_encoded");
            when(contaRepository.save(any(Conta.class))).thenAnswer(inv -> {
                Conta c = inv.getArgument(0);
                c.setId(5L);
                return c;
            });

            contaService.criarEntidade(dto);

            ArgumentCaptor<Conta> captor = ArgumentCaptor.forClass(Conta.class);
            verify(contaRepository, atLeastOnce()).save(captor.capture());
            // primeira chamada é a conta principal
            assertThat(captor.getAllValues().get(0).getSenhaTransacao()).isEqualTo("$2a$hash_encoded");
        }
    }

    // =========================================================================
    // buscarPorId()
    // =========================================================================
    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorIdTests {

        @Test
        @DisplayName("deve retornar conta quando ID existe")
        void deveRetornarContaQuandoIdExiste() {
            when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

            Conta resultado = contaService.buscarPorId(1L);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getNumeroConta()).isEqualTo("1234567-1");
            verify(contaRepository).findById(1L);
        }
    }

    // =========================================================================
    // buscarContaDoCliente()
    // =========================================================================
    @Nested
    @DisplayName("buscarContaDoCliente()")
    class BuscarContaDoClienteTests {

        @Test
        @DisplayName("deve retornar conta do cliente quando clienteId existe")
        void deveRetornarContaDoClienteExistente() {
            when(contaRepository.findByClienteId(1L)).thenReturn(Optional.of(conta));

            Conta resultado = contaService.buscarContaDoCliente(1L);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getNumeroConta()).isEqualTo("1234567-1");
            verify(contaRepository).findByClienteId(1L);
        }
    }

    // =========================================================================
    // buscarContaEmpresa()
    // =========================================================================
    @Nested
    @DisplayName("buscarContaEmpresa()")
    class BuscarContaEmpresaTests {

        @Test
        @DisplayName("deve retornar conta empresa pelo numero fixo 1234567-8")
        void deveRetornarContaEmpresa() {
            when(contaRepository.findByNumeroConta("1234567-8")).thenReturn(Optional.of(contaEmpresa));

            Conta resultado = contaService.buscarContaEmpresa();

            assertThat(resultado).isNotNull();
            assertThat(resultado.getNumeroConta()).isEqualTo("1234567-8");
            assertThat(resultado.getTipo()).isEqualTo(TipoConta.JURIDICA);
            verify(contaRepository).findByNumeroConta("1234567-8");
        }
    }

    // =========================================================================
    // validarSenhaTransacao()
    // =========================================================================
    @Nested
    @DisplayName("validarSenhaTransacao()")
    class ValidarSenhaTests {

        @Test
        @DisplayName("deve validar senha correta sem lançar exceção")
        void deveValidarSenhaCorreta() {
            when(passwordEncoder.matches("senha123", "$2a$hash_senha")).thenReturn(true);

            assertThatNoException().isThrownBy(() ->
                    contaService.validarSenhaTransacao(conta, "senha123"));

            verify(passwordEncoder).matches("senha123", "$2a$hash_senha");
        }
    }

    // =========================================================================
    // depositar()
    // =========================================================================
    @Nested
    @DisplayName("depositar()")
    class DepositarTests {

        @Test
        @DisplayName("deve depositar e atualizar saldo corretamente")
        void deveDepositarComSucesso() {
            when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));
            when(contaRepository.save(any())).thenReturn(conta);

            Conta resultado = contaService.depositar(1L, new BigDecimal("500.00"));

            assertThat(resultado.getSaldo()).isEqualByComparingTo("5500.00");
            verify(contaRepository).save(conta);
            verify(extratoService).registrar(
                    eq(conta), eq(TipoExtrato.CREDITO),
                    eq(new BigDecimal("500.00")),
                    eq(new BigDecimal("5000.00")),
                    eq(new BigDecimal("5500.00")),
                    eq("Depósito em conta"),
                    isNull(), isNull());
        }

        @Test
        @DisplayName("deve depositar valor zero sem alterar saldo")
        void deveDepositarValorZeroSemAlterarSaldo() {
            when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));
            when(contaRepository.save(any())).thenReturn(conta);

            Conta resultado = contaService.depositar(1L, BigDecimal.ZERO);

            assertThat(resultado.getSaldo()).isEqualByComparingTo("5000.00");
            verify(contaRepository).save(conta);
        }
    }

    // =========================================================================
    // debitarSaldo()
    // =========================================================================
    @Nested
    @DisplayName("debitarSaldo()")
    class DebitarSaldoTests {

        @Test
        @DisplayName("deve debitar saldo e registrar extrato")
        void deveDebitarSaldoComSucesso() {
            when(contaRepository.save(any())).thenReturn(conta);

            contaService.debitarSaldo(conta, new BigDecimal("1000.00"), pedido, pagamento, "Pagamento");

            assertThat(conta.getSaldo()).isEqualByComparingTo("4000.00");
            verify(contaRepository).save(conta);
            verify(extratoService).registrar(
                    eq(conta), eq(TipoExtrato.DEBITO),
                    eq(new BigDecimal("1000.00")),
                    eq(new BigDecimal("5000.00")),
                    eq(new BigDecimal("4000.00")),
                    eq("Pagamento"),
                    eq(pedido), eq(pagamento));
        }

        @Test
        @DisplayName("deve debitar saldo exatamente igual ao disponível — boundary")
        void deveDebitarSaldoExatamenteIgualAoDisponivel() {
            when(contaRepository.save(any())).thenReturn(conta);

            contaService.debitarSaldo(conta, new BigDecimal("5000.00"), pedido, pagamento, "Tudo");

            assertThat(conta.getSaldo()).isEqualByComparingTo("0.00");
            verify(contaRepository).save(conta);
        }
    }

    // =========================================================================
    // creditarSaldo()
    // =========================================================================
    @Nested
    @DisplayName("creditarSaldo()")
    class CreditarSaldoTests {

        @Test
        @DisplayName("deve creditar saldo e registrar extrato corretamente")
        void deveCreditarSaldoComSucesso() {
            when(contaRepository.save(any())).thenReturn(conta);

            contaService.creditarSaldo(conta, new BigDecimal("300.00"), pedido, pagamento, "Estorno");

            assertThat(conta.getSaldo()).isEqualByComparingTo("5300.00");
            verify(contaRepository).save(conta);
            verify(extratoService).registrar(
                    eq(conta), eq(TipoExtrato.CREDITO),
                    eq(new BigDecimal("300.00")),
                    eq(new BigDecimal("5000.00")),
                    eq(new BigDecimal("5300.00")),
                    eq("Estorno"),
                    eq(pedido), eq(pagamento));
        }
    }

    // =========================================================================
    // debitarLimiteCredito(Conta, valor, pedido, pagamento, descricao)
    // =========================================================================
    @Nested
    @DisplayName("debitarLimiteCredito(Conta, ...)")
    class DebitarLimiteCreditoTests {

        @Test
        @DisplayName("deve debitar limite de crédito e registrar extrato")
        void deveDebitarLimiteCreditoComSucesso() {
            when(contaRepository.save(any())).thenReturn(conta);

            contaService.debitarLimiteCredito(conta, new BigDecimal("500.00"), pedido, pagamento, "Compra crédito");

            assertThat(conta.getLimiteCreditoDisponivel()).isEqualByComparingTo("1500.00");
            verify(contaRepository).save(conta);
            verify(extratoService).registrar(
                    eq(conta), eq(TipoExtrato.DEBITO),
                    eq(new BigDecimal("500.00")),
                    eq(new BigDecimal("2000.00")),
                    eq(new BigDecimal("1500.00")),
                    eq("Compra crédito"),
                    eq(pedido), eq(pagamento));
        }

        @Test
        @DisplayName("deve debitar limite exatamente igual ao disponível — boundary")
        void deveDebitarLimiteCreditoExatamenteIgualAoDisponivel() {
            when(contaRepository.save(any())).thenReturn(conta);

            contaService.debitarLimiteCredito(conta, new BigDecimal("2000.00"), pedido, pagamento, "Limite total");

            assertThat(conta.getLimiteCreditoDisponivel()).isEqualByComparingTo("0.00");
            verify(contaRepository).save(conta);
        }
    }

    // =========================================================================
    // creditarLimiteCredito(Long contaId, BigDecimal valor)   ← sobrecarga por ID
    // =========================================================================
    @Nested
    @DisplayName("creditarLimiteCredito(Long, BigDecimal)")
    class CreditarLimiteCreditoPorIdTests {

        @Test
        @DisplayName("deve creditar limite de crédito por ID e registrar extrato")
        void deveCreditarLimiteCreditoPorId() {
            when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));
            when(contaRepository.save(any())).thenReturn(conta);

            Conta resultado = contaService.creditarLimiteCredito(1L, new BigDecimal("500.00"));

            assertThat(resultado.getLimiteCreditoDisponivel()).isEqualByComparingTo("2500.00");
            verify(contaRepository).findById(1L);
            verify(contaRepository).save(conta);
            verify(extratoService).registrar(
                    eq(conta), eq(TipoExtrato.CREDITO),
                    eq(new BigDecimal("500.00")),
                    eq(new BigDecimal("2000.00")),
                    eq(new BigDecimal("2500.00")),
                    eq("Limite de crédito adicionado"),
                    isNull(), isNull());
        }

        @Test
        @DisplayName("deve lançar RecursoNaoEncontradoException quando conta não existe")
        void deveLancarExcecaoQuandoContaNaoExiste() {
            when(contaRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> contaService.creditarLimiteCredito(999L, new BigDecimal("100.00")))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Conta não encontrada");

            verify(contaRepository, never()).save(any());
        }
    }

    // =========================================================================
    // creditarLimiteCredito(Conta, valor, pedido, pagamento, descricao) ← sobrecarga por objeto
    // =========================================================================
    @Nested
    @DisplayName("creditarLimiteCredito(Conta, ...)")
    class CreditarLimiteCreditoPorContaTests {

        @Test
        @DisplayName("deve creditar limite de crédito na conta e registrar extrato")
        void deveCreditarLimiteCreditoNaConta() {
            when(contaRepository.save(any())).thenReturn(conta);

            contaService.creditarLimiteCredito(conta, new BigDecimal("300.00"), pedido, pagamento, "Estorno crédito");

            assertThat(conta.getLimiteCreditoDisponivel()).isEqualByComparingTo("2300.00");
            verify(contaRepository).save(conta);
            verify(extratoService).registrar(
                    eq(conta), eq(TipoExtrato.CREDITO),
                    eq(new BigDecimal("300.00")),
                    eq(new BigDecimal("2000.00")),
                    eq(new BigDecimal("2300.00")),
                    eq("Estorno crédito"),
                    eq(pedido), eq(pagamento));
        }
    }
}