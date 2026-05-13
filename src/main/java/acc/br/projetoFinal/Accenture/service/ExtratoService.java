package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.response.ExtratoResponseDTO;
import acc.br.projetoFinal.Accenture.enums.TipoExtrato;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.model.Extrato;
import acc.br.projetoFinal.Accenture.model.Pagamento;
import acc.br.projetoFinal.Accenture.model.Pedido;
import acc.br.projetoFinal.Accenture.repository.ExtratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExtratoService {

    private final ExtratoRepository extratoRepository;

    @Transactional
    public Extrato registrar(
        Conta conta,
        TipoExtrato tipo,
        BigDecimal valor,
        BigDecimal saldoAntes,
        BigDecimal saldoDepois,
        String descricao,
        Pedido pedido,
        Pagamento pagamento
    ) {
        return extratoRepository.save(Extrato.builder()
            .conta(conta)
            .tipo(tipo)
            .valor(valor)
            .saldoAntes(saldoAntes)
            .saldoDepois(saldoDepois)
            .descricao(descricao)
            .pedido(pedido)
            .pagamento(pagamento)
            .dataHora(LocalDateTime.now())
            .build());
    }

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

    public List<ExtratoResponseDTO> listarPorPeriodoETipo(
        Long contaId,
        LocalDateTime inicio,
        LocalDateTime fim,
        TipoExtrato tipo
    ) {
        return extratoRepository.findByContaIdAndDataHoraBetweenOrderByDataHoraDesc(contaId, inicio, fim).stream()
            .filter(extrato -> extrato.getTipo() == tipo)
            .map(ExtratoResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }
}