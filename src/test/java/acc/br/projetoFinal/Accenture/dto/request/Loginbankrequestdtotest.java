package acc.br.projetoFinal.Accenture.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LoginBankRequestDTOTest {

    private static Validator validator;
    private LoginBankRequestDTO dto;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        dto = new LoginBankRequestDTO();
        dto.setNumero_conta("12345-6");
        dto.setSenha("senha123");
    }

    // ─── Lombok @Data: getters, setters, equals, hashCode, toString ───────────

    @Test
    @DisplayName("getters e setters devem funcionar corretamente")
    void testGettersAndSetters() {
        assertThat(dto.getNumero_conta()).isEqualTo("12345-6");
        assertThat(dto.getSenha()).isEqualTo("senha123");

        dto.setNumero_conta("99999-9");
        dto.setSenha("novaSenha");

        assertThat(dto.getNumero_conta()).isEqualTo("99999-9");
        assertThat(dto.getSenha()).isEqualTo("novaSenha");
    }

    @Test
    @DisplayName("equals deve retornar true para objetos com mesmos valores")
    void testEqualsTrue() {
        LoginBankRequestDTO outro = new LoginBankRequestDTO();
        outro.setNumero_conta("12345-6");
        outro.setSenha("senha123");

        assertThat(dto).isEqualTo(outro);
    }

    @Test
    @DisplayName("equals deve retornar false para objetos com valores diferentes")
    void testEqualsFalse() {
        LoginBankRequestDTO outro = new LoginBankRequestDTO();
        outro.setNumero_conta("00000-0");
        outro.setSenha("outraSenha");

        assertThat(dto).isNotEqualTo(outro);
    }

    @Test
    @DisplayName("equals deve retornar false ao comparar com null")
    void testEqualsNull() {
        assertThat(dto.equals(null)).isFalse();
    }

    @Test
    @DisplayName("equals deve retornar false ao comparar com tipo diferente")
    void testEqualsDifferentType() {
        Object outro = new Object();
        assertThat(dto.equals(outro)).isFalse();
    }

    @Test
    @DisplayName("equals deve retornar true ao comparar consigo mesmo")
    void testEqualsSameReference() {
        assertThat(dto).isEqualTo(dto);
    }

    @Test
    @DisplayName("hashCode deve ser igual para objetos iguais")
    void testHashCodeEquals() {
        LoginBankRequestDTO outro = new LoginBankRequestDTO();
        outro.setNumero_conta("12345-6");
        outro.setSenha("senha123");

        assertThat(dto.hashCode()).isEqualTo(outro.hashCode());
    }

    @Test
    @DisplayName("hashCode deve ser diferente para objetos diferentes")
    void testHashCodeDifferent() {
        LoginBankRequestDTO outro = new LoginBankRequestDTO();
        outro.setNumero_conta("00000-0");
        outro.setSenha("outraSenha");

        assertThat(dto.hashCode()).isNotEqualTo(outro.hashCode());
    }

    @Test
    @DisplayName("toString deve conter os valores dos campos")
    void testToString() {
        String result = dto.toString();

        assertThat(result).contains("12345-6");
        assertThat(result).contains("senha123");
    }

    // ─── Validações @NotBlank ──────────────────────────────────────────────────

    @Test
    @DisplayName("DTO válido não deve ter violações")
    void testValidDto() {
        Set<ConstraintViolation<LoginBankRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("numero_conta null deve gerar violação @NotBlank")
    void testNumeroConta_Null() {
        dto.setNumero_conta(null);
        Set<ConstraintViolation<LoginBankRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString())
                .isEqualTo("numero_conta");
    }

    @Test
    @DisplayName("numero_conta em branco deve gerar violação @NotBlank")
    void testNumeroConta_Blank() {
        dto.setNumero_conta("   ");
        Set<ConstraintViolation<LoginBankRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString())
                .isEqualTo("numero_conta");
    }

    @Test
    @DisplayName("numero_conta vazio deve gerar violação @NotBlank")
    void testNumeroConta_Empty() {
        dto.setNumero_conta("");
        Set<ConstraintViolation<LoginBankRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString())
                .isEqualTo("numero_conta");
    }

    @Test
    @DisplayName("senha null deve gerar violação @NotBlank")
    void testSenha_Null() {
        dto.setSenha(null);
        Set<ConstraintViolation<LoginBankRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString())
                .isEqualTo("senha");
    }

    @Test
    @DisplayName("senha em branco deve gerar violação @NotBlank")
    void testSenha_Blank() {
        dto.setSenha("   ");
        Set<ConstraintViolation<LoginBankRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString())
                .isEqualTo("senha");
    }

    @Test
    @DisplayName("senha vazia deve gerar violação @NotBlank")
    void testSenha_Empty() {
        dto.setSenha("");
        Set<ConstraintViolation<LoginBankRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString())
                .isEqualTo("senha");
    }

    @Test
    @DisplayName("ambos os campos nulos devem gerar duas violações")
    void testBothFieldsNull() {
        dto.setNumero_conta(null);
        dto.setSenha(null);
        Set<ConstraintViolation<LoginBankRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(2);
    }

    @Test
    @DisplayName("ambos os campos em branco devem gerar duas violações")
    void testBothFieldsBlank() {
        dto.setNumero_conta("  ");
        dto.setSenha("  ");
        Set<ConstraintViolation<LoginBankRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(2);
    }

    // ─── Construtor padrão (gerado pelo Lombok @Data) ─────────────────────────

    @Test
    @DisplayName("construtor padrão deve criar instância com campos nulos")
    void testDefaultConstructor() {
        LoginBankRequestDTO novo = new LoginBankRequestDTO();

        assertThat(novo).isNotNull();
        assertThat(novo.getNumero_conta()).isNull();
        assertThat(novo.getSenha()).isNull();
    }
}