package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.request.ClienteRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.ContaRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.LoginBankRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.LoginRequestDTO;
import acc.br.projetoFinal.Accenture.enums.TipoCliente;
import acc.br.projetoFinal.Accenture.enums.TipoConta;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.exception.SenhaInvalidaException;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.repository.ContaRepository;
import acc.br.projetoFinal.Accenture.security.JwtService;
import acc.br.projetoFinal.Accenture.service.ClienteService;
import acc.br.projetoFinal.Accenture.service.ContaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController - Testes Negativos")
class AuthControllerNegativeTests {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private ClienteRepository clienteRepository;
    @Mock private ContaRepository contaRepository;
    @Mock private ClienteService clienteService;
    @Mock private ContaService contaService;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    private LoginRequestDTO loginDto;
    private LoginBankRequestDTO loginBankDto;
    private ClienteRequestDTO registerDto;
    private ContaRequestDTO contaRequestDTO;
    private Cliente cliente;
    private Conta conta;

    @BeforeEach
    void setUp() {
        loginDto = new LoginRequestDTO();
        loginDto.setEmail("joao@test.com");
        loginDto.setSenha("senha123");

        loginBankDto = new LoginBankRequestDTO();
        loginBankDto.setNumero_conta("12345-6");
        loginBankDto.setSenha("senha123");

        registerDto = ClienteRequestDTO.builder()
                .nome("João Silva")
                .email("joao@test.com")
                .cpf("12345678900")
                .telefone("11999999999")
                .senha("senha123")
                .build();

        contaRequestDTO = ContaRequestDTO.builder()
                .clienteId(1L)
                .senhaTransacao("senha123")
                .build();

        cliente = Cliente.builder()
                .id(1L)
                .nome("João Silva")
                .email("joao@test.com")
                .tipoCliente(TipoCliente.ROLE_USER)
                .build();

        conta = Conta.builder()
                .id(1L)
                .numeroConta("12345-6")
                .saldo(BigDecimal.ZERO)
                .senhaTransacao("senhaCriptografada")
                .tipo(TipoConta.CORRENTE)
                .cliente(cliente)
                .build();
    }

    @Test
    @DisplayName("✗ Deve lançar SenhaInvalidaException ao login com credenciais inválidas")
    void testLoginComCredenciaisInvalidas() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        assertThrows(SenhaInvalidaException.class, () -> authController.login(loginDto));
    }

    @Test
    @DisplayName("✗ Não deve consultar o repositório quando a autenticação falha")
    void testLoginFalhaDeveInterromperFluxo() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        assertThrows(SenhaInvalidaException.class, () -> authController.login(loginDto));

        verifyNoInteractions(clienteRepository);
    }

    @Test
    @DisplayName("✗ Deve lançar RecursoNaoEncontradoException ao não encontrar cliente no login")
    void testLoginClienteNaoEncontrado() {
        when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> authController.login(loginDto));
    }

    @Test
    @DisplayName("✗ Deve lançar SenhaInvalidaException ao login-bank com senha inválida")
    void testLoginBankSenhaInvalida() {
        when(contaRepository.findByNumeroConta(anyString())).thenReturn(Optional.of(conta));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(SenhaInvalidaException.class, () -> authController.loginBank(loginBankDto));
    }

    @Test
    @DisplayName("✗ Não deve gerar token quando a senha de transação é inválida")
    void testLoginBankSenhaInvalidaNaoGeraToken() {
        when(contaRepository.findByNumeroConta(anyString())).thenReturn(Optional.of(conta));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(SenhaInvalidaException.class, () -> authController.loginBank(loginBankDto));

        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("✗ Não deve gerar token quando clienteService lança exceção")
    void testRegistrarFalhaNoClienteServiceNaoGeraToken() {
        when(clienteService.criarEntidade(any(ClienteRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Email já existe"));

        assertThrows(IllegalArgumentException.class, () -> authController.register(registerDto));

        verifyNoInteractions(jwtService);
    }
}