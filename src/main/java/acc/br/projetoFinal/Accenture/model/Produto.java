package acc.br.projetoFinal.Accenture.model;

import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "quantidade_estoque", nullable = false)
    @Builder.Default
    private Integer quantidadeEstoque = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pgto", nullable = false)
    @Builder.Default
    private MetodoPagamento metodoPgto = MetodoPagamento.PIX;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemPedido> itens = new ArrayList<>();

    // REGRAS DE NEGÓCIO
    public void validarPreco() {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Preço deve ser maior que zero");
    }

    public void validarEstoque() {
        if (quantidadeEstoque == null || quantidadeEstoque < 0)
            throw new IllegalArgumentException("Quantidade de estoque não pode ser negativa");
    }

    public void reduzirEstoque(Integer quantidade) {
        if (quantidade == null || quantidade < 0)
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        if (this.quantidadeEstoque < quantidade)
            throw new IllegalArgumentException("Estoque insuficiente");
        this.quantidadeEstoque -= quantidade;
    }

    public void devolverEstoque(Integer quantidade) {
        if (quantidade == null || quantidade < 0)
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        this.quantidadeEstoque += quantidade;
    }

    public boolean temEstoque(Integer quantidade) {
        return quantidadeEstoque != null && quantidadeEstoque >= quantidade;
    }
}
