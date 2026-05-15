package acc.br.projetoFinal.Accenture.config;

import acc.br.projetoFinal.Accenture.enums.Categoria;
import acc.br.projetoFinal.Accenture.model.Produto;
import acc.br.projetoFinal.Accenture.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class ProdutoDataInitializer {

	private final ProdutoRepository produtoRepository;

	public ProdutoDataInitializer (ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}

	@Bean
	public CommandLineRunner initProdutos() {
		return args -> {

			if (produtoRepository.count() > 0) {
				return;
			}

			List<Produto> produtos = List.of(

					Produto.builder()
							.nome("Notebook Dell Inspiron")
							.descricao("Notebook Dell Intel i5 16GB SSD 512GB")
							.preco(new BigDecimal("3499.90"))
							.urlImagem("https://m.media-amazon.com/images/I/51qDF9hp7gL._AC_SX679_.jpg")
							.quantidadeEstoque(10)
							.categoria(Categoria.ELETRONICOS)
							.build(),

					Produto.builder()
							.nome("Mouse Gamer Logitech")
							.descricao("Mouse gamer RGB 12000 DPI")
							.preco(new BigDecimal ("249.90"))
							.urlImagem("https://m.media-amazon.com/images/I/61mpMH5TzkL._AC_SX679_.jpg")
							.quantidadeEstoque(25)
							.categoria(Categoria.PERIFERICOS)
							.build(),

					Produto.builder()
							.nome("Smartphone Samsung Galaxy")
							.descricao("Smartphone Samsung 256GB")
							.preco(new BigDecimal("2899.90"))
							.urlImagem("https://images.unsplash.com/photo-1511707171634-5f897ff02aa9")
							.quantidadeEstoque(15)
							.categoria(Categoria.ELETRONICOS)
							.build(),

					Produto.builder()
							.nome("Headset Gamer Sem Fio Redragon Zeus Pro")
							.descricao("Prepare-se para uma experiência de jogo transcendente com o Headset Gamer Redragon Zeus Pro!")
							.preco(new BigDecimal("499.90"))
							.urlImagem("https://images6.kabum.com.br/produtos/fotos/508106/headset-gamer-redragon-zeus-pro-preto-h510-pro_1700743328_gg.jpg")
							.quantidadeEstoque(20)
							.categoria(Categoria.ACESSORIOS)
							.build()
			);

			produtoRepository.saveAll(produtos);

			System.out.println("✓ Produtos iniciais cadastrados!");
		};
	}
}