package acc.br.projetoFinal.Accenture.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("PedidoRequestDTO - Testes Positivos")
class PedidoRequestDTOTests {

    @Autowired
    private Validator validator;

    private static List<ItemPedidoRequestDTO> itenValido() {
        return new ArrayList<>(List.of(
                ItemPedidoRequestDTO.builder().produtoId(1L).quantidade(5).build()
        ));
    }

    // -------------------------------------------------------------------------
    // Construtores
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve criar objeto com construtor padrão")
    void deveCriarComConstrutorPadrao() {
        PedidoRequestDTO dto = new PedidoRequestDTO();
        assertNotNull(dto);
        assertNull(dto.getClienteId());
        assertNull(dto.getItens());
    }

    @Test
    @DisplayName("Deve criar objeto com construtor completo")
    void deveCriarComConstrutorCompleto() {
        List<ItemPedidoRequestDTO> itens = itenValido();
        PedidoRequestDTO dto = new PedidoRequestDTO(1L, itens);

        assertEquals(1L, dto.getClienteId());
        assertEquals(itens, dto.getItens());
    }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve criar objeto via builder com todos os campos")
    void deveCriarViaBuilderCompleto() {
        List<ItemPedidoRequestDTO> itens = itenValido();

        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(1L)
                .itens(itens)
                .build();

        assertEquals(1L, dto.getClienteId());
        assertEquals(itens, dto.getItens());
    }

    @Test
    @DisplayName("Deve criar objeto via builder vazio")
    void deveCriarViaBuilderVazio() {
        PedidoRequestDTO dto = PedidoRequestDTO.builder().build();

        assertNotNull(dto);
        assertNull(dto.getClienteId());
        assertNull(dto.getItens());
    }

    // -------------------------------------------------------------------------
    // Setters e Getters
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve definir e obter clienteId via setter")
    void deveDefinirClienteId() {
        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setClienteId(10L);
        assertEquals(10L, dto.getClienteId());
    }

    @Test
    @DisplayName("Deve definir e obter itens via setter")
    void deveDefinirItens() {
        PedidoRequestDTO dto = new PedidoRequestDTO();
        List<ItemPedidoRequestDTO> itens = itenValido();
        dto.setItens(itens);
        assertEquals(itens, dto.getItens());
    }

    // -------------------------------------------------------------------------
    // Validação — cenários válidos
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve validar com um único item válido")
    void deveValidarComUmItem() {
        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(1L)
                .itens(itenValido())
                .build();

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Deve validar com múltiplos itens válidos")
    void deveValidarComMultiplosItens() {
        List<ItemPedidoRequestDTO> itens = new ArrayList<>(List.of(
                ItemPedidoRequestDTO.builder().produtoId(1L).quantidade(5).build(),
                ItemPedidoRequestDTO.builder().produtoId(2L).quantidade(10).build(),
                ItemPedidoRequestDTO.builder().produtoId(3L).quantidade(1).build()
        ));

        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(1L)
                .itens(itens)
                .build();

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Deve validar com clienteId grande")
    void deveValidarComClienteIdGrande() {
        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(999999999L)
                .itens(itenValido())
                .build();

        assertTrue(validator.validate(dto).isEmpty());
    }

    // -------------------------------------------------------------------------
    // equals / hashCode
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Dois objetos com mesmos valores devem ser iguais")
    void doisObjetosComMesmosValoresDevemSerIguais() {
        List<ItemPedidoRequestDTO> itens1 = itenValido();
        List<ItemPedidoRequestDTO> itens2 = itenValido();

        PedidoRequestDTO dto1 = new PedidoRequestDTO(1L, itens1);
        PedidoRequestDTO dto2 = new PedidoRequestDTO(1L, itens2);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Objeto deve ser igual a si mesmo")
    void objetoDeveSerIgualASiMesmo() {
        PedidoRequestDTO dto = new PedidoRequestDTO(1L, itenValido());
        assertEquals(dto, dto);
    }

    @Test
    @DisplayName("Dois objetos padrão devem ser iguais")
    void doisObjetosPadraoDevemSerIguais() {
        PedidoRequestDTO dto1 = new PedidoRequestDTO();
        PedidoRequestDTO dto2 = new PedidoRequestDTO();
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("toString não deve ser nulo")
    void toStringNaoDeveSerNulo() {
        assertNotNull(new PedidoRequestDTO().toString());
    }

    @Test
    @DisplayName("toString deve conter os valores dos campos")
    void toStringDeveConterValores() {
        PedidoRequestDTO dto = new PedidoRequestDTO(1L, itenValido());
        assertTrue(dto.toString().contains("1"));
    }
}