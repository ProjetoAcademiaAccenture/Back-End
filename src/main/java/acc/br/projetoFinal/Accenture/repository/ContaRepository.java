package acc.br.projetoFinal.Accenture.repository;

import acc.br.projetoFinal.Accenture.enums.TipoConta;
import acc.br.projetoFinal.Accenture.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    Optional<Conta> findByClienteId(Long clienteId);
    Optional<Conta> findByTipo(TipoConta tipo);
    Optional<Conta> findByNumeroConta(String numeroConta);
}
