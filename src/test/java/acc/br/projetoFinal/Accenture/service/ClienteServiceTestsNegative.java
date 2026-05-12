package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ClienteRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.EnderecoRequestDTO;
import acc.br.projetoFinal.Accenture.enums.TipoEndereco;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.Endereco;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.repository.EnderecoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteService — Testes Negativos")
class ClienteServiceTestsNegative {

    @Mock private ClienteRepository clienteRepository;
    @Mock private EnderecoRepository enderecoRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClienteService clienteService;

    // ------------------------------------------------------------------ fixtures

    private ClienteRequestDTO requestDTO;
    private Cliente clienteExistente;

    @BeforeEach
    void setUp() {
        EnderecoRequestDTO enderecoDTO = EnderecoRequestDTO.builder()
                .cep("58000-000")
                .logradouro("Rua das Flores")
                .bairro("Centro")
                .cidade("João Pessoa")
                .uf("PB")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .numero("123")
                .complemento("Apto 1")
                .build();

        requestDTO = ClienteRequestDTO.builder()
                .nome("Maria Silva")
                .cpf("123.456.789-00")
                .email("maria@email.com")
                .telefone("83999990000")
                .senha("Senha@123")
                .dtNascimento(LocalDate.of(1990, 5, 20))
                .endereco(enderecoDTO)
                .build();

        clienteExistente = Cliente.builder()
                .id(1L)
                .nome("Maria Silva")
                .cpf("123.456.789-00")
                .email("maria@email.com")
                .enderecos(new ArrayList<>())
                .build();
    }

    // ------------------------------------------------------------------ criar() — CPF duplicado

    @Test
    @DisplayName("criar() — deve lançar IllegalArgumentException quando CPF já cadastrado")
    void criar_deveLancarExcecao_quandoCpfJaCadastrado() {
        when(clienteRepository.findByCpf("123.456.789-00"))
                .thenReturn(Optional.of(clienteExistente));

        assertThatThrownBy(() -> clienteService.criar(requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CPF já cadastrado");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("criar() — não deve salvar quando CPF já existe")
    void criar_naoDeveSalvar_quandoCpfDuplicado() {
        when(clienteRepository.findByCpf(anyString()))
                .thenReturn(Optional.of(clienteExistente));

        assertThatThrownBy(() -> clienteService.criar(requestDTO))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoMoreInteractions(enderecoRepository);
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    // ------------------------------------------------------------------ criar() — Email duplicado

    @Test
    @DisplayName("criar() — deve lançar IllegalArgumentException quando Email já cadastrado")
    void criar_deveLancarExcecao_quandoEmailJaCadastrado() {
        when(clienteRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail("maria@email.com"))
                .thenReturn(Optional.of(clienteExistente));

        assertThatThrownBy(() -> clienteService.criar(requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email já cadastrado");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("criar() — não deve salvar quando email duplicado (branch independente de CPF)")
    void criar_naoDeveSalvar_quandoEmailDuplicado() {
        when(clienteRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(clienteExistente));

        assertThatThrownBy(() -> clienteService.criar(requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email já cadastrado");

        verify(passwordEncoder, never()).encode(anyString());
    }

    // ------------------------------------------------------------------ criarEntidade() — duplicados

    @Test
    @DisplayName("criarEntidade() — deve lançar exceção quando CPF duplicado")
    void criarEntidade_deveLancarExcecao_quandoCpfDuplicado() {
        when(clienteRepository.findByCpf(anyString()))
                .thenReturn(Optional.of(clienteExistente));

        assertThatThrownBy(() -> clienteService.criarEntidade(requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CPF já cadastrado");
    }

    @Test
    @DisplayName("criarEntidade() — deve lançar exceção quando email duplicado")
    void criarEntidade_deveLancarExcecao_quandoEmailDuplicado() {
        when(clienteRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(clienteExistente));

        assertThatThrownBy(() -> clienteService.criarEntidade(requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email já cadastrado");
    }

    // ------------------------------------------------------------------ buscarPorId() — não encontrado

    @Test
    @DisplayName("buscarPorId() — deve lançar RecursoNaoEncontradoException quando ID inexistente")
    void buscarPorId_deveLancarExcecao_quandoIdNaoExiste() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Cliente não encontrado");
    }

    @Test
    @DisplayName("buscarPorId() — deve lançar exceção para qualquer ID inexistente")
    void buscarPorId_deveLancarExcecao_paraQualquerIdInexistente() {
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.buscarPorId(Long.MAX_VALUE))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    // ------------------------------------------------------------------ buscarPorCpf() — não encontrado

    @Test
    @DisplayName("buscarPorCpf() — deve lançar RecursoNaoEncontradoException quando CPF não existe")
    void buscarPorCpf_deveLancarExcecao_quandoCpfNaoExiste() {
        when(clienteRepository.findByCpf("000.000.000-00")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.buscarPorCpf("000.000.000-00"))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Cliente não encontrado");
    }

    // ------------------------------------------------------------------ atualizar() — não encontrado

    @Test
    @DisplayName("atualizar() — deve lançar RecursoNaoEncontradoException quando ID inexistente")
    void atualizar_deveLancarExcecao_quandoClienteNaoExiste() {
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.atualizar(999L, requestDTO))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Cliente não encontrado");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("atualizar() — não deve chamar save quando cliente não for encontrado")
    void atualizar_naoDeveSalvar_quandoClienteNaoEncontrado() {
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.atualizar(1L, requestDTO))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    // ------------------------------------------------------------------ deletar() — não encontrado

    @Test
    @DisplayName("deletar() — deve lançar RecursoNaoEncontradoException quando ID inexistente")
    void deletar_deveLancarExcecao_quandoClienteNaoExiste() {
        when(clienteRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> clienteService.deletar(999L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Cliente não encontrado");

        verify(clienteRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("deletar() — não deve chamar deleteById quando cliente não existe")
    void deletar_naoDeveDeleteById_quandoNaoExiste() {
        when(clienteRepository.existsById(anyLong())).thenReturn(false);

        assertThatThrownBy(() -> clienteService.deletar(1L))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(clienteRepository, never()).deleteById(anyLong());
    }

    // ------------------------------------------------------------------ adicionarEndereco() — cliente não encontrado

    @Test
    @DisplayName("adicionarEndereco() — deve lançar exceção quando cliente não existe")
    void adicionarEndereco_deveLancarExcecao_quandoClienteNaoExiste() {
        EnderecoRequestDTO novoEndereco = EnderecoRequestDTO.builder()
                .cep("58000-000").logradouro("Av. X").bairro("Centro")
                .cidade("CG").uf("PB").tipoEndereco(TipoEndereco.RESIDENCIAL).numero("1")
                .build();

        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.adicionarEndereco(99L, novoEndereco))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Cliente não encontrado");

        verify(enderecoRepository, never()).save(any());
    }

    @Test
    @DisplayName("adicionarEndereco() — não deve chamar enderecoRepository.save quando cliente inexistente")
    void adicionarEndereco_naoDeveSalvarEndereco_quandoClienteInexistente() {
        EnderecoRequestDTO novoEndereco = EnderecoRequestDTO.builder()
                .cep("58000-000").logradouro("Rua Y").bairro("Bairro")
                .cidade("JP").uf("PB").tipoEndereco(TipoEndereco.RESIDENCIAL).numero("2")
                .build();

        when(clienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.adicionarEndereco(1L, novoEndereco))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verifyNoInteractions(enderecoRepository);
    }

    // ------------------------------------------------------------------ removerEndereco() — endereço não encontrado

    @Test
    @DisplayName("removerEndereco() — deve lançar exceção quando endereço não existe")
    void removerEndereco_deveLancarExcecao_quandoEnderecoNaoExiste() {
        when(enderecoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.removerEndereco(1L, 99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Endereço não encontrado");

        verify(enderecoRepository, never()).deleteById(anyLong());
    }

    // ------------------------------------------------------------------ removerEndereco() — endereço de outro cliente

    @Test
    @DisplayName("removerEndereco() — deve lançar IllegalArgumentException quando endereço pertence a outro cliente")
    void removerEndereco_deveLancarExcecao_quandoEnderecoNaoPertenceAoCliente() {
        Cliente outroCliente = Cliente.builder()
                .id(2L)
                .nome("João Santos")
                .enderecos(new ArrayList<>())
                .build();

        Endereco enderecoDeOutroCliente = Endereco.builder()
                .id(5L)
                .cliente(outroCliente)
                .build();

        when(enderecoRepository.findById(5L)).thenReturn(Optional.of(enderecoDeOutroCliente));

        // clienteId = 1L, mas endereço pertence ao cliente 2L
        assertThatThrownBy(() -> clienteService.removerEndereco(1L, 5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Endereço não pertence a este cliente");

        verify(enderecoRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("removerEndereco() — não deve deletar quando endereço é de outro cliente")
    void removerEndereco_naoDeveDeletar_quandoEnderecoDeOutroCliente() {
        Cliente clienteErrado = Cliente.builder().id(99L).enderecos(new ArrayList<>()).build();
        Endereco enderecoAlheio = Endereco.builder().id(10L).cliente(clienteErrado).build();

        when(enderecoRepository.findById(10L)).thenReturn(Optional.of(enderecoAlheio));

        assertThatThrownBy(() -> clienteService.removerEndereco(1L, 10L))
                .isInstanceOf(IllegalArgumentException.class);

        verify(enderecoRepository, never()).deleteById(anyLong());
    }
}