package acc.br.projetoFinal.Accenture.security;

import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.Conta;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Gera token para o Cliente (Auth via Email)
     */
    public String gerarToken(Cliente cliente) {
        return builder()
                .subject(cliente.getEmail())
                // Garante o prefixo ROLE_ para o Spring Security
                .claim("role", "ROLE_" + cliente.getTipoCliente().name())
                .compact();
    }

    /**
     * Gera token para a Conta (Auth via Número da Conta)
     */
    public String gerarToken(Conta conta) {
        return builder()
                .subject(conta.getNumeroConta())
                // Define uma Role padrão para acessos via conta
                .claim("role", "ROLE_CLIENTE") 
                .claim("tipoConta", conta.getTipo().name())
                .compact();
    }

    /**
     * Helper para evitar repetição de código (DRY)
     */
    private io.jsonwebtoken.JwtBuilder builder() {
        Date now = new Date();
        return Jwts.builder()
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(getSigningKey());
    }

    public Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean tokenValido(String token) {
        try {
            extrairClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}