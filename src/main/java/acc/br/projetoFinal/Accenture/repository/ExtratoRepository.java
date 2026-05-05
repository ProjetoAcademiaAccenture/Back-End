package acc.br.projetoFinal.Accenture.repository;

import acc.br.projetoFinal.Accenture.enums.TipoExtrato;
import acc.br.projetoFinal.Accenture.model.Extrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExtratoRepository extends JpaRepository<Extrato, Long> {

    // Lista todas as movimentações de uma conta em ordem cronológica (mais recente primeiro)
    List<Extrato> findByContaIdOrderByDataHoraDesc(Long contaId);

    // Filtra por período
    List<Extrato> findByContaIdAndDataHoraBetweenOrderByDataHoraDesc(
            Long contaId, LocalDateTime inicio, LocalDateTime fim);

    // Filtra por tipo (só débitos, só créditos, etc)
    List<Extrato> findByContaIdAndTipoOrderByDataHoraDesc(
            Long contaId, TipoExtrato tipo);
}
