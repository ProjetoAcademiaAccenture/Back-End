package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.response.BoletoResponseDTO;
import acc.br.projetoFinal.Accenture.service.BoletoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("BoletoController - Cenários Positivos")
class BoletoControllerPositiveTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoletoService boletoService;

    private BoletoResponseDTO boletoMock;

    @BeforeEach
    void setUp() {
        boletoMock = BoletoResponseDTO.builder()
                .id(1L)
                .pagamentoId(100L)
                .codigoBarras("12345678901234567890123456789012345678901234")
                .valor(new BigDecimal("190.00"))
                .dataVencimento(LocalDate.now().plusDays(3))
                .status("PENDENTE")
                .build();
    }

    // -------------------------------------------------------
    // GET /api/boletos/{id}
    // -------------------------------------------------------

    @Test
    @DisplayName("✓ Deve retornar 200 e boleto ao buscar por id existente")
    void deveRetornarBoletoAoBuscarPorId() throws Exception {
        when(boletoService.buscarPorId(1L)).thenReturn(boletoMock);

        mockMvc.perform(get("/api/boletos/1")
                .with(user("admin").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.pagamentoId", is(100)))
                .andExpect(jsonPath("$.valor", is(190.00)))
                .andExpect(jsonPath("$.status", is("PENDENTE")))
                .andExpect(jsonPath("$.codigoBarras", hasLength(44)));
    }

    // -------------------------------------------------------
    // GET /api/boletos/pedido/{pedidoId}
    // -------------------------------------------------------

    @Test
    @DisplayName("✓ Deve retornar 200 e boleto ao buscar por pedidoId existente")
    void deveRetornarBoletoAoBuscarPorPedidoId() throws Exception {
        when(boletoService.buscarPorPedidoId(10L)).thenReturn(boletoMock);

        mockMvc.perform(get("/api/boletos/pedido/10")
                .with(user("admin").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("PENDENTE")));
    }

    // -------------------------------------------------------
    // POST /api/boletos/gerar/{pagamentoId}
    // -------------------------------------------------------

    @Test
    @DisplayName("✓ Deve retornar 201 ao gerar boleto com sucesso")
    void deveGerarBoletoComSucesso() throws Exception {
        when(boletoService.gerar(100L)).thenReturn(boletoMock);

        mockMvc.perform(post("/api/boletos/gerar/100")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.pagamentoId", is(100)))
                .andExpect(jsonPath("$.valor", is(190.00)))
                .andExpect(jsonPath("$.status", is("PENDENTE")))
                .andExpect(jsonPath("$.codigoBarras", notNullValue()))
                .andExpect(jsonPath("$.dataVencimento", notNullValue()));
    }

    // -------------------------------------------------------
    // PATCH /api/boletos/{id}/pagar
    // -------------------------------------------------------

    @Test
    @DisplayName("✓ Deve retornar 200 ao pagar boleto no prazo")
    void devePagarBoletoNoPrazo() throws Exception {
        BoletoResponseDTO boletoPago = BoletoResponseDTO.builder()
                .id(1L)
                .pagamentoId(100L)
                .codigoBarras("12345678901234567890123456789012345678901234")
                .valor(new BigDecimal("190.00"))
                .dataVencimento(LocalDate.now().plusDays(3))
                .status("PAGO")
                .build();

        when(boletoService.pagarBoleto(eq(1L), anyString())).thenReturn(boletoPago);

        mockMvc.perform(patch("/api/boletos/1/pagar")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("PAGO")))
                .andExpect(jsonPath("$.valor", is(190.00)));
    }

    @Test
    @DisplayName("✓ Deve retornar 200 ao pagar boleto em atraso com multa de 2%")
    void devePagarBoletoEmAtrasoComMulta() throws Exception {
        BoletoResponseDTO boletoAtrasadoPago = BoletoResponseDTO.builder()
                .id(1L)
                .pagamentoId(100L)
                .codigoBarras("12345678901234567890123456789012345678901234")
                .valor(new BigDecimal("193.80"))
                .dataVencimento(LocalDate.now().minusDays(1))
                .status("PAGO")
                .build();

        when(boletoService.pagarBoleto(eq(1L), anyString())).thenReturn(boletoAtrasadoPago);

        mockMvc.perform(patch("/api/boletos/1/pagar")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PAGO")))
                .andExpect(jsonPath("$.valor", is(193.80)));
    }

    // -------------------------------------------------------
    // PATCH /api/boletos/{id}/cancelar
    // -------------------------------------------------------

    @Test
    @DisplayName("✓ Deve retornar 204 ao cancelar boleto com sucesso")
    void deveCancelarBoletoComSucesso() throws Exception {
        doNothing().when(boletoService).cancelarBoleto(1L);

        mockMvc.perform(patch("/api/boletos/1/cancelar")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(boletoService, times(1)).cancelarBoleto(1L);
    }
}