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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("BoletoController - Cenários Negativos")
class BoletoControllerNegativeTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoletoService boletoService;

    @Test
    void deveRetornarNotFoundAoBuscarBoletoInexistente() throws Exception {
        when(boletoService.buscarPorId(99L)).thenThrow(new RecursoNaoEncontradoException("Boleto não encontrado"));

        mockMvc.perform(get("/api/boletos/99")
                .with(user("admin").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Boleto não encontrado")));
    }

    @Test
    void deveRetornarBadRequestAoPagarBoletoComRegraInvalida() throws Exception {
        when(boletoService.pagarBoleto(99L)).thenThrow(new IllegalArgumentException("Boleto está cancelado"));

        mockMvc.perform(patch("/api/boletos/99/pagar")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Boleto está cancelado")));
    }
}
