package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.request.PedidoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.PedidoResponseDTO;
import acc.br.projetoFinal.Accenture.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorCliente(@PathVariable Long clienteId)
    throws AccessDeniedException {
        return ResponseEntity.ok(pedidoService.listarPorCliente(clienteId));
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criar(@RequestBody @Valid PedidoRequestDTO dto) {
        PedidoResponseDTO criado = pedidoService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(criado.getId())
            .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.cancelarPedido(id));
    }
}