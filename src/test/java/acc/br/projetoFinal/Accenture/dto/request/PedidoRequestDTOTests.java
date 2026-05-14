package acc.br.projetoFinal.Accenture.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("PedidoRequestDTO - Testes")
class PedidoRequestDTOTests {

    @Autowired
    private Validator validator;

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static List<ItemPedidoRequestDTO> itensValidos() {
        return new ArrayList<>(List.of(
                ItemPedidoRequestDTO.builder().produtoId(1L).quantidade(5).build()
        ));
    }

    private static PedidoRequestDTO dtoPadrao() {
        return PedidoRequestDTO.builder()
                .clienteId(1L)
                .itens(itensValidos())
                .metodoPagamento("CARTAO_CREDITO")
                .build();
    }

    // =========================================================================
    // CONSTRUTORES
    // =========================================================================

    @Test
    @DisplayName("Deve criar objeto com construtor padrão — todos os campos nulos")
    void deveCriarComConstrutorPadrao() {
        PedidoRequestDTO dto = new PedidoRequestDTO();
        assertNotNull(dto);
        assertNull(dto.getClienteId());
        assertNull(dto.getItens());
        assertNull(dto.getMetodoPagamento());
    }

    @Test
    @DisplayName("Deve criar objeto com construtor completo (@AllArgsConstructor)")
    void deveCriarComConstrutorCompleto() {
        List<ItemPedidoRequestDTO> itens = itensValidos();
        PedidoRequestDTO dto = new PedidoRequestDTO(1L, itens, "PIX");

        assertEquals(1L, dto.getClienteId());
        assertEquals(itens, dto.getItens());
        assertEquals("PIX", dto.getMetodoPagamento());
    }

    // =========================================================================
    // BUILDER
    // =========================================================================

    @Test
    @DisplayName("Deve criar objeto via builder com todos os campos")
    void deveCriarViaBuilderCompleto() {
        List<ItemPedidoRequestDTO> itens = itensValidos();

        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(1L)
                .itens(itens)
                .metodoPagamento("BOLETO")
                .build();

        assertEquals(1L, dto.getClienteId());
        assertEquals(itens, dto.getItens());
        assertEquals("BOLETO", dto.getMetodoPagamento());
    }

    @Test
    @DisplayName("Deve criar objeto via builder vazio — todos os campos nulos")
    void deveCriarViaBuilderVazio() {
        PedidoRequestDTO dto = PedidoRequestDTO.builder().build();
        assertNotNull(dto);
        assertNull(dto.getClienteId());
        assertNull(dto.getItens());
        assertNull(dto.getMetodoPagamento());
    }

    // =========================================================================
    // SETTERS / GETTERS
    // =========================================================================

    @Test
    @DisplayName("Deve definir e obter clienteId via setter")
    void deveDefinirClienteId() {
        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setClienteId(10L);
        assertEquals(10L, dto.getClienteId());
    }

    @Test
    @DisplayName("Deve definir e obter itens via setter")
    void deveDefinirItens() {
        PedidoRequestDTO dto = new PedidoRequestDTO();
        List<ItemPedidoRequestDTO> itens = itensValidos();
        dto.setItens(itens);
        assertEquals(itens, dto.getItens());
    }

    @Test
    @DisplayName("Deve definir e obter metodoPagamento via setter")
    void deveDefinirMetodoPagamento() {
        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setMetodoPagamento("CARTAO_DEBITO");
        assertEquals("CARTAO_DEBITO", dto.getMetodoPagamento());
    }

    // =========================================================================
    // VALIDAÇÃO — cenários válidos (zero violations)
    // =========================================================================

    @Test
    @DisplayName("Deve passar validação com um único item e método de pagamento")
    void deveValidarComUmItem() {
        assertTrue(validator.validate(dtoPadrao()).isEmpty());
    }

    @Test
    @DisplayName("Deve passar validação com múltiplos itens válidos")
    void deveValidarComMultiplosItens() {
        List<ItemPedidoRequestDTO> itens = new ArrayList<>(List.of(
                ItemPedidoRequestDTO.builder().produtoId(1L).quantidade(5).build(),
                ItemPedidoRequestDTO.builder().produtoId(2L).quantidade(10).build(),
                ItemPedidoRequestDTO.builder().produtoId(3L).quantidade(1).build()
        ));

        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(1L)
                .itens(itens)
                .metodoPagamento("PIX")
                .build();

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Deve passar validação com clienteId grande")
    void deveValidarComClienteIdGrande() {
        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(999999999L)
                .itens(itensValidos())
                .metodoPagamento("BOLETO")
                .build();

        assertTrue(validator.validate(dto).isEmpty());
    }

    // =========================================================================
    // VALIDAÇÃO — cenários inválidos (deve gerar violations)
    // =========================================================================

    @Test
    @DisplayName("Deve falhar quando clienteId for nulo")
    void deveFalharClienteIdNulo() {
        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(null)
                .itens(itensValidos())
                .metodoPagamento("PIX")
                .build();

        Set<ConstraintViolation<PedidoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("clienteId")));
    }

    @Test
    @DisplayName("Deve falhar quando itens for nulo")
    void deveFalharItensNulo() {
        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(1L)
                .itens(null)
                .metodoPagamento("PIX")
                .build();

        Set<ConstraintViolation<PedidoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("itens")));
    }

    @Test
    @DisplayName("Deve falhar quando itens for uma lista vazia")
    void deveFalharItensVazio() {
        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(1L)
                .itens(new ArrayList<>())
                .metodoPagamento("PIX")
                .build();

        Set<ConstraintViolation<PedidoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("itens")));
    }

    @Test
    @DisplayName("Deve falhar quando metodoPagamento for nulo")
    void deveFalharMetodoPagamentoNulo() {
        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(1L)
                .itens(itensValidos())
                .metodoPagamento(null)
                .build();

        Set<ConstraintViolation<PedidoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("metodoPagamento")));
    }

    @Test
    @DisplayName("Deve falhar quando metodoPagamento for string vazia")
    void deveFalharMetodoPagamentoVazio() {
        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(1L)
                .itens(itensValidos())
                .metodoPagamento("")
                .build();

        Set<ConstraintViolation<PedidoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("metodoPagamento")));
    }

    @Test
    @DisplayName("Deve acumular múltiplas violations quando todos os campos obrigatórios forem nulos")
    void deveFalharComTodosCamposNulos() {
        PedidoRequestDTO dto = new PedidoRequestDTO();
        Set<ConstraintViolation<PedidoRequestDTO>> violations = validator.validate(dto);
        // clienteId (@NotNull) + itens (@NotEmpty) + metodoPagamento (@NotEmpty) = 3
        assertEquals(3, violations.size());
    }

    // =========================================================================
    // equals / hashCode
    // =========================================================================

    @Test
    @DisplayName("Dois objetos com mesmos valores devem ser iguais")
    void doisObjetosComMesmosValoresDevemSerIguais() {
        PedidoRequestDTO dto1 = new PedidoRequestDTO(1L, itensValidos(), "PIX");
        PedidoRequestDTO dto2 = new PedidoRequestDTO(1L, itensValidos(), "PIX");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Objeto deve ser igual a si mesmo")
    void objetoDeveSerIgualASiMesmo() {
        PedidoRequestDTO dto = dtoPadrao();
        assertEquals(dto, dto);
    }

    @Test
    @DisplayName("Dois objetos padrão (sem campos) devem ser iguais")
    void doisObjetosPadraoDevemSerIguais() {
        PedidoRequestDTO dto1 = new PedidoRequestDTO();
        PedidoRequestDTO dto2 = new PedidoRequestDTO();
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Objetos com metodoPagamento diferente não devem ser iguais")
    void objetosComMetodoPagamentoDiferenteNaoDevemSerIguais() {
        PedidoRequestDTO dto1 = new PedidoRequestDTO(1L, itensValidos(), "PIX");
        PedidoRequestDTO dto2 = new PedidoRequestDTO(1L, itensValidos(), "BOLETO");

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objeto não deve ser igual a null")
    void objetoNaoDeveSerIgualANull() {
        assertNotEquals(null, dtoPadrao());
    }

    @Test
    @DisplayName("Objeto não deve ser igual a outro tipo")
    void objetoNaoDeveSerIgualAOutroTipo() {
        assertNotEquals("string", dtoPadrao());
    }

    // =========================================================================
    // toString
    // =========================================================================

    @Test
    @DisplayName("toString não deve ser nulo")
    void toStringNaoDeveSerNulo() {
        assertNotNull(new PedidoRequestDTO().toString());
    }

    @Test
    @DisplayName("toString deve conter os valores de todos os campos")
    void toStringDeveConterValores() {
        PedidoRequestDTO dto = new PedidoRequestDTO(1L, itensValidos(), "PIX");
        String str = dto.toString();
        assertTrue(str.contains("1"));
        assertTrue(str.contains("PIX"));
    }
}