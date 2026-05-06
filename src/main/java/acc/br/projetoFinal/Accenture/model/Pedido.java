package acc.br.projetoFinal.Accenture.model;

import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_criacao", nullable = false)
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusPedido status = StatusPedido.CRIADO;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "multa_cancelamento", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal multaCancelamento = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemPedido> itens = new ArrayList<>();

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Boleto boleto;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Extrato> extratos = new ArrayList<>();

    // REGRAS DE NEGÓCIO
    public void calcularValorTotal() {
        this.valorTotal = itens.stream()
                .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void validarReserva() {
        if (this.status != StatusPedido.CRIADO)
            throw new IllegalArgumentException("Pedido deve estar em status CRIADO");
    }

    public void reservar() {
        validarReserva();
        this.status = StatusPedido.RESERVADO;
    }

    public void validarPagamento() {
        if (this.status != StatusPedido.RESERVADO)
            throw new IllegalArgumentException("Pedido deve estar RESERVADO");
    }

    public void pagar() {
        validarPagamento();
        this.status = StatusPedido.PAGO;
    }

    public void validarCancelamento() {
        if (this.status == StatusPedido.CANCELADO)
            throw new IllegalArgumentException("Pedido já está cancelado");
    }

    public BigDecimal calcularMultaCancelamento() {
        final BigDecimal PERCENTUAL_MULTA = new BigDecimal("0.10");
        return valorTotal.multiply(PERCENTUAL_MULTA).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public void cancelar() {
        validarCancelamento();
        if (this.status == StatusPedido.PAGO) {
            this.multaCancelamento = calcularMultaCancelamento();
        }
        this.status = StatusPedido.CANCELADO;
    }

    public boolean deveDevolverEstoque() {
        return status == StatusPedido.RESERVADO || status == StatusPedido.PAGO;
    }
}
