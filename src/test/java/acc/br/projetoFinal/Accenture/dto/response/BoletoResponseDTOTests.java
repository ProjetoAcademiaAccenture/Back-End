package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.enums.StatusBoleto;
import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import acc.br.projetoFinal.Accenture.model.Boleto;
import acc.br.projetoFinal.Accenture.model.Pagamento;
import acc.br.projetoFinal.Accenture.model.Pedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Cobertura Total - BoletoResponseDTO")
class BoletoResponseDTOTests {

    // Data futura => boleto NÃO está atrasado
    private static final LocalDate VENCIMENTO_FUTURO  = LocalDate.now().plusDays(30);
    // Data passada => boleto ESTÁ atrasado (se status == PENDENTE)
    private static final LocalDate VENCIMENTO_PASSADO = LocalDate.now().minusDays(5);

    private Boleto boleto;
    private Pagamento pagamento;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        pedido = Pedido.builder()
                .id(10L)
                .status(StatusPedido.RESERVADO)
                .build();

        pagamento = Pagamento.builder()
                .id(20L)
                .pedido(pedido)
                .build();

        boleto = Boleto.builder()
                .id(1L)
                .codigoBarras("12345678901234567890123456789012345678901234")
                .valor(new BigDecimal("1500.00"))
                .dataVencimento(VENCIMENTO_FUTURO)
                .status(StatusBoleto.PENDENTE)
                .pagamento(pagamento)
                .build();
    }

    // ------------------------------------------------------------------ helper

    private BoletoResponseDTO dtoPadraoValido() {
        // AllArgsConstructor: id, codigoBarras, valor, multaAtraso, valorTotal,
        //                     dataVencimento, status, pagamentoId, pedidoId, atrasado
        return new BoletoResponseDTO(
                1L,
                "12345678901234567890123456789012345678901234",
                new BigDecimal("1500.00"),
                BigDecimal.ZERO,
                new BigDecimal("1500.00"),
                VENCIMENTO_FUTURO,
                "PENDENTE",
                20L,
                10L,
                false
        );
    }

    // =========================================================
    // TESTES: fromEntity() — fluxo feliz, boleto em dia
    // =========================================================

    @Test
    @DisplayName("fromEntity: deve mapear todos os campos corretamente (boleto em dia)")
    void fromEntity_BoletoEmDia_DeveMappearTodosOsCampos() {
        BoletoResponseDTO dto = BoletoResponseDTO.fromEntity(boleto);

        assertAll("todos os campos mapeados",
                () -> assertEquals(1L,                                                      dto.getId()),
                () -> assertEquals("12345678901234567890123456789012345678901234",           dto.getCodigoBarras()),
                () -> assertEquals(new BigDecimal("1500.00"),                               dto.getValor()),
                () -> assertEquals(BigDecimal.ZERO,                                          dto.getMultaAtraso()),
                () -> assertEquals(new BigDecimal("1500.00"),                               dto.getValorTotal()),
                () -> assertEquals(VENCIMENTO_FUTURO,                                        dto.getDataVencimento()),
                () -> assertEquals("PENDENTE",                                               dto.getStatus()),
                () -> assertEquals(20L,                                                      dto.getPagamentoId()),
                () -> assertEquals(10L,                                                      dto.getPedidoId()),
                () -> assertFalse(dto.isAtrasado())
        );
    }

    @Test
    @DisplayName("fromEntity: deve retornar instância não nula")
    void fromEntity_DeveRetornarInstanciaNaoNula() {
        assertNotNull(BoletoResponseDTO.fromEntity(boleto));
    }

    // =========================================================
    // TESTES: fromEntity() — branch "boleto atrasado" (PENDENTE + vencimento passado)
    // Cobre o if(atrasado) que calcula multa de 2%
    // =========================================================

    @Test
    @DisplayName("fromEntity: boleto PENDENTE vencido deve calcular multa de 2% e atrasado=true")
    void fromEntity_BoletoAtrasado_DeveCalcularMulta() {
        boleto.setDataVencimento(VENCIMENTO_PASSADO);
        // status já é PENDENTE

        BoletoResponseDTO dto = BoletoResponseDTO.fromEntity(boleto);

        BigDecimal multaEsperada = new BigDecimal("30.00"); // 1500 * 2% = 30
        BigDecimal totalEsperado = new BigDecimal("1530.00");

        assertAll("boleto atrasado",
                () -> assertTrue(dto.isAtrasado()),
                () -> assertEquals(multaEsperada, dto.getMultaAtraso()),
                () -> assertEquals(totalEsperado, dto.getValorTotal())
        );
    }

    @Test
    @DisplayName("fromEntity: boleto PAGO com vencimento passado NÃO deve ter multa (atrasado=false)")
    void fromEntity_BoletoPageComVencimentoPassado_NaoDeveCalcularMulta() {
        boleto.setDataVencimento(VENCIMENTO_PASSADO);
        boleto.setStatus(StatusBoleto.PAGO);

        BoletoResponseDTO dto = BoletoResponseDTO.fromEntity(boleto);

        assertAll("boleto PAGO não tem multa",
                () -> assertFalse(dto.isAtrasado()),
                () -> assertEquals(BigDecimal.ZERO, dto.getMultaAtraso()),
                () -> assertEquals(new BigDecimal("1500.00"), dto.getValorTotal())
        );
    }

    @Test
    @DisplayName("fromEntity: boleto CANCELADO com vencimento passado NÃO deve ter multa")
    void fromEntity_BoletoCanceladoComVencimentoPassado_NaoDeveCalcularMulta() {
        boleto.setDataVencimento(VENCIMENTO_PASSADO);
        boleto.setStatus(StatusBoleto.CANCELADO);

        BoletoResponseDTO dto = BoletoResponseDTO.fromEntity(boleto);

        assertAll("boleto CANCELADO não tem multa",
                () -> assertFalse(dto.isAtrasado()),
                () -> assertEquals(BigDecimal.ZERO, dto.getMultaAtraso())
        );
    }

    // =========================================================
    // TESTES: fromEntity() — cada valor de StatusBoleto
    // =========================================================

    @Test
    @DisplayName("fromEntity: deve mapear status PENDENTE")
    void fromEntity_StatusPendente_DeveMappearStatus() {
        boleto.setStatus(StatusBoleto.PENDENTE);
        assertEquals("PENDENTE", BoletoResponseDTO.fromEntity(boleto).getStatus());
    }

    @Test
    @DisplayName("fromEntity: deve mapear status PAGO")
    void fromEntity_StatusPago_DeveMappearStatus() {
        boleto.setStatus(StatusBoleto.PAGO);
        assertEquals("PAGO", BoletoResponseDTO.fromEntity(boleto).getStatus());
    }

    @Test
    @DisplayName("fromEntity: deve mapear status CANCELADO")
    void fromEntity_StatusCancelado_DeveMappearStatus() {
        boleto.setStatus(StatusBoleto.CANCELADO);
        assertEquals("CANCELADO", BoletoResponseDTO.fromEntity(boleto).getStatus());
    }

    // =========================================================
    // TESTES: fromEntity() — branches ternários de pagamento e pedido
    // =========================================================

    @Test
    @DisplayName("fromEntity: pagamentoId deve ser preenchido quando pagamento existe (branch true)")
    void fromEntity_PagamentoPresente_DeveMappearPagamentoId() {
        assertEquals(20L, BoletoResponseDTO.fromEntity(boleto).getPagamentoId());
    }

    @Test
    @DisplayName("fromEntity: pagamentoId deve ser null quando pagamento é null (branch false)")
    void fromEntity_PagamentoNull_PagamentoIdDeveSerNull() {
        boleto.setPagamento(null);
        assertNull(BoletoResponseDTO.fromEntity(boleto).getPagamentoId());
    }

    @Test
    @DisplayName("fromEntity: pedidoId deve ser preenchido quando pagamento e pedido existem (branch true)")
    void fromEntity_PedidoPresente_DeveMappearPedidoId() {
        assertEquals(10L, BoletoResponseDTO.fromEntity(boleto).getPedidoId());
    }

    @Test
    @DisplayName("fromEntity: pedidoId deve ser null quando pagamento é null (branch false)")
    void fromEntity_PagamentoNull_PedidoIdDeveSerNull() {
        boleto.setPagamento(null);
        assertNull(BoletoResponseDTO.fromEntity(boleto).getPedidoId());
    }

    @Test
    @DisplayName("fromEntity: pedidoId deve ser null quando pagamento existe mas pedido é null (branch false)")
    void fromEntity_PedidoNull_PedidoIdDeveSerNull() {
        pagamento.setPedido(null);
        assertNull(BoletoResponseDTO.fromEntity(boleto).getPedidoId());
    }

    // =========================================================
    // TESTES NEGATIVOS: fromEntity() com argumentos inválidos
    // =========================================================

    @Test
    @DisplayName("fromEntity: deve lançar exceção quando boleto é null")
    void fromEntity_BoletoNull_DeveLancarExcecao() {
        assertThrows(NullPointerException.class,
                () -> BoletoResponseDTO.fromEntity(null));
    }

    @Test
    @DisplayName("fromEntity: deve lançar exceção quando status do boleto é null")
    void fromEntity_StatusNull_DeveLancarExcecao() {
        boleto.setStatus(null);
        assertThrows(NullPointerException.class,
                () -> BoletoResponseDTO.fromEntity(boleto));
    }

    @Test
    @DisplayName("fromEntity: deve mapear valor com precisão decimal")
    void fromEntity_DeveMappearValorComPrecisao() {
        boleto.setValor(new BigDecimal("9999999.99"));
        BoletoResponseDTO dto = BoletoResponseDTO.fromEntity(boleto);
        assertEquals(new BigDecimal("9999999.99"), dto.getValor());
    }

    // =========================================================
    // TESTES ESTRUTURAIS: NoArgsConstructor, AllArgsConstructor,
    //                     Builder, Getters, Setters
    // =========================================================

    @Test
    @DisplayName("NoArgsConstructor: deve criar instância vazia sem exceção")
    void noArgsConstructor_DeveCriarInstanciaVazia() {
        BoletoResponseDTO dto = new BoletoResponseDTO();

        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getCodigoBarras());
        assertNull(dto.getValor());
        assertNull(dto.getMultaAtraso());
        assertNull(dto.getValorTotal());
        assertNull(dto.getDataVencimento());
        assertNull(dto.getStatus());
        assertNull(dto.getPagamentoId());
        assertNull(dto.getPedidoId());
        assertFalse(dto.isAtrasado());
    }

    @Test
    @DisplayName("AllArgsConstructor: deve criar instância com todos os campos")
    void allArgsConstructor_DeveCriarInstanciaCompleta() {
        // Ordem: id, codigoBarras, valor, multaAtraso, valorTotal,
        //        dataVencimento, status, pagamentoId, pedidoId, atrasado
        LocalDate data = LocalDate.of(2026, 12, 31);
        BoletoResponseDTO dto = new BoletoResponseDTO(
                5L,
                "CODIGO_BARRAS_TESTE",
                new BigDecimal("200.00"),
                new BigDecimal("4.00"),
                new BigDecimal("204.00"),
                data,
                "PAGO",
                20L,
                10L,
                true
        );

        assertAll("campos do AllArgsConstructor",
                () -> assertEquals(5L,                          dto.getId()),
                () -> assertEquals("CODIGO_BARRAS_TESTE",       dto.getCodigoBarras()),
                () -> assertEquals(new BigDecimal("200.00"),    dto.getValor()),
                () -> assertEquals(new BigDecimal("4.00"),      dto.getMultaAtraso()),
                () -> assertEquals(new BigDecimal("204.00"),    dto.getValorTotal()),
                () -> assertEquals(data,                        dto.getDataVencimento()),
                () -> assertEquals("PAGO",                      dto.getStatus()),
                () -> assertEquals(20L,                         dto.getPagamentoId()),
                () -> assertEquals(10L,                         dto.getPedidoId()),
                () -> assertTrue(dto.isAtrasado())
        );
    }

    @Test
    @DisplayName("Builder: deve construir DTO campo a campo")
    void builder_DeveConstruirDTOCampoACampo() {
        LocalDate data = LocalDate.of(2026, 3, 10);
        BoletoResponseDTO dto = BoletoResponseDTO.builder()
                .id(7L)
                .codigoBarras("BUILDER_CODIGO")
                .valor(new BigDecimal("350.75"))
                .multaAtraso(new BigDecimal("7.02"))
                .valorTotal(new BigDecimal("357.77"))
                .dataVencimento(data)
                .status("CANCELADO")
                .pagamentoId(25L)
                .pedidoId(15L)
                .atrasado(false)
                .build();

        assertAll("campos do builder",
                () -> assertEquals(7L,                          dto.getId()),
                () -> assertEquals("BUILDER_CODIGO",            dto.getCodigoBarras()),
                () -> assertEquals(new BigDecimal("350.75"),    dto.getValor()),
                () -> assertEquals(new BigDecimal("7.02"),      dto.getMultaAtraso()),
                () -> assertEquals(new BigDecimal("357.77"),    dto.getValorTotal()),
                () -> assertEquals(data,                        dto.getDataVencimento()),
                () -> assertEquals("CANCELADO",                 dto.getStatus()),
                () -> assertEquals(25L,                         dto.getPagamentoId()),
                () -> assertEquals(15L,                         dto.getPedidoId()),
                () -> assertFalse(dto.isAtrasado())
        );
    }

    @Test
    @DisplayName("Setters/Getters: deve atribuir e retornar cada campo corretamente")
    void settersGetters_DeveAtribuirERetornarCampos() {
        BoletoResponseDTO dto = new BoletoResponseDTO();
        LocalDate data = LocalDate.now();

        dto.setId(3L);
        dto.setCodigoBarras("SET_CODIGO");
        dto.setValor(new BigDecimal("100.00"));
        dto.setMultaAtraso(new BigDecimal("2.00"));
        dto.setValorTotal(new BigDecimal("102.00"));
        dto.setDataVencimento(data);
        dto.setStatus("PENDENTE");
        dto.setPagamentoId(8L);
        dto.setPedidoId(5L);
        dto.setAtrasado(true);

        assertAll("setters e getters",
                () -> assertEquals(3L,                          dto.getId()),
                () -> assertEquals("SET_CODIGO",                dto.getCodigoBarras()),
                () -> assertEquals(new BigDecimal("100.00"),    dto.getValor()),
                () -> assertEquals(new BigDecimal("2.00"),      dto.getMultaAtraso()),
                () -> assertEquals(new BigDecimal("102.00"),    dto.getValorTotal()),
                () -> assertEquals(data,                        dto.getDataVencimento()),
                () -> assertEquals("PENDENTE",                  dto.getStatus()),
                () -> assertEquals(8L,                          dto.getPagamentoId()),
                () -> assertEquals(5L,                          dto.getPedidoId()),
                () -> assertTrue(dto.isAtrasado())
        );
    }

    @Test
    @DisplayName("Deve aceitar null em todos os setters sem exceção")
    void deveAceitarNullEmTodosOsSetters() {
        BoletoResponseDTO dto = dtoPadraoValido();

        assertThatCode(() -> {
            dto.setId(null);
            dto.setCodigoBarras(null);
            dto.setValor(null);
            dto.setMultaAtraso(null);
            dto.setValorTotal(null);
            dto.setDataVencimento(null);
            dto.setStatus(null);
            dto.setPagamentoId(null);
            dto.setPedidoId(null);
        }).doesNotThrowAnyException();

        assertNull(dto.getId());
        assertNull(dto.getCodigoBarras());
        assertNull(dto.getValor());
        assertNull(dto.getMultaAtraso());
        assertNull(dto.getValorTotal());
        assertNull(dto.getDataVencimento());
        assertNull(dto.getStatus());
        assertNull(dto.getPagamentoId());
        assertNull(dto.getPedidoId());
    }

    // =========================================================
    // TESTES: equals()
    // =========================================================

    private BoletoResponseDTO criarDTOIdentico() {
        return BoletoResponseDTO.builder()
                .id(1L)
                .codigoBarras("12345678901234567890123456789012345678901234")
                .valor(new BigDecimal("1500.00"))
                .multaAtraso(BigDecimal.ZERO)
                .valorTotal(new BigDecimal("1500.00"))
                .dataVencimento(VENCIMENTO_FUTURO)
                .status("PENDENTE")
                .pagamentoId(20L)
                .pedidoId(10L)
                .atrasado(false)
                .build();
    }

    @Test
    @DisplayName("equals: mesma instância deve ser igual (reflexividade)")
    void equals_MesmaInstancia_DeveSerIgual() {
        BoletoResponseDTO dto = criarDTOIdentico();
        assertEquals(dto, dto);
    }

    @Test
    @DisplayName("equals: dois DTOs com todos os campos iguais devem ser iguais")
    void equals_TodosCamposIguais_DeveSerIgual() {
        assertEquals(criarDTOIdentico(), criarDTOIdentico());
    }

    @Test
    @DisplayName("equals: comparação com null deve retornar false")
    void equals_ComNull_DeveRetornarFalse() {
        assertNotEquals(null, criarDTOIdentico());
    }

    @Test
    @DisplayName("equals: comparação com tipo diferente deve retornar false")
    void equals_TipoDiferente_DeveRetornarFalse() {
        assertNotEquals(criarDTOIdentico(), new Object());
        assertNotEquals("string", criarDTOIdentico());
    }

    @Test
    @DisplayName("equals: dois DTOs vazios devem ser iguais")
    void equals_AmbosVazios_DeveSerIgual() {
        assertEquals(new BoletoResponseDTO(), new BoletoResponseDTO());
    }

    // --- cada campo diferente individualmente ---

    @Test
    @DisplayName("equals: id diferente deve retornar false")
    void equals_IdDiferente_DeveRetornarFalse() {
        BoletoResponseDTO d2 = criarDTOIdentico();
        d2.setId(99L);
        assertNotEquals(criarDTOIdentico(), d2);
    }

    @Test
    @DisplayName("equals: codigoBarras diferente deve retornar false")
    void equals_CodigoBarrasDiferente_DeveRetornarFalse() {
        BoletoResponseDTO d2 = criarDTOIdentico();
        d2.setCodigoBarras("OUTRO");
        assertNotEquals(criarDTOIdentico(), d2);
    }

    @Test
    @DisplayName("equals: valor diferente deve retornar false")
    void equals_ValorDiferente_DeveRetornarFalse() {
        BoletoResponseDTO d2 = criarDTOIdentico();
        d2.setValor(new BigDecimal("0.01"));
        assertNotEquals(criarDTOIdentico(), d2);
    }

    @Test
    @DisplayName("equals: multaAtraso diferente deve retornar false")
    void equals_MultaAtrasoDiferente_DeveRetornarFalse() {
        BoletoResponseDTO d2 = criarDTOIdentico();
        d2.setMultaAtraso(new BigDecimal("10.00"));
        assertNotEquals(criarDTOIdentico(), d2);
    }

    @Test
    @DisplayName("equals: valorTotal diferente deve retornar false")
    void equals_ValorTotalDiferente_DeveRetornarFalse() {
        BoletoResponseDTO d2 = criarDTOIdentico();
        d2.setValorTotal(new BigDecimal("9999.00"));
        assertNotEquals(criarDTOIdentico(), d2);
    }

    @Test
    @DisplayName("equals: dataVencimento diferente deve retornar false")
    void equals_DataVencimentoDiferente_DeveRetornarFalse() {
        BoletoResponseDTO d2 = criarDTOIdentico();
        d2.setDataVencimento(LocalDate.of(2099, 1, 1));
        assertNotEquals(criarDTOIdentico(), d2);
    }

    @Test
    @DisplayName("equals: status diferente deve retornar false")
    void equals_StatusDiferente_DeveRetornarFalse() {
        BoletoResponseDTO d2 = criarDTOIdentico();
        d2.setStatus("PAGO");
        assertNotEquals(criarDTOIdentico(), d2);
    }

    @Test
    @DisplayName("equals: pagamentoId diferente deve retornar false")
    void equals_PagamentoIdDiferente_DeveRetornarFalse() {
        BoletoResponseDTO d2 = criarDTOIdentico();
        d2.setPagamentoId(999L);
        assertNotEquals(criarDTOIdentico(), d2);
    }

    @Test
    @DisplayName("equals: pedidoId diferente deve retornar false")
    void equals_PedidoIdDiferente_DeveRetornarFalse() {
        BoletoResponseDTO d2 = criarDTOIdentico();
        d2.setPedidoId(999L);
        assertNotEquals(criarDTOIdentico(), d2);
    }

    @Test
    @DisplayName("equals: atrasado diferente deve retornar false")
    void equals_AtrasadoDiferente_DeveRetornarFalse() {
        BoletoResponseDTO d2 = criarDTOIdentico();
        d2.setAtrasado(true);
        assertNotEquals(criarDTOIdentico(), d2);
    }

    // --- branches null ---

    @Test
    @DisplayName("equals: this.id==null e other.id!=null deve retornar false")
    void equals_ThisIdNull_DeveRetornarFalse() {
        BoletoResponseDTO d1 = criarDTOIdentico();
        d1.setId(null);
        assertNotEquals(d1, criarDTOIdentico());
    }

    @Test
    @DisplayName("equals: ambos id==null deve comparar demais campos")
    void equals_AmbosIdNull_DeveCompararDemaisCampos() {
        BoletoResponseDTO d1 = criarDTOIdentico();
        BoletoResponseDTO d2 = criarDTOIdentico();
        d1.setId(null);
        d2.setId(null);
        assertEquals(d1, d2);
    }

    @Test
    @DisplayName("equals: this.codigoBarras==null e other!=null deve retornar false")
    void equals_ThisCodigoBarrasNull_DeveRetornarFalse() {
        BoletoResponseDTO d1 = criarDTOIdentico();
        d1.setCodigoBarras(null);
        assertNotEquals(d1, criarDTOIdentico());
    }

    @Test
    @DisplayName("equals: this.valor==null e other!=null deve retornar false")
    void equals_ThisValorNull_DeveRetornarFalse() {
        BoletoResponseDTO d1 = criarDTOIdentico();
        d1.setValor(null);
        assertNotEquals(d1, criarDTOIdentico());
    }

    @Test
    @DisplayName("equals: this.multaAtraso==null e other!=null deve retornar false")
    void equals_ThisMultaAtrasoNull_DeveRetornarFalse() {
        BoletoResponseDTO d1 = criarDTOIdentico();
        d1.setMultaAtraso(null);
        assertNotEquals(d1, criarDTOIdentico());
    }

    @Test
    @DisplayName("equals: this.status==null e other!=null deve retornar false")
    void equals_ThisStatusNull_DeveRetornarFalse() {
        BoletoResponseDTO d1 = criarDTOIdentico();
        d1.setStatus(null);
        assertNotEquals(d1, criarDTOIdentico());
    }

    @Test
    @DisplayName("equals: this.pedidoId==null e other!=null deve retornar false")
    void equals_ThisPedidoIdNull_DeveRetornarFalse() {
        BoletoResponseDTO d1 = criarDTOIdentico();
        d1.setPedidoId(null);
        assertNotEquals(d1, criarDTOIdentico());
    }

    // =========================================================
    // TESTES: hashCode()
    // =========================================================

    @Test
    @DisplayName("hashCode: objetos iguais devem ter mesmo hashCode")
    void hashCode_ObjetosIguais_DeveTerMesmoHashCode() {
        assertEquals(criarDTOIdentico().hashCode(), criarDTOIdentico().hashCode());
    }

    @Test
    @DisplayName("hashCode: objetos diferentes devem ter hashCodes diferentes")
    void hashCode_ObjetosDiferentes_DeveTerHashCodesDiferentes() {
        BoletoResponseDTO d2 = criarDTOIdentico();
        d2.setCodigoBarras("DIFERENTE");
        assertNotEquals(criarDTOIdentico().hashCode(), d2.hashCode());
    }

    @Test
    @DisplayName("hashCode: DTO com campos null não deve lançar exceção")
    void hashCode_CamposNull_NaoDeveLancarExcecao() {
        assertDoesNotThrow(() -> new BoletoResponseDTO().hashCode());
    }

    @Test
    @DisplayName("hashCode: consistente em múltiplas chamadas")
    void hashCode_Consistente() {
        BoletoResponseDTO dto = criarDTOIdentico();
        assertEquals(dto.hashCode(), dto.hashCode());
    }

    // =========================================================
    // TESTES: toString()
    // =========================================================

    @Test
    @DisplayName("toString: deve conter todos os campos principais")
    void toString_DeveConterCamposPrincipais() {
        String result = criarDTOIdentico().toString();
        assertAll("toString campos",
                () -> assertTrue(result.contains("id")),
                () -> assertTrue(result.contains("codigoBarras")),
                () -> assertTrue(result.contains("valor")),
                () -> assertTrue(result.contains("multaAtraso")),
                () -> assertTrue(result.contains("valorTotal")),
                () -> assertTrue(result.contains("status")),
                () -> assertTrue(result.contains("pagamentoId")),
                () -> assertTrue(result.contains("pedidoId")),
                () -> assertTrue(result.contains("dataVencimento")),
                () -> assertTrue(result.contains("atrasado"))
        );
    }

    @Test
    @DisplayName("toString: DTO com campos null não deve lançar exceção")
    void toString_CamposNull_NaoDeveLancarExcecao() {
        assertDoesNotThrow(() -> new BoletoResponseDTO().toString());
    }

    // =========================================================
    // TESTES: canEqual()
    // =========================================================

    @Test
    @DisplayName("canEqual: deve retornar true para instância do mesmo tipo")
    void canEqual_MesmoTipo_DeveRetornarTrue() {
        BoletoResponseDTO dto1 = criarDTOIdentico();
        BoletoResponseDTO dto2 = new BoletoResponseDTO();
        assertTrue(dto1.canEqual(dto2));
    }

    @Test
    @DisplayName("canEqual: deve retornar false para tipo diferente")
    void canEqual_TipoDiferente_DeveRetornarFalse() {
        BoletoResponseDTO dto = criarDTOIdentico();
        assertFalse(dto.canEqual("string"));
        assertFalse(dto.canEqual(null));
        assertFalse(dto.canEqual(42));
    }
}