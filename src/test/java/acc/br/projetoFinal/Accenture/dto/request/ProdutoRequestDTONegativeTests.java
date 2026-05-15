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
@DisplayName("ProdutoRequestDTO - Testes Negativos")
class ProdutoRequestDTONegativeTests {

    @Autowired
    private Validator validator;

    private ProdutoRequestDTO dto;

    // ─────────────────────────────────────────────
    //  nome
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("Deve rejeitar quando nome é null")
    void deveRejeitar_NomeNull() {
        dto = ProdutoRequestDTO.builder()
                .nome(null)
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(1)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para nome null");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    @DisplayName("Deve rejeitar quando nome é vazio")
    void deveRejeitar_NomeVazio() {
        dto = ProdutoRequestDTO.builder()
                .nome("")
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(1)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para nome vazio");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    @DisplayName("Deve rejeitar quando nome é apenas espaços em branco")
    void deveRejeitar_NomeBlank() {
        dto = ProdutoRequestDTO.builder()
                .nome("   ")
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(1)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para nome em branco");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    @DisplayName("Deve rejeitar quando nome tem menos de 3 caracteres")
    void deveRejeitar_NomeMenor3Caracteres() {
        dto = ProdutoRequestDTO.builder()
                .nome("AB")
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(1)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para nome com menos de 3 caracteres");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    @DisplayName("Deve rejeitar quando nome excede 100 caracteres")
    void deveRejeitar_NomeMaior100Caracteres() {
        dto = ProdutoRequestDTO.builder()
                .nome("A".repeat(101))
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(1)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para nome com mais de 100 caracteres");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    // ─────────────────────────────────────────────
    //  descricao
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("Deve rejeitar quando descricao excede 500 caracteres")
    void deveRejeitar_DescricaoMaior500Caracteres() {
        dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .descricao("a".repeat(501))
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(1)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para descricao com mais de 500 caracteres");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("descricao")));
    }

    // ─────────────────────────────────────────────
    //  preco
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("Deve rejeitar quando preco é null")
    void deveRejeitar_PrecoNull() {
        dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .preco(null)
                .quantidadeEstoque(1)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para preco null");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("preco")));
    }

    @Test
    @DisplayName("Deve rejeitar quando preco é zero")
    void deveRejeitar_PrecoZero() {
        dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .preco(new BigDecimal("0.00"))
                .quantidadeEstoque(1)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para preco igual a 0");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("preco")));
    }

    @Test
    @DisplayName("Deve rejeitar quando preco é negativo")
    void deveRejeitar_PrecoNegativo() {
        dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .preco(new BigDecimal("-0.01"))
                .quantidadeEstoque(1)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para preco negativo");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("preco")));
    }

    // ─────────────────────────────────────────────
    //  quantidade
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("Deve rejeitar quando quantidade é null")
    void deveRejeitar_QuantidadeNull() {
        dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(null)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para quantidade null");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quantidadeEstoque")));
    }

    @Test
    @DisplayName("Deve rejeitar quando quantidade é negativa")
    void deveRejeitar_QuantidadeNegativa() {
        dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(-1)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para quantidade negativa");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quantidadeEstoque")));
    }

    // ─────────────────────────────────────────────
    //  metodoPgto
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("Deve rejeitar quando metodoPgto é null")
    void deveRejeitar_MetodoPgtoNull() {
        dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(1)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para metodoPgto null");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("metodoPgto")));
    }

    // ─────────────────────────────────────────────
    //  Múltiplas violações simultâneas
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("Deve rejeitar DTO completamente inválido com múltiplas violações")
    void deveRejeitar_DtoCompletamenteInvalido() {
        dto = ProdutoRequestDTO.builder()
                .nome(null)
                .descricao("a".repeat(501))
                .preco(null)
                .quantidadeEstoque(null)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver múltiplas violações");
        assertTrue(violations.size() >= 4, "Devem existir ao menos 4 violações simultâneas");
    }
}