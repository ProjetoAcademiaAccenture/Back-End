    package acc.br.projetoFinal.Accenture.dto.response;

    import acc.br.projetoFinal.Accenture.model.ItemPedido;
    import lombok.*;

    import java.math.BigDecimal;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class ItemPedidoResponseDTO {

        private Long id;
        private Long produtoId;
        private String produtoNome;
        private Integer quantidade;
        private BigDecimal precoUnitario;
        private BigDecimal subtotal;

        public static ItemPedidoResponseDTO fromEntity(ItemPedido item) {
            return ItemPedidoResponseDTO.builder()
                .id(item.getId())
                .produtoId(item.getProduto().getId())
                .produtoNome(item.getProduto().getNome())
                .quantidade(item.getQuantidade())
                .precoUnitario(item.getPrecoUnitario())
                .subtotal(item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .build();
        }
    }