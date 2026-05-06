package acc.br.projetoFinal.Accenture.model;

import acc.br.projetoFinal.Accenture.enums.StatusBoleto;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "boleto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Boleto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_barras", nullable = false, unique = true, length = 44)
    private String codigoBarras;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusBoleto status = StatusBoleto.PENDENTE;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;

    // REGRAS DE NEGÓCIO
    public void validarPagamento() {
        if (this.status == StatusBoleto.PAGO)
            throw new IllegalArgumentException("Boleto já foi pago");
        if (this.status == StatusBoleto.CANCELADO)
            throw new IllegalArgumentException("Boleto está cancelado");
    }

    public void pagar() {
        validarPagamento();
        this.status = StatusBoleto.PAGO;
    }

    public void validarCancelamento() {
        if (this.status == StatusBoleto.CANCELADO)
            throw new IllegalArgumentException("Boleto já está cancelado");
    }

    public void cancelar() {
        validarCancelamento();
        this.status = StatusBoleto.CANCELADO;
    }

    public boolean estaAtrasado() {
        return dataVencimento.isBefore(LocalDate.now());
    }
}
