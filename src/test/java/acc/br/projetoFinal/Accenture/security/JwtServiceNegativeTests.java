package acc.br.projetoFinal.Accenture.security;

import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.enums.TipoCliente;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
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
@DisplayName("JwtService - Testes Negativos")
class JwtServiceNegativeTests {

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
    @DisplayName("Deve rejeitar token com valor null")
    void deveRejeitar_TokenNull() {
        boolean valido = jwtService.tokenValido(null);

        assertFalse(valido);
    }

    @Test
    @DisplayName("Deve rejeitar token vazio")
    void deveRejeitar_TokenVazio() {
        boolean valido = jwtService.tokenValido("");

        assertFalse(valido);
    }

    @Test
    @DisplayName("Deve rejeitar token inválido (sem pontos)")
    void deveRejeitar_TokenSemPontos() {
        boolean valido = jwtService.tokenValido("tokeminvalidosempontos");

        assertFalse(valido);
    }

    @Test
    @DisplayName("Deve rejeitar token com formato incorreto")
    void deveRejeitar_TokenFormatoIncorreto() {
        boolean valido = jwtService.tokenValido("header.payload");

        assertFalse(valido);
    }

    @Test
    @DisplayName("Deve rejeitar token com assinatura modificada")
    void deveRejeitar_TokenAssinaturaModificada() {
        String token = jwtService.gerarToken(cliente);
        String tokenModificado = token.substring(0, token.length() - 10) + "0000000000";

        boolean valido = jwtService.tokenValido(tokenModificado);

        assertFalse(valido);
    }

    @Test
    @DisplayName("Deve rejeitar token com payload inválido")
    void deveRejeitar_TokenPayloadInvalido() {
        String token = "header.payloadinvalido.signature";

        boolean valido = jwtService.tokenValido(token);

        assertFalse(valido);
    }

    @Test
    @DisplayName("Deve rejeitar token com caracteres especiais inválidos")
    void deveRejeitar_TokenCaracteresEspeciais() {
        String token = "!@#$%^&*()_+";

        boolean valido = jwtService.tokenValido(token);

        assertFalse(valido);
    }

    @Test
    @DisplayName("Deve rejeitar ao extrair claims de token inválido")
    void deveRejeitar_ExtrairClaimsTokenInvalido() {
        String tokenInvalido = "header.payload.signature";

        assertThrows(JwtException.class, () -> {
            jwtService.extrairClaims(tokenInvalido);
        });
    }

    @Test
    @DisplayName("Deve rejeitar token null ao extrair claims")
    void deveRejeitar_ExtrairClaimsTokenNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            jwtService.extrairClaims(null);
        });
    }

    @Test
    @DisplayName("Deve rejeitar token vazio ao extrair claims")
    void deveRejeitar_ExtrairClaimsTokenVazio() {
        assertThrows(IllegalArgumentException.class, () -> {
            jwtService.extrairClaims("");
        });
    }

    @Test
    @DisplayName("Deve rejeitar cliente null ao gerar token")
    void deveRejeitar_ClienteNull() {
        assertThrows(NullPointerException.class, () -> {
            jwtService.gerarToken(null);
        });
    }

    @Test
    @DisplayName("Deve rejeitar cliente sem email ao gerar token")
    void deveRejeitar_ClienteSemEmail() {
        Cliente clienteSemEmail = new Cliente();
        clienteSemEmail.setTipoCliente(TipoCliente.ROLE_USER);
        clienteSemEmail.setEmail(null);

        String token = jwtService.gerarToken(clienteSemEmail);
        Claims claims = jwtService.extrairClaims(token);

        assertNull(claims.getSubject());
    }

    @Test
    @DisplayName("Deve rejeitar cliente com tipo null ao gerar token")
    void deveRejeitar_ClienteTipoNull() {
        Cliente clienteTipoNull = new Cliente();
        clienteTipoNull.setEmail("teste@example.com");
        clienteTipoNull.setTipoCliente(null);

        assertThrows(NullPointerException.class, () -> {
            jwtService.gerarToken(clienteTipoNull);
        });
    }

    @Test
    @DisplayName("Deve rejeitar token com header modificado")
    void deveRejeitar_TokenHeaderModificado() {
        String token = jwtService.gerarToken(cliente);
        String[] partes = token.split("\\.");
        String tokenModificado = "headerfalso." + partes[1] + "." + partes[2];

        boolean valido = jwtService.tokenValido(tokenModificado);

        assertFalse(valido);
    }

    @Test
    @DisplayName("Deve rejeitar token com payload modificado")
    void deveRejeitar_TokenPayloadModificado() {
        String token = jwtService.gerarToken(cliente);
        String[] partes = token.split("\\.");
        String tokenModificado = partes[0] + ".payloadfalso." + partes[2];

        boolean valido = jwtService.tokenValido(tokenModificado);

        assertFalse(valido);
    }

    @Test
    @DisplayName("Deve rejeitar token com múltiplos pontos extra")
    void deveRejeitar_TokenMultiplosPontosExtra() {
        String tokenInvalido = "header.payload.signature.extra";

        boolean valido = jwtService.tokenValido(tokenInvalido);

        assertFalse(valido);
    }

    @Test
    @DisplayName("Deve rejeitar token com espaços")
    void deveRejeitar_TokenComEspacos() {
        String token = jwtService.gerarToken(cliente);
        String tokenComEspacos = token.replace(".", ". ");

        boolean valido = jwtService.tokenValido(tokenComEspacos);

        assertFalse(valido);
    }

    @Test
    @DisplayName("Deve rejeitar token com quebra de linha")
    void deveRejeitar_TokenComQuebralinha() {
        String token = jwtService.gerarToken(cliente);
        String tokenComQuebra = token + "\n";

        boolean valido = jwtService.tokenValido(tokenComQuebra);

        assertFalse(valido);
    }
}
