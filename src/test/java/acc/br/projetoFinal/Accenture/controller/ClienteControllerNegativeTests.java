package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.request.ClienteRequestDTO;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("ClienteController - Cenários Negativos")
class ClienteControllerNegativeTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClienteService clienteService;

    private ClienteRequestDTO clienteRequestInvalido;

    @BeforeEach
    void setup() {
        clienteRequestInvalido = ClienteRequestDTO.builder()
                .nome("João Silva")
                .cpf("12345678901")
                .email("email-invalido") 
                .senha("123456")
                .endereco(acc.br.projetoFinal.Accenture.dto.request.EnderecoRequestDTO.builder()
                        .cep("01310100")
                        .numero("100")
                        .build())
                .build();
    }

    @Test
    void deveRetornarBadRequestAoCriarClienteComDadosInvalidos() throws Exception {
        mockMvc.perform(post("/api/clientes")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteRequestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validação de Entrada")));
    }

    @Test
    void deveRetornarNotFoundAoBuscarClienteInexistente() throws Exception {
        when(clienteService.buscarPorId(99L)).thenThrow(new RecursoNaoEncontradoException("Cliente não encontrado"));

        mockMvc.perform(get("/api/clientes/99")
                .with(user("admin").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Cliente não encontrado")));
    }
}
