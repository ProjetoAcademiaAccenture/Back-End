package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.model.Endereco;
import acc.br.projetoFinal.Accenture.enums.TipoEndereco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ClienteResponseDTO — Testes Positivos")
class ClienteResponseDTOTests {

    private Cliente cliente;
    private Endereco endereco1;
    private Endereco endereco2;
    private Conta conta;
    private ContaResponseDTO contaResponse;

    @BeforeEach
    void setUp() {
        endereco1 = Endereco.builder()
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

        endereco2 = Endereco.builder()
                .id(2L)
                .cep("58000-100")
                .logradouro("Avenida Getúlio Vargas")
                .bairro("Tambiá")
                .cidade("João Pessoa")
                .uf("PB")
                .tipoEndereco(TipoEndereco.COMERCIAL)
                .numero("456")
                .build();

        // Ajuste o builder de Conta conforme seu modelo real
        conta = Conta.builder()
                .id(1L)
                .build();

        contaResponse = ContaResponseDTO.fromEntity(conta);

        cliente = Cliente.builder()
                .id(1L)
                .nome("Maria Silva")
                .cpf("12345678900")
                .email("maria@email.com")
                .telefone("83999990000")
                .dataNascimento(LocalDate.of(1990, 5, 20))
                .conta(conta)
                .enderecos(List.of(endereco1, endereco2))
                .build();
    }

    @Test
    @DisplayName("Deve converter Cliente para ClienteResponseDTO com todos os campos")
    void deveConverterClienteParaDTO() {
        ClienteResponseDTO dto = ClienteResponseDTO.fromEntity(cliente);

        assertThat(dto)
                .isNotNull()
                .extracting("id", "nome", "cpf", "email", "telefone", "dataNascimento")
                .containsExactly(1L, "Maria Silva", "12345678900", "maria@email.com",
                        "83999990000", LocalDate.of(1990, 5, 20));
    }

    @Test
    @DisplayName("Deve mapear conta corretamente ao converter")
    void deveMapearContaAoConverter() {
        ClienteResponseDTO dto = ClienteResponseDTO.fromEntity(cliente);

        assertThat(dto.getConta()).isNotNull();
    }

    @Test
    @DisplayName("Deve mapear conta como null quando cliente não tem conta")
    void deveMapearContaNull_quandoClienteSemConta() {
        cliente.setConta(null);

        ClienteResponseDTO dto = ClienteResponseDTO.fromEntity(cliente);

        assertThat(dto.getConta()).isNull();
    }

    @Test
    @DisplayName("Deve converter lista de Enderecos para lista de EnderecoResponseDTO")
    void deveConverterEnderecosParaResponseDTO() {
        ClienteResponseDTO dto = ClienteResponseDTO.fromEntity(cliente);

        assertThat(dto.getEnderecos())
                .isNotNull()
                .hasSize(2)
                .extracting("id", "tipoEndereco")
                .containsExactly(
                        new org.assertj.core.groups.Tuple(1L, "RESIDENCIAL"),
                        new org.assertj.core.groups.Tuple(2L, "COMERCIAL")
                );
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando cliente não tem enderecos")
    void deveRetornarListaVaziaQuandoClienteSemEnderecos() {
        cliente.setEnderecos(null);

        ClienteResponseDTO dto = ClienteResponseDTO.fromEntity(cliente);

        assertThat(dto.getEnderecos())
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando cliente tem lista de enderecos vazia")
    void deveRetornarListaVaziaComListaDeEnderecoVazia() {
        cliente.setEnderecos(new ArrayList<>());

        ClienteResponseDTO dto = ClienteResponseDTO.fromEntity(cliente);

        assertThat(dto.getEnderecos())
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Deve construir DTO via builder com todos os campos")
    void deveConstituirDTOViaBuilder() {
        ClienteResponseDTO dto = ClienteResponseDTO.builder()
                .id(1L)
                .nome("João Silva")
                .cpf("98765432100")
                .email("joao@email.com")
                .telefone("83988880000")
                .dataNascimento(LocalDate.of(1995, 3, 15))
                .conta(contaResponse)
                .enderecos(List.of())
                .build();

        assertThat(dto)
                .extracting("id", "nome", "cpf", "email", "telefone", "dataNascimento")
                .containsExactly(1L, "João Silva", "98765432100", "joao@email.com",
                        "83988880000", LocalDate.of(1995, 3, 15));
        assertThat(dto.getConta()).isNotNull();
    }

    @Test
    @DisplayName("Deve criar DTO com construtor padrão")
    void deveCriarDTOComConstruturPadrao() {
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setId(1L);
        dto.setNome("Ana Santos");
        dto.setCpf("11122233344");
        dto.setEmail("ana@email.com");
        dto.setTelefone("83987770000");
        dto.setDataNascimento(LocalDate.of(1988, 7, 10));
        dto.setConta(contaResponse);
        dto.setEnderecos(List.of());

        assertThat(dto)
                .extracting("id", "nome", "cpf", "email", "telefone", "dataNascimento")
                .containsExactly(1L, "Ana Santos", "11122233344", "ana@email.com",
                        "83987770000", LocalDate.of(1988, 7, 10));
        assertThat(dto.getConta()).isNotNull();
    }

    @Test
    @DisplayName("Deve criar DTO com construtor all-args")
    void deveCriarDTOComConstruutorAllArgs() {
        List<EnderecoResponseDTO> enderecos = List.of();
        // Construtor all-args inclui: id, nome, cpf, email, telefone, dataNascimento, conta, enderecos
        ClienteResponseDTO dto = new ClienteResponseDTO(
                2L, "Pedro Costa", "55566677788", "pedro@email.com",
                "83986660000", LocalDate.of(1992, 11, 22), contaResponse, enderecos
        );

        assertThat(dto)
                .extracting("id", "nome", "cpf", "email", "telefone", "dataNascimento")
                .containsExactly(2L, "Pedro Costa", "55566677788", "pedro@email.com",
                        "83986660000", LocalDate.of(1992, 11, 22));
        assertThat(dto.getConta()).isEqualTo(contaResponse);
        assertThat(dto.getEnderecos()).isEqualTo(enderecos);
    }

    @Test
    @DisplayName("Deve manter integridade dos dados ao converter")
    void deveManterIntegridadeDosDadosAoConverter() {
        ClienteResponseDTO dto = ClienteResponseDTO.fromEntity(cliente);

        assertThat(dto.getId()).isEqualTo(cliente.getId());
        assertThat(dto.getNome()).isEqualTo(cliente.getNome());
        assertThat(dto.getCpf()).isEqualTo(cliente.getCpf());
        assertThat(dto.getEmail()).isEqualTo(cliente.getEmail());
        assertThat(dto.getTelefone()).isEqualTo(cliente.getTelefone());
        assertThat(dto.getDataNascimento()).isEqualTo(cliente.getDataNascimento());
    }

    @Test
    @DisplayName("Deve ter getters funcionando corretamente")
    void deveTemGettersFuncionandoCorretamente() {
        ClienteResponseDTO dto = ClienteResponseDTO.fromEntity(cliente);

        assertThat(dto.getId()).isNotNull();
        assertThat(dto.getNome()).isNotBlank();
        assertThat(dto.getCpf()).isNotBlank();
        assertThat(dto.getEmail()).isNotBlank();
        assertThat(dto.getTelefone()).isNotBlank();
        assertThat(dto.getDataNascimento()).isNotNull();
        assertThat(dto.getConta()).isNotNull();
        assertThat(dto.getEnderecos()).isNotNull();
    }

    @Test
    @DisplayName("Deve ter setters funcionando corretamente")
    void deveTemSettersFuncionandoCorretamente() {
        ClienteResponseDTO dto = new ClienteResponseDTO();

        dto.setId(10L);
        dto.setNome("Novo Nome");
        dto.setCpf("99988877766");
        dto.setEmail("novo@email.com");
        dto.setTelefone("83985550000");
        dto.setDataNascimento(LocalDate.of(2000, 1, 1));
        dto.setConta(contaResponse);
        dto.setEnderecos(List.of());

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getNome()).isEqualTo("Novo Nome");
        assertThat(dto.getCpf()).isEqualTo("99988877766");
        assertThat(dto.getEmail()).isEqualTo("novo@email.com");
        assertThat(dto.getTelefone()).isEqualTo("83985550000");
        assertThat(dto.getDataNascimento()).isEqualTo(LocalDate.of(2000, 1, 1));
        assertThat(dto.getConta()).isEqualTo(contaResponse);
    }

    @Test
    @DisplayName("Deve converter cliente com um único endereço")
    void deveConverterClienteComUnicoEndereco() {
        cliente.setEnderecos(List.of(endereco1));

        ClienteResponseDTO dto = ClienteResponseDTO.fromEntity(cliente);

        assertThat(dto.getEnderecos())
                .hasSize(1)
                .extracting("id", "tipoEndereco")
                .containsExactly(new org.assertj.core.groups.Tuple(1L, "RESIDENCIAL"));
    }

    @Test
    @DisplayName("Deve preservar order dos enderecos ao converter")
    void devePreservarOrderDosEnderecosAoConverter() {
        ClienteResponseDTO dto = ClienteResponseDTO.fromEntity(cliente);

        assertThat(dto.getEnderecos())
                .extracting("bairro")
                .containsExactly("Centro", "Tambiá");
    }
}