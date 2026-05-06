package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.request.ClienteRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.EnderecoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.ClienteResponseDTO;
import acc.br.projetoFinal.Accenture.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClienteControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClienteService clienteService;

    private ClienteResponseDTO clienteResponse;
    private ClienteRequestDTO clienteRequest;

    @BeforeEach
    void setup() {
        clienteRequest = ClienteRequestDTO.builder()
                .nome("João Silva")
                .cpf("12345678901")
                .email("joao@email.com")
                .telefone("11999999999")
                .cep("01310100")
                .numero("100")
                .build();

        clienteResponse = ClienteResponseDTO.builder()
                .id(1L)
                .nome("João Silva")
                .cpf("12345678901")
                .email("joao@email.com")
                .telefone("11999999999")
                .dataNascimento(LocalDate.now())
                .build();
    }

    @Test
    void deveListarTodosClientes() throws Exception {
        when(clienteService.listarTodos()).thenReturn(List.of(clienteResponse));

        mockMvc.perform(get("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nome", is("João Silva")));
    }

    @Test
    void deveBuscarClientePorId() throws Exception {
        when(clienteService.buscarPorId(1L)).thenReturn(clienteResponse);

        mockMvc.perform(get("/api/clientes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("João Silva")))
                .andExpect(jsonPath("$.email", is("joao@email.com")));
    }

    @Test
    void deveBuscarClientePorCpf() throws Exception {
        when(clienteService.buscarPorCpf("12345678901")).thenReturn(clienteResponse);

        mockMvc.perform(get("/api/clientes/cpf/12345678901")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf", is("12345678901")))
                .andExpect(jsonPath("$.nome", is("João Silva")));
    }

    @Test
    void deveCriarNovoCliente() throws Exception {
        when(clienteService.criar(any(ClienteRequestDTO.class))).thenReturn(clienteResponse);

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("João Silva")));
    }

    @Test
    void deveAtualizarCliente() throws Exception {
        ClienteResponseDTO clienteAtualizado = ClienteResponseDTO.builder()
                .id(1L)
                .nome("João Silva Atualizado")
                .cpf("12345678901")
                .email("joao.atualizado@email.com")
                .telefone("11988888888")
                .build();

        when(clienteService.atualizar(eq(1L), any(ClienteRequestDTO.class))).thenReturn(clienteAtualizado);

        mockMvc.perform(put("/api/clientes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("João Silva Atualizado")))
                .andExpect(jsonPath("$.email", is("joao.atualizado@email.com")));
    }

    @Test
    void deveDeletarCliente() throws Exception {
        doNothing().when(clienteService).deletar(1L);

        mockMvc.perform(delete("/api/clientes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRemoverEnderecoDoCliente() throws Exception {
        doNothing().when(clienteService).removerEndereco(1L, 1L);

        mockMvc.perform(delete("/api/clientes/1/enderecos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveAdicionarEnderecoAoCliente() throws Exception {
        EnderecoRequestDTO enderecoRequest = EnderecoRequestDTO.builder()
                .cep("01310100")
                .tipoEndereco(acc.br.projetoFinal.Accenture.enums.TipoEndereco.RESIDENCIAL)
                .numero("100")
                .complemento("Apto 12")
                .build();

        doNothing().when(clienteService).adicionarEndereco(eq(1L), any(EnderecoRequestDTO.class));

        mockMvc.perform(post("/api/clientes/1/enderecos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enderecoRequest)))
                .andExpect(status().isCreated());
    }
}
