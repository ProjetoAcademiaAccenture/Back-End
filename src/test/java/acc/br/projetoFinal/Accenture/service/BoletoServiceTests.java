package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ItemPedidoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.PedidoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.PedidoResponseDTO;
import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import acc.br.projetoFinal.Accenture.exception.CancelamentoException;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.model.*;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.repository.PedidoRepository;
import acc.br.projetoFinal.Accenture.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes - PedidoService")
class PedidoServiceTests {

    @Mock private PedidoRepository pedidoRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private ProdutoRepository produtoRepository;
    @Mock private EstoqueService estoqueService;
    @Mock private PagamentoService pagamentoService;

    @InjectMocks
    private PedidoService pedidoService;

    private Cliente cliente;
    private Produto produto;
    private Pedido pedido;
    private Pagamento pagamento;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1L)
                .nome("João Silva")
                .email("joao@email.com")
                .build();

        produto = Produto.builder()
                .id(1L)
                .nome("Produto A")
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(10)
                .build();

        pagamento = Pagamento.builder()
                .id(1L)
                .build();

        pedido = Pedido.builder()
                .id(1L)
                .cliente(cliente)
                .status(StatusPedido.RESERVADO)
                .valorBruto(new BigDecimal("100.00"))
                .desconto(BigDecimal.ZERO)
                .valorFinal(new BigDecimal("100.00"))
                .pagamento(pagamento)
                .build();
    }

    // =========================================================
    // criar()
    // =========================================================

    @Test
    @DisplayName("criar: deve criar pedido com sucesso")
    void criar_deveCriarPedidoComSucesso() {
        ItemPedidoRequestDTO itemDto = new ItemPedidoRequestDTO();
        itemDto.setProdutoId(1L);
        itemDto.setQuantidade(2);

        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setClienteId(1L);
        dto.setItens(List.of(itemDto));
        dto.setMetodoPagamento("PIX");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(pagamentoService.calcularDesconto(any(), any(BigDecimal.class)))
                .thenReturn(new BigDecimal("10.00"));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        doNothing().when(estoqueService).reservarItens(any(Pedido.class));
        doNothing().when(pagamentoService).criarParaPedido(any(Pedido.class), anyString());

        PedidoResponseDTO resultado = pedidoService.criar(dto);

        assertThat(resultado).isNotNull();
        verify(pedidoRepository, times(2)).save(any(Pedido.class));
        verify(estoqueService, times(1)).reservarItens(any(Pedido.class));
        verify(pagamentoService, times(1)).criarParaPedido(any(Pedido.class), eq("PIX"));
    }

    @Test
    @DisplayName("criar: deve lançar exception quando cliente não encontrado")
    void criar_deveLancarException_quandoClienteNaoEncontrado() {
        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setClienteId(99L);
        dto.setItens(List.of());
        dto.setMetodoPagamento("PIX");

        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.criar(dto))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Cliente não encontrado");
    }

    @Test
    @DisplayName("criar: deve lançar exception quando produto não encontrado")
    void criar_deveLancarException_quandoProdutoNaoEncontrado() {
        ItemPedidoRequestDTO itemDto = new ItemPedidoRequestDTO();
        itemDto.setProdutoId(99L);
        itemDto.setQuantidade(1);

        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setClienteId(1L);
        dto.setItens(List.of(itemDto));
        dto.setMetodoPagamento("PIX");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.criar(dto))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Produto não encontrado");
    }

    @Test
    @DisplayName("criar: deve lançar exception quando estoque insuficiente")
    void criar_deveLancarException_quandoEstoqueInsuficiente() {
        produto.setQuantidadeEstoque(1);

        ItemPedidoRequestDTO itemDto = new ItemPedidoRequestDTO();
        itemDto.setProdutoId(1L);
        itemDto.setQuantidade(5); // mais do que o estoque

        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setClienteId(1L);
        dto.setItens(List.of(itemDto));
        dto.setMetodoPagamento("PIX");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        assertThatThrownBy(() -> pedidoService.criar(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Estoque insuficiente");
    }

    // =========================================================
    // buscarPorId()
    // =========================================================

    @Test
    @DisplayName("buscarPorId: deve retornar pedido quando existir")
    void buscarPorId_deveRetornarPedido_quandoExistir() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        PedidoResponseDTO resultado = pedidoService.buscarPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("buscarPorId: deve lançar exception quando pedido não encontrado")
    void buscarPorId_deveLancarException_quandoNaoEncontrado() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Pedido não encontrado");
    }

    // =========================================================
    // listarPorCliente()
    // =========================================================

    @Test
    @DisplayName("listarPorCliente: deve retornar lista quando email confere")
    void listarPorCliente_deveRetornarLista_quandoEmailConfere() throws AccessDeniedException {
        Authentication auth = mock(Authentication.class);
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("joao@email.com");
        SecurityContextHolder.setContext(ctx);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(pedidoRepository.findByClienteId(1L)).thenReturn(List.of(pedido));

        List<PedidoResponseDTO> resultado = pedidoService.listarPorCliente(1L);

        assertThat(resultado).hasSize(1);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("listarPorCliente: deve lançar AccessDeniedException quando email não confere")
    void listarPorCliente_deveLancarException_quandoEmailNaoConfere() {
        Authentication auth = mock(Authentication.class);
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("outro@email.com");
        SecurityContextHolder.setContext(ctx);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        assertThatThrownBy(() -> pedidoService.listarPorCliente(1L))
                .isInstanceOf(AccessDeniedException.class);

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("listarPorCliente: deve lançar exception quando cliente não encontrado")
    void listarPorCliente_deveLancarException_quandoClienteNaoEncontrado() {
        Authentication auth = mock(Authentication.class);
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("joao@email.com");
        SecurityContextHolder.setContext(ctx);

        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.listarPorCliente(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Cliente não encontrado");

        SecurityContextHolder.clearContext();
    }

    // =========================================================
    // listarTodos()
    // =========================================================

    @Test
    @DisplayName("listarTodos: deve retornar todos os pedidos")
    void listarTodos_deveRetornarTodosOsPedidos() {
        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));

        List<PedidoResponseDTO> resultado = pedidoService.listarTodos();

        assertThat(resultado).hasSize(1);
        verify(pedidoRepository, times(1)).findAll();
    }

    // =========================================================
    // cancelarPedido()
    // =========================================================

    @Test
    @DisplayName("cancelarPedido: deve cancelar pedido RESERVADO e devolver estoque")
    void cancelarPedido_deveCancelar_quandoReservado() {
        pedido.setStatus(StatusPedido.RESERVADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        PedidoResponseDTO resultado = pedidoService.cancelarPedido(1L);

        assertThat(resultado).isNotNull();
        assertThat(pedido.getStatus()).isEqualTo(StatusPedido.CANCELADO);
        verify(estoqueService, times(1)).devolverItens(pedido);
        verify(pagamentoService, times(1)).cancelar(1L);
    }

    @Test
    @DisplayName("cancelarPedido: deve cancelar pedido PAGO e devolver estoque")
    void cancelarPedido_deveCancelar_quandoPago() {
        pedido.setStatus(StatusPedido.PAGO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        pedidoService.cancelarPedido(1L);

        assertThat(pedido.getStatus()).isEqualTo(StatusPedido.CANCELADO);
        verify(estoqueService, times(1)).devolverItens(pedido);
    }

    @Test
    @DisplayName("cancelarPedido: deve cancelar pedido CRIADO sem devolver estoque")
    void cancelarPedido_deveCancelar_quandoCriado_semDevolverEstoque() {
        pedido.setStatus(StatusPedido.CRIADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        pedidoService.cancelarPedido(1L);

        assertThat(pedido.getStatus()).isEqualTo(StatusPedido.CANCELADO);
        verify(estoqueService, never()).devolverItens(any());
    }

    @Test
    @DisplayName("cancelarPedido: deve cancelar sem chamar pagamentoService quando pagamento é nulo")
    void cancelarPedido_naoDeveCancelarPagamento_quandoPagamentoNulo() {
        pedido.setStatus(StatusPedido.CRIADO);
        pedido.setPagamento(null);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        pedidoService.cancelarPedido(1L);

        verify(pagamentoService, never()).cancelar(anyLong());
    }

    @Test
    @DisplayName("cancelarPedido: deve lançar CancelamentoException quando já cancelado")
    void cancelarPedido_deveLancarException_quandoJaCancelado() {
        pedido.setStatus(StatusPedido.CANCELADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> pedidoService.cancelarPedido(1L))
                .isInstanceOf(CancelamentoException.class)
                .hasMessageContaining("já está cancelado");
    }

    @Test
    @DisplayName("cancelarPedido: deve lançar exception quando pedido não encontrado")
    void cancelarPedido_deveLancarException_quandoNaoEncontrado() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.cancelarPedido(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Pedido não encontrado");
    }
}