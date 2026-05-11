package acc.br.projetoFinal.Accenture.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("LoginRequestDTO - Testes Negativos")
class LoginRequestDTONegativeTests {

    @Autowired
    private Validator validator;

    private LoginRequestDTO dto;

    @Test
    @DisplayName("Deve rejeitar quando email é vazio")
    void deveRejeitar_EmailVazio() {
        dto = new LoginRequestDTO();
        dto.setEmail("");
        dto.setSenha("senha123");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para email vazio");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Deve rejeitar quando email é null")
    void deveRejeitar_EmailNull() {
        dto = new LoginRequestDTO();
        dto.setEmail(null);
        dto.setSenha("senha123");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para email null");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Deve rejeitar quando email é inválido (sem @)")
    void deveRejeitar_EmailInvalido_SemArroba() {
        dto = new LoginRequestDTO();
        dto.setEmail("usuarioexample.com");
        dto.setSenha("senha123");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para email inválido");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Deve rejeitar quando email é inválido (sem domínio)")
    void deveRejeitar_EmailInvalido_SemDominio() {
        dto = new LoginRequestDTO();
        dto.setEmail("usuario@");
        dto.setSenha("senha123");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para email inválido");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Deve rejeitar quando email é inválido (somente @)")
    void deveRejeitar_EmailInvalido_ApenasArroba() {
        dto = new LoginRequestDTO();
        dto.setEmail("@");
        dto.setSenha("senha123");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para email inválido");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Deve rejeitar quando senha é vazia")
    void deveRejeitar_SenhaVazia() {
        dto = new LoginRequestDTO();
        dto.setEmail("usuario@example.com");
        dto.setSenha("");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para senha vazia");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("senha")));
    }

    @Test
    @DisplayName("Deve rejeitar quando senha é null")
    void deveRejeitar_SenhaNull() {
        dto = new LoginRequestDTO();
        dto.setEmail("usuario@example.com");
        dto.setSenha(null);

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para senha null");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("senha")));
    }

    @Test
    @DisplayName("Deve rejeitar quando ambos email e senha são vazios")
    void deveRejeitar_AmbosVazios() {
        dto = new LoginRequestDTO();
        dto.setEmail("");
        dto.setSenha("");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violações para ambos vazios");
        assertTrue(violations.size() >= 2, "Deve haver pelo menos 2 violações");
    }

    @Test
    @DisplayName("Deve rejeitar quando ambos email e senha são null")
    void deveRejeitar_AmbosNull() {
        dto = new LoginRequestDTO();
        dto.setEmail(null);
        dto.setSenha(null);

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violações para ambos null");
        assertTrue(violations.size() >= 2, "Deve haver pelo menos 2 violações");
    }
}
