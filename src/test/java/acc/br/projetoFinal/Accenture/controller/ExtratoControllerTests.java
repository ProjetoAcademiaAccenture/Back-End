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
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ExtratoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExtratoService extratoService;

    private ExtratoResponseDTO extrato1;
    private ExtratoResponseDTO extrato2;
    private ExtratoResponseDTO extrato3;

    @BeforeEach
    void setUp() {
        LocalDateTime agora = LocalDateTime.now();

        extrato1 = ExtratoResponseDTO.builder()
                .id(1L)
                .tipo("DEBITO")
                .valor(new BigDecimal("150.00"))
                .saldoAntes(new BigDecimal("5000.00"))
                .saldoDepois(new BigDecimal("4850.00"))
                .descricao("Pagamento do pedido #123")
                .dataHora(agora.minusDays(5))
                .build();

        extrato2 = ExtratoResponseDTO.builder()
                .id(2L)
                .tipo("CREDITO")
                .valor(new BigDecimal("200.00"))
                .saldoAntes(new BigDecimal("4850.00"))
                .saldoDepois(new BigDecimal("5050.00"))
                .descricao("Depósito")
                .dataHora(agora.minusDays(3))
                .build();

        extrato3 = ExtratoResponseDTO.builder()
                .id(3L)
                .tipo("DEBITO")
                .valor(new BigDecimal("100.00"))
                .saldoAntes(new BigDecimal("5050.00"))
                .saldoDepois(new BigDecimal("4950.00"))
                .descricao("Pagamento do pedido #124")
                .dataHora(agora.minusDays(1))
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve listar todos os extratos de uma conta sem parâmetros")
    void deveListarTodosOsExtratos() throws Exception {
        List<ExtratoResponseDTO> extratos = List.of(extrato3, extrato2, extrato1);
        when(extratoService.listarPorConta(1L)).thenReturn(extratos);

        mockMvc.perform(get("/api/contas/1/extrato")
                .with(user("user").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(3)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[2].id", is(1)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve listar extratos por período")
    void deveListarExtratosPorPeriodo() throws Exception {
        LocalDateTime inicio = LocalDateTime.now().minusDays(4);
        LocalDateTime fim = LocalDateTime.now().minusDays(2);

        List<ExtratoResponseDTO> extratos = List.of(extrato2);
        when(extratoService.listarPorPeriodo(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(extratos);

        mockMvc.perform(get("/api/contas/1/extrato")
                .param("inicio", inicio.toString())
                .param("fim", fim.toString())
                .with(user("user").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve listar extratos por tipo DÉBITO")
    void deveListarExtratosPorTipoDebito() throws Exception {
        List<ExtratoResponseDTO> extratos = List.of(extrato3, extrato1);
        when(extratoService.listarPorTipo(1L, TipoExtrato.DEBITO)).thenReturn(extratos);

        mockMvc.perform(get("/api/contas/1/extrato")
                .param("tipo", "DEBITO")
                .with(user("user").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].tipo", is("DEBITO")))
                .andExpect(jsonPath("$[1].tipo", is("DEBITO")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve listar extratos por tipo CRÉDITO")
    void deveListarExtratosPorTipoCredito() throws Exception {
        List<ExtratoResponseDTO> extratos = List.of(extrato2);
        when(extratoService.listarPorTipo(1L, TipoExtrato.CREDITO)).thenReturn(extratos);

        mockMvc.perform(get("/api/contas/1/extrato")
                .param("tipo", "CREDITO")
                .with(user("user").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tipo", is("CREDITO")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve listar extratos por período e tipo")
    void deveListarExtratosPorPeriodoETipo() throws Exception {
        LocalDateTime inicio = LocalDateTime.now().minusDays(4);
        LocalDateTime fim = LocalDateTime.now();

        List<ExtratoResponseDTO> extratos = List.of(extrato2);
        when(extratoService.listarPorPeriodoETipo(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class), eq(TipoExtrato.CREDITO)))
                .thenReturn(extratos);

        mockMvc.perform(get("/api/contas/1/extrato")
                .param("inicio", inicio.toString())
                .param("fim", fim.toString())
                .param("tipo", "CREDITO")
                .with(user("user").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tipo", is("CREDITO")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve retornar lista vazia quando não há extratos")
    void deveRetornarListaVaziaQuandoNaoHaExtratos() throws Exception {
        when(extratoService.listarPorConta(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/contas/1/extrato")
                .with(user("user").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve retornar lista vazia quando período não tem transações")
    void deveRetornarListaVaziaQuandoPeriodoNaoTemTransacoes() throws Exception {
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now().minusDays(25);

        when(extratoService.listarPorPeriodo(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/contas/1/extrato")
                .param("inicio", inicio.toString())
                .param("fim", fim.toString())
                .with(user("user").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve retornar status OK mesmo com lista vazia por tipo")
    void deveRetornarOkComListaVaziaQuandoTipoNaoEncontrado() throws Exception {
        when(extratoService.listarPorTipo(1L, TipoExtrato.ESTORNO)).thenReturn(List.of());

        mockMvc.perform(get("/api/contas/1/extrato")
                .param("tipo", "ESTORNO")
                .with(user("user").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve incluir valores e saldos na resposta")
    void deveIncluirValoresESaldosNaResposta() throws Exception {
        List<ExtratoResponseDTO> extratos = List.of(extrato1);
        when(extratoService.listarPorConta(1L)).thenReturn(extratos);

        mockMvc.perform(get("/api/contas/1/extrato")
                .with(user("user").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].valor", is(150.00)))
                .andExpect(jsonPath("$[0].saldoAntes", is(5000.00)))
                .andExpect(jsonPath("$[0].saldoDepois", is(4850.00)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deve incluir descrição do extrato na resposta")
    void deveIncluirDescricaoDoExtrato() throws Exception {
        List<ExtratoResponseDTO> extratos = List.of(extrato1);
        when(extratoService.listarPorConta(1L)).thenReturn(extratos);

        mockMvc.perform(get("/api/contas/1/extrato")
                .with(user("user").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descricao", is("Pagamento do pedido #123")));
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden quando não autenticado")
    void deveRetornar403QuandoNaoAutenticado() throws Exception {
        mockMvc.perform(get("/api/contas/1/extrato")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}
