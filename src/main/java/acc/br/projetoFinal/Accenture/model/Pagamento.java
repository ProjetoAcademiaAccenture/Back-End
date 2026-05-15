package acc.br.projetoFinal.Accenture.model;

import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
import acc.br.projetoFinal.Accenture.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pagamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pagamento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pedido_id", nullable = false)
	private Pedido pedido;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MetodoPagamento metodo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusPagamento status;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal valorBruto;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal desconto;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal valorFinal;

	@Column(nullable = false)
	private LocalDateTime dataCriacao;

	private LocalDateTime dataConclusao;

	@Builder.Default
	@OneToMany(mappedBy = "pagamento", cascade = CascadeType.ALL)
	private List<TentativaPagamento> tentativas = new ArrayList<>();

	@OneToOne(mappedBy = "pagamento", cascade = CascadeType.ALL)
	private Boleto boleto;
}
