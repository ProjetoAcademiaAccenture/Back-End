package acc.br.projetoFinal.Accenture.security;

import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.enums.TipoCliente;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = {
        "security.jwt.secret=meuSegredoMuitoGrandePraTestarOJwtServiceCorretamente123456",
        "security.jwt.expiration-ms=3600000"
})
@DisplayName("JwtAuthenticationFilter - Testes Positivos")
class JwtAuthenticationFilterTests {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private FilterChain filterChain;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        SecurityContextHolder.clearContext();

        cliente = new Cliente();
        cliente.setEmail("teste@example.com");
        cliente.setTipoCliente(TipoCliente.ROLE_USER);
    }

    @Test
    @DisplayName("Deve processar requisição sem header Authorization")
    void deveProcessarSemHeaderAuthorization() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve processar requisição com header Authorization sem Bearer")
    void deveProcessarHeaderSemBearer() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve processar requisição com token Bearer válido")
    void deveProcessarTokenBearerValido() throws ServletException, IOException {
        String token = jwtService.gerarToken(cliente);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve definir username do cliente no contexto de segurança")
    void deveDefinirUsernameNoContexto() throws ServletException, IOException {
        String token = jwtService.gerarToken(cliente);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        assertEquals("teste@example.com", username);
    }

    @Test
    @DisplayName("Deve definir autoridade do cliente no contexto de segurança")
    void deveDefinirAutoridadeNoContexto() throws ServletException, IOException {
        String token = jwtService.gerarToken(cliente);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("Deve processar requisição com Bearer token inválido")
    void deveProcessarBearerTokenInvalido() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer tokeninvalido");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve processar token com role ADMIN")
    void deveProcessarTokenComRoleAdmin() throws ServletException, IOException {
        cliente.setTipoCliente(TipoCliente.ROLE_ADMIN);
        String token = jwtService.gerarToken(cliente);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Deve continuar a cadeia de filtros após autenticação")
    void deveContinuarCadeiaDeFiltrosaposAutenticacao() throws ServletException, IOException {
        String token = jwtService.gerarToken(cliente);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }
}
