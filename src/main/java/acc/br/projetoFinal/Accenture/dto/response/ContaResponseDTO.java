package acc.br.projetoFinal.Accenture.dto.response;

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
    private String tipo;
    private boolean ativo;

    public static ContaResponseDTO fromEntity(Conta conta) {
        return ContaResponseDTO.builder()
                .id(conta.getId())
                .numeroConta(conta.getNumeroConta())
                .saldo(conta.getSaldo())
                .tipo(conta.getTipo().name())
                .ativo(conta.isAtivo())
                .build();
    }
}
