package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.exception.EstoqueInsuficienteException;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.model.ItemPedido;
import acc.br.projetoFinal.Accenture.model.Pedido;
import acc.br.projetoFinal.Accenture.model.Produto;
import acc.br.projetoFinal.Accenture.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EstoqueService {

	private final ProdutoRepository produtoRepository;

	@Transactional
	public void reservarItens(Pedido pedido) {
		for (ItemPedido item : pedido.getItens()) {
			Produto produto = produtoRepository.findById(item.getProduto().getId())
					.orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));

			if (produto.getQuantidadeEstoque() < item.getQuantidade()) {
				throw new EstoqueInsuficienteException("Estoque insuficiente para o produto: " + produto.getNome());
			}

			produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - item.getQuantidade());
			produtoRepository.save(produto);
		}
	}

	@Transactional
	public void devolverItens(Pedido pedido) {
		for (ItemPedido item : pedido.getItens()) {
			Produto produto = produtoRepository.findById(item.getProduto().getId())
					.orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));

			produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + item.getQuantidade());
			produtoRepository.save(produto);
		}
	}
}