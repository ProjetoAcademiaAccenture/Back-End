package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.request.PagamentoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.PagamentoResponseDTO;
import acc.br.projetoFinal.Accenture.service.PagamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

	private final PagamentoService pagamentoService;

	@GetMapping("/{id}")
	public ResponseEntity<PagamentoResponseDTO> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(pagamentoService.buscarPorId(id));
	}

	@GetMapping("/pedido/{pedidoId}")
	public ResponseEntity<PagamentoResponseDTO> buscarPorPedido(@PathVariable Long pedidoId) {
		return ResponseEntity.ok(pagamentoService.buscarPorPedidoId(pedidoId));
	}

	@PostMapping("/processar")
	public ResponseEntity<PagamentoResponseDTO> processar(@RequestBody @Valid PagamentoRequestDTO dto) {
		return ResponseEntity.ok(pagamentoService.processar(dto));
	}

	@PatchMapping("/{id}/cancelar")
	public ResponseEntity<Void> cancelar(@PathVariable Long id) {
		pagamentoService.cancelar(id);
		return ResponseEntity.noContent().build();
	}
}