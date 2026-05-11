package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.request.ClienteRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.LoginRequestDTO;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("AuthController - Testes Negativos")
class AuthControllerNegativeTests {

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
                .tipoCliente(TipoCliente.ROLE_USER)
                .build();
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao login com credenciais inválidas")
    void testLoginComCredenciaisInvalidas() {
        // Arrange
        doThrow(new BadCredentialsException("Credenciais inválidas"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authController.login(loginDto));
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao não encontrar cliente no login")
    void testLoginClienteNaoEncontrado() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(clienteRepository.findByEmail("inexistente@test.com")).thenReturn(Optional.empty());

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("inexistente@test.com");
        dto.setSenha("senha123");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authController.login(dto));
    }

    @Test
    @DisplayName("✗ Deve validar email no login")
    void testLoginValidaEmail() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(clienteRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(cliente));
        when(jwtService.gerarToken(any())).thenReturn("token");

        // Act
        authController.login(loginDto);

        // Assert
        verify(clienteRepository, times(1)).findByEmail("joao@test.com");
    }

    @Test
    @DisplayName("✗ Deve autenticar antes de fazer login")
    void testLoginAutenticaUsuario() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(clienteRepository.findByEmail(any())).thenReturn(Optional.of(cliente));
        when(jwtService.gerarToken(any())).thenReturn("token");

        // Act
        authController.login(loginDto);

        // Assert
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("✗ Deve usar ClienteService ao registrar")
    void testRegistrarUsaClienteService() {
        // Arrange
        when(clienteService.criarEntidade(registerDto)).thenReturn(cliente);
        when(jwtService.gerarToken(any())).thenReturn("token");

        // Act
        authController.register(registerDto);

        // Assert
        verify(clienteService, times(1)).criarEntidade(registerDto);
    }

    @Test
    @DisplayName("✗ Deve gerar token após registrar com sucesso")
    void testRegistrarGeraTokenAposRegistro() {
        // Arrange
        when(clienteService.criarEntidade(any())).thenReturn(cliente);
        when(jwtService.gerarToken(cliente)).thenReturn("novo_token");

        // Act
        authController.register(registerDto);

        // Assert
        verify(jwtService, times(1)).gerarToken(cliente);
    }

    @Test
    @DisplayName("✗ Deve lançar exceção se ClienteService falhar no registro")
    void testRegistrarClienteServiceFalha() {
        // Arrange
        when(clienteService.criarEntidade(any()))
                .thenThrow(new IllegalArgumentException("Email já existe"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authController.register(registerDto));
    }
}
