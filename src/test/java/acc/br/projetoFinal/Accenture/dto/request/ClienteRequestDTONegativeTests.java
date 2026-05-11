package acc.br.projetoFinal.Accenture.dto.request;

import acc.br.projetoFinal.Accenture.enums.TipoEndereco;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("ClienteRequestDTO - Testes Negativos")
class ClienteRequestDTONegativeTests {

    @Autowired
    private Validator validator;

    private ClienteRequestDTO dto;
    private EnderecoRequestDTO endereco;

    @BeforeEach
    void setUp() {
        endereco = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf("SP")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .numero("1000")
                .complemento("Apto 101")
                .build();
    }

    @Test
    @DisplayName("Deve rejeitar quando nome é vazio")
    void deveRejeitar_NomeVazio() {
        dto = ClienteRequestDTO.builder()
                .nome("")
                .cpf("12345678901")
                .email("joao@example.com")
                .senha("senha123")
                .endereco(endereco)
                .build();

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para nome vazio");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    @DisplayName("Deve rejeitar quando nome é null")
    void deveRejeitar_NomeNull() {
        dto = ClienteRequestDTO.builder()
                .nome(null)
                .cpf("12345678901")
                .email("joao@example.com")
                .senha("senha123")
                .endereco(endereco)
                .build();

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para nome null");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    @DisplayName("Deve rejeitar quando nome é menor que 3 caracteres")
    void deveRejeitar_NomeMenor3Caracteres() {
        dto = ClienteRequestDTO.builder()
                .nome("Jo")
                .cpf("12345678901")
                .email("joao@example.com")
                .senha("senha123")
                .endereco(endereco)
                .build();

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para nome com menos de 3 caracteres");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    @DisplayName("Deve rejeitar quando nome excede 100 caracteres")
    void deveRejeitar_NomeMaior100Caracteres() {
        String nomeLongo = "a".repeat(101);
        dto = ClienteRequestDTO.builder()
                .nome(nomeLongo)
                .cpf("12345678901")
                .email("joao@example.com")
                .senha("senha123")
                .endereco(endereco)
                .build();

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para nome com mais de 100 caracteres");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    @DisplayName("Deve rejeitar quando cpf é vazio")
    void deveRejeitar_CpfVazio() {
        dto = ClienteRequestDTO.builder()
                .nome("João Silva")
                .cpf("")
                .email("joao@example.com")
                .senha("senha123")
                .endereco(endereco)
                .build();

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para cpf vazio");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cpf")));
    }

    @Test
    @DisplayName("Deve rejeitar quando cpf não tem 11 caracteres")
    void deveRejeitar_CpfInvalido() {
        dto = ClienteRequestDTO.builder()
                .nome("João Silva")
                .cpf("123456789")
                .email("joao@example.com")
                .senha("senha123")
                .endereco(endereco)
                .build();

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para cpf inválido");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cpf")));
    }

    @Test
    @DisplayName("Deve rejeitar quando email é inválido")
    void deveRejeitar_EmailInvalido() {
        dto = ClienteRequestDTO.builder()
                .nome("João Silva")
                .cpf("12345678901")
                .email("emailinvalido")
                .senha("senha123")
                .endereco(endereco)
                .build();

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para email inválido");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Deve rejeitar quando email é vazio")
    void deveRejeitar_EmailVazio() {
        dto = ClienteRequestDTO.builder()
                .nome("João Silva")
                .cpf("12345678901")
                .email("")
                .senha("senha123")
                .endereco(endereco)
                .build();

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para email vazio");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Deve rejeitar quando senha é vazia")
    void deveRejeitar_SenhaVazia() {
        dto = ClienteRequestDTO.builder()
                .nome("João Silva")
                .cpf("12345678901")
                .email("joao@example.com")
                .senha("")
                .endereco(endereco)
                .build();

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para senha vazia");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("senha")));
    }

    @Test
    @DisplayName("Deve rejeitar quando senha tem menos de 6 caracteres")
    void deveRejeitar_SenhaMenor6Caracteres() {
        dto = ClienteRequestDTO.builder()
                .nome("João Silva")
                .cpf("12345678901")
                .email("joao@example.com")
                .senha("12345")
                .endereco(endereco)
                .build();

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para senha com menos de 6 caracteres");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("senha")));
    }

    @Test
    @DisplayName("Deve rejeitar quando telefone excede 15 caracteres")
    void deveRejeitar_TelefoneExcede15() {
        dto = ClienteRequestDTO.builder()
                .nome("João Silva")
                .cpf("12345678901")
                .email("joao@example.com")
                .senha("senha123")
                .telefone("119876543210123456")
                .endereco(endereco)
                .build();

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para telefone acima de 15 caracteres");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("telefone")));
    }

    @Test
    @DisplayName("Deve rejeitar quando endereco é null")
    void deveRejeitar_EnderecoNull() {
        dto = ClienteRequestDTO.builder()
                .nome("João Silva")
                .cpf("12345678901")
                .email("joao@example.com")
                .senha("senha123")
                .endereco(null)
                .build();

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para endereco null");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("endereco")));
    }

    @Test
    @DisplayName("Deve rejeitar quando endereco tem dados inválidos")
    void deveRejeitar_EnderecoInvalido() {
        EnderecoRequestDTO enderecoInvalido = EnderecoRequestDTO.builder()
                .cep("")
                .logradouro("")
                .bairro("")
                .cidade("")
                .uf("S")
                .tipoEndereco(null)
                .build();

        dto = ClienteRequestDTO.builder()
                .nome("João Silva")
                .cpf("12345678901")
                .email("joao@example.com")
                .senha("senha123")
                .endereco(enderecoInvalido)
                .build();

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violações para endereco inválido");
    }
}
