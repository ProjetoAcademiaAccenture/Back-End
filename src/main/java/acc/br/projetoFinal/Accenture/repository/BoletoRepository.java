package acc.br.projetoFinal.Accenture.repository;

import acc.br.projetoFinal.Accenture.model.Boleto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoletoRepository extends JpaRepository<Boleto, Long> {
    Optional<Boleto> findByCodigoBarras(String codigoBarras);
    Optional<Boleto> findByPagamentoId(Long pagamentoId);
    Optional<Boleto> findByPagamentoPedidoId(Long pedidoId);
}
