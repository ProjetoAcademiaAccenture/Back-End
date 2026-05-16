package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
import acc.br.projetoFinal.Accenture.enums.StatusPagamento;
import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.ItemPedido;
import acc.br.projetoFinal.Accenture.model.Pagamento;
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
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PedidoResponseDTO - Testes Positivos")
class PedidoResponseDTOPositiveTests {

    private static final LocalDateTime DATA_CRIACAO = LocalDateTime.of(2024, 6, 10, 14, 30);
    private static final Long ID         = 1L;
    private static final Long CLIENTE_ID = 10L;
    private static final BigDecimal VALOR_BRUTO = new BigDecimal("500.00");
    private static final BigDecimal DESCONTO    = new BigDecimal("50.00");
    private static final BigDecimal VALOR_FINAL = new BigDecimal("450.00");
    private static final StatusPedido STATUS = StatusPedido.CRIADO;

    private Cliente clienteBase;

    @BeforeEach
    void setUp() {
        clienteBase = Cliente.builder()
                .id(CLIENTE_ID)
                .nome("João Silva")
                .cpf("12345678901")
                .email("joao@email.com")
                .senha("senha123")
                .build();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

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

    private Pagamento pagamentoPadrao(Pedido pedido) {
        return Pagamento.builder()
                .id(100L)
                .pedido(pedido)
                .metodo(MetodoPagamento.PIX)
                .status(StatusPagamento.APROVADO)
                .valorBruto(VALOR_BRUTO)
                .desconto(DESCONTO)
                .valorFinal(VALOR_FINAL)
                .dataCriacao(DATA_CRIACAO)
                .dataConclusao(DATA_CRIACAO.plusHours(1))
                .build();
    }

    // =========================================================
    // fromEntity — mapeamento básico
    // =========================================================

    @Test
    @DisplayName("fromEntity deve mapear todos os campos corretamente")
    void fromEntity_DeveMapearTodosOsCampos() {
        Pedido pedido = pedidoPadrao();

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertAll("todos os campos",
                () -> assertEquals(ID,          dto.getId()),
                () -> assertEquals(DATA_CRIACAO, dto.getDataCriacao()),
                () -> assertEquals(STATUS,       dto.getStatus()),
                () -> assertEquals(VALOR_BRUTO,  dto.getValorBruto()),
                () -> assertEquals(DESCONTO,     dto.getDesconto()),
                () -> assertEquals(VALOR_FINAL,  dto.getValorFinal()),
                () -> assertEquals(CLIENTE_ID,   dto.getClienteId()),
                () -> assertNotNull(dto.getItens()),
                () -> assertTrue(dto.getItens().isEmpty()),
                () -> assertNull(dto.getPagamento())
        );
    }

    @Test
    @DisplayName("fromEntity deve retornar lista vazia quando itens for lista vazia")
    void fromEntity_DeveRetornarListaVazia_QuandoItensVazio() {
        Pedido pedido = pedidoPadrao();
        pedido.setItens(new ArrayList<>());

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertNotNull(dto.getItens());
        assertTrue(dto.getItens().isEmpty());
    }

    @Test
    @DisplayName("fromEntity deve retornar lista vazia quando itens for null (branch ternário)")
    void fromEntity_DeveRetornarListaVazia_QuandoItensNull() {
        Pedido pedido = pedidoPadrao();
        pedido.setItens(null);

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertNotNull(dto.getItens());
        assertTrue(dto.getItens().isEmpty());
    }

    @Test
    @DisplayName("fromEntity deve converter lista de itens corretamente")
    void fromEntity_DeveConverterItens() {
        Produto produto = Produto.builder()
                .id(1L)
                .nome("Produto Teste")
                .preco(new BigDecimal("100.00"))
                .build();

        ItemPedido item = ItemPedido.builder()
                .id(1L)
                .produto(produto)
                .quantidade(2)
                .precoUnitario(new BigDecimal("100.00"))
                .build();

        Pedido pedido = pedidoPadrao();
        pedido.setItens(List.of(item));

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertThat(dto.getItens()).hasSize(1);
        assertThat(dto.getItens().get(0).getProdutoId()).isEqualTo(1L);
        assertThat(dto.getItens().get(0).getProdutoNome()).isEqualTo("Produto Teste");
        assertThat(dto.getItens().get(0).getSubtotal()).isEqualByComparingTo("200.00");
    }

    @Test
    @DisplayName("fromEntity deve mapear pagamento quando pedido tem pagamento (branch true)")
    void fromEntity_DeveMapearPagamento_QuandoPedidoTemPagamento() {
        Pedido pedido = pedidoPadrao();
        Pagamento pagamento = pagamentoPadrao(pedido);
        pedido.setPagamento(pagamento);

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertNotNull(dto.getPagamento());
        assertEquals(100L,                dto.getPagamento().getId());
        assertEquals(MetodoPagamento.PIX, dto.getPagamento().getMetodoPagamento());
        assertEquals(StatusPagamento.APROVADO, dto.getPagamento().getStatus());
    }

    @Test
    @DisplayName("fromEntity deve mapear pagamento como null quando pedido não tem pagamento (branch false)")
    void fromEntity_DeveMappearPagamentoNull_QuandoSemPagamento() {
        Pedido pedido = pedidoPadrao();
        pedido.setPagamento(null);

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertNull(dto.getPagamento());
    }

    @Test
    @DisplayName("fromEntity com MetodoPagamento PIX deve setar pagamento corretamente")
    void fromEntity_ComMetodoPagamentoPix_DeveSetarPagamento() {
        Pedido pedido = pedidoPadrao();

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido, MetodoPagamento.PIX);

        assertNotNull(dto.getPagamento());
        assertEquals(MetodoPagamento.PIX, dto.getPagamento().getMetodoPagamento());
    }

    @Test
    @DisplayName("fromEntity com MetodoPagamento CARTAO_CREDITO deve setar pagamento corretamente")
    void fromEntity_ComMetodoPagamentoCartao_DeveSetarPagamento() {
        Pedido pedido = pedidoPadrao();

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido, MetodoPagamento.CREDITO);

        assertNotNull(dto.getPagamento());
        assertEquals(MetodoPagamento.CREDITO, dto.getPagamento().getMetodoPagamento());
    }

    @Test
    @DisplayName("fromEntity deve converter Pedido com status PAGO")
    void fromEntity_DeveConverterPedidoComStatusPago() {
        Pedido pedido = Pedido.builder()
                .id(ID).dataCriacao(DATA_CRIACAO).status(StatusPedido.PAGO)
                .valorBruto(VALOR_BRUTO).desconto(DESCONTO).valorFinal(VALOR_FINAL)
                .cliente(clienteBase).itens(new ArrayList<>()).build();

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertEquals(StatusPedido.PAGO, dto.getStatus());
    }

    @Test
    @DisplayName("fromEntity deve converter Pedido com status CANCELADO")
    void fromEntity_DeveConverterPedidoComStatusCancelado() {
        Pedido pedido = Pedido.builder()
                .id(ID).dataCriacao(DATA_CRIACAO).status(StatusPedido.CANCELADO)
                .valorBruto(VALOR_BRUTO).desconto(DESCONTO).valorFinal(VALOR_FINAL)
                .cliente(clienteBase).itens(new ArrayList<>()).build();

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertEquals(StatusPedido.CANCELADO, dto.getStatus());
    }

    @Test
    @DisplayName("fromEntity deve converter Pedido com valores zerados")
    void fromEntity_DeveConverterPedidoComValoresZerados() {
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
    // NoArgsConstructor
    // =========================================================

    @Test
    @DisplayName("Construtor padrão deve criar objeto com todos os campos nulos")
    void noArgsConstructor_DeveCriarObjetoComCamposNulos() {
        PedidoResponseDTO dto = new PedidoResponseDTO();

        assertAll(
                () -> assertNull(dto.getId()),
                () -> assertNull(dto.getDataCriacao()),
                () -> assertNull(dto.getStatus()),
                () -> assertNull(dto.getValorBruto()),
                () -> assertNull(dto.getDesconto()),
                () -> assertNull(dto.getValorFinal()),
                () -> assertNull(dto.getClienteId()),
                () -> assertNull(dto.getItens()),
                () -> assertNull(dto.getPagamento())
        );
    }

    // =========================================================
    // AllArgsConstructor
    // =========================================================

    @Test
    @DisplayName("AllArgsConstructor deve criar instância com todos os campos")
    void allArgsConstructor_DeveCriarInstanciaCompleta() {
        PedidoResponseDTO dto = new PedidoResponseDTO(
                ID, DATA_CRIACAO, STATUS,
                VALOR_BRUTO, DESCONTO, VALOR_FINAL,
                CLIENTE_ID, List.of(), null
        );

        assertAll(
                () -> assertEquals(ID,          dto.getId()),
                () -> assertEquals(DATA_CRIACAO, dto.getDataCriacao()),
                () -> assertEquals(STATUS,       dto.getStatus()),
                () -> assertEquals(VALOR_BRUTO,  dto.getValorBruto()),
                () -> assertEquals(DESCONTO,     dto.getDesconto()),
                () -> assertEquals(VALOR_FINAL,  dto.getValorFinal()),
                () -> assertEquals(CLIENTE_ID,   dto.getClienteId()),
                () -> assertNotNull(dto.getItens()),
                () -> assertNull(dto.getPagamento())
        );
    }

    // =========================================================
    // Builder
    // =========================================================

    @Test
    @DisplayName("Builder deve criar DTO com todos os campos")
    void builder_DeveCriarDTOComTodosOsCampos() {
        PedidoResponseDTO dto = dtoPadraoValido();

        assertAll(
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
    @DisplayName("Builder deve criar DTO com campos parciais")
    void builder_DeveCriarDTOComCamposParciais() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder()
                .id(ID)
                .status(STATUS)
                .build();

        assertAll(
                () -> assertEquals(ID,     dto.getId()),
                () -> assertEquals(STATUS, dto.getStatus()),
                () -> assertNull(dto.getDataCriacao()),
                () -> assertNull(dto.getValorBruto()),
                () -> assertNull(dto.getClienteId())
        );
    }

    // =========================================================
    // Setters
    // =========================================================

    @Test
    @DisplayName("Setters devem atualizar todos os campos corretamente")
    void setters_DevemAtualizarTodosOsCampos() {
        PedidoResponseDTO dto = new PedidoResponseDTO();

        dto.setId(ID);
        dto.setDataCriacao(DATA_CRIACAO);
        dto.setStatus(STATUS);
        dto.setValorBruto(VALOR_BRUTO);
        dto.setDesconto(DESCONTO);
        dto.setValorFinal(VALOR_FINAL);
        dto.setClienteId(CLIENTE_ID);
        dto.setItens(List.of());
        dto.setPagamento(null);

        assertAll(
                () -> assertEquals(ID,          dto.getId()),
                () -> assertEquals(DATA_CRIACAO, dto.getDataCriacao()),
                () -> assertEquals(STATUS,       dto.getStatus()),
                () -> assertEquals(VALOR_BRUTO,  dto.getValorBruto()),
                () -> assertEquals(DESCONTO,     dto.getDesconto()),
                () -> assertEquals(VALOR_FINAL,  dto.getValorFinal()),
                () -> assertEquals(CLIENTE_ID,   dto.getClienteId()),
                () -> assertNotNull(dto.getItens()),
                () -> assertNull(dto.getPagamento())
        );
    }

    // =========================================================
    // equals / hashCode
    // =========================================================

    @Test
    @DisplayName("Objetos com mesmos dados devem ser iguais")
    void equals_ObjetosIguais_DeveRetornarTrue() {
        PedidoResponseDTO dto1 = dtoPadraoValido();
        PedidoResponseDTO dto2 = dtoPadraoValido();
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Mesma instância deve ser igual a si mesma")
    void equals_MesmaInstancia_DeveRetornarTrue() {
        PedidoResponseDTO dto = dtoPadraoValido();
        assertEquals(dto, dto);
    }

    // =========================================================
    // toString
    // =========================================================

    @Test
    @DisplayName("toString deve conter campos principais")
    void toString_DeveConterCamposPrincipais() {
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