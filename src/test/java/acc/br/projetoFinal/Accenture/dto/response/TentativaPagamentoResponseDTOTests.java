package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
import acc.br.projetoFinal.Accenture.enums.StatusPagamento;
import acc.br.projetoFinal.Accenture.model.Pagamento;
import acc.br.projetoFinal.Accenture.model.TentativaPagamento;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TentativaPagamentoResponseDTOTests {

    private TentativaPagamento buildTentativa(Long id, Long pagamentoId,
                                              MetodoPagamento metodo, StatusPagamento status,
                                              BigDecimal valor, String mensagem,
                                              LocalDateTime data) {
        Pagamento pagamento = new Pagamento();
        pagamento.setId(pagamentoId);

        TentativaPagamento tentativa = new TentativaPagamento();
        tentativa.setId(id);
        tentativa.setPagamento(pagamento);
        tentativa.setMetodo(metodo);
        tentativa.setStatus(status);
        tentativa.setValorTentado(valor);
        tentativa.setMensagem(mensagem);
        tentativa.setDataTentativa(data);

        return tentativa;
    }

    // ─── fromEntity: happy path ───────────────────────────────────────────────

    @Test
    void fromEntity_ShouldMapAllFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        TentativaPagamento tentativa = buildTentativa(
            1L, 10L, MetodoPagamento.PIX, StatusPagamento.APROVADO,
            new BigDecimal("250.00"), "Pagamento aprovado", now
        );

        TentativaPagamentoResponseDTO dto = TentativaPagamentoResponseDTO.fromEntity(tentativa);

        assertAll(
            () -> assertEquals(1L,                        dto.getId()),
            () -> assertEquals(10L,                       dto.getPagamentoId()),
            () -> assertEquals(MetodoPagamento.PIX,       dto.getMetodoPagamento()),
            () -> assertEquals(StatusPagamento.APROVADO,  dto.getStatus()),
            () -> assertEquals(new BigDecimal("250.00"),  dto.getValorTentado()),
            () -> assertEquals("Pagamento aprovado",      dto.getMensagem()),
            () -> assertEquals(now,                       dto.getDataTentativa())
        );
    }

    @Test
    void fromEntity_ShouldMapCorrectly_WhenStatusIsReprovado() {
        LocalDateTime now = LocalDateTime.now();
        TentativaPagamento tentativa = buildTentativa(
            2L, 20L, MetodoPagamento.CREDITO, StatusPagamento.RECUSADO,
            new BigDecimal("100.00"), "Saldo insuficiente", now
        );

        TentativaPagamentoResponseDTO dto = TentativaPagamentoResponseDTO.fromEntity(tentativa);

        assertAll(
            () -> assertEquals(StatusPagamento.RECUSADO,        dto.getStatus()),
            () -> assertEquals(MetodoPagamento.CREDITO,   dto.getMetodoPagamento()),
            () -> assertEquals("Saldo insuficiente",             dto.getMensagem())
        );
    }

    @Test
    void fromEntity_ShouldMapCorrectly_WhenMensagemIsNull() {
        TentativaPagamento tentativa = buildTentativa(
            3L, 30L, MetodoPagamento.BOLETO, StatusPagamento.PENDENTE,
            new BigDecimal("50.00"), null, LocalDateTime.now()
        );

        TentativaPagamentoResponseDTO dto = TentativaPagamentoResponseDTO.fromEntity(tentativa);

        assertNull(dto.getMensagem());
    }

    @Test
    void fromEntity_ShouldMapCorrectly_WhenDataTentativaIsNull() {
        TentativaPagamento tentativa = buildTentativa(
            4L, 40L, MetodoPagamento.PIX, StatusPagamento.PENDENTE,
            new BigDecimal("75.00"), "Pendente", null
        );

        TentativaPagamentoResponseDTO dto = TentativaPagamentoResponseDTO.fromEntity(tentativa);

        assertNull(dto.getDataTentativa());
    }

    // ─── fromEntity: exceções ─────────────────────────────────────────────────

    @Test
    void fromEntity_ShouldThrow_WhenTentativaIsNull() {
        assertThrows(NullPointerException.class,
            () -> TentativaPagamentoResponseDTO.fromEntity(null));
    }

    @Test
    void fromEntity_ShouldThrow_WhenPagamentoIsNull() {
        TentativaPagamento tentativa = new TentativaPagamento();
        tentativa.setId(5L);
        tentativa.setPagamento(null);
        tentativa.setMetodo(MetodoPagamento.PIX);
        tentativa.setStatus(StatusPagamento.APROVADO);
        tentativa.setValorTentado(new BigDecimal("10.00"));

        assertThrows(NullPointerException.class,
            () -> TentativaPagamentoResponseDTO.fromEntity(tentativa));
    }

    // ─── Construtores ─────────────────────────────────────────────────────────

    @Test
    void noArgsConstructor_ShouldCreateEmptyDTO() {
        TentativaPagamentoResponseDTO dto = new TentativaPagamentoResponseDTO();

        assertAll(
            () -> assertNull(dto.getId()),
            () -> assertNull(dto.getPagamentoId()),
            () -> assertNull(dto.getMetodoPagamento()),
            () -> assertNull(dto.getStatus()),
            () -> assertNull(dto.getValorTentado()),
            () -> assertNull(dto.getMensagem()),
            () -> assertNull(dto.getDataTentativa())
        );
    }

    @Test
    void allArgsConstructor_ShouldCreateDTOWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        TentativaPagamentoResponseDTO dto = new TentativaPagamentoResponseDTO(
            1L, 10L, MetodoPagamento.PIX, StatusPagamento.APROVADO,
            new BigDecimal("200.00"), "OK", now
        );

        assertAll(
            () -> assertEquals(1L,                       dto.getId()),
            () -> assertEquals(10L,                      dto.getPagamentoId()),
            () -> assertEquals(MetodoPagamento.PIX,      dto.getMetodoPagamento()),
            () -> assertEquals(StatusPagamento.APROVADO, dto.getStatus()),
            () -> assertEquals(new BigDecimal("200.00"), dto.getValorTentado()),
            () -> assertEquals("OK",                     dto.getMensagem()),
            () -> assertEquals(now,                      dto.getDataTentativa())
        );
    }

    @Test
    void builder_ShouldCreateDTOWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        TentativaPagamentoResponseDTO dto = TentativaPagamentoResponseDTO.builder()
            .id(2L)
            .pagamentoId(20L)
            .metodoPagamento(MetodoPagamento.BOLETO)
            .status(StatusPagamento.RECUSADO)
            .valorTentado(new BigDecimal("300.00"))
            .mensagem("Recusado")
            .dataTentativa(now)
            .build();

        assertAll(
            () -> assertEquals(2L,                        dto.getId()),
            () -> assertEquals(20L,                       dto.getPagamentoId()),
            () -> assertEquals(MetodoPagamento.BOLETO,    dto.getMetodoPagamento()),
            () -> assertEquals(StatusPagamento.RECUSADO, dto.getStatus()),
            () -> assertEquals(new BigDecimal("300.00"),  dto.getValorTentado()),
            () -> assertEquals("Recusado",                dto.getMensagem()),
            () -> assertEquals(now,                       dto.getDataTentativa())
        );
    }

    // ─── Setters (@Data) ──────────────────────────────────────────────────────

    @Test
    void setters_ShouldUpdateFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        TentativaPagamentoResponseDTO dto = new TentativaPagamentoResponseDTO();

        dto.setId(9L);
        dto.setPagamentoId(90L);
        dto.setMetodoPagamento(MetodoPagamento.CREDITO);
        dto.setStatus(StatusPagamento.PENDENTE);
        dto.setValorTentado(new BigDecimal("999.99"));
        dto.setMensagem("Atualizado");
        dto.setDataTentativa(now);

        assertAll(
            () -> assertEquals(9L,                          dto.getId()),
            () -> assertEquals(90L,                         dto.getPagamentoId()),
            () -> assertEquals(MetodoPagamento.CREDITO, dto.getMetodoPagamento()),
            () -> assertEquals(StatusPagamento.PENDENTE,    dto.getStatus()),
            () -> assertEquals(new BigDecimal("999.99"),    dto.getValorTentado()),
            () -> assertEquals("Atualizado",                dto.getMensagem()),
            () -> assertEquals(now,                         dto.getDataTentativa())
        );
    }

    // ─── equals / hashCode ────────────────────────────────────────────────────

    @Test
    void equals_ShouldReturnTrue_ForEqualDTOs() {
        LocalDateTime now = LocalDateTime.now();
        TentativaPagamentoResponseDTO dto1 = new TentativaPagamentoResponseDTO(
            1L, 10L, MetodoPagamento.PIX, StatusPagamento.APROVADO,
            new BigDecimal("100.00"), "OK", now);
        TentativaPagamentoResponseDTO dto2 = new TentativaPagamentoResponseDTO(
            1L, 10L, MetodoPagamento.PIX, StatusPagamento.APROVADO,
            new BigDecimal("100.00"), "OK", now);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void equals_ShouldReturnFalse_WhenIdsDiffer() {
        LocalDateTime now = LocalDateTime.now();
        TentativaPagamentoResponseDTO dto1 = new TentativaPagamentoResponseDTO(
            1L, 10L, MetodoPagamento.PIX, StatusPagamento.APROVADO,
            new BigDecimal("100.00"), "OK", now);
        TentativaPagamentoResponseDTO dto2 = new TentativaPagamentoResponseDTO(
            2L, 10L, MetodoPagamento.PIX, StatusPagamento.APROVADO,
            new BigDecimal("100.00"), "OK", now);

        assertNotEquals(dto1, dto2);
    }

    @Test
    void equals_ShouldReturnFalse_WhenComparedToNull() {
        TentativaPagamentoResponseDTO dto = new TentativaPagamentoResponseDTO(
            1L, 10L, MetodoPagamento.PIX, StatusPagamento.APROVADO,
            new BigDecimal("100.00"), "OK", LocalDateTime.now());

        assertNotEquals(null, dto);
    }

    @Test
    void equals_ShouldReturnFalse_WhenComparedToDifferentType() {
        TentativaPagamentoResponseDTO dto = new TentativaPagamentoResponseDTO(
            1L, 10L, MetodoPagamento.PIX, StatusPagamento.APROVADO,
            new BigDecimal("100.00"), "OK", LocalDateTime.now());

        assertNotEquals("string", dto);
    }

    @Test
    void equals_ShouldReturnTrue_WithItself() {
        TentativaPagamentoResponseDTO dto = new TentativaPagamentoResponseDTO(
            1L, 10L, MetodoPagamento.PIX, StatusPagamento.APROVADO,
            new BigDecimal("100.00"), "OK", LocalDateTime.now());

        assertEquals(dto, dto);
    }

    // ─── toString ─────────────────────────────────────────────────────────────

    @Test
    void toString_ShouldContainAllFieldValues() {
        LocalDateTime now = LocalDateTime.now();
        TentativaPagamentoResponseDTO dto = new TentativaPagamentoResponseDTO(
            1L, 10L, MetodoPagamento.PIX, StatusPagamento.APROVADO,
            new BigDecimal("100.00"), "OK", now);

        String result = dto.toString();

        assertAll(
            () -> assertTrue(result.contains("1")),
            () -> assertTrue(result.contains("PIX")),
            () -> assertTrue(result.contains("APROVADO")),
            () -> assertTrue(result.contains("100.00")),
            () -> assertTrue(result.contains("OK"))
        );
    }
}