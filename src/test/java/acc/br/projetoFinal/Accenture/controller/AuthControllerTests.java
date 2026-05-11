package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.request.ClienteRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.LoginRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.AuthResponseDTO;
import acc.br.projetoFinal.Accenture.enums.TipoCliente;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.security.JwtService;
import acc.br.projetoFinal.Accenture.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("AuthController - Testes Positivos")
class AuthControllerTests {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    private LoginRequestDTO loginDto;
    private ClienteRequestDTO registerDto;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        loginDto = new LoginRequestDTO();
        loginDto.setEmail("joao@test.com");
        loginDto.setSenha("senha123");

        registerDto = ClienteRequestDTO.builder()
                .nome("João Silva")
                .email("joao@test.com")
                .cpf("12345678900")
                .telefone("11999999999")
                .senha("senha123")
                .build();

        cliente = Cliente.builder()
                .id(1L)
                .nome("João Silva")
                .email("joao@test.com")
                .cpf("12345678900")
                .tipoCliente(TipoCliente.ROLE_USER)
                .build();
    }

    @Test
    @DisplayName("✓ Deve realizar login com sucesso")
    void testLoginComSucesso() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(clienteRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(cliente));
        when(jwtService.gerarToken(cliente)).thenReturn("jwt_token_123");

        // Act
        ResponseEntity<AuthResponseDTO> response = authController.login(loginDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getClienteId());
        assertEquals("João Silva", response.getBody().getNome());
        assertEquals("jwt_token_123", response.getBody().getToken());
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("✓ Deve buscar cliente por email durante login")
    void testLoginBuscaClientePorEmail() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(clienteRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(cliente));
        when(jwtService.gerarToken(cliente)).thenReturn("token");

        // Act
        authController.login(loginDto);

        // Assert
        verify(clienteRepository, times(1)).findByEmail("joao@test.com");
    }

    @Test
    @DisplayName("✓ Deve gerar token JWT no login")
    void testLoginGeraTokenJwt() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.of(cliente));
        when(jwtService.gerarToken(cliente)).thenReturn("jwt_gerado");

        // Act
        ResponseEntity<AuthResponseDTO> response = authController.login(loginDto);

        // Assert
        assertEquals("jwt_gerado", response.getBody().getToken());
        verify(jwtService, times(1)).gerarToken(cliente);
    }

    @Test
    @DisplayName("✓ Deve retornar status 200 (OK) no login bem-sucedido")
    void testLoginRetornaStatusOk() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.of(cliente));
        when(jwtService.gerarToken(any())).thenReturn("token");

        // Act
        ResponseEntity<AuthResponseDTO> response = authController.login(loginDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("✓ Deve registrar novo cliente com sucesso")
    void testRegistrarComSucesso() {
        // Arrange
        when(clienteService.criarEntidade(registerDto)).thenReturn(cliente);
        when(jwtService.gerarToken(cliente)).thenReturn("jwt_token_456");

        // Act
        ResponseEntity<AuthResponseDTO> response = authController.register(registerDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getClienteId());
        verify(clienteService, times(1)).criarEntidade(registerDto);
    }

    @Test
    @DisplayName("✓ Deve retornar status 201 (CREATED) ao registrar")
    void testRegistrarRetornaStatusCreated() {
        // Arrange
        when(clienteService.criarEntidade(any())).thenReturn(cliente);
        when(jwtService.gerarToken(any())).thenReturn("token");

        // Act
        ResponseEntity<AuthResponseDTO> response = authController.register(registerDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @DisplayName("✓ Deve gerar token após registro")
    void testRegistrarGeraToken() {
        // Arrange
        when(clienteService.criarEntidade(registerDto)).thenReturn(cliente);
        when(jwtService.gerarToken(cliente)).thenReturn("novo_token");

        // Act
        ResponseEntity<AuthResponseDTO> response = authController.register(registerDto);

        // Assert
        assertEquals("novo_token", response.getBody().getToken());
        verify(jwtService, times(1)).gerarToken(cliente);
    }

    @Test
    @DisplayName("✓ Deve retornar dados do cliente no registro")
    void testRegistrarRetornaDados() {
        // Arrange
        when(clienteService.criarEntidade(any())).thenReturn(cliente);
        when(jwtService.gerarToken(any())).thenReturn("token");

        // Act
        ResponseEntity<AuthResponseDTO> response = authController.register(registerDto);

        // Assert
        assertEquals("João Silva", response.getBody().getNome());
        assertEquals(1L, response.getBody().getClienteId());
        assertEquals("ROLE_USER", response.getBody().getTipoCliente());
    }
}
