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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("BoletoController - Cenários Positivos")
class BoletoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoletoService boletoService;

    private BoletoResponseDTO boletoResponse;

    @BeforeEach
    void setup() {
        boletoResponse = BoletoResponseDTO.builder()
                .id(1L)
                .codigoBarras("1234567890123")
                .valor(new BigDecimal("100.00"))
                .status("PENDENTE")
                .dataVencimento(LocalDate.now().plusDays(30))
                .build();
    }

    // -------------------------------------------------------
    // GET /api/boletos/{id}
    // -------------------------------------------------------

    @Test
    @DisplayName("✓ Deve buscar boleto por id e retornar status 200")
    void deveBuscarBoletoPorId() throws Exception {
        when(boletoService.buscarPorId(1L)).thenReturn(boletoResponse);

        mockMvc.perform(get("/api/boletos/1")
                .with(user("admin").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.codigoBarras", is("1234567890123")));
    }

    @Test
    @DisplayName("✓ Deve retornar valor e status no body ao buscar boleto por id")
    void deveBuscarBoletoPorIdRetornaBodyCompleto() throws Exception {
        when(boletoService.buscarPorId(1L)).thenReturn(boletoResponse);

        mockMvc.perform(get("/api/boletos/1")
                .with(user("admin").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor", is(100.00)))
                .andExpect(jsonPath("$.status", is("PENDENTE")));
    }

    @Test
    @DisplayName("✓ Deve chamar buscarPorId() com o id correto")
    void deveChamarServiceComIdCorreto() throws Exception {
        when(boletoService.buscarPorId(1L)).thenReturn(boletoResponse);

        mockMvc.perform(get("/api/boletos/1")
                .with(user("admin").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(boletoService, times(1)).buscarPorId(1L);
    }

    // -------------------------------------------------------
    // GET /api/boletos/pedido/{pedidoId}
    // -------------------------------------------------------

    @Test
    @DisplayName("✓ Deve buscar boleto por pedidoId e retornar status 200")
    void deveBuscarBoletoPorPedidoId() throws Exception {
        when(boletoService.buscarPorPedidoId(1L)).thenReturn(boletoResponse);

        mockMvc.perform(get("/api/boletos/pedido/1")
                .with(user("admin").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("✓ Deve retornar codigoBarras, valor e status no body ao buscar por pedidoId")
    void deveBuscarPorPedidoIdRetornaBodyCompleto() throws Exception {
        when(boletoService.buscarPorPedidoId(1L)).thenReturn(boletoResponse);

        mockMvc.perform(get("/api/boletos/pedido/1")
                .with(user("admin").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoBarras", is("1234567890123")))
                .andExpect(jsonPath("$.valor", is(100.00)))
                .andExpect(jsonPath("$.status", is("PENDENTE")));
    }

    @Test
    @DisplayName("✓ Deve chamar buscarPorPedidoId() com o pedidoId correto")
    void deveChamarServiceComPedidoIdCorreto() throws Exception {
        when(boletoService.buscarPorPedidoId(1L)).thenReturn(boletoResponse);

        mockMvc.perform(get("/api/boletos/pedido/1")
                .with(user("admin").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(boletoService, times(1)).buscarPorPedidoId(1L);
    }

    // -------------------------------------------------------
    // POST /api/boletos/gerar/{pedidoId}
    // -------------------------------------------------------

    @Test
    @DisplayName("✓ Deve gerar boleto e retornar status 201")
    void deveGerarBoleto() throws Exception {
        when(boletoService.gerar(1L)).thenReturn(boletoResponse);

        mockMvc.perform(post("/api/boletos/gerar/1")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("✓ Deve retornar codigoBarras e valor no body ao gerar boleto")
    void deveGerarBoletoRetornaBodyCompleto() throws Exception {
        when(boletoService.gerar(1L)).thenReturn(boletoResponse);

        mockMvc.perform(post("/api/boletos/gerar/1")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigoBarras", is("1234567890123")))
                .andExpect(jsonPath("$.valor", is(100.00)));
    }

    @Test
    @DisplayName("✓ Header Location deve apontar para /api/boletos/{id} do boleto gerado")
    void deveGerarBoletoComLocationCorreto() throws Exception {
        when(boletoService.gerar(1L)).thenReturn(boletoResponse);

        mockMvc.perform(post("/api/boletos/gerar/1")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/boletos/1")));
    }

    @Test
    @DisplayName("✓ Deve chamar gerar() com o pedidoId correto")
    void deveChamarGerarComPedidoIdCorreto() throws Exception {
        when(boletoService.gerar(1L)).thenReturn(boletoResponse);

        mockMvc.perform(post("/api/boletos/gerar/1")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(boletoService, times(1)).gerar(1L);
    }

    // -------------------------------------------------------
    // PATCH /api/boletos/{id}/pagar
    // -------------------------------------------------------

    @Test
    @DisplayName("✓ Deve pagar boleto e retornar status 200 com status PAGO")
    void devePagarBoleto() throws Exception {
        BoletoResponseDTO boletoPago = BoletoResponseDTO.builder()
                .id(1L)
                .codigoBarras("1234567890123")
                .valor(new BigDecimal("100.00"))
                .status("PAGO")
                .dataVencimento(LocalDate.now().plusDays(30))
                .build();

        when(boletoService.pagarBoleto(1L)).thenReturn(boletoPago);

        mockMvc.perform(patch("/api/boletos/1/pagar")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PAGO")));
    }

    @Test
    @DisplayName("✓ Deve retornar id e codigoBarras no body ao pagar boleto")
    void devePagarBoletoRetornaBodyCompleto() throws Exception {
        BoletoResponseDTO boletoPago = BoletoResponseDTO.builder()
                .id(1L)
                .codigoBarras("1234567890123")
                .valor(new BigDecimal("100.00"))
                .status("PAGO")
                .dataVencimento(LocalDate.now().plusDays(30))
                .build();

        when(boletoService.pagarBoleto(1L)).thenReturn(boletoPago);

        mockMvc.perform(patch("/api/boletos/1/pagar")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.codigoBarras", is("1234567890123")));
    }

    @Test
    @DisplayName("✓ Deve chamar pagarBoleto() com o id correto")
    void deveChamarPagarComIdCorreto() throws Exception {
        when(boletoService.pagarBoleto(1L)).thenReturn(boletoResponse);

        mockMvc.perform(patch("/api/boletos/1/pagar")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(boletoService, times(1)).pagarBoleto(1L);
    }

    // -------------------------------------------------------
    // PATCH /api/boletos/{id}/cancelar
    // -------------------------------------------------------

    @Test
    @DisplayName("✓ Deve cancelar boleto e retornar status 204 sem body")
    void deveCancelarBoleto() throws Exception {
        doNothing().when(boletoService).cancelarBoleto(1L);

        mockMvc.perform(patch("/api/boletos/1/cancelar")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("✓ Deve chamar cancelarBoleto() com o id correto")
    void deveChamarCancelarComIdCorreto() throws Exception {
        doNothing().when(boletoService).cancelarBoleto(1L);

        mockMvc.perform(patch("/api/boletos/1/cancelar")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(boletoService, times(1)).cancelarBoleto(1L);
    }
}