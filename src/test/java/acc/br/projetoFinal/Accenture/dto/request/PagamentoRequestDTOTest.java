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

    private PagamentoRequestDTO dtoValido() {
        return PagamentoRequestDTO.builder()
                .pagamentoId(1L)
                .metodoPagamento(MetodoPagamento.PIX)
                .senhaTransacao("1234")
                .build();
    }

    // -----------------------------------------------------------------------
    // Lombok
    // -----------------------------------------------------------------------

    @Test
    void noArgsConstructor_deveCriarInstanciaSemErro() {
        assertNotNull(new PagamentoRequestDTO());
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
        PagamentoRequestDTO dto = dtoValido();
        assertEquals(1L, dto.getPagamentoId());
        assertEquals(MetodoPagamento.PIX, dto.getMetodoPagamento());
        assertEquals("1234", dto.getSenhaTransacao());
    }

    @Test
    void setters_devemAtualizarCampos() {
        PagamentoRequestDTO dto = new PagamentoRequestDTO();
        dto.setPagamentoId(2L);
        dto.setMetodoPagamento(MetodoPagamento.CREDITO);
        dto.setSenhaTransacao("5678");
        assertEquals(2L, dto.getPagamentoId());
        assertEquals(MetodoPagamento.CREDITO, dto.getMetodoPagamento());
        assertEquals("5678", dto.getSenhaTransacao());
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
    // POSITIVOS — caminho feliz
    // -----------------------------------------------------------------------

    @Test
    void validacao_devePassar_quandoDTOValido_comPix() {
        assertTrue(validator.validate(dtoValido()).isEmpty());
    }

    @Test
    void validacao_devePassar_quandoDTOValido_comBoleto() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setMetodoPagamento(MetodoPagamento.BOLETO);
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void validacao_devePassar_quandoDTOValido_comDebito() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setMetodoPagamento(MetodoPagamento.DEBITO);
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void validacao_devePassar_quandoDTOValido_comCredito() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setMetodoPagamento(MetodoPagamento.CREDITO);
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void validacao_devePassar_quandoSenhaTem4Digitos() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setSenhaTransacao("0000");
        assertTrue(validator.validate(dto).isEmpty());
    }

    // -----------------------------------------------------------------------
    // NEGATIVOS — pagamentoId
    // -----------------------------------------------------------------------

    @Test
    void validacao_deveFalhar_quandoPagamentoIdNulo() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setPagamentoId(null);

        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("pagamentoId")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("ID do pagamento é obrigatório")));
    }

    // -----------------------------------------------------------------------
    // NEGATIVOS — metodoPagamento
    // -----------------------------------------------------------------------

    @Test
    void validacao_deveFalhar_quandoMetodoPagamentoNulo() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setMetodoPagamento(null);

        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("metodoPagamento")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Método de pagamento é obrigatório")));
    }

    // -----------------------------------------------------------------------
    // NEGATIVOS — senhaTransacao (@NotBlank)
    // -----------------------------------------------------------------------

    @Test
    void validacao_deveFalhar_quandoSenhaTransacaoNula() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setSenhaTransacao(null);

        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senhaTransacao")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Senha de transação é obrigatória")));
    }

    @Test
    void validacao_deveFalhar_quandoSenhaTransacaoVazia() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setSenhaTransacao("");

        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senhaTransacao")));
    }

    @Test
    void validacao_deveFalhar_quandoSenhaTransacaoSoEspacos() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setSenhaTransacao("    ");

        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senhaTransacao")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Senha de transação é obrigatória")));
    }

    // -----------------------------------------------------------------------
    // NEGATIVOS — senhaTransacao (@Size min=4, max=4)
    // -----------------------------------------------------------------------

    @Test
    void validacao_deveFalhar_quandoSenhaMenorQue4Digitos() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setSenhaTransacao("123");

        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senhaTransacao")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Senha de transação deve ter 4 dígitos")));
    }

    @Test
    void validacao_deveFalhar_quandoSenhaComUmDigito() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setSenhaTransacao("1");

        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senhaTransacao")));
    }

    @Test
    void validacao_deveFalhar_quandoSenhaMaiorQue4Digitos() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setSenhaTransacao("12345");

        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senhaTransacao")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Senha de transação deve ter 4 dígitos")));
    }

    @Test
    void validacao_deveFalhar_quandoSenhaComDezDigitos() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setSenhaTransacao("1234567890");

        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senhaTransacao")));
    }

    // -----------------------------------------------------------------------
    // NEGATIVOS — múltiplos campos inválidos simultaneamente
    // -----------------------------------------------------------------------

    @Test
    void validacao_deveFalhar_quandoTodosOsCamposNulos() {
        PagamentoRequestDTO dto = new PagamentoRequestDTO(null, null, null);

        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);

        // espera violações nos 3 campos
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("pagamentoId")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("metodoPagamento")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senhaTransacao")));
    }

    @Test
    void validacao_deveFalhar_quandoPagamentoIdNuloEMetodoNulo() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setPagamentoId(null);
        dto.setMetodoPagamento(null);

        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);

        assertEquals(2, violations.size());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("pagamentoId")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("metodoPagamento")));
    }

    @Test
    void validacao_deveFalhar_quandoSenhaInvalidaEMetodoNulo() {
        PagamentoRequestDTO dto = dtoValido();
        dto.setMetodoPagamento(null);
        dto.setSenhaTransacao("99999");

        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.size() >= 2);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("metodoPagamento")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senhaTransacao")));
    }
}