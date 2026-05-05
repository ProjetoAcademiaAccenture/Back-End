package acc.br.projetoFinal.Accenture.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoRequestDTO {

    @NotNull(message = "ID do cliente é obrigatório")
    private Long clienteId;

    @NotEmpty(message = "Pedido deve ter pelo menos um item")
    private List<ItemPedidoRequestDTO> itens;
}
