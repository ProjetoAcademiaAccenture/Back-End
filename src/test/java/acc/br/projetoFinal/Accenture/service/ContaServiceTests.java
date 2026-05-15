package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ContaRequestDTO;
import acc.br.projetoFinal.Accenture.enums.TipoConta;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.Pagamento;
import acc.br.projetoFinal.Accenture.model.Pedido;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.repository.ContaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("ContaService - Testes Positivos")
class ContaServiceTests {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ExtratoService extratoService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ContaService contaService;

    private Conta conta;
    private Conta contaEmpresa;
    private Pedido pedido;
    private Pagamento pagamento;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

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
                .build();

        contaEmpresa = Conta.builder()
                .id(2L)
                .numeroConta("1234567-8")
                .saldo(new BigDecimal("10000.00"))
                .limiteCreditoDisponivel(new BigDecimal("0.00"))
                .tipo(TipoConta.JURIDICA)
                .build();

        pedido = Pedido.builder()
                .id(1L)
                .valorTotal(new BigDecimal("1000.00"))
                .build();

        pagamento = new Pagamento();
    }

    // -------------------------------------------------------------------------
    // depositar
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("✓ Deve depositar com sucesso e atualizar saldo")
    void testDepositarComSucesso() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        Conta resultado = contaService.depositar(1L, new BigDecimal("500.00"));

        assertEquals(new BigDecimal("5500.00"), resultado.getSaldo());
        verify(contaRepository).findById(1L);
        verify(contaRepository).save(conta);
        verify(extratoService).registrar(any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("✓ Deve depositar valor zero sem alterar saldo")
    void testDepositarValorZero() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        Conta resultado = contaService.depositar(1L, BigDecimal.ZERO);

        assertEquals(new BigDecimal("5000.00"), resultado.getSaldo());
        verify(contaRepository).save(conta);
    }

    // -------------------------------------------------------------------------
    // buscarPorId
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("✓ Deve retornar conta ao buscar por ID existente")
    void testBuscarPorIdExistente() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

        Conta resultado = contaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("1234567-1", resultado.getNumeroConta());
        verify(contaRepository).findById(1L);
    }

    // -------------------------------------------------------------------------
    // buscarContaDoCliente
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("✓ Deve retornar conta ao buscar pelo ID do cliente")
    void testBuscarContaDoClienteExistente() {
        when(contaRepository.findByClienteId(1L)).thenReturn(Optional.of(conta));

        Conta resultado = contaService.buscarContaDoCliente(1L);

        assertNotNull(resultado);
        assertEquals("1234567-1", resultado.getNumeroConta());
        verify(contaRepository).findByClienteId(1L);
    }

    // -------------------------------------------------------------------------
    // buscarContaEmpresa
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("✓ Deve retornar a conta empresa com sucesso")
    void testBuscarContaEmpresaComSucesso() {
        when(contaRepository.findByNumeroConta("1234567-8")).thenReturn(Optional.of(contaEmpresa));

        Conta resultado = contaService.buscarContaEmpresa();

        assertNotNull(resultado);
        assertEquals("1234567-8", resultado.getNumeroConta());
        assertEquals(TipoConta.JURIDICA, resultado.getTipo());
        verify(contaRepository).findByNumeroConta("1234567-8");
    }

    // -------------------------------------------------------------------------
    // debitarSaldo
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("✓ Deve debitar saldo com sucesso")
    void testDebitarSaldoComSucesso() {
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        contaService.debitarSaldo(conta, new BigDecimal("1000.00"), pedido, pagamento, "Pagamento de pedido");

        assertEquals(new BigDecimal("4000.00"), conta.getSaldo());
        verify(contaRepository).save(conta);
        verify(extratoService).registrar(any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("✓ Deve debitar saldo exatamente igual ao saldo disponível")
    void testDebitarSaldoExatamenteIgualAoSaldo() {
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        contaService.debitarSaldo(conta, new BigDecimal("5000.00"), pedido, pagamento, "Pagamento total");

        assertEquals(BigDecimal.ZERO.setScale(2), conta.getSaldo().setScale(2));
        verify(contaRepository).save(conta);
    }

    // -------------------------------------------------------------------------
    // creditarSaldo
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("✓ Deve creditar saldo com sucesso")
    void testCreditarSaldoComSucesso() {
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        contaService.creditarSaldo(conta, new BigDecimal("500.00"), pedido, pagamento, "Estorno de pedido");

        assertEquals(new BigDecimal("5500.00"), conta.getSaldo());
        verify(contaRepository).save(conta);
        verify(extratoService).registrar(any(), any(), any(), any(), any(), any(), any(), any());
    }

    // -------------------------------------------------------------------------
    // debitarLimiteCredito
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("✓ Deve debitar limite de crédito com sucesso")
    void testDebitarLimiteCreditoComSucesso() {
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        contaService.debitarLimiteCredito(conta, new BigDecimal("500.00"), pedido, pagamento, "Compra no crédito");

        assertEquals(new BigDecimal("1500.00"), conta.getLimiteCreditoDisponivel());
        verify(contaRepository).save(conta);
        verify(extratoService).registrar(any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("✓ Deve debitar limite exatamente igual ao disponível")
    void testDebitarLimiteCreditoExatamenteIgualAoDisponivel() {
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        contaService.debitarLimiteCredito(conta, new BigDecimal("2000.00"), pedido, pagamento, "Compra no crédito total");

        assertEquals(BigDecimal.ZERO.setScale(2), conta.getLimiteCreditoDisponivel().setScale(2));
        verify(contaRepository).save(conta);
    }

    // -------------------------------------------------------------------------
    // creditarLimiteCredito
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("✓ Deve creditar limite de crédito com sucesso")
    void testCreditarLimiteCreditoComSucesso() {
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        contaService.creditarLimiteCredito(conta, new BigDecimal("300.00"), pedido, pagamento, "Estorno crédito");

        assertEquals(new BigDecimal("2300.00"), conta.getLimiteCreditoDisponivel());
        verify(contaRepository).save(conta);
        verify(extratoService).registrar(any(), any(), any(), any(), any(), any(), any(), any());
    }

    // -------------------------------------------------------------------------
    // validarSenhaTransacao
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("✓ Deve validar senha de transação correta sem lançar exceção")
    void testValidarSenhaTransacaoCorreta() {
        when(passwordEncoder.matches("senha123", "$2a$hash_senha")).thenReturn(true);

        assertDoesNotThrow(() ->
                contaService.validarSenhaTransacao(conta, "senha123"));

        verify(passwordEncoder).matches("senha123", "$2a$hash_senha");
    }

    // -------------------------------------------------------------------------
    // criarEntidade
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("✓ Deve criar conta com sucesso e enviar email")
    void testCriarContaComSucesso() {
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

        assertNotNull(resultado);
        assertNotNull(resultado.getNumeroConta());
        assertNotNull(resultado.getSaldo());
        assertTrue(resultado.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        assertEquals(TipoConta.CORRENTE, resultado.getTipo());

        verify(clienteRepository).findById(1L);
        verify(contaRepository).findByClienteId(1L);
        verify(contaRepository).save(any(Conta.class));
        verify(extratoService).registrar(any(), any(), any(), any(), any(), any(), any(), any());
        verify(emailService).enviarDadosConta(
                eq("cliente@email.com"),
                eq("Cliente Teste"),
                any(),
                eq(TipoConta.POUPANCA.name())
        );
    }
}