package acc.br.projetoFinal.Accenture.dto.request;

import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagamentoRequestDTO {

	@NotNull(message = "ID do pagamento é obrigatório")
	private Long pagamentoId;

	@NotNull(message = "Método de pagamento é obrigatório")
	private MetodoPagamento metodoPagamento;

	@NotBlank(message = "Senha de transação é obrigatória")
	@Size(min = 4, max = 4, message = "Senha de transação deve ter 4 dígitos")
	private String senhaTransacao;
}