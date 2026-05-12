package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.enums.StatusBoleto;
import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import acc.br.projetoFinal.Accenture.model.Boleto;
import acc.br.projetoFinal.Accenture.model.Pedido;
import acc.br.projetoFinal.Accenture.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Cobertura Total - BoletoResponseDTO")
class BoletoResponseDTOTests {

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
                .id(10L)
                .cliente(cliente)
                .status(StatusPedido.RESERVADO)
                .build();

        boleto = Boleto.builder()
                .id(1L)
                .pedido(pedido)
                .codigoBarras("12345678901234567890123456789012345678901234")
                .valor(new BigDecimal("1500.00"))
                .dataVencimento(LocalDate.of(2026, 6, 30))
                .status(StatusBoleto.PENDENTE)
                .build();
    }

    // =========================================================
    // TESTES: fromEntity() — fluxo feliz para cada StatusBoleto
    // Cobre o branch de cada enum em boleto.getStatus().name()
    // =========================================================

    @Test
    @DisplayName("fromEntity: deve mapear todos os campos corretamente com status PENDENTE")
    void fromEntity_StatusPendente_DeveMappearTodosOsCampos() {
        boleto.setStatus(StatusBoleto.PENDENTE);

        BoletoResponseDTO dto = BoletoResponseDTO.fromEntity(boleto);

        assertAll("todos os campos mapeados",
                () -> assertEquals(1L,                                               dto.getId()),
                () -> assertEquals("12345678901234567890123456789012345678901234",    dto.getCodigoBarras()),
                () -> assertEquals(new BigDecimal("1500.00"),                        dto.getValor()),
                () -> assertEquals(LocalDate.of(2026, 6, 30),                       dto.getDataVencimento()),
                () -> assertEquals("PENDENTE",                                        dto.getStatus()),
                () -> assertEquals(10L,                                              dto.getPedidoId())
        );
    }

    @Test
    @DisplayName("fromEntity: deve mapear status PAGO corretamente")
    void fromEntity_StatusPago_DeveMappearStatus() {
        boleto.setStatus(StatusBoleto.PAGO);

        BoletoResponseDTO dto = BoletoResponseDTO.fromEntity(boleto);

        assertEquals("PAGO", dto.getStatus());
    }

    @Test
    @DisplayName("fromEntity: deve mapear status CANCELADO corretamente")
    void fromEntity_StatusCancelado_DeveMappearStatus() {
        boleto.setStatus(StatusBoleto.CANCELADO);

        BoletoResponseDTO dto = BoletoResponseDTO.fromEntity(boleto);

        assertEquals("CANCELADO", dto.getStatus());
    }

    @Test
    @DisplayName("fromEntity: deve mapear id do pedido corretamente")
    void fromEntity_DeveMappearPedidoId() {
        pedido.setId(99L);
        BoletoResponseDTO dto = BoletoResponseDTO.fromEntity(boleto);
        assertEquals(99L, dto.getPedidoId());
    }

    @Test
    @DisplayName("fromEntity: deve mapear valor com precisão decimal")
    void fromEntity_DeveMappearValorComPrecisao() {
        boleto.setValor(new BigDecimal("9999999.99"));
        BoletoResponseDTO dto = BoletoResponseDTO.fromEntity(boleto);
        assertEquals(new BigDecimal("9999999.99"), dto.getValor());
    }

    @Test
    @DisplayName("fromEntity: deve mapear dataVencimento corretamente")
    void fromEntity_DeveMappearDataVencimento() {
        LocalDate data = LocalDate.of(2025, 1, 15);
        boleto.setDataVencimento(data);
        BoletoResponseDTO dto = BoletoResponseDTO.fromEntity(boleto);
        assertEquals(data, dto.getDataVencimento());
    }

    @Test
    @DisplayName("fromEntity: deve retornar instância não nula")
    void fromEntity_DeveRetornarInstanciaNaoNula() {
        assertNotNull(BoletoResponseDTO.fromEntity(boleto));
    }

    // =========================================================
    // TESTES NEGATIVOS: fromEntity() com campos nulos/ausentes
    // Cobre branches de NullPointerException esperados
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
    @DisplayName("fromEntity: deve lançar exceção quando pedido é null")
    void fromEntity_PedidoNull_DeveLancarExcecao() {
        boleto.setPedido(null);
        assertThrows(NullPointerException.class,
                () -> BoletoResponseDTO.fromEntity(boleto));
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
    }

    @Test
    @DisplayName("AllArgsConstructor: deve criar instância com todos os campos")
    void allArgsConstructor_DeveCriarInstanciaCompleta() {
        LocalDate data = LocalDate.of(2026, 12, 31);
        BoletoResponseDTO dto = new BoletoResponseDTO(
                5L,
                "CODIGO_BARRAS_TESTE",
                new BigDecimal("200.00"),
                data,
                "PAGO",
                20L
        );

        assertAll("campos do AllArgsConstructor",
                () -> assertEquals(5L,                     dto.getId()),
                () -> assertEquals("CODIGO_BARRAS_TESTE",  dto.getCodigoBarras()),
                () -> assertEquals(new BigDecimal("200.00"), dto.getValor()),
                () -> assertEquals(data,                   dto.getDataVencimento()),
                () -> assertEquals("PAGO",                 dto.getStatus()),
                () -> assertEquals(20L,                    dto.getPedidoId())
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
                .dataVencimento(data)
                .status("CANCELADO")
                .pedidoId(15L)
                .build();

        assertAll("campos do builder",
                () -> assertEquals(7L,                    dto.getId()),
                () -> assertEquals("BUILDER_CODIGO",      dto.getCodigoBarras()),
                () -> assertEquals(new BigDecimal("350.75"), dto.getValor()),
                () -> assertEquals(data,                  dto.getDataVencimento()),
                () -> assertEquals("CANCELADO",           dto.getStatus()),
                () -> assertEquals(15L,                   dto.getPedidoId())
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
        dto.setDataVencimento(data);
        dto.setStatus("PENDENTE");
        dto.setPedidoId(5L);

        assertAll("setters e getters",
                () -> assertEquals(3L,                    dto.getId()),
                () -> assertEquals("SET_CODIGO",          dto.getCodigoBarras()),
                () -> assertEquals(new BigDecimal("100.00"), dto.getValor()),
                () -> assertEquals(data,                  dto.getDataVencimento()),
                () -> assertEquals("PENDENTE",            dto.getStatus()),
                () -> assertEquals(5L,                    dto.getPedidoId())
        );
    }

    // =========================================================
    // TESTES: equals(), hashCode() e toString()
    // @Data gera equals/hashCode usando TODOS os campos
    // =========================================================

    /** Cria um DTO com os mesmos valores do setUp(). */
    private BoletoResponseDTO criarDTOIdentico() {
        return BoletoResponseDTO.builder()
                .id(1L)
                .codigoBarras("12345678901234567890123456789012345678901234")
                .valor(new BigDecimal("1500.00"))
                .dataVencimento(LocalDate.of(2026, 6, 30))
                .status("PENDENTE")
                .pedidoId(10L)
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
    }

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
    @DisplayName("equals: status diferente deve retornar false")
    void equals_StatusDiferente_DeveRetornarFalse() {
        BoletoResponseDTO d2 = criarDTOIdentico();
        d2.setStatus("PAGO");
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
    @DisplayName("equals: dataVencimento diferente deve retornar false")
    void equals_DataVencimentoDiferente_DeveRetornarFalse() {
        BoletoResponseDTO d2 = criarDTOIdentico();
        d2.setDataVencimento(LocalDate.of(2099, 1, 1));
        assertNotEquals(criarDTOIdentico(), d2);
    }

    // --- branches null (this.campo == null, other.campo != null) ---

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

    @Test
    @DisplayName("equals: dois DTOs vazios devem ser iguais")
    void equals_AmbosVazios_DeveSerIgual() {
        assertEquals(new BoletoResponseDTO(), new BoletoResponseDTO());
    }

    // --- hashCode ---

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

    // --- toString ---

    @Test
    @DisplayName("toString: deve conter todos os campos principais")
    void toString_DeveConterCamposPrincipais() {
        BoletoResponseDTO dto = criarDTOIdentico();
        String result = dto.toString();
        assertAll("toString campos",
                () -> assertTrue(result.contains("id")),
                () -> assertTrue(result.contains("codigoBarras")),
                () -> assertTrue(result.contains("valor")),
                () -> assertTrue(result.contains("status")),
                () -> assertTrue(result.contains("pedidoId")),
                () -> assertTrue(result.contains("dataVencimento"))
        );
    }

    @Test
    @DisplayName("toString: DTO com campos null não deve lançar exceção")
    void toString_CamposNull_NaoDeveLancarExcecao() {
        assertDoesNotThrow(() -> new BoletoResponseDTO().toString());
    }
}