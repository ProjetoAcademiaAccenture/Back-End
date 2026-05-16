package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ProdutoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.ProdutoResponseDTO;
import acc.br.projetoFinal.Accenture.enums.Categoria;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.model.Produto;
import acc.br.projetoFinal.Accenture.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;
    private ProdutoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        produto = Produto.builder()
                .id(1L)
                .nome("Notebook")
                .descricao("Notebook gamer")
                .preco(new BigDecimal("4500.00"))
                .urlImagem("http://img.com/notebook.png")
                .quantidadeEstoque(10)
                .categoria(Categoria.ELETRONICOS)
                .build();

        requestDTO = new ProdutoRequestDTO();
        requestDTO.setNome("Notebook");
        requestDTO.setDescricao("Notebook gamer");
        requestDTO.setPreco(new BigDecimal("4500.00"));
        requestDTO.setUrlImagem("http://img.com/notebook.png");
        requestDTO.setQuantidadeEstoque(10);
        requestDTO.setCategoria(Categoria.ELETRONICOS);
    }

    // =======================================================================
    // criar
    // =======================================================================

    @Test
    void criar_deveSalvarProdutoERetornarDTO() {
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        ProdutoResponseDTO response = produtoService.criar(requestDTO);

        assertNotNull(response);
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    void criar_deveMontarProdutoComCamposDoDTO() {
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        produtoService.criar(requestDTO);

        ArgumentCaptor<Produto> captor = ArgumentCaptor.forClass(Produto.class);
        verify(produtoRepository).save(captor.capture());

        Produto capturado = captor.getValue();
        assertAll(
            () -> assertEquals("Notebook",                      capturado.getNome()),
            () -> assertEquals("Notebook gamer",               capturado.getDescricao()),
            () -> assertEquals(new BigDecimal("4500.00"),       capturado.getPreco()),
            () -> assertEquals("http://img.com/notebook.png",  capturado.getUrlImagem()),
            () -> assertEquals(10,                             capturado.getQuantidadeEstoque()),
            () -> assertEquals(Categoria.ELETRONICOS,          capturado.getCategoria())
        );
    }

    // =======================================================================
    // buscarPorId
    // =======================================================================

    @Test
    void buscarPorId_deveRetornarDTO_quandoEncontrado() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        ProdutoResponseDTO response = produtoService.buscarPorId(1L);

        assertNotNull(response);
        verify(produtoRepository).findById(1L);
    }

    @Test
    void buscarPorId_deveLancarExcecao_quandoNaoEncontrado() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> produtoService.buscarPorId(99L));
    }

    // =======================================================================
    // listarTodos
    // =======================================================================

    @Test
    void listarTodos_deveRetornarListaComTodosProdutos() {
        Produto produto2 = Produto.builder()
                .id(2L).nome("Mouse").descricao("Mouse gamer")
                .preco(new BigDecimal("150.00")).urlImagem("url")
                .quantidadeEstoque(50).categoria(Categoria.ELETRONICOS)
                .build();

        when(produtoRepository.findAll()).thenReturn(List.of(produto, produto2));

        List<ProdutoResponseDTO> lista = produtoService.listarTodos();

        assertEquals(2, lista.size());
        verify(produtoRepository).findAll();
    }

    @Test
    void listarTodos_deveRetornarListaVazia_quandoNenhumProduto() {
        when(produtoRepository.findAll()).thenReturn(List.of());

        List<ProdutoResponseDTO> lista = produtoService.listarTodos();

        assertTrue(lista.isEmpty());
    }

    // =======================================================================
    // listarPorCategoria
    // =======================================================================

    @Test
    void listarPorCategoria_deveRetornarProdutosDaCategoria() {
        when(produtoRepository.findByCategoria(Categoria.ELETRONICOS))
                .thenReturn(List.of(produto));

        List<ProdutoResponseDTO> lista =
                produtoService.listarPorCategoria("ELETRONICOS");

        assertEquals(1, lista.size());
        verify(produtoRepository).findByCategoria(Categoria.ELETRONICOS);
    }

    @Test
    void listarPorCategoria_deveConverterCategoriaParaMaiusculas() {
        when(produtoRepository.findByCategoria(Categoria.ELETRONICOS))
                .thenReturn(List.of(produto));

        // entrada em minúsculas — o service faz toUpperCase()
        List<ProdutoResponseDTO> lista =
                produtoService.listarPorCategoria("eletronicos");

        assertEquals(1, lista.size());
        verify(produtoRepository).findByCategoria(Categoria.ELETRONICOS);
    }

    @Test
    void listarPorCategoria_deveLancarExcecao_quandoCategoriaInvalida() {
        assertThrows(IllegalArgumentException.class,
                () -> produtoService.listarPorCategoria("CATEGORIA_INEXISTENTE"));
    }

    @Test
    void listarPorCategoria_deveRetornarListaVazia_quandoNenhumProdutoNaCategoria() {
        when(produtoRepository.findByCategoria(Categoria.ELETRONICOS))
                .thenReturn(List.of());

        List<ProdutoResponseDTO> lista =
                produtoService.listarPorCategoria("ELETRONICOS");

        assertTrue(lista.isEmpty());
    }

    // =======================================================================
    // atualizar
    // =======================================================================

    @Test
    void atualizar_deveAtualizarCamposESalvar() {
        ProdutoRequestDTO dtoAtualizado = new ProdutoRequestDTO();
        dtoAtualizado.setNome("Notebook Pro");
        dtoAtualizado.setDescricao("Novo desc");
        dtoAtualizado.setPreco(new BigDecimal("5000.00"));
        dtoAtualizado.setUrlImagem("http://nova-url.com");
        dtoAtualizado.setQuantidadeEstoque(5);
        dtoAtualizado.setCategoria(Categoria.ELETRONICOS);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        ProdutoResponseDTO response = produtoService.atualizar(1L, dtoAtualizado);

        assertNotNull(response);

        ArgumentCaptor<Produto> captor = ArgumentCaptor.forClass(Produto.class);
        verify(produtoRepository).save(captor.capture());

        Produto atualizado = captor.getValue();
        assertAll(
            () -> assertEquals("Notebook Pro",          atualizado.getNome()),
            () -> assertEquals("Novo desc",             atualizado.getDescricao()),
            () -> assertEquals(new BigDecimal("5000.00"), atualizado.getPreco()),
            () -> assertEquals("http://nova-url.com",   atualizado.getUrlImagem()),
            () -> assertEquals(5,                       atualizado.getQuantidadeEstoque()),
            () -> assertEquals(Categoria.ELETRONICOS,   atualizado.getCategoria())
        );
    }

    @Test
    void atualizar_deveLancarExcecao_quandoProdutoNaoEncontrado() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> produtoService.atualizar(99L, requestDTO));
    }

    // =======================================================================
    // ajustarEstoque
    // =======================================================================

    @Test
    void ajustarEstoque_deveAtualizarQuantidade_quandoValorValido() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        ProdutoResponseDTO response = produtoService.ajustarEstoque(1L, 20);

        assertNotNull(response);

        ArgumentCaptor<Produto> captor = ArgumentCaptor.forClass(Produto.class);
        verify(produtoRepository).save(captor.capture());
        assertEquals(20, captor.getValue().getQuantidadeEstoque());
    }

    @Test
    void ajustarEstoque_deveAceitarQuantidadeZero() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        assertDoesNotThrow(() -> produtoService.ajustarEstoque(1L, 0));
    }

    @Test
    void ajustarEstoque_deveLancarExcecao_quandoQuantidadeNegativa() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> produtoService.ajustarEstoque(1L, -1));

        assertEquals("Quantidade não pode ser negativa", ex.getMessage());
    }

    @Test
    void ajustarEstoque_deveLancarExcecao_quandoProdutoNaoEncontrado() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> produtoService.ajustarEstoque(99L, 10));
    }

    // =======================================================================
    // deletar
    // =======================================================================

    @Test
    void deletar_deveDeletarProduto_quandoExistente() {
        when(produtoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(produtoRepository).deleteById(1L);

        assertDoesNotThrow(() -> produtoService.deletar(1L));

        verify(produtoRepository).existsById(1L);
        verify(produtoRepository).deleteById(1L);
    }

    @Test
    void deletar_deveLancarExcecao_quandoProdutoNaoEncontrado() {
        when(produtoRepository.existsById(99L)).thenReturn(false);

        assertThrows(RecursoNaoEncontradoException.class,
                () -> produtoService.deletar(99L));

        verify(produtoRepository, never()).deleteById(any());
    }
}