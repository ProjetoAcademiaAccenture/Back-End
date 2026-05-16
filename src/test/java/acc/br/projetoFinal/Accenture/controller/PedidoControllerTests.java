package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.request.ItemPedidoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.PedidoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.PedidoResponseDTO;
import acc.br.projetoFinal.Accenture.service.PedidoService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PedidoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PedidoService pedidoService;

    private PedidoResponseDTO pedidoResponse;
    private PedidoRequestDTO pedidoRequest;

    @BeforeEach
    void setup() {
        List<ItemPedidoRequestDTO> itens = new ArrayList<>();
        itens.add(ItemPedidoRequestDTO.builder()
                .produtoId(1L)
                .quantidade(2)
                .build());

        pedidoRequest = PedidoRequestDTO.builder()
                .clienteId(1L)
                .itens(itens)
                .metodoPagamento("BOLETO")
                .build();

        pedidoResponse = PedidoResponseDTO.builder()
                .id(1L)
                .dataCriacao(LocalDateTime.now())
                .build();
    }

    // -------------------------------------------------------------------------
    // GET /api/pedidos — listarTodos()
    // -------------------------------------------------------------------------

    @Test
    void deveListarTodosPedidos() throws Exception {
        when(pedidoService.listarTodos()).thenReturn(List.of(pedidoResponse));

        mockMvc.perform(get("/api/pedidos")
                        .with(user("admin").roles("USER", "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    // -------------------------------------------------------------------------
    // GET /api/pedidos/{id} — buscarPorId()
    // -------------------------------------------------------------------------

    @Test
    void deveBuscarPedidoPorId() throws Exception {
        when(pedidoService.buscarPorId(1L)).thenReturn(pedidoResponse);

        mockMvc.perform(get("/api/pedidos/1")
                        .with(user("admin").roles("USER", "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    // -------------------------------------------------------------------------
    // GET /api/pedidos/cliente/{clienteId} — listarPorCliente()
    // -------------------------------------------------------------------------

    @Test
    void deveListarPedidosPorCliente() throws Exception {
        when(pedidoService.listarPorCliente(1L)).thenReturn(List.of(pedidoResponse));

        mockMvc.perform(get("/api/pedidos/cliente/1")
                        .with(user("admin").roles("USER", "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    // -------------------------------------------------------------------------
    // POST /api/pedidos — criar()
    // Espera 201 Created com header Location apontando para o novo recurso
    // -------------------------------------------------------------------------

    @Test
    void deveCriarPedidoERetornar201() throws Exception {
        when(pedidoService.criar(any(PedidoRequestDTO.class))).thenReturn(pedidoResponse);

        mockMvc.perform(post("/api/pedidos")
                        .with(user("admin").roles("USER", "ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoRequest)))
                .andExpect(status().isCreated())
                // Header Location deve terminar com /api/pedidos/1
                .andExpect(header().string("Location", containsString("/api/pedidos/1")))
                .andExpect(jsonPath("$.id", is(1)));
    }

    // -------------------------------------------------------------------------
    // PATCH /api/pedidos/{id}/cancelar — cancelar()
    // -------------------------------------------------------------------------

    @Test
    void deveCancelarPedido() throws Exception {
        when(pedidoService.cancelarPedido(1L)).thenReturn(pedidoResponse);

        mockMvc.perform(patch("/api/pedidos/1/cancelar")
                        .with(user("admin").roles("USER", "ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }
}