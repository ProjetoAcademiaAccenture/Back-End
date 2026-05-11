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
		return Keys.hmacShaKeyFor(
				secret.getBytes(StandardCharsets.UTF_8)
		);
	}

	public String gerarToken(Cliente cliente) {
		Date now = new Date();
		Date exp = new Date(now.getTime() + expirationMs);

		return Jwts.builder()
				.subject(cliente.getEmail())
				.claim("role", cliente.getTipoCliente().name())
				.issuedAt(now)
				.expiration(exp)
				.signWith(getSigningKey())
				.compact();
	}

	public String gerarToken(Conta conta) {
		Date now = new Date();
		Date exp = new Date(now.getTime() + expirationMs);

		return Jwts.builder()
				.subject(conta.getNumeroConta())
				.claim("tipo", conta.getTipo().name())
				.issuedAt(now)
				.expiration(exp)
				.signWith(getSigningKey())
				.compact();
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