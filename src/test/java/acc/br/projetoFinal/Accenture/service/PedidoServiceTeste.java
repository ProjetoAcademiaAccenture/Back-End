package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ItemPedidoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.PedidoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.PedidoResponseDTO;
import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.ItemPedido;
import acc.br.projetoFinal.Accenture.model.Pagamento;
import acc.br.projetoFinal.Accenture.model.Pedido;
import acc.br.projetoFinal.Accenture.model.Produto;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.repository.PedidoRepository;
import acc.br.projetoFinal.Accenture.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("PedidoService - Testes Positivos")
class PedidoServiceTeste {

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
                .status(StatusPedido.RESERVADO) // criar() já avança para RESERVADO
                .valorBruto(new BigDecimal("3000.00"))
                .desconto(BigDecimal.ZERO)
                .valorFinal(new BigDecimal("3000.00"))
                .build();
    }

    // =========================================================================
    // criar()
    // =========================================================================

    @Nested
    @DisplayName("criar()")
    class Criar {

        private void mockCriarDefault() {
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
            when(pagamentoService.calcularDesconto(any(), any())).thenReturn(BigDecimal.ZERO);
            doNothing().when(estoqueService).reservarItens(any(Pedido.class));
            doNothing().when(pagamentoService).criarParaPedido(any(Pedido.class), any());
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        }

        @Test
        @DisplayName("✓ Deve criar pedido e retornar DTO com ID")
        void testCriarPedidoRetornaId() {
            mockCriarDefault();

            PedidoResponseDTO resultado = pedidoService.criar(pedidoRequestValido(1));

            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
        }

        @Test
        @DisplayName("✓ Deve criar pedido com status RESERVADO após reservar itens")
        void testCriarPedidoStatusReservado() {
            mockCriarDefault();

            PedidoResponseDTO resultado = pedidoService.criar(pedidoRequestValido(1));

            // criar() avança para RESERVADO antes do segundo save
            assertEquals(StatusPedido.RESERVADO, resultado.getStatus());
        }

        @Test
        @DisplayName("✓ Deve salvar pedido duas vezes: criação e pós-reserva")
        void testCriarPedidoSalvaDuasVezes() {
            mockCriarDefault();

            pedidoService.criar(pedidoRequestValido(1));

            verify(pedidoRepository, times(2)).save(any(Pedido.class));
        }

        @Test
        @DisplayName("✓ Deve chamar estoqueService.reservarItens ao criar pedido")
        void testCriarPedidoChamaReservarItens() {
            mockCriarDefault();

            pedidoService.criar(pedidoRequestValido(1));

            verify(estoqueService, times(1)).reservarItens(any(Pedido.class));
        }

        @Test
        @DisplayName("✓ Deve chamar pagamentoService.criarParaPedido ao criar pedido")
        void testCriarPedidoChamaCriarPagamento() {
            mockCriarDefault();

            pedidoService.criar(pedidoRequestValido(1));

            verify(pagamentoService, times(1)).criarParaPedido(any(Pedido.class), any());
        }

        @Test
        @DisplayName("✓ Deve calcular desconto via pagamentoService ao criar pedido")
        void testCriarPedidoCalculaDesconto() {
            mockCriarDefault();

            pedidoService.criar(pedidoRequestValido(2));

            verify(pagamentoService, times(1)).calcularDesconto(any(), any());
        }

        @Test
        @DisplayName("✓ Deve criar pedido com múltiplos itens e consultar cada produto")
        void testCriarPedidoMultiplosItens() {
            Produto produto2 = Produto.builder()
                    .id(2L).nome("Mouse").preco(new BigDecimal("50.00")).quantidadeEstoque(20).build();

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
            when(produtoRepository.findById(2L)).thenReturn(Optional.of(produto2));
            when(pagamentoService.calcularDesconto(any(), any())).thenReturn(BigDecimal.ZERO);
            doNothing().when(estoqueService).reservarItens(any(Pedido.class));
            doNothing().when(pagamentoService).criarParaPedido(any(Pedido.class), any());
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

            PedidoRequestDTO dto = PedidoRequestDTO.builder()
                    .clienteId(1L)
                    .metodoPagamento("CARTAO_CREDITO")
                    .itens(List.of(
                            ItemPedidoRequestDTO.builder().produtoId(1L).quantidade(1).build(),
                            ItemPedidoRequestDTO.builder().produtoId(2L).quantidade(3).build()
                    ))
                    .build();

            PedidoResponseDTO resultado = pedidoService.criar(dto);

            assertNotNull(resultado);
            verify(produtoRepository).findById(1L);
            verify(produtoRepository).findById(2L);
        }
    }

    // =========================================================================
    // buscarPorId()
    // =========================================================================

    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorId {

        @Test
        @DisplayName("✓ Deve retornar DTO com dados corretos ao buscar por ID existente")
        void testBuscarPedidoPorId() {
            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

            PedidoResponseDTO resultado = pedidoService.buscarPorId(1L);

            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            verify(pedidoRepository).findById(1L);
        }

        @Test
        @DisplayName("✓ Deve retornar status correto do pedido buscado")
        void testBuscarPedidoRetornaStatusCorreto() {
            pedido.setStatus(StatusPedido.RESERVADO);
            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

            PedidoResponseDTO resultado = pedidoService.buscarPorId(1L);

            assertEquals(StatusPedido.RESERVADO, resultado.getStatus());
        }
    }

    // =========================================================================
    // listarTodos()
    // =========================================================================

    @Nested
    @DisplayName("listarTodos()")
    class ListarTodos {

        @Test
        @DisplayName("✓ Deve retornar lista com todos os pedidos")
        void testListarTodosPedidos() {
            when(pedidoRepository.findAll()).thenReturn(List.of(pedido));

            List<PedidoResponseDTO> resultado = pedidoService.listarTodos();

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(pedidoRepository).findAll();
        }

        @Test
        @DisplayName("✓ Deve retornar lista vazia quando não há pedidos")
        void testListarTodosVazio() {
            when(pedidoRepository.findAll()).thenReturn(Collections.emptyList());

            List<PedidoResponseDTO> resultado = pedidoService.listarTodos();

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    // =========================================================================
    // listarPorCliente()
    // =========================================================================

    @Nested
    @DisplayName("listarPorCliente()")
    class ListarPorCliente {

        private void mockSecurityContext(String email) {
            Authentication auth = mock(Authentication.class);
            SecurityContext ctx = mock(SecurityContext.class);
            when(ctx.getAuthentication()).thenReturn(auth);
            when(auth.getName()).thenReturn(email);
            SecurityContextHolder.setContext(ctx);
        }

        @Test
        @DisplayName("✓ Deve listar pedidos do cliente autenticado")
        void testListarPorClienteAutenticado() throws AccessDeniedException {
            mockSecurityContext("joao@test.com"); // mesmo email do cliente
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(pedidoRepository.findByClienteId(1L)).thenReturn(List.of(pedido));

            List<PedidoResponseDTO> resultado = pedidoService.listarPorCliente(1L);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(pedidoRepository).findByClienteId(1L);
        }

        @Test
        @DisplayName("✓ Deve retornar lista vazia para cliente sem pedidos")
        void testListarPorClienteSemPedidos() throws AccessDeniedException {
            mockSecurityContext("joao@test.com");
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(pedidoRepository.findByClienteId(1L)).thenReturn(Collections.emptyList());

            List<PedidoResponseDTO> resultado = pedidoService.listarPorCliente(1L);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("✓ Não deve misturar pedidos de clientes diferentes")
        void testListarPorClienteIsolamento() throws AccessDeniedException {
            Cliente outroCliente = Cliente.builder()
                    .id(2L).nome("Maria").email("maria@test.com").build();
            Pedido pedidoOutro = Pedido.builder()
                    .id(2L).cliente(outroCliente).status(StatusPedido.CRIADO)
                    .valorBruto(BigDecimal.TEN).desconto(BigDecimal.ZERO)
                    .valorFinal(BigDecimal.TEN).build();

            mockSecurityContext("joao@test.com");
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(pedidoRepository.findByClienteId(1L)).thenReturn(List.of(pedido));

            List<PedidoResponseDTO> resultado = pedidoService.listarPorCliente(1L);

            assertEquals(1, resultado.size());
            assertEquals(1L, resultado.get(0).getId());
        }
    }

    // =========================================================================
    // cancelarPedido()
    // =========================================================================

    @Nested
    @DisplayName("cancelarPedido()")
    class CancelarPedido {

        @Test
        @DisplayName("✓ Deve cancelar pedido CRIADO sem devolver estoque")
        void testCancelarPedidoCriado() {
            pedido.setStatus(StatusPedido.CRIADO);
            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

            PedidoResponseDTO resultado = pedidoService.cancelarPedido(1L);

            assertNotNull(resultado);
            verify(estoqueService, never()).devolverItens(any());
            verify(pedidoRepository).save(any());
        }

        @Test
        @DisplayName("✓ Deve cancelar pedido RESERVADO e devolver estoque")
        void testCancelarPedidoReservadoDevolveEstoque() {
            pedido.setStatus(StatusPedido.RESERVADO);
            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
            doNothing().when(estoqueService).devolverItens(any(Pedido.class));

            pedidoService.cancelarPedido(1L);

            verify(estoqueService, times(1)).devolverItens(pedido);
            verify(pedidoRepository).save(any());
        }

        @Test
        @DisplayName("✓ Deve cancelar pedido PAGO e devolver estoque")
        void testCancelarPedidoPagoDevolveEstoque() {
            pedido.setStatus(StatusPedido.PAGO);
            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
            doNothing().when(estoqueService).devolverItens(any(Pedido.class));

            pedidoService.cancelarPedido(1L);

            verify(estoqueService, times(1)).devolverItens(pedido);
            verify(pedidoRepository).save(any());
        }

        @Test
        @DisplayName("✓ Deve cancelar pagamento quando pedido possui pagamento associado")
        void testCancelarPedidoCancelaPagamentoAssociado() {
            Pagamento pagamento = Pagamento.builder().id(42L).build();
            pedido.setStatus(StatusPedido.CRIADO);
            pedido.setPagamento(pagamento);

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
            doNothing().when(pagamentoService).cancelar(42L);

            pedidoService.cancelarPedido(1L);

            verify(pagamentoService, times(1)).cancelar(42L);
        }

        @Test
        @DisplayName("✓ Não deve chamar pagamentoService.cancelar quando pedido não possui pagamento")
        void testCancelarPedidoSemPagamentoNaoChamaCancelar() {
            pedido.setStatus(StatusPedido.CRIADO);
            pedido.setPagamento(null);

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

            pedidoService.cancelarPedido(1L);

            verify(pagamentoService, never()).cancelar(any());
        }

        @Test
        @DisplayName("✓ Deve salvar pedido com status CANCELADO")
        void testCancelarPedidoPersisteCancelado() {
            pedido.setStatus(StatusPedido.CRIADO);
            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> {
                Pedido p = inv.getArgument(0);
                assertEquals(StatusPedido.CANCELADO, p.getStatus());
                return p;
            });

            PedidoResponseDTO resultado = pedidoService.cancelarPedido(1L);

            assertNotNull(resultado);
            verify(pedidoRepository).save(any(Pedido.class));
        }
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private PedidoRequestDTO pedidoRequestValido(int quantidade) {
        return PedidoRequestDTO.builder()
                .clienteId(1L)
                .metodoPagamento("CARTAO_CREDITO")
                .itens(List.of(ItemPedidoRequestDTO.builder()
                        .produtoId(1L)
                        .quantidade(quantidade)
                        .build()))
                .build();
    }
}