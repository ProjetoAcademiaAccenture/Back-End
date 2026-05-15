package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.enums.TipoConta;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.exception.SaldoInsuficienteException;
import acc.br.projetoFinal.Accenture.model.Conta;
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

@DisplayName("ContaService - Testes Negativos")
class ContaServiceNegativeTests {

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

    private Conta contaComSaldoBaixo;
    private Conta contaEmpresa;
    private Pedido pedido;
    private Pagamento pagamento;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Conta com saldo insuficiente para cobrir transferências grandes
        contaComSaldoBaixo = Conta.builder()
                .id(1L)
                .numeroConta("123456")
                .saldo(new BigDecimal("100.00"))
                .limiteCreditoDisponivel(new BigDecimal("200.00"))
                .build();

        contaEmpresa = Conta.builder()
                .id(2L)
                .numeroConta("1234567-8")
                .saldo(new BigDecimal("10000.00"))
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
    @DisplayName("✗ Deve lançar exceção ao depositar em conta inexistente")
    void testDepositarContaInexistente() {
        when(contaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () ->
                contaService.depositar(99L, new BigDecimal("500.00")));

        verify(contaRepository).findById(99L);
        verify(extratoService, never()).registrar(any(), any(), any(), any(), any(), any(), any(), any());
    }

    // -------------------------------------------------------------------------
    // buscarPorId
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("✗ Deve lançar exceção ao buscar conta por ID inexistente")
    void testBuscarPorIdInexistente() {
        when(contaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () ->
                contaService.buscarPorId(99L));

        verify(contaRepository).findById(99L);
    }

    // -------------------------------------------------------------------------
    // buscarContaDoCliente
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("✗ Deve lançar exceção ao buscar conta de cliente inexistente")
    void testBuscarContaDoClienteInexistente() {
        when(contaRepository.findByClienteId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () ->
                contaService.buscarContaDoCliente(99L));

        verify(contaRepository).findByClienteId(99L);
    }

    // -------------------------------------------------------------------------
    // buscarContaEmpresa
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("✗ Deve lançar exceção ao buscar conta empresa inexistente")
    void testBuscarContaEmpresaInexistente() {
        when(contaRepository.findByNumeroConta("1234567-8")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () ->
                contaService.buscarContaEmpresa());

        verify(contaRepository).findByNumeroConta("1234567-8");
    }

    // -------------------------------------------------------------------------
    // debitarSaldo
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("✗ Deve lançar exceção ao debitar com saldo insuficiente")
    void testDebitarSaldoInsuficiente() {
        // saldo = 100.00, tentativa de debitar 500.00
        assertThrows(SaldoInsuficienteException.class, () ->
                contaService.debitarSaldo(
                        contaComSaldoBaixo,
                        new BigDecimal("500.00"),
                        pedido,
                        pagamento,
                        "Pagamento de pedido"
                ));

        verify(contaRepository, never()).save(any());
        verify(extratoService, never()).registrar(any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao debitar valor exatamente acima do saldo")
    void testDebitarSaldoExatamenteAcimaDoSaldo() {
        // saldo = 100.00, tentativa de debitar 100.01
        assertThrows(SaldoInsuficienteException.class, () ->
                contaService.debitarSaldo(
                        contaComSaldoBaixo,
                        new BigDecimal("100.01"),
                        pedido,
                        pagamento,
                        "Pagamento de pedido"
                ));

        verify(contaRepository, never()).save(any());
        verify(extratoService, never()).registrar(any(), any(), any(), any(), any(), any(), any(), any());
    }

    // -------------------------------------------------------------------------
    // debitarLimiteCredito
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("✗ Deve lançar exceção ao debitar limite de crédito insuficiente")
    void testDebitarLimiteCreditoInsuficiente() {
        // limiteCreditoDisponivel = 200.00, tentativa de debitar 1000.00
        assertThrows(SaldoInsuficienteException.class, () ->
                contaService.debitarLimiteCredito(
                        contaComSaldoBaixo,
                        new BigDecimal("1000.00"),
                        pedido,
                        pagamento,
                        "Compra no crédito"
                ));

        verify(contaRepository, never()).save(any());
        verify(extratoService, never()).registrar(any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao debitar limite exatamente acima do disponível")
    void testDebitarLimiteCreditoExatamenteAcima() {
        // limiteCreditoDisponivel = 200.00, tentativa de debitar 200.01
        assertThrows(SaldoInsuficienteException.class, () ->
                contaService.debitarLimiteCredito(
                        contaComSaldoBaixo,
                        new BigDecimal("200.01"),
                        pedido,
                        pagamento,
                        "Compra no crédito"
                ));

        verify(contaRepository, never()).save(any());
        verify(extratoService, never()).registrar(any(), any(), any(), any(), any(), any(), any(), any());
    }

    // -------------------------------------------------------------------------
    // validarSenhaTransacao
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("✗ Deve lançar exceção ao validar senha de transação incorreta")
    void testValidarSenhaTransacaoInvalida() {
        contaComSaldoBaixo.setSenhaTransacao("$2a$hash_senha_correta");
        when(passwordEncoder.matches("senhaErrada", "$2a$hash_senha_correta")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                contaService.validarSenhaTransacao(contaComSaldoBaixo, "senhaErrada"));

        verify(passwordEncoder).matches("senhaErrada", "$2a$hash_senha_correta");
    }

    // -------------------------------------------------------------------------
    // criarEntidade
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("✗ Deve lançar exceção ao criar conta para cliente inexistente")
    void testCriarContaClienteInexistente() {
        var dto = new acc.br.projetoFinal.Accenture.dto.request.ContaRequestDTO();
        dto.setClienteId(99L);

        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () ->
                contaService.criarEntidade(dto));

        verify(clienteRepository).findById(99L);
        verify(contaRepository, never()).save(any());
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao criar conta duplicada para o mesmo cliente")
    void testCriarContaDuplicada() {
        var cliente = acc.br.projetoFinal.Accenture.model.Cliente.builder()
                .id(1L)
                .email("cliente@email.com")
                .nome("Cliente Teste")
                .build();

        var dto = new acc.br.projetoFinal.Accenture.dto.request.ContaRequestDTO();
        dto.setClienteId(1L);
        dto.setSenhaTransacao("senha123");
        dto.setTipoConta(TipoConta.CORRENTE);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(contaRepository.findByClienteId(1L)).thenReturn(Optional.of(contaComSaldoBaixo));

        assertThrows(IllegalArgumentException.class, () ->
                contaService.criarEntidade(dto));

        verify(contaRepository, never()).save(any());
    }
}