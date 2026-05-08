package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.request.ClienteRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.LoginRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.AuthResponseDTO;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.security.JwtService;
import acc.br.projetoFinal.Accenture.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final ClienteRepository clienteRepository;
	private final ClienteService clienteService;
	private final JwtService jwtService;

	@PostMapping("/login")
	public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
		);

		Cliente cliente = clienteRepository.findByEmail(dto.getEmail())
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		String token = jwtService.gerarToken(cliente);

		return ResponseEntity.status(HttpStatus.OK).body(AuthResponseDTO.builder()
				.token(token)
				.clienteId(cliente.getId())
				.nome(cliente.getNome())
				.tipoCliente(cliente.getTipoCliente().name())
				.build());
	}

	@PostMapping("/register")
	public ResponseEntity<AuthResponseDTO> register(@RequestBody @Valid ClienteRequestDTO dto) {
		Cliente cliente = clienteService.criarEntidade(dto);
		String token = jwtService.gerarToken(cliente);

		return ResponseEntity.status(HttpStatus.CREATED).body(AuthResponseDTO.builder()
				.token(token)
				.clienteId(cliente.getId())
				.nome(cliente.getNome())
				.tipoCliente(cliente.getTipoCliente().name())
				.build());
	}
}