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
@DisplayName("PedidoRequestDTO - Testes Negativos")
class PedidoRequestDTONegativeTests {

    @Autowired
    private Validator validator;

    private PedidoRequestDTO dto;

    @Test
    @DisplayName("Deve rejeitar quando clienteId é null")
    void deveRejeitar_ClienteIdNull() {
        ItemPedidoRequestDTO item = ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(5)
                .build();

        List<ItemPedidoRequestDTO> itens = new ArrayList<>();
        itens.add(item);

        dto = PedidoRequestDTO.builder()
                .clienteId(null)
                .itens(itens)
                .build();

        Set<ConstraintViolation<PedidoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para clienteId null");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("clienteId")));
    }

    @Test
    @DisplayName("Deve rejeitar quando itens é vazio")
    void deveRejeitar_ItensVazio() {
        List<ItemPedidoRequestDTO> itens = new ArrayList<>();

        dto = PedidoRequestDTO.builder()
                .clienteId(1L)
                .itens(itens)
                .build();

        Set<ConstraintViolation<PedidoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para itens vazio");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("itens")));
    }

    @Test
    @DisplayName("Deve rejeitar quando itens é null")
    void deveRejeitar_ItensNull() {
        dto = PedidoRequestDTO.builder()
                .clienteId(1L)
                .itens(null)
                .build();

        Set<ConstraintViolation<PedidoRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver violação para itens null");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("itens")));
    }
}
