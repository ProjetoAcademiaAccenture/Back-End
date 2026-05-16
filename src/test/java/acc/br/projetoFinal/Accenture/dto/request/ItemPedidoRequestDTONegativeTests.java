package acc.br.projetoFinal.Accenture.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("ItemPedidoRequestDTO - Testes Negativos")
class ItemPedidoRequestDTONegativeTests {

    @Autowired
    private Validator validator;

    // -------------------------------------------------------------------------
    // Validação — @NotNull produtoId
    // -------------------------------------------------------------------------

 

    @Test
    @DisplayName("Mensagem de violação de produtoId deve ser correta")
    void deveTerMensagemCorretaParaProdutoIdNull() {
        ItemPedidoRequestDTO dto = ItemPedidoRequestDTO.builder()
                .produtoId(null)
                .quantidade(5)
                .build();

        Set<ConstraintViolation<ItemPedidoRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("produtoId")
                        && v.getMessage().equals("ID do produto é obrigatório")));
    }

    // -------------------------------------------------------------------------
    // Validação — @NotNull quantidade
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve rejeitar quando quantidade é null")
    void deveRejeitar_QuantidadeNull() {
        ItemPedidoRequestDTO dto = ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(null)
                .build();

        Set<ConstraintViolation<ItemPedidoRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("quantidade")));
    }

    @Test
    @DisplayName("Mensagem de violação de quantidade null deve ser correta")
    void deveTerMensagemCorretaParaQuantidadeNull() {
        ItemPedidoRequestDTO dto = ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(null)
                .build();

        Set<ConstraintViolation<ItemPedidoRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("quantidade")
                        && v.getMessage().equals("Quantidade é obrigatória")));
    }

    // -------------------------------------------------------------------------
    // Validação — @Min(1) quantidade
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve rejeitar quando quantidade é zero")
    void deveRejeitar_QuantidadeZero() {
        ItemPedidoRequestDTO dto = ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(0)
                .build();

        Set<ConstraintViolation<ItemPedidoRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("quantidade")));
    }

    @Test
    @DisplayName("Deve rejeitar quando quantidade é negativa")
    void deveRejeitar_QuantidadeNegativa() {
        ItemPedidoRequestDTO dto = ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(-5)
                .build();

        Set<ConstraintViolation<ItemPedidoRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("quantidade")));
    }

    @Test
    @DisplayName("Deve rejeitar quando quantidade é Integer.MIN_VALUE")
    void deveRejeitar_QuantidadeMinValue() {
        ItemPedidoRequestDTO dto = ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(Integer.MIN_VALUE)
                .build();

        Set<ConstraintViolation<ItemPedidoRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("quantidade")));
    }

    @Test
    @DisplayName("Mensagem de violação de @Min deve ser correta")
    void deveTerMensagemCorretaParaQuantidadeMin() {
        ItemPedidoRequestDTO dto = ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(0)
                .build();

        Set<ConstraintViolation<ItemPedidoRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("quantidade")
                        && v.getMessage().equals("Quantidade deve ser no mínimo 1")));
    }

    // -------------------------------------------------------------------------
    // Validação — ambos inválidos
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve rejeitar quando produtoId e quantidade são null")
    void deveRejeitar_AmbosNull() {
        ItemPedidoRequestDTO dto = ItemPedidoRequestDTO.builder()
                .produtoId(null)
                .quantidade(null)
                .build();

        Set<ConstraintViolation<ItemPedidoRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 2);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("produtoId")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quantidade")));
    }

    @Test
    @DisplayName("Deve rejeitar quando produtoId é null e quantidade é negativa")
    void deveRejeitar_ProdutoIdNullEQuantidadeNegativa() {
        ItemPedidoRequestDTO dto = ItemPedidoRequestDTO.builder()
                .produtoId(null)
                .quantidade(-1)
                .build();

        Set<ConstraintViolation<ItemPedidoRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 2);
    }

    // -------------------------------------------------------------------------
    // equals / hashCode — objetos diferentes
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Objetos com produtoId diferentes não devem ser iguais")
    void objetosComProdutoIdDiferentesNaoDevemSerIguais() {
        ItemPedidoRequestDTO dto1 = new ItemPedidoRequestDTO(1L, 5);
        ItemPedidoRequestDTO dto2 = new ItemPedidoRequestDTO(2L, 5);
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objetos com quantidade diferentes não devem ser iguais")
    void objetosComQuantidadeDiferentesNaoDevemSerIguais() {
        ItemPedidoRequestDTO dto1 = new ItemPedidoRequestDTO(1L, 5);
        ItemPedidoRequestDTO dto2 = new ItemPedidoRequestDTO(1L, 10);
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objeto não deve ser igual a null")
    void objetoNaoDeveSerIgualANull() {
        ItemPedidoRequestDTO dto = new ItemPedidoRequestDTO(1L, 5);
        assertNotEquals(null, dto);
    }

    @Test
    @DisplayName("Objeto não deve ser igual a tipo diferente")
    void objetoNaoDeveSerIgualATipoDiferente() {
        ItemPedidoRequestDTO dto = new ItemPedidoRequestDTO(1L, 5);
        assertNotEquals("string", dto);
    }

    // -------------------------------------------------------------------------
    // Setters — atribuição de nulos
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve aceitar null nos setters")
    void deveAceitarNullNosSetters() {
        ItemPedidoRequestDTO dto = new ItemPedidoRequestDTO(1L, 5);

        dto.setProdutoId(null);
        dto.setQuantidade(null);

        assertNull(dto.getProdutoId());
        assertNull(dto.getQuantidade());
    }
}