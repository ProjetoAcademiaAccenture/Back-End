package acc.br.projetoFinal.Accenture.dto.request;

import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
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
@DisplayName("ProdutoRequestDTO - Testes Positivos")
class ProdutoRequestDTOTests {

    @Autowired
    private Validator validator;

    private ProdutoRequestDTO dto;

    // ─────────────────────────────────────────────
    //  Cenários positivos (sem violações esperadas)
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("Deve validar ProdutoRequestDTO com todos os dados válidos")
    void deveValidarComDadosValidos() {
        dto = ProdutoRequestDTO.builder()
                .nome("Notebook")
                .descricao("Notebook de alta performance")
                .preco(new BigDecimal("2999.99"))
                .quantidade(10)
                .metodoPgto(MetodoPagamento.CREDITO)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve validar ProdutoRequestDTO com quantidade zero")
    void deveValidarComQuantidadeZero() {
        dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .descricao("Descrição")
                .preco(new BigDecimal("100.00"))
                .quantidade(0)
                .metodoPgto(MetodoPagamento.DEBITO)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve validar ProdutoRequestDTO sem descrição (campo opcional)")
    void deveValidarSemDescricao() {
        dto = ProdutoRequestDTO.builder()
                .nome("Notebook")
                .preco(new BigDecimal("2999.99"))
                .quantidade(10)
                .metodoPgto(MetodoPagamento.PIX)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve validar ProdutoRequestDTO com nome no limite mínimo (3 caracteres)")
    void deveValidarComNomeMinimo() {
        dto = ProdutoRequestDTO.builder()
                .nome("ABC")
                .preco(new BigDecimal("0.01"))
                .quantidade(0)
                .metodoPgto(MetodoPagamento.BOLETO)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve validar ProdutoRequestDTO com nome no limite máximo (100 caracteres)")
    void deveValidarComNomeMaximo() {
        dto = ProdutoRequestDTO.builder()
                .nome("A".repeat(100))
                .preco(new BigDecimal("100.00"))
                .quantidade(1)
                .metodoPgto(MetodoPagamento.CREDITO)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve validar ProdutoRequestDTO com descrição no limite máximo (500 caracteres)")
    void deveValidarComDescricaoMaxima() {
        dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .descricao("a".repeat(500))
                .preco(new BigDecimal("100.00"))
                .quantidade(5)
                .metodoPgto(MetodoPagamento.CREDITO)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve validar ProdutoRequestDTO com preço mínimo permitido (0.01)")
    void deveValidarComPrecoMinimo() {
        dto = ProdutoRequestDTO.builder()
                .nome("Produto Barato")
                .preco(new BigDecimal("0.01"))
                .quantidade(1)
                .metodoPgto(MetodoPagamento.PIX)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve validar com metodoPgto BOLETO")
    void deveValidarComMetodoPgtoBoleto() {
        dto = ProdutoRequestDTO.builder()
                .nome("Produto Boleto")
                .preco(new BigDecimal("150.00"))
                .quantidade(2)
                .metodoPgto(MetodoPagamento.BOLETO)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações para metodoPgto BOLETO");
    }

    @Test
    @DisplayName("Deve validar com metodoPgto DEBITO")
    void deveValidarComMetodoPgtoDebito() {
        dto = ProdutoRequestDTO.builder()
                .nome("Produto Débito")
                .preco(new BigDecimal("200.00"))
                .quantidade(3)
                .metodoPgto(MetodoPagamento.DEBITO)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações para metodoPgto DEBITO");
    }

    // ─────────────────────────────────────────────
    //  Cobertura Lombok: getters, setters, construtores,
    //  equals, hashCode, toString
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar os valores corretos pelos getters")
    void deveRetornarValoresCorretosPelosGetters() {
        dto = ProdutoRequestDTO.builder()
                .nome("Notebook")
                .descricao("Alta performance")
                .preco(new BigDecimal("2999.99"))
                .quantidade(10)
                .metodoPgto(MetodoPagamento.CREDITO)
                .build();

        assertEquals("Notebook", dto.getNome());
        assertEquals("Alta performance", dto.getDescricao());
        assertEquals(new BigDecimal("2999.99"), dto.getPreco());
        assertEquals(10, dto.getQuantidade());
        assertEquals(MetodoPagamento.CREDITO, dto.getMetodoPgto());
    }

    @Test
    @DisplayName("Deve instanciar via construtor sem args e permitir setters")
    void deveInstanciarViaConstrutorSemArgs() {
        dto = new ProdutoRequestDTO();
        dto.setNome("Mouse");
        dto.setDescricao("Mouse sem fio");
        dto.setPreco(new BigDecimal("89.90"));
        dto.setQuantidade(50);
        dto.setMetodoPgto(MetodoPagamento.PIX);

        assertEquals("Mouse", dto.getNome());
        assertEquals("Mouse sem fio", dto.getDescricao());
        assertEquals(new BigDecimal("89.90"), dto.getPreco());
        assertEquals(50, dto.getQuantidade());
        assertEquals(MetodoPagamento.PIX, dto.getMetodoPgto());
    }

    @Test
    @DisplayName("Deve instanciar via construtor com todos os args")
    void deveInstanciarViaConstrutorComTodosArgs() {
        dto = new ProdutoRequestDTO(
                "Teclado",
                "Teclado mecânico",
                new BigDecimal("350.00"),
                5,
                MetodoPagamento.CREDITO
        );

        assertNotNull(dto);
        assertEquals("Teclado", dto.getNome());
        assertEquals(MetodoPagamento.CREDITO, dto.getMetodoPgto());
    }

    @Test
    @DisplayName("Dois DTOs com mesmos dados devem ser iguais (equals/hashCode)")
    void doisDtosComMesmosDadosDevemSerIguais() {
        ProdutoRequestDTO dto1 = ProdutoRequestDTO.builder()
                .nome("Notebook")
                .descricao("Alta performance")
                .preco(new BigDecimal("2999.99"))
                .quantidade(10)
                .metodoPgto(MetodoPagamento.CREDITO)
                .build();

        ProdutoRequestDTO dto2 = ProdutoRequestDTO.builder()
                .nome("Notebook")
                .descricao("Alta performance")
                .preco(new BigDecimal("2999.99"))
                .quantidade(10)
                .metodoPgto(MetodoPagamento.CREDITO)
                .build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Dois DTOs com dados diferentes não devem ser iguais")
    void doisDtosComDadosDiferentesNaoDevemSerIguais() {
        ProdutoRequestDTO dto1 = ProdutoRequestDTO.builder()
                .nome("Notebook")
                .preco(new BigDecimal("2999.99"))
                .quantidade(10)
                .metodoPgto(MetodoPagamento.CREDITO)
                .build();

        ProdutoRequestDTO dto2 = ProdutoRequestDTO.builder()
                .nome("Mouse")
                .preco(new BigDecimal("89.90"))
                .quantidade(50)
                .metodoPgto(MetodoPagamento.PIX)
                .build();

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("toString deve conter os campos principais")
    void toStringDeveConterCamposPrincipais() {
        dto = ProdutoRequestDTO.builder()
                .nome("Notebook")
                .descricao("Alta performance")
                .preco(new BigDecimal("2999.99"))
                .quantidade(10)
                .metodoPgto(MetodoPagamento.CREDITO)
                .build();

        String result = dto.toString();
        assertNotNull(result);
        assertTrue(result.contains("Notebook"));
        assertTrue(result.contains("2999.99"));
        assertTrue(result.contains("CREDITO"));
    }

    @Test
    @DisplayName("Deve permitir alterar campos via setter após criação")
    void devePermitirAlterarCamposViaSetterAposCriacao() {
        dto = ProdutoRequestDTO.builder()
                .nome("Notebook")
                .preco(new BigDecimal("2999.99"))
                .quantidade(10)
                .metodoPgto(MetodoPagamento.CREDITO)
                .build();

        dto.setNome("Notebook Gamer");
        dto.setDescricao("Alta performance para jogos");
        dto.setPreco(new BigDecimal("4500.00"));
        dto.setQuantidade(3);
        dto.setMetodoPgto(MetodoPagamento.BOLETO);

        assertEquals("Notebook Gamer", dto.getNome());
        assertEquals("Alta performance para jogos", dto.getDescricao());
        assertEquals(new BigDecimal("4500.00"), dto.getPreco());
        assertEquals(3, dto.getQuantidade());
        assertEquals(MetodoPagamento.BOLETO, dto.getMetodoPgto());

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações após alteração válida");
    }
}