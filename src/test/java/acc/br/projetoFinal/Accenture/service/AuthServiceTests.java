package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ClienteRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.AuthResponseDTO;
import acc.br.projetoFinal.Accenture.enums.TipoCliente;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Testes Positivos")
class AuthServiceTests {

    @Mock private ClienteRepository clienteRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private EmailService emailService; // <- estava faltando

    @InjectMocks
    private AuthService authService;

    private ClienteRequestDTO dto;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        dto = ClienteRequestDTO.builder()
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
                .telefone("11999999999")
                .senha("encoded_senha123")
                .tipoCliente(TipoCliente.ROLE_USER)
                .build();
    }

    @Test
    @DisplayName("✓ Deve registrar novo cliente com sucesso")
    void testRegistrarClienteComSucesso() {
        when(clienteRepository.findByCpf(dto.getCpf())).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.getSenha())).thenReturn("encoded_senha123");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("jwt_token_123");
        doNothing().when(emailService).enviarBoasVindas(anyString(), anyString());

        AuthResponseDTO resultado = authService.register(dto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getClienteId());
        assertEquals("João Silva", resultado.getNome());
        assertEquals("jwt_token_123", resultado.getToken());
        assertEquals("ROLE_USER", resultado.getTipoCliente());
        verify(clienteRepository).save(any(Cliente.class));
        verify(jwtService).gerarToken(any(Cliente.class));
        verify(emailService).enviarBoasVindas("joao@test.com", "João Silva");
    }

    @Test
    @DisplayName("✓ Deve codificar senha corretamente ao registrar")
    void testRegistrarClienteComSenhaCodeificada() {
        when(clienteRepository.findByCpf(dto.getCpf())).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha123")).thenReturn("hashed_password_xyz");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("jwt_token_456");
        doNothing().when(emailService).enviarBoasVindas(anyString(), anyString());

        AuthResponseDTO resultado = authService.register(dto);

        assertNotNull(resultado);
        verify(passwordEncoder).encode("senha123");
    }

    @Test
    @DisplayName("✓ Deve gerar token JWT após registro bem-sucedido")
    void testRegistrarClienteGeraToken() {
        when(clienteRepository.findByCpf(dto.getCpf())).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("jwt_token_final");
        doNothing().when(emailService).enviarBoasVindas(anyString(), anyString());

        AuthResponseDTO resultado = authService.register(dto);

        assertNotNull(resultado.getToken());
        assertEquals("jwt_token_final", resultado.getToken());
    }

    @Test
    @DisplayName("✓ Deve retornar dados do cliente no response")
    void testRegistrarClienteRetornaDados() {
        when(clienteRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("token");
        doNothing().when(emailService).enviarBoasVindas(anyString(), anyString());

        AuthResponseDTO resultado = authService.register(dto);

        assertEquals("João Silva", resultado.getNome());
        assertEquals(1L, resultado.getClienteId());
        assertNotNull(resultado.getTipoCliente());
    }

    @Test
    @DisplayName("✓ Deve enviar e-mail de boas-vindas após registro")
    void testRegistrarEnviaEmailBoasVindas() {
        when(clienteRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("token");
        doNothing().when(emailService).enviarBoasVindas(anyString(), anyString());

        authService.register(dto);

        verify(emailService).enviarBoasVindas("joao@test.com", "João Silva");
    }
}