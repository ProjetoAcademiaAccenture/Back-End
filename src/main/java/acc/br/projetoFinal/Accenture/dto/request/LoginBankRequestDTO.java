package acc.br.projetoFinal.Accenture.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginBankRequestDTO {
	@NotBlank
	private String numero_conta;

	@NotBlank
	private String senha;
}
