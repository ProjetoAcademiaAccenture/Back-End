package acc.br.projetoFinal.Accenture.dto.request;

import acc.br.projetoFinal.Accenture.enums.TipoConta;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContaRequestDTO {

	@NotNull(message = "ID do cliente é obrigatório")
	private Long clienteId;

	@NotBlank(message = "Senha de transação é obrigatória")
	@Size(min = 4, max = 4, message = "Senha de transação deve ter 4 dígitos")
	private String senhaTransacao;

	@NotNull(message = "Tipo da conta é obrigatório")
	private TipoConta tipoConta;
}