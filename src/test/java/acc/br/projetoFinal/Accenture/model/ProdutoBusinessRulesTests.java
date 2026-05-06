package acc.br.projetoFinal.Accenture.model;

import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
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
                .nome("Produto Teste")
                .descricao("Descrição teste")
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(10)
                .metodoPgto(MetodoPagamento.PIX)
                .build();
    }

    @Test
    @DisplayName("Deve validar preço positivo com sucesso")
    void deveValidarPrecoPositivoComSucesso() {
        produto.setPreco(new BigDecimal("50.00"));
        assertDoesNotThrow(produto::validarPreco);
    }

    @Test
    @DisplayName("Não deve validar preço zero")
    void naoDeveValidarPrecoZero() {
        produto.setPreco(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, produto::validarPreco);
    }

    @Test
    @DisplayName("Não deve validar preço negativo")
    void naoDeveValidarPrecoNegativo() {
        produto.setPreco(new BigDecimal("-10.00"));
        assertThrows(IllegalArgumentException.class, produto::validarPreco);
    }

    @Test
    @DisplayName("Não deve validar preço nulo")
    void naoDeveValidarPrecoNulo() {
        produto.setPreco(null);
        assertThrows(IllegalArgumentException.class, produto::validarPreco);
    }

    @Test
    @DisplayName("Deve validar estoque não negativo com sucesso")
    void deveValidarEstoqueNaoNegativoComSucesso() {
        produto.setQuantidadeEstoque(0);
        assertDoesNotThrow(produto::validarEstoque);
        
        produto.setQuantidadeEstoque(100);
        assertDoesNotThrow(produto::validarEstoque);
    }

    @Test
    @DisplayName("Não deve validar estoque negativo")
    void naoDeveValidarEstoqueNegativo() {
        produto.setQuantidadeEstoque(-5);
        assertThrows(IllegalArgumentException.class, produto::validarEstoque);
    }

    @Test
    @DisplayName("Não deve validar estoque nulo")
    void naoDeveValidarEstoqueNulo() {
        produto.setQuantidadeEstoque(null);
        assertThrows(IllegalArgumentException.class, produto::validarEstoque);
    }

    @Test
    @DisplayName("Deve reduzir estoque corretamente")
    void deveReduzirEstoqueCorretamente() {
        produto.setQuantidadeEstoque(10);
        produto.reduzirEstoque(3);
        assertEquals(7, produto.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Não deve reduzir estoque com quantidade insuficiente")
    void naoDeveReduzirEstoqueComQuantidadeInsuficiente() {
        produto.setQuantidadeEstoque(5);
        assertThrows(IllegalArgumentException.class, () -> produto.reduzirEstoque(10));
    }

    @Test
    @DisplayName("Não deve reduzir estoque com quantidade negativa")
    void naoDeveReduzirEstoqueComQuantidadeNegativa() {
        produto.setQuantidadeEstoque(10);
        assertThrows(IllegalArgumentException.class, () -> produto.reduzirEstoque(-1));
    }

    @Test
    @DisplayName("Não deve reduzir estoque com quantidade nula")
    void naoDeveReduzirEstoqueComQuantidadeNula() {
        produto.setQuantidadeEstoque(10);
        assertThrows(IllegalArgumentException.class, () -> produto.reduzirEstoque(null));
    }

    @Test
    @DisplayName("Deve reduzir estoque até zero")
    void deveReduzirEstoqueAteZero() {
        produto.setQuantidadeEstoque(5);
        produto.reduzirEstoque(5);
        assertEquals(0, produto.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve devolver estoque ao produto")
    void deveDeoolverEstoqueAoProduto() {
        produto.setQuantidadeEstoque(5);
        produto.devolverEstoque(3);
        assertEquals(8, produto.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Não deve devolver estoque com quantidade negativa")
    void naoDeveDevolverEstoqueComQuantidadeNegativa() {
        produto.setQuantidadeEstoque(10);
        assertThrows(IllegalArgumentException.class, () -> produto.devolverEstoque(-1));
    }

    @Test
    @DisplayName("Não deve devolver estoque com quantidade nula")
    void naoDeveDevolverEstoqueComQuantidadeNula() {
        produto.setQuantidadeEstoque(10);
        assertThrows(IllegalArgumentException.class, () -> produto.devolverEstoque(null));
    }

    @Test
    @DisplayName("Deve verificar se tem estoque disponível")
    void deveVerificarSeTemEstoqueDisponivel() {
        produto.setQuantidadeEstoque(10);
        assertTrue(produto.temEstoque(5));
        assertTrue(produto.temEstoque(10));
        assertFalse(produto.temEstoque(11));
    }

    @Test
    @DisplayName("Deve retornar false quando estoque é nulo")
    void deveRetornarFalseQuandoEstoqueNulo() {
        produto.setQuantidadeEstoque(null);
        assertFalse(produto.temEstoque(1));
    }

    @Test
    @DisplayName("Deve retornar false quando não tem estoque")
    void deveRetornarFalseQuandoNaoTemEstoque() {
        produto.setQuantidadeEstoque(0);
        assertFalse(produto.temEstoque(1));
    }
}
