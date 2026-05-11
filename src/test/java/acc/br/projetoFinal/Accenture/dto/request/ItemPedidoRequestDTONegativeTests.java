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

    private ItemPedidoRequestDTO dto;

    @Test
    @DisplayName("Deve rejeitar quando produtoId é null")
    void deveRejeitar_ProdutoIdNull() {
        dto = ItemPedidoRequestDTO.builder()
                .produtoId(null)
                .quantidade(5)
                .build();

        Set<ConstraintViolation<ItemPedidoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para produtoId null");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("produtoId")));
    }

    @Test
    @DisplayName("Deve rejeitar quando quantidade é null")
    void deveRejeitar_QuantidadeNull() {
        dto = ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(null)
                .build();

        Set<ConstraintViolation<ItemPedidoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para quantidade null");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quantidade")));
    }

    @Test
    @DisplayName("Deve rejeitar quando quantidade é zero")
    void deveRejeitar_QuantidadeZero() {
        dto = ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(0)
                .build();

        Set<ConstraintViolation<ItemPedidoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para quantidade zero");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quantidade")));
    }

    @Test
    @DisplayName("Deve rejeitar quando quantidade é negativa")
    void deveRejeitar_QuantidadeNegativa() {
        dto = ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(-5)
                .build();

        Set<ConstraintViolation<ItemPedidoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para quantidade negativa");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quantidade")));
    }

    @Test
    @DisplayName("Deve rejeitar quando tanto produtoId quanto quantidade são null")
    void deveRejeitar_AmbosNull() {
        dto = ItemPedidoRequestDTO.builder()
                .produtoId(null)
                .quantidade(null)
                .build();

        Set<ConstraintViolation<ItemPedidoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violações para ambos null");
        assertTrue(violations.size() >= 2, "Deve haver pelo menos 2 violações");
    }
}
