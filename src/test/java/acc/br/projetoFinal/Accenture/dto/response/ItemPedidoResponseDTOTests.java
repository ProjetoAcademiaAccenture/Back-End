package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.ItemPedido;
import acc.br.projetoFinal.Accenture.model.Pedido;
import acc.br.projetoFinal.Accenture.model.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PedidoResponseDTO - Testes Positivos")
class PedidoResponseDTOPositiveTests {

    private static final LocalDateTime DATA_CRIACAO  = LocalDateTime.of(2024, 6, 10, 14, 30);
    private static final Long          ID            = 1L;
    private static final Long          CLIENTE_ID    = 10L;
    private static final BigDecimal    VALOR_BRUTO   = new BigDecimal("500.00");
    private static final BigDecimal    DESCONTO      = new BigDecimal("50.00");
    private static final BigDecimal    VALOR_FINAL   = new BigDecimal("450.00");
    private static final StatusPedido  STATUS        = StatusPedido.CRIADO;

    private Cliente clienteBase;
    private Produto produtoBase;

    @BeforeEach
    void setUp() {
        clienteBase = Cliente.builder()
                .id(CLIENTE_ID)
                .nome("João Silva")
                .cpf("12345678901")
                .email("joao@email.com")
                .senha("senha123")
                .build();

        produtoBase = Produto.builder()
                .id(1L)
                .nome("Notebook")
                .preco(new BigDecimal("250.00"))
                .build();
    }

    // ------------------------------------------------------------------ helper

    private PedidoResponseDTO dtoPadraoValido() {
        return PedidoResponseDTO.builder()
                .id(ID)
                .dataCriacao(DATA_CRIACAO)
                .status(STATUS)
                .valorBruto(VALOR_BRUTO)
                .desconto(DESCONTO)
                .valorFinal(VALOR_FINAL)
                .clienteId(CLIENTE_ID)
                .itens(List.of())
                .build();
    }

    private Pedido pedidoPadrao() {
        return Pedido.builder()
                .id(ID)
                .dataCriacao(DATA_CRIACAO)
                .status(STATUS)
                .valorBruto(VALOR_BRUTO)
                .desconto(DESCONTO)
                .valorFinal(VALOR_FINAL)
                .cliente(clienteBase)
                .itens(new ArrayList<>())
                .build();
    }

    // =========================================================
    // Construtores
    // =========================================================

    @Test
    @DisplayName("Deve criar objeto com construtor padrão")
    void deveCriarComConstrutorPadrao() {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        assertNotNull(dto);
    }

    @Test
    @DisplayName("Deve criar objeto com construtor all-args e todos os campos")
    void deveCriarComConstrutorAllArgs() {
        // Ordem: id, dataCriacao, status, valorBruto, desconto, valorFinal,
        //        clienteId, itens, pagamento
        List<ItemPedidoResponseDTO> itens = List.of();

        PedidoResponseDTO dto = new PedidoResponseDTO(
                ID, DATA_CRIACAO, STATUS,
                VALOR_BRUTO, DESCONTO, VALOR_FINAL,
                CLIENTE_ID, itens, null
        );

        assertAll("AllArgsConstructor",
                () -> assertEquals(ID,           dto.getId()),
                () -> assertEquals(DATA_CRIACAO,  dto.getDataCriacao()),
                () -> assertEquals(STATUS,        dto.getStatus()),
                () -> assertEquals(VALOR_BRUTO,   dto.getValorBruto()),
                () -> assertEquals(DESCONTO,      dto.getDesconto()),
                () -> assertEquals(VALOR_FINAL,   dto.getValorFinal()),
                () -> assertEquals(CLIENTE_ID,    dto.getClienteId()),
                () -> assertEquals(itens,         dto.getItens()),
                () -> assertNull(dto.getPagamento())
        );
    }

    // =========================================================
    // Builder
    // =========================================================

    @Test
    @DisplayName("Deve criar objeto via builder com todos os campos")
    void deveCriarViaBuilderCompleto() {
        PedidoResponseDTO dto = dtoPadraoValido();

        assertAll("builder completo",
                () -> assertEquals(ID,          dto.getId()),
                () -> assertEquals(DATA_CRIACAO, dto.getDataCriacao()),
                () -> assertEquals(STATUS,       dto.getStatus()),
                () -> assertEquals(VALOR_BRUTO,  dto.getValorBruto()),
                () -> assertEquals(DESCONTO,     dto.getDesconto()),
                () -> assertEquals(VALOR_FINAL,  dto.getValorFinal()),
                () -> assertEquals(CLIENTE_ID,   dto.getClienteId()),
                () -> assertNotNull(dto.getItens())
        );
    }

    @Test
    @DisplayName("Deve criar objeto via builder parcial")
    void deveCriarViaBuilderParcial() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder()
                .id(ID)
                .status(STATUS)
                .build();

        assertEquals(ID, dto.getId());
        assertEquals(STATUS, dto.getStatus());
        assertNull(dto.getValorBruto());
        assertNull(dto.getDesconto());
        assertNull(dto.getValorFinal());
    }

    // =========================================================
    // Setters e Getters
    // =========================================================

    @Test
    @DisplayName("Deve definir e obter todos os campos via setters")
    void deveDefinirEObterTodosCamposViaSetters() {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        List<ItemPedidoResponseDTO> itens = List.of(ItemPedidoResponseDTO.builder().id(1L).build());

        dto.setId(ID);
        dto.setDataCriacao(DATA_CRIACAO);
        dto.setStatus(STATUS);
        dto.setValorBruto(VALOR_BRUTO);
        dto.setDesconto(DESCONTO);
        dto.setValorFinal(VALOR_FINAL);
        dto.setClienteId(CLIENTE_ID);
        dto.setItens(itens);
        dto.setPagamento(null);

        assertAll("setters/getters",
                () -> assertEquals(ID,          dto.getId()),
                () -> assertEquals(DATA_CRIACAO, dto.getDataCriacao()),
                () -> assertEquals(STATUS,       dto.getStatus()),
                () -> assertEquals(VALOR_BRUTO,  dto.getValorBruto()),
                () -> assertEquals(DESCONTO,     dto.getDesconto()),
                () -> assertEquals(VALOR_FINAL,  dto.getValorFinal()),
                () -> assertEquals(CLIENTE_ID,   dto.getClienteId()),
                () -> assertEquals(itens,        dto.getItens()),
                () -> assertNull(dto.getPagamento())
        );
    }

    @Test
    @DisplayName("Deve atualizar campos múltiplas vezes")
    void deveAtualizarCamposMultiplasVezes() {
        PedidoResponseDTO dto = new PedidoResponseDTO();

        dto.setValorBruto(new BigDecimal("100.00"));
        assertEquals(new BigDecimal("100.00"), dto.getValorBruto());

        dto.setValorBruto(new BigDecimal("200.00"));
        assertEquals(new BigDecimal("200.00"), dto.getValorBruto());
    }

    // =========================================================
    // fromEntity — branch itens != null
    // =========================================================

    @Test
    @DisplayName("Deve converter Pedido com um item para DTO corretamente")
    void deveConverterPedidoComUmItem() {
        ItemPedido item = ItemPedido.builder()
                .id(1L)
                .produto(produtoBase)
                .quantidade(2)
                .precoUnitario(new BigDecimal("250.00"))
                .build();

        Pedido pedido = Pedido.builder()
                .id(ID).dataCriacao(DATA_CRIACAO).status(STATUS)
                .valorBruto(VALOR_BRUTO).desconto(DESCONTO).valorFinal(VALOR_FINAL)
                .cliente(clienteBase).itens(new ArrayList<>(List.of(item))).build();

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertAll("fromEntity com um item",
                () -> assertNotNull(dto),
                () -> assertEquals(ID,          dto.getId()),
                () -> assertEquals(DATA_CRIACAO, dto.getDataCriacao()),
                () -> assertEquals(STATUS,       dto.getStatus()),
                () -> assertEquals(VALOR_BRUTO,  dto.getValorBruto()),
                () -> assertEquals(DESCONTO,     dto.getDesconto()),
                () -> assertEquals(VALOR_FINAL,  dto.getValorFinal()),
                () -> assertEquals(CLIENTE_ID,   dto.getClienteId()),
                () -> assertEquals(1,            dto.getItens().size())
        );
    }

    @Test
    @DisplayName("Deve converter Pedido com múltiplos itens")
    void deveConverterPedidoComMultiplosItens() {
        Produto produto2 = Produto.builder()
                .id(2L).nome("Mouse").preco(new BigDecimal("80.00")).build();

        ItemPedido item1 = ItemPedido.builder()
                .id(1L).produto(produtoBase).quantidade(2)
                .precoUnitario(new BigDecimal("250.00")).build();

        ItemPedido item2 = ItemPedido.builder()
                .id(2L).produto(produto2).quantidade(1)
                .precoUnitario(new BigDecimal("80.00")).build();

        Pedido pedido = Pedido.builder()
                .id(ID).dataCriacao(DATA_CRIACAO).status(STATUS)
                .valorBruto(new BigDecimal("580.00")).desconto(BigDecimal.ZERO)
                .valorFinal(new BigDecimal("580.00"))
                .cliente(clienteBase).itens(new ArrayList<>(List.of(item1, item2))).build();

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertEquals(2, dto.getItens().size());
        assertThat(dto.getItens()).extracting("produtoNome")
                .containsExactly("Notebook", "Mouse");
    }

    @Test
    @DisplayName("Deve converter Pedido com lista de itens vazia")
    void deveConverterPedidoComItensVazios() {
        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedidoPadrao());

        assertNotNull(dto.getItens());
        assertTrue(dto.getItens().isEmpty());
    }

    @Test
    @DisplayName("fromEntity deve retornar lista vazia quando itens for null")
    void fromEntity_ItensNull_DeveRetornarListaVazia() {
        Pedido pedido = pedidoPadrao();
        pedido.setItens(null);

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertNotNull(dto.getItens());
        assertTrue(dto.getItens().isEmpty());
    }

    @Test
    @DisplayName("fromEntity deve mapear pagamento como null quando não há pagamento (branch false)")
    void fromEntity_SemPagamento_PagamentoDeveSerNull() {
        Pedido pedido = pedidoPadrao();
        pedido.setPagamento(null);

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertNull(dto.getPagamento());
    }

    @Test
    @DisplayName("fromEntity(pedido, metodoPagamento) deve definir pagamento com método informado")
    void fromEntity_ComMetodoPagamento_DeveSetarMetodo() {
        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedidoPadrao(), MetodoPagamento.PIX);

        assertNotNull(dto.getPagamento());
        assertEquals(MetodoPagamento.PIX, dto.getPagamento().getMetodoPagamento());
    }

    @Test
    @DisplayName("fromEntity(pedido, metodoPagamento) deve manter demais campos do pedido")
    void fromEntity_ComMetodoPagamento_DeveManterDemaisCampos() {
        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedidoPadrao(), MetodoPagamento.BOLETO);

        assertEquals(ID,         dto.getId());
        assertEquals(STATUS,     dto.getStatus());
        assertEquals(VALOR_BRUTO, dto.getValorBruto());
        assertEquals(DESCONTO,   dto.getDesconto());
        assertEquals(VALOR_FINAL, dto.getValorFinal());
        assertEquals(CLIENTE_ID, dto.getClienteId());
    }

    // =========================================================
    // fromEntity — cada StatusPedido
    // =========================================================

    @Test
    @DisplayName("Deve converter Pedido com status RESERVADO")
    void deveConverterPedidoComStatusReservado() {
        Pedido pedido = Pedido.builder()
                .id(ID).dataCriacao(DATA_CRIACAO).status(StatusPedido.RESERVADO)
                .valorBruto(VALOR_BRUTO).desconto(DESCONTO).valorFinal(VALOR_FINAL)
                .cliente(clienteBase).itens(new ArrayList<>()).build();

        assertEquals(StatusPedido.RESERVADO, PedidoResponseDTO.fromEntity(pedido).getStatus());
    }

    @Test
    @DisplayName("Deve converter Pedido com status PAGO")
    void deveConverterPedidoComStatusPago() {
        Pedido pedido = Pedido.builder()
                .id(ID).dataCriacao(DATA_CRIACAO).status(StatusPedido.PAGO)
                .valorBruto(VALOR_BRUTO).desconto(DESCONTO).valorFinal(VALOR_FINAL)
                .cliente(clienteBase).itens(new ArrayList<>()).build();

        assertEquals(StatusPedido.PAGO, PedidoResponseDTO.fromEntity(pedido).getStatus());
    }

    @Test
    @DisplayName("Deve converter Pedido com status CANCELADO")
    void deveConverterPedidoComStatusCancelado() {
        Pedido pedido = Pedido.builder()
                .id(ID).dataCriacao(DATA_CRIACAO).status(StatusPedido.CANCELADO)
                .valorBruto(VALOR_BRUTO).desconto(DESCONTO).valorFinal(VALOR_FINAL)
                .cliente(clienteBase).itens(new ArrayList<>()).build();

        assertEquals(StatusPedido.CANCELADO, PedidoResponseDTO.fromEntity(pedido).getStatus());
    }

    @Test
    @DisplayName("Deve converter Pedido com valores zerados")
    void deveConverterPedidoComValoresZerados() {
        Pedido pedido = Pedido.builder()
                .id(ID).dataCriacao(DATA_CRIACAO).status(STATUS)
                .valorBruto(BigDecimal.ZERO).desconto(BigDecimal.ZERO).valorFinal(BigDecimal.ZERO)
                .cliente(clienteBase).itens(new ArrayList<>()).build();

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertEquals(BigDecimal.ZERO, dto.getValorBruto());
        assertEquals(BigDecimal.ZERO, dto.getDesconto());
        assertEquals(BigDecimal.ZERO, dto.getValorFinal());
    }

    // =========================================================
    // equals / hashCode
    // =========================================================

    @Test
    @DisplayName("Dois objetos com mesmos valores devem ser iguais")
    void doisObjetosComMesmosValoresDevemSerIguais() {
        PedidoResponseDTO dto1 = dtoPadraoValido();
        PedidoResponseDTO dto2 = dtoPadraoValido();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Objeto deve ser igual a si mesmo")
    void objetoDeveSerIgualASiMesmo() {
        PedidoResponseDTO dto = dtoPadraoValido();
        assertEquals(dto, dto);
    }

    @Test
    @DisplayName("Dois objetos padrão devem ser iguais entre si")
    void doisObjetosPadraoDevemSerIguais() {
        PedidoResponseDTO dto1 = new PedidoResponseDTO();
        PedidoResponseDTO dto2 = new PedidoResponseDTO();
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("equals deve ser simétrico")
    void equals_DeveSerSimetrico() {
        PedidoResponseDTO dto1 = dtoPadraoValido();
        PedidoResponseDTO dto2 = dtoPadraoValido();
        assertEquals(dto1.equals(dto2), dto2.equals(dto1));
    }

    @Test
    @DisplayName("hashCode deve ser consistente em múltiplas chamadas")
    void hashCode_DeveSerConsistente() {
        PedidoResponseDTO dto = dtoPadraoValido();
        assertEquals(dto.hashCode(), dto.hashCode());
    }

    @Test
    @DisplayName("hashCode não deve lançar exceção com campos nulos")
    void hashCode_NaoDeveLancarExcecao_QuandoCamposNulos() {
        assertThatCode(() -> new PedidoResponseDTO().hashCode()).doesNotThrowAnyException();
    }

    // =========================================================
    // toString
    // =========================================================

    @Test
    @DisplayName("toString não deve ser nulo")
    void toStringNaoDeveSerNulo() {
        assertNotNull(new PedidoResponseDTO().toString());
    }

    @Test
    @DisplayName("toString deve conter os campos principais")
    void toStringDeveConterCamposPrincipais() {
        String result = dtoPadraoValido().toString();

        assertAll("toString",
                () -> assertTrue(result.contains("id")),
                () -> assertTrue(result.contains("status")),
                () -> assertTrue(result.contains("valorBruto")),
                () -> assertTrue(result.contains("desconto")),
                () -> assertTrue(result.contains("valorFinal")),
                () -> assertTrue(result.contains("clienteId"))
        );
    }

    @Test
    @DisplayName("toString não deve lançar exceção com campos nulos")
    void toString_NaoDeveLancarExcecao_QuandoCamposNulos() {
        assertThatCode(() -> new PedidoResponseDTO().toString()).doesNotThrowAnyException();
    }

    // =========================================================
    // canEqual
    // =========================================================

    @Test
    @DisplayName("canEqual deve retornar true para instância do mesmo tipo")
    void canEqual_MesmoTipo_DeveRetornarTrue() {
        PedidoResponseDTO dto1 = dtoPadraoValido();
        PedidoResponseDTO dto2 = new PedidoResponseDTO();
        assertTrue(dto1.canEqual(dto2));
    }

    @Test
    @DisplayName("canEqual deve retornar false para tipo diferente")
    void canEqual_TipoDiferente_DeveRetornarFalse() {
        PedidoResponseDTO dto = dtoPadraoValido();
        assertFalse(dto.canEqual("string"));
        assertFalse(dto.canEqual(null));
    }
}