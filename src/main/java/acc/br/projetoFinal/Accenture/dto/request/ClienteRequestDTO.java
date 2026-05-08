package acc.br.projetoFinal.Accenture.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100)
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @Size(min = 11, max = 11)
    private String cpf;

    @NotBlank(message = "Email é obrigatório")
    @Email
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100)
    private String senha;

    @Size(max = 15)
    private String telefone;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dtNascimento;

    @Valid
    @NotNull
    private EnderecoRequestDTO endereco;
}