package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
import acc.br.projetoFinal.Accenture.model.Produto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProdutoResponseDTO - Testes Negativos")
class ProdutoResponseDTONegativeTests {

    private static final Long ID = 1L;
    private static final String NOME = "Notebook Dell";
    private static final String DESCRICAO = "Notebook com 16GB RAM";
    private static final BigDecimal PRECO = new BigDecimal("3500.00");
    private static final Integer ESTOQUE = 10;

    // -------------------------------------------------------------------------
    // Builder — cada campo nulo individualmente
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve criar objeto via builder vazio com todos os campos nulos")
    void deveCriarViaBuilderVazioComCamposNulos() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder().build();

        assertNull(dto.getId());
        assertNull(dto.getNome());
        assertNull(dto.getDescricao());
        assertNull(dto.getPreco());
        assertNull(dto.getQuantidadeEstoque());
        assertNull(dto.getMetodoPgto());
    }

    @Test
    @DisplayName("Deve criar objeto com id null via builder")
    void deveCriarComIdNull() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .id(null).nome(NOME).descricao(DESCRICAO)
                .preco(PRECO).quantidadeEstoque(ESTOQUE).metodoPgto("PIX").build();

        assertNull(dto.getId());
        assertNotNull(dto.getNome());
    }

    @Test
    @DisplayName("Deve criar objeto com nome null via builder")
    void deveCriarComNomeNull() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .id(ID).nome(null).descricao(DESCRICAO)
                .preco(PRECO).quantidadeEstoque(ESTOQUE).metodoPgto("PIX").build();

        assertNull(dto.getNome());
    }

    @Test
    @DisplayName("Deve criar objeto com descricao null via builder")
    void deveCriarComDescricaoNull() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .id(ID).nome(NOME).descricao(null)
                .preco(PRECO).quantidadeEstoque(ESTOQUE).metodoPgto("PIX").build();

        assertNull(dto.getDescricao());
    }

    @Test
    @DisplayName("Deve criar objeto com preco null via builder")
    void deveCriarComPrecoNull() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .id(ID).nome(NOME).descricao(DESCRICAO)
                .preco(null).quantidadeEstoque(ESTOQUE).metodoPgto("PIX").build();

        assertNull(dto.getPreco());
    }

    @Test
    @DisplayName("Deve criar objeto com quantidadeEstoque null via builder")
    void deveCriarComQuantidadeEstoqueNull() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .id(ID).nome(NOME).descricao(DESCRICAO)
                .preco(PRECO).quantidadeEstoque(null).metodoPgto("PIX").build();

        assertNull(dto.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve criar objeto com metodoPgto null via builder")
    void deveCriarComMetodoPgtoNull() {
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder()
                .id(ID).nome(NOME).descricao(DESCRICAO)
                .preco(PRECO).quantidadeEstoque(ESTOQUE).metodoPgto(null).build();

        assertNull(dto.getMetodoPgto());
    }

    // -------------------------------------------------------------------------
    // Valores inválidos / extremos
    // -------------------------------------------------------------------------

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
    @DisplayName("Deve aceitar preco muito grande")
    void deveAceitarPrecoMuitoGrande() {
        BigDecimal precoGrande = new BigDecimal("999999999.99");
        ProdutoResponseDTO dto = ProdutoResponseDTO.builder().preco(precoGrande).build();
        assertEquals(precoGrande, dto.getPreco());
    }

    // -------------------------------------------------------------------------
    // Setters — atribuição de nulos
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve aceitar null em todos os setters")
    void deveAceitarNullEmTodosOsSetters() {
        ProdutoResponseDTO dto = new ProdutoResponseDTO(ID, NOME, DESCRICAO, PRECO, ESTOQUE, "PIX");

        dto.setId(null);
        dto.setNome(null);
        dto.setDescricao(null);
        dto.setPreco(null);
        dto.setQuantidadeEstoque(null);
        dto.setMetodoPgto(null);

        assertNull(dto.getId());
        assertNull(dto.getNome());
        assertNull(dto.getDescricao());
        assertNull(dto.getPreco());
        assertNull(dto.getQuantidadeEstoque());
        assertNull(dto.getMetodoPgto());
    }

    // -------------------------------------------------------------------------
    // equals / hashCode — objetos diferentes
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Objetos com id diferentes não devem ser iguais")
    void objetosComIdDiferentesNaoDevemSerIguais() {
        ProdutoResponseDTO dto1 = new ProdutoResponseDTO(1L, NOME, DESCRICAO, PRECO, ESTOQUE, "PIX");
        ProdutoResponseDTO dto2 = new ProdutoResponseDTO(2L, NOME, DESCRICAO, PRECO, ESTOQUE, "PIX");
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objetos com nome diferentes não devem ser iguais")
    void objetosComNomeDiferentesNaoDevemSerIguais() {
        ProdutoResponseDTO dto1 = new ProdutoResponseDTO(ID, "Notebook", DESCRICAO, PRECO, ESTOQUE, "PIX");
        ProdutoResponseDTO dto2 = new ProdutoResponseDTO(ID, "Mouse", DESCRICAO, PRECO, ESTOQUE, "PIX");
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objetos com preco diferentes não devem ser iguais")
    void objetosComPrecoDiferentesNaoDevemSerIguais() {
        ProdutoResponseDTO dto1 = new ProdutoResponseDTO(ID, NOME, DESCRICAO, new BigDecimal("100.00"), ESTOQUE, "PIX");
        ProdutoResponseDTO dto2 = new ProdutoResponseDTO(ID, NOME, DESCRICAO, new BigDecimal("200.00"), ESTOQUE, "PIX");
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objetos com metodoPgto diferentes não devem ser iguais")
    void objetosComMetodoPgtoDiferentesNaoDevemSerIguais() {
        ProdutoResponseDTO dto1 = new ProdutoResponseDTO(ID, NOME, DESCRICAO, PRECO, ESTOQUE, "PIX");
        ProdutoResponseDTO dto2 = new ProdutoResponseDTO(ID, NOME, DESCRICAO, PRECO, ESTOQUE, "CREDITO");
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objetos com quantidadeEstoque diferentes não devem ser iguais")
    void objetosComEstoqueDiferentesNaoDevemSerIguais() {
        ProdutoResponseDTO dto1 = new ProdutoResponseDTO(ID, NOME, DESCRICAO, PRECO, 10, "PIX");
        ProdutoResponseDTO dto2 = new ProdutoResponseDTO(ID, NOME, DESCRICAO, PRECO, 99, "PIX");
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objeto não deve ser igual a null")
    void objetoNaoDeveSerIgualANull() {
        ProdutoResponseDTO dto = new ProdutoResponseDTO(ID, NOME, DESCRICAO, PRECO, ESTOQUE, "PIX");
        assertNotEquals(null, dto);
    }

    @Test
    @DisplayName("Objeto não deve ser igual a tipo diferente")
    void objetoNaoDeveSerIgualATipoDiferente() {
        ProdutoResponseDTO dto = new ProdutoResponseDTO(ID, NOME, DESCRICAO, PRECO, ESTOQUE, "PIX");
        assertNotEquals("string", dto);
    }
}