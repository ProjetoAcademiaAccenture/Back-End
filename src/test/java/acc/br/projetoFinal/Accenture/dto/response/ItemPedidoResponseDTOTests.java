// FILE 1: ItemPedidoResponseDTOPositiveTests.java
package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.model.ItemPedido;
import acc.br.projetoFinal.Accenture.model.Produto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ItemPedidoResponseDTOPositiveTests {

    // ─── Helper ───────────────────────────────────────────────────────────────

    private ItemPedido buildItem(Long id, Long produtoId, String nome,
                                  Integer quantidade, BigDecimal preco) {
        Produto produto = new Produto();
        produto.setId(produtoId);
        produto.setNome(nome);

        ItemPedido item = new ItemPedido();
        item.setId(id);
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(preco);
        return item;
    }

    // ─── fromEntity ───────────────────────────────────────────────────────────

    @Test
    void fromEntity_ShouldMapAllFieldsCorrectly() {
        ItemPedido item = buildItem(1L, 10L, "Produto A", 3, new BigDecimal("50.00"));

        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.fromEntity(item);

        assertAll(
            () -> assertEquals(1L,                       dto.getId()),
            () -> assertEquals(10L,                      dto.getProdutoId()),
            () -> assertEquals("Produto A",              dto.getProdutoNome()),
            () -> assertEquals(3,                        dto.getQuantidade()),
            () -> assertEquals(new BigDecimal("50.00"),  dto.getPrecoUnitario()),
            () -> assertEquals(new BigDecimal("150.00"), dto.getSubtotal())
        );
    }

    @Test
    void fromEntity_ShouldCalculateSubtotalCorrectly() {
        ItemPedido item = buildItem(2L, 20L, "Produto B", 5, new BigDecimal("19.99"));

        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.fromEntity(item);

        assertEquals(new BigDecimal("99.95"), dto.getSubtotal());
    }

    @Test
    void fromEntity_ShouldHandleQuantityOfOne() {
        ItemPedido item = buildItem(3L, 30L, "Produto C", 1, new BigDecimal("100.00"));

        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.fromEntity(item);

        assertAll(
            () -> assertEquals(new BigDecimal("100.00"), dto.getSubtotal()),
            () -> assertEquals(1,                        dto.getQuantidade())
        );
    }

    @Test
    void fromEntity_ShouldHandleLargeQuantity() {
        ItemPedido item = buildItem(4L, 40L, "Produto D", 1000, new BigDecimal("9.99"));

        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.fromEntity(item);

        assertEquals(new BigDecimal("9990.00"), dto.getSubtotal());
    }

    @Test
    void fromEntity_ShouldHandleDecimalPreco() {
        ItemPedido item = buildItem(5L, 50L, "Produto E", 3, new BigDecimal("10.555"));

        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.fromEntity(item);

        assertEquals(new BigDecimal("31.665"), dto.getSubtotal());
    }

    // ─── NoArgsConstructor ────────────────────────────────────────────────────

    @Test
    void noArgsConstructor_ShouldCreateEmptyDTO() {
        ItemPedidoResponseDTO dto = new ItemPedidoResponseDTO();

        assertAll(
            () -> assertNull(dto.getId()),
            () -> assertNull(dto.getProdutoId()),
            () -> assertNull(dto.getProdutoNome()),
            () -> assertNull(dto.getQuantidade()),
            () -> assertNull(dto.getPrecoUnitario()),
            () -> assertNull(dto.getSubtotal())
        );
    }

    // ─── AllArgsConstructor ───────────────────────────────────────────────────

    @Test
    void allArgsConstructor_ShouldCreateDTOWithAllFields() {
        ItemPedidoResponseDTO dto = new ItemPedidoResponseDTO(
            1L, 10L, "Produto A", 3,
            new BigDecimal("50.00"), new BigDecimal("150.00")
        );

        assertAll(
            () -> assertEquals(1L,                       dto.getId()),
            () -> assertEquals(10L,                      dto.getProdutoId()),
            () -> assertEquals("Produto A",              dto.getProdutoNome()),
            () -> assertEquals(3,                        dto.getQuantidade()),
            () -> assertEquals(new BigDecimal("50.00"),  dto.getPrecoUnitario()),
            () -> assertEquals(new BigDecimal("150.00"), dto.getSubtotal())
        );
    }

    // ─── Builder ──────────────────────────────────────────────────────────────

    @Test
    void builder_ShouldCreateDTOWithAllFields() {
        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.builder()
            .id(2L)
            .produtoId(20L)
            .produtoNome("Produto B")
            .quantidade(4)
            .precoUnitario(new BigDecimal("25.00"))
            .subtotal(new BigDecimal("100.00"))
            .build();

        assertAll(
            () -> assertEquals(2L,                       dto.getId()),
            () -> assertEquals(20L,                      dto.getProdutoId()),
            () -> assertEquals("Produto B",              dto.getProdutoNome()),
            () -> assertEquals(4,                        dto.getQuantidade()),
            () -> assertEquals(new BigDecimal("25.00"),  dto.getPrecoUnitario()),
            () -> assertEquals(new BigDecimal("100.00"), dto.getSubtotal())
        );
    }

    @Test
    void builder_ShouldCreateDTOWithPartialFields() {
        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.builder()
            .id(3L)
            .produtoNome("Produto C")
            .build();

        assertAll(
            () -> assertEquals(3L,          dto.getId()),
            () -> assertEquals("Produto C", dto.getProdutoNome()),
            () -> assertNull(dto.getProdutoId()),
            () -> assertNull(dto.getQuantidade()),
            () -> assertNull(dto.getPrecoUnitario()),
            () -> assertNull(dto.getSubtotal())
        );
    }

    // ─── Setters (@Data) ──────────────────────────────────────────────────────

    @Test
    void setters_ShouldUpdateAllFieldsCorrectly() {
        ItemPedidoResponseDTO dto = new ItemPedidoResponseDTO();

        dto.setId(9L);
        dto.setProdutoId(90L);
        dto.setProdutoNome("Produto Z");
        dto.setQuantidade(7);
        dto.setPrecoUnitario(new BigDecimal("77.00"));
        dto.setSubtotal(new BigDecimal("539.00"));

        assertAll(
            () -> assertEquals(9L,                       dto.getId()),
            () -> assertEquals(90L,                      dto.getProdutoId()),
            () -> assertEquals("Produto Z",              dto.getProdutoNome()),
            () -> assertEquals(7,                        dto.getQuantidade()),
            () -> assertEquals(new BigDecimal("77.00"),  dto.getPrecoUnitario()),
            () -> assertEquals(new BigDecimal("539.00"), dto.getSubtotal())
        );
    }

    // ─── equals / hashCode ────────────────────────────────────────────────────

    @Test
    void equals_ShouldReturnTrue_ForEqualDTOs() {
        ItemPedidoResponseDTO dto1 = new ItemPedidoResponseDTO(
            1L, 10L, "P", 2, BigDecimal.TEN, BigDecimal.TEN);
        ItemPedidoResponseDTO dto2 = new ItemPedidoResponseDTO(
            1L, 10L, "P", 2, BigDecimal.TEN, BigDecimal.TEN);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void equals_ShouldReturnTrue_WithItself() {
        ItemPedidoResponseDTO dto = new ItemPedidoResponseDTO(
            1L, 10L, "P", 2, BigDecimal.TEN, BigDecimal.TEN);

        assertEquals(dto, dto);
    }

    // ─── toString ─────────────────────────────────────────────────────────────

    @Test
    void toString_ShouldContainAllFieldValues() {
        ItemPedidoResponseDTO dto = new ItemPedidoResponseDTO(
            1L, 10L, "Produto A", 3,
            new BigDecimal("50.00"), new BigDecimal("150.00"));

        String result = dto.toString();

        assertAll(
            () -> assertTrue(result.contains("1")),
            () -> assertTrue(result.contains("Produto A")),
            () -> assertTrue(result.contains("3")),
            () -> assertTrue(result.contains("50.00")),
            () -> assertTrue(result.contains("150.00"))
        );
    }
}