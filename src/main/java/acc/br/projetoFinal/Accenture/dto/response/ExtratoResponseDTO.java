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
    private Long contaId;
    private Long pedidoId;
    private Long pagamentoId;
    private String tipo;
    private BigDecimal valor;
    private BigDecimal saldoAntes;
    private BigDecimal saldoDepois;
    private String descricao;
    private LocalDateTime dataHora;

    public static ExtratoResponseDTO fromEntity(Extrato extrato) {
        return ExtratoResponseDTO.builder()
            .id(extrato.getId())
            .contaId(extrato.getConta() != null ? extrato.getConta().getId() : null)
            .pedidoId(extrato.getPedido() != null ? extrato.getPedido().getId() : null)
            .pagamentoId(extrato.getPagamento() != null ? extrato.getPagamento().getId() : null)
            .tipo(extrato.getTipo().name())
            .valor(extrato.getValor())
            .saldoAntes(extrato.getSaldoAntes())
            .saldoDepois(extrato.getSaldoDepois())
            .descricao(extrato.getDescricao())
            .dataHora(extrato.getDataHora())
            .build();
    }
}