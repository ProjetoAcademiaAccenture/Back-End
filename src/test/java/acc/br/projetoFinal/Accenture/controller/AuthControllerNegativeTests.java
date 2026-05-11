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

@DisplayName("AuthController - Testes")
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
    @DisplayName("✓ Deve buscar cliente pelo email no login")
    void testLoginValidaEmail() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(clienteRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(cliente));
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("token");

        authController.login(loginDto);

        verify(clienteRepository, times(1)).findByEmail("joao@test.com");
    }

    @Test
    @DisplayName("✓ Deve autenticar antes de fazer login")
    void testLoginAutenticaUsuario() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(clienteRepository.findByEmail(any())).thenReturn(Optional.of(cliente));
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("token");

        authController.login(loginDto);

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
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
    @DisplayName("✗ Deve lançar SenhaInvalidaException ao login-bank com senha inválida")
    void testLoginBankSenhaInvalida() {
        when(contaRepository.findByNumeroConta("12345-6")).thenReturn(Optional.of(conta));
        when(passwordEncoder.matches("senha123", "senhaCriptografada")).thenReturn(false);

        assertThrows(SenhaInvalidaException.class, () -> authController.loginBank(loginBankDto));
    }

    @Test
    @DisplayName("✓ Deve realizar login-bank com sucesso")
    void testLoginBankSucesso() {
        when(contaRepository.findByNumeroConta("12345-6")).thenReturn(Optional.of(conta));
        when(passwordEncoder.matches("senha123", "senhaCriptografada")).thenReturn(true);
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("token");

        authController.loginBank(loginBankDto);

        verify(jwtService, times(1)).gerarToken(cliente);
    }

    // -------------------------------------------------------
    // REGISTER
    // -------------------------------------------------------

    @Test
    @DisplayName("✓ Deve usar ClienteService ao registrar")
    void testRegistrarUsaClienteService() {
        when(clienteService.criarEntidade(registerDto)).thenReturn(cliente);
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("token");

        authController.register(registerDto);

        verify(clienteService, times(1)).criarEntidade(registerDto);
    }

    @Test
    @DisplayName("✓ Deve gerar token após registrar com sucesso")
    void testRegistrarGeraTokenAposRegistro() {
        when(clienteService.criarEntidade(any())).thenReturn(cliente);
        when(jwtService.gerarToken(cliente)).thenReturn("novo_token");

        authController.register(registerDto);

        verify(jwtService, times(1)).gerarToken(cliente);
    }

    @Test
    @DisplayName("✗ Deve lançar exceção se ClienteService falhar no registro")
    void testRegistrarClienteServiceFalha() {
        when(clienteService.criarEntidade(any()))
                .thenThrow(new IllegalArgumentException("Email já existe"));

        assertThrows(IllegalArgumentException.class, () -> authController.register(registerDto));
    }

    // -------------------------------------------------------
    // REGISTER BANK
    // -------------------------------------------------------

    @Test
    @DisplayName("✓ Deve usar ContaService ao registrar conta")
    void testRegisterBankUsaContaService() {
        when(contaService.criarEntidade(contaRequestDTO)).thenReturn(conta);
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("token");

        authController.registerBank(contaRequestDTO);

        verify(contaService, times(1)).criarEntidade(contaRequestDTO);
    }

    @Test
    @DisplayName("✓ Deve gerar token com o cliente da conta ao registrar")
    void testRegisterBankGeraTokenComCliente() {
        when(contaService.criarEntidade(any())).thenReturn(conta);
        when(jwtService.gerarToken(cliente)).thenReturn("token");

        authController.registerBank(contaRequestDTO);

        // gerarToken deve receber o Cliente, não a Conta
        verify(jwtService, times(1)).gerarToken(cliente);
    }

    @Test
    @DisplayName("✗ Deve lançar exceção se ContaService falhar no registro")
    void testRegisterBankContaServiceFalha() {
        when(contaService.criarEntidade(any()))
                .thenThrow(new IllegalArgumentException("Cliente não encontrado"));

        assertThrows(IllegalArgumentException.class, () -> authController.registerBank(contaRequestDTO));
    }
}