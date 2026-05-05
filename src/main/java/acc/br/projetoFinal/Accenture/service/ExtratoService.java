package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.response.ExtratoResponseDTO;
import acc.br.projetoFinal.Accenture.enums.TipoExtrato;
import acc.br.projetoFinal.Accenture.model.Extrato;
import acc.br.projetoFinal.Accenture.repository.ExtratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExtratoService {

    private final ExtratoRepository extratoRepository;

    public List<ExtratoResponseDTO> listarPorConta(Long contaId) {
        return extratoRepository.findByContaIdOrderByDataHoraDesc(contaId).stream()
                .map(ExtratoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ExtratoResponseDTO> listarPorPeriodo(Long contaId, LocalDateTime inicio, LocalDateTime fim) {
        return extratoRepository.findByContaIdAndDataHoraBetweenOrderByDataHoraDesc(contaId, inicio, fim).stream()
                .map(ExtratoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ExtratoResponseDTO> listarPorTipo(Long contaId, TipoExtrato tipo) {
        return extratoRepository.findByContaIdAndTipoOrderByDataHoraDesc(contaId, tipo).stream()
                .map(ExtratoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ExtratoResponseDTO> listarPorPeriodoETipo(Long contaId, LocalDateTime inicio, LocalDateTime fim, TipoExtrato tipo) {
        return listarPorPeriodo(contaId, inicio, fim).stream()
                .filter(e -> e.getTipo().equals(tipo.name()))
                .collect(Collectors.toList());
    }
}
