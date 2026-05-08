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

    @Column(nullable = false, length = 11, unique = true)
    private String cpf;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCliente tipoCliente = TipoCliente.ROLE_USER;

    @Column(length = 15)
    private String telefone;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    // 1:N — cliente pode ter vários endereços (residencial, comercial...)
    @Builder.Default
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Endereco> enderecos = new ArrayList<>();

    // 1:1 obrigatório — todo cliente tem uma conta
    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL)
    private Conta conta;

    // 1:N com pedidos
    @Builder.Default
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pedido> pedidos = new ArrayList<>();
}
