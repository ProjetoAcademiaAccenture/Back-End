package acc.br.projetoFinal.Accenture.repository;

import acc.br.projetoFinal.Accenture.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
	Optional<Pagamento> findByPedidoId(Long pedidoId);
}