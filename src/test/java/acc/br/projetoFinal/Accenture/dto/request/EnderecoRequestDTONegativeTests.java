package acc.br.projetoFinal.Accenture.dto.request;

import acc.br.projetoFinal.Accenture.enums.TipoEndereco;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("EnderecoRequestDTO - Testes Negativos")
class EnderecoRequestDTONegativeTests {

    @Autowired
    private Validator validator;

    private EnderecoRequestDTO dto;

    @Test
    @DisplayName("Deve rejeitar quando cep é vazio")
    void deveRejeitar_CepVazio() {
        dto = EnderecoRequestDTO.builder()
                .cep("")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf("SP")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .build();

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para cep vazio");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cep")));
    }

    @Test
    @DisplayName("Deve rejeitar quando cep é null")
    void deveRejeitar_CepNull() {
        dto = EnderecoRequestDTO.builder()
                .cep(null)
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf("SP")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .build();

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para cep null");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cep")));
    }

    @Test
    @DisplayName("Deve rejeitar quando logradouro é vazio")
    void deveRejeitar_LogradouroVazio() {
        dto = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf("SP")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .build();

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para logradouro vazio");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("logradouro")));
    }

    @Test
    @DisplayName("Deve rejeitar quando logradouro é null")
    void deveRejeitar_LogradouroNull() {
        dto = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro(null)
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf("SP")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .build();

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para logradouro null");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("logradouro")));
    }

    @Test
    @DisplayName("Deve rejeitar quando bairro é vazio")
    void deveRejeitar_BairroVazio() {
        dto = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("")
                .cidade("São Paulo")
                .uf("SP")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .build();

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para bairro vazio");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("bairro")));
    }

    @Test
    @DisplayName("Deve rejeitar quando bairro é null")
    void deveRejeitar_BairroNull() {
        dto = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro(null)
                .cidade("São Paulo")
                .uf("SP")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .build();

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para bairro null");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("bairro")));
    }

    @Test
    @DisplayName("Deve rejeitar quando cidade é vazio")
    void deveRejeitar_CidadeVazio() {
        dto = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("")
                .uf("SP")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .build();

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para cidade vazio");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cidade")));
    }

    @Test
    @DisplayName("Deve rejeitar quando cidade é null")
    void deveRejeitar_CidadeNull() {
        dto = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade(null)
                .uf("SP")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .build();

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para cidade null");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cidade")));
    }

    @Test
    @DisplayName("Deve rejeitar quando uf é vazio")
    void deveRejeitar_UfVazio() {
        dto = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf("")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .build();

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para uf vazio");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("uf")));
    }

    @Test
    @DisplayName("Deve rejeitar quando uf é null")
    void deveRejeitar_UfNull() {
        dto = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf(null)
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .build();

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para uf null");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("uf")));
    }

    @Test
    @DisplayName("Deve rejeitar quando uf tem menos de 2 caracteres")
    void deveRejeitar_UfMenor2Caracteres() {
        dto = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf("S")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .build();

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para uf com menos de 2 caracteres");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("uf")));
    }

    @Test
    @DisplayName("Deve rejeitar quando uf tem mais de 2 caracteres")
    void deveRejeitar_UfMaior2Caracteres() {
        dto = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf("SPP")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .build();

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para uf com mais de 2 caracteres");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("uf")));
    }

    @Test
    @DisplayName("Deve rejeitar quando tipoEndereco é null")
    void deveRejeitar_TipoEnderecoNull() {
        dto = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf("SP")
                .tipoEndereco(null)
                .build();

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para tipoEndereco null");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("tipoEndereco")));
    }

    @Test
    @DisplayName("Deve rejeitar quando numero excede 10 caracteres")
    void deveRejeitar_NumeroMaior10Caracteres() {
        dto = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf("SP")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .numero("12345678901")
                .build();

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para numero com mais de 10 caracteres");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("numero")));
    }

    @Test
    @DisplayName("Deve rejeitar quando complemento excede 100 caracteres")
    void deveRejeitar_ComplementoMaior100Caracteres() {
        String complementoLongo = "a".repeat(101);
        dto = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf("SP")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .complemento(complementoLongo)
                .build();

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para complemento com mais de 100 caracteres");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("complemento")));
    }
}
