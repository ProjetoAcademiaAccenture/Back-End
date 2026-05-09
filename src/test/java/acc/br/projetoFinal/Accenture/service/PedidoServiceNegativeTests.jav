package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ItemPedidoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.PedidoRequestDTO;
import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
import acc.br.projetoFinal.Accenture.enums.TipoCliente;
import acc.br.projetoFinal.Accenture.exception.CancelamentoException;
import acc.br.projetoFinal.Accenture.exception.EstoqueInsuficienteException;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DisplayName("PedidoService - Testes Negativos")
class PedidoServiceNegativeTests {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

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
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao criar pedido com cliente inexistente")
    void deveLancarExcecaoAoCriarComClienteInexistente() {
        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(999L)
                .itens(List.of(ItemPedidoRequestDTO.builder()
                        .produtoId(produto.getId())
                        .quantidade(1)
                        .build()))
                .build();

        assertThatThrownBy(() -> pedidoService.criar(dto))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Cliente não encontrado");
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao criar pedido com produto inexistente")
    void deveLancarExcecaoAoCriarComProdutoInexistente() {
        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(cliente.getId())
                .itens(List.of(ItemPedidoRequestDTO.builder()
                        .produtoId(999L)
                        .quantidade(1)
                        .build()))
                .build();

        assertThatThrownBy(() -> pedidoService.criar(dto))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Produto não encontrado");
    }

    @Test
    @DisplayName("Deve lançar EstoqueInsuficienteException ao criar pedido com quantidade maior que estoque")
    void deveLancarExcecaoAoCriarComEstoqueInsuficiente() {
        assertThatThrownBy(() -> pedidoService.criar(pedidoRequestValido(11)))
                .isInstanceOf(EstoqueInsuficienteException.class)
                .hasMessageContaining("Estoque insuficiente");
    }

    @Test
    @DisplayName("Deve lançar EstoqueInsuficienteException ao criar pedido com estoque zerado")
    void deveLancarExcecaoAoCriarComEstoqueZerado() {
        produto.setQuantidadeEstoque(0);
        produtoRepository.save(produto);

        assertThatThrownBy(() -> pedidoService.criar(pedidoRequestValido(1)))
                .isInstanceOf(EstoqueInsuficienteException.class)
                .hasMessageContaining("Estoque insuficiente");
    }

    // =========================================================================
    // buscarPorId()
    // =========================================================================

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao buscar pedido inexistente")
    void deveLancarExcecaoAoBuscarPedidoInexistente() {
        assertThatThrownBy(() -> pedidoService.buscarPorId(999L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Pedido não encontrado");
    }

    // =========================================================================
    // reservarPedido()
    // =========================================================================

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao reservar pedido inexistente")
    void deveLancarExcecaoAoReservarPedidoInexistente() {
        assertThatThrownBy(() -> pedidoService.reservarPedido(999L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Pedido não encontrado");
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao reservar pedido já RESERVADO")
    void deveLancarExcecaoAoReservarPedidoJaReservado() {
        var criado = pedidoService.criar(pedidoRequestValido(1));
        pedidoService.reservarPedido(criado.getId());

        assertThatThrownBy(() -> pedidoService.reservarPedido(criado.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CRIADO");
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao reservar pedido já PAGO")
    void deveLancarExcecaoAoReservarPedidoJaPago() {
        var criado = pedidoService.criar(pedidoRequestValido(1));
        pedidoService.reservarPedido(criado.getId());
        pedidoService.pagarPedido(criado.getId());

        assertThatThrownBy(() -> pedidoService.reservarPedido(criado.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CRIADO");
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao reservar pedido CANCELADO")
    void deveLancarExcecaoAoReservarPedidoCancelado() {
        var criado = pedidoService.criar(pedidoRequestValido(1));
        pedidoService.cancelarPedido(criado.getId());

        assertThatThrownBy(() -> pedidoService.reservarPedido(criado.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CRIADO");
    }

    // =========================================================================
    // pagarPedido()
    // =========================================================================

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao pagar pedido inexistente")
    void deveLancarExcecaoAoPagarPedidoInexistente() {
        assertThatThrownBy(() -> pedidoService.pagarPedido(999L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Pedido não encontrado");
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao pagar pedido ainda CRIADO")
    void deveLancarExcecaoAoPagarPedidoCriado() {
        var criado = pedidoService.criar(pedidoRequestValido(1));

        assertThatThrownBy(() -> pedidoService.pagarPedido(criado.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("RESERVADO");
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao pagar pedido CANCELADO")
    void deveLancarExcecaoAoPagarPedidoCancelado() {
        var criado = pedidoService.criar(pedidoRequestValido(1));
        pedidoService.cancelarPedido(criado.getId());

        assertThatThrownBy(() -> pedidoService.pagarPedido(criado.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("RESERVADO");
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao tentar pagar pedido já PAGO")
    void deveLancarExcecaoAoPagarPedidoJaPago() {
        var criado = pedidoService.criar(pedidoRequestValido(1));
        pedidoService.reservarPedido(criado.getId());
        pedidoService.pagarPedido(criado.getId());

        assertThatThrownBy(() -> pedidoService.pagarPedido(criado.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("RESERVADO");
    }

    // =========================================================================
    // cancelarPedido()
    // =========================================================================

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao cancelar pedido inexistente")
    void deveLancarExcecaoAoCancelarPedidoInexistente() {
        assertThatThrownBy(() -> pedidoService.cancelarPedido(999L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Pedido não encontrado");
    }

    @Test
    @DisplayName("Deve lançar CancelamentoException ao cancelar pedido já CANCELADO")
    void deveLancarExcecaoAoCancelarPedidoJaCancelado() {
        var criado = pedidoService.criar(pedidoRequestValido(1));
        pedidoService.cancelarPedido(criado.getId());

        assertThatThrownBy(() -> pedidoService.cancelarPedido(criado.getId()))
                .isInstanceOf(CancelamentoException.class)
                .hasMessageContaining("já está cancelado");
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