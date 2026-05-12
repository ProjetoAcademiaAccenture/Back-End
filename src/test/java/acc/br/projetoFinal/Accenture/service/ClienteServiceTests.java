package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ClienteRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.EnderecoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.ClienteResponseDTO;
import acc.br.projetoFinal.Accenture.enums.TipoCliente;
import acc.br.projetoFinal.Accenture.enums.TipoEndereco;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteService — Testes Positivos")
class ClienteServiceTests {

    @Mock private ClienteRepository clienteRepository;
    @Mock private EnderecoRepository enderecoRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClienteService clienteService;

    // ------------------------------------------------------------------ fixtures

    private ClienteRequestDTO requestDTO;
    private Cliente clienteSalvo;

    @BeforeEach
    void setUp() {
        EnderecoRequestDTO enderecoDTO = EnderecoRequestDTO.builder()
                .cep("58000-000")
                .logradouro("Rua das Flores")
                .bairro("Centro")
                .cidade("João Pessoa")
                .uf("PB")
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

        Endereco endereco = Endereco.builder()
                .id(1L)
                .cep("58000-000")
                .logradouro("Rua das Flores")
                .bairro("Centro")
                .cidade("João Pessoa")
                .uf("PB")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .numero("123")
                .complemento("Apto 1")
                .build();

        clienteSalvo = Cliente.builder()
                .id(1L)
                .nome("Maria Silva")
                .cpf("123.456.789-00")
                .email("maria@email.com")
                .telefone("83999990000")
                .senha("hashed_senha")
                .tipoCliente(TipoCliente.ROLE_USER)
                .dataNascimento(LocalDate.of(1990, 5, 20))
                .enderecos(new ArrayList<>(List.of(endereco)))
                .build();

        endereco.setCliente(clienteSalvo);
    }

    // ------------------------------------------------------------------ criar()

    @Test
    @DisplayName("criar() — deve criar cliente com sucesso e retornar DTO")
    void criar_deveRetornarClienteResponseDTO() {
        when(clienteRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_senha");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        ClienteResponseDTO resultado = clienteService.criar(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Maria Silva");
        assertThat(resultado.getCpf()).isEqualTo("123.456.789-00");
        assertThat(resultado.getEmail()).isEqualTo("maria@email.com");

        verify(clienteRepository).save(any(Cliente.class));
        verify(passwordEncoder).encode("Senha@123");
    }

    @Test
    @DisplayName("criar() — deve criptografar a senha antes de salvar")
    void criar_deveCriptografarSenha() {
        when(clienteRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Senha@123")).thenReturn("hashed_senha");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        clienteService.criar(requestDTO);

        verify(passwordEncoder, times(1)).encode("Senha@123");
    }

    @Test
    @DisplayName("criar() — deve atribuir ROLE_USER ao novo cliente")
    void criar_deveAtribuirTipoClienteRoleUser() {
        when(clienteRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_senha");
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> {
            Cliente c = inv.getArgument(0);
            assertThat(c.getTipoCliente()).isEqualTo(TipoCliente.ROLE_USER);
            return clienteSalvo;
        });

        clienteService.criar(requestDTO);
    }

    // ------------------------------------------------------------------ criarEntidade()

    @Test
    @DisplayName("criarEntidade() — deve retornar entidade Cliente")
    void criarEntidade_deveRetornarCliente() {
        when(clienteRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_senha");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        Cliente resultado = clienteService.criarEntidade(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Maria Silva");
    }

    // ------------------------------------------------------------------ buscarPorId()

    @Test
    @DisplayName("buscarPorId() — deve retornar DTO quando cliente existe")
    void buscarPorId_deveRetornarDTO_quandoClienteExiste() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteSalvo));

        ClienteResponseDTO resultado = clienteService.buscarPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(clienteRepository).findById(1L);
    }

    // ------------------------------------------------------------------ buscarPorCpf()

    @Test
    @DisplayName("buscarPorCpf() — deve retornar DTO quando CPF existe")
    void buscarPorCpf_deveRetornarDTO_quandoCpfExiste() {
        when(clienteRepository.findByCpf("123.456.789-00")).thenReturn(Optional.of(clienteSalvo));

        ClienteResponseDTO resultado = clienteService.buscarPorCpf("123.456.789-00");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getCpf()).isEqualTo("123.456.789-00");
    }

    // ------------------------------------------------------------------ listarTodos()

    @Test
    @DisplayName("listarTodos() — deve retornar lista com todos os clientes")
    void listarTodos_deveRetornarLista() {
        Cliente outroCliente = Cliente.builder()
                .id(2L)
                .nome("João Santos")
                .cpf("987.654.321-00")
                .email("joao@email.com")
                .enderecos(new ArrayList<>())
                .build();

        when(clienteRepository.findAll()).thenReturn(List.of(clienteSalvo, outroCliente));

        List<ClienteResponseDTO> resultado = clienteService.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(ClienteResponseDTO::getNome)
                .containsExactlyInAnyOrder("Maria Silva", "João Santos");
    }

    @Test
    @DisplayName("listarTodos() — deve retornar lista vazia quando não há clientes")
    void listarTodos_deveRetornarListaVazia_quandoNaoHaClientes() {
        when(clienteRepository.findAll()).thenReturn(List.of());

        List<ClienteResponseDTO> resultado = clienteService.listarTodos();

        assertThat(resultado).isEmpty();
    }

    // ------------------------------------------------------------------ atualizar()

    @Test
    @DisplayName("atualizar() — deve atualizar os campos e retornar DTO atualizado")
    void atualizar_deveAtualizarCamposERetornarDTO() {
        ClienteRequestDTO dtoAtualizado = ClienteRequestDTO.builder()
                .nome("Maria Atualizada")
                .email("nova@email.com")
                .telefone("83988880000")
                .build();

        Cliente clienteAtualizado = Cliente.builder()
                .id(1L)
                .nome("Maria Atualizada")
                .email("nova@email.com")
                .telefone("83988880000")
                .enderecos(new ArrayList<>())
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteSalvo));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteAtualizado);

        ClienteResponseDTO resultado = clienteService.atualizar(1L, dtoAtualizado);

        assertThat(resultado.getNome()).isEqualTo("Maria Atualizada");
        assertThat(resultado.getEmail()).isEqualTo("nova@email.com");
        assertThat(resultado.getTelefone()).isEqualTo("83988880000");
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("atualizar() — deve chamar save exatamente uma vez")
    void atualizar_deveChamarSaveUmaVez() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteSalvo));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        clienteService.atualizar(1L, requestDTO);

        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    // ------------------------------------------------------------------ deletar()

    @Test
    @DisplayName("deletar() — deve deletar cliente existente sem lançar exceção")
    void deletar_deveDeletarClienteExistente() {
        when(clienteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(clienteRepository).deleteById(1L);

        clienteService.deletar(1L);

        verify(clienteRepository).deleteById(1L);
    }

    // ------------------------------------------------------------------ adicionarEndereco()

    @Test
    @DisplayName("adicionarEndereco() — deve salvar novo endereço ao cliente existente")
    void adicionarEndereco_deveSalvarEndereco() {
        EnderecoRequestDTO novoEndereco = EnderecoRequestDTO.builder()
                .cep("59000-000")
                .logradouro("Av. Comercial")
                .bairro("Manaíra")
                .cidade("João Pessoa")
                .uf("PB")
                .numero("500")
                .complemento(null)
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteSalvo));
        when(enderecoRepository.save(any(Endereco.class))).thenAnswer(inv -> inv.getArgument(0));

        clienteService.adicionarEndereco(1L, novoEndereco);

        verify(enderecoRepository, times(1)).save(any(Endereco.class));
    }

    @Test
    @DisplayName("adicionarEndereco() — deve associar o endereço ao cliente correto")
    void adicionarEndereco_deveAssociarClienteCorreto() {
        EnderecoRequestDTO novoEndereco = EnderecoRequestDTO.builder()
                .cep("58040-000")
                .logradouro("Rua X")
                .bairro("Tambaú")
                .cidade("João Pessoa")
                .uf("PB")
                .numero("10")
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteSalvo));
        when(enderecoRepository.save(any(Endereco.class))).thenAnswer(inv -> {
            Endereco e = inv.getArgument(0);
            assertThat(e.getCliente()).isEqualTo(clienteSalvo);
            return e;
        });

        clienteService.adicionarEndereco(1L, novoEndereco);
    }

    @Test
    @DisplayName("adicionarEndereco() — deve aceitar complemento nulo")
    void adicionarEndereco_deveAceitarComplementoNulo() {
        EnderecoRequestDTO enderecoSemComplemento = EnderecoRequestDTO.builder()
                .cep("58000-100")
                .logradouro("Rua Sem Complemento")
                .bairro("Bairro")
                .cidade("João Pessoa")
                .uf("PB")
                .numero("1")
                .complemento(null)
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteSalvo));
        when(enderecoRepository.save(any(Endereco.class))).thenAnswer(inv -> inv.getArgument(0));

        clienteService.adicionarEndereco(1L, enderecoSemComplemento);

        verify(enderecoRepository).save(argThat(e -> e.getComplemento() == null));
    }

    // ------------------------------------------------------------------ removerEndereco()

    @Test
    @DisplayName("removerEndereco() — deve remover endereço pertencente ao cliente")
    void removerEndereco_deveRemoverQuandoEnderecoEhDoCliente() {
        Endereco endereco = Endereco.builder()
                .id(1L)
                .cliente(clienteSalvo)
                .build();

        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
        doNothing().when(enderecoRepository).deleteById(1L);

        clienteService.removerEndereco(1L, 1L);

        verify(enderecoRepository).deleteById(1L);
    }
}