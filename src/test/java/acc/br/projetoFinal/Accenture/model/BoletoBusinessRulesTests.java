package acc.br.projetoFinal.Accenture.model;

import acc.br.projetoFinal.Accenture.enums.StatusBoleto;
import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Cobertura Total - Boleto Model")
class BoletoBusinessRulesTests {

    private Boleto boleto;
    private Pedido pedido;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1L)
                .nome("João Silva")
                .build();

        pedido = Pedido.builder()
                .id(1L)
                .cliente(cliente)
                .status(StatusPedido.RESERVADO)
                .build();

        boleto = Boleto.builder()
                .id(1L)
                .pedido(pedido)
                .codigoBarras("12345678901234567890123456789012345678901234")
                .valor(new BigDecimal("1000.00"))
                .dataVencimento(LocalDate.now().plusDays(3))
                .status(StatusBoleto.PENDENTE)
                .build();
    }

    // =========================================================
    // TESTES: validarPagamento() — cobre todos os branches do if
    // =========================================================

    @Test
    @DisplayName("validarPagamento: não deve lançar exceção quando status é PENDENTE")
    void validarPagamento_StatusPendente_NaoLancaExcecao() {
        boleto.setStatus(StatusBoleto.PENDENTE);
        assertDoesNotThrow(() -> boleto.validarPagamento());
    }

    @Test
    @DisplayName("validarPagamento: deve lançar exceção quando status é PAGO (1º branch)")
    void validarPagamento_StatusPago_LancaExcecao() {
        boleto.setStatus(StatusBoleto.PAGO);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> boleto.validarPagamento()
        );
        assertEquals("Boleto já foi pago", ex.getMessage());
    }

    @Test
    @DisplayName("validarPagamento: deve lançar exceção quando status é CANCELADO (2º branch)")
    void validarPagamento_StatusCancelado_LancaExcecao() {
        boleto.setStatus(StatusBoleto.CANCELADO);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> boleto.validarPagamento()
        );
        assertEquals("Boleto está cancelado", ex.getMessage());
    }

    // =========================================================
    // TESTES: pagar() — cobre fluxo feliz e fluxos de erro
    // =========================================================

    @Test
    @DisplayName("pagar: deve alterar status para PAGO quando PENDENTE")
    void pagar_StatusPendente_AlteraParaPago() {
        boleto.setStatus(StatusBoleto.PENDENTE);
        boleto.pagar();
        assertEquals(StatusBoleto.PAGO, boleto.getStatus());
    }

    @Test
    @DisplayName("pagar: não deve pagar boleto já PAGO")
    void pagar_StatusPago_LancaExcecao() {
        boleto.setStatus(StatusBoleto.PAGO);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> boleto.pagar()
        );
        assertEquals("Boleto já foi pago", ex.getMessage());
    }

    @Test
    @DisplayName("pagar: não deve pagar boleto CANCELADO")
    void pagar_StatusCancelado_LancaExcecao() {
        boleto.setStatus(StatusBoleto.CANCELADO);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> boleto.pagar()
        );
        assertEquals("Boleto está cancelado", ex.getMessage());
    }

    // =========================================================
    // TESTES: validarCancelamento() — cobre todos os branches
    // =========================================================

    @Test
    @DisplayName("validarCancelamento: não deve lançar exceção quando status é PENDENTE")
    void validarCancelamento_StatusPendente_NaoLancaExcecao() {
        boleto.setStatus(StatusBoleto.PENDENTE);
        assertDoesNotThrow(() -> boleto.validarCancelamento());
    }

    @Test
    @DisplayName("validarCancelamento: não deve lançar exceção quando status é PAGO")
    void validarCancelamento_StatusPago_NaoLancaExcecao() {
        boleto.setStatus(StatusBoleto.PAGO);
        assertDoesNotThrow(() -> boleto.validarCancelamento());
    }

    @Test
    @DisplayName("validarCancelamento: deve lançar exceção quando status já é CANCELADO")
    void validarCancelamento_StatusCancelado_LancaExcecao() {
        boleto.setStatus(StatusBoleto.CANCELADO);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> boleto.validarCancelamento()
        );
        assertEquals("Boleto já está cancelado", ex.getMessage());
    }

    // =========================================================
    // TESTES: cancelar() — cobre fluxo feliz e fluxo de erro
    // =========================================================

    @Test
    @DisplayName("cancelar: deve alterar status para CANCELADO quando PENDENTE")
    void cancelar_StatusPendente_AlteraParaCancelado() {
        boleto.setStatus(StatusBoleto.PENDENTE);
        boleto.cancelar();
        assertEquals(StatusBoleto.CANCELADO, boleto.getStatus());
    }

    @Test
    @DisplayName("cancelar: deve alterar status para CANCELADO quando PAGO")
    void cancelar_StatusPago_AlteraParaCancelado() {
        boleto.setStatus(StatusBoleto.PAGO);
        boleto.cancelar();
        assertEquals(StatusBoleto.CANCELADO, boleto.getStatus());
    }

    @Test
    @DisplayName("cancelar: não deve cancelar boleto já CANCELADO")
    void cancelar_StatusCancelado_LancaExcecao() {
        boleto.setStatus(StatusBoleto.CANCELADO);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> boleto.cancelar()
        );
        assertEquals("Boleto já está cancelado", ex.getMessage());
    }

    // =========================================================
    // TESTES: estaAtrasado() — cobre os 3 branches da comparação
    // =========================================================

    @Test
    @DisplayName("estaAtrasado: deve retornar true quando vencimento é ontem")
    void estaAtrasado_VencimentoOntem_RetornaTrue() {
        boleto.setDataVencimento(LocalDate.now().minusDays(1));
        assertTrue(boleto.estaAtrasado());
    }

    @Test
    @DisplayName("estaAtrasado: deve retornar false quando vencimento é hoje")
    void estaAtrasado_VencimentoHoje_RetornaFalse() {
        // isBefore(hoje) == false quando dataVencimento == hoje
        boleto.setDataVencimento(LocalDate.now());
        assertFalse(boleto.estaAtrasado());
    }

    @Test
    @DisplayName("estaAtrasado: deve retornar false quando vencimento é no futuro")
    void estaAtrasado_VencimentoFuturo_RetornaFalse() {
        boleto.setDataVencimento(LocalDate.now().plusDays(5));
        assertFalse(boleto.estaAtrasado());
    }

    // =========================================================
    // TESTES ESTRUTURAIS: Lombok (Getters, Setters, Builder,
    //                     NoArgsConstructor, AllArgsConstructor)
    // =========================================================

    @Test
    @DisplayName("Deve instanciar Boleto com NoArgsConstructor")
    void noArgsConstructor_DeveCriarInstanciaVazia() {
        Boleto boletoVazio = new Boleto();
        assertNotNull(boletoVazio);
    }

    @Test
    @DisplayName("Deve instanciar Boleto com AllArgsConstructor")
    void allArgsConstructor_DeveCriarInstanciaCompleta() {
        Boleto b = new Boleto(
                10L,
                "00000000000000000000000000000000000000000001",
                new BigDecimal("500.00"),
                LocalDate.now().plusDays(7),
                StatusBoleto.PENDENTE,
                pedido
        );
        assertEquals(10L, b.getId());
        assertEquals("00000000000000000000000000000000000000000001", b.getCodigoBarras());
        assertEquals(new BigDecimal("500.00"), b.getValor());
        assertEquals(StatusBoleto.PENDENTE, b.getStatus());
        assertEquals(pedido, b.getPedido());
    }

    @Test
    @DisplayName("Deve testar todos os Setters e Getters")
    void settersEGetters_DeveAtribuirERetornarValoresCorretamente() {
        Boleto b = new Boleto();
        LocalDate vencimento = LocalDate.now().plusDays(10);

        b.setId(99L);
        b.setCodigoBarras("CODIGO123");
        b.setValor(new BigDecimal("250.75"));
        b.setDataVencimento(vencimento);
        b.setStatus(StatusBoleto.PAGO);
        b.setPedido(pedido);

        assertEquals(99L, b.getId());
        assertEquals("CODIGO123", b.getCodigoBarras());
        assertEquals(new BigDecimal("250.75"), b.getValor());
        assertEquals(vencimento, b.getDataVencimento());
        assertEquals(StatusBoleto.PAGO, b.getStatus());
        assertEquals(pedido, b.getPedido());
    }

    @Test
    @DisplayName("Deve testar Builder explicitamente campo a campo")
    void builder_DevePreencherTodosOsCampos() {
        LocalDate vencimento = LocalDate.now().plusDays(2);
        Boleto b = Boleto.builder()
                .id(5L)
                .codigoBarras("BUILDER_CODE")
                .valor(new BigDecimal("99.99"))
                .dataVencimento(vencimento)
                .status(StatusBoleto.CANCELADO)
                .pedido(pedido)
                .build();

        assertEquals(5L, b.getId());
        assertEquals("BUILDER_CODE", b.getCodigoBarras());
        assertEquals(new BigDecimal("99.99"), b.getValor());
        assertEquals(vencimento, b.getDataVencimento());
        assertEquals(StatusBoleto.CANCELADO, b.getStatus());
        assertEquals(pedido, b.getPedido());
    }

    @Test
    @DisplayName("Builder.Default: status deve ser PENDENTE quando não informado")
    void builder_Default_StatusDeveSePendente() {
        Boleto b = Boleto.builder()
                .codigoBarras("DEFAULT_STATUS")
                .build();
        assertEquals(StatusBoleto.PENDENTE, b.getStatus());
    }

    // =========================================================
    // TESTES: equals(), hashCode() e toString()
    //
    // O @Data do Lombok gera equals() com branches para CADA campo:
    //   if (this.campo == null ? other.campo != null : !this.campo.equals(other.campo)) return false
    // Para cobrir 100% dos branches precisamos exercitar:
    //   1. Objetos idênticos (todos os == passam → return true)
    //   2. Mesma instância (this == other → return true imediato)
    //   3. null / tipo errado
    //   4. Cada campo diferente individualmente (força o branch "não-igual" de cada campo)
    //   5. this.campo == null comparado a other.campo != null (branch null de cada campo)
    // =========================================================

    /** Cria um Boleto com exatamente os mesmos valores do setUp(). */
    private Boleto criarBoletoIdentico() {
        return Boleto.builder()
                .id(1L)
                .pedido(pedido)
                .codigoBarras("12345678901234567890123456789012345678901234")
                .valor(new BigDecimal("1000.00"))
                .dataVencimento(boleto.getDataVencimento())
                .status(StatusBoleto.PENDENTE)
                .build();
    }

    // --- equals: fluxos de curto-circuito ---

    @Test
    @DisplayName("equals: mesma instância deve ser igual (branch this==other)")
    void equals_MesmaInstancia_DeveSerIgual() {
        assertEquals(boleto, boleto);
    }

    @Test
    @DisplayName("equals: comparação com null deve retornar false")
    void equals_ComNull_DeveRetornarFalse() {
        assertNotEquals(null, boleto);
    }

    @Test
    @DisplayName("equals: comparação com tipo diferente deve retornar false (branch canEqual)")
    void equals_TipoDiferente_DeveRetornarFalse() {
        assertNotEquals(boleto, new Object());
    }

    // --- equals: todos os campos iguais ---

    @Test
    @DisplayName("equals: objetos com todos os campos iguais devem ser iguais")
    void equals_TodosCamposIguais_DeveRetornarTrue() {
        assertEquals(boleto, criarBoletoIdentico());
    }

    // --- equals: cada campo diferente individualmente (branch "!equal" de cada campo) ---

    @Test
    @DisplayName("equals: id diferente deve retornar false")
    void equals_IdDiferente_DeveRetornarFalse() {
        Boleto b2 = criarBoletoIdentico();
        b2.setId(99L);
        assertNotEquals(boleto, b2);
    }

    @Test
    @DisplayName("equals: codigoBarras diferente deve retornar false")
    void equals_CodigoBarrasDiferente_DeveRetornarFalse() {
        Boleto b2 = criarBoletoIdentico();
        b2.setCodigoBarras("DIFERENTE");
        assertNotEquals(boleto, b2);
    }

    @Test
    @DisplayName("equals: valor diferente deve retornar false")
    void equals_ValorDiferente_DeveRetornarFalse() {
        Boleto b2 = criarBoletoIdentico();
        b2.setValor(new BigDecimal("0.01"));
        assertNotEquals(boleto, b2);
    }

    @Test
    @DisplayName("equals: dataVencimento diferente deve retornar false")
    void equals_DataVencimentoDiferente_DeveRetornarFalse() {
        Boleto b2 = criarBoletoIdentico();
        b2.setDataVencimento(LocalDate.now().plusYears(1));
        assertNotEquals(boleto, b2);
    }

    @Test
    @DisplayName("equals: status diferente deve retornar false")
    void equals_StatusDiferente_DeveRetornarFalse() {
        Boleto b2 = criarBoletoIdentico();
        b2.setStatus(StatusBoleto.PAGO);
        assertNotEquals(boleto, b2);
    }

    @Test
    @DisplayName("equals: pedido diferente deve retornar false")
    void equals_PedidoDiferente_DeveRetornarFalse() {
        Boleto b2 = criarBoletoIdentico();
        Pedido outroPedido = Pedido.builder().id(999L).build();
        b2.setPedido(outroPedido);
        assertNotEquals(boleto, b2);
    }

    // --- equals: branches null em this (this.campo == null → other.campo != null → false) ---

    @Test
    @DisplayName("equals: this.id==null e other.id!=null deve retornar false")
    void equals_ThisIdNull_OutroIdNaoNull_DeveRetornarFalse() {
        Boleto semId = criarBoletoIdentico();
        semId.setId(null);
        assertNotEquals(semId, boleto);
    }

    @Test
    @DisplayName("equals: this.id==null e other.id==null deve continuar comparando demais campos")
    void equals_AmbosIdNull_DeveCompararDemaisCampos() {
        Boleto b1 = criarBoletoIdentico();
        Boleto b2 = criarBoletoIdentico();
        b1.setId(null);
        b2.setId(null);
        assertEquals(b1, b2);
    }

    @Test
    @DisplayName("equals: this.codigoBarras==null e other!=null deve retornar false")
    void equals_ThisCodigoBarrasNull_DeveRetornarFalse() {
        Boleto b1 = criarBoletoIdentico();
        b1.setCodigoBarras(null);
        assertNotEquals(b1, boleto);
    }

    @Test
    @DisplayName("equals: this.valor==null e other!=null deve retornar false")
    void equals_ThisValorNull_DeveRetornarFalse() {
        Boleto b1 = criarBoletoIdentico();
        b1.setValor(null);
        assertNotEquals(b1, boleto);
    }

    @Test
    @DisplayName("equals: this.dataVencimento==null e other!=null deve retornar false")
    void equals_ThisDataVencimentoNull_DeveRetornarFalse() {
        Boleto b1 = criarBoletoIdentico();
        b1.setDataVencimento(null);
        assertNotEquals(b1, boleto);
    }

    @Test
    @DisplayName("equals: this.pedido==null e other!=null deve retornar false")
    void equals_ThisPedidoNull_DeveRetornarFalse() {
        Boleto b1 = criarBoletoIdentico();
        b1.setPedido(null);
        assertNotEquals(b1, boleto);
    }

    @Test
    @DisplayName("equals: ambos com todos os campos null devem ser iguais")
    void equals_AmbosVazios_DeveRetornarTrue() {
        Boleto b1 = new Boleto();
        Boleto b2 = new Boleto();
        assertEquals(b1, b2);
    }

    // --- hashCode ---

    @Test
    @DisplayName("hashCode: objetos iguais devem ter mesmo hashCode")
    void hashCode_ObjetosIguais_DeveTerMesmoHashCode() {
        assertEquals(boleto.hashCode(), criarBoletoIdentico().hashCode());
    }

    @Test
    @DisplayName("hashCode: objetos diferentes devem ter hashCodes diferentes")
    void hashCode_ObjetosDiferentes_DeveTerHashCodesDiferentes() {
        Boleto b2 = criarBoletoIdentico();
        b2.setCodigoBarras("OUTRO");
        assertNotEquals(boleto.hashCode(), b2.hashCode());
    }

    @Test
    @DisplayName("hashCode: boleto com campos null não deve lançar exceção")
    void hashCode_CamposNull_NaoDeveLancarExcecao() {
        Boleto vazio = new Boleto();
        assertDoesNotThrow(vazio::hashCode);
    }

    // --- toString ---

    @Test
    @DisplayName("toString: deve conter todos os campos principais")
    void toString_DeveConterCamposPrincipais() {
        String result = boleto.toString();
        assertAll("toString deve conter campos relevantes",
                () -> assertTrue(result.contains("codigoBarras")),
                () -> assertTrue(result.contains("valor")),
                () -> assertTrue(result.contains("status")),
                () -> assertTrue(result.contains("dataVencimento")),
                () -> assertTrue(result.contains("id"))
        );
    }

    @Test
    @DisplayName("toString: boleto com campos null não deve lançar exceção")
    void toString_CamposNull_NaoDeveLancarExcecao() {
        Boleto vazio = new Boleto();
        assertDoesNotThrow(vazio::toString);
    }
}