// FILE 2: ItemPedidoResponseDTONegativeTests.java
package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.model.ItemPedido;
import acc.br.projetoFinal.Accenture.model.Produto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ItemPedidoResponseDTONegativeTests {

    // ─── fromEntity: entradas nulas ───────────────────────────────────────────

    @Test
    void fromEntity_ShouldThrow_WhenItemIsNull() {
        assertThrows(NullPointerException.class,
            () -> ItemPedidoResponseDTO.fromEntity(null));
    }

    @Test
    void fromEntity_ShouldThrow_WhenProdutoIsNull() {
        ItemPedido item = new ItemPedido();
        item.setId(1L);
        item.setProduto(null);
        item.setQuantidade(2);
        item.setPrecoUnitario(new BigDecimal("10.00"));

        assertThrows(NullPointerException.class,
            () -> ItemPedidoResponseDTO.fromEntity(item));
    }

    @Test
    void fromEntity_ShouldThrow_WhenPrecoUnitarioIsNull() {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto A");

        ItemPedido item = new ItemPedido();
        item.setId(1L);
        item.setProduto(produto);
        item.setQuantidade(2);
        item.setPrecoUnitario(null);

        assertThrows(NullPointerException.class,
            () -> ItemPedidoResponseDTO.fromEntity(item));
    }

    @Test
    void fromEntity_ShouldThrow_WhenQuantidadeIsNull() {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto A");

        ItemPedido item = new ItemPedido();
        item.setId(1L);
        item.setProduto(produto);
        item.setQuantidade(null);
        item.setPrecoUnitario(new BigDecimal("10.00"));

        assertThrows(NullPointerException.class,
            () -> ItemPedidoResponseDTO.fromEntity(item));
    }

    // ─── Subtotal: valores incorretos ─────────────────────────────────────────

    @Test
    void fromEntity_SubtotalShouldNotMatchWrongExpectedValue() {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto A");

        ItemPedido item = new ItemPedido();
        item.setId(1L);
        item.setProduto(produto);
        item.setQuantidade(3);
        item.setPrecoUnitario(new BigDecimal("10.00"));

        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.fromEntity(item);

        assertNotEquals(new BigDecimal("999.00"), dto.getSubtotal());
    }

    @Test
    void fromEntity_SubtotalShouldNotBeZero_WhenPrecoAndQuantidadeArePositive() {
        Produto produto = new Produto();
        produto.setId(2L);
        produto.setNome("Produto B");

        ItemPedido item = new ItemPedido();
        item.setId(2L);
        item.setProduto(produto);
        item.setQuantidade(5);
        item.setPrecoUnitario(new BigDecimal("20.00"));

        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.fromEntity(item);

        assertNotEquals(BigDecimal.ZERO, dto.getSubtotal());
    }

    // ─── Mapeamento: campos incorretos ────────────────────────────────────────

    @Test
    void fromEntity_ShouldNotMapWrongProdutoId() {
        Produto produto = new Produto();
        produto.setId(99L);
        produto.setNome("Produto C");

        ItemPedido item = new ItemPedido();
        item.setId(3L);
        item.setProduto(produto);
        item.setQuantidade(1);
        item.setPrecoUnitario(new BigDecimal("5.00"));

        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.fromEntity(item);

        assertNotEquals(1L, dto.getProdutoId());
    }

    @Test
    void fromEntity_ShouldNotMapWrongProdutoNome() {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Nome Real");

        ItemPedido item = new ItemPedido();
        item.setId(4L);
        item.setProduto(produto);
        item.setQuantidade(1);
        item.setPrecoUnitario(new BigDecimal("5.00"));

        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.fromEntity(item);

        assertNotEquals("Nome Errado", dto.getProdutoNome());
    }

    @Test
    void fromEntity_ShouldNotMapWrongItemId() {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto D");

        ItemPedido item = new ItemPedido();
        item.setId(5L);
        item.setProduto(produto);
        item.setQuantidade(2);
        item.setPrecoUnitario(new BigDecimal("10.00"));

        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.fromEntity(item);

        assertNotEquals(999L, dto.getId());
    }

    // ─── equals: DTOs diferentes ──────────────────────────────────────────────

    @Test
    void equals_ShouldReturnFalse_WhenIdsDiffer() {
        ItemPedidoResponseDTO dto1 = new ItemPedidoResponseDTO(
            1L, 10L, "Produto", 2, new BigDecimal("10.00"), new BigDecimal("20.00"));
        ItemPedidoResponseDTO dto2 = new ItemPedidoResponseDTO(
            2L, 10L, "Produto", 2, new BigDecimal("10.00"), new BigDecimal("20.00"));

        assertNotEquals(dto1, dto2);
    }

    @Test
    void equals_ShouldReturnFalse_WhenProdutoNomeDiffers() {
        ItemPedidoResponseDTO dto1 = new ItemPedidoResponseDTO(
            1L, 10L, "Produto A", 2, new BigDecimal("10.00"), new BigDecimal("20.00"));
        ItemPedidoResponseDTO dto2 = new ItemPedidoResponseDTO(
            1L, 10L, "Produto B", 2, new BigDecimal("10.00"), new BigDecimal("20.00"));

        assertNotEquals(dto1, dto2);
    }

    @Test
    void equals_ShouldReturnFalse_WhenSubtotalDiffers() {
        ItemPedidoResponseDTO dto1 = new ItemPedidoResponseDTO(
            1L, 10L, "Produto", 2, new BigDecimal("10.00"), new BigDecimal("20.00"));
        ItemPedidoResponseDTO dto2 = new ItemPedidoResponseDTO(
            1L, 10L, "Produto", 2, new BigDecimal("10.00"), new BigDecimal("99.00"));

        assertNotEquals(dto1, dto2);
    }

    @Test
    void equals_ShouldReturnFalse_WhenQuantidadeDiffers() {
        ItemPedidoResponseDTO dto1 = new ItemPedidoResponseDTO(
            1L, 10L, "Produto", 2, new BigDecimal("10.00"), new BigDecimal("20.00"));
        ItemPedidoResponseDTO dto2 = new ItemPedidoResponseDTO(
            1L, 10L, "Produto", 5, new BigDecimal("10.00"), new BigDecimal("50.00"));

        assertNotEquals(dto1, dto2);
    }

    @Test
    void equals_ShouldReturnFalse_WhenComparedToNull() {
        ItemPedidoResponseDTO dto = new ItemPedidoResponseDTO(
            1L, 10L, "Produto", 2, new BigDecimal("10.00"), new BigDecimal("20.00"));

        assertNotEquals(null, dto);
    }

    @Test
    void equals_ShouldReturnFalse_WhenComparedToDifferentType() {
        ItemPedidoResponseDTO dto = new ItemPedidoResponseDTO(
            1L, 10L, "Produto", 2, new BigDecimal("10.00"), new BigDecimal("20.00"));

        assertNotEquals("uma string qualquer", dto);
    }

    // ─── hashCode: DTOs diferentes ────────────────────────────────────────────

    @Test
    void hashCode_ShouldDiffer_WhenDTOsDiffer() {
        ItemPedidoResponseDTO dto1 = new ItemPedidoResponseDTO(
            1L, 10L, "Produto A", 2, new BigDecimal("10.00"), new BigDecimal("20.00"));
        ItemPedidoResponseDTO dto2 = new ItemPedidoResponseDTO(
            2L, 20L, "Produto B", 3, new BigDecimal("30.00"), new BigDecimal("90.00"));

        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }

    // ─── toString: não deve conter valores errados ────────────────────────────

    @Test
void toString_ShouldNotContainWrongValues() {
    ItemPedidoResponseDTO dto = new ItemPedidoResponseDTO(
        1L, 10L, "Produto A", 3,
        new BigDecimal("50.00"), new BigDecimal("150.00"));

    String result = dto.toString();

    assertAll(
        () -> assertFalse(result.contains("999")),
        () -> assertFalse(result.contains("Produto Z"))
    );
}
}