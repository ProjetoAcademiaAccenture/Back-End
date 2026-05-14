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

    /**
     * Valor total calculado a partir dos itens do pedido.
     * Persistido para uso em relatórios e cálculo de multa.
     */
    @Column(name = "valor_total", precision = 15, scale = 2)
    private BigDecimal valorTotal;

    /**
     * Multa aplicada no cancelamento de pedidos PAGO (10% do valorTotal).
     * Zero para pedidos cancelados antes do pagamento.
     */
    @Builder.Default
    @Column(name = "multa_cancelamento", precision = 15, scale = 2)
    private BigDecimal multaCancelamento = BigDecimal.ZERO;

    @Builder.Default
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Pagamento pagamento;

    // =========================================================
    // Métodos de negócio
    // =========================================================

    /**
     * Soma precoUnitario * quantidade de cada item e armazena em valorTotal.
     * Pedido sem itens resulta em ZERO.
     */
    public void calcularValorTotal() {
        this.valorTotal = itens.stream()
                .map(item -> item.getPrecoUnitario()
                        .multiply(new BigDecimal(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Avança o pedido de CRIADO → RESERVADO.
     *
     * @throws IllegalArgumentException se o status atual não for CRIADO.
     */
    public void reservar() {
        if (this.status != StatusPedido.CRIADO) {
            throw new IllegalArgumentException(
                    "Somente pedidos com status CRIADO podem ser reservados. Status atual: " + this.status);
        }
        this.status = StatusPedido.RESERVADO;
    }

    /**
     * Avança o pedido de RESERVADO → PAGO.
     *
     * @throws IllegalArgumentException se o status atual não for RESERVADO.
     */
    public void pagar() {
        if (this.status != StatusPedido.RESERVADO) {
            throw new IllegalArgumentException(
                    "Somente pedidos com status RESERVADO podem ser pagos. Status atual: " + this.status);
        }
        this.status = StatusPedido.PAGO;
    }

    /**
     * Cancela o pedido a partir de qualquer status exceto CANCELADO.
     * Aplica multa de 10% se o pedido já estava PAGO.
     *
     * @throws IllegalArgumentException se o pedido já estiver CANCELADO.
     */
    public void cancelar() {
        if (this.status == StatusPedido.CANCELADO) {
            throw new IllegalArgumentException("O pedido já está cancelado.");
        }
        if (this.status == StatusPedido.PAGO) {
            this.multaCancelamento = calcularMultaCancelamento();
        }
        this.status = StatusPedido.CANCELADO;
    }

    /**
     * Calcula a multa de cancelamento: 10% do valorTotal.
     *
     * @return BigDecimal com o valor da multa.
     */
    public BigDecimal calcularMultaCancelamento() {
        return this.valorTotal.multiply(new BigDecimal("0.10"))
                .setScale(2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Indica se o estoque deve ser devolvido ao cancelar este pedido.
     * Somente pedidos RESERVADO ou PAGO tiveram estoque reservado.
     *
     * @return true se o estoque deve ser devolvido, false caso contrário.
     */
    public boolean deveDevolverEstoque() {
        return this.status == StatusPedido.RESERVADO
                || this.status == StatusPedido.PAGO;
    }
}