package acc.br.projetoFinal.Accenture.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDTO {
	private String token;
	private Long clienteId;
	private String nome;
	private String tipoCliente;
}
