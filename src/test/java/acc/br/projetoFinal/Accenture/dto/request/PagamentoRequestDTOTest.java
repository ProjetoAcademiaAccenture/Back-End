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

    // -----------------------------------------------------------------------
    // Lombok: @NoArgsConstructor, @AllArgsConstructor, @Builder, @Data
    // -----------------------------------------------------------------------

    @Test
    void noArgsConstructor_deveCriarInstanciaSemErro() {
        PagamentoRequestDTO dto = new PagamentoRequestDTO();
        assertNotNull(dto);
    }

    @Test
    void allArgsConstructor_devePreencherTodosOsCampos() {
        PagamentoRequestDTO dto = new PagamentoRequestDTO(1L, MetodoPagamento.PIX, "1234");

        assertEquals(1L, dto.getPagamentoId());
        assertEquals(MetodoPagamento.PIX, dto.getMetodoPagamento());
        assertEquals("1234", dto.getSenhaTransacao());
    }

    @Test
    void builder_devePreencherTodosOsCampos() {
        PagamentoRequestDTO dto = PagamentoRequestDTO.builder()
                .pagamentoId(2L)
                .metodoPagamento(MetodoPagamento.CREDITO)
                .senhaTransacao("5678")
                .build();

        assertEquals(2L, dto.getPagamentoId());
        assertEquals(MetodoPagamento.CREDITO, dto.getMetodoPagamento());
        assertEquals("5678", dto.getSenhaTransacao());
    }

    @Test
    void setters_devemAtualizarCampos() {
        PagamentoRequestDTO dto = new PagamentoRequestDTO();
        dto.setPagamentoId(3L);
        dto.setMetodoPagamento(MetodoPagamento.BOLETO);
        dto.setSenhaTransacao("9999");

        assertEquals(3L, dto.getPagamentoId());
        assertEquals(MetodoPagamento.BOLETO, dto.getMetodoPagamento());
        assertEquals("9999", dto.getSenhaTransacao());
    }

    @Test
    void equals_deveSerVerdadeiro_paraInstanciasIguais() {
        PagamentoRequestDTO a = new PagamentoRequestDTO(1L, MetodoPagamento.PIX, "1234");
        PagamentoRequestDTO b = new PagamentoRequestDTO(1L, MetodoPagamento.PIX, "1234");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_deveSerFalso_paraInstanciasDiferentes() {
        PagamentoRequestDTO a = new PagamentoRequestDTO(1L, MetodoPagamento.PIX, "1234");
        PagamentoRequestDTO b = new PagamentoRequestDTO(2L, MetodoPagamento.CREDITO, "5678");
        assertNotEquals(a, b);
    }

    @Test
    void toString_naoDeveRetornarNulo() {
        PagamentoRequestDTO dto = new PagamentoRequestDTO(1L, MetodoPagamento.PIX, "1234");
        assertNotNull(dto.toString());
        assertTrue(dto.toString().contains("PagamentoRequestDTO"));
    }

    // -----------------------------------------------------------------------
    // Bean Validation — objeto válido
    // -----------------------------------------------------------------------

    @Test
    void validacao_devePassar_quandoDTOValido() {
        PagamentoRequestDTO dto = new PagamentoRequestDTO(1L, MetodoPagamento.PIX, "1234");
        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    // -----------------------------------------------------------------------
    // @NotNull pagamentoId
    // -----------------------------------------------------------------------

    @Test
    void validacao_deveFalhar_quandoPagamentoIdNulo() {
        PagamentoRequestDTO dto = new PagamentoRequestDTO(null, MetodoPagamento.PIX, "1234");
        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("pagamentoId")));
    }

    // -----------------------------------------------------------------------
    // @NotNull metodoPagamento
    // -----------------------------------------------------------------------

    @Test
    void validacao_deveFalhar_quandoMetodoPagamentoNulo() {
        PagamentoRequestDTO dto = new PagamentoRequestDTO(1L, null, "1234");
        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("metodoPagamento")));
    }

    // -----------------------------------------------------------------------
    // @NotBlank senhaTransacao
    // -----------------------------------------------------------------------

    @Test
    void validacao_deveFalhar_quandoSenhaTransacaoNula() {
        PagamentoRequestDTO dto = new PagamentoRequestDTO(1L, MetodoPagamento.PIX, null);
        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senhaTransacao")));
    }

    @Test
    void validacao_deveFalhar_quandoSenhaTransacaoEmBranco() {
        PagamentoRequestDTO dto = new PagamentoRequestDTO(1L, MetodoPagamento.PIX, "   ");
        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senhaTransacao")));
    }

    // -----------------------------------------------------------------------
    // @Size(min=4, max=4) senhaTransacao
    // -----------------------------------------------------------------------

    @Test
    void validacao_deveFalhar_quandoSenhaMenorQue4Digitos() {
        PagamentoRequestDTO dto = new PagamentoRequestDTO(1L, MetodoPagamento.PIX, "123");
        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senhaTransacao")));
    }

    @Test
    void validacao_deveFalhar_quandoSenhaMaiorQue4Digitos() {
        PagamentoRequestDTO dto = new PagamentoRequestDTO(1L, MetodoPagamento.PIX, "12345");
        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senhaTransacao")));
    }

    @Test
    void validacao_devePassar_quandoSenhaTem4Digitos() {
        PagamentoRequestDTO dto = new PagamentoRequestDTO(1L, MetodoPagamento.BOLETO, "0000");
        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    // -----------------------------------------------------------------------
    // Todos os métodos de pagamento são cobertos
    // -----------------------------------------------------------------------

    @Test
    void validacao_devePassar_paraMetodoDebito() {
        PagamentoRequestDTO dto = new PagamentoRequestDTO(1L, MetodoPagamento.DEBITO, "1234");
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void validacao_devePassar_paraMetodoCredito() {
        PagamentoRequestDTO dto = new PagamentoRequestDTO(1L, MetodoPagamento.CREDITO, "1234");
        assertTrue(validator.validate(dto).isEmpty());
    }
}