package acc.br.projetoFinal.Accenture.security;

import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.enums.TipoCliente;
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
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = {
        "security.jwt.secret=meuSegredoMuitoGrandePraTestarOJwtServiceCorretamente123456",
        "security.jwt.expiration-ms=3600000"
})
@DisplayName("JwtAuthenticationFilter - Testes Negativos")
class JwtAuthenticationFilterNegativeTests {

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
    @DisplayName("Não deve autenticar com header Authorization vazio")
    void naoDeveAutenticarHeaderVazio() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Não deve autenticar com Bearer vazio")
    void naoDeveAutenticarBearerVazio() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Não deve autenticar com token inválido")
    void naoDeveAutenticarTokenInvalido() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidtoken123");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Não deve autenticar com Bearer case mismatch")
    void naoDeveAutenticarBearerCaseMismatch() throws ServletException, IOException {
        String token = jwtService.gerarToken(cliente);
        when(request.getHeader("Authorization")).thenReturn("bearer " + token);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Não deve autenticar com múltiplos Bearer prefixes")
    void naoDeveAutenticarMultiplosBearerPrefixes() throws ServletException, IOException {
        String token = jwtService.gerarToken(cliente);
        when(request.getHeader("Authorization")).thenReturn("Bearer Bearer " + token);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Não deve autenticar com token modificado")
    void naoDeveAutenticarTokenModificado() throws ServletException, IOException {
        String token = jwtService.gerarToken(cliente);
        String tokenModificado = token.substring(0, token.length() - 5) + "00000";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + tokenModificado);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Não deve autenticar com token null após Bearer")
    void naoDeveAutenticarTokenNull() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer null");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Não deve autenticar com header contendo apenas espaços")
    void naoDeveAutenticarHeaderComEspacos() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("   ");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Não deve autenticar com token contendo quebras de linha")
    void naoDeveAutenticarTokenComQuebralinha() throws ServletException, IOException {
        String token = jwtService.gerarToken(cliente);
        String tokenComQuebra = token.replace(".", ".\n");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + tokenComQuebra);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Não deve autenticar com token contendo caracteres especiais")
    void naoDeveAutenticarTokenComCaracteresEspeciais() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer !@#$%^&*()");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Não deve deixar contexto de segurança autenticado com token inválido")
    void naoDeveAutenticarComTokenInvalido() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer token.invalid.jwt");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Não deve processar header Authorization com valor booleano false")
    void naoDeveProcessarHeaderFalse() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("false");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Não deve processar header Authorization com apenas 'Bearer'")
    void naoDeveProcessarApenasBearer() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Não deve deixar usuario autenticado sem roles definidos")
    void naoDeveAutenticarSemRolesDefinidos() throws ServletException, IOException {
        String token = jwtService.gerarToken(cliente);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertFalse(SecurityContextHolder.getContext().getAuthentication().getAuthorities().isEmpty());
    }
}
