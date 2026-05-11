package acc.br.projetoFinal.Accenture.dto.request;

import acc.br.projetoFinal.Accenture.enums.TipoEndereco;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DisplayName("ClienteRequestDTO - Testes Positivos")
class ClienteRequestDTOTests {

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
    @DisplayName("Deve validar ClienteRequestDTO com todos os dados válidos")
    void deveValidarComDadosValidos() {
        dto = ClienteRequestDTO.builder()
                .nome("João Silva")
                .cpf("12345678901")
                .email("joao@example.com")
                .senha("senha123")
                .telefone("11987654321")
                .dtNascimento(LocalDate.of(1990, 5, 15))
                .endereco(endereco)
                .build();

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve validar ClienteRequestDTO com dados mínimos válidos")
    void deveValidarComDadosMinimoValidos() {
        dto = ClienteRequestDTO.builder()
                .nome("João")
                .cpf("12345678901")
                .email("joao@example.com")
                .senha("123456")
                .endereco(endereco)
                .build();

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve validar ClienteRequestDTO com telefone máximo")
    void deveValidarComTelefoneMaximo() {
        dto = ClienteRequestDTO.builder()
                .nome("João Silva")
                .cpf("12345678901")
                .email("joao@example.com")
                .senha("senha123")
                .telefone("119876543211")
                .endereco(endereco)
                .build();

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve validar ClienteRequestDTO sem telefone")
    void deveValidarSemTelefone() {
        dto = ClienteRequestDTO.builder()
                .nome("João Silva")
                .cpf("12345678901")
                .email("joao@example.com")
                .senha("senha123")
                .endereco(endereco)
                .build();

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }
}
