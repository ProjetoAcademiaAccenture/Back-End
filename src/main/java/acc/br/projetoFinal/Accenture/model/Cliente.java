package acc.br.projetoFinal.Accenture.model;

import acc.br.projetoFinal.Accenture.enums.TipoCliente;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(length = 15)
    private String telefone;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCliente tipoCliente;

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL)
    private Conta conta;

    @Builder.Default
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Endereco> enderecos = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "cliente")
    private List<Pedido> pedidos = new ArrayList<>();

    /**
     * Valida o CPF do cliente.
     * Regras:
     * - Não pode ser nulo ou vazio
     * - Deve conter exatamente 11 dígitos
     * - Deve conter apenas caracteres numéricos
     *
     * @throws IllegalArgumentException se o CPF for inválido
     */
    public void validarCpf() {
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF não pode ser nulo ou vazio.");
        }
        if (!cpf.matches("\\d{11}")) {
            throw new IllegalArgumentException(
                "CPF deve conter exatamente 11 dígitos numéricos. CPF informado: " + cpf
            );
        }
    }

    /**
     * Valida o e-mail do cliente.
     * Regras:
     * - Não pode ser nulo ou vazio
     * - Deve conter '@'
     * - Deve possuir domínio após o '@'
     *
     * @throws IllegalArgumentException se o e-mail for inválido
     */
    public void validarEmail() {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("E-mail não pode ser nulo ou vazio.");
        }
        // Regex básica: <local>@<domínio> onde domínio não pode ser vazio
        if (!email.matches("^[^@]+@[^@]+\\.[^@]+$")) {
            throw new IllegalArgumentException(
                "E-mail inválido. Deve conter '@' e um domínio válido. E-mail informado: " + email
            );
        }
    }

    /**
     * Valida o nome do cliente.
     * Regras:
     * - Não pode ser nulo ou vazio
     * - Não pode ser composto apenas por espaços
     * - Deve ter entre 3 e 100 caracteres
     *
     * @throws IllegalArgumentException se o nome for inválido
     */
    public void validarNome() {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser nulo, vazio ou composto apenas por espaços.");
        }
        if (nome.length() < 3) {
            throw new IllegalArgumentException(
                "Nome deve ter no mínimo 3 caracteres. Nome informado: '" + nome + "'"
            );
        }
        if (nome.length() > 100) {
            throw new IllegalArgumentException(
                "Nome deve ter no máximo 100 caracteres. Tamanho informado: " + nome.length()
            );
        }
    }
}