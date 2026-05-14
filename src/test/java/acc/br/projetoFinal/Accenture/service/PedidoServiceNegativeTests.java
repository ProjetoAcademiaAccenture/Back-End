package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ItemPedidoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.PedidoRequestDTO;
import acc.br.projetoFinal.Accenture.exception.CancelamentoException;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.Pagamento;
import acc.br.projetoFinal.Accenture.model.Pedido;
import acc.br.projetoFinal.Accenture.model.Produto;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.repository.PedidoRepository;
import acc.br.projetoFinal.Accenture.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;
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
    private EstoqueService estoqueService;

    @Mock
    private PagamentoService pagamentoService;

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
                .valorBruto(new BigDecimal("3000.00"))
                .desconto(BigDecimal.ZERO)
                .valorFinal(new BigDecimal("3000.00"))
                .build();
    }

    // -------------------------------------------------------------------------
    // criar()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("✗ Deve lançar exceção ao criar pedido com cliente inexistente")
    void testCriarPedidoClienteInexistente() {
        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(99L)
                .metodoPagamento("CARTAO_CREDITO")
                .itens(List.of(ItemPedidoRequestDTO.builder()
                        .produtoId(1L).quantidade(1).build()))
                .build();

        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> pedidoService.criar(dto));

        verify(clienteRepository).findById(99L);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao criar pedido com produto inexistente")
    void testCriarPedidoProdutoInexistente() {
        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(1L)
                .metodoPagamento("CARTAO_CREDITO")
                .itens(List.of(ItemPedidoRequestDTO.builder()
                        .produtoId(99L).quantidade(1).build()))
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> pedidoService.criar(dto));

        verify(produtoRepository).findById(99L);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("✗ Deve lançar IllegalArgumentException ao criar pedido com estoque insuficiente")
    void testCriarPedidoEstoqueInsuficiente() {
        // PedidoService lança IllegalArgumentException (não EstoqueInsuficienteException)
        // quando produto.getQuantidadeEstoque() < itemDto.getQuantidade()
        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(1L)
                .metodoPagamento("CARTAO_CREDITO")
                .itens(List.of(ItemPedidoRequestDTO.builder()
                        .produtoId(1L).quantidade(20).build())) // estoque = 10
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        assertThrows(IllegalArgumentException.class, () -> pedidoService.criar(dto));

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("✗ Deve lançar IllegalArgumentException ao criar pedido com método de pagamento inválido")
    void testCriarPedidoMetodoPagamentoInvalido() {
        // Produto tem estoque suficiente; o erro vem do MetodoPagamento.valueOf()
        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(1L)
                .metodoPagamento("METODO_INVALIDO")
                .itens(List.of(ItemPedidoRequestDTO.builder()
                        .produtoId(1L).quantidade(1).build()))
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        assertThrows(IllegalArgumentException.class, () -> pedidoService.criar(dto));
    }

    // -------------------------------------------------------------------------
    // buscarPorId()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("✗ Deve lançar exceção ao buscar pedido com ID inexistente")
    void testBuscarPedidoInexistente() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> pedidoService.buscarPorId(999L));

        verify(pedidoRepository).findById(999L);
    }

    // -------------------------------------------------------------------------
    // listarPorCliente()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("✗ Deve lançar RecursoNaoEncontradoException ao listar pedidos de cliente inexistente")
    void testListarPorClienteClienteInexistente() throws AccessDeniedException {
        Authentication auth = mock(Authentication.class);
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("joao@test.com");
        SecurityContextHolder.setContext(ctx);

        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> pedidoService.listarPorCliente(99L));

        verify(clienteRepository).findById(99L);
    }

    @Test
    @DisplayName("✗ Deve lançar AccessDeniedException ao listar pedidos de outro cliente")
    void testListarPorClienteAcessoNegado() {
        Authentication auth = mock(Authentication.class);
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("outro@test.com"); // email diferente do cliente
        SecurityContextHolder.setContext(ctx);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        // AccessDeniedException é checked — usar assertThrows com throws na lambda
        assertThrows(AccessDeniedException.class,
                () -> pedidoService.listarPorCliente(1L));
    }

    // -------------------------------------------------------------------------
    // cancelarPedido()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("✗ Deve lançar RecursoNaoEncontradoException ao cancelar pedido inexistente")
    void testCancelarPedidoInexistente() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> pedidoService.cancelarPedido(999L));

        verify(pedidoRepository).findById(999L);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("✗ Deve lançar CancelamentoException ao cancelar pedido já cancelado")
    void testCancelarPedidoJaCancelado() {
        pedido.setStatus(StatusPedido.CANCELADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(CancelamentoException.class,
                () -> pedidoService.cancelarPedido(1L));

        verify(pedidoRepository).findById(1L);
        verify(estoqueService, never()).devolverItens(any());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("✗ Não deve chamar devolverItens ao cancelar pedido com status CRIADO")
    void testCancelarPedidoCriadoNaoDevolveEstoque() {
        pedido.setStatus(StatusPedido.CRIADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        pedidoService.cancelarPedido(1L);

        verify(estoqueService, never()).devolverItens(any());
        verify(pedidoRepository).save(any());
    }

    @Test
    @DisplayName("✗ Deve lançar exceção se EstoqueService falhar ao cancelar pedido RESERVADO")
    void testCancelarPedidoReservadoEstoqueServiceFalha() {
        pedido.setStatus(StatusPedido.RESERVADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        doThrow(new RuntimeException("Erro ao devolver estoque"))
                .when(estoqueService).devolverItens(any(Pedido.class));

        assertThrows(RuntimeException.class,
                () -> pedidoService.cancelarPedido(1L));

        verify(estoqueService).devolverItens(pedido);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("✗ Deve lançar exceção se EstoqueService falhar ao cancelar pedido PAGO")
    void testCancelarPedidoPagoEstoqueServiceFalha() {
        pedido.setStatus(StatusPedido.PAGO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        doThrow(new RuntimeException("Erro crítico de estoque"))
                .when(estoqueService).devolverItens(any(Pedido.class));

        assertThrows(RuntimeException.class,
                () -> pedidoService.cancelarPedido(1L));

        verify(estoqueService).devolverItens(pedido);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("✗ Deve lançar exceção se PagamentoService falhar ao cancelar pedido")
    void testCancelarPedidoPagamentoServiceFalha() {
        Pagamento pagamento = Pagamento.builder().id(1L).build();
        pedido.setStatus(StatusPedido.CRIADO);
        pedido.setPagamento(pagamento);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        doThrow(new RuntimeException("Erro ao cancelar pagamento"))
                .when(pagamentoService).cancelar(1L);

        assertThrows(RuntimeException.class,
                () -> pedidoService.cancelarPedido(1L));

        verify(pagamentoService).cancelar(1L);
        verify(pedidoRepository, never()).save(any());
    }
}