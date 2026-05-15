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

    // ─────────────────────────────────────────────
    //  Cenários positivos (sem violações esperadas)
    // ─────────────────────────────────────────────

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
    @DisplayName("Deve validar EnderecoRequestDTO sem numero (campo opcional)")
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
    @DisplayName("Deve validar EnderecoRequestDTO sem complemento (campo opcional)")
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
    @DisplayName("Deve validar EnderecoRequestDTO sem numero e sem complemento")
    void deveValidarSemNumeroESemComplemento() {
        dto = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf("SP")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .build();

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve validar EnderecoRequestDTO com numero no limite máximo (10 caracteres)")
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
    @DisplayName("Deve validar EnderecoRequestDTO com complemento no limite máximo (100 caracteres)")
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

    @Test
    @DisplayName("Deve validar com tipoEndereco COMERCIAL")
    void deveValidarComTipoEnderecoComercial() {
        dto = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("Rua Augusta")
                .bairro("Consolação")
                .cidade("São Paulo")
                .uf("SP")
                .tipoEndereco(TipoEndereco.COMERCIAL)
                .build();

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações para tipoEndereco COMERCIAL");
    }

    // ─────────────────────────────────────────────
    //  Cobertura de getters / equals / hashCode / toString (Lombok)
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar os valores corretos pelos getters")
    void deveRetornarValoresCorretosPelosGetters() {
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

        assertEquals("01310100", dto.getCep());
        assertEquals("Avenida Paulista", dto.getLogradouro());
        assertEquals("Bela Vista", dto.getBairro());
        assertEquals("São Paulo", dto.getCidade());
        assertEquals("SP", dto.getUf());
        assertEquals(TipoEndereco.RESIDENCIAL, dto.getTipoEndereco());
        assertEquals("1000", dto.getNumero());
        assertEquals("Apto 101", dto.getComplemento());
    }

    @Test
    @DisplayName("Deve instanciar via construtor sem args e permitir setters")
    void deveInstanciarViaConstrutorSemArgs() {
        dto = new EnderecoRequestDTO();
        dto.setCep("04538133");
        dto.setLogradouro("Rua Funchal");
        dto.setBairro("Vila Olímpia");
        dto.setCidade("São Paulo");
        dto.setUf("SP");
        dto.setTipoEndereco(TipoEndereco.RESIDENCIAL);
        dto.setNumero("100");
        dto.setComplemento("Sala 3");

        assertEquals("04538133", dto.getCep());
        assertEquals("Rua Funchal", dto.getLogradouro());
        assertEquals("Vila Olímpia", dto.getBairro());
        assertEquals("São Paulo", dto.getCidade());
        assertEquals("SP", dto.getUf());
        assertEquals(TipoEndereco.RESIDENCIAL, dto.getTipoEndereco());
        assertEquals("100", dto.getNumero());
        assertEquals("Sala 3", dto.getComplemento());
    }

    @Test
    @DisplayName("Deve instanciar via construtor com todos os args")
    void deveInstanciarViaConstrutorComTodosArgs() {
        dto = new EnderecoRequestDTO(
                "01310100",
                "Avenida Paulista",
                "Bela Vista",
                "São Paulo",
                "SP",
                TipoEndereco.RESIDENCIAL,
                "1000",
                "Apto 101"
        );

        assertNotNull(dto);
        assertEquals("01310100", dto.getCep());
        assertEquals(TipoEndereco.RESIDENCIAL, dto.getTipoEndereco());
    }

    @Test
    @DisplayName("Dois DTOs com mesmos dados devem ser iguais (equals/hashCode)")
    void doisDtosComMesmosDadosDevemSerIguais() {
        EnderecoRequestDTO dto1 = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf("SP")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .numero("1000")
                .complemento("Apto 101")
                .build();

        EnderecoRequestDTO dto2 = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf("SP")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .numero("1000")
                .complemento("Apto 101")
                .build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Dois DTOs com dados diferentes não devem ser iguais")
    void doisDtosComDadosDiferentesNaoDevemSerIguais() {
        EnderecoRequestDTO dto1 = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf("SP")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .build();

        EnderecoRequestDTO dto2 = EnderecoRequestDTO.builder()
                .cep("04538133")
                .logradouro("Rua Funchal")
                .bairro("Vila Olímpia")
                .cidade("São Paulo")
                .uf("SP")
                .tipoEndereco(TipoEndereco.COMERCIAL)
                .build();

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("toString deve conter os campos principais")
    void toStringDeveConterCamposPrincipais() {
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

        String result = dto.toString();
        assertNotNull(result);
        assertTrue(result.contains("01310100"));
        assertTrue(result.contains("Avenida Paulista"));
        assertTrue(result.contains("SP"));
    }

    @Test
    @DisplayName("Deve permitir alterar campo via setter após criação")
    void devePermitirAlterarCampoViaSetterAposCriacao() {
        dto = EnderecoRequestDTO.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf("SP")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .build();

        dto.setCidade("Campinas");
        dto.setUf("SP");
        dto.setNumero("200");
        dto.setComplemento("Bloco B");

        assertEquals("Campinas", dto.getCidade());
        assertEquals("200", dto.getNumero());
        assertEquals("Bloco B", dto.getComplemento());

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações após alteração válida");
    }
}