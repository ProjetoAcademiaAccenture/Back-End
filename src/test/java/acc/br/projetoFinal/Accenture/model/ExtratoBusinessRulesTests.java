package acc.br.projetoFinal.Accenture.model;

import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import acc.br.projetoFinal.Accenture.enums.TipoExtrato;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Cobertura Total - Extrato Model")
class ExtratoBusinessRulesTests {

    private Extrato extrato;
    private Conta conta;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        conta = Conta.builder()
                .id(1L)
                .build();

        pedido = Pedido.builder()
                .id(1L)
                .status(StatusPedido.RESERVADO)
                .build();

        extrato = Extrato.builder()
                .id(1L)
                .conta(conta)
                .tipo(TipoExtrato.CREDITO)
                .valor(new BigDecimal("500.00"))
                .saldoAntes(new BigDecimal("1000.00"))
                .saldoDepois(new BigDecimal("1500.00"))
                .descricao("Crédito inicial")
                .pedido(pedido)
                .build();
    }

    // =========================================================
    // TESTES ESTRUTURAIS: NoArgsConstructor, AllArgsConstructor
    // =========================================================

    @Test
    @DisplayName("NoArgsConstructor: deve criar instância vazia sem exceção")
    void noArgsConstructor_DeveCriarInstanciaVazia() {
        Extrato e = new Extrato();
        assertNotNull(e);
    }

    @Test
    @DisplayName("AllArgsConstructor: deve criar instância com todos os campos")
    void allArgsConstructor_DeveCriarInstanciaCompleta() {
        LocalDateTime agora = LocalDateTime.now();
        Extrato e = new Extrato(
                2L,
                conta,
                TipoExtrato.DEBITO,
                new BigDecimal("200.00"),
                new BigDecimal("800.00"),
                new BigDecimal("600.00"),
                "Débito em conta",
                pedido,
                agora
        );

        assertAll("AllArgsConstructor",
                () -> assertEquals(2L,                        e.getId()),
                () -> assertEquals(conta,                     e.getConta()),
                () -> assertEquals(TipoExtrato.DEBITO,         e.getTipo()),
                () -> assertEquals(new BigDecimal("200.00"),  e.getValor()),
                () -> assertEquals(new BigDecimal("800.00"),  e.getSaldoAntes()),
                () -> assertEquals(new BigDecimal("600.00"),  e.getSaldoDepois()),
                () -> assertEquals("Débito em conta",               e.getDescricao()),
                () -> assertEquals(pedido,                    e.getPedido()),
                () -> assertEquals(agora,                     e.getDataHora())
        );
    }

    // =========================================================
    // TESTES: Builder campo a campo + @Builder.Default
    // =========================================================

    @Test
    @DisplayName("Builder: deve construir Extrato com todos os campos explícitos")
    void builder_DeveConstruirComTodosOsCampos() {
        LocalDateTime dataHora = LocalDateTime.of(2026, 1, 15, 10, 30);
        Extrato e = Extrato.builder()
                .id(5L)
                .conta(conta)
                .tipo(TipoExtrato.ESTORNO)
                .valor(new BigDecimal("300.00"))
                .saldoAntes(new BigDecimal("1000.00"))
                .saldoDepois(new BigDecimal("700.00"))
                .descricao("Estorno de pagamento")
                .pedido(pedido)
                .dataHora(dataHora)
                .build();

        assertAll("Builder campos",
                () -> assertEquals(5L,                               e.getId()),
                () -> assertEquals(conta,                            e.getConta()),
                () -> assertEquals(TipoExtrato.ESTORNO, e.getTipo()),
                () -> assertEquals(new BigDecimal("300.00"),         e.getValor()),
                () -> assertEquals(new BigDecimal("1000.00"),        e.getSaldoAntes()),
                () -> assertEquals(new BigDecimal("700.00"),         e.getSaldoDepois()),
                () -> assertEquals("Estorno de pagamento",          e.getDescricao()),
                () -> assertEquals(pedido,                           e.getPedido()),
                () -> assertEquals(dataHora,                         e.getDataHora())
        );
    }

    @Test
    @DisplayName("Builder.Default: dataHora deve ser preenchida automaticamente quando não informada")
    void builder_Default_DataHoraDeveSerPreenchidaAutomaticamente() {
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        Extrato e = Extrato.builder()
                .id(1L)
                .tipo(TipoExtrato.CREDITO)
                .valor(new BigDecimal("100.00"))
                .saldoAntes(BigDecimal.ZERO)
                .saldoDepois(new BigDecimal("100.00"))
                .build();
        LocalDateTime depois = LocalDateTime.now().plusSeconds(1);

        assertNotNull(e.getDataHora());
        assertTrue(e.getDataHora().isAfter(antes) && e.getDataHora().isBefore(depois),
                "dataHora deve estar entre 'antes' e 'depois'");
    }

    @Test
    @DisplayName("Builder: extrato sem pedido (pedido opcional) deve funcionar")
    void builder_SemPedido_DeveFuncionar() {
        Extrato e = Extrato.builder()
                .id(3L)
                .conta(conta)
                .tipo(TipoExtrato.CREDITO)
                .valor(new BigDecimal("50.00"))
                .saldoAntes(BigDecimal.ZERO)
                .saldoDepois(new BigDecimal("50.00"))
                .build();

        assertNull(e.getPedido());
    }

    @Test
    @DisplayName("Builder: extrato sem descricao (campo opcional) deve funcionar")
    void builder_SemDescricao_DeveFuncionar() {
        Extrato e = Extrato.builder()
                .tipo(TipoExtrato.DEBITO)
                .valor(new BigDecimal("100.00"))
                .saldoAntes(new BigDecimal("500.00"))
                .saldoDepois(new BigDecimal("400.00"))
                .build();

        assertNull(e.getDescricao());
    }

    // =========================================================
    // TESTES: Getters e Setters — cada campo individualmente
    // =========================================================

    @Test
    @DisplayName("Setters/Getters: deve atribuir e retornar cada campo corretamente")
    void settersGetters_DeveAtribuirERetornarCampos() {
        Extrato e = new Extrato();
        LocalDateTime dataHora = LocalDateTime.of(2026, 5, 10, 8, 0);

        e.setId(10L);
        e.setConta(conta);
        e.setTipo(TipoExtrato.MULTA);
        e.setValor(new BigDecimal("750.50"));
        e.setSaldoAntes(new BigDecimal("2000.00"));
        e.setSaldoDepois(new BigDecimal("1249.50"));
        e.setDescricao("Multa de cancelamento");
        e.setPedido(pedido);
        e.setDataHora(dataHora);

        assertAll("setters e getters",
                () -> assertEquals(10L,                          e.getId()),
                () -> assertEquals(conta,                        e.getConta()),
                () -> assertEquals(TipoExtrato.MULTA, e.getTipo()),
                () -> assertEquals(new BigDecimal("750.50"),     e.getValor()),
                () -> assertEquals(new BigDecimal("2000.00"),    e.getSaldoAntes()),
                () -> assertEquals(new BigDecimal("1249.50"),    e.getSaldoDepois()),
                () -> assertEquals("Multa de cancelamento",        e.getDescricao()),
                () -> assertEquals(pedido,                       e.getPedido()),
                () -> assertEquals(dataHora,                     e.getDataHora())
        );
    }

    // =========================================================
    // TESTES: Todos os valores de TipoExtrato
    // Cobre cada branch do enum no modelo
    // =========================================================

    @Test
    @DisplayName("TipoExtrato: deve aceitar CREDITO")
    void tipoExtrato_Credito_DeveSerAtribuido() {
        extrato.setTipo(TipoExtrato.CREDITO);
        assertEquals(TipoExtrato.CREDITO, extrato.getTipo());
    }

    @Test
    @DisplayName("TipoExtrato: deve aceitar DEBITO")
    void tipoExtrato_Debito_DeveSerAtribuido() {
        extrato.setTipo(TipoExtrato.DEBITO);
        assertEquals(TipoExtrato.DEBITO, extrato.getTipo());
    }

    @Test
    @DisplayName("TipoExtrato: deve aceitar ESTORNO")
    void tipoExtrato_Estorno_DeveSerAtribuido() {
        extrato.setTipo(TipoExtrato.ESTORNO);
        assertEquals(TipoExtrato.ESTORNO, extrato.getTipo());
    }

    @Test
    @DisplayName("TipoExtrato: deve aceitar MULTA")
    void tipoExtrato_Multa_DeveSerAtribuido() {
        extrato.setTipo(TipoExtrato.MULTA);
        assertEquals(TipoExtrato.MULTA, extrato.getTipo());
    }

    // =========================================================
    // TESTES: equals() — todos os branches do @Data
    // =========================================================

    /** Cria Extrato idêntico ao setUp() para comparação. */
    private Extrato criarExtratoIdentico() {
        return Extrato.builder()
                .id(1L)
                .conta(conta)
                .tipo(TipoExtrato.CREDITO)
                .valor(new BigDecimal("500.00"))
                .saldoAntes(new BigDecimal("1000.00"))
                .saldoDepois(new BigDecimal("1500.00"))
                .descricao("Crédito inicial")
                .pedido(pedido)
                .dataHora(extrato.getDataHora())
                .build();
    }

    @Test
    @DisplayName("equals: mesma instância deve ser igual (reflexividade)")
    void equals_MesmaInstancia_DeveSerIgual() {
        assertEquals(extrato, extrato);
    }

    @Test
    @DisplayName("equals: dois extratos com todos os campos iguais devem ser iguais")
    void equals_TodosCamposIguais_DeveSerIgual() {
        assertEquals(extrato, criarExtratoIdentico());
    }

    @Test
    @DisplayName("equals: comparação com null deve retornar false")
    void equals_ComNull_DeveRetornarFalse() {
        assertNotEquals(null, extrato);
    }

    @Test
    @DisplayName("equals: comparação com tipo diferente deve retornar false")
    void equals_TipoDiferente_DeveRetornarFalse() {
        assertNotEquals(extrato, new Object());
    }

    @Test
    @DisplayName("equals: dois extratos com todos os campos nulos exceto dataHora igual devem ser iguais")
    void equals_AmbosVazios_DeveSerIgual() {
        // dataHora está no equals gerado pelo @Data, por isso usamos
        // AllArgsConstructor fixando o mesmo LocalDateTime nos dois objetos.
        LocalDateTime dataFixa = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
        Extrato e1 = new Extrato(null, null, null, null, null, null, null, null, dataFixa);
        Extrato e2 = new Extrato(null, null, null, null, null, null, null, null, dataFixa);
        assertEquals(e1, e2);
    }

    // --- cada campo diferente individualmente ---

    @Test
    @DisplayName("equals: id diferente deve retornar false")
    void equals_IdDiferente_DeveRetornarFalse() {
        Extrato e2 = criarExtratoIdentico();
        e2.setId(99L);
        assertNotEquals(extrato, e2);
    }

    @Test
    @DisplayName("equals: tipo diferente deve retornar false")
    void equals_TipoDiferenteEnum_DeveRetornarFalse() {
        Extrato e2 = criarExtratoIdentico();
        e2.setTipo(TipoExtrato.DEBITO);
        assertNotEquals(extrato, e2);
    }

    @Test
    @DisplayName("equals: valor diferente deve retornar false")
    void equals_ValorDiferente_DeveRetornarFalse() {
        Extrato e2 = criarExtratoIdentico();
        e2.setValor(new BigDecimal("0.01"));
        assertNotEquals(extrato, e2);
    }

    @Test
    @DisplayName("equals: saldoAntes diferente deve retornar false")
    void equals_SaldoAntesDiferente_DeveRetornarFalse() {
        Extrato e2 = criarExtratoIdentico();
        e2.setSaldoAntes(new BigDecimal("0.01"));
        assertNotEquals(extrato, e2);
    }

    @Test
    @DisplayName("equals: saldoDepois diferente deve retornar false")
    void equals_SaldoDepoisDiferente_DeveRetornarFalse() {
        Extrato e2 = criarExtratoIdentico();
        e2.setSaldoDepois(new BigDecimal("0.01"));
        assertNotEquals(extrato, e2);
    }

    @Test
    @DisplayName("equals: descricao diferente deve retornar false")
    void equals_DescricaoDiferente_DeveRetornarFalse() {
        Extrato e2 = criarExtratoIdentico();
        e2.setDescricao("Outra descrição");
        assertNotEquals(extrato, e2);
    }

    @Test
    @DisplayName("equals: dataHora diferente deve retornar false")
    void equals_DataHoraDiferente_DeveRetornarFalse() {
        Extrato e2 = criarExtratoIdentico();
        e2.setDataHora(LocalDateTime.of(2099, 1, 1, 0, 0));
        assertNotEquals(extrato, e2);
    }

    @Test
    @DisplayName("equals: conta diferente deve retornar false")
    void equals_ContaDiferente_DeveRetornarFalse() {
        Extrato e2 = criarExtratoIdentico();
        e2.setConta(Conta.builder().id(999L).build());
        assertNotEquals(extrato, e2);
    }

    @Test
    @DisplayName("equals: pedido diferente deve retornar false")
    void equals_PedidoDiferente_DeveRetornarFalse() {
        Extrato e2 = criarExtratoIdentico();
        e2.setPedido(Pedido.builder().id(999L).build());
        assertNotEquals(extrato, e2);
    }

    // --- branches null: this.campo == null, other.campo != null ---

    @Test
    @DisplayName("equals: this.id==null e other.id!=null deve retornar false")
    void equals_ThisIdNull_DeveRetornarFalse() {
        Extrato e1 = criarExtratoIdentico();
        e1.setId(null);
        assertNotEquals(e1, extrato);
    }

    @Test
    @DisplayName("equals: ambos id==null deve continuar comparando demais campos")
    void equals_AmbosIdNull_DeveCompararDemaisCampos() {
        Extrato e1 = criarExtratoIdentico();
        Extrato e2 = criarExtratoIdentico();
        e1.setId(null);
        e2.setId(null);
        assertEquals(e1, e2);
    }

    @Test
    @DisplayName("equals: this.valor==null e other!=null deve retornar false")
    void equals_ThisValorNull_DeveRetornarFalse() {
        Extrato e1 = criarExtratoIdentico();
        e1.setValor(null);
        assertNotEquals(e1, extrato);
    }

    @Test
    @DisplayName("equals: this.saldoAntes==null e other!=null deve retornar false")
    void equals_ThisSaldoAntesNull_DeveRetornarFalse() {
        Extrato e1 = criarExtratoIdentico();
        e1.setSaldoAntes(null);
        assertNotEquals(e1, extrato);
    }

    @Test
    @DisplayName("equals: this.saldoDepois==null e other!=null deve retornar false")
    void equals_ThisSaldoDepoisNull_DeveRetornarFalse() {
        Extrato e1 = criarExtratoIdentico();
        e1.setSaldoDepois(null);
        assertNotEquals(e1, extrato);
    }

    @Test
    @DisplayName("equals: this.descricao==null e other!=null deve retornar false")
    void equals_ThisDescricaoNull_DeveRetornarFalse() {
        Extrato e1 = criarExtratoIdentico();
        e1.setDescricao(null);
        assertNotEquals(e1, extrato);
    }

    @Test
    @DisplayName("equals: this.dataHora==null e other!=null deve retornar false")
    void equals_ThisDataHoraNull_DeveRetornarFalse() {
        Extrato e1 = criarExtratoIdentico();
        e1.setDataHora(null);
        assertNotEquals(e1, extrato);
    }

    @Test
    @DisplayName("equals: this.conta==null e other!=null deve retornar false")
    void equals_ThisContaNull_DeveRetornarFalse() {
        Extrato e1 = criarExtratoIdentico();
        e1.setConta(null);
        assertNotEquals(e1, extrato);
    }

    @Test
    @DisplayName("equals: this.pedido==null e other!=null deve retornar false")
    void equals_ThisPedidoNull_DeveRetornarFalse() {
        Extrato e1 = criarExtratoIdentico();
        e1.setPedido(null);
        assertNotEquals(e1, extrato);
    }

    // =========================================================
    // TESTES: hashCode()
    // =========================================================

    @Test
    @DisplayName("hashCode: objetos iguais devem ter mesmo hashCode")
    void hashCode_ObjetosIguais_DeveTerMesmoHashCode() {
        assertEquals(extrato.hashCode(), criarExtratoIdentico().hashCode());
    }

    @Test
    @DisplayName("hashCode: objetos diferentes devem ter hashCodes diferentes")
    void hashCode_ObjetosDiferentes_DeveTerHashCodesDiferentes() {
        Extrato e2 = criarExtratoIdentico();
        e2.setValor(new BigDecimal("9999.99"));
        assertNotEquals(extrato.hashCode(), e2.hashCode());
    }

    @Test
    @DisplayName("hashCode: extrato com campos null não deve lançar exceção")
    void hashCode_CamposNull_NaoDeveLancarExcecao() {
        assertDoesNotThrow(() -> new Extrato().hashCode());
    }

    // =========================================================
    // TESTES: toString()
    // =========================================================

    @Test
    @DisplayName("toString: deve conter todos os campos principais")
    void toString_DeveConterCamposPrincipais() {
        String result = extrato.toString();
        assertAll("toString campos",
                () -> assertTrue(result.contains("id")),
                () -> assertTrue(result.contains("tipo")),
                () -> assertTrue(result.contains("valor")),
                () -> assertTrue(result.contains("saldoAntes")),
                () -> assertTrue(result.contains("saldoDepois")),
                () -> assertTrue(result.contains("descricao")),
                () -> assertTrue(result.contains("dataHora"))
        );
    }

    @Test
    @DisplayName("toString: extrato com campos null não deve lançar exceção")
    void toString_CamposNull_NaoDeveLancarExcecao() {
        assertDoesNotThrow(() -> new Extrato().toString());
    }
}