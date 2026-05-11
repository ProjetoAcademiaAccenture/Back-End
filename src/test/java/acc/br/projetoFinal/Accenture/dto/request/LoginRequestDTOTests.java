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
@DisplayName("LoginRequestDTO - Testes Positivos")
class LoginRequestDTOTests {

    @Autowired
    private Validator validator;

    private LoginRequestDTO dto;

    @Test
    @DisplayName("Deve validar LoginRequestDTO com todos os dados válidos")
    void deveValidarComDadosValidos() {
        dto = new LoginRequestDTO();
        dto.setEmail("usuario@example.com");
        dto.setSenha("senha123");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve validar LoginRequestDTO com email válido simples")
    void deveValidarComEmailValido() {
        dto = new LoginRequestDTO();
        dto.setEmail("test@test.com");
        dto.setSenha("123456");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve validar LoginRequestDTO com senha longa")
    void deveValidarComSenhaLonga() {
        dto = new LoginRequestDTO();
        dto.setEmail("usuario@example.com");
        dto.setSenha("senhamuitorealmentebemcompridacommuitos123caracteres");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve validar LoginRequestDTO com email contendo números")
    void deveValidarComEmailNumeros() {
        dto = new LoginRequestDTO();
        dto.setEmail("usuario123@example456.com");
        dto.setSenha("senha123");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }
}
