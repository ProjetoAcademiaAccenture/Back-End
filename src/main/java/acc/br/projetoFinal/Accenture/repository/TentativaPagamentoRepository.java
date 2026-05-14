package acc.br.projetoFinal.Accenture.repository;

import acc.br.projetoFinal.Accenture.model.TentativaPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TentativaPagamentoRepository extends JpaRepository<TentativaPagamento, Long> {
	List<TentativaPagamento> findByPagamentoIdOrderByDataTentativaDesc(Long pagamentoId);
}
