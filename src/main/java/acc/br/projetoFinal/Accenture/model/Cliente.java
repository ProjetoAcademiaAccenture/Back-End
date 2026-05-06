package acc.br.projetoFinal.Accenture.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 11, unique = true)
    private String cpf;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(length = 15)
    private String telefone;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Endereco> enderecos = new ArrayList<>();

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL)
    private Conta conta;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Pedido> pedidos = new ArrayList<>();

    // REGRAS DE NEGÓCIO
    public void validarCpf() {
        if (cpf == null || cpf.isEmpty() || cpf.length() != 11)
            throw new IllegalArgumentException("CPF deve ter 11 dígitos");
        if (!cpf.matches("\\d+"))
            throw new IllegalArgumentException("CPF deve conter apenas dígitos");
    }

    public void validarEmail() {
        if (email == null || email.isEmpty())
            throw new IllegalArgumentException("Email não pode estar vazio");
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$"))
            throw new IllegalArgumentException("Email deve ser válido");
    }

    public void validarNome() {
        if (nome == null || nome.trim().isEmpty())
            throw new IllegalArgumentException("Nome não pode estar vazio");
        if (nome.length() < 3 || nome.length() > 100)
            throw new IllegalArgumentException("Nome deve ter entre 3 e 100 caracteres");
    }
}
