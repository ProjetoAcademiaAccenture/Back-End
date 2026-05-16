package acc.br.projetoFinal.Accenture.model;

import acc.br.projetoFinal.Accenture.enums.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Regras de Negócio - Produto")
class ProdutoBusinessRulesTests {

    private Produto produto;

    @BeforeEach
    void setUp() {
        produto = Produto.builder()
                .id(1L)
                .nome("Notebook")
                .descricao("Notebook gamer")
                .preco(new BigDecimal("3500.00"))
                .quantidadeEstoque(10)
                .categoria(Categoria.ELETRONICOS)
                .build();
    }

    // =========================================================
    // temEstoqueSuficiente()
    // =========================================================

    @Test
    @DisplayName("Deve retornar true quando estoque é suficiente")
    void deveRetornarTrueQuandoEstoqueSuficiente() {
        assertTrue(produto.temEstoqueSuficiente(5));
    }

    @Test
    @DisplayName("Deve retornar true quando quantidade é exatamente igual ao estoque")
    void deveRetornarTrueQuandoQuantidadeIgualAoEstoque() {
        assertTrue(produto.temEstoqueSuficiente(10));
    }

    @Test
    @DisplayName("Deve retornar false quando estoque é insuficiente")
    void deveRetornarFalseQuandoEstoqueInsuficiente() {
        assertFalse(produto.temEstoqueSuficiente(11));
    }

    // =========================================================
    // reservarEstoque()
    // =========================================================

    @Test
    @DisplayName("Deve reservar estoque corretamente")
    void deveReservarEstoque() {
        produto.reservarEstoque(3);
        assertEquals(7, produto.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve reservar todo o estoque disponível")
    void deveReservarTodoOEstoque() {
        produto.reservarEstoque(10);
        assertEquals(0, produto.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Não deve reservar quantidade zero")
    void naoDeveReservarQuantidadeZero() {
        assertThrows(IllegalArgumentException.class,
            () -> produto.reservarEstoque(0));
    }

    @Test
    @DisplayName("Não deve reservar quantidade negativa")
    void naoDeveReservarQuantidadeNegativa() {
        assertThrows(IllegalArgumentException.class,
            () -> produto.reservarEstoque(-1));
    }

    @Test
    @DisplayName("Não deve reservar quando estoque insuficiente")
    void naoDeveReservarQuandoEstoqueInsuficiente() {
        assertThrows(IllegalArgumentException.class,
            () -> produto.reservarEstoque(11));
    }

    @Test
    @DisplayName("Não deve alterar estoque ao lançar exceção por insuficiência")
    void naoDeveAlterarEstoqueAoLancarExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> produto.reservarEstoque(20));
        assertEquals(10, produto.getQuantidadeEstoque());
    }

    // =========================================================
    // devolverEstoque()
    // =========================================================

    @Test
    @DisplayName("Deve devolver estoque corretamente")
    void deveDevolverEstoque() {
        produto.devolverEstoque(5);
        assertEquals(15, produto.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve acumular devoluções sucessivas")
    void deveAcumularDevolucoesSucessivas() {
        produto.devolverEstoque(2);
        produto.devolverEstoque(3);
        assertEquals(15, produto.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Não deve devolver quantidade zero")
    void naoDeveDevolverQuantidadeZero() {
        assertThrows(IllegalArgumentException.class,
            () -> produto.devolverEstoque(0));
    }

    @Test
    @DisplayName("Não deve devolver quantidade negativa")
    void naoDeveDevolverQuantidadeNegativa() {
        assertThrows(IllegalArgumentException.class,
            () -> produto.devolverEstoque(-5));
    }

    @Test
    @DisplayName("Não deve alterar estoque ao lançar exceção na devolução")
    void naoDeveAlterarEstoqueAoLancarExcecaoNaDevolucao() {
        assertThrows(IllegalArgumentException.class,
            () -> produto.devolverEstoque(0));
        assertEquals(10, produto.getQuantidadeEstoque());
    }

    // =========================================================
    // ajustarEstoque()
    // =========================================================

    @Test
    @DisplayName("Deve ajustar estoque para novo valor positivo")
    void deveAjustarEstoqueParaValorPositivo() {
        produto.ajustarEstoque(50);
        assertEquals(50, produto.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve ajustar estoque para zero")
    void deveAjustarEstoqueParaZero() {
        produto.ajustarEstoque(0);
        assertEquals(0, produto.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Não deve ajustar estoque para valor negativo")
    void naoDeveAjustarEstoqueParaNegativo() {
        assertThrows(IllegalArgumentException.class,
            () -> produto.ajustarEstoque(-1));
    }

    @Test
    @DisplayName("Não deve alterar estoque ao ajustar com valor negativo")
    void naoDeveAlterarEstoqueAoAjustarComNegativo() {
        assertThrows(IllegalArgumentException.class,
            () -> produto.ajustarEstoque(-10));
        assertEquals(10, produto.getQuantidadeEstoque());
    }
}