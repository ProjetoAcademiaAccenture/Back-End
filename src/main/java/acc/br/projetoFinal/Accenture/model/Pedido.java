package acc.br.projetoFinal.Accenture.model;

import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorBruto;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal desconto;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorFinal;

    @Builder.Default
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Pagamento pagamento;

    // ── Regras de negócio ──────────────────────────────────────

    /**
     * Calcula valorBruto somando precoUnitario * quantidade de cada item.
     */
    public void calcularValorBruto() {
        this.valorBruto = itens.stream()
            .map(item -> item.getPrecoUnitario()
                             .multiply(BigDecimal.valueOf(item.getQuantidade())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Aplica desconto e recalcula valorFinal.
     * valorFinal = valorBruto - desconto
     */
    public void aplicarDesconto(BigDecimal desconto) {
        if (desconto == null || desconto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Desconto não pode ser negativo ou nulo.");
        }
        if (desconto.compareTo(this.valorBruto) > 0) {
            throw new IllegalArgumentException("Desconto não pode ser maior que o valor bruto.");
        }
        this.desconto = desconto;
        this.valorFinal = this.valorBruto.subtract(desconto);
    }

    /**
     * Transição CRIADO → RESERVADO.
     */
    public void reservar() {
        if (this.status != StatusPedido.CRIADO) {
            throw new IllegalArgumentException(
                "Pedido só pode ser reservado se estiver com status CRIADO.");
        }
        this.status = StatusPedido.RESERVADO;
    }

    /**
     * Transição RESERVADO → PAGO.
     */
    public void pagar() {
        if (this.status != StatusPedido.RESERVADO) {
            throw new IllegalArgumentException(
                "Pedido só pode ser pago se estiver com status RESERVADO.");
        }
        this.status = StatusPedido.PAGO;
    }

    /**
     * Cancela o pedido a partir de qualquer status exceto CANCELADO.
     */
    public void cancelar() {
        if (this.status == StatusPedido.CANCELADO) {
            throw new IllegalArgumentException("Pedido já está cancelado.");
        }
        this.status = StatusPedido.CANCELADO;
    }

    /**
     * Indica se o estoque deve ser devolvido ao cancelar.
     * Apenas RESERVADO e PAGO tiveram estoque reservado.
     */
    public boolean deveDevolverEstoque() {
        return this.status == StatusPedido.RESERVADO
            || this.status == StatusPedido.PAGO;
    }
}