package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ItemPedidoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.PedidoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.PedidoResponseDTO;
import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.ItemPedido;
import acc.br.projetoFinal.Accenture.model.Pedido;
import acc.br.projetoFinal.Accenture.model.Produto;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.repository.ItemPedidoRepository;
import acc.br.projetoFinal.Accenture.repository.PedidoRepository;
import acc.br.projetoFinal.Accenture.repository.ProdutoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("PedidoService - Testes Positivos")
class PedidoServiceTests {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ItemPedidoRepository itemPedidoRepository;

    @Mock
    private ContaService contaService;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private PedidoService pedidoService;

    private Cliente cliente;
    private Produto produto;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        cliente = Cliente.builder()
                .id(1L)
                .nome("João Silva")
                .email("joao@test.com")
                .build();

        produto = Produto.builder()
                .id(1L)
                .nome("Notebook")
                .preco(new BigDecimal("3000.00"))
                .quantidadeEstoque(10)
                .build();

        pedido = Pedido.builder()
                .id(1L)
                .cliente(cliente)
                .status(StatusPedido.CRIADO)
                .valorTotal(new BigDecimal("3000.00"))
                .multaCancelamento(BigDecimal.ZERO)
                .build();
    }

    @Test
    @DisplayName("✓ Deve criar um pedido com sucesso")
    void testCriarPedidoComSucesso() {
        // Arrange
        ItemPedidoRequestDTO itemDto = ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(1)
                .build();

        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(1L)
                .itens(Arrays.asList(itemDto))
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(itemPedidoRepository.save(any(ItemPedido.class))).thenReturn(ItemPedido.builder().build());

        // Act
        PedidoResponseDTO resultado = pedidoService.criar(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(pedidoRepository, times(2)).save(any(Pedido.class));
        verify(itemPedidoRepository, times(1)).save(any(ItemPedido.class));
    }

    @Test
    @DisplayName("✓ Deve criar pedido com múltiplos itens")
    void testCriarPedidoComMultiplosItens() {
        // Arrange
        ItemPedidoRequestDTO item1 = ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(2)
                .build();

        Produto produto2 = Produto.builder()
                .id(2L)
                .nome("Mouse")
                .preco(new BigDecimal("50.00"))
                .quantidadeEstoque(20)
                .build();

        ItemPedidoRequestDTO item2 = ItemPedidoRequestDTO.builder()
                .produtoId(2L)
                .quantidade(3)
                .build();

        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(1L)
                .itens(Arrays.asList(item1, item2))
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.findById(2L)).thenReturn(Optional.of(produto2));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(itemPedidoRepository.save(any(ItemPedido.class))).thenReturn(ItemPedido.builder().build());

        // Act
        PedidoResponseDTO resultado = pedidoService.criar(dto);

        // Assert
        assertNotNull(resultado);
        verify(itemPedidoRepository, times(2)).save(any(ItemPedido.class));
    }

    @Test
    @DisplayName("✓ Deve buscar pedido por ID com sucesso")
    void testBuscarPedidoPorIdComSucesso() {
        // Arrange
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Act
        PedidoResponseDTO resultado = pedidoService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("✓ Deve listar pedidos por cliente com sucesso")
    void testListarPedidosPorClienteComSucesso() {
        // Arrange
        List<Pedido> pedidos = Arrays.asList(pedido);
        when(pedidoRepository.findByClienteId(1L)).thenReturn(pedidos);

        // Act
        List<PedidoResponseDTO> resultado = pedidoService.listarPorCliente(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(pedidoRepository, times(1)).findByClienteId(1L);
    }

    @Test
    @DisplayName("✓ Deve listar todos os pedidos com sucesso")
    void testListarTodosPedidosComSucesso() {
        // Arrange
        List<Pedido> pedidos = Arrays.asList(pedido);
        when(pedidoRepository.findAll()).thenReturn(pedidos);

        // Act
        List<PedidoResponseDTO> resultado = pedidoService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("✓ Deve reservar pedido com sucesso")
    void testReservarPedidoComSucesso() {
        // Arrange
        ItemPedido item = ItemPedido.builder()
                .pedido(pedido)
                .produto(produto)
                .quantidade(2)
                .precoUnitario(new BigDecimal("3000.00"))
                .build();
        pedido.setItens(Arrays.asList(item));

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        doNothing().when(entityManager).flush();
        doNothing().when(entityManager).refresh(any(Produto.class));

        // Act
        PedidoResponseDTO resultado = pedidoService.reservarPedido(1L);

        // Assert
        assertNotNull(resultado);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("✓ Deve pagar pedido com sucesso")
    void testPagarPedidoComSucesso() {
        // Arrange
        pedido.setStatus(StatusPedido.RESERVADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        doNothing().when(contaService).transferir(1L, new BigDecimal("3000.00"), pedido);

        // Act
        PedidoResponseDTO resultado = pedidoService.pagarPedido(1L);

        // Assert
        assertNotNull(resultado);
        verify(contaService, times(1)).transferir(1L, new BigDecimal("3000.00"), pedido);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("✓ Deve cancelar pedido CRIADO com sucesso")
    void testCancelarPedidoCriadoComSucesso() {
        // Arrange
        pedido.setStatus(StatusPedido.CRIADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        // Act
        PedidoResponseDTO resultado = pedidoService.cancelarPedido(1L);

        // Assert
        assertNotNull(resultado);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("✓ Deve cancelar pedido RESERVADO com devolução de estoque")
    void testCancelarPedidoReservadoComDevolucaoEstoque() {
        // Arrange
        pedido.setStatus(StatusPedido.RESERVADO);
        ItemPedido item = ItemPedido.builder()
                .pedido(pedido)
                .produto(produto)
                .quantidade(2)
                .precoUnitario(new BigDecimal("3000.00"))
                .build();
        pedido.setItens(Arrays.asList(item));

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        // Act
        PedidoResponseDTO resultado = pedidoService.cancelarPedido(1L);

        // Assert
        assertNotNull(resultado);
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("✓ Deve cancelar pedido PAGO com multa e estorno")
    void testCancelarPedidoPagoComMultaEstorno() {
        // Arrange
        pedido.setStatus(StatusPedido.PAGO);
        ItemPedido item = ItemPedido.builder()
                .pedido(pedido)
                .produto(produto)
                .quantidade(1)
                .precoUnitario(new BigDecimal("3000.00"))
                .build();
        pedido.setItens(Arrays.asList(item));

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        doNothing().when(contaService).estornarComMulta(anyLong(), any(BigDecimal.class), 
                any(BigDecimal.class), any(Pedido.class));

        // Act
        PedidoResponseDTO resultado = pedidoService.cancelarPedido(1L);

        // Assert
        assertNotNull(resultado);
        verify(contaService, times(1)).estornarComMulta(anyLong(), any(BigDecimal.class),
                any(BigDecimal.class), any(Pedido.class));
    }
}
