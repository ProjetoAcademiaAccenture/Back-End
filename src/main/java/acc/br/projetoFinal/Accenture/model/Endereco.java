package acc.br.projetoFinal.Accenture.model;

import acc.br.projetoFinal.Accenture.enums.TipoEndereco;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "endereco")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_endereco", nullable = false)
    private TipoEndereco tipoEndereco = TipoEndereco.RESIDENCIAL;

    @Column(length = 8, nullable = false)
    private String cep;

    @Column(length = 150, nullable = false)
    private String logradouro;

    @Column(length = 10)
    private String numero;

    @Column(length = 100)
    private String complemento;

    @Column(length = 100, nullable = false)
    private String bairro;

    @Column(length = 100, nullable = false)
    private String cidade;

    @Column(length = 2, nullable = false)
    private String uf;
}
