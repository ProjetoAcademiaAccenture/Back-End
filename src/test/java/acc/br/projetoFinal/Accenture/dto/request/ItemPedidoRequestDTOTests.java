package acc.br.projetoFinal.Accenture.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("ItemPedidoRequestDTO - Testes Positivos")
class ItemPedidoRequestDTOTests {

    @Autowired
    private Validator validator;

    // -------------------------------------------------------------------------
    // Construtor padrão
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve criar objeto com construtor padrão")
    void deveCriarComConstrutorPadrao() {
        ItemPedidoRequestDTO dto = new ItemPedidoRequestDTO();
        assertNotNull(dto);
    }

    // -------------------------------------------------------------------------
    // Construtor completo
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve criar objeto com construtor completo")
    void deveCriarComConstrutorCompleto() {
        ItemPedidoRequestDTO dto = new ItemPedidoRequestDTO(1L, 5);

        assertEquals(1L, dto.getProdutoId());
        assertEquals(5, dto.getQuantidade());
    }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve criar objeto via builder com todos os campos")
    void deveCriarViaBuilderCompleto() {
        ItemPedidoRequestDTO dto = ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(5)
                .build();

        assertEquals(1L, dto.getProdutoId());
        assertEquals(5, dto.getQuantidade());
    }

    @Test
    @DisplayName("Deve criar objeto via builder vazio")
    void deveCriarViaBuilderVazio() {
        ItemPedidoRequestDTO dto = ItemPedidoRequestDTO.builder().build();
        assertNotNull(dto);
        assertNull(dto.getProdutoId());
        assertNull(dto.getQuantidade());
    }

    // -------------------------------------------------------------------------
    // Setters e Getters
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve definir e obter produtoId via setter")
    void deveDefinirProdutoId() {
        ItemPedidoRequestDTO dto = new ItemPedidoRequestDTO();
        dto.setProdutoId(10L);
        assertEquals(10L, dto.getProdutoId());
    }

    @Test
    @DisplayName("Deve definir e obter quantidade via setter")
    void deveDefinirQuantidade() {
        ItemPedidoRequestDTO dto = new ItemPedidoRequestDTO();
        dto.setQuantidade(3);
        assertEquals(3, dto.getQuantidade());
    }

    // -------------------------------------------------------------------------
    // Validação — cenários válidos
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve validar com dados válidos")
    void deveValidarComDadosValidos() {
        ItemPedidoRequestDTO dto = ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(5)
                .build();

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Deve validar com quantidade mínima (1)")
    void deveValidarComQuantidadeMinima() {
        ItemPedidoRequestDTO dto = ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(1)
                .build();

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Deve validar com quantidade grande")
    void deveValidarComQuantidadeGrande() {
        ItemPedidoRequestDTO dto = ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(999999)
                .build();

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Deve validar com produtoId grande")
    void deveValidarComProdutoIdGrande() {
        ItemPedidoRequestDTO dto = ItemPedidoRequestDTO.builder()
                .produtoId(999999999L)
                .quantidade(5)
                .build();

        assertTrue(validator.validate(dto).isEmpty());
    }

    // -------------------------------------------------------------------------
    // equals / hashCode
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Dois objetos com mesmos valores devem ser iguais")
    void doisObjetosComMesmosValoresDevemSerIguais() {
        ItemPedidoRequestDTO dto1 = new ItemPedidoRequestDTO(1L, 5);
        ItemPedidoRequestDTO dto2 = new ItemPedidoRequestDTO(1L, 5);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Objeto deve ser igual a si mesmo")
    void objetoDeveSerIgualASiMesmo() {
        ItemPedidoRequestDTO dto = new ItemPedidoRequestDTO(1L, 5);
        assertEquals(dto, dto);
    }

    @Test
    @DisplayName("Dois objetos padrão devem ser iguais")
    void doisObjetosPadraoDevemSerIguais() {
        ItemPedidoRequestDTO dto1 = new ItemPedidoRequestDTO();
        ItemPedidoRequestDTO dto2 = new ItemPedidoRequestDTO();
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("toString não deve ser nulo")
    void toStringNaoDeveSerNulo() {
        assertNotNull(new ItemPedidoRequestDTO().toString());
    }

    @Test
    @DisplayName("toString deve conter os valores dos campos")
    void toStringDeveConterValores() {
        ItemPedidoRequestDTO dto = new ItemPedidoRequestDTO(1L, 5);
        String str = dto.toString();
        assertTrue(str.contains("1"));
        assertTrue(str.contains("5"));
    }
}