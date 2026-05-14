package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ProdutoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.ProdutoResponseDTO;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.model.Produto;
import acc.br.projetoFinal.Accenture.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    @Transactional
    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {

        Produto produto = Produto.builder()
            .nome(dto.getNome())
            .descricao(dto.getDescricao())
            .preco(dto.getPreco())
            .urlImagem(dto.getUrlImagem())
            .quantidadeEstoque(dto.getQuantidadeEstoque())
            .categoria(dto.getCategoria())
            .build();

        Produto salvo = produtoRepository.save(produto);

        return ProdutoResponseDTO.fromEntity(salvo);
    }

    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));
        return ProdutoResponseDTO.fromEntity(produto);
    }

    public List<ProdutoResponseDTO> listarTodos() {
        return produtoRepository.findAll().stream()
            .map(ProdutoResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public List<ProdutoResponseDTO> listarPorCategoria(String categoria) {

        return produtoRepository.findByCategoria(
                Enum.valueOf(
                    acc.br.projetoFinal.Accenture.enums.Categoria.class,
                    categoria.toUpperCase()
                )
            )
            .stream()
            .map(ProdutoResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {

        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() ->
                new RecursoNaoEncontradoException("Produto não encontrado")
            );

        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setUrlImagem(dto.getUrlImagem());
        produto.setQuantidadeEstoque(dto.getQuantidadeEstoque());
        produto.setCategoria(dto.getCategoria());

        Produto atualizado = produtoRepository.save(produto);

        return ProdutoResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public ProdutoResponseDTO ajustarEstoque(
        Long id,
        Integer novaQuantidade
    ) {

        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() ->
                new RecursoNaoEncontradoException("Produto não encontrado")
            );

        if (novaQuantidade < 0) {
            throw new IllegalArgumentException(
                "Quantidade não pode ser negativa"
            );
        }

        produto.setQuantidadeEstoque(novaQuantidade);

        Produto atualizado = produtoRepository.save(produto);

        return ProdutoResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void deletar(Long id) {

        if (!produtoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException(
                "Produto não encontrado"
            );
        }

        produtoRepository.deleteById(id);
    }
}