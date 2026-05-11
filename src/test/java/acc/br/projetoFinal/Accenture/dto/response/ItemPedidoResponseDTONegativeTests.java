package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.model.ItemPedido;
import acc.br.projetoFinal.Accenture.model.Produto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("ItemPedidoResponseDTO - Testes Negativos")
class ItemPedidoResponseDTONegativeTests {

    @Test
    @DisplayName("Deve criar ItemPedidoResponseDTO com todos os valores null")
    void deveCriarComTodosValoresNull() {
        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.builder()
                .id(null)
                .produtoId(null)
                .produtoNome(null)
                .quantidade(null)
                .precoUnitario(null)
                .subtotal(null)
                .build();

        assertNull(dto.getId());
        assertNull(dto.getProdutoId());
        assertNull(dto.getProdutoNome());
        assertNull(dto.getQuantidade());
        assertNull(dto.getPrecoUnitario());
        assertNull(dto.getSubtotal());
    }

    @Test
    @DisplayName("Deve criar ItemPedidoResponseDTO com id null")
    void deveCriarComIdNull() {
        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.builder()
                .id(null)
                .produtoId(1L)
                .produtoNome("Produto")
                .quantidade(5)
                .precoUnitario(new BigDecimal("100.00"))
                .subtotal(new BigDecimal("500.00"))
                .build();

        assertNull(dto.getId());
        assertNotNull(dto.getProdutoId());
    }

    @Test
    @DisplayName("Deve criar ItemPedidoResponseDTO com produtoId null")
    void deveCriarComProdutoIdNull() {
        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.builder()
                .id(1L)
                .produtoId(null)
                .produtoNome("Produto")
                .quantidade(5)
                .precoUnitario(new BigDecimal("100.00"))
                .subtotal(new BigDecimal("500.00"))
                .build();

        assertNotNull(dto.getId());
        assertNull(dto.getProdutoId());
    }

    @Test
    @DisplayName("Deve criar ItemPedidoResponseDTO com produtoNome null")
    void deveCriarComProdutoNomeNull() {
        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.builder()
                .id(1L)
                .produtoId(2L)
                .produtoNome(null)
                .quantidade(5)
                .precoUnitario(new BigDecimal("100.00"))
                .subtotal(new BigDecimal("500.00"))
                .build();

        assertNull(dto.getProdutoNome());
    }

    @Test
    @DisplayName("Deve criar ItemPedidoResponseDTO com quantidade null")
    void deveCriarComQuantidadeNull() {
        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.builder()
                .id(1L)
                .produtoId(2L)
                .produtoNome("Produto")
                .quantidade(null)
                .precoUnitario(new BigDecimal("100.00"))
                .subtotal(new BigDecimal("500.00"))
                .build();

        assertNull(dto.getQuantidade());
    }

    @Test
    @DisplayName("Deve criar ItemPedidoResponseDTO com preço unitário null")
    void deveCriarComPrecoUnitarioNull() {
        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.builder()
                .id(1L)
                .produtoId(2L)
                .produtoNome("Produto")
                .quantidade(5)
                .precoUnitario(null)
                .subtotal(new BigDecimal("500.00"))
                .build();

        assertNull(dto.getPrecoUnitario());
    }

    @Test
    @DisplayName("Deve criar ItemPedidoResponseDTO com subtotal null")
    void deveCriarComSubtotalNull() {
        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.builder()
                .id(1L)
                .produtoId(2L)
                .produtoNome("Produto")
                .quantidade(5)
                .precoUnitario(new BigDecimal("100.00"))
                .subtotal(null)
                .build();

        assertNull(dto.getSubtotal());
    }

    @Test
    @DisplayName("Deve criar ItemPedidoResponseDTO com quantidade zero")
    void deveCriarComQuantidadeZero() {
        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.builder()
                .id(1L)
                .produtoId(2L)
                .produtoNome("Produto")
                .quantidade(0)
                .precoUnitario(new BigDecimal("100.00"))
                .subtotal(BigDecimal.ZERO)
                .build();

        assertEquals(0, dto.getQuantidade());
    }

    @Test
    @DisplayName("Deve criar ItemPedidoResponseDTO com quantidade negativa")
    void deveCriarComQuantidadeNegativa() {
        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.builder()
                .id(1L)
                .produtoId(2L)
                .produtoNome("Produto")
                .quantidade(-5)
                .precoUnitario(new BigDecimal("100.00"))
                .subtotal(new BigDecimal("-500.00"))
                .build();

        assertTrue(dto.getQuantidade() < 0);
    }

    @Test
    @DisplayName("Deve criar ItemPedidoResponseDTO com preço negativo")
    void deveCriarComPrecoNegativo() {
        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.builder()
                .id(1L)
                .produtoId(2L)
                .produtoNome("Produto")
                .quantidade(5)
                .precoUnitario(new BigDecimal("-50.00"))
                .subtotal(new BigDecimal("-250.00"))
                .build();

        assertTrue(dto.getPrecoUnitario().signum() < 0);
    }

    @Test
    @DisplayName("Deve criar ItemPedidoResponseDTO com subtotal incorreto")
    void deveCriarComSubtotalIncorreto() {
        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.builder()
                .id(1L)
                .produtoId(2L)
                .produtoNome("Produto")
                .quantidade(5)
                .precoUnitario(new BigDecimal("100.00"))
                .subtotal(new BigDecimal("999.99"))
                .build();

        // O subtotal está incorreto, mas o DTO ainda é criado
        assertNotEquals(new BigDecimal("500.00"), dto.getSubtotal());
        assertEquals(new BigDecimal("999.99"), dto.getSubtotal());
    }

    @Test
    @DisplayName("Deve criar ItemPedidoResponseDTO com id negativo")
    void deveCriarComIdNegativo() {
        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.builder()
                .id(-1L)
                .produtoId(2L)
                .produtoNome("Produto")
                .quantidade(5)
                .precoUnitario(new BigDecimal("100.00"))
                .subtotal(new BigDecimal("500.00"))
                .build();

        assertTrue(dto.getId() < 0);
    }

    @Test
    @DisplayName("Deve criar ItemPedidoResponseDTO com produtoId negativo")
    void deveCriarComProdutoIdNegativo() {
        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.builder()
                .id(1L)
                .produtoId(-5L)
                .produtoNome("Produto")
                .quantidade(5)
                .precoUnitario(new BigDecimal("100.00"))
                .subtotal(new BigDecimal("500.00"))
                .build();

        assertTrue(dto.getProdutoId() < 0);
    }

    @Test
    @DisplayName("Deve criar ItemPedidoResponseDTO com nome vazio")
    void deveCriarComNomeVazio() {
        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.builder()
                .id(1L)
                .produtoId(2L)
                .produtoNome("")
                .quantidade(5)
                .precoUnitario(new BigDecimal("100.00"))
                .subtotal(new BigDecimal("500.00"))
                .build();

        assertEquals("", dto.getProdutoNome());
    }

    @Test
    @DisplayName("Deve manter valores inconsistentes entre subtotal e cálculo")
    void deveManterValoresInconsistentes() {
        BigDecimal precoUnitario = new BigDecimal("100.00");
        Integer quantidade = 5;
        BigDecimal subtotalIncorreto = new BigDecimal("600.00"); // Deveria ser 500.00

        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.builder()
                .id(1L)
                .produtoId(2L)
                .produtoNome("Produto")
                .quantidade(quantidade)
                .precoUnitario(precoUnitario)
                .subtotal(subtotalIncorreto)
                .build();

        BigDecimal subtotalCorreto = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
        assertNotEquals(subtotalCorreto, dto.getSubtotal());
    }

    @Test
    @DisplayName("Deve aceitar quantidade muito grande")
    void deveAceitarQuantidadeMuitoGrande() {
        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.builder()
                .id(1L)
                .produtoId(2L)
                .produtoNome("Produto")
                .quantidade(Integer.MAX_VALUE)
                .precoUnitario(new BigDecimal("0.01"))
                .subtotal(new BigDecimal("21474836.47"))
                .build();

        assertEquals(Integer.MAX_VALUE, dto.getQuantidade());
    }

    @Test
    @DisplayName("Deve aceitar preço muito grande")
    void deveAceitarPrecoMuitoGrande() {
        BigDecimal precoGrande = new BigDecimal("999999999.99");
        
        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.builder()
                .id(1L)
                .produtoId(2L)
                .produtoNome("Produto")
                .quantidade(1)
                .precoUnitario(precoGrande)
                .subtotal(precoGrande)
                .build();

        assertEquals(precoGrande, dto.getPrecoUnitario());
    }
}
