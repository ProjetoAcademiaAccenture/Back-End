package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.request.ClienteRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.ContaRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.LoginBankRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.LoginRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.AuthBankResponseDTO;
import acc.br.projetoFinal.Accenture.dto.response.AuthResponseDTO;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.repository.ContaRepository;
import acc.br.projetoFinal.Accenture.security.JwtService;
import acc.br.projetoFinal.Accenture.service.ClienteService;
import acc.br.projetoFinal.Accenture.service.ContaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final ClienteRepository clienteRepository;
	private final ContaRepository contaRepository;
	private final ClienteService clienteService;
	private final JwtService jwtService;
	private final ContaService contaService;
	private final PasswordEncoder passwordEncoder;

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
				.email(cliente.getEmail())
				.tipoCliente(cliente.getTipoCliente().name())
				.build());
	}

	@PostMapping("/login-bank")
	public ResponseEntity<AuthBankResponseDTO> loginBank(@RequestBody @Valid LoginBankRequestDTO dto) {
		Conta conta = contaRepository.findByNumeroConta(dto.getNumero_conta())
				.orElseThrow(() -> new RuntimeException("Conta não encontrada"));

		if (!passwordEncoder.matches(dto.getSenha(), conta.getSenhaTransacao())) {
			throw new RuntimeException("Senha inválida");
		}

		String token = jwtService.gerarToken(conta.getCliente ());

		return ResponseEntity.status (HttpStatus.OK).body(AuthBankResponseDTO.builder()
				.token(token)
				.clienteId(conta.getCliente().getId())
				.contaId(conta.getId())
				.numeroConta(conta.getNumeroConta())
				.saldo(conta.getSaldo().toString())
				.tipoConta(conta.getTipo().name())
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
				.email(cliente.getEmail())
				.tipoCliente(cliente.getTipoCliente().name())
				.build());
	}

	@PostMapping("/register-bank")
	public ResponseEntity<AuthBankResponseDTO> registerBank(@RequestBody @Valid ContaRequestDTO dto) {
		Conta conta = contaService.criarEntidade(dto);
		String token = jwtService.gerarToken(conta);

		return ResponseEntity.status(HttpStatus.CREATED).body(AuthBankResponseDTO.builder()
				.token(token)
				.clienteId(conta.getCliente().getId())
				.contaId(conta.getId())
				.numeroConta(conta.getNumeroConta())
				.saldo(conta.getSaldo().toString())
				.tipoConta(conta.getTipo().name())
				.build());
	}
}