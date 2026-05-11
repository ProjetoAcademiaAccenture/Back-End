package acc.br.projetoFinal.Accenture.dto.request;

import acc.br.projetoFinal.Accenture.enums.TipoConta;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContaRequestDTO {

	@NotNull
	private Long clienteId;

	@NotBlank(message = "Senha de trasação é obrigatória")
	@Size(min = 4, max = 4)
	private String senhaTransacao;

	@NotNull(message = "Tipo da conta é obrigatório")
	private TipoConta tipoConta;
}
