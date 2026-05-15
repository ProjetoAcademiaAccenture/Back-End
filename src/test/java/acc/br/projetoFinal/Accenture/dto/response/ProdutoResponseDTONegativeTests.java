package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.enums.Categoria;
import acc.br.projetoFinal.Accenture.model.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProdutoResponseDTO - Testes Negativos")
class ProdutoResponseDTONegativeTests {

    private static final Long       ID        = 1L;
    private static final String     NOME      = "Notebook Dell";
    private static final String     DESCRICAO = "Notebook com 16GB RAM";
    private static final String     URL       = "https://img.com/nb.jpg";
    private static final BigDecimal PRECO     = new BigDecimal("3500.00");
    private static final Integer    ESTOQUE   = 10;
    private static final String     CATEGORIA = "ELETRONICOS";

    // ------------------------------------------------------------------ helper

    private ProdutoResponseDTO dtoPadraoValido() {
        // AllArgsConstructor: id, nome, descricao, urlImagem, preco, quantidadeEstoque, categoria
        return new ProdutoResponseDTO(ID, NOME, DESCRICAO, URL, PRECO, ESTOQUE, CATEGORIA);
    }

    // =========================================================
    // Builder — cada campo nulo individualmente
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
                .id(null).nome(NOME).descricao(DESCRICAO)
                .urlImagem(URL).preco(PRECO).quantidadeEstoque(ESTOQUE)
                .categoria(CATEGORIA).build();

        assertNull(dto.getId());
        assertNotNull(dto.getNome());
    }

    @Test
    @DisplayName("Deve criar objeto com nome null via builder")
    void deveCriarComNomeNull() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .id(ID).nome(null).descricao(DESCRICAO)
                .urlImagem(URL).preco(PRECO).quantidadeEstoque(ESTOQUE)
                .categoria(CATEGORIA).build();

        assertNull(dto.getNome());
    }

    @Test
    @DisplayName("Deve criar objeto com descricao null via builder")
    void deveCriarComDescricaoNull() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .id(ID).nome(NOME).descricao(null)
                .urlImagem(URL).preco(PRECO).quantidadeEstoque(ESTOQUE)
                .categoria(CATEGORIA).build();

        assertNull(dto.getDescricao());
    }

    @Test
    @DisplayName("Deve criar objeto com urlImagem null via builder")
    void deveCriarComUrlImagemNull() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .id(ID).nome(NOME).descricao(DESCRICAO)
                .urlImagem(null).preco(PRECO).quantidadeEstoque(ESTOQUE)
                .categoria(CATEGORIA).build();

        assertNull(dto.getUrlImagem());
    }

    @Test
    @DisplayName("Deve criar objeto com preco null via builder")
    void deveCriarComPrecoNull() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .id(ID).nome(NOME).descricao(DESCRICAO)
                .urlImagem(URL).preco(null).quantidadeEstoque(ESTOQUE)
                .categoria(CATEGORIA).build();

        assertNull(dto.getPreco());
    }

    @Test
    @DisplayName("Deve criar objeto com quantidadeEstoque null via builder")
    void deveCriarComQuantidadeEstoqueNull() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .id(ID).nome(NOME).descricao(DESCRICAO)
                .urlImagem(URL).preco(PRECO).quantidadeEstoque(null)
                .categoria(CATEGORIA).build();

        assertNull(dto.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve criar objeto com categoria null via builder")
    void deveCriarComCategoriaNull() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .id(ID).nome(NOME).descricao(DESCRICAO)
                .urlImagem(URL).preco(PRECO).quantidadeEstoque(ESTOQUE)
                .categoria(null).build();

        assertNull(dto.getCategoria());
    }

    // =========================================================
    // Valores inválidos / extremos
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
                .preco(new BigDecimal("-50.00")).build();
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
    @DisplayName("Deve aceitar quantidadeEstoque negativa")
    void deveAceitarQuantidadeEstoqueNegativa() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .quantidadeEstoque(-5).build();
        assertTrue(dto.getQuantidadeEstoque() < 0);
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

    @Test
    @DisplayName("Deve aceitar preco muito grande")
    void deveAceitarPrecoMuitoGrande() {
        BigDecimal precoGrande = new BigDecimal("999999999.99");
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder().preco(precoGrande).build();
        assertEquals(precoGrande, dto.getPreco());
    }

    @Test
    @DisplayName("Deve aceitar quantidadeEstoque zero")
    void deveAceitarQuantidadeEstoqueZero() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder().quantidadeEstoque(0).build();
        assertEquals(0, dto.getQuantidadeEstoque());
    }

    // =========================================================
    // Setters — atribuição de nulos
    // =========================================================

    @Test
    @DisplayName("Deve aceitar null em todos os setters")
    void deveAceitarNullEmTodosOsSetters() {
        // AllArgsConstructor: id, nome, descricao, urlImagem, preco, quantidadeEstoque, categoria
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

    @Test
    @DisplayName("Deve atualizar campo múltiplas vezes via setter")
    void deveAtualizarCampoMultiplasVezes() {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();

        dto.setNome("Nome 1");
        assertEquals("Nome 1", dto.getNome());

        dto.setNome("Nome 2");
        assertEquals("Nome 2", dto.getNome());
    }

    // =========================================================
    // equals / hashCode — objetos diferentes
    // =========================================================

    @Test
    @DisplayName("Objetos com id diferentes não devem ser iguais")
    void objetosComIdDiferentesNaoDevemSerIguais() {
        ProdutoResponseDTO dto1 = new ProdutoResponseDTO(1L, NOME, DESCRICAO, URL, PRECO, ESTOQUE, CATEGORIA);
        ProdutoResponseDTO dto2 = new ProdutoResponseDTO(2L, NOME, DESCRICAO, URL, PRECO, ESTOQUE, CATEGORIA);
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objetos com nome diferentes não devem ser iguais")
    void objetosComNomeDiferentesNaoDevemSerIguais() {
        ProdutoResponseDTO dto1 = new ProdutoResponseDTO(ID, "Notebook", DESCRICAO, URL, PRECO, ESTOQUE, CATEGORIA);
        ProdutoResponseDTO dto2 = new ProdutoResponseDTO(ID, "Mouse",    DESCRICAO, URL, PRECO, ESTOQUE, CATEGORIA);
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objetos com descricao diferentes não devem ser iguais")
    void objetosComDescricaoDiferentesNaoDevemSerIguais() {
        ProdutoResponseDTO dto1 = new ProdutoResponseDTO(ID, NOME, "Desc A", URL, PRECO, ESTOQUE, CATEGORIA);
        ProdutoResponseDTO dto2 = new ProdutoResponseDTO(ID, NOME, "Desc B", URL, PRECO, ESTOQUE, CATEGORIA);
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objetos com urlImagem diferentes não devem ser iguais")
    void objetosComUrlImagemDiferentesNaoDevemSerIguais() {
        ProdutoResponseDTO dto1 = new ProdutoResponseDTO(ID, NOME, DESCRICAO, "https://a.com", PRECO, ESTOQUE, CATEGORIA);
        ProdutoResponseDTO dto2 = new ProdutoResponseDTO(ID, NOME, DESCRICAO, "https://b.com", PRECO, ESTOQUE, CATEGORIA);
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objetos com preco diferentes não devem ser iguais")
    void objetosComPrecoDiferentesNaoDevemSerIguais() {
        ProdutoResponseDTO dto1 = new ProdutoResponseDTO(ID, NOME, DESCRICAO, URL, new BigDecimal("100.00"), ESTOQUE, CATEGORIA);
        ProdutoResponseDTO dto2 = new ProdutoResponseDTO(ID, NOME, DESCRICAO, URL, new BigDecimal("200.00"), ESTOQUE, CATEGORIA);
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objetos com quantidadeEstoque diferentes não devem ser iguais")
    void objetosComEstoqueDiferentesNaoDevemSerIguais() {
        ProdutoResponseDTO dto1 = new ProdutoResponseDTO(ID, NOME, DESCRICAO, URL, PRECO, 10,  CATEGORIA);
        ProdutoResponseDTO dto2 = new ProdutoResponseDTO(ID, NOME, DESCRICAO, URL, PRECO, 99,  CATEGORIA);
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objetos com categoria diferentes não devem ser iguais")
    void objetosComCategoriaDiferentesNaoDevemSerIguais() {
        ProdutoResponseDTO dto1 = new ProdutoResponseDTO(ID, NOME, DESCRICAO, URL, PRECO, ESTOQUE, "ELETRONICOS");
        ProdutoResponseDTO dto2 = new ProdutoResponseDTO(ID, NOME, DESCRICAO, URL, PRECO, ESTOQUE, "ALIMENTO");
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
        assertNotEquals(42,       dtoPadraoValido());
    }

    // --- branches null: this.campo == null, other.campo != null ---

    @Test
    @DisplayName("equals: this.id==null e other.id!=null deve retornar false")
    void equals_ThisIdNull_DeveRetornarFalse() {
        ProdutoResponseDTO d1 = dtoPadraoValido();
        d1.setId(null);
        assertNotEquals(d1, dtoPadraoValido());
    }

    @Test
    @DisplayName("equals: this.nome==null e other!=null deve retornar false")
    void equals_ThisNomeNull_DeveRetornarFalse() {
        ProdutoResponseDTO d1 = dtoPadraoValido();
        d1.setNome(null);
        assertNotEquals(d1, dtoPadraoValido());
    }

    @Test
    @DisplayName("equals: this.descricao==null e other!=null deve retornar false")
    void equals_ThisDescricaoNull_DeveRetornarFalse() {
        ProdutoResponseDTO d1 = dtoPadraoValido();
        d1.setDescricao(null);
        assertNotEquals(d1, dtoPadraoValido());
    }

    @Test
    @DisplayName("equals: this.urlImagem==null e other!=null deve retornar false")
    void equals_ThisUrlImagemNull_DeveRetornarFalse() {
        ProdutoResponseDTO d1 = dtoPadraoValido();
        d1.setUrlImagem(null);
        assertNotEquals(d1, dtoPadraoValido());
    }

    @Test
    @DisplayName("equals: this.preco==null e other!=null deve retornar false")
    void equals_ThisPrecoNull_DeveRetornarFalse() {
        ProdutoResponseDTO d1 = dtoPadraoValido();
        d1.setPreco(null);
        assertNotEquals(d1, dtoPadraoValido());
    }

    @Test
    @DisplayName("equals: this.quantidadeEstoque==null e other!=null deve retornar false")
    void equals_ThisQuantidadeEstoqueNull_DeveRetornarFalse() {
        ProdutoResponseDTO d1 = dtoPadraoValido();
        d1.setQuantidadeEstoque(null);
        assertNotEquals(d1, dtoPadraoValido());
    }

    @Test
    @DisplayName("equals: this.categoria==null e other!=null deve retornar false")
    void equals_ThisCategoriaNull_DeveRetornarFalse() {
        ProdutoResponseDTO d1 = dtoPadraoValido();
        d1.setCategoria(null);
        assertNotEquals(d1, dtoPadraoValido());
    }

    // =========================================================
    // hashCode
    // =========================================================

    @Test
    @DisplayName("hashCode não deve lançar exceção com todos os campos nulos")
    void hashCode_NaoDeveLancarExcecao_QuandoCamposNulos() {
        assertThatCode(() -> new ProdutoResponseDTO().hashCode()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("hashCode deve diferir quando dados diferem")
    void hashCode_DeveDiferir_QuandoDadosDiferem() {
        ProdutoResponseDTO dto1 = dtoPadraoValido();
        ProdutoResponseDTO dto2 = dtoPadraoValido();
        dto2.setNome("Nome Completamente Diferente");
        assertNotEquals(dto1.hashCode(), dto2.hashCode());
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
        assertThatCode(() -> new ProdutoResponseDTO().toString()).doesNotThrowAnyException();
    }

    // =========================================================
    // canEqual
    // =========================================================

    @Test
    @DisplayName("canEqual deve retornar false para tipo diferente")
    void canEqual_TipoDiferente_DeveRetornarFalse() {
        ProdutoResponseDTO dto = dtoPadraoValido();
        assertFalse(dto.canEqual("string"));
        assertFalse(dto.canEqual(null));
        assertFalse(dto.canEqual(42));
    }
}