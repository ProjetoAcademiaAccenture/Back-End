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

    // ------------------------------------------------------------------ helper

    private ClienteRequestDTO dtoPadraoValido() {
        return ClienteRequestDTO.builder()
                .nome("Maria Silva")
                .cpf("12345678900")
                .email("maria@email.com")
                .senha("Senha@123")
                .telefone("83999990000")
                .dtNascimento(LocalDate.of(1990, 5, 20))
                .endereco(endereco)
                .build();
    }

    // ------------------------------------------------------------------ construtores

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
    void deveConstruirComConstrutorVazio() {
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

    // ------------------------------------------------------------------ getters e setters

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
    void deveAtualizarCamposMultiplasVezes() {
        dto = new ClienteRequestDTO();

        dto.setNome("Nome 1");
        assertThat(dto.getNome()).isEqualTo("Nome 1");

        dto.setNome("Nome 2");
        assertThat(dto.getNome()).isEqualTo("Nome 2");

        dto.setNome("Nome 3");
        assertThat(dto.getNome()).isEqualTo("Nome 3");
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
    @DisplayName("Deve aceitar datas nulas sem erros")
    void deveAceitarDatasNulasSemErros() {
        dto = new ClienteRequestDTO();
        assertThatCode(() -> dto.setDtNascimento(null))
                .doesNotThrowAnyException();

        assertThat(dto.getDtNascimento()).isNull();
    }

    @Test
    @DisplayName("Deve permitir mudar de um endereço para outro")
    void devePermitirMudarDeUmEnderecoParaOutro() {
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

    // ------------------------------------------------------------------ independência de instâncias

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

    // ------------------------------------------------------------------ equals

    @Test
    @DisplayName("equals deve retornar true para DTOs com os mesmos dados")
    void equals_deveRetornarTrue_quandoMesmosDados() {
        ClienteRequestDTO dto1 = dtoPadraoValido();
        ClienteRequestDTO dto2 = dtoPadraoValido();

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar true para a mesma instância")
    void equals_deveRetornarTrue_quandoMesmaInstancia() {
        ClienteRequestDTO dto1 = dtoPadraoValido();

        assertThat(dto1).isEqualTo(dto1);
    }

    @Test
    @DisplayName("equals deve retornar false quando nome difere")
    void equals_deveRetornarFalse_quandoNomeDifere() {
        ClienteRequestDTO dto1 = dtoPadraoValido();
        ClienteRequestDTO dto2 = dtoPadraoValido();
        dto2.setNome("Outro Nome");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar false quando cpf difere")
    void equals_deveRetornarFalse_quandoCpfDifere() {
        ClienteRequestDTO dto1 = dtoPadraoValido();
        ClienteRequestDTO dto2 = dtoPadraoValido();
        dto2.setCpf("00000000000");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar false quando email difere")
    void equals_deveRetornarFalse_quandoEmailDifere() {
        ClienteRequestDTO dto1 = dtoPadraoValido();
        ClienteRequestDTO dto2 = dtoPadraoValido();
        dto2.setEmail("outro@email.com");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar false quando senha difere")
    void equals_deveRetornarFalse_quandoSenhaDifere() {
        ClienteRequestDTO dto1 = dtoPadraoValido();
        ClienteRequestDTO dto2 = dtoPadraoValido();
        dto2.setSenha("OutraSenha@999");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar false quando telefone difere")
    void equals_deveRetornarFalse_quandoTelefoneDifere() {
        ClienteRequestDTO dto1 = dtoPadraoValido();
        ClienteRequestDTO dto2 = dtoPadraoValido();
        dto2.setTelefone("83000000000");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar false quando dtNascimento difere")
    void equals_deveRetornarFalse_quandoDtNascimentoDifere() {
        ClienteRequestDTO dto1 = dtoPadraoValido();
        ClienteRequestDTO dto2 = dtoPadraoValido();
        dto2.setDtNascimento(LocalDate.of(2000, 1, 1));

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar false quando endereço difere")
    void equals_deveRetornarFalse_quandoEnderecoDifere() {
        ClienteRequestDTO dto1 = dtoPadraoValido();
        ClienteRequestDTO dto2 = dtoPadraoValido();
        dto2.setEndereco(EnderecoRequestDTO.builder()
                .cep("00000-000")
                .logradouro("Outra Rua")
                .bairro("Outro Bairro")
                .cidade("Outra Cidade")
                .uf("SP")
                .tipoEndereco(TipoEndereco.COMERCIAL)
                .numero("999")
                .build());

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar false para null")
    void equals_deveRetornarFalse_quandoNull() {
        ClienteRequestDTO dto1 = dtoPadraoValido();

        assertThat(dto1).isNotEqualTo(null);
    }

    @Test
    @DisplayName("equals deve retornar false para objeto de tipo diferente")
    void equals_deveRetornarFalse_quandoTipoDiferente() {
        ClienteRequestDTO dto1 = dtoPadraoValido();

        assertThat(dto1).isNotEqualTo("uma string qualquer");
        assertThat(dto1).isNotEqualTo(42);
    }

    @Test
    @DisplayName("equals deve ser simétrico")
    void equals_deveSersimetrico() {
        ClienteRequestDTO dto1 = dtoPadraoValido();
        ClienteRequestDTO dto2 = dtoPadraoValido();

        assertThat(dto1.equals(dto2)).isEqualTo(dto2.equals(dto1));
    }

    @Test
    @DisplayName("equals deve funcionar quando campos são nulos em ambos")
    void equals_deveFuncionar_quandoCamposNulosEmAmbos() {
        ClienteRequestDTO dto1 = new ClienteRequestDTO();
        ClienteRequestDTO dto2 = new ClienteRequestDTO();

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar false quando um campo é nulo e o outro não")
    void equals_deveRetornarFalse_quandoUmCampoNuloEOutroNao() {
        ClienteRequestDTO dto1 = new ClienteRequestDTO();
        dto1.setNome(null);

        ClienteRequestDTO dto2 = new ClienteRequestDTO();
        dto2.setNome("Maria");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    // ------------------------------------------------------------------ hashCode

    @Test
    @DisplayName("hashCode deve ser igual para DTOs com os mesmos dados")
    void hashCode_deveSerIgual_quandoMesmosDados() {
        ClienteRequestDTO dto1 = dtoPadraoValido();
        ClienteRequestDTO dto2 = dtoPadraoValido();

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("hashCode deve ser consistente em múltiplas chamadas")
    void hashCode_deveSerConsistente() {
        ClienteRequestDTO dto1 = dtoPadraoValido();

        assertThat(dto1.hashCode()).isEqualTo(dto1.hashCode());
    }

    @Test
    @DisplayName("hashCode deve funcionar com todos os campos nulos")
    void hashCode_deveNaoLancarExcecao_quandoCamposNulos() {
        dto = new ClienteRequestDTO();

        assertThatCode(dto::hashCode).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("hashCode deve diferir quando dados diferem")
    void hashCode_deveDiferir_quandoDadosDiferem() {
        ClienteRequestDTO dto1 = dtoPadraoValido();
        ClienteRequestDTO dto2 = dtoPadraoValido();
        dto2.setNome("Nome Completamente Diferente");

        // não é garantido por contrato, mas é esperado para implementações sãs
        assertThat(dto1.hashCode()).isNotEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("hashCode deve funcionar com apenas alguns campos preenchidos")
    void hashCode_deveFuncionar_quandoApenasAlgunsCamposPreenchidos() {
        dto = ClienteRequestDTO.builder()
                .nome("Parcial")
                .email("parcial@email.com")
                .build();

        assertThatCode(dto::hashCode).doesNotThrowAnyException();
    }

    // ------------------------------------------------------------------ toString

    @Test
    @DisplayName("toString não deve lançar exceção")
    void toString_naoDeveLancarExcecao() {
        dto = dtoPadraoValido();

        assertThatCode(dto::toString).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("toString deve conter os campos principais")
    void toString_deveConterCamposPrincipais() {
        dto = dtoPadraoValido();
        String result = dto.toString();

        assertThat(result).contains("Maria Silva");
        assertThat(result).contains("maria@email.com");
        assertThat(result).contains("12345678900");
    }

    @Test
    @DisplayName("toString não deve lançar exceção com campos nulos")
    void toString_naoDeveLancarExcecao_quandoCamposNulos() {
        dto = new ClienteRequestDTO();

        assertThatCode(dto::toString).doesNotThrowAnyException();
    }

    // ------------------------------------------------------------------ canEqual

    @Test
    @DisplayName("canEqual deve retornar true para instância do mesmo tipo")
    void canEqual_deveRetornarTrue_paraMesmoTipo() {
        ClienteRequestDTO dto1 = dtoPadraoValido();
        ClienteRequestDTO dto2 = new ClienteRequestDTO();

        assertThat(dto1.canEqual(dto2)).isTrue();
    }

    @Test
    @DisplayName("canEqual deve retornar false para tipo diferente")
    void canEqual_deveRetornarFalse_paraTipoDiferente() {
        ClienteRequestDTO dto1 = dtoPadraoValido();

        assertThat(dto1.canEqual("string")).isFalse();
        assertThat(dto1.canEqual(42)).isFalse();
        assertThat(dto1.canEqual(null)).isFalse();
    }
}