package acc.br.projetoFinal.Accenture.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestDTOPositiveTests {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ─── Getters / Setters ────────────────────────────────────────────────────

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@email.com");
        dto.setSenha("senha123");

        assertAll(
            () -> assertEquals("user@email.com", dto.getEmail()),
            () -> assertEquals("senha123",       dto.getSenha())
        );
    }

    // ─── Validação: dados válidos ─────────────────────────────────────────────

    @Test
    void validation_ShouldPass_WithValidEmailAndSenha() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@email.com");
        dto.setSenha("senha123");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_ShouldPass_WithSubdomainEmail() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@mail.company.com");
        dto.setSenha("qualquerSenha");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_ShouldPass_WithLongSenha() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("test@test.com");
        dto.setSenha("senhaMuitoLongaComVariosCaracteres123!@#");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    // ─── equals / hashCode ────────────────────────────────────────────────────

    @Test
    void equals_ShouldReturnTrue_ForEqualDTOs() {
        LoginRequestDTO dto1 = new LoginRequestDTO();
        dto1.setEmail("user@email.com");
        dto1.setSenha("senha123");

        LoginRequestDTO dto2 = new LoginRequestDTO();
        dto2.setEmail("user@email.com");
        dto2.setSenha("senha123");

        assertAll(
            () -> assertEquals(dto1, dto2),
            () -> assertEquals(dto1.hashCode(), dto2.hashCode())
        );
    }

    @Test
    void equals_ShouldReturnTrue_WithItself() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@email.com");
        dto.setSenha("senha123");

        assertEquals(dto, dto);
    }

    // ─── toString ─────────────────────────────────────────────────────────────

    @Test
    void toString_ShouldContainEmailAndSenha() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@email.com");
        dto.setSenha("senha123");

        String result = dto.toString();

        assertAll(
            () -> assertTrue(result.contains("user@email.com")),
            () -> assertTrue(result.contains("senha123"))
        );
    }
}