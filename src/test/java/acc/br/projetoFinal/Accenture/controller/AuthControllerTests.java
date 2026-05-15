package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.request.ClienteRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.ContaRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.LoginBankRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.LoginRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.AuthBankResponseDTO;
import acc.br.projetoFinal.Accenture.dto.response.AuthResponseDTO;
import acc.br.projetoFinal.Accenture.enums.TipoCliente;
import acc.br.projetoFinal.Accenture.enums.TipoConta;
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
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("AuthController - Testes Positivos")
class AuthControllerTests {

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
            when(contaService.criarEntidade(any())).thenReturn(conta);
    when(contaService.depositar(any(), any())).thenReturn(conta);
    when(contaService.creditarLimiteCredito(any(), any())).thenReturn(conta);


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
                .cpf("12345678900")
                .tipoCliente(TipoCliente.ROLE_USER)
                .build();

        conta = Conta.builder()
                .id(1L)
                .numeroConta("12345-6")
                .saldo(BigDecimal.valueOf(1000))
                .senhaTransacao("senhaCriptografada")
                .tipo(TipoConta.CORRENTE)
                .cliente(cliente)
                .build();
    }

    // -------------------------------------------------------
    // LOGIN
    // -------------------------------------------------------

    @Test
    @DisplayName("✓ Deve realizar login com sucesso")
    void testLoginComSucesso() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(clienteRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(cliente));
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("jwt_token_123");

        ResponseEntity<AuthResponseDTO> response = authController.login(loginDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getClienteId());
        assertEquals("João Silva", response.getBody().getNome());
        assertEquals("jwt_token_123", response.getBody().getToken());
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("✓ Deve retornar status 200 (OK) no login bem-sucedido")
    void testLoginRetornaStatusOk() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.of(cliente));
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("token");

        ResponseEntity<AuthResponseDTO> response = authController.login(loginDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("✓ Deve retornar email e tipoCliente no body do login")
    void testLoginRetornaEmailETipoCliente() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(clienteRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(cliente));
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("token");

        AuthResponseDTO body = authController.login(loginDto).getBody();

        assertNotNull(body);
        assertEquals("joao@test.com", body.getEmail());
        assertEquals("ROLE_USER", body.getTipoCliente());
    }

    @Test
    @DisplayName("✓ Deve buscar cliente pelo email correto durante o login")
    void testLoginBuscaClientePorEmail() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(clienteRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(cliente));
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("token");

        authController.login(loginDto);

        verify(clienteRepository, times(1)).findByEmail("joao@test.com");
    }

    @Test
    @DisplayName("✓ Deve gerar token JWT no login")
    void testLoginGeraTokenJwt() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.of(cliente));
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("jwt_gerado");

        ResponseEntity<AuthResponseDTO> response = authController.login(loginDto);

        assertEquals("jwt_gerado", response.getBody().getToken());
        verify(jwtService, times(1)).gerarToken(any(Cliente.class));
    }

    @Test
    @DisplayName("✓ Deve chamar authenticate() antes de buscar o cliente")
    void testLoginAutenticaAntesDebusar() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(clienteRepository.findByEmail(any())).thenReturn(Optional.of(cliente));
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("token");

        authController.login(loginDto);

        InOrder ordem = inOrder(authenticationManager, clienteRepository);
        ordem.verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        ordem.verify(clienteRepository).findByEmail("joao@test.com");
    }

    @Test
    @DisplayName("✓ Deve passar exatamente o cliente encontrado para gerarToken()")
    void testLoginGerarTokenRecebeClienteExato() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(clienteRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(cliente));
        when(jwtService.gerarToken(cliente)).thenReturn("token-preciso");

        authController.login(loginDto);

        verify(jwtService).gerarToken(cliente);
    }

    // -------------------------------------------------------
    // LOGIN BANK
    // -------------------------------------------------------

    @Test
    @DisplayName("✓ Deve realizar login-bank com sucesso")
    void testLoginBankComSucesso() {
        when(contaRepository.findByNumeroConta("12345-6")).thenReturn(Optional.of(conta));
        when(passwordEncoder.matches("senha123", "senhaCriptografada")).thenReturn(true);
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("jwt_bank_token");

        ResponseEntity<AuthBankResponseDTO> response = authController.loginBank(loginBankDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getContaId());
        assertEquals("12345-6", response.getBody().getNumeroConta());
        assertEquals("jwt_bank_token", response.getBody().getToken());
        assertEquals("1000", response.getBody().getSaldo());
    }

    @Test
    @DisplayName("✓ Deve retornar tipoConta e clienteId no body do login-bank")
    void testLoginBankRetornaTipoContaEClienteId() {
        when(contaRepository.findByNumeroConta("12345-6")).thenReturn(Optional.of(conta));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("token");

        AuthBankResponseDTO body = authController.loginBank(loginBankDto).getBody();

        assertNotNull(body);
        assertEquals("CORRENTE", body.getTipoConta());
        assertEquals(1L, body.getClienteId());
    }

    @Test
    @DisplayName("✓ Deve verificar a senha da conta com os argumentos exatos")
    void testLoginBankVerificaSenhaComArgumentosExatos() {
        when(contaRepository.findByNumeroConta("12345-6")).thenReturn(Optional.of(conta));
        when(passwordEncoder.matches("senha123", "senhaCriptografada")).thenReturn(true);
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("token");

        authController.loginBank(loginBankDto);

        verify(passwordEncoder, times(1)).matches("senha123", "senhaCriptografada");
    }

    @Test
    @DisplayName("✓ Deve gerar token com o cliente vinculado à conta no login-bank")
    void testLoginBankGeraTokenComClienteDaConta() {
        when(contaRepository.findByNumeroConta("12345-6")).thenReturn(Optional.of(conta));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtService.gerarToken(eq(cliente))).thenReturn("token");

        authController.loginBank(loginBankDto);

        verify(jwtService, times(1)).gerarToken(eq(cliente));
    }

    // -------------------------------------------------------
    // REGISTER
    // -------------------------------------------------------

    @Test
    @DisplayName("✓ Deve registrar novo cliente com sucesso")
    void testRegistrarComSucesso() {
        when(clienteService.criarEntidade(registerDto)).thenReturn(cliente);
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("jwt_token_456");

        ResponseEntity<AuthResponseDTO> response = authController.register(registerDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getClienteId());
        verify(clienteService, times(1)).criarEntidade(registerDto);
    }

    @Test
    @DisplayName("✓ Deve retornar status 201 (CREATED) ao registrar")
    void testRegistrarRetornaStatusCreated() {
        when(clienteService.criarEntidade(any())).thenReturn(cliente);
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("token");

        ResponseEntity<AuthResponseDTO> response = authController.register(registerDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @DisplayName("✓ Deve retornar nome, email e tipoCliente no body do register")
    void testRegistrarRetornaDados() {
        when(clienteService.criarEntidade(any())).thenReturn(cliente);
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("token");

        AuthResponseDTO body = authController.register(registerDto).getBody();

        assertNotNull(body);
        assertEquals("João Silva", body.getNome());
        assertEquals("joao@test.com", body.getEmail());
        assertEquals(1L, body.getClienteId());
        assertEquals("ROLE_USER", body.getTipoCliente());
    }

    @Test
    @DisplayName("✓ Deve gerar token após registro")
    void testRegistrarGeraToken() {
        when(clienteService.criarEntidade(registerDto)).thenReturn(cliente);
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("novo_token");

        ResponseEntity<AuthResponseDTO> response = authController.register(registerDto);

        assertEquals("novo_token", response.getBody().getToken());
        verify(jwtService, times(1)).gerarToken(any(Cliente.class));
    }

    @Test
    @DisplayName("✓ Deve chamar criarEntidade() antes de gerarToken() no register")
    void testRegistrarOrdemDeOperacoes() {
        when(clienteService.criarEntidade(any())).thenReturn(cliente);
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("token");

        authController.register(registerDto);

        InOrder ordem = inOrder(clienteService, jwtService);
        ordem.verify(clienteService).criarEntidade(any());
        ordem.verify(jwtService).gerarToken(eq(cliente));
    }

    // -------------------------------------------------------
    // REGISTER BANK
    // -------------------------------------------------------

    @Test
    @DisplayName("✓ Deve registrar nova conta com sucesso")
    void testRegisterBankComSucesso() {
        when(contaService.criarEntidade(contaRequestDTO)).thenReturn(conta);
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("jwt_bank_456");

        ResponseEntity<AuthBankResponseDTO> response = authController.registerBank(contaRequestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getContaId());
        assertEquals("12345-6", response.getBody().getNumeroConta());
        assertEquals("jwt_bank_456", response.getBody().getToken());
    }

    @Test
    @DisplayName("✓ Deve retornar status 201 (CREATED) ao registrar conta")
    void testRegisterBankRetornaStatusCreated() {
        when(contaService.criarEntidade(any())).thenReturn(conta);
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("token");

        ResponseEntity<AuthBankResponseDTO> response = authController.registerBank(contaRequestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @DisplayName("✓ Deve retornar tipoConta, saldo e clienteId no body do register-bank")
    void testRegisterBankRetornaDados() {
        when(contaService.criarEntidade(any())).thenReturn(conta);
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("token");

        AuthBankResponseDTO body = authController.registerBank(contaRequestDTO).getBody();

        assertNotNull(body);
        assertEquals("CORRENTE", body.getTipoConta());
        assertEquals("1000", body.getSaldo());
        assertEquals(1L, body.getClienteId());
    }

    @Test
    @DisplayName("✓ Deve gerar token com o cliente da conta ao registrar")
    void testRegisterBankGeraTokenComCliente() {
        when(contaService.criarEntidade(any())).thenReturn(conta);
        when(jwtService.gerarToken(eq(cliente))).thenReturn("token");

        authController.registerBank(contaRequestDTO);

        verify(jwtService, times(1)).gerarToken(eq(cliente));
    }

    @Test
    @DisplayName("✓ Deve usar ContaService ao registrar conta")
    void testRegisterBankUsaContaService() {
        when(contaService.criarEntidade(contaRequestDTO)).thenReturn(conta);
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("token");

        authController.registerBank(contaRequestDTO);

        verify(contaService, times(1)).criarEntidade(contaRequestDTO);
    }
}