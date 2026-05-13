package acc.br.projetoFinal.Accenture.model;

import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
import acc.br.projetoFinal.Accenture.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tentativa_pagamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TentativaPagamento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pagamento_id", nullable = false)
	private Pagamento pagamento;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MetodoPagamento metodo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusPagamento status;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal valorTentado;

	@Column(length = 255)
	private String mensagem;

	@Column(nullable = false)
	private LocalDateTime dataTentativa;
}
