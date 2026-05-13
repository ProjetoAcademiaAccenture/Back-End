package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.model.Cliente;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteResponseDTO {

    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;
    private ContaResponseDTO conta;
    private List<EnderecoResponseDTO> enderecos;

    public static ClienteResponseDTO fromEntity(Cliente cliente) {
        return ClienteResponseDTO.builder()
            .id(cliente.getId())
            .nome(cliente.getNome())
            .cpf(cliente.getCpf())
            .email(cliente.getEmail())
            .telefone(cliente.getTelefone())
            .dataNascimento(cliente.getDataNascimento())
            .conta(cliente.getConta() != null ? ContaResponseDTO.fromEntity(cliente.getConta()) : null)
            .enderecos(cliente.getEnderecos() != null
                ? cliente.getEnderecos().stream()
                  .map(EnderecoResponseDTO::fromEntity)
                  .collect(Collectors.toList())
                : List.of())
            .build();
    }
}