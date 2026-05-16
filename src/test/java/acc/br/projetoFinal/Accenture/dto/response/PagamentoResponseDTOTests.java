package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
import acc.br.projetoFinal.Accenture.enums.StatusPagamento;
import acc.br.projetoFinal.Accenture.model.Pagamento;
import acc.br.projetoFinal.Accenture.model.Pedido;
import acc.br.projetoFinal.Accenture.model.TentativaPagamento;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoResponseDTOTests {

    // ─── Helper ───────────────────────────────────────────────────────────────

    private Pagamento buildPagamento(Long id, Long pedidoId,
                                     MetodoPagamento metodo, StatusPagamento status,
                                     BigDecimal valorBruto, BigDecimal desconto,
                                     BigDecimal valorFinal, LocalDateTime dataCriacao,
                                     LocalDateTime dataConclusao,
                                     List<TentativaPagamento> tentativas) {
        Pedido pedido = new Pedido();
        pedido.setId(pedidoId);

        Pagamento pagamento = new Pagamento();
        pagamento.setId(id);
        pagamento.setPedido(pedido);
        pagamento.setMetodo(metodo);
        pagamento.setStatus(status);
        pagamento.setValorBruto(valorBruto);
        pagamento.setDesconto(desconto);
        pagamento.setValorFinal(valorFinal);
        pagamento.setDataCriacao(dataCriacao);
        pagamento.setDataConclusao(dataConclusao);
        pagamento.setTentativas(tentativas);

        return pagamento;
    }

    private TentativaPagamento buildTentativa(Long id, Pagamento pagamento,
                                               MetodoPagamento metodo, StatusPagamento status,
                                               BigDecimal valor, String mensagem,
                                               LocalDateTime data) {
        TentativaPagamento t = new TentativaPagamento();
        t.setId(id);
        t.setPagamento(pagamento);
        t.setMetodo(metodo);
        t.setStatus(status);
        t.setValorTentado(valor);
        t.setMensagem(mensagem);
        t.setDataTentativa(data);
        return t;
    }

    // ─── fromEntity: happy path ───────────────────────────────────────────────

    @Test
    void fromEntity_ShouldMapAllFieldsCorrectly() {
        LocalDateTime criacao   = LocalDateTime.now();
        LocalDateTime conclusao = LocalDateTime.now().plusHours(1);

        Pagamento pagamento = buildPagamento(
            1L, 10L, MetodoPagamento.PIX, StatusPagamento.APROVADO,
            new BigDecimal("500.00"), new BigDecimal("50.00"),
            new BigDecimal("450.00"), criacao, conclusao, List.of()
        );

        PagamentoResponseDTO dto = PagamentoResponseDTO.fromEntity(pagamento);

        assertAll(
            () -> assertEquals(1L,                        dto.getId()),
            () -> assertEquals(10L,                       dto.getPedidoId()),
            () -> assertEquals(MetodoPagamento.PIX,       dto.getMetodoPagamento()),
            () -> assertEquals(StatusPagamento.APROVADO,  dto.getStatus()),
            () -> assertEquals(new BigDecimal("500.00"),  dto.getValorBruto()),
            () -> assertEquals(new BigDecimal("50.00"),   dto.getDesconto()),
            () -> assertEquals(new BigDecimal("450.00"),  dto.getValorFinal()),
            () -> assertEquals(criacao,                   dto.getDataCriacao()),
            () -> assertEquals(conclusao,                 dto.getDataConclusao()),
            () -> assertNotNull(dto.getTentativas()),
            () -> assertTrue(dto.getTentativas().isEmpty())
        );
    }

    @Test
    void fromEntity_ShouldMapTentativas_WhenListIsNotEmpty() {
        LocalDateTime now = LocalDateTime.now();

        Pagamento pagamento = buildPagamento(
            2L, 20L, MetodoPagamento.BOLETO, StatusPagamento.RECUSADO,
            new BigDecimal("200.00"), BigDecimal.ZERO,
            new BigDecimal("200.00"), now, null, null
        );

        TentativaPagamento t1 = buildTentativa(
            1L, pagamento, MetodoPagamento.BOLETO,
            StatusPagamento.RECUSADO, new BigDecimal("200.00"), "Falhou", now
        );
        TentativaPagamento t2 = buildTentativa(
            2L, pagamento, MetodoPagamento.PIX,
            StatusPagamento.APROVADO, new BigDecimal("200.00"), "OK", now
        );

        pagamento.setTentativas(List.of(t1, t2));

        PagamentoResponseDTO dto = PagamentoResponseDTO.fromEntity(pagamento);

        assertAll(
            () -> assertEquals(2,           dto.getTentativas().size()),
            () -> assertEquals(1L,          dto.getTentativas().get(0).getId()),
            () -> assertEquals(2L,          dto.getTentativas().get(1).getId()),
            () -> assertEquals("Falhou",    dto.getTentativas().get(0).getMensagem()),
            () -> assertEquals("OK",        dto.getTentativas().get(1).getMensagem())
        );
    }

    @Test
    void fromEntity_ShouldReturnEmptyList_WhenTentativasIsNull() {
        Pagamento pagamento = buildPagamento(
            3L, 30L, MetodoPagamento.PIX, StatusPagamento.PENDENTE,
            new BigDecimal("100.00"), BigDecimal.ZERO,
            new BigDecimal("100.00"), LocalDateTime.now(), null, null
        );

        PagamentoResponseDTO dto = PagamentoResponseDTO.fromEntity(pagamento);

        assertNotNull(dto.getTentativas());
        assertTrue(dto.getTentativas().isEmpty());
    }

    @Test
    void fromEntity_ShouldMapCorrectly_WhenDataConclusaoIsNull() {
        Pagamento pagamento = buildPagamento(
            4L, 40L, MetodoPagamento.CREDITO, StatusPagamento.PENDENTE,
            new BigDecimal("300.00"), new BigDecimal("10.00"),
            new BigDecimal("290.00"), LocalDateTime.now(), null, List.of()
        );

        PagamentoResponseDTO dto = PagamentoResponseDTO.fromEntity(pagamento);

        assertNull(dto.getDataConclusao());
    }

    @Test
    void fromEntity_ShouldMapCorrectly_WhenDescontoIsZero() {
        Pagamento pagamento = buildPagamento(
            5L, 50L, MetodoPagamento.PIX, StatusPagamento.APROVADO,
            new BigDecimal("150.00"), BigDecimal.ZERO,
            new BigDecimal("150.00"), LocalDateTime.now(), LocalDateTime.now(), List.of()
        );

        PagamentoResponseDTO dto = PagamentoResponseDTO.fromEntity(pagamento);

        assertEquals(BigDecimal.ZERO, dto.getDesconto());
        assertEquals(new BigDecimal("150.00"), dto.getValorFinal());
    }

    // ─── fromEntity: exceções ─────────────────────────────────────────────────

    @Test
    void fromEntity_ShouldThrow_WhenPagamentoIsNull() {
        assertThrows(NullPointerException.class,
            () -> PagamentoResponseDTO.fromEntity(null));
    }

    @Test
    void fromEntity_ShouldThrow_WhenPedidoIsNull() {
        Pagamento pagamento = new Pagamento();
        pagamento.setId(1L);
        pagamento.setPedido(null);

        assertThrows(NullPointerException.class,
            () -> PagamentoResponseDTO.fromEntity(pagamento));
    }

    // ─── Construtores ─────────────────────────────────────────────────────────

    @Test
    void noArgsConstructor_ShouldCreateEmptyDTO() {
        PagamentoResponseDTO dto = new PagamentoResponseDTO();

        assertAll(
            () -> assertNull(dto.getId()),
            () -> assertNull(dto.getPedidoId()),
            () -> assertNull(dto.getMetodoPagamento()),
            () -> assertNull(dto.getStatus()),
            () -> assertNull(dto.getValorBruto()),
            () -> assertNull(dto.getDesconto()),
            () -> assertNull(dto.getValorFinal()),
            () -> assertNull(dto.getDataCriacao()),
            () -> assertNull(dto.getDataConclusao()),
            () -> assertNull(dto.getTentativas())
        );
    }

    @Test
    void allArgsConstructor_ShouldCreateDTOWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        List<TentativaPagamentoResponseDTO> tentativas = List.of();

        PagamentoResponseDTO dto = new PagamentoResponseDTO(
            1L, 10L, MetodoPagamento.PIX, StatusPagamento.APROVADO,
            new BigDecimal("500.00"), new BigDecimal("50.00"),
            new BigDecimal("450.00"), now, now, tentativas
        );

        assertAll(
            () -> assertEquals(1L,                        dto.getId()),
            () -> assertEquals(10L,                       dto.getPedidoId()),
            () -> assertEquals(MetodoPagamento.PIX,       dto.getMetodoPagamento()),
            () -> assertEquals(StatusPagamento.APROVADO,  dto.getStatus()),
            () -> assertEquals(new BigDecimal("500.00"),  dto.getValorBruto()),
            () -> assertEquals(new BigDecimal("50.00"),   dto.getDesconto()),
            () -> assertEquals(new BigDecimal("450.00"),  dto.getValorFinal()),
            () -> assertEquals(now,                       dto.getDataCriacao()),
            () -> assertEquals(now,                       dto.getDataConclusao()),
            () -> assertEquals(tentativas,                dto.getTentativas())
        );
    }

    @Test
    void builder_ShouldCreateDTOWithAllFields() {
        LocalDateTime now = LocalDateTime.now();

        PagamentoResponseDTO dto = PagamentoResponseDTO.builder()
            .id(2L)
            .pedidoId(20L)
            .metodoPagamento(MetodoPagamento.BOLETO)
            .status(StatusPagamento.RECUSADO)
            .valorBruto(new BigDecimal("400.00"))
            .desconto(new BigDecimal("40.00"))
            .valorFinal(new BigDecimal("360.00"))
            .dataCriacao(now)
            .dataConclusao(now)
            .tentativas(List.of())
            .build();

        assertAll(
            () -> assertEquals(2L,                        dto.getId()),
            () -> assertEquals(20L,                       dto.getPedidoId()),
            () -> assertEquals(MetodoPagamento.BOLETO,    dto.getMetodoPagamento()),
            () -> assertEquals(StatusPagamento.RECUSADO, dto.getStatus()),
            () -> assertEquals(new BigDecimal("400.00"),  dto.getValorBruto()),
            () -> assertEquals(new BigDecimal("40.00"),   dto.getDesconto()),
            () -> assertEquals(new BigDecimal("360.00"),  dto.getValorFinal()),
            () -> assertEquals(now,                       dto.getDataCriacao()),
            () -> assertEquals(now,                       dto.getDataConclusao()),
            () -> assertTrue(dto.getTentativas().isEmpty())
        );
    }

    // ─── Setters (@Data) ──────────────────────────────────────────────────────

    @Test
    void setters_ShouldUpdateAllFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        PagamentoResponseDTO dto = new PagamentoResponseDTO();

        dto.setId(9L);
        dto.setPedidoId(90L);
        dto.setMetodoPagamento(MetodoPagamento.DEBITO);
        dto.setStatus(StatusPagamento.PENDENTE);
        dto.setValorBruto(new BigDecimal("999.00"));
        dto.setDesconto(new BigDecimal("99.00"));
        dto.setValorFinal(new BigDecimal("900.00"));
        dto.setDataCriacao(now);
        dto.setDataConclusao(now);
        dto.setTentativas(List.of());

        assertAll(
            () -> assertEquals(9L,                            dto.getId()),
            () -> assertEquals(90L,                           dto.getPedidoId()),
            () -> assertEquals(MetodoPagamento.DEBITO, dto.getMetodoPagamento()),
            () -> assertEquals(StatusPagamento.PENDENTE,      dto.getStatus()),
            () -> assertEquals(new BigDecimal("999.00"),      dto.getValorBruto()),
            () -> assertEquals(new BigDecimal("99.00"),       dto.getDesconto()),
            () -> assertEquals(new BigDecimal("900.00"),      dto.getValorFinal()),
            () -> assertEquals(now,                           dto.getDataCriacao()),
            () -> assertEquals(now,                           dto.getDataConclusao()),
            () -> assertTrue(dto.getTentativas().isEmpty())
        );
    }

    // ─── equals / hashCode ────────────────────────────────────────────────────

    @Test
    void equals_ShouldReturnTrue_ForEqualDTOs() {
        LocalDateTime now = LocalDateTime.now();

        PagamentoResponseDTO dto1 = new PagamentoResponseDTO(
            1L, 10L, MetodoPagamento.PIX, StatusPagamento.APROVADO,
            new BigDecimal("100.00"), BigDecimal.ZERO,
            new BigDecimal("100.00"), now, now, List.of());
        PagamentoResponseDTO dto2 = new PagamentoResponseDTO(
            1L, 10L, MetodoPagamento.PIX, StatusPagamento.APROVADO,
            new BigDecimal("100.00"), BigDecimal.ZERO,
            new BigDecimal("100.00"), now, now, List.of());

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void equals_ShouldReturnTrue_WithItself() {
        PagamentoResponseDTO dto = new PagamentoResponseDTO(
            1L, 10L, MetodoPagamento.PIX, StatusPagamento.APROVADO,
            new BigDecimal("100.00"), BigDecimal.ZERO,
            new BigDecimal("100.00"), LocalDateTime.now(), null, List.of());

        assertEquals(dto, dto);
    }

    @Test
    void equals_ShouldReturnFalse_WhenIdsDiffer() {
        LocalDateTime now = LocalDateTime.now();

        PagamentoResponseDTO dto1 = new PagamentoResponseDTO(
            1L, 10L, MetodoPagamento.PIX, StatusPagamento.APROVADO,
            new BigDecimal("100.00"), BigDecimal.ZERO,
            new BigDecimal("100.00"), now, now, List.of());
        PagamentoResponseDTO dto2 = new PagamentoResponseDTO(
            2L, 10L, MetodoPagamento.PIX, StatusPagamento.APROVADO,
            new BigDecimal("100.00"), BigDecimal.ZERO,
            new BigDecimal("100.00"), now, now, List.of());

        assertNotEquals(dto1, dto2);
    }

    @Test
    void equals_ShouldReturnFalse_WhenComparedToNull() {
        PagamentoResponseDTO dto = new PagamentoResponseDTO(
            1L, 10L, MetodoPagamento.PIX, StatusPagamento.APROVADO,
            new BigDecimal("100.00"), BigDecimal.ZERO,
            new BigDecimal("100.00"), LocalDateTime.now(), null, List.of());

        assertNotEquals(null, dto);
    }

    @Test
    void equals_ShouldReturnFalse_WhenComparedToDifferentType() {
        PagamentoResponseDTO dto = new PagamentoResponseDTO(
            1L, 10L, MetodoPagamento.PIX, StatusPagamento.APROVADO,
            new BigDecimal("100.00"), BigDecimal.ZERO,
            new BigDecimal("100.00"), LocalDateTime.now(), null, List.of());

        assertNotEquals("string", dto);
    }

    // ─── toString ─────────────────────────────────────────────────────────────

    @Test
    void toString_ShouldContainAllRelevantValues() {
        LocalDateTime now = LocalDateTime.now();

        PagamentoResponseDTO dto = new PagamentoResponseDTO(
            1L, 10L, MetodoPagamento.PIX, StatusPagamento.APROVADO,
            new BigDecimal("500.00"), new BigDecimal("50.00"),
            new BigDecimal("450.00"), now, now, List.of());

        String result = dto.toString();

        assertAll(
            () -> assertTrue(result.contains("1")),
            () -> assertTrue(result.contains("PIX")),
            () -> assertTrue(result.contains("APROVADO")),
            () -> assertTrue(result.contains("500.00")),
            () -> assertTrue(result.contains("50.00")),
            () -> assertTrue(result.contains("450.00"))
        );
    }
    
}