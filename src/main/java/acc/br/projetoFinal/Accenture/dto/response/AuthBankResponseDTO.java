package acc.br.projetoFinal.Accenture.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthBankResponseDTO {
	private String token;
	private Long clienteId;
	private Long contaId;
	private String numeroConta;
	private String saldo;
	private String tipoConta;
}
