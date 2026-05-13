package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ClienteRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.AuthResponseDTO;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService; // <- adicionado

    @Transactional
    public AuthResponseDTO register(ClienteRequestDTO dto) {

        if (clienteRepository.findByCpf(dto.getCpf()).isPresent()) {
            log.warn("Tentativa de cadastro com CPF já existente: {}", dto.getCpf());
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        if (clienteRepository.findByEmail(dto.getEmail()).isPresent()) {
            log.warn("Tentativa de cadastro com email já existente: {}", dto.getEmail());
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
        log.info("Cliente registrado com sucesso. ID: {}", salvo.getId());

        // Email de boas-vindas ao cliente
        emailService.enviarBoasVindas(salvo.getEmail(), salvo.getNome());

        String token = jwtService.gerarToken(salvo);

        return AuthResponseDTO.builder()
                .token(token)
                .clienteId(salvo.getId())
                .nome(salvo.getNome())
                .email(salvo.getEmail())
                .tipoCliente(salvo.getTipoCliente().name())
                .build();
    }
}