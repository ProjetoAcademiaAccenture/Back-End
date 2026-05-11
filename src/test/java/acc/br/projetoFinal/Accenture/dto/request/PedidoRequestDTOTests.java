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

    private PedidoRequestDTO dto;

    @Test
    @DisplayName("Deve validar PedidoRequestDTO com todos os dados válidos")
    void deveValidarComDadosValidos() {
        ItemPedidoRequestDTO item = ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(5)
                .build();

        List<ItemPedidoRequestDTO> itens = new ArrayList<>();
        itens.add(item);

        dto = PedidoRequestDTO.builder()
                .clienteId(1L)
                .itens(itens)
                .build();

        Set<ConstraintViolation<PedidoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve validar PedidoRequestDTO com múltiplos itens")
    void deveValidarComMultiplosItens() {
        List<ItemPedidoRequestDTO> itens = new ArrayList<>();
        itens.add(ItemPedidoRequestDTO.builder().produtoId(1L).quantidade(5).build());
        itens.add(ItemPedidoRequestDTO.builder().produtoId(2L).quantidade(10).build());
        itens.add(ItemPedidoRequestDTO.builder().produtoId(3L).quantidade(1).build());

        dto = PedidoRequestDTO.builder()
                .clienteId(1L)
                .itens(itens)
                .build();

        Set<ConstraintViolation<PedidoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve validar PedidoRequestDTO com um único item")
    void deveValidarComUnicoItem() {
        ItemPedidoRequestDTO item = ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(1)
                .build();

        List<ItemPedidoRequestDTO> itens = new ArrayList<>();
        itens.add(item);

        dto = PedidoRequestDTO.builder()
                .clienteId(1L)
                .itens(itens)
                .build();

        Set<ConstraintViolation<PedidoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }
}
