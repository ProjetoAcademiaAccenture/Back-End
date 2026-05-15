package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("ClienteResponseDTO — Testes Negativos")
class ClienteResponseDTONegativeTests {

    private ClienteResponseDTO dto;

    @BeforeEach
    void setUp() {
        dto = new ClienteResponseDTO();
    }

    @Test
    @DisplayName("Deve criar DTO com valores nulos")
    void deveCriarDTOComValoresNulos() {
        ClienteResponseDTO dtoNulo = new ClienteResponseDTO();

        assertThat(dtoNulo.getId()).isNull();
        assertThat(dtoNulo.getNome()).isNull();
        assertThat(dtoNulo.getCpf()).isNull();
        assertThat(dtoNulo.getEmail()).isNull();
        assertThat(dtoNulo.getTelefone()).isNull();
        assertThat(dtoNulo.getDataNascimento()).isNull();
        assertThat(dtoNulo.getEnderecos()).isNull();
    }

    @Test
    @DisplayName("Deve atualizar id sem erros")
    void deveAtualizarIdSemErros() {
        dto.setId(1L);
        dto.setId(2L);
        dto.setId(null);

        assertThat(dto.getId()).isNull();
    }

    @Test
    @DisplayName("Deve lidar com enderecos nulo durante conversão")
    void deveHandlearComEnderecosNuloDuranteConversao() {
        Cliente cliente = Cliente.builder()
                .id(1L)
                .nome("João")
                .cpf("12345678900")
                .email("joao@email.com")
                .telefone("83999990000")
                .dataNascimento(LocalDate.of(1990, 5, 20))
                .enderecos(null)
                .build();

        ClienteResponseDTO resultado = ClienteResponseDTO.fromEntity(cliente);

        assertThat(resultado.getEnderecos()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Deve lidar com enderecos vazio durante conversão")
    void deveHandlearComEnderecosVazioDuranteConversao() {
        Cliente cliente = Cliente.builder()
                .id(1L)
                .nome("João")
                .cpf("12345678900")
                .email("joao@email.com")
                .telefone("83999990000")
                .dataNascimento(LocalDate.of(1990, 5, 20))
                .enderecos(List.of())
                .build();

        ClienteResponseDTO resultado = ClienteResponseDTO.fromEntity(cliente);

        assertThat(resultado.getEnderecos()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Deve atualizar enderecos sem erros mesmo com lista nula")
    void deveAtualizarEnderecosComListaNula() {
        assertThatCode(() -> {
            dto.setEnderecos(null);
            dto.setEnderecos(List.of());
            dto.setEnderecos(null);
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve aceitar valores vazios em campos String")
    void deveAceitarValoresVaziosEmCamposString() {
        assertThatCode(() -> {
            dto.setNome("");
            dto.setCpf("");
            dto.setEmail("");
            dto.setTelefone("");
        }).doesNotThrowAnyException();

        assertThat(dto.getNome()).isEmpty();
        assertThat(dto.getCpf()).isEmpty();
        assertThat(dto.getEmail()).isEmpty();
        assertThat(dto.getTelefone()).isEmpty();
    }

    @Test
    @DisplayName("Deve aceitar datas futuras")
    void deveAceitarDatasFuturas() {
        LocalDate dataFutura = LocalDate.now().plusYears(10);
        dto.setDataNascimento(dataFutura);

        assertThat(dto.getDataNascimento()).isEqualTo(dataFutura);
    }

    @Test
    @DisplayName("Deve aceitar datas antigas")
    void deveAceitarDatasAntigas() {
        LocalDate dataAntiga = LocalDate.of(1900, 1, 1);
        dto.setDataNascimento(dataAntiga);

        assertThat(dto.getDataNascimento()).isEqualTo(dataAntiga);
    }

    @Test
    @DisplayName("Deve retornar não nulo mesmo com cliente nulo na conversão")
    void deveRetornarNaoNuloComClienteValido() {
        Cliente cliente = Cliente.builder()
                .id(null)
                .nome(null)
                .cpf(null)
                .email(null)
                .telefone(null)
                .dataNascimento(null)
                .enderecos(null)
                .build();

        ClienteResponseDTO resultado = ClienteResponseDTO.fromEntity(cliente);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isNull();
        assertThat(resultado.getNome()).isNull();
        assertThat(resultado.getEnderecos()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Deve manter lista de enderecos após múltiplas atualizações")
    void deveMantarListaDeEnderecosAposMultiplasAtualizacoes() {
        List<EnderecoResponseDTO> lista1 = List.of();
        List<EnderecoResponseDTO> lista2 = List.of();

        dto.setEnderecos(lista1);
        assertThat(dto.getEnderecos()).isEmpty();

        dto.setEnderecos(lista2);
        assertThat(dto.getEnderecos()).isEmpty();

        dto.setEnderecos(null);
        assertThat(dto.getEnderecos()).isNull();
    }

    @Test
    @DisplayName("Deve converter Cliente com campos mínimos")
    void deveConverterClienteComCamposMinimos() {
        Cliente cliente = Cliente.builder()
                .id(1L)
                .build();

        ClienteResponseDTO resultado = ClienteResponseDTO.fromEntity(cliente);

        assertThat(resultado)
                .isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getEnderecos()).isEmpty();
    }

    @Test
    @DisplayName("Deve usar ID grande sem problemas")
    void deveUsarIDGrandeSemProblemas() {
        Cliente cliente = Cliente.builder()
                .id(Long.MAX_VALUE)
                .nome("Nome")
                .enderecos(null)
                .build();

        ClienteResponseDTO resultado = ClienteResponseDTO.fromEntity(cliente);

        assertThat(resultado.getId()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    @DisplayName("Deve lidar com strings especiais em campos")
    void deveHandlearComStringsEspeciaisEmCampos() {
        dto.setNome("João@#$%ção");
        dto.setCpf("123-456-789-00");
        dto.setEmail("email+tag@domain.com");
        dto.setTelefone("+55 (83) 99999-0000");

        assertThat(dto.getNome()).contains("@#$%");
        assertThat(dto.getCpf()).contains("-");
        assertThat(dto.getEmail()).contains("+");
        assertThat(dto.getTelefone()).contains("(", ")");
    }

}
