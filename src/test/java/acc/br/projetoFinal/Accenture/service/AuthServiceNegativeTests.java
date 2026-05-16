package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ClienteRequestDTO;
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
@DisplayName("AuthService - Testes Negativos")
class AuthServiceNegativeTests {

    @Mock private ClienteRepository clienteRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private EmailService emailService; // <- estava faltando

    @InjectMocks
    private AuthService authService;

    private ClienteRequestDTO dto;
    private Cliente clienteExistente;

    @BeforeEach
    void setUp() {
        dto = ClienteRequestDTO.builder()
                .nome("João Silva")
                .email("joao@test.com")
                .cpf("12345678900")
                .telefone("11999999999")
                .senha("senha123")
                .build();

        clienteExistente = Cliente.builder()
                .id(1L)
                .nome("João Existente")
                .email("joao@test.com")
                .cpf("12345678900")
                .tipoCliente(TipoCliente.ROLE_USER)
                .build();
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao registrar com CPF duplicado")
    void testRegistrarComCpfDuplicado() {
        when(clienteRepository.findByCpf("12345678900")).thenReturn(Optional.of(clienteExistente));

        assertThrows(IllegalArgumentException.class, () -> authService.register(dto));

        verify(clienteRepository).findByCpf("12345678900");
        verify(clienteRepository, never()).save(any(Cliente.class));
        verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao registrar com email duplicado")
    void testRegistrarComEmailDuplicado() {
        when(clienteRepository.findByCpf("12345678900")).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(clienteExistente));

        assertThrows(IllegalArgumentException.class, () -> authService.register(dto));

        verify(clienteRepository).findByEmail("joao@test.com");
        verify(clienteRepository, never()).save(any(Cliente.class));
        verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("✗ Deve validar CPF antes de registrar")
    void testRegistrarValidaCpf() {
        when(clienteRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteExistente);
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("token");
        doNothing().when(emailService).enviarBoasVindas(anyString(), anyString());

        authService.register(dto);

        verify(clienteRepository).findByCpf(dto.getCpf());
    }

    @Test
    @DisplayName("✗ Deve validar email antes de registrar")
    void testRegistrarValidaEmail() {
        when(clienteRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteExistente);
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("token");
        doNothing().when(emailService).enviarBoasVindas(anyString(), anyString());

        authService.register(dto);

        verify(clienteRepository).findByEmail(dto.getEmail());
    }

    @Test
    @DisplayName("✗ Deve não permitir CPF duplicado mesmo em outro contexto")
    void testRegistrarComCpfDuplicadoEmContextoDiferente() {
        Cliente outroCliente = Cliente.builder()
                .id(999L)
                .nome("Outro Cliente")
                .email("outro@test.com")
                .cpf("12345678900")
                .tipoCliente(TipoCliente.ROLE_USER)
                .build();

        when(clienteRepository.findByCpf("12345678900")).thenReturn(Optional.of(outroCliente));

        assertThrows(IllegalArgumentException.class, () -> authService.register(dto));

        verify(clienteRepository, never()).save(any());
        verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("✓ Verifica se múltiplos clientes podem ser registrados com dados diferentes")
    void testRegistrarMultiplosClientesSucessivamente() {
        ClienteRequestDTO dto1 = ClienteRequestDTO.builder()
                .nome("Cliente Um")
                .email("cliente1@test.com")
                .cpf("11111111111")
                .telefone("11111111111")
                .senha("senha1")
                .build();

        Cliente cliente1 = Cliente.builder()
                .id(1L)
                .nome("Cliente Um")
                .email("cliente1@test.com")
                .cpf("11111111111")
                .tipoCliente(TipoCliente.ROLE_USER)
                .build();

        when(clienteRepository.findByCpf("11111111111")).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail("cliente1@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente1);
        when(jwtService.gerarToken(any(Cliente.class))).thenReturn("token1");
        doNothing().when(emailService).enviarBoasVindas(anyString(), anyString());

        assertDoesNotThrow(() -> authService.register(dto1));

        verify(clienteRepository).save(any(Cliente.class));
        verify(emailService).enviarBoasVindas("cliente1@test.com", "Cliente Um");
    }

    @Test
    @DisplayName("✗ Não deve salvar cliente quando CPF duplicado")
    void testNaoDeveSalvarClienteComCpfDuplicado() {
        when(clienteRepository.findByCpf(anyString())).thenReturn(Optional.of(clienteExistente));

        assertThrows(IllegalArgumentException.class, () -> authService.register(dto))  ;

        verifyNoInteractions(passwordEncoder, emailService, jwtService);
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("✗ Não deve salvar cliente quando email duplicado")
    void testNaoDeveSalvarClienteComEmailDuplicado() {
        when(clienteRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.of(clienteExistente));

        assertThrows(IllegalArgumentException.class, () -> authService.register(dto));

        verifyNoInteractions(passwordEncoder, emailService, jwtService);
        verify(clienteRepository, never()).save(any());
    }
}