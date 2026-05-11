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
@DisplayName("EnderecoRequestDTO - Testes Positivos")
class EnderecoRequestDTOTests {

    @Autowired
    private Validator validator;

    private EnderecoRequestDTO dto;

    // TESTES POSITIVOS
    @Test
    @DisplayName("Deve validar EnderecoRequestDTO com todos os dados válidos")
    void deveValidarComDadosValidos() {
        dto = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf("SP")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .numero("1000")
                .complemento("Apto 101")
                .build();

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve validar EnderecoRequestDTO sem numero")
    void deveValidarSemNumero() {
        dto = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf("SP")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .complemento("Apto 101")
                .build();

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve validar EnderecoRequestDTO sem complemento")
    void deveValidarSemComplemento() {
        dto = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf("SP")
                .tipoEndereco(TipoEndereco.COMERCIAL)
                .numero("1000")
                .build();

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve validar EnderecoRequestDTO com numero máximo")
    void deveValidarComNumeroMaximo() {
        dto = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf("SP")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .numero("1234567890")
                .build();

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve validar EnderecoRequestDTO com complemento máximo")
    void deveValidarComComplementoMaximo() {
        String complementoMaximo = "a".repeat(100);
        dto = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf("SP")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .complemento(complementoMaximo)
                .build();

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }
}
