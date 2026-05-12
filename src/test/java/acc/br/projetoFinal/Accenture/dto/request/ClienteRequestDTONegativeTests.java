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

@DisplayName("ClienteRequestDTO — Testes Negativos")
class ClienteRequestDTONegativeTests {

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

    private boolean temViolacaoNoCampo(Set<ConstraintViolation<ClienteRequestDTO>> violations, String campo) {
        return violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals(campo));
    }

    // ------------------------------------------------------------------ nome

    @Test
    @DisplayName("deve falhar quando nome é nulo")
    void deveFalhar_quandoNomeNulo() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setNome(null);

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(temViolacaoNoCampo(violations, "nome")).isTrue();
    }

    @Test
    @DisplayName("deve falhar quando nome é vazio")
    void deveFalhar_quandoNomeVazio() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setNome("");

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(temViolacaoNoCampo(violations, "nome")).isTrue();
    }

    @Test
    @DisplayName("deve falhar quando nome é apenas espaços em branco")
    void deveFalhar_quandoNomeApenasEspacos() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setNome("   ");

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(temViolacaoNoCampo(violations, "nome")).isTrue();
    }

    @Test
    @DisplayName("deve falhar quando nome tem menos de 3 caracteres")
    void deveFalhar_quandoNomeMenorQueMinimo() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setNome("Jo");

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(temViolacaoNoCampo(violations, "nome")).isTrue();
    }

    @Test
    @DisplayName("deve falhar quando nome tem mais de 100 caracteres")
    void deveFalhar_quandoNomeMaiorQueMaximo() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setNome("A".repeat(101));

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(temViolacaoNoCampo(violations, "nome")).isTrue();
    }

    // ------------------------------------------------------------------ cpf

    @Test
    @DisplayName("deve falhar quando CPF é nulo")
    void deveFalhar_quandoCpfNulo() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setCpf(null);

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(temViolacaoNoCampo(violations, "cpf")).isTrue();
    }

    @Test
    @DisplayName("deve falhar quando CPF é vazio")
    void deveFalhar_quandoCpfVazio() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setCpf("");

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(temViolacaoNoCampo(violations, "cpf")).isTrue();
    }

    @Test
    @DisplayName("deve falhar quando CPF tem menos de 11 caracteres")
    void deveFalhar_quandoCpfMenorQueOnze() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setCpf("1234567890"); // 10 chars

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(temViolacaoNoCampo(violations, "cpf")).isTrue();
    }

    @Test
    @DisplayName("deve falhar quando CPF tem mais de 11 caracteres")
    void deveFalhar_quandoCpfMaiorQueOnze() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setCpf("123456789001"); // 12 chars

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(temViolacaoNoCampo(violations, "cpf")).isTrue();
    }

    // ------------------------------------------------------------------ email

    @Test
    @DisplayName("deve falhar quando email é nulo")
    void deveFalhar_quandoEmailNulo() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setEmail(null);

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(temViolacaoNoCampo(violations, "email")).isTrue();
    }

    @Test
    @DisplayName("deve falhar quando email é vazio")
    void deveFalhar_quandoEmailVazio() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setEmail("");

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(temViolacaoNoCampo(violations, "email")).isTrue();
    }

    @Test
    @DisplayName("deve falhar quando email não tem @")
    void deveFalhar_quandoEmailSemArroba() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setEmail("emailsemarroba.com");

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(temViolacaoNoCampo(violations, "email")).isTrue();
    }

    @Test
    @DisplayName("deve falhar quando email não tem domínio")
    void deveFalhar_quandoEmailSemDominio() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setEmail("usuario@");

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(temViolacaoNoCampo(violations, "email")).isTrue();
    }

    @Test
    @DisplayName("deve falhar quando email tem formato completamente inválido")
    void deveFalhar_quandoEmailFormatoInvalido() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setEmail("nao-e-um-email");

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(temViolacaoNoCampo(violations, "email")).isTrue();
    }

    // ------------------------------------------------------------------ senha

    @Test
    @DisplayName("deve falhar quando senha é nula")
    void deveFalhar_quandoSenhaNula() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setSenha(null);

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(temViolacaoNoCampo(violations, "senha")).isTrue();
    }

    @Test
    @DisplayName("deve falhar quando senha é vazia")
    void deveFalhar_quandoSenhaVazia() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setSenha("");

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(temViolacaoNoCampo(violations, "senha")).isTrue();
    }

    @Test
    @DisplayName("deve falhar quando senha tem menos de 6 caracteres")
    void deveFalhar_quandoSenhaMenorQueMinimo() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setSenha("abc1"); // 4 chars

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(temViolacaoNoCampo(violations, "senha")).isTrue();
    }

    @Test
    @DisplayName("deve falhar quando senha tem mais de 100 caracteres")
    void deveFalhar_quandoSenhaMaiorQueMaximo() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setSenha("S".repeat(101));

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(temViolacaoNoCampo(violations, "senha")).isTrue();
    }

    // ------------------------------------------------------------------ telefone

    @Test
    @DisplayName("deve falhar quando telefone tem mais de 15 caracteres")
    void deveFalhar_quandoTelefoneMaiorQueMaximo() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setTelefone("1".repeat(16)); // 16 chars

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(temViolacaoNoCampo(violations, "telefone")).isTrue();
    }

    // ------------------------------------------------------------------ endereco (@NotNull + @Valid cascaded)

    @Test
    @DisplayName("deve falhar quando endereço é nulo (@NotNull)")
    void deveFalhar_quandoEnderecoNulo() {
        ClienteRequestDTO dto = dtoPadraoValido();
        dto.setEndereco(null);

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(temViolacaoNoCampo(violations, "endereco")).isTrue();
    }

    @Test
    @DisplayName("deve falhar quando endereço interno tem campos inválidos (@Valid propagado)")
    void deveFalhar_quandoEnderecoInternoInvalido() {
        ClienteRequestDTO dto = dtoPadraoValido();
        // zera campos obrigatórios do endereço para forçar violação via @Valid
        dto.setEndereco(EnderecoRequestDTO.builder().build());

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        // ao menos uma violação deve ser num campo do endereço
        boolean temViolacaoNoEndereco = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().startsWith("endereco."));
        assertThat(temViolacaoNoEndereco).isTrue();
    }

    // ------------------------------------------------------------------ múltiplos campos inválidos ao mesmo tempo

    @Test
    @DisplayName("deve acumular múltiplas violações quando vários campos são inválidos")
    void deveFalhar_quandoMultiplosCamposInvalidos() {
        ClienteRequestDTO dto = ClienteRequestDTO.builder()
                .nome(null)
                .cpf(null)
                .email(null)
                .senha(null)
                .endereco(null)
                .build();

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        // nome, cpf, email, senha, endereco — mínimo 5 violações
        assertThat(violations.size()).isGreaterThanOrEqualTo(5);
    }
}