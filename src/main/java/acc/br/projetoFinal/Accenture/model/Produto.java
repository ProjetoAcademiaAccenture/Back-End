package acc.br.projetoFinal.Accenture.model;

import acc.br.projetoFinal.Accenture.enums.Categoria;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

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
 // ── Regras de negócio ──────────────────────────────────────
 
    /**
     * Verifica se há estoque suficiente para a quantidade solicitada.
     */
    public boolean temEstoqueSuficiente(int quantidade) {
        return this.quantidadeEstoque >= quantidade;
    }
 
    /**
     * Reserva (diminui) o estoque pelo quantidade informada.
     * Lança exceção se estoque insuficiente ou quantidade inválida.
     */
    public void reservarEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException(
                "Quantidade a reservar deve ser maior que zero.");
        }
        if (!temEstoqueSuficiente(quantidade)) {
            throw new IllegalArgumentException(
                "Estoque insuficiente para o produto: " + this.nome);
        }
        this.quantidadeEstoque -= quantidade;
    }
 
    /**
     * Devolve (aumenta) o estoque pela quantidade informada.
     * Lança exceção se quantidade inválida.
     */
    public void devolverEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException(
                "Quantidade a devolver deve ser maior que zero.");
        }
        this.quantidadeEstoque += quantidade;
    }
 
    /**
     * Ajusta o estoque para um valor absoluto.
     * Espelha o ProdutoService.ajustarEstoque() no modelo.
     */
    public void ajustarEstoque(int novaQuantidade) {
        if (novaQuantidade < 0) {
            throw new IllegalArgumentException(
                "Quantidade não pode ser negativa.");
        }
        this.quantidadeEstoque = novaQuantidade;
    }
}