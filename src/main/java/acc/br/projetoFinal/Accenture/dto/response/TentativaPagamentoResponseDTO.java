package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
import acc.br.projetoFinal.Accenture.enums.StatusPagamento;
import acc.br.projetoFinal.Accenture.model.TentativaPagamento;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TentativaPagamentoResponseDTO {

	private Long id;
	private Long pagamentoId;
	private MetodoPagamento metodoPagamento;
	private StatusPagamento status;
	private BigDecimal valorTentado;
	private String mensagem;
	private LocalDateTime dataTentativa;

	public static TentativaPagamentoResponseDTO fromEntity(TentativaPagamento tentativa) {
		return TentativaPagamentoResponseDTO.builder()
				.id(tentativa.getId())
				.pagamentoId(tentativa.getPagamento().getId())
				.metodoPagamento(tentativa.getMetodo())
				.status(tentativa.getStatus())
				.valorTentado(tentativa.getValorTentado())
				.mensagem(tentativa.getMensagem())
				.dataTentativa(tentativa.getDataTentativa())
				.build();
	}
}