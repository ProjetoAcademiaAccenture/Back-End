package acc.br.projetoFinal.Accenture.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestDTONegativeTests {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ─── email: @NotBlank ─────────────────────────────────────────────────────

    @Test
    void validation_ShouldFail_WhenEmailIsNull() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail(null);
        dto.setSenha("senha123");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        Set<String> fields = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());

        assertFalse(violations.isEmpty());
        assertTrue(fields.contains("email"));
    }

    @Test
    void validation_ShouldFail_WhenEmailIsBlank() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("   ");
        dto.setSenha("senha123");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        Set<String> fields = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());

        assertFalse(violations.isEmpty());
        assertTrue(fields.contains("email"));
    }

    @Test
    void validation_ShouldFail_WhenEmailIsEmpty() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("");
        dto.setSenha("senha123");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        Set<String> fields = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());

        assertFalse(violations.isEmpty());
        assertTrue(fields.contains("email"));
    }

    // ─── email: @Email ────────────────────────────────────────────────────────

    @Test
    void validation_ShouldFail_WhenEmailHasNoAtSign() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("emailsemarroba.com");
        dto.setSenha("senha123");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        Set<String> fields = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());

        assertFalse(violations.isEmpty());
        assertTrue(fields.contains("email"));
    }

    @Test
    void validation_ShouldFail_WhenEmailHasNoDomain() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@");
        dto.setSenha("senha123");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        Set<String> fields = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());

        assertFalse(violations.isEmpty());
        assertTrue(fields.contains("email"));
    }

    @Test
    void validation_ShouldFail_WhenEmailHasNoUser() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("@domain.com");
        dto.setSenha("senha123");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        Set<String> fields = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());

        assertFalse(violations.isEmpty());
        assertTrue(fields.contains("email"));
    }

    @Test
    void validation_ShouldFail_WhenEmailIsPlainText() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("nao-é-um-email");
        dto.setSenha("senha123");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        Set<String> fields = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());

        assertFalse(violations.isEmpty());
        assertTrue(fields.contains("email"));
    }

    // ─── senha: @NotBlank ─────────────────────────────────────────────────────

    @Test
    void validation_ShouldFail_WhenSenhaIsNull() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@email.com");
        dto.setSenha(null);

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        Set<String> fields = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());

        assertFalse(violations.isEmpty());
        assertTrue(fields.contains("senha"));
    }

    @Test
    void validation_ShouldFail_WhenSenhaIsBlank() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@email.com");
        dto.setSenha("   ");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        Set<String> fields = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());

        assertFalse(violations.isEmpty());
        assertTrue(fields.contains("senha"));
    }

    @Test
    void validation_ShouldFail_WhenSenhaIsEmpty() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@email.com");
        dto.setSenha("");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        Set<String> fields = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());

        assertFalse(violations.isEmpty());
        assertTrue(fields.contains("senha"));
    }

    // ─── ambos inválidos ──────────────────────────────────────────────────────

    @Test
    void validation_ShouldFail_WhenBothFieldsAreNull() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail(null);
        dto.setSenha(null);

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        Set<String> fields = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());

        assertAll(
            () -> assertTrue(violations.size() >= 2),
            () -> assertTrue(fields.contains("email")),
            () -> assertTrue(fields.contains("senha"))
        );
    }

    @Test
    void validation_ShouldFail_WhenBothFieldsAreBlank() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("   ");
        dto.setSenha("   ");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        Set<String> fields = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());

        assertAll(
            () -> assertTrue(violations.size() >= 2),
            () -> assertTrue(fields.contains("email")),
            () -> assertTrue(fields.contains("senha"))
        );
    }

    // ─── equals ───────────────────────────────────────────────────────────────

    @Test
    void equals_ShouldReturnFalse_WhenEmailDiffers() {
        LoginRequestDTO dto1 = new LoginRequestDTO();
        dto1.setEmail("a@email.com");
        dto1.setSenha("senha123");

        LoginRequestDTO dto2 = new LoginRequestDTO();
        dto2.setEmail("b@email.com");
        dto2.setSenha("senha123");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void equals_ShouldReturnFalse_WhenSenhaDiffers() {
        LoginRequestDTO dto1 = new LoginRequestDTO();
        dto1.setEmail("user@email.com");
        dto1.setSenha("senha123");

        LoginRequestDTO dto2 = new LoginRequestDTO();
        dto2.setEmail("user@email.com");
        dto2.setSenha("outraSenha");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void equals_ShouldReturnFalse_WhenComparedToNull() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@email.com");
        dto.setSenha("senha123");

        assertNotEquals(null, dto);
    }

    @Test
    void equals_ShouldReturnFalse_WhenComparedToDifferentType() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@email.com");
        dto.setSenha("senha123");

        assertNotEquals("string", dto);
    }

    // ─── hashCode ─────────────────────────────────────────────────────────────

    @Test
    void hashCode_ShouldDiffer_WhenDTOsDiffer() {
        LoginRequestDTO dto1 = new LoginRequestDTO();
        dto1.setEmail("a@email.com");
        dto1.setSenha("senha1");

        LoginRequestDTO dto2 = new LoginRequestDTO();
        dto2.setEmail("b@email.com");
        dto2.setSenha("senha2");

        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }

    // ─── toString ─────────────────────────────────────────────────────────────

    @Test
    void toString_ShouldNotContainWrongValues() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@email.com");
        dto.setSenha("senha123");

        String result = dto.toString();

        assertAll(
            () -> assertFalse(result.contains("errado@email.com")),
            () -> assertFalse(result.contains("senhaErrada"))
        );
    }
}