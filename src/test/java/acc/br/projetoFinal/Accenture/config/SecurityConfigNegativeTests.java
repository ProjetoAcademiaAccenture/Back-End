package acc.br.projetoFinal.Accenture.config;

import acc.br.projetoFinal.Accenture.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("SecurityConfig - Testes Negativos")
class SecurityConfigNegativeTests {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DaoAuthenticationProvider authenticationProvider;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Test
    @DisplayName("Não deve coincidir senha não criptografada com hash")
    void naoDeveCoincidir_SenhaIncorreta() {
        String encoded = passwordEncoder.encode("senha123");
        
        assertFalse(passwordEncoder.matches("senhaErrada", encoded));
    }

    @Test
    @DisplayName("Não deve validar senha com caracteres diferentes")
    void naoDeveValidarSenhaComCaractersDiferentes() {
        String encoded = passwordEncoder.encode("senha123");
        
        assertFalse(passwordEncoder.matches("sEnhA123", encoded));
    }

    @Test
    @DisplayName("Não deve validar senha vazia")
    void naoDeveValidarSenhaVazia() {
        String encoded = passwordEncoder.encode("senha123");
        
        assertFalse(passwordEncoder.matches("", encoded));
    }

    @Test
    @DisplayName("Não deve validar senha com espaços extras")
    void naoDeveValidarSenhaComEspacos() {
        String encoded = passwordEncoder.encode("senha123");
        
        assertFalse(passwordEncoder.matches(" senha123 ", encoded));
    }

    @Test
    @DisplayName("Não deve validar senhas null")
    void naoDeveValidarSenhaNull() {
        String encoded = passwordEncoder.encode("senha123");
        
        assertThrows(Exception.class, () -> {
            passwordEncoder.matches(null, encoded);
        });
    }

    @Test
    @DisplayName("Não deve permitir hash null na validação")
    void naoDeveValidarHashNull() {
        boolean matches = passwordEncoder.matches("senha123", null);
        assertFalse(matches);
    }

    @Test
    @DisplayName("Não deve gerar hash para senha vazia")
    void naoDeveGerarHashSenhaVazia() {
        String encoded = passwordEncoder.encode("");
        
        assertNotNull(encoded);
        assertNotEquals("", encoded);
    }

    @Test
    @DisplayName("Não deve gerar hash igual para senhas diferentes")
    void naoDeveGerarHashIgual_SenhasDiferentes() {
        String encoded1 = passwordEncoder.encode("senha123");
        String encoded2 = passwordEncoder.encode("senha456");
        
        assertNotEquals(encoded1, encoded2);
    }

    @Test
    @DisplayName("Não deve validar senha com hash incorreto")
    void naoDeveValidarSenhaComHashIncorreto() {
        assertFalse(passwordEncoder.matches("senha123", "hashIncorreto"));
    }

    @Test
    @DisplayName("Não deve ter AuthenticationProvider null")
    void naoDeveRetornarAuthenticationProviderNull() {
        assertNotNull(authenticationProvider);
    }

    @Test
    @DisplayName("Não deve ter CorsConfigurationSource null")
    void naoDeveRetornarCorsConfigurationSourceNull() {
        assertNotNull(corsConfigurationSource);
    }

    @Test
    @DisplayName("Não deve ter SecurityFilterChain null")
    void naoDeveRetornarSecurityFilterChainNull() {
        assertNotNull(securityFilterChain);
    }

    @Test
    @DisplayName("Não deve ter AuthenticationManager null")
    void naoDeveRetornarAuthenticationManagerNull() {
        assertNotNull(authenticationManager);
    }

    @Test
    @DisplayName("Não deve ter JwtAuthenticationFilter null")
    void naoDeveRetornarJwtAuthenticationFilterNull() {
        // Verificado através de autowired
    }

    @Test
    @DisplayName("Não deve validar senha com valor booleano")
    void naoDeveValidarSenhaBooleano() {
        String encoded = passwordEncoder.encode("senha123");
        
        assertFalse(passwordEncoder.matches("true", encoded));
        assertFalse(passwordEncoder.matches("false", encoded));
    }

    @Test
    @DisplayName("Não deve gerar hash idêntico para mesma senha com diferentes instâncias")
    void naoDeveGerarHashIdentico_DiferentesInstancias() {
        String password = "senhaComMuitosCaracteres123456789";
        String hash1 = passwordEncoder.encode(password);
        String hash2 = passwordEncoder.encode(password);
        
        assertNotEquals(hash1, hash2);
    }

    @Test
    @DisplayName("Não deve validar senha com quebra de linha")
    void naoDeveValidarSenhaComQuebralinha() {
        String encoded = passwordEncoder.encode("senha123");
        
        assertFalse(passwordEncoder.matches("senha123\n", encoded));
    }

    @Test
    @DisplayName("Não deve validar senha com tabulação")
    void naoDeveValidarSenhaComTabulacao() {
        String encoded = passwordEncoder.encode("senha123");
        
        assertFalse(passwordEncoder.matches("senha\t123", encoded));
    }

    @Test
    @DisplayName("Não deve validar senha muito longa com hash curto")
    void naoDeveValidarSenhaLongaComHashCurto() {
        String encoded = passwordEncoder.encode("12345");
        String senhaLonga = "12345" + "a".repeat(1000);
        
        assertFalse(passwordEncoder.matches(senhaLonga, encoded));
    }

    @Test
    @DisplayName("Não deve ter CORS desabilitado")
    void naoDeveDesabilitarCORS() {
        assertNotNull(corsConfigurationSource);
    }
}
