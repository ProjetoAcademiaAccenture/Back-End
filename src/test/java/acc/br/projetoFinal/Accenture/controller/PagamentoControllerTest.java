package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.request.PagamentoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.PagamentoResponseDTO;
import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
import acc.br.projetoFinal.Accenture.enums.StatusPagamento;
import acc.br.projetoFinal.Accenture.service.PagamentoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PagamentoControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private PagamentoService pagamentoService;

    @InjectMocks
    private PagamentoController pagamentoController;

    private PagamentoResponseDTO responseDTO;
    private PagamentoRequestDTO  requestDTO;

    @BeforeEach
    void setUp() {
        // sobe MockMvc standalone — sem contexto Spring, sem Security
        mockMvc = MockMvcBuilders
                .standaloneSetup(pagamentoController)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        responseDTO = PagamentoResponseDTO.builder()
                .id(1L)
                .pedidoId(10L)
                .metodoPagamento(MetodoPagamento.PIX)
                .status(StatusPagamento.APROVADO)
                .valorBruto(new BigDecimal("200.00"))
                .desconto(new BigDecimal("10.00"))
                .valorFinal(new BigDecimal("190.00"))
                .dataCriacao(LocalDateTime.now())
                .dataConclusao(LocalDateTime.now())
                .tentativas(List.of())
                .build();

        requestDTO = PagamentoRequestDTO.builder()
                .pagamentoId(1L)
                .metodoPagamento(MetodoPagamento.PIX)
                .senhaTransacao("1234")
                .build();
    }

    // =======================================================================
    // GET /api/pagamentos/{id}
    // =======================================================================

    @Test
    void buscarPorId_deveRetornar200_quandoEncontrado() throws Exception {
        when(pagamentoService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/pagamentos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.pedidoId").value(10L))
                .andExpect(jsonPath("$.metodoPagamento").value("PIX"))
                .andExpect(jsonPath("$.status").value("APROVADO"))
                .andExpect(jsonPath("$.valorFinal").value(190.00));

        verify(pagamentoService, times(1)).buscarPorId(1L);
    }

    @Test
    void buscarPorId_devePassarIdCorretoParaServico() throws Exception {
        when(pagamentoService.buscarPorId(42L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/pagamentos/42"))
                .andExpect(status().isOk());

        verify(pagamentoService).buscarPorId(42L);
    }

    // =======================================================================
    // GET /api/pagamentos/pedido/{pedidoId}
    // =======================================================================

    @Test
    void buscarPorPedido_deveRetornar200_quandoEncontrado() throws Exception {
        when(pagamentoService.buscarPorPedidoId(10L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/pagamentos/pedido/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pedidoId").value(10L))
                .andExpect(jsonPath("$.status").value("APROVADO"));

        verify(pagamentoService, times(1)).buscarPorPedidoId(10L);
    }

    @Test
    void buscarPorPedido_devePassarPedidoIdCorretoParaServico() throws Exception {
        when(pagamentoService.buscarPorPedidoId(99L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/pagamentos/pedido/99"))
                .andExpect(status().isOk());

        verify(pagamentoService).buscarPorPedidoId(99L);
    }

    // =======================================================================
    // POST /api/pagamentos/processar
    // =======================================================================

    @Test
    void processar_deveRetornar200_quandoPayloadValido() throws Exception {
        when(pagamentoService.processar(any(PagamentoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/pagamentos/processar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("APROVADO"))
                .andExpect(jsonPath("$.metodoPagamento").value("PIX"));

        verify(pagamentoService, times(1)).processar(any(PagamentoRequestDTO.class));
    }

    @Test
    void processar_deveRetornar400_quandoBodyAusente() throws Exception {
        mockMvc.perform(post("/api/pagamentos/processar")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(pagamentoService);
    }

    // =======================================================================
    // PATCH /api/pagamentos/{id}/cancelar
    // =======================================================================

    @Test
    void cancelar_deveRetornar204_quandoCancelamentoSucesso() throws Exception {
        doNothing().when(pagamentoService).cancelar(1L);

        mockMvc.perform(patch("/api/pagamentos/1/cancelar"))
                .andExpect(status().isNoContent());

        verify(pagamentoService, times(1)).cancelar(1L);
    }

    @Test
    void cancelar_devePassarIdCorretoParaServico() throws Exception {
        doNothing().when(pagamentoService).cancelar(77L);

        mockMvc.perform(patch("/api/pagamentos/77/cancelar"))
                .andExpect(status().isNoContent());

        verify(pagamentoService).cancelar(77L);
    }

    @Test
    void cancelar_naoDeveRetornarBody() throws Exception {
        doNothing().when(pagamentoService).cancelar(1L);

        mockMvc.perform(patch("/api/pagamentos/1/cancelar"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }
}