package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.service.BoletoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("BoletoController - Cenários Negativos")
class BoletoControllerNegativeTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoletoService boletoService;

    // -------------------------------------------------------
    // GET /api/boletos/{id}
    // -------------------------------------------------------

    @Test
    @DisplayName("✗ Deve retornar 404 ao buscar boleto com id inexistente")
    void deveRetornarNotFoundAoBuscarBoletoInexistente() throws Exception {
        when(boletoService.buscarPorId(99L)).thenThrow(new RecursoNaoEncontradoException("Boleto não encontrado"));

        mockMvc.perform(get("/api/boletos/99")
                .with(user("admin").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Boleto não encontrado")));
    }

    @Test
    @DisplayName("✗ Deve retornar 403 ao buscar boleto sem autenticação")
    void deveRetornarUnauthorizedAoBuscarSemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/boletos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    // -------------------------------------------------------
    // GET /api/boletos/pedido/{pedidoId}
    // -------------------------------------------------------

    @Test
    @DisplayName("✗ Deve retornar 404 ao buscar boleto com pedidoId inexistente")
    void deveRetornarNotFoundAoBuscarPorPedidoIdInexistente() throws Exception {
        when(boletoService.buscarPorPedidoId(99L)).thenThrow(new RecursoNaoEncontradoException("Pedido não encontrado"));

        mockMvc.perform(get("/api/boletos/pedido/99")
                .with(user("admin").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Pedido não encontrado")));
    }

    @Test
    @DisplayName("✗ Deve retornar 403 ao buscar boleto por pedidoId sem autenticação")
    void deveRetornarUnauthorizedAoBuscarPorPedidoIdSemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/boletos/pedido/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    // -------------------------------------------------------
    // POST /api/boletos/gerar/{pedidoId}
    // -------------------------------------------------------

    @Test
    @DisplayName("✗ Deve retornar 404 ao gerar boleto para pedido inexistente")
    void deveRetornarNotFoundAoGerarBoletoParaPedidoInexistente() throws Exception {
        when(boletoService.gerar(99L)).thenThrow(new RecursoNaoEncontradoException("Pedido não encontrado"));

        mockMvc.perform(post("/api/boletos/gerar/99")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Pedido não encontrado")));
    }

    @Test
    @DisplayName("✗ Deve retornar 400 ao gerar boleto com regra de negócio inválida")
    void deveRetornarBadRequestAoGerarBoletoComRegraInvalida() throws Exception {
        when(boletoService.gerar(1L)).thenThrow(new IllegalArgumentException("Pedido já possui boleto ativo"));

        mockMvc.perform(post("/api/boletos/gerar/1")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Pedido já possui boleto ativo")));
    }

    @Test
    @DisplayName("✗ Deve retornar 403 ao gerar boleto sem autenticação")
    void deveRetornarUnauthorizedAoGerarSemAutenticacao() throws Exception {
        mockMvc.perform(post("/api/boletos/gerar/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    // -------------------------------------------------------
    // PATCH /api/boletos/{id}/pagar
    // -------------------------------------------------------

    @Test
    @DisplayName("✗ Deve retornar 400 ao pagar boleto com regra inválida")
    void deveRetornarBadRequestAoPagarBoletoComRegraInvalida() throws Exception {
        when(boletoService.pagarBoleto(99L)).thenThrow(new IllegalArgumentException("Boleto está cancelado"));

        mockMvc.perform(patch("/api/boletos/99/pagar")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Boleto está cancelado")));
    }

    @Test
    @DisplayName("✗ Deve retornar 404 ao pagar boleto com id inexistente")
    void deveRetornarNotFoundAoPagarBoletoInexistente() throws Exception {
        when(boletoService.pagarBoleto(99L)).thenThrow(new RecursoNaoEncontradoException("Boleto não encontrado"));

        mockMvc.perform(patch("/api/boletos/99/pagar")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Boleto não encontrado")));
    }

    @Test
    @DisplayName("✗ Deve retornar 403 ao pagar boleto sem autenticação")
    void deveRetornarUnauthorizedAoPagarSemAutenticacao() throws Exception {
        mockMvc.perform(patch("/api/boletos/1/pagar")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    // -------------------------------------------------------
    // PATCH /api/boletos/{id}/cancelar
    // -------------------------------------------------------

    @Test
    @DisplayName("✗ Deve retornar 404 ao cancelar boleto com id inexistente")
    void deveRetornarNotFoundAoCancelarBoletoInexistente() throws Exception {
        doThrow(new RecursoNaoEncontradoException("Boleto não encontrado"))
                .when(boletoService).cancelarBoleto(99L);

        mockMvc.perform(patch("/api/boletos/99/cancelar")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Boleto não encontrado")));
    }

    @Test
    @DisplayName("✗ Deve retornar 400 ao cancelar boleto com regra inválida")
    void deveRetornarBadRequestAoCancelarBoletoComRegraInvalida() throws Exception {
        doThrow(new IllegalArgumentException("Boleto já foi pago"))
                .when(boletoService).cancelarBoleto(1L);

        mockMvc.perform(patch("/api/boletos/1/cancelar")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Boleto já foi pago")));
    }

    @Test
    @DisplayName("✗ Deve retornar 403 ao cancelar boleto sem autenticação")
    void deveRetornarUnauthorizedAoCancelarSemAutenticacao() throws Exception {
        mockMvc.perform(patch("/api/boletos/1/cancelar")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}