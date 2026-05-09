package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.response.ContaResponseDTO;
import acc.br.projetoFinal.Accenture.dto.response.ExtratoResponseDTO;
import acc.br.projetoFinal.Accenture.enums.TipoExtrato;
import acc.br.projetoFinal.Accenture.repository.ContaRepository;
import acc.br.projetoFinal.Accenture.service.ContaService;
import acc.br.projetoFinal.Accenture.service.ExtratoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/contas")
@RequiredArgsConstructor
public class ContaController {

    private final ContaRepository contaRepository;
    private final ContaService contaService;
    private final ExtratoService extratoService;

    @GetMapping("/{id}")
    public ResponseEntity<ContaResponseDTO> buscarPorId(@PathVariable Long id) {
        var conta = contaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        return ResponseEntity.ok(ContaResponseDTO.fromEntity(conta));
    }

    @PatchMapping("/{id}/depositar")
    public ResponseEntity<ContaResponseDTO> depositar(@PathVariable Long id, @RequestParam BigDecimal valor) {
        contaService.depositar(id, valor);
        var conta = contaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        return ResponseEntity.ok(ContaResponseDTO.fromEntity(conta));
    }

    @GetMapping("/{id}/extrato")
    public ResponseEntity<List<ExtratoResponseDTO>> listarExtrato(
            @PathVariable Long id,
            @RequestParam(required = false) LocalDateTime inicio,
            @RequestParam(required = false) LocalDateTime fim,
            @RequestParam(required = false) TipoExtrato tipo) {

        List<ExtratoResponseDTO> extrato;
        if (inicio != null && fim != null && tipo != null) {
            extrato = extratoService.listarPorPeriodoETipo(id, inicio, fim, tipo);
        } else if (inicio != null && fim != null) {
            extrato = extratoService.listarPorPeriodo(id, inicio, fim);
        } else if (tipo != null) {
            extrato = extratoService.listarPorTipo(id, tipo);
        } else {
            extrato = extratoService.listarPorConta(id);
        }
        return ResponseEntity.ok(extrato);
    }
}