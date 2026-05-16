package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.request.ClienteRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.EnderecoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.ClienteResponseDTO;
import acc.br.projetoFinal.Accenture.dto.response.EnderecoResponseDTO;
import acc.br.projetoFinal.Accenture.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarTodos() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ClienteResponseDTO> buscarPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(clienteService.buscarPorCpf(cpf));
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> criar(@RequestBody @Valid ClienteRequestDTO dto) {
        ClienteResponseDTO criado = clienteService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(criado.getId())
            .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid ClienteRequestDTO dto) {
        return ResponseEntity.ok(clienteService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // ── Endereços ──────────────────────────────────────────────────────────────

    @GetMapping("/{id}/enderecos")
    public ResponseEntity<List<EnderecoResponseDTO>> listarEnderecos(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.listarEnderecos(id));
    }

    @GetMapping("/{id}/enderecos/{enderecoId}")
    public ResponseEntity<EnderecoResponseDTO> buscarEnderecoPorId(
            @PathVariable Long id,
            @PathVariable Long enderecoId) {
        return ResponseEntity.ok(clienteService.buscarEnderecoPorId(id, enderecoId));
    }

    @PostMapping("/{id}/enderecos")
    public ResponseEntity<Void> adicionarEndereco(@PathVariable Long id, @RequestBody @Valid EnderecoRequestDTO dto) {
        clienteService.adicionarEndereco(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/enderecos/{enderecoId}")
    public ResponseEntity<Void> removerEndereco(@PathVariable Long id, @PathVariable Long enderecoId) {
        clienteService.removerEndereco(id, enderecoId);
        return ResponseEntity.noContent().build();
    }
}