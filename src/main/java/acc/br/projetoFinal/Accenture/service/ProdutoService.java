package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ProdutoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.ProdutoResponseDTO;
import acc.br.projetoFinal.Accenture.enums.TipoConta;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.exception.SaldoInsuficienteException;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.model.Produto;
import acc.br.projetoFinal.Accenture.repository.ContaRepository;
import acc.br.projetoFinal.Accenture.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ContaRepository contaRepository;

    @Transactional
    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
        Produto produto = Produto.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .preco(dto.getPreco())
                .quantidadeEstoque(dto.getQuantidade())
                .metodoPgto(dto.getMetodoPgto())
                .build();
        Produto salvo = produtoRepository.save(produto);

        // Empresa "compra" o produto — debita conta jurídica
        BigDecimal custoTotal = dto.getPreco().multiply(BigDecimal.valueOf(dto.getQuantidade()));
        Conta contaEmpresa = contaRepository.findByTipo(TipoConta.JURIDICA)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta empresa não encontrada"));

        if (contaEmpresa.getSaldo().compareTo(custoTotal) < 0)
            throw new SaldoInsuficienteException("Saldo da empresa insuficiente para adquirir o produto");

        contaEmpresa.setSaldo(contaEmpresa.getSaldo().subtract(custoTotal));
        contaRepository.save(contaEmpresa);

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

    @Transactional
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));

        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setMetodoPgto(dto.getMetodoPgto());

        Produto atualizado = produtoRepository.save(produto);
        return ProdutoResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void deletar(Long id) {
        if (!produtoRepository.existsById(id))
            throw new RecursoNaoEncontradoException("Produto não encontrado");
        produtoRepository.deleteById(id);
    }

    @Transactional
    public ProdutoResponseDTO ajustarEstoque(Long id, Integer novaQuantidade) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));

        if (novaQuantidade < 0)
            throw new IllegalArgumentException("Quantidade não pode ser negativa");

        produto.setQuantidadeEstoque(novaQuantidade);
        Produto atualizado = produtoRepository.save(produto);
        return ProdutoResponseDTO.fromEntity(atualizado);
    }
}
