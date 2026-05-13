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

@DisplayName("PedidoResponseDTO - Testes Positivos")
class PedidoResponseDTOPositiveTests {

    private static final LocalDateTime DATA_CRIACAO = LocalDateTime.of(2024, 6, 10, 14, 30);
    private static final Long ID = 1L;
    private static final Long CLIENTE_ID = 10L;
    private static final BigDecimal VALOR_TOTAL = new BigDecimal("500.00");
    private static final BigDecimal MULTA = new BigDecimal("50.00");
    private static final StatusPedido STATUS = StatusPedido.CRIADO;

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

        produtoBase = new Produto();
        produtoBase.setId(1L);
        produtoBase.setNome("Notebook");
    }

    // -------------------------------------------------------------------------
    // Construtores
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve criar objeto com construtor padrão")
    void deveCriarComConstrutorPadrao() {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        assertNotNull(dto);
    }

    @Test
    @DisplayName("Deve criar objeto com construtor completo e todos os campos")
    void deveCriarComConstrutorCompleto() {
        List<ItemPedidoResponseDTO> itens = List.of();

        PedidoResponseDTO dto = new PedidoResponseDTO(
                ID, DATA_CRIACAO, STATUS, VALOR_TOTAL, MULTA, CLIENTE_ID, itens
        );

        assertEquals(ID, dto.getId());
        assertEquals(DATA_CRIACAO, dto.getDataCriacao());
        assertEquals(STATUS, dto.getStatus());
        assertEquals(VALOR_TOTAL, dto.getValorTotal());
        assertEquals(MULTA, dto.getMultaCancelamento());
        assertEquals(CLIENTE_ID, dto.getClienteId());
        assertEquals(itens, dto.getItens());
    }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve criar objeto via builder com todos os campos")
    void deveCriarViaBuilderCompleto() {
        List<ItemPedidoResponseDTO> itens = List.of();

        PedidoResponseDTO dto = PedidoResponseDTO.builder()
                .id(ID)
                .dataCriacao(DATA_CRIACAO)
                .status(STATUS)
                .valorTotal(VALOR_TOTAL)
                .multaCancelamento(MULTA)
                .clienteId(CLIENTE_ID)
                .itens(itens)
                .build();

        assertEquals(ID, dto.getId());
        assertEquals(DATA_CRIACAO, dto.getDataCriacao());
        assertEquals(STATUS, dto.getStatus());
        assertEquals(VALOR_TOTAL, dto.getValorTotal());
        assertEquals(MULTA, dto.getMultaCancelamento());
        assertEquals(CLIENTE_ID, dto.getClienteId());
        assertEquals(itens, dto.getItens());
    }

    // -------------------------------------------------------------------------
    // Setters e Getters
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve definir e obter todos os campos via setters")
    void deveDefinirEObterTodosCamposViaSetters() {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        List<ItemPedidoResponseDTO> itens = List.of(ItemPedidoResponseDTO.builder().id(1L).build());

        dto.setId(ID);
        dto.setDataCriacao(DATA_CRIACAO);
        dto.setStatus(STATUS);
        dto.setValorTotal(VALOR_TOTAL);
        dto.setMultaCancelamento(MULTA);
        dto.setClienteId(CLIENTE_ID);
        dto.setItens(itens);

        assertEquals(ID, dto.getId());
        assertEquals(DATA_CRIACAO, dto.getDataCriacao());
        assertEquals(STATUS, dto.getStatus());
        assertEquals(VALOR_TOTAL, dto.getValorTotal());
        assertEquals(MULTA, dto.getMultaCancelamento());
        assertEquals(CLIENTE_ID, dto.getClienteId());
        assertEquals(itens, dto.getItens());
    }

    // -------------------------------------------------------------------------
    // fromEntity — branch itens != null (lista com dados)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve converter Pedido com um item para DTO corretamente")
    void deveConverterPedidoComUmItem() {
        ItemPedido item = new ItemPedido();
        item.setId(1L);
        item.setProduto(produtoBase);
        item.setQuantidade(2);
        item.setPrecoUnitario(new BigDecimal("250.00"));

        Pedido pedido = Pedido.builder()
                .id(ID)
                .dataCriacao(DATA_CRIACAO)
                .status(STATUS)
                .valorTotal(VALOR_TOTAL)
                .multaCancelamento(MULTA)
                .cliente(clienteBase)
                .itens(new ArrayList<>(List.of(item)))
                .build();

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertNotNull(dto);
        assertEquals(ID, dto.getId());
        assertEquals(DATA_CRIACAO, dto.getDataCriacao());
        assertEquals(STATUS, dto.getStatus());
        assertEquals(VALOR_TOTAL, dto.getValorTotal());
        assertEquals(MULTA, dto.getMultaCancelamento());
        assertEquals(CLIENTE_ID, dto.getClienteId());
        assertEquals(1, dto.getItens().size());
    }

    @Test
    @DisplayName("Deve converter Pedido com múltiplos itens")
    void deveConverterPedidoComMultiplosItens() {
        Produto produto2 = new Produto();
        produto2.setId(2L);
        produto2.setNome("Mouse");

        ItemPedido item1 = new ItemPedido();
        item1.setId(1L);
        item1.setProduto(produtoBase);
        item1.setQuantidade(2);
        item1.setPrecoUnitario(new BigDecimal("250.00"));

        ItemPedido item2 = new ItemPedido();
        item2.setId(2L);
        item2.setProduto(produto2);
        item2.setQuantidade(1);
        item2.setPrecoUnitario(new BigDecimal("80.00"));

        Pedido pedido = Pedido.builder()
                .id(ID)
                .dataCriacao(DATA_CRIACAO)
                .status(STATUS)
                .valorTotal(new BigDecimal("580.00"))
                .multaCancelamento(BigDecimal.ZERO)
                .cliente(clienteBase)
                .itens(new ArrayList<>(List.of(item1, item2)))
                .build();

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertEquals(2, dto.getItens().size());
    }

    @Test
    @DisplayName("Deve converter Pedido com lista de itens vazia")
    void deveConverterPedidoComItensVazios() {
        Pedido pedido = Pedido.builder()
                .id(ID)
                .dataCriacao(DATA_CRIACAO)
                .status(STATUS)
                .valorTotal(VALOR_TOTAL)
                .multaCancelamento(MULTA)
                .cliente(clienteBase)
                .itens(new ArrayList<>())
                .build();

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertNotNull(dto.getItens());
        assertTrue(dto.getItens().isEmpty());
    }

    @Test
    @DisplayName("Deve converter Pedido com status RESERVADO")
    void deveConverterPedidoComStatusReservado() {
        Pedido pedido = Pedido.builder()
                .id(ID).dataCriacao(DATA_CRIACAO).status(StatusPedido.RESERVADO)
                .valorTotal(VALOR_TOTAL).multaCancelamento(MULTA)
                .cliente(clienteBase).itens(new ArrayList<>()).build();

        assertEquals(StatusPedido.RESERVADO, PedidoResponseDTO.fromEntity(pedido).getStatus());
    }

    @Test
    @DisplayName("Deve converter Pedido com status PAGO")
    void deveConverterPedidoComStatusPago() {
        Pedido pedido = Pedido.builder()
                .id(ID).dataCriacao(DATA_CRIACAO).status(StatusPedido.PAGO)
                .valorTotal(VALOR_TOTAL).multaCancelamento(MULTA)
                .cliente(clienteBase).itens(new ArrayList<>()).build();

        assertEquals(StatusPedido.PAGO, PedidoResponseDTO.fromEntity(pedido).getStatus());
    }

    @Test
    @DisplayName("Deve converter Pedido com valorTotal zero")
    void deveConverterPedidoComValorTotalZero() {
        Pedido pedido = Pedido.builder()
                .id(ID).dataCriacao(DATA_CRIACAO).status(STATUS)
                .valorTotal(BigDecimal.ZERO).multaCancelamento(BigDecimal.ZERO)
                .cliente(clienteBase).itens(new ArrayList<>()).build();

        PedidoResponseDTO dto = PedidoResponseDTO.fromEntity(pedido);

        assertEquals(BigDecimal.ZERO, dto.getValorTotal());
        assertEquals(BigDecimal.ZERO, dto.getMultaCancelamento());
    }

    // -------------------------------------------------------------------------
    // equals / hashCode
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Dois objetos com mesmos valores devem ser iguais")
    void doisObjetosComMesmosValoresDevemSerIguais() {
        PedidoResponseDTO dto1 = PedidoResponseDTO.builder()
                .id(ID).dataCriacao(DATA_CRIACAO).status(STATUS)
                .valorTotal(VALOR_TOTAL).multaCancelamento(MULTA)
                .clienteId(CLIENTE_ID).itens(List.of()).build();

        PedidoResponseDTO dto2 = PedidoResponseDTO.builder()
                .id(ID).dataCriacao(DATA_CRIACAO).status(STATUS)
                .valorTotal(VALOR_TOTAL).multaCancelamento(MULTA)
                .clienteId(CLIENTE_ID).itens(List.of()).build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Objeto deve ser igual a si mesmo")
    void objetoDeveSerIgualASiMesmo() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder().id(ID).build();
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

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("toString não deve ser nulo")
    void toStringNaoDeveSerNulo() {
        assertNotNull(new PedidoResponseDTO().toString());
    }

    @Test
    @DisplayName("toString deve conter os valores dos campos preenchidos")
    void toStringDeveConterValores() {
        PedidoResponseDTO dto = PedidoResponseDTO.builder()
                .id(ID).status(STATUS).valorTotal(VALOR_TOTAL).build();

        String str = dto.toString();
        assertTrue(str.contains(String.valueOf(ID)));
        assertTrue(str.contains(STATUS.name()));
    }
}