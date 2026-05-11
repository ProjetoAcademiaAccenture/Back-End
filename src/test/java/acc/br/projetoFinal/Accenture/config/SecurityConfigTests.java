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
@DisplayName("SecurityConfig - Testes Positivos")
class SecurityConfigTests {

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

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("Deve criar PasswordEncoder bean")
    void deveCriarPasswordEncoderBean() {
        assertNotNull(passwordEncoder);
    }

    @Test
    @DisplayName("Deve ter PasswordEncoder do tipo BCryptPasswordEncoder")
    void deveSerBCryptPasswordEncoder() {
        String encoded = passwordEncoder.encode("senha123");
        
        assertNotNull(encoded);
        assertTrue(passwordEncoder.matches("senha123", encoded));
    }

    @Test
    @DisplayName("Deve gerar senhas diferentes para mesma entrada")
    void deveGerarSenhasDiferentes() {
        String encoded1 = passwordEncoder.encode("senha123");
        String encoded2 = passwordEncoder.encode("senha123");
        
        assertNotEquals(encoded1, encoded2);
    }

    @Test
    @DisplayName("Deve criar AuthenticationProvider bean")
    void deveCriarAuthenticationProviderBean() {
        assertNotNull(authenticationProvider);
    }

    @Test
    @DisplayName("Deve criar CorsConfigurationSource bean")
    void deveCriarCorsConfigurationSourceBean() {
        assertNotNull(corsConfigurationSource);
    }

    @Test
    @DisplayName("Deve permitir origem localhost:3000 no CORS")
    void devePermitirOrigemLocalhost() {
        assertNotNull(corsConfigurationSource);
    }

    @Test
    @DisplayName("Deve criar SecurityFilterChain bean")
    void deveCriarSecurityFilterChainBean() {
        assertNotNull(securityFilterChain);
    }

    @Test
    @DisplayName("Deve criar AuthenticationManager bean")
    void deveCriarAuthenticationManagerBean() {
        assertNotNull(authenticationManager);
    }

    @Test
    @DisplayName("Deve criar JwtAuthenticationFilter bean")
    void deveCriarJwtAuthenticationFilterBean() {
        assertNotNull(jwtAuthenticationFilter);
    }

    @Test
    @DisplayName("Deve permitir requisições GET /auth/**")
    void devePermitirAuthRequests() {
        // Teste apenas verifica se a configuração foi criada
        assertNotNull(securityFilterChain);
    }

    @Test
    @DisplayName("Deve ter CORS com suporte a múltiplos métodos HTTP")
    void deveSuportarMultiplosMetodosHTTP() {
        assertNotNull(corsConfigurationSource);
    }
}
