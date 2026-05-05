package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.model.Boleto;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoletoResponseDTO {

    private Long id;
    private String codigoBarras;
    private BigDecimal valor;
    private LocalDate dataVencimento;
    private String status;
    private Long pedidoId;

    public static BoletoResponseDTO fromEntity(Boleto boleto) {
        return BoletoResponseDTO.builder()
                .id(boleto.getId())
                .codigoBarras(boleto.getCodigoBarras())
                .valor(boleto.getValor())
                .dataVencimento(boleto.getDataVencimento())
                .status(boleto.getStatus().name())
                .pedidoId(boleto.getPedido().getId())
                .build();
    }
}
