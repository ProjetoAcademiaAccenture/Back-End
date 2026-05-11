package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ClienteRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.EnderecoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.ClienteResponseDTO;
import acc.br.projetoFinal.Accenture.enums.TipoCliente;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.Endereco;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.repository.EnderecoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ClienteResponseDTO criar(ClienteRequestDTO dto) {
        Cliente salvo = salvarCliente(dto);
        return ClienteResponseDTO.fromEntity(salvo);
    }

    @Transactional
    public Cliente criarEntidade(ClienteRequestDTO dto) {
        return salvarCliente(dto);
    }

    private Cliente salvarCliente(ClienteRequestDTO dto) {
        if (clienteRepository.findByCpf(dto.getCpf()).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        if (clienteRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        Cliente cliente = Cliente.builder()
                .nome(dto.getNome())
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .telefone(dto.getTelefone())
                .senha(passwordEncoder.encode(dto.getSenha()))
                .tipoCliente(TipoCliente.ROLE_USER)
                .dataNascimento(dto.getDtNascimento())
                .build();

        EnderecoRequestDTO end = dto.getEndereco();

        Endereco endereco = Endereco.builder()
                .cep(end.getCep())
                .logradouro(end.getLogradouro())
                .bairro(end.getBairro())
                .cidade(end.getCidade())
                .uf(end.getUf())
                .tipoEndereco(end.getTipoEndereco())
                .numero(end.getNumero())
                .complemento(end.getComplemento())
                .cliente(cliente)
                .build();

        cliente.getEnderecos().add(endereco);

        return clienteRepository.save(cliente);
    }

    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));
        return ClienteResponseDTO.fromEntity(cliente);
    }

    public ClienteResponseDTO buscarPorCpf(String cpf) {
        Cliente cliente = clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));
        return ClienteResponseDTO.fromEntity(cliente);
    }

    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll().stream()
                .map(ClienteResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));

        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());

        Cliente atualizado = clienteRepository.save(cliente);
        return ClienteResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void deletar(Long id) {
        if (!clienteRepository.existsById(id))
            throw new RecursoNaoEncontradoException("Cliente não encontrado");
        clienteRepository.deleteById(id);
    }

    @Transactional
    public void adicionarEndereco(Long clienteId, EnderecoRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));

        Endereco endereco = Endereco.builder()
                .cep(dto.getCep())
                .logradouro(dto.getLogradouro())
                .bairro(dto.getBairro())
                .cidade(dto.getCidade())
                .uf(dto.getUf())
                .numero(dto.getNumero())
                .complemento(dto.getComplemento())
                .tipoEndereco(dto.getTipoEndereco())
                .cliente(cliente)
                .build();

        enderecoRepository.save(endereco);
    }

    @Transactional
    public void removerEndereco(Long clienteId, Long enderecoId) {
        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Endereço não encontrado"));

        if (!endereco.getCliente().getId().equals(clienteId))
            throw new IllegalArgumentException("Endereço não pertence a este cliente");

        enderecoRepository.deleteById(enderecoId);
    }
}