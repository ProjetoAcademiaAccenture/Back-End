package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.enums.TipoConta;
import acc.br.projetoFinal.Accenture.repository.ContaRepository;
import acc.br.projetoFinal.Accenture.service.ContaService;
import acc.br.projetoFinal.Accenture.service.ExtratoService;
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
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("ContaController - Cenários Negativos")
class ContaControllerNegativeTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContaRepository contaRepository;

    @MockBean
    private ContaService contaService;

    @MockBean
    private ExtratoService extratoService;

    @Test
    void deveRetornarErroQuandoContaNaoExisteAoBuscarPorId() throws Exception {
        when(contaRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/contas/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deveRetornarErroQuandoContaNaoExisteAoDepositar() throws Exception {
        when(contaRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/contas/99/depositar")
            .param("valor", "500.00")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
