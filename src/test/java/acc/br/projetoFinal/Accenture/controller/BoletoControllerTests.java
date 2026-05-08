package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.response.BoletoResponseDTO;
import acc.br.projetoFinal.Accenture.service.BoletoService;
import org.junit.jupiter.api.BeforeEach;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
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

    @Test
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
    void deveBuscarBoletoPorPedidoId() throws Exception {
        when(boletoService.buscarPorPedidoId(1L)).thenReturn(boletoResponse);

        mockMvc.perform(get("/api/boletos/pedido/1")
                .with(user("admin").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
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
    void deveCancelarBoleto() throws Exception {
        doNothing().when(boletoService).cancelarBoleto(1L);

        mockMvc.perform(patch("/api/boletos/1/cancelar")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
