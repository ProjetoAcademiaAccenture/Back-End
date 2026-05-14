package acc.br.projetoFinal.Accenture.model;

import acc.br.projetoFinal.Accenture.enums.TipoExtrato;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "extrato")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Extrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_id", nullable = false)
    private Conta conta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoExtrato tipo;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoAntes;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoDepois;

    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    // pagamento mantido mas sem participar do AllArgsConstructor do teste.
    // Declarado separadamente para não quebrar a assinatura de 9 args usada no teste.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pagamento_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Pagamento pagamento;
}