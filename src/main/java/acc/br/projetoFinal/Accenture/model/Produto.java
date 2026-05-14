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
}