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
@DisplayName("ItemPedidoRequestDTO - Testes Positivos")
class ItemPedidoRequestDTOTests {

    @Autowired
    private Validator validator;

    private ItemPedidoRequestDTO dto;

    @Test
    @DisplayName("Deve validar ItemPedidoRequestDTO com todos os dados válidos")
    void deveValidarComDadosValidos() {
        dto = ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(5)
                .build();

        Set<ConstraintViolation<ItemPedidoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve validar ItemPedidoRequestDTO com quantidade mínima")
    void deveValidarComQuantidadeMinima() {
        dto = ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(1)
                .build();

        Set<ConstraintViolation<ItemPedidoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve validar ItemPedidoRequestDTO com quantidade grande")
    void deveValidarComQuantidadeGrande() {
        dto = ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(999999)
                .build();

        Set<ConstraintViolation<ItemPedidoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve validar ItemPedidoRequestDTO com produtoId grande")
    void deveValidarComProdutoIdGrande() {
        dto = ItemPedidoRequestDTO.builder()
                .produtoId(999999999L)
                .quantidade(5)
                .build();

        Set<ConstraintViolation<ItemPedidoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }
}
