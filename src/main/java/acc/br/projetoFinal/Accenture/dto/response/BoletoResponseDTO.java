package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.model.Boleto;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoletoResponseDTO {

    private Long id;
    private String codigoBarras;
    private BigDecimal valor;
    private BigDecimal multaAtraso;
    private BigDecimal valorTotal;
    private LocalDate dataVencimento;
    private String status;
    private Long pagamentoId;
    private Long pedidoId;
    private boolean atrasado;

    public static BoletoResponseDTO fromEntity(Boleto boleto) {
        BigDecimal multa = BigDecimal.ZERO;

        boolean atrasado = boleto.getStatus() == acc.br.projetoFinal.Accenture.enums.StatusBoleto.PENDENTE
            && boleto.estaAtrasado();

        if (atrasado) {
            multa = boleto.getValor()
                .multiply(new BigDecimal("0.02"))
                .setScale(2, RoundingMode.HALF_UP);
        }

        return BoletoResponseDTO.builder()
            .id(boleto.getId())
            .codigoBarras(boleto.getCodigoBarras())
            .valor(boleto.getValor())
            .multaAtraso(multa)
            .valorTotal(boleto.getValor().add(multa))
            .dataVencimento(boleto.getDataVencimento())
            .status(boleto.getStatus().name())
            .pagamentoId(boleto.getPagamento() != null ? boleto.getPagamento().getId() : null)
            .pedidoId(boleto.getPagamento() != null && boleto.getPagamento().getPedido() != null
                ? boleto.getPagamento().getPedido().getId()
                : null)
            .atrasado(atrasado)
            .build();
    }
}