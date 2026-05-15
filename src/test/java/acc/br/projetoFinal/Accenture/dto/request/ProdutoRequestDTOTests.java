package acc.br.projetoFinal.Accenture.dto.request;

import acc.br.projetoFinal.Accenture.enums.Categoria;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("ProdutoRequestDTO - Testes")
class ProdutoRequestDTOTests {

    @Autowired
    private Validator validator;

    // -------------------------------------------------------------------------
    // Helper — DTO válido completo
    // -------------------------------------------------------------------------

    private ProdutoRequestDTO dtoPadrao() {
        return ProdutoRequestDTO.builder()
                .nome("Notebook")
                .descricao("Notebook de alta performance")
                .preco(new BigDecimal("2999.99"))
                .urlImagem("https://imagens.loja.com/notebook.jpg")
                .quantidadeEstoque(10)
                .categoria(Categoria.ELETRONICOS)
                .build();
    }

    // =========================================================================
    // CONSTRUTORES
    // =========================================================================

    @Test
    @DisplayName("Deve criar objeto com construtor padrão — todos os campos nulos")
    void deveCriarComConstrutorPadrao() {
        ProdutoRequestDTO dto = new ProdutoRequestDTO();
        assertNotNull(dto);
        assertNull(dto.getNome());
        assertNull(dto.getDescricao());
        assertNull(dto.getPreco());
        assertNull(dto.getUrlImagem());
        assertNull(dto.getQuantidadeEstoque());
        assertNull(dto.getCategoria());
    }

    @Test
    @DisplayName("Deve criar objeto com @AllArgsConstructor (6 campos)")
    void deveCriarComConstrutorCompleto() {
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Teclado",
                "Teclado mecânico",
                new BigDecimal("350.00"),
                "https://imagens.loja.com/teclado.jpg",
                5,
                Categoria.ELETRONICOS
        );

        assertNotNull(dto);
        assertEquals("Teclado", dto.getNome());
        assertEquals("Teclado mecânico", dto.getDescricao());
        assertEquals(new BigDecimal("350.00"), dto.getPreco());
        assertEquals("https://imagens.loja.com/teclado.jpg", dto.getUrlImagem());
        assertEquals(5, dto.getQuantidadeEstoque());
        assertEquals(Categoria.ELETRONICOS, dto.getCategoria());
    }

    // =========================================================================
    // BUILDER
    // =========================================================================

    @Test
    @DisplayName("Deve criar objeto via builder com todos os campos")
    void deveCriarViaBuilderCompleto() {
        ProdutoRequestDTO dto = dtoPadrao();

        assertEquals("Notebook", dto.getNome());
        assertEquals("Notebook de alta performance", dto.getDescricao());
        assertEquals(new BigDecimal("2999.99"), dto.getPreco());
        assertEquals("https://imagens.loja.com/notebook.jpg", dto.getUrlImagem());
        assertEquals(10, dto.getQuantidadeEstoque());
        assertEquals(Categoria.ELETRONICOS, dto.getCategoria());
    }

    @Test
    @DisplayName("Deve criar objeto via builder vazio — todos os campos nulos")
    void deveCriarViaBuilderVazio() {
        ProdutoRequestDTO dto = ProdutoRequestDTO.builder().build();
        assertNotNull(dto);
        assertNull(dto.getNome());
        assertNull(dto.getDescricao());
        assertNull(dto.getPreco());
        assertNull(dto.getUrlImagem());
        assertNull(dto.getQuantidadeEstoque());
        assertNull(dto.getCategoria());
    }

    // =========================================================================
    // SETTERS / GETTERS
    // =========================================================================

    @Test
    @DisplayName("Deve definir e obter todos os campos via setters")
    void deveDefinirEObterCamposViaSetters() {
        ProdutoRequestDTO dto = new ProdutoRequestDTO();

        dto.setNome("Mouse");
        dto.setDescricao("Mouse sem fio");
        dto.setPreco(new BigDecimal("89.90"));
        dto.setUrlImagem("https://imagens.loja.com/mouse.jpg");
        dto.setQuantidadeEstoque(50);
        dto.setCategoria(Categoria.ELETRONICOS);

        assertEquals("Mouse", dto.getNome());
        assertEquals("Mouse sem fio", dto.getDescricao());
        assertEquals(new BigDecimal("89.90"), dto.getPreco());
        assertEquals("https://imagens.loja.com/mouse.jpg", dto.getUrlImagem());
        assertEquals(50, dto.getQuantidadeEstoque());
        assertEquals(Categoria.ELETRONICOS, dto.getCategoria());
    }

    // =========================================================================
    // VALIDAÇÃO — cenários válidos (zero violations)
    // =========================================================================

    @Test
    @DisplayName("Deve passar validação com todos os campos válidos")
    void deveValidarComDadosValidos() {
        assertTrue(validator.validate(dtoPadrao()).isEmpty());
    }

    @Test
    @DisplayName("Deve passar validação sem descrição e sem urlImagem (campos opcionais)")
    void deveValidarSemCamposOpcionais() {
        ProdutoRequestDTO dto = ProdutoRequestDTO.builder()
                .nome("Notebook")
                .preco(new BigDecimal("2999.99"))
                .quantidadeEstoque(10)
                .categoria(Categoria.ELETRONICOS)
                .build();

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Deve passar validação com quantidadeEstoque zero (mínimo permitido)")
    void deveValidarComQuantidadeZero() {
        ProdutoRequestDTO dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(0)
                .categoria(Categoria.ELETRONICOS)
                .build();

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Deve passar validação com nome no limite mínimo (3 caracteres)")
    void deveValidarComNomeMinimo() {
        ProdutoRequestDTO dto = ProdutoRequestDTO.builder()
                .nome("ABC")
                .preco(new BigDecimal("0.01"))
                .quantidadeEstoque(0)
                .categoria(Categoria.ELETRONICOS)
                .build();

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Deve passar validação com nome no limite máximo (100 caracteres)")
    void deveValidarComNomeMaximo() {
        ProdutoRequestDTO dto = ProdutoRequestDTO.builder()
                .nome("A".repeat(100))
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(1)
                .categoria(Categoria.ELETRONICOS)
                .build();

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Deve passar validação com descrição no limite máximo (500 caracteres)")
    void deveValidarComDescricaoMaxima() {
        ProdutoRequestDTO dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .descricao("a".repeat(500))
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(5)
                .categoria(Categoria.ELETRONICOS)
                .build();

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Deve passar validação com preço mínimo permitido (0.01)")
    void deveValidarComPrecoMinimo() {
        ProdutoRequestDTO dto = ProdutoRequestDTO.builder()
                .nome("Produto Barato")
                .preco(new BigDecimal("0.01"))
                .quantidadeEstoque(1)
                .categoria(Categoria.ELETRONICOS)
                .build();

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Deve passar validação com urlImagem no limite máximo (500 caracteres)")
    void deveValidarComUrlImagemMaxima() {
        ProdutoRequestDTO dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .preco(new BigDecimal("10.00"))
                .quantidadeEstoque(1)
                .urlImagem("https://x.com/" + "a".repeat(484)) // total = 500
                .categoria(Categoria.ELETRONICOS)
                .build();

        assertTrue(validator.validate(dto).isEmpty());
    }

    // =========================================================================
    // VALIDAÇÃO — cenários inválidos (deve gerar violations)
    // =========================================================================

    @Test
    @DisplayName("Deve falhar quando nome for nulo")
    void deveFalharNomeNulo() {
        ProdutoRequestDTO dto = ProdutoRequestDTO.builder()
                .nome(null)
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(1)
                .categoria(Categoria.ELETRONICOS)
                .build();

        assertViolation(dto, "nome");
    }

    @Test
    @DisplayName("Deve falhar quando nome for vazio")
    void deveFalharNomeVazio() {
        ProdutoRequestDTO dto = ProdutoRequestDTO.builder()
                .nome("")
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(1)
                .categoria(Categoria.ELETRONICOS)
                .build();

        assertViolation(dto, "nome");
    }

    @Test
    @DisplayName("Deve falhar quando nome tiver menos de 3 caracteres")
    void deveFalharNomeMenorQueMinimo() {
        ProdutoRequestDTO dto = ProdutoRequestDTO.builder()
                .nome("AB")
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(1)
                .categoria(Categoria.ELETRONICOS)
                .build();

        assertViolation(dto, "nome");
    }

    @Test
    @DisplayName("Deve falhar quando nome tiver mais de 100 caracteres")
    void deveFalharNomeMaiorQueMaximo() {
        ProdutoRequestDTO dto = ProdutoRequestDTO.builder()
                .nome("A".repeat(101))
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(1)
                .categoria(Categoria.ELETRONICOS)
                .build();

        assertViolation(dto, "nome");
    }

    @Test
    @DisplayName("Deve falhar quando descrição tiver mais de 500 caracteres")
    void deveFalharDescricaoMaiorQueMaximo() {
        ProdutoRequestDTO dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .descricao("a".repeat(501))
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(1)
                .categoria(Categoria.ELETRONICOS)
                .build();

        assertViolation(dto, "descricao");
    }

    @Test
    @DisplayName("Deve falhar quando preço for nulo")
    void deveFalharPrecoNulo() {
        ProdutoRequestDTO dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .preco(null)
                .quantidadeEstoque(1)
                .categoria(Categoria.ELETRONICOS)
                .build();

        assertViolation(dto, "preco");
    }

    @Test
    @DisplayName("Deve falhar quando preço for zero")
    void deveFalharPrecoZero() {
        ProdutoRequestDTO dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .preco(new BigDecimal("0.00"))
                .quantidadeEstoque(1)
                .categoria(Categoria.ELETRONICOS)
                .build();

        assertViolation(dto, "preco");
    }

    @Test
    @DisplayName("Deve falhar quando preço for negativo")
    void deveFalharPrecoNegativo() {
        ProdutoRequestDTO dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .preco(new BigDecimal("-1.00"))
                .quantidadeEstoque(1)
                .categoria(Categoria.ELETRONICOS)
                .build();

        assertViolation(dto, "preco");
    }

    @Test
    @DisplayName("Deve falhar quando urlImagem tiver mais de 500 caracteres")
    void deveFalharUrlImagemMaiorQueMaximo() {
        ProdutoRequestDTO dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .preco(new BigDecimal("10.00"))
                .urlImagem("https://x.com/" + "a".repeat(487)) // total = 501
                .quantidadeEstoque(1)
                .categoria(Categoria.ELETRONICOS)
                .build();

        assertViolation(dto, "urlImagem");
    }

    @Test
    @DisplayName("Deve falhar quando quantidadeEstoque for nula")
    void deveFalharQuantidadeNula() {
        ProdutoRequestDTO dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(null)
                .categoria(Categoria.ELETRONICOS)
                .build();

        assertViolation(dto, "quantidadeEstoque");
    }

    @Test
    @DisplayName("Deve falhar quando quantidadeEstoque for negativa")
    void deveFalharQuantidadeNegativa() {
        ProdutoRequestDTO dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(-1)
                .categoria(Categoria.ELETRONICOS)
                .build();

        assertViolation(dto, "quantidadeEstoque");
    }

    @Test
    @DisplayName("Deve falhar quando categoria for nula")
    void deveFalharCategoriaNula() {
        ProdutoRequestDTO dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(1)
                .categoria(null)
                .build();

        assertViolation(dto, "categoria");
    }

    @Test
    @DisplayName("Deve acumular violations quando todos os campos obrigatórios forem nulos")
    void deveFalharComTodosOsCamposObrigatoriosNulos() {
        ProdutoRequestDTO dto = new ProdutoRequestDTO();
        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        // nome (@NotBlank) + preco (@NotNull) + quantidadeEstoque (@NotNull) + categoria (@NotNull) = 4
        assertEquals(4, violations.size());
    }

    // =========================================================================
    // equals / hashCode
    // =========================================================================

    @Test
    @DisplayName("Dois objetos com mesmos valores devem ser iguais")
    void doisObjetosComMesmosValoresDevemSerIguais() {
        ProdutoRequestDTO dto1 = dtoPadrao();
        ProdutoRequestDTO dto2 = dtoPadrao();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Objeto deve ser igual a si mesmo")
    void objetoDeveSerIgualASiMesmo() {
        ProdutoRequestDTO dto = dtoPadrao();
        assertEquals(dto, dto);
    }

    @Test
    @DisplayName("Dois objetos padrão (sem campos) devem ser iguais")
    void doisObjetosPadraoDevemSerIguais() {
        assertEquals(new ProdutoRequestDTO(), new ProdutoRequestDTO());
    }

    @Test
    @DisplayName("Objetos com categoria diferente não devem ser iguais")
    void objetosComCategoriaDiferenteNaoDevemSerIguais() {
        ProdutoRequestDTO dto1 = dtoPadrao();
        ProdutoRequestDTO dto2 = ProdutoRequestDTO.builder()
                .nome("Notebook")
                .descricao("Notebook de alta performance")
                .preco(new BigDecimal("2999.99"))
                .urlImagem("https://imagens.loja.com/notebook.jpg")
                .quantidadeEstoque(10)
                .categoria(Categoria.PERIFERICOS) // categoria diferente
                .build();

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objeto não deve ser igual a null")
    void objetoNaoDeveSerIgualANull() {
        assertNotEquals(null, dtoPadrao());
    }

    @Test
    @DisplayName("Objeto não deve ser igual a outro tipo")
    void objetoNaoDeveSerIgualAOutroTipo() {
        assertNotEquals("string", dtoPadrao());
    }

    // =========================================================================
    // toString
    // =========================================================================

    @Test
    @DisplayName("toString não deve ser nulo")
    void toStringNaoDeveSerNulo() {
        assertNotNull(new ProdutoRequestDTO().toString());
    }

    @Test
    @DisplayName("toString deve conter os valores dos campos principais")
    void toStringDeveConterCamposPrincipais() {
        ProdutoRequestDTO dto = dtoPadrao();
        String str = dto.toString();
        assertTrue(str.contains("Notebook"));
        assertTrue(str.contains("2999.99"));
        assertTrue(str.contains("ELETRONICOS"));
    }

    // =========================================================================
    // Helpers internos
    // =========================================================================

    private void assertViolation(ProdutoRequestDTO dto, String campo) {
        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Esperava violations para o campo: " + campo);
        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals(campo)),
                "Esperava violation no campo '" + campo + "', mas violations foram: " + violations
        );
    }
}