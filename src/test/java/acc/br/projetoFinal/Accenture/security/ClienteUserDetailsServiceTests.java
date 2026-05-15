package acc.br.projetoFinal.Accenture.security;

import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.enums.TipoCliente;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.repository.ContaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayName("ClienteUserDetailsService - Testes Positivos")
class ClienteUserDetailsServiceTests {

    @Autowired
    private ClienteUserDetailsService userDetailsService;

    @MockBean
    private ClienteRepository clienteRepository;

    @MockBean
    private ContaRepository contaRepository;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setEmail("teste@example.com");
        cliente.setSenha("senha123");
        cliente.setTipoCliente(TipoCliente.ROLE_USER);
    }

    @Test
    @DisplayName("Deve carregar detalhes do usuário por email válido")
    void deveCarregarDetalhesDoUsuario() {
        when(clienteRepository.findByEmail("teste@example.com")).thenReturn(Optional.of(cliente));

        UserDetails userDetails = userDetailsService.loadUserByUsername("teste@example.com");

        assertNotNull(userDetails);
        assertEquals("teste@example.com", userDetails.getUsername());
        assertEquals("senha123", userDetails.getPassword());
    }

    @Test
    @DisplayName("Deve retornar usuário com autoridade CLIENTE")
    void deveRetornarComAutoridadeCliente() {
        when(clienteRepository.findByEmail("teste@example.com")).thenReturn(Optional.of(cliente));

        UserDetails userDetails = userDetailsService.loadUserByUsername("teste@example.com");

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("Deve retornar usuário com autoridade ADMIN")
    void deveRetornarComAutoridadeAdmin() {
        cliente.setTipoCliente(TipoCliente.ROLE_ADMIN);
        when(clienteRepository.findByEmail("teste@example.com")).thenReturn(Optional.of(cliente));

        UserDetails userDetails = userDetailsService.loadUserByUsername("teste@example.com");

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Deve carregar detalhes com email contendo números")
    void deveCarregarComEmailComNumeros() {
        cliente.setEmail("teste123@example456.com");
        when(clienteRepository.findByEmail("teste123@example456.com")).thenReturn(Optional.of(cliente));

        UserDetails userDetails = userDetailsService.loadUserByUsername("teste123@example456.com");

        assertNotNull(userDetails);
        assertEquals("teste123@example456.com", userDetails.getUsername());
    }

    @Test
    @DisplayName("Deve carregar detalhes com email em minúsculas")
    void deveCarregarComEmailMinusculas() {
        cliente.setEmail("usuario@example.com");
        when(clienteRepository.findByEmail("usuario@example.com")).thenReturn(Optional.of(cliente));

        UserDetails userDetails = userDetailsService.loadUserByUsername("usuario@example.com");

        assertNotNull(userDetails);
        assertEquals("usuario@example.com", userDetails.getUsername());
    }

    @Test
    @DisplayName("Deve retornar usuário com senha criptografada")
    void deveRetornarComSenhaCriptografada() {
        cliente.setSenha("$2a$10$hashedPassword123456789");
        when(clienteRepository.findByEmail("teste@example.com")).thenReturn(Optional.of(cliente));

        UserDetails userDetails = userDetailsService.loadUserByUsername("teste@example.com");

        assertEquals("$2a$10$hashedPassword123456789", userDetails.getPassword());
    }

    @Test
    @DisplayName("Deve carregar detalhes com email muito longo")
    void deveCarregarComEmailMuitoLongo() {
        String emailLongo = "usuario" + "a".repeat(100) + "@example.com";
        cliente.setEmail(emailLongo);
        when(clienteRepository.findByEmail(emailLongo)).thenReturn(Optional.of(cliente));

        UserDetails userDetails = userDetailsService.loadUserByUsername(emailLongo);

        assertNotNull(userDetails);
        assertEquals(emailLongo, userDetails.getUsername());
    }

    @Test
    @DisplayName("Deve retornar usuário enabled")
    void deveRetornarUsuarioEnabled() {
        when(clienteRepository.findByEmail("teste@example.com")).thenReturn(Optional.of(cliente));

        UserDetails userDetails = userDetailsService.loadUserByUsername("teste@example.com");

        assertTrue(userDetails.isEnabled());
    }

    @Test
    @DisplayName("Deve retornar usuário com credenciais válidas")
    void deveRetornarComCredenciaisValidas() {
        when(clienteRepository.findByEmail("teste@example.com")).thenReturn(Optional.of(cliente));

        UserDetails userDetails = userDetailsService.loadUserByUsername("teste@example.com");

        assertTrue(userDetails.isCredentialsNonExpired());
    }
}
