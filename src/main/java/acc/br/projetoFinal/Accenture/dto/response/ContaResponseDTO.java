package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.enums.TipoConta;
import acc.br.projetoFinal.Accenture.model.Conta;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContaResponseDTO {

    private Long id;
    private String numeroConta;
    private BigDecimal saldo;
    private BigDecimal limiteCreditoDisponivel;
    private String tipo;

    public static ContaResponseDTO fromEntity(Conta conta) {
        return ContaResponseDTO.builder()
            .id(conta.getId())
            .numeroConta(conta.getNumeroConta())
            .saldo(conta.getSaldo())
            .limiteCreditoDisponivel(conta.getLimiteCreditoDisponivel())
            .tipo(conta.getTipo().name())
            .build();
    }
}