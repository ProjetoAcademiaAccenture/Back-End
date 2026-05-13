package acc.br.projetoFinal.Accenture.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import acc.br.projetoFinal.Accenture.dto.request.ClienteRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.ContaRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.LoginBankRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.LoginRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.AuthBankResponseDTO;
import acc.br.projetoFinal.Accenture.dto.response.AuthResponseDTO;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.exception.SenhaInvalidaException;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.repository.ContaRepository;
import acc.br.projetoFinal.Accenture.security.JwtService;
import acc.br.projetoFinal.Accenture.service.ClienteService;
import acc.br.projetoFinal.Accenture.service.ContaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
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
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
            );
        } catch (BadCredentialsException e) {
            log.warn("Tentativa de login com credenciais inválidas para o email: {}", dto.getEmail());
            throw new SenhaInvalidaException("Email ou senha inválidos");
        }

        Cliente cliente = clienteRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        String token = jwtService.gerarToken(cliente);
        log.info("Login realizado com sucesso para o cliente: {}", cliente.getId());

        return ResponseEntity.ok(AuthResponseDTO.builder()
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
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));

        if (!passwordEncoder.matches(dto.getSenha(), conta.getSenhaTransacao())) {
            log.warn("Tentativa de login-bank com senha inválida para a conta: {}", dto.getNumero_conta());
            throw new SenhaInvalidaException("Senha de transação inválida");
        }

        String token = jwtService.gerarToken(conta.getCliente());
        log.info("Login bancário realizado com sucesso para a conta: {}", conta.getId());

        return ResponseEntity.ok(AuthBankResponseDTO.builder()
                .token(token)
                .clienteId(conta.getCliente().getId())
                .contaId(conta.getId())
                .numeroConta(conta.getNumeroConta())
                .saldo(conta.getSaldo().toString())
                .limiteCeditoDisponivel(conta.getLimiteCreditoDisponivel().toString())
                .tipoConta(conta.getTipo().name())
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody @Valid ClienteRequestDTO dto) {
        Cliente cliente = clienteService.criarEntidade(dto);
        String token = jwtService.gerarToken(cliente);
        log.info("Novo cliente registrado com sucesso: {}", cliente.getId());

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
        String token = jwtService.gerarToken(conta.getCliente());
        BigDecimal valorDeposito = new BigDecimal("1000.00"); // valor fixo para depósito inicial
        BigDecimal limiteCreditoDisponivel = new BigDecimal("2000.00"); // valor fixo para limite de crédito
        conta = (contaService.depositar(conta.getId(), valorDeposito));
        conta = (contaService.creditarLimiteCredito(conta.getId(), limiteCreditoDisponivel));
        log.info("Nova conta bancária registrada com sucesso: {}", conta.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(AuthBankResponseDTO.builder()
                .token(token)
                .clienteId(conta.getCliente().getId())
                .contaId(conta.getId())
                .numeroConta(conta.getNumeroConta())
                .saldo(conta.getSaldo().toString())
                .limiteCeditoDisponivel(conta.getLimiteCreditoDisponivel().toString())
                .tipoConta(conta.getTipo().name())
                .build());
    }
}