package acc.br.projetoFinal.Accenture.model;

import acc.br.projetoFinal.Accenture.enums.Categoria;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal preco;

    @Column(name = "url_imagem", length = 500)
    private String urlImagem;

    @Column(nullable = false)
    private Integer quantidadeEstoque;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    @Version
    private Long version;

    // =========================================================
    // Métodos de negócio
    // =========================================================

    /**
     * Valida o preço do produto.
     * Regras: não nulo, deve ser maior que zero.
     *
     * @throws IllegalArgumentException se o preço for nulo, zero ou negativo.
     */
    public void validarPreco() {
        if (preco == null) {
            throw new IllegalArgumentException("Preço não pode ser nulo.");
        }
        if (preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Preço deve ser maior que zero. Valor informado: " + preco);
        }
    }

    /**
     * Valida a quantidade em estoque.
     * Regras: não nulo, deve ser >= 0.
     *
     * @throws IllegalArgumentException se a quantidade for nula ou negativa.
     */
    public void validarEstoque() {
        if (quantidadeEstoque == null) {
            throw new IllegalArgumentException("Quantidade em estoque não pode ser nula.");
        }
        if (quantidadeEstoque < 0) {
            throw new IllegalArgumentException(
                    "Quantidade em estoque não pode ser negativa. Valor informado: " + quantidadeEstoque);
        }
    }

    /**
     * Reduz o estoque do produto pela quantidade informada.
     * Regras: quantidade não nula, não negativa e não superior ao estoque atual.
     *
     * @param quantidade quantidade a ser removida do estoque.
     * @throws IllegalArgumentException se a quantidade for nula, negativa ou insuficiente.
     */
    public void reduzirEstoque(Integer quantidade) {
        if (quantidade == null) {
            throw new IllegalArgumentException("Quantidade para redução não pode ser nula.");
        }
        if (quantidade < 0) {
            throw new IllegalArgumentException(
                    "Quantidade para redução não pode ser negativa. Valor informado: " + quantidade);
        }
        if (quantidade > this.quantidadeEstoque) {
            throw new IllegalArgumentException(
                    "Estoque insuficiente. Disponível: " + this.quantidadeEstoque
                    + ", solicitado: " + quantidade);
        }
        this.quantidadeEstoque -= quantidade;
    }

    /**
     * Devolve quantidade ao estoque do produto.
     * Regras: quantidade não nula e não negativa.
     *
     * @param quantidade quantidade a ser adicionada ao estoque.
     * @throws IllegalArgumentException se a quantidade for nula ou negativa.
     */
    public void devolverEstoque(Integer quantidade) {
        if (quantidade == null) {
            throw new IllegalArgumentException("Quantidade para devolução não pode ser nula.");
        }
        if (quantidade < 0) {
            throw new IllegalArgumentException(
                    "Quantidade para devolução não pode ser negativa. Valor informado: " + quantidade);
        }
        this.quantidadeEstoque += quantidade;
    }

    /**
     * Verifica se o produto possui estoque suficiente para a quantidade solicitada.
     *
     * @param quantidade quantidade desejada.
     * @return true se houver estoque >= quantidade, false caso contrário (incluindo estoque nulo).
     */
    public boolean temEstoque(int quantidade) {
        if (this.quantidadeEstoque == null) {
            return false;
        }
        return this.quantidadeEstoque >= quantidade;
    }
}