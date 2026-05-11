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

    @Test
    @DisplayName("Deve rejeitar quando nome é vazio")
    void deveRejeitar_NomeVazio() {
        dto = ProdutoRequestDTO.builder()
                .nome("")
                .preco(new BigDecimal("100.00"))
                .quantidade(10)
                .metodoPgto(MetodoPagamento.CREDITO)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para nome vazio");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    @DisplayName("Deve rejeitar quando nome é null")
    void deveRejeitar_NomeNull() {
        dto = ProdutoRequestDTO.builder()
                .nome(null)
                .preco(new BigDecimal("100.00"))
                .quantidade(10)
                .metodoPgto(MetodoPagamento.CREDITO)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para nome null");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    @DisplayName("Deve rejeitar quando nome tem menos de 3 caracteres")
    void deveRejeitar_NomeMenor3Caracteres() {
        dto = ProdutoRequestDTO.builder()
                .nome("AB")
                .preco(new BigDecimal("100.00"))
                .quantidade(10)
                .metodoPgto(MetodoPagamento.CREDITO)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para nome com menos de 3 caracteres");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    @DisplayName("Deve rejeitar quando nome excede 100 caracteres")
    void deveRejeitar_NomeMaior100Caracteres() {
        String nomeLongo = "a".repeat(101);
        dto = ProdutoRequestDTO.builder()
                .nome(nomeLongo)
                .preco(new BigDecimal("100.00"))
                .quantidade(10)
                .metodoPgto(MetodoPagamento.CREDITO)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para nome com mais de 100 caracteres");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    @DisplayName("Deve rejeitar quando descrição excede 500 caracteres")
    void deveRejeitar_DescricaoMaior500Caracteres() {
        String descricaoLonga = "a".repeat(501);
        dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .descricao(descricaoLonga)
                .preco(new BigDecimal("100.00"))
                .quantidade(10)
                .metodoPgto(MetodoPagamento.CREDITO)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para descrição com mais de 500 caracteres");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("descricao")));
    }

    @Test
    @DisplayName("Deve rejeitar quando preco é null")
    void deveRejeitar_PrecoNull() {
        dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .preco(null)
                .quantidade(10)
                .metodoPgto(MetodoPagamento.CREDITO)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para preco null");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("preco")));
    }

    @Test
    @DisplayName("Deve rejeitar quando preco é menor ou igual a zero")
    void deveRejeitar_PrecoMenorIgualZero() {
        dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .preco(new BigDecimal("0.00"))
                .quantidade(10)
                .metodoPgto(MetodoPagamento.CREDITO)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para preco menor ou igual a 0");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("preco")));
    }

    @Test
    @DisplayName("Deve rejeitar quando preco é negativo")
    void deveRejeitar_PrecoNegativo() {
        dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .preco(new BigDecimal("-100.00"))
                .quantidade(10)
                .metodoPgto(MetodoPagamento.CREDITO)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para preco negativo");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("preco")));
    }

    @Test
    @DisplayName("Deve rejeitar quando quantidade é null")
    void deveRejeitar_QuantidadeNull() {
        dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .preco(new BigDecimal("100.00"))
                .quantidade(null)
                .metodoPgto(MetodoPagamento.CREDITO)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para quantidade null");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quantidade")));
    }

    @Test
    @DisplayName("Deve rejeitar quando quantidade é negativa")
    void deveRejeitar_QuantidadeNegativa() {
        dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .preco(new BigDecimal("100.00"))
                .quantidade(-5)
                .metodoPgto(MetodoPagamento.CREDITO)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para quantidade negativa");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quantidade")));
    }

    @Test
    @DisplayName("Deve rejeitar quando metodoPgto é null")
    void deveRejeitar_MetodoPgtoNull() {
        dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .preco(new BigDecimal("100.00"))
                .quantidade(10)
                .metodoPgto(null)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para metodoPgto null");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("metodoPgto")));
    }
}
