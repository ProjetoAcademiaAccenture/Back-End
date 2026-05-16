package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.enums.Categoria;
import acc.br.projetoFinal.Accenture.model.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProdutoResponseDTO - Testes Positivos")
class ProdutoResponseDTOTests {

    private static final Long       ID        = 1L;
    private static final String     NOME      = "Notebook Dell";
    private static final String     DESCRICAO = "Notebook com 16GB RAM";
    private static final String     URL       = "https://img.com/nb.jpg";
    private static final BigDecimal PRECO     = new BigDecimal("3500.00");
    private static final Integer    ESTOQUE   = 10;
    private static final String     CATEGORIA = "ELETRONICOS";

    private Produto produtoBase;

    @BeforeEach
    void setUp() {
        produtoBase = Produto.builder()
                .id(ID)
                .nome(NOME)
                .descricao(DESCRICAO)
                .urlImagem(URL)
                .preco(PRECO)
                .quantidadeEstoque(ESTOQUE)
                .categoria(Categoria.ELETRONICOS)
                .build();
    }

    private ProdutoResponseDTO dtoPadraoValido() {
        return new ProdutoResponseDTO(ID, NOME, DESCRICAO, URL, PRECO, ESTOQUE, CATEGORIA);
    }

    // =========================================================
    // Construtores
    // =========================================================

    @Test
    @DisplayName("Deve criar objeto com construtor padrão")
    void deveCriarComConstrutorPadrao() {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();

        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getNome());
        assertNull(dto.getDescricao());
        assertNull(dto.getUrlImagem());
        assertNull(dto.getPreco());
        assertNull(dto.getQuantidadeEstoque());
        assertNull(dto.getCategoria());
    }

    @Test
    @DisplayName("Deve criar objeto com construtor all-args")
    void deveCriarComConstrutorAllArgs() {
        ProdutoResponseDTO dto = dtoPadraoValido();

        assertAll("AllArgsConstructor",
                () -> assertEquals(ID,        dto.getId()),
                () -> assertEquals(NOME,      dto.getNome()),
                () -> assertEquals(DESCRICAO, dto.getDescricao()),
                () -> assertEquals(URL,       dto.getUrlImagem()),
                () -> assertEquals(PRECO,     dto.getPreco()),
                () -> assertEquals(ESTOQUE,   dto.getQuantidadeEstoque()),
                () -> assertEquals(CATEGORIA, dto.getCategoria())
        );
    }

    // =========================================================
    // Builder
    // =========================================================

    @Test
    @DisplayName("Deve criar objeto via builder com todos os campos")
    void deveCriarViaBuilderCompleto() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .id(ID)
                .nome(NOME)
                .descricao(DESCRICAO)
                .urlImagem(URL)
                .preco(PRECO)
                .quantidadeEstoque(ESTOQUE)
                .categoria(CATEGORIA)
                .build();

        assertAll("builder completo",
                () -> assertEquals(ID,        dto.getId()),
                () -> assertEquals(NOME,      dto.getNome()),
                () -> assertEquals(DESCRICAO, dto.getDescricao()),
                () -> assertEquals(URL,       dto.getUrlImagem()),
                () -> assertEquals(PRECO,     dto.getPreco()),
                () -> assertEquals(ESTOQUE,   dto.getQuantidadeEstoque()),
                () -> assertEquals(CATEGORIA, dto.getCategoria())
        );
    }

    @Test
    @DisplayName("Deve criar objeto via builder vazio com todos os campos nulos")
    void deveCriarViaBuilderVazio() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder().build();

        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getNome());
        assertNull(dto.getDescricao());
        assertNull(dto.getUrlImagem());
        assertNull(dto.getPreco());
        assertNull(dto.getQuantidadeEstoque());
        assertNull(dto.getCategoria());
    }

    @Test
    @DisplayName("Deve criar objeto via builder parcial")
    void deveCriarViaBuilderParcial() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .id(ID)
                .nome(NOME)
                .build();

        assertEquals(ID, dto.getId());
        assertEquals(NOME, dto.getNome());
        assertNull(dto.getUrlImagem());
        assertNull(dto.getCategoria());
    }

    // =========================================================
    // Setters e Getters
    // =========================================================

    @Test
    @DisplayName("Deve definir e obter todos os campos via setters")
    void deveDefinirEObterTodosCamposViaSetters() {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();

        dto.setId(ID);
        dto.setNome(NOME);
        dto.setDescricao(DESCRICAO);
        dto.setUrlImagem(URL);
        dto.setPreco(PRECO);
        dto.setQuantidadeEstoque(ESTOQUE);
        dto.setCategoria(CATEGORIA);

        assertAll("setters/getters",
                () -> assertEquals(ID,        dto.getId()),
                () -> assertEquals(NOME,      dto.getNome()),
                () -> assertEquals(DESCRICAO, dto.getDescricao()),
                () -> assertEquals(URL,       dto.getUrlImagem()),
                () -> assertEquals(PRECO,     dto.getPreco()),
                () -> assertEquals(ESTOQUE,   dto.getQuantidadeEstoque()),
                () -> assertEquals(CATEGORIA, dto.getCategoria())
        );
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

    @Test
    @DisplayName("Deve verificar independência entre instâncias")
    void deveVerificarIndependenciaEntreInstancias() {
        ProdutoResponseDTO dto1 = new ProdutoResponseDTO();
        dto1.setNome("Produto A");

        ProdutoResponseDTO dto2 = new ProdutoResponseDTO();
        dto2.setNome("Produto B");

        assertEquals("Produto A", dto1.getNome());
        assertEquals("Produto B", dto2.getNome());
    }

    // =========================================================
    // fromEntity
    // =========================================================

    @Test
    @DisplayName("Deve converter Produto para DTO com todos os campos corretamente")
    void deveConverterProdutoParaDTO() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.fromEntity(produtoBase);

        assertAll("fromEntity campos",
                () -> assertNotNull(dto),
                () -> assertEquals(ID,            dto.getId()),
                () -> assertEquals(NOME,          dto.getNome()),
                () -> assertEquals(DESCRICAO,     dto.getDescricao()),
                () -> assertEquals(URL,           dto.getUrlImagem()),
                () -> assertEquals(PRECO,         dto.getPreco()),
                () -> assertEquals(ESTOQUE,       dto.getQuantidadeEstoque()),
                () -> assertEquals("ELETRONICOS", dto.getCategoria())
        );
    }

    @Test
    @DisplayName("Deve converter Produto com categoria ALIMENTO")
    void deveConverterProdutoComCategoriaAlimento() {
        Produto produto = Produto.builder()
                .id(2L).nome("Arroz").descricao("Arroz tipo 1")
                .urlImagem("https://img.com/arroz.jpg")
                .preco(new BigDecimal("25.00")).quantidadeEstoque(100)
                .categoria(Categoria.ALIMENTOS)
                .build();

        assertEquals("ALIMENTOS", ProdutoResponseDTO.fromEntity(produto).getCategoria());
    }

    @Test
    @DisplayName("Deve converter Produto com categoria VESTUARIO")
    void deveConverterProdutoComCategoriaVestuario() {
        Produto produto = Produto.builder()
                .id(3L).nome("Camiseta").descricao("Camiseta polo")
                .urlImagem("https://img.com/camisa.jpg")
                .preco(new BigDecimal("80.00")).quantidadeEstoque(50)
                .categoria(Categoria.VESTUARIO)
                .build();

        assertEquals("VESTUARIO", ProdutoResponseDTO.fromEntity(produto).getCategoria());
    }

    @Test
    @DisplayName("Deve converter Produto com estoque zero")
    void deveConverterProdutoComEstoqueZero() {
        Produto produto = Produto.builder()
                .id(4L).nome("Produto Esgotado").descricao("Sem estoque")
                .urlImagem("https://img.com/esg.jpg")
                .preco(new BigDecimal("99.99")).quantidadeEstoque(0)
                .categoria(Categoria.ELETRONICOS)
                .build();

        assertEquals(0, ProdutoResponseDTO.fromEntity(produto).getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve converter Produto com urlImagem null")
    void deveConverterProdutoComUrlImagemNull() {
        Produto produto = Produto.builder()
                .id(5L).nome("Produto Sem Foto").descricao("Sem imagem")
                .urlImagem(null)
                .preco(new BigDecimal("50.00")).quantidadeEstoque(5)
                .categoria(Categoria.ELETRONICOS)
                .build();

        assertNull(ProdutoResponseDTO.fromEntity(produto).getUrlImagem());
    }

    // =========================================================
    // equals / hashCode
    // =========================================================

    @Test
    @DisplayName("Dois objetos com mesmos valores devem ser iguais")
    void doisObjetosComMesmosValoresDevemSerIguais() {
        ProdutoResponseDTO dto1 = dtoPadraoValido();
        ProdutoResponseDTO dto2 = dtoPadraoValido();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Objeto deve ser igual a si mesmo (reflexividade)")
    void objetoDeveSerIgualASiMesmo() {
        ProdutoResponseDTO dto = dtoPadraoValido();
        assertEquals(dto, dto);
    }

    @Test
    @DisplayName("equals deve ser simétrico")
    void equals_DeveSerSimetrico() {
        ProdutoResponseDTO dto1 = dtoPadraoValido();
        ProdutoResponseDTO dto2 = dtoPadraoValido();
        assertEquals(dto1.equals(dto2), dto2.equals(dto1));
    }

    @Test
    @DisplayName("Dois objetos padrão devem ser iguais")
    void doisObjetosPadraoDevemSerIguais() {
        ProdutoResponseDTO dto1 = new ProdutoResponseDTO();
        ProdutoResponseDTO dto2 = new ProdutoResponseDTO();
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("hashCode deve ser consistente em múltiplas chamadas")
    void hashCode_DeveSerConsistente() {
        ProdutoResponseDTO dto = dtoPadraoValido();
        assertEquals(dto.hashCode(), dto.hashCode());
    }

    @Test
    @DisplayName("hashCode não deve lançar exceção com campos nulos")
    void hashCode_NaoDeveLancarExcecao_QuandoCamposNulos() {
        assertThatCode(() -> new ProdutoResponseDTO().hashCode()).doesNotThrowAnyException();
    }

    // =========================================================
    // toString
    // =========================================================

    @Test
    @DisplayName("toString não deve ser nulo")
    void toStringNaoDeveSerNulo() {
        assertNotNull(new ProdutoResponseDTO().toString());
    }

    @Test
    @DisplayName("toString deve conter os campos principais")
    void toStringDeveConterCamposPrincipais() {
        String result = dtoPadraoValido().toString();

        assertAll("toString",
                () -> assertTrue(result.contains("nome")),
                () -> assertTrue(result.contains("descricao")),
                () -> assertTrue(result.contains("urlImagem")),
                () -> assertTrue(result.contains("preco")),
                () -> assertTrue(result.contains("categoria"))
        );
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