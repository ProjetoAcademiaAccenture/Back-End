package acc.br.projetoFinal.Accenture.dto.request;

import acc.br.projetoFinal.Accenture.enums.TipoEndereco;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ClienteRequestDTO — Testes Positivos")
class ClienteRequestDTOTests {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    // ------------------------------------------------------------------ helper

    private ClienteRequestDTO dtoPadraoValido() {
        return ClienteRequestDTO.builder()
                .nome("Maria Silva")
                .cpf("12345678900")
                .email("maria@email.com")
                .senha("Senha@123")
                .telefone("83999990000")
                .dtNascimento(LocalDate.of(1990, 5, 20))
                .endereco(enderecoPadraoValido())
                .build();
    }

    private EnderecoRequestDTO enderecoPadraoValido() {
        return EnderecoRequestDTO.builder()
                .cep("58000-000")
                .logradouro("Rua das Flores")
                .bairro("Centro")
                .cidade("João Pessoa")
                .uf("PB")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .numero("123")
                .complemento("Apto 1")
                .build();
    }

    // ------------------------------------------------------------------ DTO completo válido

    @Test
    @DisplayName("deve passar na validação com todos os campos válidos")
    void devePassarValidacao_quandoTodosCamposValidos() {
        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dtoPadraoValido());

        assertThat(violations).isEmpty();
    }

    // ------------------------------------------------------------------ nome

    @Test
    @DisplayName("deve aceitar nome com exatamente 3 caracteres (limite mínimo)")
    void deveAceitar_nomeComTresCaracteres() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setNome("Ana");

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    @DisplayName("deve aceitar nome com exatamente 100 caracteres (limite máximo)")
    void deveAceitar_nomeComCemCaracteres() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setNome("A".repeat(100));

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    @DisplayName("deve aceitar nome com comprimento intermediário")
    void deveAceitar_nomeComComprimentoIntermediario() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setNome("Carlos Eduardo Mendes");

        assertThat(validator.validate(dto)).isEmpty();
    }

    // ------------------------------------------------------------------ cpf

    @Test
    @DisplayName("deve aceitar CPF com exatamente 11 caracteres")
    void deveAceitar_cpfComOnzeCaracteres() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setCpf("98765432100");

        assertThat(validator.validate(dto)).isEmpty();
    }

    // ------------------------------------------------------------------ email

    @Test
    @DisplayName("deve aceitar email com formato válido")
    void deveAceitar_emailValido() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setEmail("usuario@dominio.com.br");

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    @DisplayName("deve aceitar email com subdomínio")
    void deveAceitar_emailComSubdominio() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setEmail("user@mail.empresa.org");

        assertThat(validator.validate(dto)).isEmpty();
    }

    // ------------------------------------------------------------------ senha

    @Test
    @DisplayName("deve aceitar senha com exatamente 6 caracteres (limite mínimo)")
    void deveAceitar_senhaComSeisCacteres() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setSenha("abc123");

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    @DisplayName("deve aceitar senha com exatamente 100 caracteres (limite máximo)")
    void deveAceitar_senhaComCemCaracteres() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setSenha("S".repeat(100));

        assertThat(validator.validate(dto)).isEmpty();
    }

    // ------------------------------------------------------------------ telefone

    @Test
    @DisplayName("deve aceitar telefone nulo (campo opcional)")
    void deveAceitar_telefoneNulo() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setTelefone(null);

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    @DisplayName("deve aceitar telefone vazio (campo opcional)")
    void deveAceitar_telefoneVazio() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setTelefone("");

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    @DisplayName("deve aceitar telefone com exatamente 15 caracteres (limite máximo)")
    void deveAceitar_telefoneComQuinzeCaracteres() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setTelefone("(83)99999-00001"); // 15 chars

        assertThat(validator.validate(dto)).isEmpty();
    }

    // ------------------------------------------------------------------ dtNascimento

    @Test
    @DisplayName("deve aceitar dtNascimento nulo (campo opcional)")
    void deveAceitar_dtNascimentoNulo() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setDtNascimento(null);

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    @DisplayName("deve aceitar dtNascimento com data passada")
    void deveAceitar_dtNascimentoComDataPassada() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setDtNascimento(LocalDate.of(2000, 1, 1));

        assertThat(validator.validate(dto)).isEmpty();
    }

    // ------------------------------------------------------------------ endereco (@Valid propagado)

    @Test
    @DisplayName("deve aceitar endereço completamente preenchido")
    void deveAceitar_enderecoCompleto() {
        ClienteRequestDTO dto = dtoPadraoValido();

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    @DisplayName("deve aceitar endereço com complemento nulo (campo opcional)")
    void deveAceitar_enderecoSemComplemento() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.getEndereco().setComplemento(null);

        assertThat(validator.validate(dto)).isEmpty();
    }
}