package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
import acc.br.projetoFinal.Accenture.model.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProdutoResponseDTO - Testes Positivos")
class ProdutoResponseDTOTests {

    private static final Long ID = 1L;
    private static final String NOME = "Notebook Dell";
    private static final String DESCRICAO = "Notebook com 16GB RAM";
    private static final BigDecimal PRECO = new BigDecimal("3500.00");
    private static final Integer ESTOQUE = 10;

    private Produto produtoBase;

    @BeforeEach
    void setUp() {
        produtoBase = Produto.builder()
                .id(ID)
                .nome(NOME)
                .descricao(DESCRICAO)
                .preco(PRECO)
                .quantidadeEstoque(ESTOQUE)
                .metodoPgto(MetodoPagamento.PIX)
                .build();
    }

    // -------------------------------------------------------------------------
    // Construtores
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve criar objeto com construtor padrão")
    void deveCriarComConstrutorPadrao() {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();

        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getNome());
        assertNull(dto.getDescricao());
        assertNull(dto.getPreco());
        assertNull(dto.getQuantidadeEstoque());
        assertNull(dto.getMetodoPgto());
    }

    @Test
    @DisplayName("Deve criar objeto com construtor completo")
    void deveCriarComConstrutorCompleto() {
        ProdutoResponseDTO dto = new ProdutoResponseDTO(ID, NOME, DESCRICAO, PRECO, ESTOQUE, "PIX");

        assertEquals(ID, dto.getId());
        assertEquals(NOME, dto.getNome());
        assertEquals(DESCRICAO, dto.getDescricao());
        assertEquals(PRECO, dto.getPreco());
        assertEquals(ESTOQUE, dto.getQuantidadeEstoque());
        assertEquals("PIX", dto.getMetodoPgto());
    }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve criar objeto via builder com todos os campos")
    void deveCriarViaBuilderCompleto() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .id(ID)
                .nome(NOME)
                .descricao(DESCRICAO)
                .preco(PRECO)
                .quantidadeEstoque(ESTOQUE)
                .metodoPgto("PIX")
                .build();

        assertEquals(ID, dto.getId());
        assertEquals(NOME, dto.getNome());
        assertEquals(DESCRICAO, dto.getDescricao());
        assertEquals(PRECO, dto.getPreco());
        assertEquals(ESTOQUE, dto.getQuantidadeEstoque());
        assertEquals("PIX", dto.getMetodoPgto());
    }

    @Test
    @DisplayName("Deve criar objeto via builder vazio")
    void deveCriarViaBuilderVazio() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder().build();

        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getNome());
        assertNull(dto.getDescricao());
        assertNull(dto.getPreco());
        assertNull(dto.getQuantidadeEstoque());
        assertNull(dto.getMetodoPgto());
    }

    // -------------------------------------------------------------------------
    // Setters e Getters
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve definir e obter todos os campos via setters")
    void deveDefinirEObterTodosCamposViaSetters() {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();

        dto.setId(ID);
        dto.setNome(NOME);
        dto.setDescricao(DESCRICAO);
        dto.setPreco(PRECO);
        dto.setQuantidadeEstoque(ESTOQUE);
        dto.setMetodoPgto("PIX");

        assertEquals(ID, dto.getId());
        assertEquals(NOME, dto.getNome());
        assertEquals(DESCRICAO, dto.getDescricao());
        assertEquals(PRECO, dto.getPreco());
        assertEquals(ESTOQUE, dto.getQuantidadeEstoque());
        assertEquals("PIX", dto.getMetodoPgto());
    }

    // -------------------------------------------------------------------------
    // fromEntity — cada MetodoPagamento
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve converter Produto com MetodoPagamento PIX para DTO")
    void deveConverterProdutoComPix() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.fromEntity(produtoBase);

        assertNotNull(dto);
        assertEquals(ID, dto.getId());
        assertEquals(NOME, dto.getNome());
        assertEquals(DESCRICAO, dto.getDescricao());
        assertEquals(PRECO, dto.getPreco());
        assertEquals(ESTOQUE, dto.getQuantidadeEstoque());
        assertEquals("PIX", dto.getMetodoPgto());
    }

    @Test
    @DisplayName("Deve converter Produto com MetodoPagamento CREDITO para DTO")
    void deveConverterProdutoComCredito() {
        Produto produto = Produto.builder()
                .id(2L).nome("Mouse").descricao("Mouse sem fio")
                .preco(new BigDecimal("150.00")).quantidadeEstoque(20)
                .metodoPgto(MetodoPagamento.CREDITO).build();

        ProdutoResponseDTO dto = ProdutoResponseDTO.fromEntity(produto);

        assertEquals("CREDITO", dto.getMetodoPgto());
    }

    @Test
    @DisplayName("Deve converter Produto com MetodoPagamento DEBITO para DTO")
    void deveConverterProdutoComDebito() {
        Produto produto = Produto.builder()
                .id(3L).nome("Teclado").descricao("Teclado mecânico")
                .preco(new BigDecimal("300.00")).quantidadeEstoque(5)
                .metodoPgto(MetodoPagamento.DEBITO).build();

        ProdutoResponseDTO dto = ProdutoResponseDTO.fromEntity(produto);

        assertEquals("DEBITO", dto.getMetodoPgto());
    }

    @Test
    @DisplayName("Deve converter Produto com MetodoPagamento BOLETO para DTO")
    void deveConverterProdutoComBoleto() {
        Produto produto = Produto.builder()
                .id(4L).nome("Monitor").descricao("Monitor 27\"")
                .preco(new BigDecimal("1200.00")).quantidadeEstoque(3)
                .metodoPgto(MetodoPagamento.BOLETO).build();

        ProdutoResponseDTO dto = ProdutoResponseDTO.fromEntity(produto);

        assertEquals("BOLETO", dto.getMetodoPgto());
    }

    @Test
    @DisplayName("Deve converter Produto com estoque zero")
    void deveConverterProdutoComEstoqueZero() {
        Produto produto = Produto.builder()
                .id(5L).nome("Produto Esgotado").descricao("Sem estoque")
                .preco(new BigDecimal("99.99")).quantidadeEstoque(0)
                .metodoPgto(MetodoPagamento.PIX).build();

        ProdutoResponseDTO dto = ProdutoResponseDTO.fromEntity(produto);

        assertEquals(0, dto.getQuantidadeEstoque());
    }

    // -------------------------------------------------------------------------
    // equals / hashCode
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Dois objetos com mesmos valores devem ser iguais")
    void doisObjetosComMesmosValoresDevemSerIguais() {
        ProdutoResponseDTO dto1 = new ProdutoResponseDTO(ID, NOME, DESCRICAO, PRECO, ESTOQUE, "PIX");
        ProdutoResponseDTO dto2 = new ProdutoResponseDTO(ID, NOME, DESCRICAO, PRECO, ESTOQUE, "PIX");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Objeto deve ser igual a si mesmo")
    void objetoDeveSerIgualASiMesmo() {
        ProdutoResponseDTO dto = new ProdutoResponseDTO(ID, NOME, DESCRICAO, PRECO, ESTOQUE, "PIX");
        assertEquals(dto, dto);
    }

    @Test
    @DisplayName("Dois objetos padrão devem ser iguais")
    void doisObjetosPadraoDevemSerIguais() {
        ProdutoResponseDTO dto1 = new ProdutoResponseDTO();
        ProdutoResponseDTO dto2 = new ProdutoResponseDTO();
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("toString não deve ser nulo")
    void toStringNaoDeveSerNulo() {
        assertNotNull(new ProdutoResponseDTO().toString());
    }

    @Test
    @DisplayName("toString deve conter os valores dos campos")
    void toStringDeveConterValores() {
        ProdutoResponseDTO dto = new ProdutoResponseDTO(ID, NOME, DESCRICAO, PRECO, ESTOQUE, "PIX");
        String str = dto.toString();

        assertTrue(str.contains(NOME));
        assertTrue(str.contains("PIX"));
    }
}