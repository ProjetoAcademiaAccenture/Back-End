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

@DisplayName("PedidoResponseDTO - Testes Negativos")
class PedidoResponseDTONegativeTests {

    private static final LocalDateTime DATA_CRIACAO = LocalDateTime.of(2024, 6, 10, 14, 30);
    private static final Long ID        = 1L;
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
    // Construtor padrão — campos nulos
    // =========================================================

    @Test
    @DisplayName("Deve criar objeto com construtor padrão com todos os campos nulos")
    void deveCriarComConstrutorPadraoComCamposNulos() {
        PedidoResponseDTO dto = new PedidoResponseDTO();

        assertNull(dto.getId());
        assertNull(dto.getDataCriacao());
        assertNull(dto.getStatus());
        assertNull(dto.getValorBruto());
        assertNull(dto.getDesconto());
        assertNull(dto.getValorFinal());
        assertNull(dto.getClienteId());
        assertNull(dto.getItens());
        assertNull(dto.getPagamento());
    }

    // =========================================================
    // Builder — campos nulos individualmente
    // =========================================================

    @Test
    @DisplayName("Deve criar objeto via builder vazio com todos os campos nulos")
    void deveCriarViaBuilderVazioComCamposNulos() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder().build();

        assertNull(dto.getId());
        assertNull(dto.getDataCriacao());
        assertNull(dto.getStatus());
        assertNull(dto.getValorBruto());
        assertNull(dto.getDesconto());
        assertNull(dto.getValorFinal());
        assertNull(dto.getClienteId());
        assertNull(dto.getItens());
        assertNull(dto.getPagamento());
    }

    @Test
    @DisplayName("Deve criar objeto com id null via builder")
    void deveCriarComIdNull() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder()
                .id(null).status(STATUS).valorBruto(VALOR_BRUTO)
                .desconto(DESCONTO).valorFinal(VALOR_FINAL).clienteId(CLIENTE_ID).build();

        assertNull(dto.getId());
        assertNotNull(dto.getStatus());
    }

    @Test
    @DisplayName("Deve criar objeto com dataCriacao null via builder")
    void deveCriarComDataCriacaoNull() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder()
                .id(ID).dataCriacao(null).build();

        assertNull(dto.getDataCriacao());
    }

    @Test
    @DisplayName("Deve criar objeto com status null via builder")
    void deveCriarComStatusNull() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder()
                .id(ID).status(null).build();

        assertNull(dto.getStatus());
    }

    @Test
    @DisplayName("Deve criar objeto com valorBruto null via builder")
    void deveCriarComValorBrutoNull() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder()
                .id(ID).valorBruto(null).build();

        assertNull(dto.getValorBruto());
    }

    @Test
    @DisplayName("Deve criar objeto com desconto null via builder")
    void deveCriarComDescontoNull() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder()
                .id(ID).desconto(null).build();

        assertNull(dto.getDesconto());
    }

    @Test
    @DisplayName("Deve criar objeto com valorFinal null via builder")
    void deveCriarComValorFinalNull() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder()
                .id(ID).valorFinal(null).build();

        assertNull(dto.getValorFinal());
    }

    @Test
    @DisplayName("Deve criar objeto com clienteId null via builder")
    void deveCriarComClienteIdNull() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder()
                .id(ID).clienteId(null).build();

        assertNull(dto.getClienteId());
    }

    @Test
    @DisplayName("Deve criar objeto com itens null via builder")
    void deveCriarComItensNull() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder()
                .id(ID).itens(null).build();

        assertNull(dto.getItens());
    }

    @Test
    @DisplayName("Deve criar objeto com pagamento null via builder")
    void deveCriarComPagamentoNull() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder()
                .id(ID).pagamento(null).build();

        assertNull(dto.getPagamento());
    }

    // =========================================================
    // Valores extremos / inválidos
    // =========================================================

    @Test
    @DisplayName("Deve aceitar id negativo")
    void deveAceitarIdNegativo() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder().id(-1L).build();
        assertTrue(dto.getId() < 0);
    }

    @Test
    @DisplayName("Deve aceitar clienteId negativo")
    void deveAceitarClienteIdNegativo() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder().clienteId(-99L).build();
        assertTrue(dto.getClienteId() < 0);
    }

    @Test
    @DisplayName("Deve aceitar valorBruto negativo")
    void deveAceitarValorBrutoNegativo() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder()
                .valorBruto(new BigDecimal("-100.00")).build();

        assertTrue(dto.getValorBruto().signum() < 0);
    }

    @Test
    @DisplayName("Deve aceitar desconto negativo")
    void deveAceitarDescontoNegativo() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder()
                .desconto(new BigDecimal("-10.00")).build();

        assertTrue(dto.getDesconto().signum() < 0);
    }

    @Test
    @DisplayName("Deve aceitar valorFinal negativo")
    void deveAceitarValorFinalNegativo() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder()
                .valorFinal(new BigDecimal("-50.00")).build();

        assertTrue(dto.getValorFinal().signum() < 0);
    }

    @Test
    @DisplayName("Deve aceitar valorBruto muito grande")
    void deveAceitarValorBrutoMuitoGrande() {
        BigDecimal valorGrande = new BigDecimal("999999999.99");
        PedidoResponseDTO dto = PedidoResponseDTO.builder().valorBruto(valorGrande).build();
        assertEquals(valorGrande, dto.getValorBruto());
    }

    @Test
    @DisplayName("Deve aceitar dataCriacao no futuro")
    void deveAceitarDataCriacaoNoFuturo() {
        LocalDateTime futuro = LocalDateTime.now().plusYears(10);
        PedidoResponseDTO dto = PedidoResponseDTO.builder().dataCriacao(futuro).build();
        assertTrue(dto.getDataCriacao().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("Deve aceitar lista de itens vazia explicitamente")
    void deveAceitarListaDeItensVazia() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder()
                .id(ID).itens(new ArrayList<>()).build();

        assertNotNull(dto.getItens());
        assertTrue(dto.getItens().isEmpty());
    }

    // =========================================================
    // fromEntity — branches ternários
    // =========================================================

    @Test
    @DisplayName("fromEntity deve retornar lista vazia quando itens for null")
    void deveRetornarListaVaziaQuandoItensNull() {
        Pedido pedido = pedidoPadrao();
        pedido.setItens(null);

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertNotNull(dto.getItens());
        assertTrue(dto.getItens().isEmpty());
    }

    @Test
    @DisplayName("fromEntity deve mapear pagamento como null quando pedido não tem pagamento (branch false)")
    void fromEntity_PagamentoNull_DeveMappearNull() {
        Pedido pedido = pedidoPadrao();
        pedido.setPagamento(null);

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertNull(dto.getPagamento());
    }

    @Test
    @DisplayName("fromEntity deve converter Pedido com status CANCELADO")
    void deveConverterPedidoComStatusCancelado() {
        Pedido pedido = Pedido.builder()
                .id(ID).dataCriacao(DATA_CRIACAO).status(StatusPedido.CANCELADO)
                .valorBruto(VALOR_BRUTO).desconto(DESCONTO).valorFinal(VALOR_FINAL)
                .cliente(clienteBase).itens(new ArrayList<>()).build();

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertEquals(StatusPedido.CANCELADO, dto.getStatus());
    }

    @Test
    @DisplayName("fromEntity deve converter Pedido com status PAGO")
    void deveConverterPedidoComStatusPago() {
        Pedido pedido = Pedido.builder()
                .id(ID).dataCriacao(DATA_CRIACAO).status(StatusPedido.PAGO)
                .valorBruto(VALOR_BRUTO).desconto(DESCONTO).valorFinal(VALOR_FINAL)
                .cliente(clienteBase).itens(new ArrayList<>()).build();

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertEquals(StatusPedido.PAGO, dto.getStatus());
    }

    @Test
    @DisplayName("fromEntity deve converter Pedido com valores zerados")
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
                () -> assertNotNull(dto.getItens())
        );
    }

    @Test
    @DisplayName("fromEntity(pedido, metodoPagamento) deve sobrescrever pagamento com metodoPagamento")
    void fromEntity_ComMetodoPagamento_DeveSetarPagamento() {
        Pedido pedido = pedidoPadrao();

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido, MetodoPagamento.PIX);

        assertNotNull(dto.getPagamento());
        assertEquals(MetodoPagamento.PIX, dto.getPagamento().getMetodoPagamento());
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
    }

    // =========================================================
    // AllArgsConstructor
    // =========================================================

    @Test
    @DisplayName("AllArgsConstructor: deve criar instância com todos os campos")
    void allArgsConstructor_DeveCriarInstanciaCompleta() {
        // Ordem: id, dataCriacao, status, valorBruto, desconto, valorFinal,
        //        clienteId, itens, pagamento
        PedidoResponseDTO dto = new PedidoResponseDTO(
                ID, DATA_CRIACAO, STATUS,
                VALOR_BRUTO, DESCONTO, VALOR_FINAL,
                CLIENTE_ID, List.of(), null
        );

        assertAll("AllArgsConstructor",
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
    // Setters — atribuição de nulos
    // =========================================================

    @Test
    @DisplayName("Deve aceitar null em todos os setters")
    void deveAceitarNullEmTodosOsSetters() {
        PedidoResponseDTO dto = dtoPadraoValido();

        assertThatCode(() -> {
            dto.setId(null);
            dto.setDataCriacao(null);
            dto.setStatus(null);
            dto.setValorBruto(null);
            dto.setDesconto(null);
            dto.setValorFinal(null);
            dto.setClienteId(null);
            dto.setItens(null);
            dto.setPagamento(null);
        }).doesNotThrowAnyException();

        assertNull(dto.getId());
        assertNull(dto.getDataCriacao());
        assertNull(dto.getStatus());
        assertNull(dto.getValorBruto());
        assertNull(dto.getDesconto());
        assertNull(dto.getValorFinal());
        assertNull(dto.getClienteId());
        assertNull(dto.getItens());
        assertNull(dto.getPagamento());
    }

    // =========================================================
    // equals / hashCode — objetos diferentes
    // =========================================================

    @Test
    @DisplayName("Objetos com id diferentes não devem ser iguais")
    void objetosComIdDiferentesNaoDevemSerIguais() {
        PedidoResponseDTO dto1 = PedidoResponseDTO.builder().id(1L).build();
        PedidoResponseDTO dto2 = PedidoResponseDTO.builder().id(2L).build();
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objetos com status diferentes não devem ser iguais")
    void objetosComStatusDiferentesNaoDevemSerIguais() {
        PedidoResponseDTO dto1 = PedidoResponseDTO.builder().id(ID).status(StatusPedido.CRIADO).build();
        PedidoResponseDTO dto2 = PedidoResponseDTO.builder().id(ID).status(StatusPedido.PAGO).build();
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objetos com valorBruto diferentes não devem ser iguais")
    void objetosComValorBrutoDiferentesNaoDevemSerIguais() {
        PedidoResponseDTO dto1 = PedidoResponseDTO.builder().id(ID).valorBruto(new BigDecimal("100.00")).build();
        PedidoResponseDTO dto2 = PedidoResponseDTO.builder().id(ID).valorBruto(new BigDecimal("200.00")).build();
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objetos com desconto diferentes não devem ser iguais")
    void objetosComDescontoDiferentesNaoDevemSerIguais() {
        PedidoResponseDTO dto1 = PedidoResponseDTO.builder().id(ID).desconto(new BigDecimal("10.00")).build();
        PedidoResponseDTO dto2 = PedidoResponseDTO.builder().id(ID).desconto(new BigDecimal("20.00")).build();
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objetos com valorFinal diferentes não devem ser iguais")
    void objetosComValorFinalDiferentesNaoDevemSerIguais() {
        PedidoResponseDTO dto1 = PedidoResponseDTO.builder().id(ID).valorFinal(new BigDecimal("90.00")).build();
        PedidoResponseDTO dto2 = PedidoResponseDTO.builder().id(ID).valorFinal(new BigDecimal("180.00")).build();
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objetos com clienteId diferentes não devem ser iguais")
    void objetosComClienteIdDiferentesNaoDevemSerIguais() {
        PedidoResponseDTO dto1 = PedidoResponseDTO.builder().id(ID).clienteId(1L).build();
        PedidoResponseDTO dto2 = PedidoResponseDTO.builder().id(ID).clienteId(2L).build();
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Objeto não deve ser igual a null")
    void objetoNaoDeveSerIgualANull() {
        assertNotEquals(null, dtoPadraoValido());
    }

    @Test
    @DisplayName("Objeto não deve ser igual a tipo diferente")
    void objetoNaoDeveSerIgualATipoDiferente() {
        assertNotEquals("string", dtoPadraoValido());
        assertNotEquals(42, dtoPadraoValido());
    }

    @Test
    @DisplayName("Objetos com mesmos dados devem ser iguais")
    void objetosComMesmosDadosDevemSerIguais() {
        PedidoResponseDTO dto1 = dtoPadraoValido();
        PedidoResponseDTO dto2 = dtoPadraoValido();
        assertEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Mesma instância deve ser igual a si mesma")
    void mesmaInstanciaDeveSerIgualASiMesma() {
        PedidoResponseDTO dto = dtoPadraoValido();
        assertEquals(dto, dto);
    }

    // =========================================================
    // hashCode
    // =========================================================

    @Test
    @DisplayName("hashCode: objetos iguais devem ter mesmo hashCode")
    void hashCode_ObjetosIguais_DeveTerMesmoHashCode() {
        assertEquals(dtoPadraoValido().hashCode(), dtoPadraoValido().hashCode());
    }

    @Test
    @DisplayName("hashCode: não deve lançar exceção com campos nulos")
    void hashCode_CamposNull_NaoDeveLancarExcecao() {
        assertThatCode(() -> new PedidoResponseDTO().hashCode()).doesNotThrowAnyException();
    }

    // =========================================================
    // toString
    // =========================================================

    @Test
    @DisplayName("toString não deve lançar exceção")
    void toString_NaoDeveLancarExcecao() {
        assertThatCode(() -> dtoPadraoValido().toString()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("toString não deve lançar exceção com campos nulos")
    void toString_NaoDeveLancarExcecao_QuandoCamposNulos() {
        assertThatCode(() -> new PedidoResponseDTO().toString()).doesNotThrowAnyException();
    }

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