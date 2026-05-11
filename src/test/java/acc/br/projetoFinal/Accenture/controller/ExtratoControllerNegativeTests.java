package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.response.ExtratoResponseDTO;
import acc.br.projetoFinal.Accenture.enums.TipoExtrato;
import acc.br.projetoFinal.Accenture.service.ExtratoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ExtratoControllerNegativeTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExtratoService extratoService;

    @BeforeEach
    void setUp() {
        // Mock padrão: lista vazia para simular cenários negativos
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden quando não autenticado")
    void deveRetornar403QuandoNaoAutenticado() throws Exception {
        mockMvc.perform(get("/api/contas/1/extrato")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve retornar lista vazia para conta sem extratos")
    void deveRetornarListaVaziaParaContaSemExtratos() throws Exception {
        when(extratoService.listarPorConta(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/contas/1/extrato")
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve retornar lista vazia para conta inválida (ID negativo)")
    void deveRetornarListaVaziaParaContaComIdNegativo() throws Exception {
        when(extratoService.listarPorConta(-1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/contas/-1/extrato")
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve retornar lista vazia para conta inexistente")
    void deveRetornarListaVaziaParaContaInexistente() throws Exception {
        when(extratoService.listarPorConta(999999L)).thenReturn(List.of());

        mockMvc.perform(get("/api/contas/999999/extrato")
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve retornar lista vazia quando período não contém transações")
    void deveRetornarListaVaziaQuandoPeriodoNaoTemTransacoes() throws Exception {
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now().minusDays(25);

        when(extratoService.listarPorPeriodo(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/contas/1/extrato")
                .param("inicio", inicio.toString())
                .param("fim", fim.toString())
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve retornar lista vazia com período invertido")
    void deveRetornarListaVaziaComPeriodoInvertido() throws Exception {
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fim = LocalDateTime.now().minusDays(10);

        when(extratoService.listarPorPeriodo(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/contas/1/extrato")
                .param("inicio", inicio.toString())
                .param("fim", fim.toString())
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve retornar lista vazia para tipo ESTORNO sem resultados")
    void deveRetornarListaVaziaParaTipoEstornoSemResultados() throws Exception {
        when(extratoService.listarPorTipo(1L, TipoExtrato.ESTORNO)).thenReturn(List.of());

        mockMvc.perform(get("/api/contas/1/extrato")
                .param("tipo", "ESTORNO")
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve retornar lista vazia para tipo MULTA sem resultados")
    void deveRetornarListaVaziaParaTipoMultaSemResultados() throws Exception {
        when(extratoService.listarPorTipo(1L, TipoExtrato.MULTA)).thenReturn(List.of());

        mockMvc.perform(get("/api/contas/1/extrato")
                .param("tipo", "MULTA")
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve retornar lista vazia quando combinar período e tipo sem resultados")
    void deveRetornarListaVaziaAoCombinarPeriodoETipoSemResultados() throws Exception {
        LocalDateTime inicio = LocalDateTime.now().minusDays(10);
        LocalDateTime fim = LocalDateTime.now();

        when(extratoService.listarPorPeriodoETipo(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class), eq(TipoExtrato.ESTORNO)))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/contas/1/extrato")
                .param("inicio", inicio.toString())
                .param("fim", fim.toString())
                .param("tipo", "ESTORNO")
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve retornar lista vazia com tipo DEBITO e período sem resultados")
    void deveRetornarListaVaziaComTipoDebitoESemResultados() throws Exception {
        LocalDateTime inicio = LocalDateTime.now().minusDays(10);
        LocalDateTime fim = LocalDateTime.now();

        when(extratoService.listarPorPeriodoETipo(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class), eq(TipoExtrato.DEBITO)))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/contas/1/extrato")
                .param("inicio", inicio.toString())
                .param("fim", fim.toString())
                .param("tipo", "DEBITO")
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve retornar lista vazia com tipo CREDITO e período sem resultados")
    void deveRetornarListaVaziaComTipoCreditoESemResultados() throws Exception {
        LocalDateTime inicio = LocalDateTime.now().minusDays(10);
        LocalDateTime fim = LocalDateTime.now();

        when(extratoService.listarPorPeriodoETipo(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class), eq(TipoExtrato.CREDITO)))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/contas/1/extrato")
                .param("inicio", inicio.toString())
                .param("fim", fim.toString())
                .param("tipo", "CREDITO")
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve retornar lista vazia com período muito antigo")
    void deveRetornarListaVaziaComPeriodoMuitoAntigo() throws Exception {
        LocalDateTime inicio = LocalDateTime.now().minusYears(10);
        LocalDateTime fim = LocalDateTime.now().minusYears(5);

        when(extratoService.listarPorPeriodo(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/contas/1/extrato")
                .param("inicio", inicio.toString())
                .param("fim", fim.toString())
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve retornar lista vazia quando apenas início é fornecido sem fim")
    void deveRetornarListaVaziaQuandoApenasInicioDeFornecido() throws Exception {
        LocalDateTime inicio = LocalDateTime.now().minusDays(10);

        when(extratoService.listarPorConta(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/contas/1/extrato")
                .param("inicio", inicio.toString())
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve retornar lista vazia quando apenas fim é fornecido sem início")
    void deveRetornarListaVaziaQuandoApenasFinEFornecido() throws Exception {
        LocalDateTime fim = LocalDateTime.now();

        when(extratoService.listarPorConta(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/contas/1/extrato")
                .param("fim", fim.toString())
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve retornar lista vazia quando apenas tipo é fornecido sem período")
    void deveRetornarListaVaziaQuandoApenastipoEFornecido() throws Exception {
        when(extratoService.listarPorTipo(1L, TipoExtrato.DEBITO)).thenReturn(List.of());

        mockMvc.perform(get("/api/contas/1/extrato")
                .param("tipo", "DEBITO")
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

}
