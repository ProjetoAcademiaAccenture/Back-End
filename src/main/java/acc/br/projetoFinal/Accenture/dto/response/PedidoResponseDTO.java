package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import acc.br.projetoFinal.Accenture.model.Pedido;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoResponseDTO {

    private Long id;
    private LocalDateTime dataCriacao;
    private StatusPedido status;
    private BigDecimal valorTotal;
    private BigDecimal multaCancelamento;
    private Long clienteId;
    private List<ItemPedidoResponseDTO> itens;

    public static PedidoResponseDTO fromEntity(Pedido pedido) {
        return PedidoResponseDTO.builder()
                .id(pedido.getId())
                .dataCriacao(pedido.getDataCriacao())
                .status(pedido.getStatus())
                .valorTotal(pedido.getValorTotal())
                .multaCancelamento(pedido.getMultaCancelamento())
                .clienteId(pedido.getCliente().getId())
                .itens(pedido.getItens() != null
                    ? pedido.getItens().stream()
                        .map(ItemPedidoResponseDTO::fromEntity)
                        .collect(Collectors.toList())
                    : List.of())
                .build();
    }
}
