package acc.br.projetoFinal.Accenture.model;

import acc.br.projetoFinal.Accenture.enums.TipoConta;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conta")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_conta", unique = true, nullable = false)
    private String numeroConta;

    @Column(name = "senha_transacao", nullable = false)
    private String senhaTransacao;

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal saldo = BigDecimal.ZERO;

    @Column(name = "limite_credito", nullable = false, precision = 15, scale = 2)
    private BigDecimal limiteCredito = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConta tipo;

    // Obrigatório — toda conta pertence a um cliente (ou empresa)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false)
    @Builder.Default
    private boolean ativo = true;

    // 1:N com extratos
    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Extrato> extratos = new ArrayList<>();
}