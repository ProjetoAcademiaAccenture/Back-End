package acc.br.projetoFinal.Accenture.dto.response;

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

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PedidoResponseDTO - Testes Negativos")
class PedidoResponseDTONegativeTests {

    private static final LocalDateTime DATA_CRIACAO = LocalDateTime.of(2024, 6, 10, 14, 30);
    private static final Long ID = 1L;
    private static final Long CLIENTE_ID = 10L;
    private static final BigDecimal VALOR_TOTAL = new BigDecimal("500.00");
    private static final BigDecimal MULTA = new BigDecimal("50.00");
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

    // -------------------------------------------------------------------------
    // Construtor padrão — campos nulos
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve criar objeto com construtor padrão com todos os campos nulos")
    void deveCriarComConstrutorPadraoComCamposNulos() {
        PedidoResponseDTO dto = new PedidoResponseDTO();

        assertNull(dto.getId());
        assertNull(dto.getDataCriacao());
        assertNull(dto.getStatus());
        assertNull(dto.getValorTotal());
        assertNull(dto.getMultaCancelamento());
        assertNull(dto.getClienteId());
        assertNull(dto.getItens());
    }

    // -------------------------------------------------------------------------
    // Builder — campos nulos individualmente
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve criar objeto via builder vazio com todos os campos nulos")
    void deveCriarViaBuilderVazioComCamposNulos() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder().build();

        assertNull(dto.getId());
        assertNull(dto.getDataCriacao());
        assertNull(dto.getStatus());
        assertNull(dto.getValorTotal());
        assertNull(dto.getMultaCancelamento());
        assertNull(dto.getClienteId());
        assertNull(dto.getItens());
    }

    @Test
    @DisplayName("Deve criar objeto com id null via builder")
    void deveCriarComIdNull() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder()
                .id(null).status(STATUS).valorTotal(VALOR_TOTAL)
                .multaCancelamento(MULTA).clienteId(CLIENTE_ID).build();

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
    @DisplayName("Deve criar objeto com valorTotal null via builder")
    void deveCriarComValorTotalNull() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder()
                .id(ID).valorTotal(null).build();

        assertNull(dto.getValorTotal());
    }

    @Test
    @DisplayName("Deve criar objeto com multaCancelamento null via builder")
    void deveCriarComMultaCancelamentoNull() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder()
                .id(ID).multaCancelamento(null).build();

        assertNull(dto.getMultaCancelamento());
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

    // -------------------------------------------------------------------------
    // Valores inválidos / extremos
    // -------------------------------------------------------------------------

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
    @DisplayName("Deve aceitar valorTotal negativo")
    void deveAceitarValorTotalNegativo() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder()
                .valorTotal(new BigDecimal("-100.00")).build();

        assertTrue(dto.getValorTotal().signum() < 0);
    }

    @Test
    @DisplayName("Deve aceitar multaCancelamento negativa")
    void deveAceitarMultaCancelamentoNegativa() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder()
                .multaCancelamento(new BigDecimal("-10.00")).build();

        assertTrue(dto.getMultaCancelamento().signum() < 0);
    }

    @Test
    @DisplayName("Deve aceitar valorTotal muito grande")
    void deveAceitarValorTotalMuitoGrande() {
        BigDecimal valorGrande = new BigDecimal("999999999.99");
        PedidoResponseDTO dto = PedidoResponseDTO.builder().valorTotal(valorGrande).build();
        assertEquals(valorGrande, dto.getValorTotal());
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

    // -------------------------------------------------------------------------
    // fromEntity — branch itens == null (cobre o else do ternário)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("fromEntity deve retornar lista vazia quando itens for null")
    void deveRetornarListaVaziaQuandoItensNull() {
        Pedido pedido = Pedido.builder()
                .id(ID).dataCriacao(DATA_CRIACAO).status(STATUS)
                .valorTotal(VALOR_TOTAL).multaCancelamento(MULTA)
                .cliente(clienteBase).build();
        pedido.setItens(null);

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertNotNull(dto.getItens());
        assertTrue(dto.getItens().isEmpty());
    }

    @Test
    @DisplayName("fromEntity deve converter Pedido com status CANCELADO")
    void deveConverterPedidoComStatusCancelado() {
        Pedido pedido = Pedido.builder()
                .id(ID).dataCriacao(DATA_CRIACAO).status(StatusPedido.CANCELADO)
                .valorTotal(VALOR_TOTAL).multaCancelamento(MULTA)
                .cliente(clienteBase).itens(new ArrayList<>()).build();

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertEquals(StatusPedido.CANCELADO, dto.getStatus());
    }

    @Test
    @DisplayName("fromEntity deve converter Pedido com valorTotal e multa zerados")
    void deveConverterPedidoComValoresZerados() {
        Pedido pedido = Pedido.builder()
                .id(ID).dataCriacao(DATA_CRIACAO).status(STATUS)
                .valorTotal(BigDecimal.ZERO).multaCancelamento(BigDecimal.ZERO)
                .cliente(clienteBase).itens(new ArrayList<>()).build();

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertEquals(BigDecimal.ZERO, dto.getValorTotal());
        assertEquals(BigDecimal.ZERO, dto.getMultaCancelamento());
    }

    // -------------------------------------------------------------------------
    // equals / hashCode — objetos diferentes
    // -------------------------------------------------------------------------

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
    @DisplayName("Objetos com valorTotal diferentes não devem ser iguais")
    void objetosComValorTotalDiferentesNaoDevemSerIguais() {
        PedidoResponseDTO dto1 = PedidoResponseDTO.builder().id(ID).valorTotal(new BigDecimal("100.00")).build();
        PedidoResponseDTO dto2 = PedidoResponseDTO.builder().id(ID).valorTotal(new BigDecimal("200.00")).build();
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
        PedidoResponseDTO dto = PedidoResponseDTO.builder().id(ID).build();
        assertNotEquals(null, dto);
    }

    @Test
    @DisplayName("Objeto não deve ser igual a tipo diferente")
    void objetoNaoDeveSerIgualATipoDiferente() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder().id(ID).build();
        assertNotEquals("string", dto);
    }

    // -------------------------------------------------------------------------
    // Setters — atribuição de nulos
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve aceitar null em todos os setters")
    void deveAceitarNullEmTodosOsSetters() {
        PedidoResponseDTO dto = new PedidoResponseDTO(
                ID, DATA_CRIACAO, STATUS, VALOR_TOTAL, MULTA, CLIENTE_ID, List.of()
        );

        dto.setId(null);
        dto.setDataCriacao(null);
        dto.setStatus(null);
        dto.setValorTotal(null);
        dto.setMultaCancelamento(null);
        dto.setClienteId(null);
        dto.setItens(null);

        assertNull(dto.getId());
        assertNull(dto.getDataCriacao());
        assertNull(dto.getStatus());
        assertNull(dto.getValorTotal());
        assertNull(dto.getMultaCancelamento());
        assertNull(dto.getClienteId());
        assertNull(dto.getItens());
    }
}