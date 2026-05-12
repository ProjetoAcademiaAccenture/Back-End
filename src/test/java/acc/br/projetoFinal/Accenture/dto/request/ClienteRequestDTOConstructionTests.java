package acc.br.projetoFinal.Accenture.dto.request;

import acc.br.projetoFinal.Accenture.enums.TipoEndereco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("ClienteRequestDTO — Testes de Construção e Métodos")
class ClienteRequestDTOConstructionTests {

    private ClienteRequestDTO dto;
    private EnderecoRequestDTO endereco;

    @BeforeEach
    void setUp() {
        endereco = EnderecoRequestDTO.builder()
                .cep("58000-000")
                .logradouro("Rua das Flores")
                .bairro("Centro")
                .cidade("João Pessoa")
                .uf("PB")
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .numero("123")
                .complemento("Apto 1")
                .build();
    }

    @Test
    @DisplayName("Deve construir DTO com construtor all-args")
    void deveConstruirComConstrutorAllArgs() {
        dto = new ClienteRequestDTO("João Silva", "12345678900", "joao@email.com",
                "Senha@123", "83999990000", LocalDate.of(1990, 5, 20), endereco);

        assertThat(dto)
                .extracting("nome", "cpf", "email", "senha", "telefone", "dtNascimento")
                .containsExactly("João Silva", "12345678900", "joao@email.com",
                        "Senha@123", "83999990000", LocalDate.of(1990, 5, 20));
        assertThat(dto.getEndereco()).isEqualTo(endereco);
    }

    @Test
    @DisplayName("Deve construir DTO com construtor vazio")
    void deveConstruirComConstrutuorVazio() {
        dto = new ClienteRequestDTO();

        assertThat(dto.getNome()).isNull();
        assertThat(dto.getCpf()).isNull();
        assertThat(dto.getEmail()).isNull();
        assertThat(dto.getSenha()).isNull();
        assertThat(dto.getTelefone()).isNull();
        assertThat(dto.getDtNascimento()).isNull();
        assertThat(dto.getEndereco()).isNull();
    }

    @Test
    @DisplayName("Deve construir via builder com todos os campos")
    void deveConstruirViaBuilderComTodosCampos() {
        dto = ClienteRequestDTO.builder()
                .nome("Maria Silva")
                .cpf("98765432100")
                .email("maria@email.com")
                .senha("Senha@456")
                .telefone("83988880000")
                .dtNascimento(LocalDate.of(1995, 3, 15))
                .endereco(endereco)
                .build();

        assertThat(dto.getNome()).isEqualTo("Maria Silva");
        assertThat(dto.getCpf()).isEqualTo("98765432100");
        assertThat(dto.getEmail()).isEqualTo("maria@email.com");
        assertThat(dto.getSenha()).isEqualTo("Senha@456");
        assertThat(dto.getTelefone()).isEqualTo("83988880000");
        assertThat(dto.getDtNascimento()).isEqualTo(LocalDate.of(1995, 3, 15));
        assertThat(dto.getEndereco()).isEqualTo(endereco);
    }

    @Test
    @DisplayName("Deve settar e gettar nome")
    void deveSettarEGettarNome() {
        dto = new ClienteRequestDTO();
        dto.setNome("Carlos Eduardo");

        assertThat(dto.getNome()).isEqualTo("Carlos Eduardo");
    }

    @Test
    @DisplayName("Deve settar e gettar cpf")
    void deveSettarEGettarCpf() {
        dto = new ClienteRequestDTO();
        dto.setCpf("11122233344");

        assertThat(dto.getCpf()).isEqualTo("11122233344");
    }

    @Test
    @DisplayName("Deve settar e gettar email")
    void deveSettarEGettarEmail() {
        dto = new ClienteRequestDTO();
        dto.setEmail("teste@email.com");

        assertThat(dto.getEmail()).isEqualTo("teste@email.com");
    }

    @Test
    @DisplayName("Deve settar e gettar senha")
    void deveSettarEGettarSenha() {
        dto = new ClienteRequestDTO();
        dto.setSenha("SenhaForte@123");

        assertThat(dto.getSenha()).isEqualTo("SenhaForte@123");
    }

    @Test
    @DisplayName("Deve settar e gettar telefone")
    void deveSettarEGettarTelefone() {
        dto = new ClienteRequestDTO();
        dto.setTelefone("83987654321");

        assertThat(dto.getTelefone()).isEqualTo("83987654321");
    }

    @Test
    @DisplayName("Deve settar e gettar dataNascimento")
    void deveSettarEGettarDataNascimento() {
        dto = new ClienteRequestDTO();
        LocalDate data = LocalDate.of(1992, 7, 10);
        dto.setDtNascimento(data);

        assertThat(dto.getDtNascimento()).isEqualTo(data);
    }

    @Test
    @DisplayName("Deve settar e gettar endereco")
    void deveSettarEGettarEndereco() {
        dto = new ClienteRequestDTO();
        dto.setEndereco(endereco);

        assertThat(dto.getEndereco()).isEqualTo(endereco);
    }

    @Test
    @DisplayName("Deve atualizar campos múltiplas vezes")
    void deveAtualizarCamposMúltiplaVezes() {
        dto = new ClienteRequestDTO();

        dto.setNome("Nome 1");
        assertThat(dto.getNome()).isEqualTo("Nome 1");

        dto.setNome("Nome 2");
        assertThat(dto.getNome()).isEqualTo("Nome 2");

        dto.setNome("Nome 3");
        assertThat(dto.getNome()).isEqualTo("Nome 3");
    }

    @Test
    @DisplayName("Deve criar DTO com builder parcial (alguns campos)")
    void deveCriarDTOComBuilderParcial() {
        dto = ClienteRequestDTO.builder()
                .nome("Ana Silva")
                .email("ana@email.com")
                .build();

        assertThat(dto.getNome()).isEqualTo("Ana Silva");
        assertThat(dto.getEmail()).isEqualTo("ana@email.com");
        assertThat(dto.getCpf()).isNull();
        assertThat(dto.getSenha()).isNull();
    }

    @Test
    @DisplayName("Deve aceitar valores nulos em setters")
    void deveAceitarValoresNulosEmSetters() {
        dto = ClienteRequestDTO.builder()
                .nome("Teste")
                .email("teste@email.com")
                .build();

        assertThatCode(() -> {
            dto.setNome(null);
            dto.setEmail(null);
            dto.setCpf(null);
            dto.setSenha(null);
            dto.setTelefone(null);
            dto.setDtNascimento(null);
            dto.setEndereco(null);
        }).doesNotThrowAnyException();

        assertThat(dto.getNome()).isNull();
        assertThat(dto.getEmail()).isNull();
        assertThat(dto.getEndereco()).isNull();
    }

    @Test
    @DisplayName("Deve criar DTO e verificar independência de instâncias")
    void deveCriarDTOEVerificarIndependenciaDeInstancias() {
        ClienteRequestDTO dto1 = new ClienteRequestDTO();
        dto1.setNome("João");

        ClienteRequestDTO dto2 = new ClienteRequestDTO();
        dto2.setNome("Maria");

        assertThat(dto1.getNome()).isEqualTo("João");
        assertThat(dto2.getNome()).isEqualTo("Maria");
    }

    @Test
    @DisplayName("Deve construir DTO com dados completos via builder")
    void deveConstruirDTOComDadosCompletosViaBuilder() {
        LocalDate dataNascimento = LocalDate.of(1985, 12, 25);

        dto = ClienteRequestDTO.builder()
                .nome("Pedro Oliveira")
                .cpf("55588899900")
                .email("pedro@email.com")
                .senha("PedroSenha@789")
                .telefone("83986665555")
                .dtNascimento(dataNascimento)
                .endereco(endereco)
                .build();

        assertThat(dto.getNome()).isEqualTo("Pedro Oliveira");
        assertThat(dto.getCpf()).isEqualTo("55588899900");
        assertThat(dto.getEmail()).isEqualTo("pedro@email.com");
        assertThat(dto.getSenha()).isEqualTo("PedroSenha@789");
        assertThat(dto.getTelefone()).isEqualTo("83986665555");
        assertThat(dto.getDtNascimento()).isEqualTo(dataNascimento);
        assertThat(dto.getEndereco()).isEqualTo(endereco);
    }

    @Test
    @DisplayName("Deve aceitar datas nulas sem erros")
    void deveAceitarDatasNulasSemErros() {
        dto = new ClienteRequestDTO();
        assertThatCode(() -> dto.setDtNascimento(null))
                .doesNotThrowAnyException();

        assertThat(dto.getDtNascimento()).isNull();
    }

    @Test
    @DisplayName("Deve permitir mudar de um endereço para outro")
    void devePermitirMudarDeUmEnderecParaOutro() {
        EnderecoRequestDTO endereco2 = EnderecoRequestDTO.builder()
                .cep("58001-000")
                .logradouro("Avenida Getúlio Vargas")
                .bairro("Tambiá")
                .cidade("João Pessoa")
                .uf("PB")
                .tipoEndereco(TipoEndereco.COMERCIAL)
                .numero("456")
                .build();

        dto = new ClienteRequestDTO();
        dto.setEndereco(endereco);
        assertThat(dto.getEndereco()).isEqualTo(endereco);

        dto.setEndereco(endereco2);
        assertThat(dto.getEndereco()).isEqualTo(endereco2);
    }

}
