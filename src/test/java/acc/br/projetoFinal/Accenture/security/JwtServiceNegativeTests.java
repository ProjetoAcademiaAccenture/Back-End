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
        assertFalse(jwtService.tokenValido(null));
    }

    @Test
    @DisplayName("Deve rejeitar token vazio")
    void deveRejeitar_TokenVazio() {
        assertFalse(jwtService.tokenValido(""));
    }

    @Test
    @DisplayName("Deve rejeitar token inválido (sem pontos)")
    void deveRejeitar_TokenSemPontos() {
        assertFalse(jwtService.tokenValido("tokeminvalidosempontos"));
    }

    @Test
    @DisplayName("Deve rejeitar token com formato incorreto")
    void deveRejeitar_TokenFormatoIncorreto() {
        assertFalse(jwtService.tokenValido("header.payload"));
    }

    @Test
    @DisplayName("Deve rejeitar token com assinatura modificada")
    void deveRejeitar_TokenAssinaturaModificada() {
        String token = jwtService.gerarToken(cliente);
        String tokenModificado = token.substring(0, token.length() - 10) + "0000000000";

        assertFalse(jwtService.tokenValido(tokenModificado));
    }

    @Test
    @DisplayName("Deve rejeitar token com payload inválido")
    void deveRejeitar_TokenPayloadInvalido() {
        assertFalse(jwtService.tokenValido("header.payloadinvalido.signature"));
    }

    @Test
    @DisplayName("Deve rejeitar token com caracteres especiais inválidos")
    void deveRejeitar_TokenCaracteresEspeciais() {
        assertFalse(jwtService.tokenValido("!@#$%^&*()_+"));
    }

    @Test
    @DisplayName("Deve lançar JwtException ao extrair claims de token inválido")
    void deveRejeitar_ExtrairClaimsTokenInvalido() {
        assertThrows(JwtException.class, () ->
                jwtService.extrairClaims("header.payload.signature"));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao extrair claims de token null")
    void deveRejeitar_ExtrairClaimsTokenNull() {
        assertThrows(IllegalArgumentException.class, () ->
                jwtService.extrairClaims(null));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao extrair claims de token vazio")
    void deveRejeitar_ExtrairClaimsTokenVazio() {
        assertThrows(IllegalArgumentException.class, () ->
                jwtService.extrairClaims(""));
    }

    @Test
    @DisplayName("Deve lançar NullPointerException ao gerar token com cliente null")
    void deveRejeitar_ClienteNull() {
        // cast explícito para resolver ambiguidade entre gerarToken(Cliente) e gerarToken(Conta)
        assertThrows(NullPointerException.class, () ->
                jwtService.gerarToken((Cliente) null));
    }

    @Test
    @DisplayName("Deve gerar token com subject null quando cliente sem email")
    void deveRejeitar_ClienteSemEmail() {
        Cliente clienteSemEmail = new Cliente();
        clienteSemEmail.setTipoCliente(TipoCliente.ROLE_USER);
        clienteSemEmail.setEmail(null);

        // JWT aceita subject null — verificamos que o claims retorna null no subject
        String token = jwtService.gerarToken(clienteSemEmail);
        Claims claims = jwtService.extrairClaims(token);

        assertNull(claims.getSubject());
    }

    @Test
    @DisplayName("Deve lançar NullPointerException ao gerar token com tipoCliente null")
    void deveRejeitar_ClienteTipoNull() {
        Cliente clienteTipoNull = new Cliente();
        clienteTipoNull.setEmail("teste@example.com");
        clienteTipoNull.setTipoCliente(null);

        // cast explícito para resolver ambiguidade entre gerarToken(Cliente) e gerarToken(Conta)
        assertThrows(NullPointerException.class, () ->
                jwtService.gerarToken((Cliente) clienteTipoNull));
    }

    @Test
    @DisplayName("Deve rejeitar token com header modificado")
    void deveRejeitar_TokenHeaderModificado() {
        String token = jwtService.gerarToken(cliente);
        String[] partes = token.split("\\.");
        String tokenModificado = "headerfalso." + partes[1] + "." + partes[2];

        assertFalse(jwtService.tokenValido(tokenModificado));
    }

    @Test
    @DisplayName("Deve rejeitar token com payload modificado")
    void deveRejeitar_TokenPayloadModificado() {
        String token = jwtService.gerarToken(cliente);
        String[] partes = token.split("\\.");
        String tokenModificado = partes[0] + ".payloadfalso." + partes[2];

        assertFalse(jwtService.tokenValido(tokenModificado));
    }

    @Test
    @DisplayName("Deve rejeitar token com múltiplos pontos extra")
    void deveRejeitar_TokenMultiplosPontosExtra() {
        assertFalse(jwtService.tokenValido("header.payload.signature.extra"));
    }

    @Test
    @DisplayName("Deve rejeitar token com espaços")
    void deveRejeitar_TokenComEspacos() {
        String token = jwtService.gerarToken(cliente);
        String tokenComEspacos = token.replace(".", ". ");

        assertFalse(jwtService.tokenValido(tokenComEspacos));
    }

    @Test
    @DisplayName("Deve rejeitar token com quebra de linha")
    void deveRejeitar_TokenComQuebralinha() {
        String token = jwtService.gerarToken(cliente);
        String tokenComQuebra = token + "\n";

        assertFalse(jwtService.tokenValido(tokenComQuebra));
    }
}