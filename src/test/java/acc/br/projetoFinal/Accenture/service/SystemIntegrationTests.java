package acc.br.projetoFinal.Accenture.service;

import static org.junit.jupiter.api.Assertions.*;

import acc.br.projetoFinal.Accenture.dto.request.ClienteRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.ProdutoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.ClienteResponseDTO;
import acc.br.projetoFinal.Accenture.dto.response.ProdutoResponseDTO;
import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.repository.ContaRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Testes de integração do sistema Loja + Banco
 * Cobre: Cliente, Conta, Produto, Depósito e validações
 */
@SpringBootTest
@Transactional
class SystemIntegrationTests {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ContaService contaService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ContaRepository contaRepository;

    private ClienteResponseDTO cliente;
    private Conta contaCliente;
    private ProdutoResponseDTO produto;

    @BeforeEach
    void setup() {
        // Criar cliente
        ClienteRequestDTO clienteRequest = new ClienteRequestDTO();
        clienteRequest.setNome("João Silva");
        clienteRequest.setCpf("10123456789");
        clienteRequest.setEmail("joao@email.com");
        clienteRequest.setTelefone("11999999999");
        clienteRequest.setCep("01310100");
        clienteRequest.setNumero("100");

        cliente = clienteService.criar(clienteRequest);
        contaCliente = contaRepository.findByClienteId(cliente.getId()).orElse(null);

        // Depositar saldo na conta do cliente
        contaService.depositar(contaCliente.getId(), new BigDecimal("5000.00"));

        // Criar produto
        ProdutoRequestDTO produtoRequest = new ProdutoRequestDTO();
        produtoRequest.setNome("Mouse");
        produtoRequest.setDescricao("Mouse Óptico");
        produtoRequest.setPreco(new BigDecimal("100.00"));
        produtoRequest.setQuantidade(5);
        produtoRequest.setMetodoPgto(MetodoPagamento.PIX);

        produto = produtoService.criar(produtoRequest);
    }

    @Test
    void deveCriarClienteComContaAutomatica() {
        assertNotNull(cliente.getId());
        assertNotNull(contaCliente);
        assertEquals("CORRENTE", contaCliente.getTipo().toString());
    }

    @Test
    void deveDepositarNaContaDoCliente() {
        var contaAtualizada = contaRepository.findById(contaCliente.getId()).orElse(null);
        assertEquals(new BigDecimal("5000.00"), contaAtualizada.getSaldo());
    }

    @Test
    void deveCriarProdutoComDebito() {
        assertNotNull(produto.getId());
        assertEquals("Mouse", produto.getNome());
        assertEquals(5, produto.getQuantidadeEstoque());
    }

    @Test
    void deveListarTodosProdutos() {
        var produtos = produtoService.listarTodos();
        assertTrue(produtos.size() >= 1);
    }

    @Test
    void deveBuscarProdutoPorId() {
        var produtoEncontrado = produtoService.buscarPorId(produto.getId());
        assertNotNull(produtoEncontrado);
        assertEquals("Mouse", produtoEncontrado.getNome());
    }

    @Test
    void deveAtualizarProduto() {
        ProdutoRequestDTO update = new ProdutoRequestDTO();
        update.setNome("Mouse Atualizado");
        update.setDescricao("Mouse Gamer");
        update.setPreco(new BigDecimal("150.00"));
        update.setQuantidade(5);
        update.setMetodoPgto(MetodoPagamento.PIX);

        produtoService.atualizar(produto.getId(), update);
        
        var produtoAtualizado = produtoService.buscarPorId(produto.getId());
        assertEquals("Mouse Atualizado", produtoAtualizado.getNome());
    }

    @Test
    void deveAjustarEstoqueDoProduto() {
        produtoService.ajustarEstoque(produto.getId(), 10);
        
        var produtoAtualizado = produtoService.buscarPorId(produto.getId());
        assertEquals(10, produtoAtualizado.getQuantidadeEstoque());
    }

    @Test
    void deveBuscarClientePorCPF() {
        ClienteResponseDTO encontrado = clienteService.buscarPorCpf("10123456789");
        assertNotNull(encontrado);
        assertEquals("João Silva", encontrado.getNome());
    }

    @Test
    void deveValidarSaldoComDepositos() {
        contaService.depositar(contaCliente.getId(), new BigDecimal("1000.00"));
        var contaAtualizada = contaRepository.findById(contaCliente.getId()).orElse(null);
        assertEquals(new BigDecimal("6000.00"), contaAtualizada.getSaldo());
    }

    @Test
    void deveCriarMultiplosCLientes() {
        ClienteRequestDTO clienteRequest2 = new ClienteRequestDTO();
        clienteRequest2.setNome("Maria Santos");
        clienteRequest2.setCpf("20987654321");
        clienteRequest2.setEmail("maria@email.com");
        clienteRequest2.setTelefone("11988888888");
        clienteRequest2.setCep("01310100");
        clienteRequest2.setNumero("50");

        ClienteResponseDTO cliente2 = clienteService.criar(clienteRequest2);
        
        assertNotNull(cliente2);
        assertNotEquals(cliente.getId(), cliente2.getId());
    }

    @Test
    void deveValidarCPFDuplicado() {
        ClienteRequestDTO duplicado = new ClienteRequestDTO();
        duplicado.setNome("Outro Nome");
        duplicado.setCpf("10123456789");
        duplicado.setEmail("outro@email.com");
        duplicado.setTelefone("11988888888");
        duplicado.setCep("01310100");
        duplicado.setNumero("50");

        assertThrows(Exception.class, () -> clienteService.criar(duplicado));
    }

    @Test
    void deveValidarEmailDuplicado() {
        ClienteRequestDTO duplicado = new ClienteRequestDTO();
        duplicado.setNome("Outro Nome");
        duplicado.setCpf("30111111111");
        duplicado.setEmail("joao@email.com");
        duplicado.setTelefone("11988888888");
        duplicado.setCep("01310100");
        duplicado.setNumero("50");

        assertThrows(Exception.class, () -> clienteService.criar(duplicado));
    }

    @Test
    void deveDeletarProduto() {
        Long produtoId = produto.getId();
        produtoService.deletar(produtoId);
        
        assertThrows(Exception.class, () -> produtoService.buscarPorId(produtoId));
    }

    @Test
    void deveBuscarClientePorId() {
        ClienteResponseDTO encontrado = clienteService.buscarPorId(cliente.getId());
        assertNotNull(encontrado);
        assertEquals("João Silva", encontrado.getNome());
    }

    @Test
    void deveListarTodosClientes() {
        var clientes = clienteService.listarTodos();
        assertTrue(clientes.size() >= 1);
    }
}
