package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.model.ItemPedido;
import acc.br.projetoFinal.Accenture.model.Produto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("ItemPedidoResponseDTO - Testes")
class ItemPedidoResponseDTOTests {

    @Test
    @DisplayName("Deve criar ItemPedidoResponseDTO com construtor padrão")
    void deveCriarComConstrutorPadrao() {
        ItemPedidoResponseDTO dto = new ItemPedidoResponseDTO();
        
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getProdutoId());
        assertNull(dto.getProdutoNome());
        assertNull(dto.getQuantidade());
    }

    @Test
    @DisplayName("Deve criar ItemPedidoResponseDTO com construtor completo")
    void deveCriarComConstrutorCompleto() {
        Long id = 1L;
        Long produtoId = 2L;
        String produtoNome = "Notebook";
        Integer quantidade = 5;
        BigDecimal precoUnitario = new BigDecimal("1000.00");
        BigDecimal subtotal = new BigDecimal("5000.00");

        ItemPedidoResponseDTO dto = new ItemPedidoResponseDTO(id, produtoId, produtoNome, quantidade, precoUnitario, subtotal);

        assertEquals(id, dto.getId());
        assertEquals(produtoId, dto.getProdutoId());
        assertEquals(produtoNome, dto.getProdutoNome());
        assertEquals(quantidade, dto.getQuantidade());
        assertEquals(precoUnitario, dto.getPrecoUnitario());
        assertEquals(subtotal, dto.getSubtotal());
    }

    @Test
    @DisplayName("Deve criar ItemPedidoResponseDTO usando Builder")
    void deveCriarUsandoBuilder() {
        Long id = 1L;
        Long produtoId = 2L;
        String produtoNome = "Teclado Mecânico";
        Integer quantidade = 10;
        BigDecimal precoUnitario = new BigDecimal("250.00");
        BigDecimal subtotal = new BigDecimal("2500.00");

        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.builder()
                .id(id)
                .produtoId(produtoId)
                .produtoNome(produtoNome)
                .quantidade(quantidade)
                .precoUnitario(precoUnitario)
                .subtotal(subtotal)
                .build();

        assertEquals(id, dto.getId());
        assertEquals(produtoId, dto.getProdutoId());
        assertEquals(produtoNome, dto.getProdutoNome());
        assertEquals(quantidade, dto.getQuantidade());
        assertEquals(precoUnitario, dto.getPrecoUnitario());
        assertEquals(subtotal, dto.getSubtotal());
    }

    @Test
    @DisplayName("Deve atualizar campos via setters")
    void deveAtualizarCamposViaSetters() {
        ItemPedidoResponseDTO dto = new ItemPedidoResponseDTO();

        dto.setId(5L);
        dto.setProdutoId(10L);
        dto.setProdutoNome("Mouse");
        dto.setQuantidade(3);
        dto.setPrecoUnitario(new BigDecimal("100.00"));
        dto.setSubtotal(new BigDecimal("300.00"));

        assertEquals(5L, dto.getId());
        assertEquals(10L, dto.getProdutoId());
        assertEquals("Mouse", dto.getProdutoNome());
        assertEquals(3, dto.getQuantidade());
        assertEquals(new BigDecimal("100.00"), dto.getPrecoUnitario());
        assertEquals(new BigDecimal("300.00"), dto.getSubtotal());
    }

    @Test
    @DisplayName("Deve criar ItemPedidoResponseDTO com valores null")
    void deveCriarComValoresNull() {
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
    @DisplayName("Deve converter ItemPedido para ItemPedidoResponseDTO")
    void deveConverterItemPedidoParaDTO() {
        // Criar um produto mock
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Monitor 27\"");

        // Criar um ItemPedido mock
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setId(10L);
        itemPedido.setProduto(produto);
        itemPedido.setQuantidade(2);
        itemPedido.setPrecoUnitario(new BigDecimal("800.00"));

        // Converter para DTO
        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.fromEntity(itemPedido);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals(1L, dto.getProdutoId());
        assertEquals("Monitor 27\"", dto.getProdutoNome());
        assertEquals(2, dto.getQuantidade());
        assertEquals(new BigDecimal("800.00"), dto.getPrecoUnitario());
        assertEquals(new BigDecimal("1600.00"), dto.getSubtotal());
    }

    @Test
    @DisplayName("Deve calcular subtotal corretamente na conversão")
    void deveCalcularSubtotalCorretamente() {
        Produto produto = new Produto();
        produto.setId(5L);
        produto.setNome("Webcam");

        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setId(20L);
        itemPedido.setProduto(produto);
        itemPedido.setQuantidade(4);
        itemPedido.setPrecoUnitario(new BigDecimal("150.50"));

        ItemPedidoResponseDTO dto = ItemPedidoResponseDTO.fromEntity(itemPedido);

        BigDecimal subtotalEsperado = new BigDecimal("150.50").multiply(BigDecimal.valueOf(4));
        assertEquals(subtotalEsperado, dto.getSubtotal());
    }

    @Test
    @DisplayName("Deve testar igualdade entre DTOs com mesmo conteúdo")
    void deveTestarIgualdadeEntreDTOs() {
        ItemPedidoResponseDTO dto1 = ItemPedidoResponseDTO.builder()
                .id(1L)
                .produtoId(2L)
                .produtoNome("Headset")
                .quantidade(1)
                .precoUnitario(new BigDecimal("200.00"))
                .subtotal(new BigDecimal("200.00"))
                .build();

        ItemPedidoResponseDTO dto2 = ItemPedidoResponseDTO.builder()
                .id(1L)
                .produtoId(2L)
                .produtoNome("Headset")
                .quantidade(1)
                .precoUnitario(new BigDecimal("200.00"))
                .subtotal(new BigDecimal("200.00"))
                .build();

        assertEquals(dto1, dto2);
    }
}
