package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.request.ContaRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.ContaResponseDTO;
import acc.br.projetoFinal.Accenture.dto.response.ExtratoResponseDTO;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.service.ContaService;
import acc.br.projetoFinal.Accenture.service.ExtratoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/contas")
@RequiredArgsConstructor
public class ContaController {

    private final ContaService contaService;
    private final ExtratoService extratoService;

    @GetMapping("/{id}")
    public ResponseEntity<ContaResponseDTO> buscarPorId(@PathVariable Long id) {
        Conta conta = contaService.buscarPorId(id);
        return ResponseEntity.ok(ContaResponseDTO.fromEntity(conta));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ContaResponseDTO> buscarPorCliente(@PathVariable Long clienteId) {
        Conta conta = contaService.buscarContaDoCliente(clienteId);
        return ResponseEntity.ok(ContaResponseDTO.fromEntity(conta));
    }

    @PostMapping
    public ResponseEntity<ContaResponseDTO> criar(@RequestBody @Valid ContaRequestDTO dto) {
        Conta criada = contaService.criarEntidade(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(criada.getId())
            .toUri();
        return ResponseEntity.created(location).body(ContaResponseDTO.fromEntity(criada));
    }

    @PatchMapping("/{id}/depositar")
    public ResponseEntity<ContaResponseDTO> depositar(@PathVariable Long id, @RequestParam BigDecimal valor) {
        Conta atualizada = contaService.depositar(id, valor);
        return ResponseEntity.ok(ContaResponseDTO.fromEntity(atualizada));
    }

    @GetMapping("/{id}/extrato")
    public ResponseEntity<List<ExtratoResponseDTO>> listarExtrato(
        @PathVariable Long id,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {

        if (inicio != null && fim != null) {
            return ResponseEntity.ok(extratoService.listarPorPeriodo(id, inicio, fim));
        }

        return ResponseEntity.ok(extratoService.listarPorConta(id));
    }
}