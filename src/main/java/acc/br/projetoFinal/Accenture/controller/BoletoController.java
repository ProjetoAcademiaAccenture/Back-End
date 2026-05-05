package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.response.BoletoResponseDTO;
import acc.br.projetoFinal.Accenture.service.BoletoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@RestController
@RequestMapping("/api/boletos")
@RequiredArgsConstructor
public class BoletoController {

    private final BoletoService boletoService;

    @GetMapping("/{id}")
    public ResponseEntity<BoletoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(boletoService.buscarPorId(id));
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<BoletoResponseDTO> buscarPorPedidoId(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(boletoService.buscarPorPedidoId(pedidoId));
    }

    @PostMapping("/gerar/{pedidoId}")
    public ResponseEntity<BoletoResponseDTO> gerar(@PathVariable Long pedidoId) {
        BoletoResponseDTO gerado = boletoService.gerar(pedidoId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath("/api/boletos/{id}").buildAndExpand(gerado.getId()).toUri();
        return ResponseEntity.created(location).body(gerado);
    }

    @PatchMapping("/{id}/pagar")
    public ResponseEntity<BoletoResponseDTO> pagar(@PathVariable Long id) {
        return ResponseEntity.ok(boletoService.pagarBoleto(id));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        boletoService.cancelarBoleto(id);
        return ResponseEntity.noContent().build();
    }
}
