package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.request.ClienteRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.ContaRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.LoginBankRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.LoginRequestDTO;
import acc.br.projetoFinal.Accenture.enums.TipoCliente;
import acc.br.projetoFinal.Accenture.enums.TipoConta;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.exception.SenhaInvalidaException;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.repository.ContaRepository;
import acc.br.projetoFinal.Accenture.security.JwtService;
import acc.br.projetoFinal.Accenture.service.ClienteService;
import acc.br.projetoFinal.Accenture.service.ContaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("AuthController - Testes Negativos")
class AuthControllerNegativeTests {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private ClienteRepository clienteRepository;
    @Mock private ContaRepository contaRepository;
    @Mock private ClienteService clienteService;
    @Mock private ContaService contaService;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    private LoginRequestDTO loginDto;
    private LoginBankRequestDTO loginBankDto;
    private ClienteRequestDTO registerDto;
    private ContaRequestDTO contaRequestDTO;
    private Cliente cliente;
    private Conta conta;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        loginDto = new LoginRequestDTO();
        loginDto.setEmail("joao@test.com");
        loginDto.setSenha("senha123");

        loginBankDto = new LoginBankRequestDTO();
        loginBankDto.setNumero_conta("12345-6");
        loginBankDto.setSenha("senha123");

        registerDto = ClienteRequestDTO.builder()
                .nome("João Silva")
                .email("joao@test.com")
                .cpf("12345678900")
                .telefone("11999999999")
                .senha("senha123")
                .build();

        contaRequestDTO = ContaRequestDTO.builder()
                .clienteId(1L)
                .senhaTransacao("senha123")
                .build();

        cliente = Cliente.builder()
                .id(1L)
                .nome("João Silva")
                .email("joao@test.com")
                .tipoCliente(TipoCliente.ROLE_USER)
                .build();

        conta = Conta.builder()
                .id(1L)
                .numeroConta("12345-6")
                .saldo(BigDecimal.ZERO)
                .senhaTransacao("senhaCriptografada")
                .tipo(TipoConta.CORRENTE)
                .cliente(cliente)
                .build();
    }

    // -------------------------------------------------------
    // LOGIN
    // -------------------------------------------------------

    @Test
    @DisplayName("✗ Deve lançar SenhaInvalidaException ao login com credenciais inválidas")
    void testLoginComCredenciaisInvalidas() {
        doThrow(new BadCredentialsException("Credenciais inválidas"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(SenhaInvalidaException.class, () -> authController.login(loginDto));
    }

    @Test
    @DisplayName("✗ Não deve consultar o repositório quando a autenticação falha")
    void testLoginFalhaDeveInterromperFluxo() {
        doThrow(new BadCredentialsException("Credenciais inválidas"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(SenhaInvalidaException.class, () -> authController.login(loginDto));

        verifyNoInteractions(clienteRepository);
    }

    @Test
    @DisplayName("✗ Deve lançar RecursoNaoEncontradoException ao não encontrar cliente no login")
    void testLoginClienteNaoEncontrado() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(clienteRepository.findByEmail("inexistente@test.com")).thenReturn(Optional.empty());

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("inexistente@test.com");
        dto.setSenha("senha123");

        assertThrows(RecursoNaoEncontradoException.class, () -> authController.login(dto));
    }

    @Test
    @DisplayName("✗ Não deve gerar token quando o cliente não é encontrado")
    void testLoginClienteNaoEncontradoNaoGeraToken() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(clienteRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> authController.login(loginDto));

        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("✗ Deve propagar exceção do jwtService no login")
    void testLoginFalhaNoJwtServicePropaga() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(clienteRepository.findByEmail(any())).thenReturn(Optional.of(cliente));
        when(jwtService.gerarToken(any(Cliente.class)))
                .thenThrow(new RuntimeException("Erro ao gerar token"));

        assertThrows(RuntimeException.class, () -> authController.login(loginDto));
    }

    // -------------------------------------------------------
    // LOGIN BANK
    // -------------------------------------------------------

    @Test
    @DisplayName("✗ Deve lançar RecursoNaoEncontradoException ao não encontrar conta no login-bank")
    void testLoginBankContaNaoEncontrada() {
        when(contaRepository.findByNumeroConta("12345-6")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> authController.loginBank(loginBankDto));
    }

    @Test
    @DisplayName("✗ Não deve verificar senha quando a conta não é encontrada")
    void testLoginBankContaNaoEncontradaNaoVerificaSenha() {
        when(contaRepository.findByNumeroConta("12345-6")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> authController.loginBank(loginBankDto));

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("✗ Deve lançar SenhaInvalidaException ao login-bank com senha inválida")
    void testLoginBankSenhaInvalida() {
        when(contaRepository.findByNumeroConta("12345-6")).thenReturn(Optional.of(conta));
        when(passwordEncoder.matches("senha123", "senhaCriptografada")).thenReturn(false);

        assertThrows(SenhaInvalidaException.class, () -> authController.loginBank(loginBankDto));
    }

    @Test
    @DisplayName("✗ Não deve gerar token quando a senha de transação é inválida")
    void testLoginBankSenhaInvalidaNaoGeraToken() {
        when(contaRepository.findByNumeroConta("12345-6")).thenReturn(Optional.of(conta));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(SenhaInvalidaException.class, () -> authController.loginBank(loginBankDto));

        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("✗ Deve comparar senha com os argumentos exatos da conta")
    void testLoginBankPasswordEncoderRecebeArgumentosExatos() {
        when(contaRepository.findByNumeroConta("12345-6")).thenReturn(Optional.of(conta));
        when(passwordEncoder.matches("senha123", "senhaCriptografada")).thenReturn(false);

        assertThrows(SenhaInvalidaException.class, () -> authController.loginBank(loginBankDto));

        verify(passwordEncoder).matches("senha123", "senhaCriptografada");
    }

    // -------------------------------------------------------
    // REGISTER
    // -------------------------------------------------------

    @Test
    @DisplayName("✗ Deve lançar exceção se ClienteService falhar no registro")
    void testRegistrarClienteServiceFalha() {
        when(clienteService.criarEntidade(any()))
                .thenThrow(new IllegalArgumentException("Email já existe"));

        assertThrows(IllegalArgumentException.class, () -> authController.register(registerDto));
    }

    @Test
    @DisplayName("✗ Não deve gerar token quando clienteService lança exceção")
    void testRegistrarFalhaNoClienteServiceNaoGeraToken() {
        when(clienteService.criarEntidade(any()))
                .thenThrow(new IllegalArgumentException("Email já existe"));

        assertThrows(IllegalArgumentException.class, () -> authController.register(registerDto));

        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("✗ Deve propagar exceção do jwtService no register")
    void testRegistrarFalhaNoJwtServicePropaga() {
        when(clienteService.criarEntidade(any())).thenReturn(cliente);
        when(jwtService.gerarToken(any(Cliente.class)))
                .thenThrow(new RuntimeException("Falha ao gerar JWT"));

        assertThrows(RuntimeException.class, () -> authController.register(registerDto));
    }

    // -------------------------------------------------------
    // REGISTER BANK
    // -------------------------------------------------------

    @Test
    @DisplayName("✗ Deve lançar exceção se ContaService falhar no registro")
    void testRegisterBankContaServiceFalha() {
        when(contaService.criarEntidade(any()))
                .thenThrow(new IllegalArgumentException("Cliente não encontrado"));

        assertThrows(IllegalArgumentException.class, () -> authController.registerBank(contaRequestDTO));
    }

    @Test
    @DisplayName("✗ Não deve gerar token quando contaService lança exceção")
    void testRegisterBankFalhaNoContaServiceNaoGeraToken() {
        when(contaService.criarEntidade(any()))
                .thenThrow(new IllegalArgumentException("Cliente não encontrado"));

        assertThrows(IllegalArgumentException.class, () -> authController.registerBank(contaRequestDTO));

        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("✗ Deve propagar exceção do jwtService no register-bank")
    void testRegisterBankFalhaNoJwtServicePropaga() {
        when(contaService.criarEntidade(any())).thenReturn(conta);
        when(jwtService.gerarToken(any(Cliente.class)))
                .thenThrow(new RuntimeException("Falha ao gerar JWT"));

        assertThrows(RuntimeException.class, () -> authController.registerBank(contaRequestDTO));
    }
}