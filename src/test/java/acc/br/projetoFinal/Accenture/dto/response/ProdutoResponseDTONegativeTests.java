package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.enums.Categoria;
import acc.br.projetoFinal.Accenture.model.Produto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProdutoResponseDTO - Testes Negativos")
class ProdutoResponseDTONegativeTests {

    // ─── Helper ───────────────────────────────────────────────────────────────

    private Produto buildProduto(Long id, String nome, String descricao,
                                  String urlImagem, BigDecimal preco,
                                  Integer quantidade, Categoria categoria) {
        return Produto.builder()
                .id(id)
                .nome(nome)
                .descricao(descricao)
                .urlImagem(urlImagem)
                .preco(preco)
                .quantidadeEstoque(quantidade)
                .categoria(categoria)
                .build();
    }

    private ProdutoResponseDTO dtoPadraoValido() {
        return ProdutoResponseDTO.builder()
                .id(1L)
                .nome("Produto A")
                .descricao("Descrição A")
                .urlImagem("http://img.com/a.png")
                .preco(new BigDecimal("99.90"))
                .quantidadeEstoque(10)
                .categoria("ELETRONICO")
                .build();
    }

    // =========================================================
    // Construtor padrão — campos nulos
    // =========================================================

    @Test
    @DisplayName("Deve criar objeto com construtor padrão com todos os campos nulos")
    void deveCriarComConstrutorPadraoComCamposNulos() {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();

        assertNull(dto.getId());
        assertNull(dto.getNome());
        assertNull(dto.getDescricao());
        assertNull(dto.getUrlImagem());
        assertNull(dto.getPreco());
        assertNull(dto.getQuantidadeEstoque());
        assertNull(dto.getCategoria());
    }

    // =========================================================
    // Builder — campos nulos individualmente
    // =========================================================

    @Test
    @DisplayName("Deve criar objeto via builder vazio com todos os campos nulos")
    void deveCriarViaBuilderVazioComCamposNulos() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder().build();

        assertNull(dto.getId());
        assertNull(dto.getNome());
        assertNull(dto.getDescricao());
        assertNull(dto.getUrlImagem());
        assertNull(dto.getPreco());
        assertNull(dto.getQuantidadeEstoque());
        assertNull(dto.getCategoria());
    }

    @Test
    @DisplayName("Deve criar objeto com id null via builder")
    void deveCriarComIdNull() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .id(null).nome("Produto X").build();

        assertNull(dto.getId());
        assertNotNull(dto.getNome());
    }

    @Test
    @DisplayName("Deve criar objeto com nome null via builder")
    void deveCriarComNomeNull() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .id(1L).nome(null).build();

        assertNull(dto.getNome());
    }

    @Test
    @DisplayName("Deve criar objeto com descricao null via builder")
    void deveCriarComDescricaoNull() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .id(1L).descricao(null).build();

        assertNull(dto.getDescricao());
    }

    @Test
    @DisplayName("Deve criar objeto com urlImagem null via builder")
    void deveCriarComUrlImagemNull() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .id(1L).urlImagem(null).build();

        assertNull(dto.getUrlImagem());
    }

    @Test
    @DisplayName("Deve criar objeto com preco null via builder")
    void deveCriarComPrecoNull() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .id(1L).preco(null).build();

        assertNull(dto.getPreco());
    }

    @Test
    @DisplayName("Deve criar objeto com quantidadeEstoque null via builder")
    void deveCriarComQuantidadeEstoqueNull() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .id(1L).quantidadeEstoque(null).build();

        assertNull(dto.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve criar objeto com categoria null via builder")
    void deveCriarComCategoriaNull() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .id(1L).categoria(null).build();

        assertNull(dto.getCategoria());
    }

    // =========================================================
    // Valores extremos / inválidos
    // =========================================================

    @Test
    @DisplayName("Deve aceitar id negativo")
    void deveAceitarIdNegativo() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder().id(-1L).build();
        assertTrue(dto.getId() < 0);
    }

    @Test
    @DisplayName("Deve aceitar preco negativo")
    void deveAceitarPrecoNegativo() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .preco(new BigDecimal("-10.00")).build();

        assertTrue(dto.getPreco().signum() < 0);
    }

    @Test
    @DisplayName("Deve aceitar preco zero")
    void deveAceitarPrecoZero() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .preco(BigDecimal.ZERO).build();

        assertEquals(BigDecimal.ZERO, dto.getPreco());
    }

    @Test
    @DisplayName("Deve aceitar preco muito grande")
    void deveAceitarPrecoMuitoGrande() {
        BigDecimal valorGrande = new BigDecimal("999999999.99");
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .preco(valorGrande).build();

        assertEquals(valorGrande, dto.getPreco());
    }

    @Test
    @DisplayName("Deve aceitar quantidadeEstoque negativa")
    void deveAceitarQuantidadeEstoqueNegativa() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .quantidadeEstoque(-5).build();

        assertTrue(dto.getQuantidadeEstoque() < 0);
    }

    @Test
    @DisplayName("Deve aceitar quantidadeEstoque zero")
    void deveAceitarQuantidadeEstoqueZero() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .quantidadeEstoque(0).build();

        assertEquals(0, dto.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve aceitar nome vazio")
    void deveAceitarNomeVazio() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder().nome("").build();
        assertEquals("", dto.getNome());
    }

    @Test
    @DisplayName("Deve aceitar descricao vazia")
    void deveAceitarDescricaoVazia() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder().descricao("").build();
        assertEquals("", dto.getDescricao());
    }

    @Test
    @DisplayName("Deve aceitar urlImagem vazia")
    void deveAceitarUrlImagemVazia() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder().urlImagem("").build();
        assertEquals("", dto.getUrlImagem());
    }

    // =========================================================
    // fromEntity — branches de NPE
    // =========================================================

    @Test
    @DisplayName("fromEntity deve lançar NullPointerException quando produto for null")
    void fromEntity_DeveLancarExcecao_QuandoProdutoNull() {
        assertThatThrownBy(() -> ProdutoResponseDTO.fromEntity(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("fromEntity deve lançar NullPointerException quando categoria for null")
    void fromEntity_DeveLancarExcecao_QuandoCategoriaNull() {
        Produto produto = buildProduto(1L, "Produto A", "Desc", "url",
                new BigDecimal("10.00"), 5, null);

        assertThatThrownBy(() -> ProdutoResponseDTO.fromEntity(produto))
                .isInstanceOf(NullPointerException.class);
    }

    // =========================================================
    // Setters — atribuição de nulos
    // =========================================================

    @Test
    @DisplayName("Deve aceitar null em todos os setters")
    void deveAceitarNullEmTodosOsSetters() {
        ProdutoResponseDTO dto = dtoPadraoValido();

        assertThatCode(() -> {
            dto.setId(null);
            dto.setNome(null);
            dto.setDescricao(null);
            dto.setUrlImagem(null);
            dto.setPreco(null);
            dto.setQuantidadeEstoque(null);
            dto.setCategoria(null);
        }).doesNotThrowAnyException();

        assertNull(dto.getId());
        assertNull(dto.getNome());
        assertNull(dto.getDescricao());
        assertNull(dto.getUrlImagem());
        assertNull(dto.getPreco());
        assertNull(dto.getQuantidadeEstoque());
        assertNull(dto.getCategoria());
    }

    // =========================================================
    // equals / hashCode — objetos diferentes
    // =========================================================

    @Test
    @DisplayName("Objetos com id diferentes não devem ser iguais")
    void objetosComIdDiferentesNaoDevemSerIguais() {
        ProdutoResponseDTO dto1 = ProdutoResponseDTO.builder().id(1L).build();
        ProdutoResponseDTO dto2 = ProdutoResponseDTO.builder().id(2L).build();
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objetos com nome diferentes não devem ser iguais")
    void objetosComNomeDiferentesNaoDevemSerIguais() {
        ProdutoResponseDTO dto1 = ProdutoResponseDTO.builder().id(1L).nome("A").build();
        ProdutoResponseDTO dto2 = ProdutoResponseDTO.builder().id(1L).nome("B").build();
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objetos com preco diferentes não devem ser iguais")
    void objetosComPrecoDiferentesNaoDevemSerIguais() {
        ProdutoResponseDTO dto1 = ProdutoResponseDTO.builder()
                .id(1L).preco(new BigDecimal("10.00")).build();
        ProdutoResponseDTO dto2 = ProdutoResponseDTO.builder()
                .id(1L).preco(new BigDecimal("20.00")).build();
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objetos com quantidadeEstoque diferentes não devem ser iguais")
    void objetosComQuantidadeEstoqueDiferentesNaoDevemSerIguais() {
        ProdutoResponseDTO dto1 = ProdutoResponseDTO.builder().id(1L).quantidadeEstoque(1).build();
        ProdutoResponseDTO dto2 = ProdutoResponseDTO.builder().id(1L).quantidadeEstoque(2).build();
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objetos com categoria diferentes não devem ser iguais")
    void objetosComCategoriaDiferentesNaoDevemSerIguais() {
        ProdutoResponseDTO dto1 = ProdutoResponseDTO.builder().id(1L).categoria("ELETRONICO").build();
        ProdutoResponseDTO dto2 = ProdutoResponseDTO.builder().id(1L).categoria("ALIMENTO").build();
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objeto não deve ser igual a null")
    void objetoNaoDeveSerIgualANull() {
        assertNotEquals(null, dtoPadraoValido());
    }

    @Test
    @DisplayName("Objeto não deve ser igual a tipo diferente")
    void objetoNaoDeveSerIgualATipoDiferente() {
        assertNotEquals("string", dtoPadraoValido());
        assertNotEquals(42, dtoPadraoValido());
    }

    @Test
    @DisplayName("Objetos com mesmos dados devem ser iguais")
    void objetosComMesmosDadosDevemSerIguais() {
        ProdutoResponseDTO dto1 = dtoPadraoValido();
        ProdutoResponseDTO dto2 = dtoPadraoValido();
        assertEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Mesma instância deve ser igual a si mesma")
    void mesmaInstanciaDeveSerIgualASiMesma() {
        ProdutoResponseDTO dto = dtoPadraoValido();
        assertEquals(dto, dto);
    }

    // =========================================================
    // hashCode
    // =========================================================

    @Test
    @DisplayName("hashCode: objetos iguais devem ter mesmo hashCode")
    void hashCode_ObjetosIguais_DeveTerMesmoHashCode() {
        assertEquals(dtoPadraoValido().hashCode(), dtoPadraoValido().hashCode());
    }

    @Test
    @DisplayName("hashCode: não deve lançar exceção com campos nulos")
    void hashCode_CamposNull_NaoDeveLancarExcecao() {
        assertThatCode(() -> new ProdutoResponseDTO().hashCode())
                .doesNotThrowAnyException();
    }

    // =========================================================
    // toString
    // =========================================================

    @Test
    @DisplayName("toString não deve lançar exceção")
    void toString_NaoDeveLancarExcecao() {
        assertThatCode(() -> dtoPadraoValido().toString()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("toString não deve lançar exceção com campos nulos")
    void toString_NaoDeveLancarExcecao_QuandoCamposNulos() {
        assertThatCode(() -> new ProdutoResponseDTO().toString())
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("toString deve conter campos principais")
    void toString_DeveConterCamposPrincipais() {
        String result = dtoPadraoValido().toString();
        assertAll("toString",
                () -> assertTrue(result.contains("id")),
                () -> assertTrue(result.contains("nome")),
                () -> assertTrue(result.contains("descricao")),
                () -> assertTrue(result.contains("preco")),
                () -> assertTrue(result.contains("quantidadeEstoque")),
                () -> assertTrue(result.contains("categoria"))
        );
    }

    // =========================================================
    // canEqual
    // =========================================================

    @Test
    @DisplayName("canEqual deve retornar true para instância do mesmo tipo")
    void canEqual_MesmoTipo_DeveRetornarTrue() {
        ProdutoResponseDTO dto1 = dtoPadraoValido();
        ProdutoResponseDTO dto2 = new ProdutoResponseDTO();
        assertTrue(dto1.canEqual(dto2));
    }

    @Test
    @DisplayName("canEqual deve retornar false para tipo diferente")
    void canEqual_TipoDiferente_DeveRetornarFalse() {
        ProdutoResponseDTO dto = dtoPadraoValido();
        assertFalse(dto.canEqual("string"));
        assertFalse(dto.canEqual(null));
    }
}