package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ItemPedidoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.PedidoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.PedidoResponseDTO;
import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import acc.br.projetoFinal.Accenture.enums.TipoCliente;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.Produto;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.repository.PedidoRepository;
import acc.br.projetoFinal.Accenture.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@DisplayName("PedidoService - Testes Positivos")
class PedidoServicePositive {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @MockBean
    private ContaService contaService;

    private Cliente cliente;
    private Produto produto;

    @BeforeEach
    void setup() {
        cliente = clienteRepository.save(Cliente.builder()
                .nome("Cliente Teste")
                .email("cliente@teste.com")
                .cpf("12345678901")
                .senha("senha123")
                .tipoCliente(TipoCliente.ROLE_USER)
                .build());

        produto = produtoRepository.save(Produto.builder()
                .nome("Produto Teste")
                .descricao("Descrição teste")
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(10)
                .metodoPgto(MetodoPagamento.PIX)
                .build());
    }

    // =========================================================================
    // criar()
    // =========================================================================

    @Test
    @DisplayName("Deve criar pedido com status CRIADO")
    void deveCriarPedidoComStatusCriado() {
        PedidoResponseDTO resposta = pedidoService.criar(pedidoRequestValido(1));

        assertThat(resposta.getStatus()).isEqualTo(StatusPedido.CRIADO);
    }

    @Test
    @DisplayName("Deve criar pedido com ID gerado pelo banco")
    void deveCriarPedidoComIdGerado() {
        PedidoResponseDTO resposta = pedidoService.criar(pedidoRequestValido(1));

        assertThat(resposta.getId()).isNotNull().isPositive();
    }

    @Test
    @DisplayName("Deve criar pedido com data de criação preenchida")
    void deveCriarPedidoComDataCriacao() {
        PedidoResponseDTO resposta = pedidoService.criar(pedidoRequestValido(1));

        assertThat(resposta.getDataCriacao()).isNotNull();
    }

    @Test
    @DisplayName("Deve calcular valor total corretamente para um item")
    void deveCriarPedidoComValorTotalCorretoParaUmItem() {
        PedidoResponseDTO resposta = pedidoService.criar(pedidoRequestValido(3));

        assertThat(resposta.getValorTotal()).isEqualByComparingTo(new BigDecimal("300.00"));
    }

    @Test
    @DisplayName("Deve calcular valor total corretamente para múltiplos itens")
    void deveCriarPedidoComValorTotalCorretoParaMultiplosItens() {
        Produto produto2 = produtoRepository.save(Produto.builder()
                .nome("Produto 2")
                .descricao("Desc 2")
                .preco(new BigDecimal("50.00"))
                .quantidadeEstoque(5)
                .metodoPgto(MetodoPagamento.PIX)
                .build());

        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(cliente.getId())
                .itens(List.of(
                        ItemPedidoRequestDTO.builder().produtoId(produto.getId()).quantidade(2).build(),
                        ItemPedidoRequestDTO.builder().produtoId(produto2.getId()).quantidade(4).build()
                ))
                .build();

        PedidoResponseDTO resposta = pedidoService.criar(dto);

        // (100 x 2) + (50 x 4) = 400.00
        assertThat(resposta.getValorTotal()).isEqualByComparingTo(new BigDecimal("400.00"));
    }

    @Test
    @DisplayName("Deve persistir pedido no banco após criação")
    void devePersistirPedidoNoBanco() {
        PedidoResponseDTO resposta = pedidoService.criar(pedidoRequestValido(1));

        assertThat(pedidoRepository.findById(resposta.getId())).isPresent();
    }

    @Test
    @DisplayName("Deve criar pedido com multa de cancelamento zerada inicialmente")
    void deveCriarPedidoComMultaZerada() {
        PedidoResponseDTO resposta = pedidoService.criar(pedidoRequestValido(1));

        assertThat(resposta.getMultaCancelamento()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve criar pedido com quantidade exata disponível em estoque")
    void deveCriarPedidoComQuantidadeExataDoEstoque() {
        PedidoResponseDTO resposta = pedidoService.criar(pedidoRequestValido(10));

        assertThat(resposta.getId()).isNotNull();
        assertThat(resposta.getValorTotal()).isEqualByComparingTo(new BigDecimal("1000.00"));
    }

    // =========================================================================
    // buscarPorId()
    // =========================================================================

    @Test
    @DisplayName("Deve buscar pedido por ID e retornar os dados corretos")
    void deveBuscarPedidoPorId() {
        PedidoResponseDTO criado = pedidoService.criar(pedidoRequestValido(2));

        PedidoResponseDTO encontrado = pedidoService.buscarPorId(criado.getId());

        assertThat(encontrado.getId()).isEqualTo(criado.getId());
        assertThat(encontrado.getStatus()).isEqualTo(StatusPedido.CRIADO);
        assertThat(encontrado.getValorTotal()).isEqualByComparingTo(criado.getValorTotal());
    }

    // =========================================================================
    // listarTodos()
    // =========================================================================

    @Test
    @DisplayName("Deve listar todos os pedidos criados")
    void deveListarTodosPedidos() {
        pedidoService.criar(pedidoRequestValido(1));
        pedidoService.criar(pedidoRequestValido(2));

        List<PedidoResponseDTO> lista = pedidoService.listarTodos();

        assertThat(lista).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há pedidos")
    void deveRetornarListaVaziaQuandoNaoHaPedidos() {
        List<PedidoResponseDTO> lista = pedidoService.listarTodos();

        assertThat(lista).isEmpty();
    }

    // =========================================================================
    // listarPorCliente()
    // =========================================================================

    @Test
    @DisplayName("Deve listar apenas pedidos do cliente informado")
    void deveListarPedidosPorCliente() {
        pedidoService.criar(pedidoRequestValido(1));
        pedidoService.criar(pedidoRequestValido(1));

        List<PedidoResponseDTO> lista = pedidoService.listarPorCliente(cliente.getId());

        assertThat(lista).hasSize(2);
        assertThat(lista).allMatch(p -> p.getClienteId().equals(cliente.getId()));
    }

    @Test
    @DisplayName("Deve retornar lista vazia para cliente sem pedidos")
    void deveRetornarListaVaziaParaClienteSemPedidos() {
        List<PedidoResponseDTO> lista = pedidoService.listarPorCliente(cliente.getId());

        assertThat(lista).isEmpty();
    }

    @Test
    @DisplayName("Deve não misturar pedidos de clientes diferentes")
    void deveNaoMisturarPedidosDeClientesDiferentes() {
        Cliente outroCliente = clienteRepository.save(Cliente.builder()
                .nome("Outro Cliente")
                .email("outro@teste.com")
                .cpf("98765432100")
                .senha("senha456")
                .tipoCliente(TipoCliente.ROLE_USER)
                .build());

        pedidoService.criar(pedidoRequestValido(1));
        pedidoService.criar(PedidoRequestDTO.builder()
                .clienteId(outroCliente.getId())
                .itens(List.of(ItemPedidoRequestDTO.builder()
                        .produtoId(produto.getId())
                        .quantidade(1)
                        .build()))
                .build());

        List<PedidoResponseDTO> lista = pedidoService.listarPorCliente(cliente.getId());

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).getClienteId()).isEqualTo(cliente.getId());
    }

    // =========================================================================
    // reservarPedido()
    // =========================================================================

    @Test
    @DisplayName("Deve reservar pedido alterando status para RESERVADO")
    void deveReservarPedidoAlterandoStatus() {
        PedidoResponseDTO criado = pedidoService.criar(pedidoRequestValido(1));

        PedidoResponseDTO reservado = pedidoService.reservarPedido(criado.getId());

        assertThat(reservado.getStatus()).isEqualTo(StatusPedido.RESERVADO);
    }

    @Test
    @DisplayName("Deve decrementar estoque ao reservar pedido")
    void deveDecrementarEstoqueAoReservar() {
        PedidoResponseDTO criado = pedidoService.criar(pedidoRequestValido(4));

        pedidoService.reservarPedido(criado.getId());

        int estoqueAtual = produtoRepository.findById(produto.getId()).get().getQuantidadeEstoque();
        assertThat(estoqueAtual).isEqualTo(6); // 10 - 4
    }

    @Test
    @DisplayName("Deve zerar estoque ao reservar com quantidade total disponível")
    void deveZerarEstoqueAoReservarQuantidadeTotal() {
        PedidoResponseDTO criado = pedidoService.criar(pedidoRequestValido(10));

        pedidoService.reservarPedido(criado.getId());

        int estoqueAtual = produtoRepository.findById(produto.getId()).get().getQuantidadeEstoque();
        assertThat(estoqueAtual).isZero();
    }

    // =========================================================================
    // pagarPedido()
    // =========================================================================

    @Test
    @DisplayName("Deve pagar pedido alterando status para PAGO")
    void devePagarPedidoAlterandoStatus() {
        PedidoResponseDTO criado = pedidoService.criar(pedidoRequestValido(1));
        pedidoService.reservarPedido(criado.getId());

        PedidoResponseDTO pago = pedidoService.pagarPedido(criado.getId());

        assertThat(pago.getStatus()).isEqualTo(StatusPedido.PAGO);
    }

    @Test
    @DisplayName("Deve chamar ContaService.transferir com os valores corretos ao pagar")
    void deveChamarTransferirAoPagar() {
        PedidoResponseDTO criado = pedidoService.criar(pedidoRequestValido(2));
        pedidoService.reservarPedido(criado.getId());

        pedidoService.pagarPedido(criado.getId());

        verify(contaService, times(1))
                .transferir(eq(cliente.getId()), eq(new BigDecimal("200.00")), any());
    }

    @Test
    @DisplayName("Deve manter valor total inalterado após pagamento")
    void deveManterValorTotalAposPagamento() {
        PedidoResponseDTO criado = pedidoService.criar(pedidoRequestValido(3));
        pedidoService.reservarPedido(criado.getId());

        PedidoResponseDTO pago = pedidoService.pagarPedido(criado.getId());

        assertThat(pago.getValorTotal()).isEqualByComparingTo(new BigDecimal("300.00"));
    }

    // =========================================================================
    // cancelarPedido()
    // =========================================================================

    @Test
    @DisplayName("Deve cancelar pedido CRIADO alterando status para CANCELADO")
    void deveCancelarPedidoCriado() {
        PedidoResponseDTO criado = pedidoService.criar(pedidoRequestValido(1));

        PedidoResponseDTO cancelado = pedidoService.cancelarPedido(criado.getId());

        assertThat(cancelado.getStatus()).isEqualTo(StatusPedido.CANCELADO);
    }

    @Test
    @DisplayName("Deve cancelar pedido RESERVADO e restaurar estoque")
    void deveCancelarPedidoReservadoRestaurandoEstoque() {
        PedidoResponseDTO criado = pedidoService.criar(pedidoRequestValido(5));
        pedidoService.reservarPedido(criado.getId());

        pedidoService.cancelarPedido(criado.getId());

        int estoqueAtual = produtoRepository.findById(produto.getId()).get().getQuantidadeEstoque();
        assertThat(estoqueAtual).isEqualTo(10);
    }

    @Test
    @DisplayName("Deve cancelar pedido PAGO, restaurar estoque e aplicar multa de 10%")
    void deveCancelarPedidoPagoComMultaDe10Porcento() {
        PedidoResponseDTO criado = pedidoService.criar(pedidoRequestValido(2)); // 200.00
        pedidoService.reservarPedido(criado.getId());
        pedidoService.pagarPedido(criado.getId());

        PedidoResponseDTO cancelado = pedidoService.cancelarPedido(criado.getId());

        BigDecimal multaEsperada = new BigDecimal("200.00")
                .multiply(PedidoService.PERCENTUAL_MULTA)
                .setScale(2, RoundingMode.HALF_UP); // 20.00

        assertThat(cancelado.getStatus()).isEqualTo(StatusPedido.CANCELADO);
        assertThat(cancelado.getMultaCancelamento()).isEqualByComparingTo(multaEsperada);
    }

    @Test
    @DisplayName("Deve chamar ContaService.estornarComMulta com valores corretos ao cancelar pedido PAGO")
    void deveChamarEstornarComMultaAoCancelarPedidoPago() {
        PedidoResponseDTO criado = pedidoService.criar(pedidoRequestValido(2)); // 200.00
        pedidoService.reservarPedido(criado.getId());
        pedidoService.pagarPedido(criado.getId());

        pedidoService.cancelarPedido(criado.getId());

        verify(contaService, times(1))
                .estornarComMulta(eq(cliente.getId()), eq(new BigDecimal("180.00")), eq(new BigDecimal("20.00")), any());
    }

    @Test
    @DisplayName("Deve restaurar estoque ao cancelar pedido PAGO")
    void deveRestaurarEstoqueAoCancelarPedidoPago() {
        PedidoResponseDTO criado = pedidoService.criar(pedidoRequestValido(3));
        pedidoService.reservarPedido(criado.getId());
        pedidoService.pagarPedido(criado.getId());

        pedidoService.cancelarPedido(criado.getId());

        int estoqueAtual = produtoRepository.findById(produto.getId()).get().getQuantidadeEstoque();
        assertThat(estoqueAtual).isEqualTo(10);
    }

    @Test
    @DisplayName("Deve não chamar estornarComMulta ao cancelar pedido CRIADO")
    void deveNaoChamarEstornoAoCancelarPedidoCriado() {
        PedidoResponseDTO criado = pedidoService.criar(pedidoRequestValido(1));

        pedidoService.cancelarPedido(criado.getId());

        verify(contaService, never()).estornarComMulta(anyLong(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve não chamar estornarComMulta ao cancelar pedido RESERVADO")
    void deveNaoChamarEstornoAoCancelarPedidoReservado() {
        PedidoResponseDTO criado = pedidoService.criar(pedidoRequestValido(1));
        pedidoService.reservarPedido(criado.getId());

        pedidoService.cancelarPedido(criado.getId());

        verify(contaService, never()).estornarComMulta(anyLong(), any(), any(), any());
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private PedidoRequestDTO pedidoRequestValido(int quantidade) {
        return PedidoRequestDTO.builder()
                .clienteId(cliente.getId())
                .itens(List.of(ItemPedidoRequestDTO.builder()
                        .produtoId(produto.getId())
                        .quantidade(quantidade)
                        .build()))
                .build();
    }
}




