package acc.br.projetoFinal.Accenture.dto.request;

import acc.br.projetoFinal.Accenture.enums.TipoEndereco;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EnderecoRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // helper para montar um DTO 100% válido
    private EnderecoRequestDTO dtoValido() {
        return EnderecoRequestDTO.builder()
                .cep("58700000")
                .logradouro("Rua das Flores")
                .bairro("Centro")
                .cidade("Patos")
                .uf("PB")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .numero("123")
                .complemento("Apto 01")
                .build();
    }

    // -----------------------------------------------------------------------
    // Lombok: @NoArgsConstructor, @AllArgsConstructor, @Builder, @Data
    // -----------------------------------------------------------------------

    @Test
    void noArgsConstructor_deveCriarInstanciaSemErro() {
        assertNotNull(new EnderecoRequestDTO());
    }

    @Test
    void allArgsConstructor_devePreencherTodosOsCampos() {
        EnderecoRequestDTO dto = new EnderecoRequestDTO(
                "58700000", "Rua das Flores", "Centro", "Patos",
                "PB", TipoEndereco.RESIDENCIAL, "123", "Apto 01"
        );

        assertEquals("58700000",           dto.getCep());
        assertEquals("Rua das Flores",     dto.getLogradouro());
        assertEquals("Centro",             dto.getBairro());
        assertEquals("Patos",              dto.getCidade());
        assertEquals("PB",                 dto.getUf());
        assertEquals(TipoEndereco.RESIDENCIAL, dto.getTipoEndereco());
        assertEquals("123",                dto.getNumero());
        assertEquals("Apto 01",            dto.getComplemento());
    }

    @Test
    void builder_devePreencherTodosOsCampos() {
        EnderecoRequestDTO dto = dtoValido();

        assertEquals("58700000",           dto.getCep());
        assertEquals("Rua das Flores",     dto.getLogradouro());
        assertEquals("Centro",             dto.getBairro());
        assertEquals("Patos",              dto.getCidade());
        assertEquals("PB",                 dto.getUf());
        assertEquals(TipoEndereco.RESIDENCIAL, dto.getTipoEndereco());
        assertEquals("123",                dto.getNumero());
        assertEquals("Apto 01",            dto.getComplemento());
    }

    @Test
    void setters_devemAtualizarCampos() {
        EnderecoRequestDTO dto = new EnderecoRequestDTO();
        dto.setCep("58700000");
        dto.setLogradouro("Av. Principal");
        dto.setBairro("Bela Vista");
        dto.setCidade("Campina Grande");
        dto.setUf("PB");
        dto.setTipoEndereco(TipoEndereco.COMERCIAL);
        dto.setNumero("999");
        dto.setComplemento("Sala 2");

        assertEquals("58700000",          dto.getCep());
        assertEquals("Av. Principal",     dto.getLogradouro());
        assertEquals("Bela Vista",        dto.getBairro());
        assertEquals("Campina Grande",    dto.getCidade());
        assertEquals("PB",                dto.getUf());
        assertEquals(TipoEndereco.COMERCIAL, dto.getTipoEndereco());
        assertEquals("999",               dto.getNumero());
        assertEquals("Sala 2",            dto.getComplemento());
    }

    @Test
    void equals_hashCode_deveSerVerdadeiro_paraInstanciasIguais() {
        EnderecoRequestDTO a = dtoValido();
        EnderecoRequestDTO b = dtoValido();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_deveSerFalso_paraInstanciasDiferentes() {
        EnderecoRequestDTO a = dtoValido();
        EnderecoRequestDTO b = dtoValido();
        b.setCidade("João Pessoa");
        assertNotEquals(a, b);
    }

    @Test
    void toString_naoDeveRetornarNulo_eDeveConterNomeClasse() {
        String str = dtoValido().toString();
        assertNotNull(str);
        assertTrue(str.contains("EnderecoRequestDTO"));
    }

    // -----------------------------------------------------------------------
    // Caminho feliz — sem violações
    // -----------------------------------------------------------------------

    @Test
    void validacao_devePassar_quandoDTOValido() {
        assertTrue(validator.validate(dtoValido()).isEmpty());
    }

    @Test
    void validacao_devePassar_quandoNumeroEComplementoNulos() {
        EnderecoRequestDTO dto = dtoValido();
        dto.setNumero(null);
        dto.setComplemento(null);
        assertTrue(validator.validate(dto).isEmpty());
    }

    // -----------------------------------------------------------------------
    // @NotBlank — cep
    // -----------------------------------------------------------------------

    @Test
    void validacao_deveFalhar_quandoCepNulo() {
        EnderecoRequestDTO dto = dtoValido();
        dto.setCep(null);
        assertViolacaoEm("cep", dto);
    }

    @Test
    void validacao_deveFalhar_quandoCepEmBranco() {
        EnderecoRequestDTO dto = dtoValido();
        dto.setCep("   ");
        assertViolacaoEm("cep", dto);
    }

    // -----------------------------------------------------------------------
    // @NotBlank — logradouro
    // -----------------------------------------------------------------------

    @Test
    void validacao_deveFalhar_quandoLogradouroNulo() {
        EnderecoRequestDTO dto = dtoValido();
        dto.setLogradouro(null);
        assertViolacaoEm("logradouro", dto);
    }

    @Test
    void validacao_deveFalhar_quandoLogradouroEmBranco() {
        EnderecoRequestDTO dto = dtoValido();
        dto.setLogradouro("");
        assertViolacaoEm("logradouro", dto);
    }

    // -----------------------------------------------------------------------
    // @NotBlank — bairro
    // -----------------------------------------------------------------------

    @Test
    void validacao_deveFalhar_quandoBairroNulo() {
        EnderecoRequestDTO dto = dtoValido();
        dto.setBairro(null);
        assertViolacaoEm("bairro", dto);
    }

    @Test
    void validacao_deveFalhar_quandoBairroEmBranco() {
        EnderecoRequestDTO dto = dtoValido();
        dto.setBairro("  ");
        assertViolacaoEm("bairro", dto);
    }

    // -----------------------------------------------------------------------
    // @NotBlank — cidade
    // -----------------------------------------------------------------------

    @Test
    void validacao_deveFalhar_quandoCidadeNula() {
        EnderecoRequestDTO dto = dtoValido();
        dto.setCidade(null);
        assertViolacaoEm("cidade", dto);
    }

    @Test
    void validacao_deveFalhar_quandoCidadeEmBranco() {
        EnderecoRequestDTO dto = dtoValido();
        dto.setCidade("");
        assertViolacaoEm("cidade", dto);
    }

    // -----------------------------------------------------------------------
    // @NotBlank + @Size(min=2, max=2) — uf
    // -----------------------------------------------------------------------

    @Test
    void validacao_deveFalhar_quandoUfNula() {
        EnderecoRequestDTO dto = dtoValido();
        dto.setUf(null);
        assertViolacaoEm("uf", dto);
    }

    @Test
    void validacao_deveFalhar_quandoUfEmBranco() {
        EnderecoRequestDTO dto = dtoValido();
        dto.setUf("  ");
        assertViolacaoEm("uf", dto);
    }

    @Test
    void validacao_deveFalhar_quandoUfMenorQue2Caracteres() {
        EnderecoRequestDTO dto = dtoValido();
        dto.setUf("P");
        assertViolacaoEm("uf", dto);
    }

    @Test
    void validacao_deveFalhar_quandoUfMaiorQue2Caracteres() {
        EnderecoRequestDTO dto = dtoValido();
        dto.setUf("PBA");
        assertViolacaoEm("uf", dto);
    }

    @Test
    void validacao_devePassar_quandoUfTem2Caracteres() {
        EnderecoRequestDTO dto = dtoValido();
        dto.setUf("SP");
        assertTrue(validator.validate(dto).isEmpty());
    }

    // -----------------------------------------------------------------------
    // @NotNull — tipoEndereco
    // -----------------------------------------------------------------------

    @Test
    void validacao_deveFalhar_quandoTipoEnderecoNulo() {
        EnderecoRequestDTO dto = dtoValido();
        dto.setTipoEndereco(null);
        assertViolacaoEm("tipoEndereco", dto);
    }

    @Test
    void validacao_devePassar_paraTipoEnderecoComercial() {
        EnderecoRequestDTO dto = dtoValido();
        dto.setTipoEndereco(TipoEndereco.COMERCIAL);
        assertTrue(validator.validate(dto).isEmpty());
    }

    // -----------------------------------------------------------------------
    // @Size(max=10) — numero
    // -----------------------------------------------------------------------

    @Test
    void validacao_deveFalhar_quandoNumeroMaiorQue10Caracteres() {
        EnderecoRequestDTO dto = dtoValido();
        dto.setNumero("12345678901"); // 11 chars
        assertViolacaoEm("numero", dto);
    }

    @Test
    void validacao_devePassar_quandoNumeroTem10Caracteres() {
        EnderecoRequestDTO dto = dtoValido();
        dto.setNumero("1234567890"); // exato limite
        assertTrue(validator.validate(dto).isEmpty());
    }

    // -----------------------------------------------------------------------
    // @Size(max=100) — complemento
    // -----------------------------------------------------------------------

    @Test
    void validacao_deveFalhar_quandoComplementoMaiorQue100Caracteres() {
        EnderecoRequestDTO dto = dtoValido();
        dto.setComplemento("A".repeat(101));
        assertViolacaoEm("complemento", dto);
    }

    @Test
    void validacao_devePassar_quandoComplementoTem100Caracteres() {
        EnderecoRequestDTO dto = dtoValido();
        dto.setComplemento("A".repeat(100));
        assertTrue(validator.validate(dto).isEmpty());
    }

    // -----------------------------------------------------------------------
    // Utilitário
    // -----------------------------------------------------------------------

    private void assertViolacaoEm(String campo, EnderecoRequestDTO dto) {
        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Esperava violação no campo: " + campo);
        assertTrue(
            violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals(campo)),
            "Violação esperada em '" + campo + "' mas não encontrada. Encontradas: " + violations
        );
    }
    // -----------------------------------------------------------------------
// equals — comparações especiais (branches: this==o, null, outro tipo)
// -----------------------------------------------------------------------

@Test
void equals_deveSerVerdadeiro_comparandoConsigoProprio() {
    EnderecoRequestDTO dto = dtoValido();
    assertEquals(dto, dto);
}

@Test
void equals_deveSerFalso_comparandoComNulo() {
    EnderecoRequestDTO dto = dtoValido();
    assertNotEquals(null, dto);
}

@Test
void equals_deveSerFalso_comparandoComOutroTipo() {
    EnderecoRequestDTO dto = dtoValido();
    assertNotEquals("string", dto);
}

// -----------------------------------------------------------------------
// equals — branches por campo individual (valor diferente)
// -----------------------------------------------------------------------

@Test
void equals_deveSerFalso_quandoCepDiferente() {
    EnderecoRequestDTO a = dtoValido();
    EnderecoRequestDTO b = dtoValido();
    b.setCep("99999999");
    assertNotEquals(a, b);
}

@Test
void equals_deveSerFalso_quandoLogradouroDiferente() {
    EnderecoRequestDTO a = dtoValido();
    EnderecoRequestDTO b = dtoValido();
    b.setLogradouro("Av. Brasil");
    assertNotEquals(a, b);
}

@Test
void equals_deveSerFalso_quandoBairroDiferente() {
    EnderecoRequestDTO a = dtoValido();
    EnderecoRequestDTO b = dtoValido();
    b.setBairro("Outro Bairro");
    assertNotEquals(a, b);
}

@Test
void equals_deveSerFalso_quandoUfDiferente() {
    EnderecoRequestDTO a = dtoValido();
    EnderecoRequestDTO b = dtoValido();
    b.setUf("SP");
    assertNotEquals(a, b);
}

@Test
void equals_deveSerFalso_quandoTipoEnderecoDiferente() {
    EnderecoRequestDTO a = dtoValido();
    EnderecoRequestDTO b = dtoValido();
    b.setTipoEndereco(TipoEndereco.COMERCIAL);
    assertNotEquals(a, b);
}

@Test
void equals_deveSerFalso_quandoNumeroDiferente() {
    EnderecoRequestDTO a = dtoValido();
    EnderecoRequestDTO b = dtoValido();
    b.setNumero("456");
    assertNotEquals(a, b);
}

@Test
void equals_deveSerFalso_quandoComplementoDiferente() {
    EnderecoRequestDTO a = dtoValido();
    EnderecoRequestDTO b = dtoValido();
    b.setComplemento("Casa");
    assertNotEquals(a, b);
}

// -----------------------------------------------------------------------
// equals/hashCode — null-check branches (campo null vs não-null)
// -----------------------------------------------------------------------

@Test
void equals_deveSerVerdadeiro_quandoTodosOsCamposNulos() {
    EnderecoRequestDTO a = new EnderecoRequestDTO();
    EnderecoRequestDTO b = new EnderecoRequestDTO();
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
}

@Test
void equals_deveSerFalso_quandoCepNuloEmUmEPreenchidoNoOutro() {
    EnderecoRequestDTO a = new EnderecoRequestDTO(); // cep = null
    EnderecoRequestDTO b = dtoValido();
    assertNotEquals(a, b);
}

@Test
void equals_deveSerFalso_quandoLogradouroNuloEmUmEPreenchidoNoOutro() {
    EnderecoRequestDTO a = new EnderecoRequestDTO();
    a.setCep("58700000");
    // logradouro = null

    EnderecoRequestDTO b = dtoValido();
    assertNotEquals(a, b);
}

@Test
void equals_deveSerFalso_quandoBairroNuloEmUmEPreenchidoNoOutro() {
    EnderecoRequestDTO a = new EnderecoRequestDTO();
    a.setCep("58700000");
    a.setLogradouro("Rua das Flores");
    // bairro = null

    EnderecoRequestDTO b = dtoValido();
    assertNotEquals(a, b);
}

@Test
void equals_deveSerFalso_quandoCidadeNulaEmUmEPreenchidaNoOutro() {
    EnderecoRequestDTO a = new EnderecoRequestDTO();
    a.setCep("58700000");
    a.setLogradouro("Rua das Flores");
    a.setBairro("Centro");
    // cidade = null

    EnderecoRequestDTO b = dtoValido();
    assertNotEquals(a, b);
}

@Test
void equals_deveSerFalso_quandoUfNulaEmUmEPreenchidaNoOutro() {
    EnderecoRequestDTO a = new EnderecoRequestDTO();
    a.setCep("58700000");
    a.setLogradouro("Rua das Flores");
    a.setBairro("Centro");
    a.setCidade("Patos");
    // uf = null

    EnderecoRequestDTO b = dtoValido();
    assertNotEquals(a, b);
}

@Test
void equals_deveSerFalso_quandoTipoEnderecoNuloEmUmEPreenchidoNoOutro() {
    EnderecoRequestDTO a = new EnderecoRequestDTO();
    a.setCep("58700000");
    a.setLogradouro("Rua das Flores");
    a.setBairro("Centro");
    a.setCidade("Patos");
    a.setUf("PB");
    // tipoEndereco = null

    EnderecoRequestDTO b = dtoValido();
    assertNotEquals(a, b);
}

@Test
void equals_deveSerFalso_quandoNumeroNuloEmUmEPreenchidoNoOutro() {
    EnderecoRequestDTO a = new EnderecoRequestDTO();
    a.setCep("58700000");
    a.setLogradouro("Rua das Flores");
    a.setBairro("Centro");
    a.setCidade("Patos");
    a.setUf("PB");
    a.setTipoEndereco(TipoEndereco.RESIDENCIAL);
    // numero = null

    EnderecoRequestDTO b = dtoValido(); // numero = "123"
    assertNotEquals(a, b);
}

@Test
void equals_deveSerFalso_quandoComplementoNuloEmUmEPreenchidoNoOutro() {
    EnderecoRequestDTO a = new EnderecoRequestDTO();
    a.setCep("58700000");
    a.setLogradouro("Rua das Flores");
    a.setBairro("Centro");
    a.setCidade("Patos");
    a.setUf("PB");
    a.setTipoEndereco(TipoEndereco.RESIDENCIAL);
    a.setNumero("123");
    // complemento = null

    EnderecoRequestDTO b = dtoValido(); // complemento = "Apto 01"
    assertNotEquals(a, b);
}

// -----------------------------------------------------------------------
// hashCode — diferente quando campos diferem
// -----------------------------------------------------------------------

@Test
void hashCode_deveDiferir_quandoCamposDiferentes() {
    EnderecoRequestDTO a = dtoValido();
    EnderecoRequestDTO b = dtoValido();
    b.setCep("11111111");
    assertNotEquals(a.hashCode(), b.hashCode());
}

@Test
void hashCode_naoDeveLancarExcecao_quandoCamposNulos() {
    EnderecoRequestDTO dto = new EnderecoRequestDTO();
    assertDoesNotThrow(dto::hashCode);
}

// -----------------------------------------------------------------------
// toString — campos nulos não quebram
// -----------------------------------------------------------------------

@Test
void toString_naoDeveRetornarNulo_quandoCamposNulos() {
    EnderecoRequestDTO dto = new EnderecoRequestDTO();
    String str = dto.toString();
    assertNotNull(str);
    assertTrue(str.contains("EnderecoRequestDTO"));
}

@Test
void toString_deveConterValoresDosAtributos() {
    EnderecoRequestDTO dto = dtoValido();
    String str = dto.toString();
    assertAll(
        () -> assertTrue(str.contains("58700000")),
        () -> assertTrue(str.contains("Rua das Flores")),
        () -> assertTrue(str.contains("Centro")),
        () -> assertTrue(str.contains("Patos")),
        () -> assertTrue(str.contains("PB")),
        () -> assertTrue(str.contains("RESIDENCIAL")),
        () -> assertTrue(str.contains("123")),
        () -> assertTrue(str.contains("Apto 01"))
    );
}
}