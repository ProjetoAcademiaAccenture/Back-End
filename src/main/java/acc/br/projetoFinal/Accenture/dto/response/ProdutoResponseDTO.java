package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.model.Produto;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoResponseDTO {

    private Long id;
    private String nome;
    private String descricao;
    private String urlImagem;
    private BigDecimal preco;
    private Integer quantidadeEstoque;
    private String categoria;

    public static ProdutoResponseDTO fromEntity(Produto produto) {

        return ProdutoResponseDTO.builder()
            .id(produto.getId())
            .nome(produto.getNome())
            .descricao(produto.getDescricao())
            .preco(produto.getPreco())
            .urlImagem(produto.getUrlImagem())
            .quantidadeEstoque(produto.getQuantidadeEstoque())
            .categoria(produto.getCategoria().name())
            .build();
    }
}