package acc.br.projetoFinal.Accenture.config;

import acc.br.projetoFinal.Accenture.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final UserDetailsService userDetailsService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:3000"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.cors(Customizer.withDefaults())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

						// --- Produtos ---
						.requestMatchers(HttpMethod.GET, "/api/produtos/**").permitAll()
						.requestMatchers("/api/produtos/**").hasAuthority("ROLE_ADMIN")

						// --- Clientes ---
						.requestMatchers(HttpMethod.POST, "/api/clientes").permitAll()
						.requestMatchers("/api/clientes/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

						// --- Pedidos ---
								.requestMatchers(HttpMethod.POST, "/api/pedidos").hasAuthority("ROLE_USER")
								.requestMatchers(HttpMethod.GET, "/api/pedidos/cliente/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
								.requestMatchers(HttpMethod.GET, "/api/pedidos/*").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
								.requestMatchers(HttpMethod.PATCH, "/api/pedidos/*/cancelar").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
								.requestMatchers("/api/pedidos/**").hasAuthority("ROLE_ADMIN")

						// --- Boletos ---
						.requestMatchers("/api/boletos/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

						// ... aplique o mesmo para os demais (Pagamentos, Contas, etc)
						.requestMatchers("/api/pagamentos/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
						.anyRequest().authenticated()
				)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
}