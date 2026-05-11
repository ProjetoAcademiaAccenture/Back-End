package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ItemPedidoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.PedidoRequestDTO;
import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import acc.br.projetoFinal.Accenture.exception.CancelamentoException;
import acc.br.projetoFinal.Accenture.exception.EstoqueInsuficienteException;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("PedidoService - Testes Negativos")
class PedidoServiceNegativeTests {

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
    @DisplayName("✗ Deve lançar exceção ao criar pedido com cliente inexistente")
    void testCriarPedidoClienteInexistente() {
        // Arrange
        ItemPedidoRequestDTO itemDto = ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(1)
                .build();

        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(1L)
                .itens(Arrays.asList(itemDto))
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNaoEncontradoException.class, () -> pedidoService.criar(dto));
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao criar pedido com produto inexistente")
    void testCriarPedidoProdutoInexistente() {
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
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNaoEncontradoException.class, () -> pedidoService.criar(dto));
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao criar pedido com estoque insuficiente")
    void testCriarPedidoEstoqueInsuficiente() {
        // Arrange
        ItemPedidoRequestDTO itemDto = ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(20) // Estoque tem apenas 10
                .build();

        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(1L)
                .itens(Arrays.asList(itemDto))
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        // Act & Assert
        assertThrows(EstoqueInsuficienteException.class, () -> pedidoService.criar(dto));
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao buscar pedido inexistente")
    void testBuscarPedidoInexistente() {
        // Arrange
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNaoEncontradoException.class, () -> pedidoService.buscarPorId(999L));
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao reservar pedido inexistente")
    void testReservarPedidoInexistente() {
        // Arrange
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNaoEncontradoException.class, () -> pedidoService.reservarPedido(999L));
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao reservar pedido não-CRIADO")
    void testReservarPedidoStatusInvalido() {
        // Arrange
        pedido.setStatus(StatusPedido.RESERVADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> pedidoService.reservarPedido(1L));
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao reservar com estoque insuficiente durante refresh")
    void testReservarPedidoEstoqueInsuficienteAposRefresh() {
        // Arrange
        ItemPedido item = ItemPedido.builder()
                .pedido(pedido)
                .produto(produto)
                .quantidade(15) // Mais que o estoque
                .precoUnitario(new BigDecimal("3000.00"))
                .build();
        pedido.setItens(Arrays.asList(item));

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        doNothing().when(entityManager).flush();
        doNothing().when(entityManager).refresh(any(Produto.class));

        // Act & Assert
        assertThrows(EstoqueInsuficienteException.class, () -> pedidoService.reservarPedido(1L));
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao pagar pedido inexistente")
    void testPagarPedidoInexistente() {
        // Arrange
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNaoEncontradoException.class, () -> pedidoService.pagarPedido(999L));
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao pagar pedido não-RESERVADO")
    void testPagarPedidoStatusInvalido() {
        // Arrange
        pedido.setStatus(StatusPedido.CRIADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> pedidoService.pagarPedido(1L));
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao cancelar pedido inexistente")
    void testCancelarPedidoInexistente() {
        // Arrange
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNaoEncontradoException.class, () -> pedidoService.cancelarPedido(999L));
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao cancelar pedido já cancelado")
    void testCancelarPedidoJaCancelado() {
        // Arrange
        pedido.setStatus(StatusPedido.CANCELADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Act & Assert
        assertThrows(CancelamentoException.class, () -> pedidoService.cancelarPedido(1L));
    }

    @Test
    @DisplayName("✗ Deve lançar exceção na transferência se ContaService falhar no pagamento")
    void testPagarPedidoComErroNaTransferencia() {
        // Arrange
        pedido.setStatus(StatusPedido.RESERVADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        doThrow(new IllegalArgumentException("Saldo insuficiente"))
                .when(contaService).transferir(1L, new BigDecimal("3000.00"), pedido);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> pedidoService.pagarPedido(1L));
    }

    @Test
    @DisplayName("✓ Deve permitir múltiplas tentativas de cancelamento com estados diferentes")
    void testCancelarPedidoPagoComItensReservados() {
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
        assertDoesNotThrow(() -> pedidoService.cancelarPedido(1L));
        
        // Assert
        verify(contaService, times(1)).estornarComMulta(anyLong(), any(BigDecimal.class),
                any(BigDecimal.class), any(Pedido.class));
    }
}
