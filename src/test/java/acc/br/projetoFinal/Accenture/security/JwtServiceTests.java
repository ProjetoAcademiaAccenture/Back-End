package acc.br.projetoFinal.Accenture.security;

import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.enums.TipoCliente;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "security.jwt.secret=meuSegredoMuitoGrandePraTestarOJwtServiceCorretamente123456",
        "security.jwt.expiration-ms=3600000"
})
@DisplayName("JwtService - Testes Positivos")
class JwtServiceTests {

    @Autowired
    private JwtService jwtService;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setEmail("teste@example.com");
        cliente.setTipoCliente(TipoCliente.ROLE_USER);
    }

    @Test
    @DisplayName("Deve gerar token válido para cliente")
    void deveGerarTokenValido() {
        String token = jwtService.gerarToken(cliente);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    @DisplayName("Deve extrair claims do token válido")
    void deveExtrairClaimsDoTokenValido() {
        String token = jwtService.gerarToken(cliente);

        Claims claims = jwtService.extrairClaims(token);

        assertNotNull(claims);
        assertEquals("teste@example.com", claims.getSubject());
        assertEquals("ROLE_USER", claims.get("role", String.class));
    }

    @Test
    @DisplayName("Deve validar token válido")
    void deveValidarTokenValido() {
        String token = jwtService.gerarToken(cliente);

        boolean valido = jwtService.tokenValido(token);

        assertTrue(valido);
    }

    @Test
    @DisplayName("Deve extrair email do token")
    void deveExtrairEmailDoToken() {
        String token = jwtService.gerarToken(cliente);

        Claims claims = jwtService.extrairClaims(token);
        String email = claims.getSubject();

        assertEquals("teste@example.com", email);
    }

    @Test
    @DisplayName("Deve extrair role do token")
    void deveExtrairRoleDoToken() {
        cliente.setTipoCliente(TipoCliente.ROLE_ADMIN);
        String token = jwtService.gerarToken(cliente);

        Claims claims = jwtService.extrairClaims(token);
        String role = claims.get("role", String.class);

        assertEquals("ROLE_ADMIN", role);
    }

    @Test
    @DisplayName("Deve conter três partes no token (header.payload.signature)")
    void deveConterTresPartesNoToken() {
        String token = jwtService.gerarToken(cliente);

        String[] partes = token.split("\\.");

        assertEquals(3, partes.length);
    }

    @Test
    @DisplayName("Deve ter data de expiração no token")
    void deveExtrairDataExpiracaoDoToken() {
        String token = jwtService.gerarToken(cliente);

        Claims claims = jwtService.extrairClaims(token);

        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().getTime() > System.currentTimeMillis());
    }
}
