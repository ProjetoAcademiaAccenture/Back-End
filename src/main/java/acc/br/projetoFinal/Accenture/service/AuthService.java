package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ClienteRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.AuthResponseDTO;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.security.JwtService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final ClienteRepository clienteRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	@Transactional
	public AuthResponseDTO register(ClienteRequestDTO dto) {

		if (clienteRepository.findByCpf(dto.getCpf()).isPresent()) {
			throw new IllegalArgumentException("CPF já cadastrado");
		}

		if (clienteRepository.findByEmail(dto.getEmail()).isPresent()) {
			throw new IllegalArgumentException("Email já cadastrado");
		}

		Cliente cliente = Cliente.builder()
				.nome(dto.getNome())
				.email(dto.getEmail())
				.cpf(dto.getCpf())
				.telefone(dto.getTelefone())
				.senha(passwordEncoder.encode(dto.getSenha()))
				.build();

		Cliente salvo = clienteRepository.save(cliente);

		String token = jwtService.gerarToken(salvo);

		return AuthResponseDTO.builder()
				.token(token)
				.clienteId(salvo.getId())
				.nome(salvo.getNome())
				.tipoCliente(salvo.getTipoCliente().name())
				.build();
	}
}