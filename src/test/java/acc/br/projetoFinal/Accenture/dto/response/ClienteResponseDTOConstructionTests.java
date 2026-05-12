package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.enums.TipoEndereco;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.Endereco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("ClienteResponseDTO — Testes de Construção e Métodos")
class ClienteResponseDTOConstructionTests {

    private ClienteResponseDTO dto;
    private List<EnderecoResponseDTO> enderecosResponse;
    private Cliente clientePadrao;

    @BeforeEach
    void setUp() {
        Endereco endereco = Endereco.builder()
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

        clientePadrao = Cliente.builder()
                .id(1L)
                .nome("Maria Silva")
                .cpf("12345678900")
                .email("maria@email.com")
                .telefone("83999990000")
                .dataNascimento(LocalDate.of(1990, 5, 20))
                .enderecos(List.of(endereco))
                .build();

        enderecosResponse = List.of(EnderecoResponseDTO.fromEntity(endereco));
    }

    // ------------------------------------------------------------------ helper

    private ClienteResponseDTO dtoPadraoValido() {
        return ClienteResponseDTO.builder()
                .id(1L)
                .nome("Maria Silva")
                .cpf("12345678900")
                .email("maria@email.com")
                .telefone("83999990000")
                .dataNascimento(LocalDate.of(1990, 5, 20))
                .enderecos(enderecosResponse)
                .build();
    }

    // ------------------------------------------------------------------ construtores

    @Test
    @DisplayName("Deve construir DTO com construtor vazio")
    void deveConstruirComConstrutorVazio() {
        dto = new ClienteResponseDTO();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getNome()).isNull();
        assertThat(dto.getCpf()).isNull();
        assertThat(dto.getEmail()).isNull();
        assertThat(dto.getTelefone()).isNull();
        assertThat(dto.getDataNascimento()).isNull();
        assertThat(dto.getEnderecos()).isNull();
    }

    @Test
    @DisplayName("Deve construir DTO com construtor all-args")
    void deveConstruirComConstrutorAllArgs() {
        dto = new ClienteResponseDTO(
                2L, "Pedro Costa", "55566677788", "pedro@email.com",
                "83986660000", LocalDate.of(1992, 11, 22), enderecosResponse
        );

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getNome()).isEqualTo("Pedro Costa");
        assertThat(dto.getCpf()).isEqualTo("55566677788");
        assertThat(dto.getEmail()).isEqualTo("pedro@email.com");
        assertThat(dto.getTelefone()).isEqualTo("83986660000");
        assertThat(dto.getDataNascimento()).isEqualTo(LocalDate.of(1992, 11, 22));
        assertThat(dto.getEnderecos()).isEqualTo(enderecosResponse);
    }

    @Test
    @DisplayName("Deve construir via builder com todos os campos")
    void deveConstruirViaBuilderComTodosCampos() {
        dto = dtoPadraoValido();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getNome()).isEqualTo("Maria Silva");
        assertThat(dto.getCpf()).isEqualTo("12345678900");
        assertThat(dto.getEmail()).isEqualTo("maria@email.com");
        assertThat(dto.getTelefone()).isEqualTo("83999990000");
        assertThat(dto.getDataNascimento()).isEqualTo(LocalDate.of(1990, 5, 20));
        assertThat(dto.getEnderecos()).isEqualTo(enderecosResponse);
    }

    @Test
    @DisplayName("Deve construir via builder parcial")
    void deveConstruirViaBuilderParcial() {
        dto = ClienteResponseDTO.builder()
                .nome("Ana")
                .email("ana@email.com")
                .build();

        assertThat(dto.getNome()).isEqualTo("Ana");
        assertThat(dto.getEmail()).isEqualTo("ana@email.com");
        assertThat(dto.getId()).isNull();
        assertThat(dto.getCpf()).isNull();
    }

    // ------------------------------------------------------------------ fromEntity

    @Test
    @DisplayName("Deve converter Cliente para DTO corretamente")
    void deveConverterClienteParaDTO() {
        dto = ClienteResponseDTO.fromEntity(clientePadrao);

        assertThat(dto.getId()).isEqualTo(clientePadrao.getId());
        assertThat(dto.getNome()).isEqualTo(clientePadrao.getNome());
        assertThat(dto.getCpf()).isEqualTo(clientePadrao.getCpf());
        assertThat(dto.getEmail()).isEqualTo(clientePadrao.getEmail());
        assertThat(dto.getTelefone()).isEqualTo(clientePadrao.getTelefone());
        assertThat(dto.getDataNascimento()).isEqualTo(clientePadrao.getDataNascimento());
        assertThat(dto.getEnderecos()).hasSize(1);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando enderecos é null no fromEntity")
    void deveRetornarListaVazia_quandoEnderecosNull() {
        clientePadrao.setEnderecos(null);
        dto = ClienteResponseDTO.fromEntity(clientePadrao);

        assertThat(dto.getEnderecos()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando enderecos está vazio no fromEntity")
    void deveRetornarListaVazia_quandoEnderecosVazio() {
        clientePadrao.setEnderecos(List.of());
        dto = ClienteResponseDTO.fromEntity(clientePadrao);

        assertThat(dto.getEnderecos()).isNotNull().isEmpty();
    }

    // ------------------------------------------------------------------ getters e setters

    @Test
    @DisplayName("Deve settar e gettar id")
    void deveSettarEGettarId() {
        dto = new ClienteResponseDTO();
        dto.setId(99L);

        assertThat(dto.getId()).isEqualTo(99L);
    }

    @Test
    @DisplayName("Deve settar e gettar nome")
    void deveSettarEGettarNome() {
        dto = new ClienteResponseDTO();
        dto.setNome("Carlos Eduardo");

        assertThat(dto.getNome()).isEqualTo("Carlos Eduardo");
    }

    @Test
    @DisplayName("Deve settar e gettar cpf")
    void deveSettarEGettarCpf() {
        dto = new ClienteResponseDTO();
        dto.setCpf("11122233344");

        assertThat(dto.getCpf()).isEqualTo("11122233344");
    }

    @Test
    @DisplayName("Deve settar e gettar email")
    void deveSettarEGettarEmail() {
        dto = new ClienteResponseDTO();
        dto.setEmail("teste@email.com");

        assertThat(dto.getEmail()).isEqualTo("teste@email.com");
    }

    @Test
    @DisplayName("Deve settar e gettar telefone")
    void deveSettarEGettarTelefone() {
        dto = new ClienteResponseDTO();
        dto.setTelefone("83987654321");

        assertThat(dto.getTelefone()).isEqualTo("83987654321");
    }

    @Test
    @DisplayName("Deve settar e gettar dataNascimento")
    void deveSettarEGettarDataNascimento() {
        dto = new ClienteResponseDTO();
        LocalDate data = LocalDate.of(1992, 7, 10);
        dto.setDataNascimento(data);

        assertThat(dto.getDataNascimento()).isEqualTo(data);
    }

    @Test
    @DisplayName("Deve settar e gettar enderecos")
    void deveSettarEGettarEnderecos() {
        dto = new ClienteResponseDTO();
        dto.setEnderecos(enderecosResponse);

        assertThat(dto.getEnderecos()).isEqualTo(enderecosResponse);
    }

    @Test
    @DisplayName("Deve aceitar valores nulos em todos os setters")
    void deveAceitarValoresNulosEmSetters() {
        dto = dtoPadraoValido();

        assertThatCode(() -> {
            dto.setId(null);
            dto.setNome(null);
            dto.setCpf(null);
            dto.setEmail(null);
            dto.setTelefone(null);
            dto.setDataNascimento(null);
            dto.setEnderecos(null);
        }).doesNotThrowAnyException();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getNome()).isNull();
        assertThat(dto.getEnderecos()).isNull();
    }

    @Test
    @DisplayName("Deve atualizar campos múltiplas vezes")
    void deveAtualizarCamposMultiplasVezes() {
        dto = new ClienteResponseDTO();

        dto.setNome("Nome 1");
        assertThat(dto.getNome()).isEqualTo("Nome 1");

        dto.setNome("Nome 2");
        assertThat(dto.getNome()).isEqualTo("Nome 2");

        dto.setNome("Nome 3");
        assertThat(dto.getNome()).isEqualTo("Nome 3");
    }

    @Test
    @DisplayName("Deve verificar independência entre instâncias")
    void deveVerificarIndependenciaEntreInstancias() {
        ClienteResponseDTO dto1 = new ClienteResponseDTO();
        dto1.setNome("João");

        ClienteResponseDTO dto2 = new ClienteResponseDTO();
        dto2.setNome("Maria");

        assertThat(dto1.getNome()).isEqualTo("João");
        assertThat(dto2.getNome()).isEqualTo("Maria");
    }

    // ------------------------------------------------------------------ equals

    @Test
    @DisplayName("equals deve retornar true para DTOs com os mesmos dados")
    void equals_deveRetornarTrue_quandoMesmosDados() {
        ClienteResponseDTO dto1 = dtoPadraoValido();
        ClienteResponseDTO dto2 = dtoPadraoValido();

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar true para a mesma instância")
    void equals_deveRetornarTrue_quandoMesmaInstancia() {
        ClienteResponseDTO dto1 = dtoPadraoValido();

        assertThat(dto1).isEqualTo(dto1);
    }

    @Test
    @DisplayName("equals deve ser simétrico")
    void equals_deveSerSimetrico() {
        ClienteResponseDTO dto1 = dtoPadraoValido();
        ClienteResponseDTO dto2 = dtoPadraoValido();

        assertThat(dto1.equals(dto2)).isEqualTo(dto2.equals(dto1));
    }

    @Test
    @DisplayName("equals deve retornar false quando id difere")
    void equals_deveRetornarFalse_quandoIdDifere() {
        ClienteResponseDTO dto1 = dtoPadraoValido();
        ClienteResponseDTO dto2 = dtoPadraoValido();
        dto2.setId(99L);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar false quando nome difere")
    void equals_deveRetornarFalse_quandoNomeDifere() {
        ClienteResponseDTO dto1 = dtoPadraoValido();
        ClienteResponseDTO dto2 = dtoPadraoValido();
        dto2.setNome("Outro Nome");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar false quando cpf difere")
    void equals_deveRetornarFalse_quandoCpfDifere() {
        ClienteResponseDTO dto1 = dtoPadraoValido();
        ClienteResponseDTO dto2 = dtoPadraoValido();
        dto2.setCpf("00000000000");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar false quando email difere")
    void equals_deveRetornarFalse_quandoEmailDifere() {
        ClienteResponseDTO dto1 = dtoPadraoValido();
        ClienteResponseDTO dto2 = dtoPadraoValido();
        dto2.setEmail("outro@email.com");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar false quando telefone difere")
    void equals_deveRetornarFalse_quandoTelefoneDifere() {
        ClienteResponseDTO dto1 = dtoPadraoValido();
        ClienteResponseDTO dto2 = dtoPadraoValido();
        dto2.setTelefone("83000000000");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar false quando dataNascimento difere")
    void equals_deveRetornarFalse_quandoDataNascimentoDifere() {
        ClienteResponseDTO dto1 = dtoPadraoValido();
        ClienteResponseDTO dto2 = dtoPadraoValido();
        dto2.setDataNascimento(LocalDate.of(2000, 1, 1));

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar false quando enderecos difere")
    void equals_deveRetornarFalse_quandoEnderecosDifere() {
        ClienteResponseDTO dto1 = dtoPadraoValido();
        ClienteResponseDTO dto2 = dtoPadraoValido();
        dto2.setEnderecos(List.of());

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar false para null")
    void equals_deveRetornarFalse_quandoNull() {
        ClienteResponseDTO dto1 = dtoPadraoValido();

        assertThat(dto1).isNotEqualTo(null);
    }

    @Test
    @DisplayName("equals deve retornar false para tipo diferente")
    void equals_deveRetornarFalse_quandoTipoDiferente() {
        ClienteResponseDTO dto1 = dtoPadraoValido();

        assertThat(dto1).isNotEqualTo("uma string qualquer");
        assertThat(dto1).isNotEqualTo(42);
    }

    @Test
    @DisplayName("equals deve funcionar quando todos os campos são nulos")
    void equals_deveFuncionar_quandoTodosCamposNulos() {
        ClienteResponseDTO dto1 = new ClienteResponseDTO();
        ClienteResponseDTO dto2 = new ClienteResponseDTO();

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar false quando um campo é nulo e o outro não")
    void equals_deveRetornarFalse_quandoUmCampoNuloEOutroNao() {
        ClienteResponseDTO dto1 = new ClienteResponseDTO();
        dto1.setNome(null);

        ClienteResponseDTO dto2 = new ClienteResponseDTO();
        dto2.setNome("Maria");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    // ------------------------------------------------------------------ hashCode

    @Test
    @DisplayName("hashCode deve ser igual para DTOs com os mesmos dados")
    void hashCode_deveSerIgual_quandoMesmosDados() {
        ClienteResponseDTO dto1 = dtoPadraoValido();
        ClienteResponseDTO dto2 = dtoPadraoValido();

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("hashCode deve ser consistente em múltiplas chamadas")
    void hashCode_deveSerConsistente() {
        ClienteResponseDTO dto1 = dtoPadraoValido();

        assertThat(dto1.hashCode()).isEqualTo(dto1.hashCode());
    }

    @Test
    @DisplayName("hashCode não deve lançar exceção com todos os campos nulos")
    void hashCode_naoDeveLancarExcecao_quandoCamposNulos() {
        dto = new ClienteResponseDTO();

        assertThatCode(dto::hashCode).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("hashCode deve diferir quando dados diferem")
    void hashCode_deveDiferir_quandoDadosDiferem() {
        ClienteResponseDTO dto1 = dtoPadraoValido();
        ClienteResponseDTO dto2 = dtoPadraoValido();
        dto2.setNome("Nome Completamente Diferente");

        assertThat(dto1.hashCode()).isNotEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("hashCode deve funcionar com campos parcialmente preenchidos")
    void hashCode_deveFuncionar_quandoCamposParciais() {
        dto = ClienteResponseDTO.builder()
                .nome("Parcial")
                .email("parcial@email.com")
                .build();

        assertThatCode(dto::hashCode).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("hashCode deve funcionar com id nulo e outros campos preenchidos")
    void hashCode_deveFuncionar_quandoIdNulo() {
        dto = ClienteResponseDTO.builder()
                .nome("Sem ID")
                .cpf("12345678900")
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
        dto = new ClienteResponseDTO();

        assertThatCode(dto::toString).doesNotThrowAnyException();
    }

    // ------------------------------------------------------------------ canEqual

    @Test
    @DisplayName("canEqual deve retornar true para instância do mesmo tipo")
    void canEqual_deveRetornarTrue_paraMesmoTipo() {
        ClienteResponseDTO dto1 = dtoPadraoValido();
        ClienteResponseDTO dto2 = new ClienteResponseDTO();

        assertThat(dto1.canEqual(dto2)).isTrue();
    }

    @Test
    @DisplayName("canEqual deve retornar false para tipo diferente")
    void canEqual_deveRetornarFalse_paraTipoDiferente() {
        ClienteResponseDTO dto1 = dtoPadraoValido();

        assertThat(dto1.canEqual("string")).isFalse();
        assertThat(dto1.canEqual(42)).isFalse();
        assertThat(dto1.canEqual(null)).isFalse();
    }
}