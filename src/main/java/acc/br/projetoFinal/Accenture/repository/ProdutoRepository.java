package acc.br.projetoFinal.Accenture.repository;

import acc.br.projetoFinal.Accenture.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
