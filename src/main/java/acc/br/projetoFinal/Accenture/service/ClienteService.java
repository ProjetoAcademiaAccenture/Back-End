package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ClienteRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.EnderecoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.ClienteResponseDTO;
import acc.br.projetoFinal.Accenture.enums.TipoConta;
import acc.br.projetoFinal.Accenture.enums.TipoEndereco;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.model.Endereco;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.repository.ContaRepository;
import acc.br.projetoFinal.Accenture.repository.EnderecoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ContaRepository contaRepository;
    private final EnderecoRepository enderecoRepository;
    private final ViaCepService viaCepService;

    @Transactional
    public ClienteResponseDTO criar(ClienteRequestDTO dto) {
        // 1. Valida CPF e email únicos
        if (clienteRepository.findByCpf(dto.getCpf()).isPresent())
            throw new IllegalArgumentException("CPF já cadastrado");
        if (clienteRepository.findByEmail(dto.getEmail()).isPresent())
            throw new IllegalArgumentException("Email já cadastrado");

        // 2. Cria o cliente
        Cliente cliente = Cliente.builder()
                .nome(dto.getNome())
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .telefone(dto.getTelefone())
                .build();
        Cliente salvo = clienteRepository.save(cliente);

        // 3. Busca endereço pelo CEP e associa ao cliente
        Endereco endereco = viaCepService.buscarEnderecoPorCep(dto.getCep());
        endereco.setCliente(salvo);
        endereco.setNumero(dto.getNumero());
        endereco.setComplemento(dto.getComplemento());
        endereco.setTipoEndereco(TipoEndereco.RESIDENCIAL);
        enderecoRepository.save(endereco);

        // 4. Cria conta corrente automaticamente (obrigatória)
        Conta conta = Conta.builder()
                .numeroConta("CLI-" + salvo.getId())
                .saldo(BigDecimal.ZERO)
                .tipo(TipoConta.CORRENTE)
                .cliente(salvo)
                .ativo(true)
                .build();
        contaRepository.save(conta);

        return ClienteResponseDTO.fromEntity(salvo);
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

        Endereco endereco = viaCepService.buscarEnderecoPorCep(dto.getCep());
        endereco.setCliente(cliente);
        endereco.setNumero(dto.getNumero());
        endereco.setComplemento(dto.getComplemento());
        endereco.setTipoEndereco(dto.getTipoEndereco());
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
