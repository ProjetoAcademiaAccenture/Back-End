package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.model.Extrato;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtratoResponseDTO {

    private Long id;
    private String tipo;
    private BigDecimal valor;
    private BigDecimal saldoAntes;
    private BigDecimal saldoDepois;
    private String descricao;
    private Long pedidoId;
    private LocalDateTime dataHora;

    public static ExtratoResponseDTO fromEntity(Extrato extrato) {
        return ExtratoResponseDTO.builder()
                .id(extrato.getId())
                .tipo(extrato.getTipo().name())
                .valor(extrato.getValor())
                .saldoAntes(extrato.getSaldoAntes())
                .saldoDepois(extrato.getSaldoDepois())
                .descricao(extrato.getDescricao())
                .pedidoId(extrato.getPedido() != null ? extrato.getPedido().getId() : null)
                .dataHora(extrato.getDataHora())
                .build();
    }
}
