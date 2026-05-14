package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.enums.TipoConta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ContaResponseDTO - Testes Positivos")
class ContaResponseDTOTests {

    private Conta contaPadrao;
    private static final BigDecimal SALDO_PADRAO = new BigDecimal("10000.00");
    private static final BigDecimal LIMITE_PADRAO = new BigDecimal("5000.00");

    @BeforeEach
    void setUp() {
        contaPadrao = new Conta();
        contaPadrao.setId(1L);
        contaPadrao.setNumeroConta("EMPRESA-001");
        contaPadrao.setSaldo(SALDO_PADRAO);
        contaPadrao.setLimiteCreditoDisponivel(LIMITE_PADRAO);
        contaPadrao.setTipo(TipoConta.CORRENTE);
    }

    // ------------------------------------------------------------------ helper

    private ContaResponseDTO dtoPadraoValido() {
        return ContaResponseDTO.builder()
                .id(1L)
                .numeroConta("EMPRESA-001")
                .saldo(SALDO_PADRAO)
                .limiteCreditoDisponivel(LIMITE_PADRAO)
                .tipo("CORRENTE")
                .build();
    }

    // ------------------------------------------------------------------ construtores

    @Test
    @DisplayName("Deve criar ContaResponseDTO com construtor padrão")
    void deveCriarComConstrutorPadrao() {
        ContaResponseDTO dto = new ContaResponseDTO();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getNumeroConta()).isNull();
        assertThat(dto.getSaldo()).isNull();
        assertThat(dto.getLimiteCreditoDisponivel()).isNull();
        assertThat(dto.getTipo()).isNull();
    }

    @Test
    @DisplayName("Deve criar ContaResponseDTO com construtor all-args")
    void deveCriarComConstrutorAllArgs() {
        // Construtor all-args inclui: id, numeroConta, saldo, limiteCreditoDisponivel, tipo
        ContaResponseDTO dto = new ContaResponseDTO(
                1L, "EMPRESA-001", SALDO_PADRAO, LIMITE_PADRAO, "CORRENTE"
        );

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getNumeroConta()).isEqualTo("EMPRESA-001");
        assertThat(dto.getSaldo()).isEqualTo(SALDO_PADRAO);
        assertThat(dto.getLimiteCreditoDisponivel()).isEqualTo(LIMITE_PADRAO);
        assertThat(dto.getTipo()).isEqualTo("CORRENTE");
    }

    @Test
    @DisplayName("Deve criar ContaResponseDTO usando Builder com todos os campos")
    void deveCriarUsandoBuilderComTodosCampos() {
        ContaResponseDTO dto = dtoPadraoValido();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getNumeroConta()).isEqualTo("EMPRESA-001");
        assertThat(dto.getSaldo()).isEqualTo(SALDO_PADRAO);
        assertThat(dto.getLimiteCreditoDisponivel()).isEqualTo(LIMITE_PADRAO);
        assertThat(dto.getTipo()).isEqualTo("CORRENTE");
    }

    @Test
    @DisplayName("Deve criar ContaResponseDTO usando Builder parcial")
    void deveCriarUsandoBuilderParcial() {
        ContaResponseDTO dto = ContaResponseDTO.builder()
                .id(2L)
                .numeroConta("POUPANCA-001")
                .saldo(new BigDecimal("5000.00"))
                .tipo("POUPANCA")
                .build();

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getNumeroConta()).isEqualTo("POUPANCA-001");
        assertThat(dto.getSaldo()).isEqualTo(new BigDecimal("5000.00"));
        assertThat(dto.getLimiteCreditoDisponivel()).isNull();
        assertThat(dto.getTipo()).isEqualTo("POUPANCA");
    }

    // ------------------------------------------------------------------ getters e setters

    @Test
    @DisplayName("Deve atualizar campos via setters")
    void deveAtualizarCamposViaSetters() {
        ContaResponseDTO dto = new ContaResponseDTO();

        dto.setId(5L);
        dto.setNumeroConta("INVESTIMENTO-001");
        dto.setSaldo(new BigDecimal("50000.00"));
        dto.setLimiteCreditoDisponivel(new BigDecimal("2000.00"));
        dto.setTipo("CORRENTE");

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getNumeroConta()).isEqualTo("INVESTIMENTO-001");
        assertThat(dto.getSaldo()).isEqualByComparingTo(new BigDecimal("50000.00"));
        assertThat(dto.getLimiteCreditoDisponivel()).isEqualByComparingTo(new BigDecimal("2000.00"));
        assertThat(dto.getTipo()).isEqualTo("CORRENTE");
    }

    @Test
    @DisplayName("Deve aceitar valores nulos em todos os setters")
    void deveAceitarValoresNulosEmSetters() {
        ContaResponseDTO dto = dtoPadraoValido();

        assertThatCode(() -> {
            dto.setId(null);
            dto.setNumeroConta(null);
            dto.setSaldo(null);
            dto.setLimiteCreditoDisponivel(null);
            dto.setTipo(null);
        }).doesNotThrowAnyException();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getNumeroConta()).isNull();
        assertThat(dto.getSaldo()).isNull();
        assertThat(dto.getLimiteCreditoDisponivel()).isNull();
        assertThat(dto.getTipo()).isNull();
    }

    @Test
    @DisplayName("Deve atualizar campo múltiplas vezes")
    void deveAtualizarCampoMultiplasVezes() {
        ContaResponseDTO dto = new ContaResponseDTO();

        dto.setNumeroConta("NUM-1");
        assertThat(dto.getNumeroConta()).isEqualTo("NUM-1");

        dto.setNumeroConta("NUM-2");
        assertThat(dto.getNumeroConta()).isEqualTo("NUM-2");
    }

    @Test
    @DisplayName("Deve verificar independência entre instâncias")
    void deveVerificarIndependenciaEntreInstancias() {
        ContaResponseDTO dto1 = new ContaResponseDTO();
        dto1.setNumeroConta("CONTA-A");

        ContaResponseDTO dto2 = new ContaResponseDTO();
        dto2.setNumeroConta("CONTA-B");

        assertThat(dto1.getNumeroConta()).isEqualTo("CONTA-A");
        assertThat(dto2.getNumeroConta()).isEqualTo("CONTA-B");
    }

    // ------------------------------------------------------------------ fromEntity

    @Test
    @DisplayName("Deve converter Conta para ContaResponseDTO")
    void deveConverterContaParaDTO() {
        ContaResponseDTO dto = ContaResponseDTO.fromEntity(contaPadrao);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getNumeroConta()).isEqualTo("EMPRESA-001");
        assertThat(dto.getSaldo()).isEqualByComparingTo(SALDO_PADRAO);
        assertThat(dto.getLimiteCreditoDisponivel()).isEqualByComparingTo(LIMITE_PADRAO);
        assertThat(dto.getTipo()).isEqualTo("CORRENTE");
    }

    @Test
    @DisplayName("Deve preservar tipo de conta como string na conversão")
    void devePreservarTipoContaComoString() {
        ContaResponseDTO dto = ContaResponseDTO.fromEntity(contaPadrao);

        assertThat(dto.getTipo()).isNotNull().isInstanceOf(String.class).isEqualTo("CORRENTE");
    }

    @Test
    @DisplayName("Deve converter conta com tipo POUPANCA corretamente")
    void deveConverterContaComTipoPoupanca() {
        contaPadrao.setTipo(TipoConta.POUPANCA);

        ContaResponseDTO dto = ContaResponseDTO.fromEntity(contaPadrao);

        assertThat(dto.getTipo()).isEqualTo("POUPANCA");
    }

    @Test
    @DisplayName("Deve mapear limiteCreditoDisponivel como null quando não informado")
    void deveMapearLimiteNullQuandoNaoInformado() {
        contaPadrao.setLimiteCreditoDisponivel(null);

        ContaResponseDTO dto = ContaResponseDTO.fromEntity(contaPadrao);

        assertThat(dto.getLimiteCreditoDisponivel()).isNull();
    }

    // ------------------------------------------------------------------ equals e hashCode

    @Test
    @DisplayName("equals deve retornar true para DTOs com os mesmos dados")
    void equals_deveRetornarTrue_quandoMesmosDados() {
        ContaResponseDTO dto1 = dtoPadraoValido();
        ContaResponseDTO dto2 = dtoPadraoValido();

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar true para a mesma instância")
    void equals_deveRetornarTrue_quandoMesmaInstancia() {
        ContaResponseDTO dto1 = dtoPadraoValido();

        assertThat(dto1).isEqualTo(dto1);
    }

    @Test
    @DisplayName("equals deve ser simétrico")
    void equals_deveSerSimetrico() {
        ContaResponseDTO dto1 = dtoPadraoValido();
        ContaResponseDTO dto2 = dtoPadraoValido();

        assertThat(dto1.equals(dto2)).isEqualTo(dto2.equals(dto1));
    }

    @Test
    @DisplayName("equals deve retornar false quando id difere")
    void equals_deveRetornarFalse_quandoIdDifere() {
        ContaResponseDTO dto1 = dtoPadraoValido();
        ContaResponseDTO dto2 = dtoPadraoValido();
        dto2.setId(99L);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar false quando numeroConta difere")
    void equals_deveRetornarFalse_quandoNumeroContaDifere() {
        ContaResponseDTO dto1 = dtoPadraoValido();
        ContaResponseDTO dto2 = dtoPadraoValido();
        dto2.setNumeroConta("OUTRO-999");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar false quando saldo difere")
    void equals_deveRetornarFalse_quandoSaldoDifere() {
        ContaResponseDTO dto1 = dtoPadraoValido();
        ContaResponseDTO dto2 = dtoPadraoValido();
        dto2.setSaldo(BigDecimal.ZERO);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar false quando limiteCreditoDisponivel difere")
    void equals_deveRetornarFalse_quandoLimiteDifere() {
        ContaResponseDTO dto1 = dtoPadraoValido();
        ContaResponseDTO dto2 = dtoPadraoValido();
        dto2.setLimiteCreditoDisponivel(BigDecimal.ZERO);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar false quando tipo difere")
    void equals_deveRetornarFalse_quandoTipoDifere() {
        ContaResponseDTO dto1 = dtoPadraoValido();
        ContaResponseDTO dto2 = dtoPadraoValido();
        dto2.setTipo("POUPANCA");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("equals deve retornar false para null")
    void equals_deveRetornarFalse_quandoNull() {
        assertThat(dtoPadraoValido()).isNotEqualTo(null);
    }

    @Test
    @DisplayName("equals deve retornar false para tipo diferente")
    void equals_deveRetornarFalse_quandoTipoObjDiferente() {
        assertThat(dtoPadraoValido()).isNotEqualTo("uma string");
        assertThat(dtoPadraoValido()).isNotEqualTo(42);
    }

    @Test
    @DisplayName("equals deve funcionar quando todos os campos são nulos")
    void equals_deveFuncionar_quandoTodosCamposNulos() {
        ContaResponseDTO dto1 = new ContaResponseDTO();
        ContaResponseDTO dto2 = new ContaResponseDTO();

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("hashCode deve ser igual para DTOs com os mesmos dados")
    void hashCode_deveSerIgual_quandoMesmosDados() {
        ContaResponseDTO dto1 = dtoPadraoValido();
        ContaResponseDTO dto2 = dtoPadraoValido();

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("hashCode deve ser consistente em múltiplas chamadas")
    void hashCode_deveSerConsistente() {
        ContaResponseDTO dto = dtoPadraoValido();

        assertThat(dto.hashCode()).isEqualTo(dto.hashCode());
    }

    @Test
    @DisplayName("hashCode não deve lançar exceção com campos nulos")
    void hashCode_naoDeveLancarExcecao_quandoCamposNulos() {
        ContaResponseDTO dto = new ContaResponseDTO();

        assertThatCode(dto::hashCode).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("hashCode deve diferir quando dados diferem")
    void hashCode_deveDiferir_quandoDadosDiferem() {
        ContaResponseDTO dto1 = dtoPadraoValido();
        ContaResponseDTO dto2 = dtoPadraoValido();
        dto2.setNumeroConta("CONTA-DIFERENTE-999");

        assertThat(dto1.hashCode()).isNotEqualTo(dto2.hashCode());
    }

    // ------------------------------------------------------------------ toString

    @Test
    @DisplayName("toString não deve lançar exceção")
    void toString_naoDeveLancarExcecao() {
        assertThatCode(() -> dtoPadraoValido().toString()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("toString deve conter os campos principais")
    void toString_deveConterCamposPrincipais() {
        String result = dtoPadraoValido().toString();

        assertThat(result).contains("EMPRESA-001");
        assertThat(result).contains("CORRENTE");
    }

    @Test
    @DisplayName("toString não deve lançar exceção com campos nulos")
    void toString_naoDeveLancarExcecao_quandoCamposNulos() {
        assertThatCode(() -> new ContaResponseDTO().toString()).doesNotThrowAnyException();
    }

    // ------------------------------------------------------------------ canEqual

    @Test
    @DisplayName("canEqual deve retornar true para instância do mesmo tipo")
    void canEqual_deveRetornarTrue_paraMesmoTipo() {
        ContaResponseDTO dto1 = dtoPadraoValido();
        ContaResponseDTO dto2 = new ContaResponseDTO();

        assertThat(dto1.canEqual(dto2)).isTrue();
    }

    @Test
    @DisplayName("canEqual deve retornar false para tipo diferente")
    void canEqual_deveRetornarFalse_paraTipoDiferente() {
        ContaResponseDTO dto = dtoPadraoValido();

        assertThat(dto.canEqual("string")).isFalse();
        assertThat(dto.canEqual(42)).isFalse();
        assertThat(dto.canEqual(null)).isFalse();
    }

    // ------------------------------------------------------------------ cenários de negócio

    @Test
    @DisplayName("Deve suportar saldos muito grandes")
    void deveSuportarSaldosMuitoGrandes() {
        BigDecimal saldoGrande = new BigDecimal("999999999.99");

        ContaResponseDTO dto = ContaResponseDTO.builder()
                .id(8L)
                .numeroConta("MILIONARIA-001")
                .saldo(saldoGrande)
                .tipo("CORRENTE")
                .build();

        assertThat(dto.getSaldo()).isEqualByComparingTo(saldoGrande);
    }

    @Test
    @DisplayName("Deve suportar saldo zero")
    void deveSuportarSaldoZero() {
        ContaResponseDTO dto = ContaResponseDTO.builder()
                .id(9L)
                .numeroConta("ZERADA-001")
                .saldo(BigDecimal.ZERO)
                .tipo("POUPANCA")
                .build();

        assertThat(dto.getSaldo()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}