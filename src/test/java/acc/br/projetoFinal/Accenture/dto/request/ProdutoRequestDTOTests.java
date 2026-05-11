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

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DisplayName("ProdutoRequestDTO - Testes Positivos")
class ProdutoRequestDTOTests {

    @Autowired
    private Validator validator;

    private ProdutoRequestDTO dto;

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
    @DisplayName("Deve validar ProdutoRequestDTO sem descrição")
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
    @DisplayName("Deve validar ProdutoRequestDTO com nome mínimo")
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
    @DisplayName("Deve validar ProdutoRequestDTO com descrição máxima")
    void deveValidarComDescricaoMaxima() {
        String descricaoMaxima = "a".repeat(500);
        dto = ProdutoRequestDTO.builder()
                .nome("Produto")
                .descricao(descricaoMaxima)
                .preco(new BigDecimal("100.00"))
                .quantidade(5)
                .metodoPgto(MetodoPagamento.CREDITO)
                .build();

        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }
}
