package acc.br.projetoFinal.Accenture.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("ItemPedido - Testes unitários")
class ItemPedidoTest {

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /**
     * Data fixa usada em todos os Pedidos de teste.
     *
     * O modelo Pedido possui dataCriacao preenchida automaticamente com
     * LocalDateTime.now() (via inicializador de campo ou @PrePersist).
     * Dois objetos Pedido criados em instantes diferentes nunca seriam iguais
     * pelo equals gerado pelo Lombok. Fixar a data resolve o problema.
     */
    private static final LocalDateTime DATA_FIXA =
            LocalDateTime.of(2024, 1, 1, 0, 0, 0);

    private Pedido pedidoFake() {
        Pedido p = new Pedido();
        p.setId(1L);
        p.setDataCriacao(DATA_FIXA);   // <-- garante igualdade entre chamadas
        return p;
    }

    private Produto produtoFake() {
        Produto pr = new Produto();
        pr.setId(10L);
        return pr;
    }

    private ItemPedido itemCompleto() {
        return ItemPedido.builder()
                .id(1L)
                .pedido(pedidoFake())
                .produto(produtoFake())
                .quantidade(3)
                .precoUnitario(new BigDecimal("49.99"))
                .build();
    }

    // -----------------------------------------------------------------------
    // 1. Construtores
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Construtores")
    class Construtores {

        @Test
        @DisplayName("NoArgsConstructor deve criar objeto com todos os campos nulos")
        void noArgsConstructorDeveCriarObjetoVazio() {
            ItemPedido item = new ItemPedido();

            assertAll("noArgs",
                    () -> assertNull(item.getId()),
                    () -> assertNull(item.getPedido()),
                    () -> assertNull(item.getProduto()),
                    () -> assertNull(item.getQuantidade()),
                    () -> assertNull(item.getPrecoUnitario())
            );
        }

        @Test
        @DisplayName("AllArgsConstructor deve preencher todos os campos na ordem correta")
        void allArgsConstructorDevePreencherTodosOsCampos() {
            Pedido pedido    = pedidoFake();
            Produto produto  = produtoFake();
            BigDecimal preco = new BigDecimal("99.90");

            ItemPedido item = new ItemPedido(2L, pedido, produto, 5, preco);

            assertAll("allArgs",
                    () -> assertEquals(2L,     item.getId()),
                    () -> assertEquals(pedido,  item.getPedido()),
                    () -> assertEquals(produto, item.getProduto()),
                    () -> assertEquals(5,       item.getQuantidade()),
                    () -> assertEquals(preco,   item.getPrecoUnitario())
            );
        }

        @Test
        @DisplayName("Builder deve construir o objeto corretamente")
        void builderDeveConstruirCorretamente() {
            ItemPedido item = itemCompleto();

            assertAll("builder",
                    () -> assertEquals(1L,                      item.getId()),
                    () -> assertNotNull(item.getPedido()),
                    () -> assertNotNull(item.getProduto()),
                    () -> assertEquals(3,                       item.getQuantidade()),
                    () -> assertEquals(new BigDecimal("49.99"), item.getPrecoUnitario())
            );
        }

        @Test
        @DisplayName("Builder deve aceitar campos nulos sem lançar exceção — cenário negativo")
        void builderDeveAceitarCamposNulos() {
            assertDoesNotThrow(() -> {
                ItemPedido item = ItemPedido.builder().build();
                assertNull(item.getId());
                assertNull(item.getPedido());
                assertNull(item.getProduto());
                assertNull(item.getQuantidade());
                assertNull(item.getPrecoUnitario());
            });
        }
    }

    // -----------------------------------------------------------------------
    // 2. Getters e Setters
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Getters e Setters")
    class GettersSetters {

        @Test
        @DisplayName("Deve atualizar id via setter")
        void deveAtualizarId() {
            ItemPedido item = new ItemPedido();
            item.setId(99L);
            assertEquals(99L, item.getId());
        }

        @Test
        @DisplayName("Deve atualizar pedido via setter")
        void deveAtualizarPedido() {
            ItemPedido item = new ItemPedido();
            Pedido pedido   = pedidoFake();
            item.setPedido(pedido);
            assertEquals(pedido, item.getPedido());
        }

        @Test
        @DisplayName("Deve aceitar pedido nulo via setter — cenário negativo")
        void deveAceitarPedidoNulo() {
            ItemPedido item = itemCompleto();
            item.setPedido(null);
            assertNull(item.getPedido());
        }

        @Test
        @DisplayName("Deve atualizar produto via setter")
        void deveAtualizarProduto() {
            ItemPedido item = new ItemPedido();
            Produto produto = produtoFake();
            item.setProduto(produto);
            assertEquals(produto, item.getProduto());
        }

        @Test
        @DisplayName("Deve aceitar produto nulo via setter — cenário negativo")
        void deveAceitarProdutoNulo() {
            ItemPedido item = itemCompleto();
            item.setProduto(null);
            assertNull(item.getProduto());
        }

        @Test
        @DisplayName("Deve atualizar quantidade via setter")
        void deveAtualizarQuantidade() {
            ItemPedido item = new ItemPedido();
            item.setQuantidade(10);
            assertEquals(10, item.getQuantidade());
        }

        @Test
        @DisplayName("Deve aceitar quantidade zero — cenário de borda negativo")
        void deveAceitarQuantidadeZero() {
            ItemPedido item = new ItemPedido();
            item.setQuantidade(0);
            assertEquals(0, item.getQuantidade());
        }

        @Test
        @DisplayName("Deve aceitar quantidade negativa — cenário negativo")
        void deveAceitarQuantidadeNegativa() {
            ItemPedido item = new ItemPedido();
            item.setQuantidade(-1);
            assertEquals(-1, item.getQuantidade());
        }

        @Test
        @DisplayName("Deve atualizar precoUnitario via setter")
        void deveAtualizarPrecoUnitario() {
            ItemPedido item  = new ItemPedido();
            BigDecimal preco = new BigDecimal("199.99");
            item.setPrecoUnitario(preco);
            assertEquals(preco, item.getPrecoUnitario());
        }

        @Test
        @DisplayName("Deve aceitar precoUnitario zero — cenário de borda")
        void deveAceitarPrecoZero() {
            ItemPedido item = new ItemPedido();
            item.setPrecoUnitario(BigDecimal.ZERO);
            assertEquals(BigDecimal.ZERO, item.getPrecoUnitario());
        }

        @Test
        @DisplayName("Deve aceitar precoUnitario negativo — cenário negativo")
        void deveAceitarPrecoNegativo() {
            ItemPedido item = new ItemPedido();
            item.setPrecoUnitario(new BigDecimal("-10.00"));
            assertEquals(new BigDecimal("-10.00"), item.getPrecoUnitario());
        }

        @Test
        @DisplayName("Deve aceitar precoUnitario nulo via setter — cenário negativo")
        void deveAceitarPrecoNulo() {
            ItemPedido item = itemCompleto();
            item.setPrecoUnitario(null);
            assertNull(item.getPrecoUnitario());
        }
    }

    // -----------------------------------------------------------------------
    // 3. equals() e hashCode()
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("equals e hashCode")
    class EqualsHashCode {

        @Test
        @DisplayName("Dois itens com mesmos valores devem ser iguais")
        void doisItensIguaisDevemSerIguais() {
            ItemPedido a = itemCompleto();
            ItemPedido b = itemCompleto();
            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
        }

        @Test
        @DisplayName("Item deve ser igual a si mesmo")
        void deveSerIgualASiMesmo() {
            ItemPedido item = itemCompleto();
            assertEquals(item, item);
        }

        @Test
        @DisplayName("Item não deve ser igual a null — cenário negativo")
        void naoDeveSerIgualANull() {
            ItemPedido item = itemCompleto();
            assertNotEquals(null, item);
        }

        @Test
        @DisplayName("Item não deve ser igual a outro tipo — cenário negativo")
        void naoDeveSerIgualAOutroTipo() {
            ItemPedido item = itemCompleto();
            assertNotEquals("string", item);
        }

        @Test
        @DisplayName("Itens com id diferente não devem ser iguais — cenário negativo")
        void itensComIdDiferenteNaoDevemSerIguais() {
            ItemPedido a = itemCompleto();
            ItemPedido b = itemCompleto();
            b.setId(999L);
            assertNotEquals(a, b);
        }

        @Test
        @DisplayName("Itens com quantidade diferente não devem ser iguais — cenário negativo")
        void itensComQuantidadeDiferenteNaoDevemSerIguais() {
            ItemPedido a = itemCompleto();
            ItemPedido b = itemCompleto();
            b.setQuantidade(100);
            assertNotEquals(a, b);
        }

        @Test
        @DisplayName("Itens com precoUnitario diferente não devem ser iguais — cenário negativo")
        void itensComPrecoDiferenteNaoDevemSerIguais() {
            ItemPedido a = itemCompleto();
            ItemPedido b = itemCompleto();
            b.setPrecoUnitario(new BigDecimal("0.01"));
            assertNotEquals(a, b);
        }

        @Test
        @DisplayName("hashCode deve diferir quando id é diferente")
        void hashCodeDeveDiferirComIdDiferente() {
            ItemPedido a = itemCompleto();
            ItemPedido b = itemCompleto();
            b.setId(777L);
            assertNotEquals(a.hashCode(), b.hashCode());
        }
    }

    // -----------------------------------------------------------------------
    // 4. toString()
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("toString")
    class ToStringTests {

        @Test
        @DisplayName("toString não deve retornar nulo")
        void toStringNaoDeveRetornarNulo() {
            assertNotNull(itemCompleto().toString());
        }

        @Test
        @DisplayName("toString deve conter os valores dos campos principais")
        void toStringDeveConterValoresDoCampos() {
            ItemPedido item = itemCompleto();
            String result   = item.toString();

            assertAll("toString campos",
                    () -> assertTrue(result.contains("1")),
                    () -> assertTrue(result.contains("3")),
                    () -> assertTrue(result.contains("49.99"))
            );
        }

        @Test
        @DisplayName("toString de item vazio não deve lançar exceção — cenário negativo")
        void toStringItemVazioNaoDeveLancarExcecao() {
            assertDoesNotThrow(() -> {
                String s = new ItemPedido().toString();
                assertNotNull(s);
            });
        }
    }

    // -----------------------------------------------------------------------
    // 5. Builder — toString e sobrescrita
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Builder")
    class BuilderTests {

        @Test
        @DisplayName("Builder.toString() não deve lançar exceção")
        void builderToStringNaoDeveLancarExcecao() {
            assertDoesNotThrow(() -> {
                String s = ItemPedido.builder()
                        .id(1L)
                        .quantidade(2)
                        .precoUnitario(BigDecimal.TEN)
                        .toString();
                assertNotNull(s);
            });
        }

        @Test
        @DisplayName("Builder deve sobrescrever o último valor atribuído ao mesmo campo")
        void builderDeveSobrescreverValor() {
            ItemPedido item = ItemPedido.builder()
                    .quantidade(1)
                    .quantidade(99)
                    .build();
            assertEquals(99, item.getQuantidade());
        }
    }

    // -----------------------------------------------------------------------
    // 6. Casos de borda
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Casos de borda")
    class CasosDeBorda {

        @Test
        @DisplayName("Deve aceitar precoUnitario com valor muito alto")
        void deveAceitarPrecisaoAlta() {
            BigDecimal preco = new BigDecimal("9999999.99");
            ItemPedido item  = ItemPedido.builder()
                    .precoUnitario(preco)
                    .build();
            assertEquals(preco, item.getPrecoUnitario());
        }

        @Test
        @DisplayName("Deve aceitar quantidade Integer.MAX_VALUE")
        void deveAceitarQuantidadeMaxima() {
            ItemPedido item = ItemPedido.builder()
                    .quantidade(Integer.MAX_VALUE)
                    .build();
            assertEquals(Integer.MAX_VALUE, item.getQuantidade());
        }

        @Test
        @DisplayName("Deve aceitar id Long.MAX_VALUE")
        void deveAceitarIdMaximo() {
            ItemPedido item = ItemPedido.builder()
                    .id(Long.MAX_VALUE)
                    .build();
            assertEquals(Long.MAX_VALUE, item.getId());
        }

        @Test
        @DisplayName("Dois itens sem id (nulo) e mesmos dados devem ser iguais")
        void doisItensSemIdDevemSerIguais() {
            ItemPedido a = ItemPedido.builder()
                    .quantidade(1)
                    .precoUnitario(BigDecimal.ONE)
                    .build();
            ItemPedido b = ItemPedido.builder()
                    .quantidade(1)
                    .precoUnitario(BigDecimal.ONE)
                    .build();
            assertEquals(a, b);
        }
    }
}