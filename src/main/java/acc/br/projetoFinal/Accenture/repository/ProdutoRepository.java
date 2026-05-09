package acc.br.projetoFinal.Accenture.repository;

import acc.br.projetoFinal.Accenture.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Query(value = "SELECT quantidade_estoque FROM produto WHERE id = :id", nativeQuery = true)
    int findEstoqueById(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Produto p SET p.quantidadeEstoque = p.quantidadeEstoque - :qtd WHERE p.id = :id")
    void decrementarEstoque(@Param("id") Long id, @Param("qtd") int qtd);
}