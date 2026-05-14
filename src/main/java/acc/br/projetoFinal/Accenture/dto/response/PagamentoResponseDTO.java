package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
import acc.br.projetoFinal.Accenture.enums.StatusPagamento;
import acc.br.projetoFinal.Accenture.model.Pagamento;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagamentoResponseDTO {

	private Long id;
	private Long pedidoId;
	private MetodoPagamento metodoPagamento;
	private StatusPagamento status;
	private BigDecimal valorBruto;
	private BigDecimal desconto;
	private BigDecimal valorFinal;
	private LocalDateTime dataCriacao;
	private LocalDateTime dataConclusao;
	private List<TentativaPagamentoResponseDTO> tentativas;

	public static PagamentoResponseDTO fromEntity(Pagamento pagamento) {
		return PagamentoResponseDTO.builder()
				.id(pagamento.getId())
				.pedidoId(pagamento.getPedido().getId())
				.metodoPagamento(pagamento.getMetodo())
				.status(pagamento.getStatus())
				.valorBruto(pagamento.getValorBruto())
				.desconto(pagamento.getDesconto())
				.valorFinal(pagamento.getValorFinal())
				.dataCriacao(pagamento.getDataCriacao())
				.dataConclusao(pagamento.getDataConclusao())
				.tentativas(pagamento.getTentativas() != null
						? pagamento.getTentativas().stream()
							.map(TentativaPagamentoResponseDTO::fromEntity)
							.collect(Collectors.toList())
						: List.of())
				.build();
	}
}