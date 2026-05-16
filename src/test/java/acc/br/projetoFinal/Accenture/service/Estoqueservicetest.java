package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.exception.EstoqueInsuficienteException;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.model.ItemPedido;
import acc.br.projetoFinal.Accenture.model.Pedido;
import acc.br.projetoFinal.Accenture.model.Produto;
import acc.br.projetoFinal.Accenture.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private EstoqueService estoqueService;

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------
    private Produto criarProduto(Long id, String nome, int quantidadeEstoque) {
        Produto produto = new Produto();
        produto.setId(id);
        produto.setNome(nome);
        produto.setQuantidadeEstoque(quantidadeEstoque);
        return produto;
    }

    private ItemPedido criarItem(Long produtoId, int quantidade) {
        Produto produtoRef = new Produto();
        produtoRef.setId(produtoId);

        ItemPedido item = new ItemPedido();
        item.setProduto(produtoRef);
        item.setQuantidade(quantidade);
        return item;
    }

    private Pedido criarPedido(ItemPedido... itens) {
        Pedido pedido = new Pedido();
        pedido.setItens(List.of(itens));
        return pedido;
    }

    // =======================================================================
    // reservarItens
    // =======================================================================

    @Test
    void reservarItens_deveReservarComSucesso_quantidadeExata() {
        // estoque == quantidade pedida (limite)
        Produto produto = criarProduto(1L, "Teclado", 5);
        ItemPedido item = criarItem(1L, 5);
        Pedido pedido   = criarPedido(item);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        estoqueService.reservarItens(pedido);

        assertEquals(0, produto.getQuantidadeEstoque());
        verify(produtoRepository).save(produto);
    }

    @Test
    void reservarItens_deveReservarComSucesso_quantidadeMenorQueEstoque() {
        Produto produto = criarProduto(1L, "Mouse", 10);
        ItemPedido item = criarItem(1L, 3);
        Pedido pedido   = criarPedido(item);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        estoqueService.reservarItens(pedido);

        assertEquals(7, produto.getQuantidadeEstoque());
        verify(produtoRepository).save(produto);
    }

    @Test
    void reservarItens_deveReservarMultiplosItensComSucesso() {
        Produto p1 = criarProduto(1L, "Monitor", 10);
        Produto p2 = criarProduto(2L, "Cadeira", 4);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(produtoRepository.findById(2L)).thenReturn(Optional.of(p2));

        Pedido pedido = criarPedido(criarItem(1L, 2), criarItem(2L, 4));

        estoqueService.reservarItens(pedido);

        assertEquals(8, p1.getQuantidadeEstoque());
        assertEquals(0, p2.getQuantidadeEstoque());
        verify(produtoRepository).save(p1);
        verify(produtoRepository).save(p2);
    }

    @Test
    void reservarItens_deveLancarRecursoNaoEncontradoException_quandoProdutoNaoExiste() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        Pedido pedido = criarPedido(criarItem(99L, 1));

        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> estoqueService.reservarItens(pedido)
        );

        assertTrue(ex.getMessage().contains("Produto não encontrado"));
        verify(produtoRepository, never()).save(any());
    }

    @Test
    void reservarItens_deveLancarEstoqueInsuficienteException_quandoEstoqueInsuficiente() {
        Produto produto = criarProduto(1L, "Notebook", 2);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        Pedido pedido = criarPedido(criarItem(1L, 5)); // pede mais do que tem

        EstoqueInsuficienteException ex = assertThrows(
                EstoqueInsuficienteException.class,
                () -> estoqueService.reservarItens(pedido)
        );

        assertTrue(ex.getMessage().contains("Notebook"));
        verify(produtoRepository, never()).save(any());
    }

    @Test
    void reservarItens_deveLancarEstoqueInsuficienteException_quandoEstoqueZero() {
        Produto produto = criarProduto(1L, "Headset", 0);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        Pedido pedido = criarPedido(criarItem(1L, 1));

        assertThrows(
                EstoqueInsuficienteException.class,
                () -> estoqueService.reservarItens(pedido)
        );

        verify(produtoRepository, never()).save(any());
    }

    @Test
    void reservarItens_deveLancarExcecaoNoPrimeiroProduto_eNaoProcessarSegundo() {
        // Garante que o loop para assim que a exceção é lançada
        Produto p1 = criarProduto(1L, "Webcam", 0);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(p1));

        Pedido pedido = criarPedido(criarItem(1L, 1), criarItem(2L, 1));

        assertThrows(
                EstoqueInsuficienteException.class,
                () -> estoqueService.reservarItens(pedido)
        );

        // Segundo produto jamais deve ser consultado
        verify(produtoRepository, never()).findById(2L);
        verify(produtoRepository, never()).save(any());
    }

    // =======================================================================
    // devolverItens
    // =======================================================================

    @Test
    void devolverItens_deveDevolverComSucesso() {
        Produto produto = criarProduto(1L, "Teclado", 0);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        Pedido pedido = criarPedido(criarItem(1L, 5));

        estoqueService.devolverItens(pedido);

        assertEquals(5, produto.getQuantidadeEstoque());
        verify(produtoRepository).save(produto);
    }

    @Test
    void devolverItens_deveDevolverMultiplosItensComSucesso() {
        Produto p1 = criarProduto(1L, "Monitor", 1);
        Produto p2 = criarProduto(2L, "Cadeira", 2);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(produtoRepository.findById(2L)).thenReturn(Optional.of(p2));

        Pedido pedido = criarPedido(criarItem(1L, 3), criarItem(2L, 7));

        estoqueService.devolverItens(pedido);

        assertEquals(4,  p1.getQuantidadeEstoque());
        assertEquals(9,  p2.getQuantidadeEstoque());
        verify(produtoRepository).save(p1);
        verify(produtoRepository).save(p2);
    }

    @Test
    void devolverItens_deveLancarRecursoNaoEncontradoException_quandoProdutoNaoExiste() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        Pedido pedido = criarPedido(criarItem(99L, 3));

        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> estoqueService.devolverItens(pedido)
        );

        assertTrue(ex.getMessage().contains("Produto não encontrado"));
        verify(produtoRepository, never()).save(any());
    }

    @Test
    void devolverItens_deveLancarExcecaoNoPrimeiroProduto_eNaoProcessarSegundo() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        Pedido pedido = criarPedido(criarItem(1L, 1), criarItem(2L, 1));

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> estoqueService.devolverItens(pedido)
        );

        verify(produtoRepository, never()).findById(2L);
        verify(produtoRepository, never()).save(any());
    }
}