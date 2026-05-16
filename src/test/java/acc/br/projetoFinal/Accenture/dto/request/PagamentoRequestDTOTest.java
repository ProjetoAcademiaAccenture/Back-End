package acc.br.projetoFinal.Accenture.dto.request;

import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // helper — DTO sempre válido
    private PagamentoRequestDTO dtoValido() {
        return PagamentoRequestDTO.builder()
                .pagamentoId(1L)
                .metodoPagamento(MetodoPagamento.PIX)
                .senhaTransacao("1234")
                .build();
    }

    // -----------------------------------------------------------------------
    // Lombok: @NoArgsConstructor, @AllArgsConstructor, @Builder, @Data
    // -----------------------------------------------------------------------

    @Test
    void noArgsConstructor_deveCriarInstanciaSemErro() {
        assertNotNull(new PagamentoRequestDTO());
    }

    @Test
    void allArgsConstructor_devePreencherTodosOsCampos() {
        PagamentoRequestDTO dto = new PagamentoRequestDTO(1L, MetodoPagamento.PIX, "1234");

        assertEquals(1L,                   dto.getPagamentoId());
        assertEquals(MetodoPagamento.PIX,  dto.getMetodoPagamento());
        assertEquals("1234",               dto.getSenhaTransacao());
    }

    @Test
    void builder_devePreencherTodosOsCampos() {
        PagamentoRequestDTO dto = dtoValido();

        assertEquals(1L,                      dto.getPagamentoId());
        assertEquals(MetodoPagamento.PIX,     dto.getMetodoPagamento());
        assertEquals("1234",                  dto.getSenhaTransacao());
    }

    @Test
    void setters_devemAtualizarCampos() {
        PagamentoRequestDTO dto = new PagamentoRequestDTO();
        dto.setPagamentoId(2L);
        dto.setMetodoPagamento(MetodoPagamento.CREDITO);
        dto.setSenhaTransacao("5678");

        assertEquals(2L,                      dto.getPagamentoId());
        assertEquals(MetodoPagamento.CREDITO, dto.getMetodoPagamento());
        assertEquals("5678",                  dto.getSenhaTransacao());
    }

    @Test
    void equals_hashCode_deveSerVerdadeiro_paraInstanciasIguais() {
        PagamentoRequestDTO a = dtoValido();
        PagamentoRequestDTO b = dtoValido();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_deveSerFalso_paraInstanciasDiferentes() {
        PagamentoRequestDTO a = dtoValido();
        PagamentoRequestDTO b = dtoValido();
        b.setPagamentoId(99L);
        assertNotEquals(a, b);
    }

    @Test
    void toString_naoDeveRetornarNulo_eDeveConterNomeClasse() {
        String str = dtoValido().toString();
        assertNotNull(str);
        assertTrue(str.contains("PagamentoRequestDTO"));
    }

    // -----------------------------------------------------------------------
    // Caminho feliz — sem violações
    // -----------------------------------------------------------------------

    @Test
    void validacao_devePassar_quandoDTOValido() {
        assertTrue(validator.validate(dtoValido()).isEmpty());
    }

    // -----------------------------------------------------------------------
    // @NotNull — pagamentoId
    // -----------------------------------------------------------------------

    @Test
    void validacao_deveFalhar_quandoPagamentoIdNulo() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setPagamentoId(null);
        assertViolacaoEm("pagamentoId", dto);
    }

    // -----------------------------------------------------------------------
    // @NotNull — metodoPagamento
    // -----------------------------------------------------------------------

    @Test
    void validacao_deveFalhar_quandoMetodoPagamentoNulo() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setMetodoPagamento(null);
        assertViolacaoEm("metodoPagamento", dto);
    }

    @Test
    void validacao_devePassar_paraMetodoBoleto() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setMetodoPagamento(MetodoPagamento.BOLETO);
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void validacao_devePassar_paraMetodoDebito() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setMetodoPagamento(MetodoPagamento.DEBITO);
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void validacao_devePassar_paraMetodoCredito() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setMetodoPagamento(MetodoPagamento.CREDITO);
        assertTrue(validator.validate(dto).isEmpty());
    }

    // -----------------------------------------------------------------------
    // @NotBlank — senhaTransacao
    // -----------------------------------------------------------------------

    @Test
    void validacao_deveFalhar_quandoSenhaTransacaoNula() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setSenhaTransacao(null);
        assertViolacaoEm("senhaTransacao", dto);
    }

    @Test
    void validacao_deveFalhar_quandoSenhaTransacaoEmBranco() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setSenhaTransacao("    ");
        assertViolacaoEm("senhaTransacao", dto);
    }

    // -----------------------------------------------------------------------
    // @Size(min=4, max=4) — senhaTransacao
    // -----------------------------------------------------------------------

    @Test
    void validacao_deveFalhar_quandoSenhaMenorQue4Digitos() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setSenhaTransacao("123");
        assertViolacaoEm("senhaTransacao", dto);
    }

    @Test
    void validacao_deveFalhar_quandoSenhaMaiorQue4Digitos() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setSenhaTransacao("12345");
        assertViolacaoEm("senhaTransacao", dto);
    }

    @Test
    void validacao_devePassar_quandoSenhaTem4Digitos() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setSenhaTransacao("0000");
        assertTrue(validator.validate(dto).isEmpty());
    }

    // -----------------------------------------------------------------------
    // Utilitário
    // -----------------------------------------------------------------------

    private void assertViolacaoEm(String campo, PagamentoRequestDTO dto) {
        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Esperava violação no campo: " + campo);
        assertTrue(
            violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals(campo)),
            "Violação esperada em '" + campo + "' mas não encontrada. Encontradas: " + violations
        );
    }
}