package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import acc.br.projetoFinal.Accenture.enums.TipoExtrato;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.model.Extrato;
import acc.br.projetoFinal.Accenture.model.Pagamento;
import acc.br.projetoFinal.Accenture.model.Pedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Cobertura Total - ExtratoResponseDTO")
class ExtratoResponseDTOTests {

    private static final LocalDateTime DATA_FIXA = LocalDateTime.of(2026, 5, 12, 10, 0, 0);

    private Extrato extrato;
    private Pedido pedido;
    private Conta conta;
    private Pagamento pagamento;

    @BeforeEach
    void setUp() {
        conta = Conta.builder().id(1L).build();

        pedido = Pedido.builder()
                .id(10L)
                .status(StatusPedido.RESERVADO)
                .build();

        pagamento = Pagamento.builder()
                .id(20L)
                .build();

        extrato = Extrato.builder()
                .id(1L)
                .conta(conta)
                .tipo(TipoExtrato.CREDITO)
                .valor(new BigDecimal("500.00"))
                .saldoAntes(new BigDecimal("1000.00"))
                .saldoDepois(new BigDecimal("1500.00"))
                .descricao("Crédito de venda")
                .pedido(pedido)
                .pagamento(pagamento)
                .dataHora(DATA_FIXA)
                .build();
    }

    // =========================================================
    // TESTES: fromEntity() — fluxo feliz completo
    // =========================================================

    @Test
    @DisplayName("fromEntity: deve mapear todos os campos corretamente")
    void fromEntity_DeveMapearTodosOsCampos() {
        ExtratoResponseDTO dto = ExtratoResponseDTO.fromEntity(extrato);

        assertAll("todos os campos mapeados",
                () -> assertEquals(1L,                           dto.getId()),
                () -> assertEquals(1L,                           dto.getContaId()),
                () -> assertEquals(10L,                          dto.getPedidoId()),
                () -> assertEquals(20L,                          dto.getPagamentoId()),
                () -> assertEquals("CREDITO",                    dto.getTipo()),
                () -> assertEquals(new BigDecimal("500.00"),     dto.getValor()),
                () -> assertEquals(new BigDecimal("1000.00"),    dto.getSaldoAntes()),
                () -> assertEquals(new BigDecimal("1500.00"),    dto.getSaldoDepois()),
                () -> assertEquals("Crédito de venda",           dto.getDescricao()),
                () -> assertEquals(DATA_FIXA,                    dto.getDataHora())
        );
    }

    @Test
    @DisplayName("fromEntity: deve retornar instância não nula")
    void fromEntity_DeveRetornarInstanciaNaoNula() {
        assertNotNull(ExtratoResponseDTO.fromEntity(extrato));
    }

    // =========================================================
    // TESTES: fromEntity() — cada valor de TipoExtrato
    // =========================================================

    @Test
    @DisplayName("fromEntity: deve mapear tipo CREDITO")
    void fromEntity_TipoCredito_DeveMappearTipo() {
        extrato.setTipo(TipoExtrato.CREDITO);
        assertEquals("CREDITO", ExtratoResponseDTO.fromEntity(extrato).getTipo());
    }

    @Test
    @DisplayName("fromEntity: deve mapear tipo DEBITO")
    void fromEntity_TipoDebito_DeveMappearTipo() {
        extrato.setTipo(TipoExtrato.DEBITO);
        assertEquals("DEBITO", ExtratoResponseDTO.fromEntity(extrato).getTipo());
    }

    @Test
    @DisplayName("fromEntity: deve mapear tipo ESTORNO")
    void fromEntity_TipoEstorno_DeveMappearTipo() {
        extrato.setTipo(TipoExtrato.ESTORNO);
        assertEquals("ESTORNO", ExtratoResponseDTO.fromEntity(extrato).getTipo());
    }

    // =========================================================
    // TESTES: fromEntity() — branches ternários de conta, pedido e pagamento
    // =========================================================

    @Test
    @DisplayName("fromEntity: contaId deve ser preenchido quando conta existe (branch true)")
    void fromEntity_ContaPresente_DeveMappearContaId() {
        extrato.setConta(conta);
        assertEquals(1L, ExtratoResponseDTO.fromEntity(extrato).getContaId());
    }

    @Test
    @DisplayName("fromEntity: contaId deve ser null quando conta é null (branch false)")
    void fromEntity_ContaNull_ContaIdDeveSerNull() {
        extrato.setConta(null);
        assertNull(ExtratoResponseDTO.fromEntity(extrato).getContaId());
    }

    @Test
    @DisplayName("fromEntity: pedidoId deve ser preenchido quando pedido existe (branch true)")
    void fromEntity_PedidoPresente_DeveMappearPedidoId() {
        extrato.setPedido(pedido);
        assertEquals(10L, ExtratoResponseDTO.fromEntity(extrato).getPedidoId());
    }

    @Test
    @DisplayName("fromEntity: pedidoId deve ser null quando pedido é null (branch false)")
    void fromEntity_PedidoNull_PedidoIdDeveSerNull() {
        extrato.setPedido(null);
        assertNull(ExtratoResponseDTO.fromEntity(extrato).getPedidoId());
    }

    @Test
    @DisplayName("fromEntity: pagamentoId deve ser preenchido quando pagamento existe (branch true)")
    void fromEntity_PagamentoPresente_DeveMappearPagamentoId() {
        extrato.setPagamento(pagamento);
        assertEquals(20L, ExtratoResponseDTO.fromEntity(extrato).getPagamentoId());
    }

    @Test
    @DisplayName("fromEntity: pagamentoId deve ser null quando pagamento é null (branch false)")
    void fromEntity_PagamentoNull_PagamentoIdDeveSerNull() {
        extrato.setPagamento(null);
        assertNull(ExtratoResponseDTO.fromEntity(extrato).getPagamentoId());
    }

    // =========================================================
    // TESTES NEGATIVOS: fromEntity() com campos inválidos
    // =========================================================

    @Test
    @DisplayName("fromEntity: deve lançar exceção quando extrato é null")
    void fromEntity_ExtratoNull_DeveLancarExcecao() {
        assertThrows(NullPointerException.class,
                () -> ExtratoResponseDTO.fromEntity(null));
    }

    @Test
    @DisplayName("fromEntity: deve lançar exceção quando tipo é null")
    void fromEntity_TipoNull_DeveLancarExcecao() {
        extrato.setTipo(null);
        assertThrows(NullPointerException.class,
                () -> ExtratoResponseDTO.fromEntity(extrato));
    }

    @Test
    @DisplayName("fromEntity: descricao null deve ser mapeado como null sem exceção")
    void fromEntity_DescricaoNull_DeveMappearNull() {
        extrato.setDescricao(null);
        assertNull(ExtratoResponseDTO.fromEntity(extrato).getDescricao());
    }

    @Test
    @DisplayName("fromEntity: valor com precisão máxima deve ser mapeado corretamente")
    void fromEntity_ValorMaximo_DeveMappearCorretamente() {
        extrato.setValor(new BigDecimal("99999999.99"));
        assertEquals(new BigDecimal("99999999.99"),
                ExtratoResponseDTO.fromEntity(extrato).getValor());
    }

    // =========================================================
    // TESTES ESTRUTURAIS: NoArgsConstructor, AllArgsConstructor,
    //                     Builder, Getters, Setters
    // =========================================================

    @Test
    @DisplayName("NoArgsConstructor: deve criar instância vazia sem exceção")
    void noArgsConstructor_DeveCriarInstanciaVazia() {
        ExtratoResponseDTO dto = new ExtratoResponseDTO();

        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getContaId());
        assertNull(dto.getPedidoId());
        assertNull(dto.getPagamentoId());
        assertNull(dto.getTipo());
        assertNull(dto.getValor());
        assertNull(dto.getSaldoAntes());
        assertNull(dto.getSaldoDepois());
        assertNull(dto.getDescricao());
        assertNull(dto.getDataHora());
    }

    @Test
    @DisplayName("AllArgsConstructor: deve criar instância com todos os campos")
    void allArgsConstructor_DeveCriarInstanciaCompleta() {
        // Ordem dos campos: id, contaId, pedidoId, pagamentoId, tipo, valor,
        //                   saldoAntes, saldoDepois, descricao, dataHora
        ExtratoResponseDTO dto = new ExtratoResponseDTO(
                5L,
                1L,
                20L,
                30L,
                "DEBITO",
                new BigDecimal("200.00"),
                new BigDecimal("800.00"),
                new BigDecimal("600.00"),
                "Débito em conta",
                DATA_FIXA
        );

        assertAll("AllArgsConstructor",
                () -> assertEquals(5L,                        dto.getId()),
                () -> assertEquals(1L,                        dto.getContaId()),
                () -> assertEquals(20L,                       dto.getPedidoId()),
                () -> assertEquals(30L,                       dto.getPagamentoId()),
                () -> assertEquals("DEBITO",                  dto.getTipo()),
                () -> assertEquals(new BigDecimal("200.00"),  dto.getValor()),
                () -> assertEquals(new BigDecimal("800.00"),  dto.getSaldoAntes()),
                () -> assertEquals(new BigDecimal("600.00"),  dto.getSaldoDepois()),
                () -> assertEquals("Débito em conta",         dto.getDescricao()),
                () -> assertEquals(DATA_FIXA,                 dto.getDataHora())
        );
    }

    @Test
    @DisplayName("Builder: deve construir DTO campo a campo")
    void builder_DeveConstruirDTOCampoACampo() {
        ExtratoResponseDTO dto = ExtratoResponseDTO.builder()
                .id(7L)
                .contaId(2L)
                .pedidoId(15L)
                .pagamentoId(25L)
                .tipo("ESTORNO")
                .valor(new BigDecimal("350.75"))
                .saldoAntes(new BigDecimal("1000.00"))
                .saldoDepois(new BigDecimal("1350.75"))
                .descricao("Estorno de compra")
                .dataHora(DATA_FIXA)
                .build();

        assertAll("Builder campos",
                () -> assertEquals(7L,                         dto.getId()),
                () -> assertEquals(2L,                         dto.getContaId()),
                () -> assertEquals(15L,                        dto.getPedidoId()),
                () -> assertEquals(25L,                        dto.getPagamentoId()),
                () -> assertEquals("ESTORNO",                  dto.getTipo()),
                () -> assertEquals(new BigDecimal("350.75"),   dto.getValor()),
                () -> assertEquals(new BigDecimal("1000.00"),  dto.getSaldoAntes()),
                () -> assertEquals(new BigDecimal("1350.75"),  dto.getSaldoDepois()),
                () -> assertEquals("Estorno de compra",        dto.getDescricao()),
                () -> assertEquals(DATA_FIXA,                  dto.getDataHora())
        );
    }

    @Test
    @DisplayName("Setters/Getters: deve atribuir e retornar cada campo corretamente")
    void settersGetters_DeveAtribuirERetornarCampos() {
        ExtratoResponseDTO dto = new ExtratoResponseDTO();

        dto.setId(3L);
        dto.setContaId(1L);
        dto.setPedidoId(5L);
        dto.setPagamentoId(8L);
        dto.setTipo("DEBITO");
        dto.setValor(new BigDecimal("100.00"));
        dto.setSaldoAntes(new BigDecimal("500.00"));
        dto.setSaldoDepois(new BigDecimal("400.00"));
        dto.setDescricao("Débito em compra");
        dto.setDataHora(DATA_FIXA);

        assertAll("setters e getters",
                () -> assertEquals(3L,                        dto.getId()),
                () -> assertEquals(1L,                        dto.getContaId()),
                () -> assertEquals(5L,                        dto.getPedidoId()),
                () -> assertEquals(8L,                        dto.getPagamentoId()),
                () -> assertEquals("DEBITO",                  dto.getTipo()),
                () -> assertEquals(new BigDecimal("100.00"),  dto.getValor()),
                () -> assertEquals(new BigDecimal("500.00"),  dto.getSaldoAntes()),
                () -> assertEquals(new BigDecimal("400.00"),  dto.getSaldoDepois()),
                () -> assertEquals("Débito em compra",        dto.getDescricao()),
                () -> assertEquals(DATA_FIXA,                 dto.getDataHora())
        );
    }

    // =========================================================
    // TESTES: equals() — todos os branches do @Data
    // =========================================================

    private ExtratoResponseDTO criarDTOIdentico() {
        return ExtratoResponseDTO.builder()
                .id(1L)
                .contaId(1L)
                .pedidoId(10L)
                .pagamentoId(20L)
                .tipo("CREDITO")
                .valor(new BigDecimal("500.00"))
                .saldoAntes(new BigDecimal("1000.00"))
                .saldoDepois(new BigDecimal("1500.00"))
                .descricao("Crédito de venda")
                .dataHora(DATA_FIXA)
                .build();
    }

    @Test
    @DisplayName("equals: mesma instância deve ser igual (reflexividade)")
    void equals_MesmaInstancia_DeveSerIgual() {
        ExtratoResponseDTO dto = criarDTOIdentico();
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
    @DisplayName("equals: comparação com tipo diferente deve retornar false (canEqual)")
    void equals_TipoObjDiferente_DeveRetornarFalse() {
        assertNotEquals(criarDTOIdentico(), new Object());
    }

    @Test
    @DisplayName("equals: dois DTOs vazios devem ser iguais")
    void equals_AmbosVazios_DeveSerIgual() {
        assertEquals(new ExtratoResponseDTO(), new ExtratoResponseDTO());
    }

    // --- cada campo diferente individualmente ---

    @Test
    @DisplayName("equals: id diferente deve retornar false")
    void equals_IdDiferente_DeveRetornarFalse() {
        ExtratoResponseDTO d2 = criarDTOIdentico();
        d2.setId(99L);
        assertNotEquals(criarDTOIdentico(), d2);
    }

    @Test
    @DisplayName("equals: contaId diferente deve retornar false")
    void equals_ContaIdDiferente_DeveRetornarFalse() {
        ExtratoResponseDTO d2 = criarDTOIdentico();
        d2.setContaId(99L);
        assertNotEquals(criarDTOIdentico(), d2);
    }

    @Test
    @DisplayName("equals: pedidoId diferente deve retornar false")
    void equals_PedidoIdDiferente_DeveRetornarFalse() {
        ExtratoResponseDTO d2 = criarDTOIdentico();
        d2.setPedidoId(999L);
        assertNotEquals(criarDTOIdentico(), d2);
    }

    @Test
    @DisplayName("equals: pagamentoId diferente deve retornar false")
    void equals_PagamentoIdDiferente_DeveRetornarFalse() {
        ExtratoResponseDTO d2 = criarDTOIdentico();
        d2.setPagamentoId(999L);
        assertNotEquals(criarDTOIdentico(), d2);
    }

    @Test
    @DisplayName("equals: tipo diferente deve retornar false")
    void equals_TipoDiferenteStr_DeveRetornarFalse() {
        ExtratoResponseDTO d2 = criarDTOIdentico();
        d2.setTipo("DEBITO");
        assertNotEquals(criarDTOIdentico(), d2);
    }

    @Test
    @DisplayName("equals: valor diferente deve retornar false")
    void equals_ValorDiferente_DeveRetornarFalse() {
        ExtratoResponseDTO d2 = criarDTOIdentico();
        d2.setValor(new BigDecimal("0.01"));
        assertNotEquals(criarDTOIdentico(), d2);
    }

    @Test
    @DisplayName("equals: saldoAntes diferente deve retornar false")
    void equals_SaldoAntesDiferente_DeveRetornarFalse() {
        ExtratoResponseDTO d2 = criarDTOIdentico();
        d2.setSaldoAntes(new BigDecimal("0.01"));
        assertNotEquals(criarDTOIdentico(), d2);
    }

    @Test
    @DisplayName("equals: saldoDepois diferente deve retornar false")
    void equals_SaldoDepoisDiferente_DeveRetornarFalse() {
        ExtratoResponseDTO d2 = criarDTOIdentico();
        d2.setSaldoDepois(new BigDecimal("0.01"));
        assertNotEquals(criarDTOIdentico(), d2);
    }

    @Test
    @DisplayName("equals: descricao diferente deve retornar false")
    void equals_DescricaoDiferente_DeveRetornarFalse() {
        ExtratoResponseDTO d2 = criarDTOIdentico();
        d2.setDescricao("Outra");
        assertNotEquals(criarDTOIdentico(), d2);
    }

    @Test
    @DisplayName("equals: dataHora diferente deve retornar false")
    void equals_DataHoraDiferente_DeveRetornarFalse() {
        ExtratoResponseDTO d2 = criarDTOIdentico();
        d2.setDataHora(LocalDateTime.of(2099, 1, 1, 0, 0));
        assertNotEquals(criarDTOIdentico(), d2);
    }

    // --- branches null: this.campo == null, other.campo != null ---

    @Test
    @DisplayName("equals: this.id==null e other.id!=null deve retornar false")
    void equals_ThisIdNull_DeveRetornarFalse() {
        ExtratoResponseDTO d1 = criarDTOIdentico();
        d1.setId(null);
        assertNotEquals(d1, criarDTOIdentico());
    }

    @Test
    @DisplayName("equals: ambos id==null deve continuar comparando demais campos")
    void equals_AmbosIdNull_DeveCompararDemaisCampos() {
        ExtratoResponseDTO d1 = criarDTOIdentico();
        ExtratoResponseDTO d2 = criarDTOIdentico();
        d1.setId(null);
        d2.setId(null);
        assertEquals(d1, d2);
    }

    @Test
    @DisplayName("equals: this.contaId==null e other!=null deve retornar false")
    void equals_ThisContaIdNull_DeveRetornarFalse() {
        ExtratoResponseDTO d1 = criarDTOIdentico();
        d1.setContaId(null);
        assertNotEquals(d1, criarDTOIdentico());
    }

    @Test
    @DisplayName("equals: this.pedidoId==null e other!=null deve retornar false")
    void equals_ThisPedidoIdNull_DeveRetornarFalse() {
        ExtratoResponseDTO d1 = criarDTOIdentico();
        d1.setPedidoId(null);
        assertNotEquals(d1, criarDTOIdentico());
    }

    @Test
    @DisplayName("equals: this.pagamentoId==null e other!=null deve retornar false")
    void equals_ThisPagamentoIdNull_DeveRetornarFalse() {
        ExtratoResponseDTO d1 = criarDTOIdentico();
        d1.setPagamentoId(null);
        assertNotEquals(d1, criarDTOIdentico());
    }

    @Test
    @DisplayName("equals: this.tipo==null e other!=null deve retornar false")
    void equals_ThisTipoNull_DeveRetornarFalse() {
        ExtratoResponseDTO d1 = criarDTOIdentico();
        d1.setTipo(null);
        assertNotEquals(d1, criarDTOIdentico());
    }

    @Test
    @DisplayName("equals: this.valor==null e other!=null deve retornar false")
    void equals_ThisValorNull_DeveRetornarFalse() {
        ExtratoResponseDTO d1 = criarDTOIdentico();
        d1.setValor(null);
        assertNotEquals(d1, criarDTOIdentico());
    }

    @Test
    @DisplayName("equals: this.saldoAntes==null e other!=null deve retornar false")
    void equals_ThisSaldoAntesNull_DeveRetornarFalse() {
        ExtratoResponseDTO d1 = criarDTOIdentico();
        d1.setSaldoAntes(null);
        assertNotEquals(d1, criarDTOIdentico());
    }

    @Test
    @DisplayName("equals: this.saldoDepois==null e other!=null deve retornar false")
    void equals_ThisSaldoDepoisNull_DeveRetornarFalse() {
        ExtratoResponseDTO d1 = criarDTOIdentico();
        d1.setSaldoDepois(null);
        assertNotEquals(d1, criarDTOIdentico());
    }

    @Test
    @DisplayName("equals: this.descricao==null e other!=null deve retornar false")
    void equals_ThisDescricaoNull_DeveRetornarFalse() {
        ExtratoResponseDTO d1 = criarDTOIdentico();
        d1.setDescricao(null);
        assertNotEquals(d1, criarDTOIdentico());
    }

    @Test
    @DisplayName("equals: this.dataHora==null e other!=null deve retornar false")
    void equals_ThisDataHoraNull_DeveRetornarFalse() {
        ExtratoResponseDTO d1 = criarDTOIdentico();
        d1.setDataHora(null);
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
        ExtratoResponseDTO d2 = criarDTOIdentico();
        d2.setValor(new BigDecimal("9999.99"));
        assertNotEquals(criarDTOIdentico().hashCode(), d2.hashCode());
    }

    @Test
    @DisplayName("hashCode: DTO com campos null não deve lançar exceção")
    void hashCode_CamposNull_NaoDeveLancarExcecao() {
        assertDoesNotThrow(() -> new ExtratoResponseDTO().hashCode());
    }

    @Test
    @DisplayName("hashCode: consistente em múltiplas chamadas")
    void hashCode_Consistente() {
        ExtratoResponseDTO dto = criarDTOIdentico();
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
                () -> assertTrue(result.contains("contaId")),
                () -> assertTrue(result.contains("pedidoId")),
                () -> assertTrue(result.contains("pagamentoId")),
                () -> assertTrue(result.contains("tipo")),
                () -> assertTrue(result.contains("valor")),
                () -> assertTrue(result.contains("saldoAntes")),
                () -> assertTrue(result.contains("saldoDepois")),
                () -> assertTrue(result.contains("descricao")),
                () -> assertTrue(result.contains("dataHora"))
        );
    }

    @Test
    @DisplayName("toString: DTO com campos null não deve lançar exceção")
    void toString_CamposNull_NaoDeveLancarExcecao() {
        assertDoesNotThrow(() -> new ExtratoResponseDTO().toString());
    }

    // =========================================================
    // TESTES: canEqual()
    // =========================================================

    @Test
    @DisplayName("canEqual: deve retornar true para instância do mesmo tipo")
    void canEqual_MesmoTipo_DeveRetornarTrue() {
        ExtratoResponseDTO dto1 = criarDTOIdentico();
        ExtratoResponseDTO dto2 = new ExtratoResponseDTO();
        assertTrue(dto1.canEqual(dto2));
    }

    @Test
    @DisplayName("canEqual: deve retornar false para tipo diferente")
    void canEqual_TipoDiferente_DeveRetornarFalse() {
        ExtratoResponseDTO dto = criarDTOIdentico();
        assertFalse(dto.canEqual("string"));
        assertFalse(dto.canEqual(42));
        assertFalse(dto.canEqual(null));
    }
}