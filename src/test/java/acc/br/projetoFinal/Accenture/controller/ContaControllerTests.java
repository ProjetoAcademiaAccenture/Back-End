package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.response.ContaResponseDTO;
import acc.br.projetoFinal.Accenture.dto.response.ExtratoResponseDTO;
import acc.br.projetoFinal.Accenture.enums.TipoExtrato;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.enums.TipoConta;
import acc.br.projetoFinal.Accenture.service.ContaService;
import acc.br.projetoFinal.Accenture.service.ExtratoService;
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
import java.util.Optional;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ContaControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private acc.br.projetoFinal.Accenture.repository.ContaRepository contaRepository;

    @MockBean
    private ContaService contaService;

    @MockBean
    private ExtratoService extratoService;

    private ContaResponseDTO contaResponse;
    private ExtratoResponseDTO extratoResponse;

    @BeforeEach
    void setup() {
        contaResponse = ContaResponseDTO.builder()
                .id(1L)
                .numeroConta("123456")
                .saldo(new BigDecimal("1000.00"))
                .build();

        extratoResponse = ExtratoResponseDTO.builder()
                .id(1L)
                .descricao("Depósito")
                .valor(new BigDecimal("100.00"))
                .build();
    }

    @Test
    void deveListarExtratoDaConta() throws Exception {
        when(extratoService.listarPorConta(1L)).thenReturn(List.of(extratoResponse));

        mockMvc.perform(get("/api/contas/1/extrato")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deveBuscarContaPorId() throws Exception {
            Conta conta = Conta.builder()
                .id(1L)
                .numeroConta("123456")
                .saldo(new BigDecimal("1000.00"))
                .tipo(TipoConta.CORRENTE)
                .ativo(true)
                .cliente(Cliente.builder().id(1L).nome("João Silva").cpf("12345678901").email("joao@email.com").build())
                .build();

            when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

            mockMvc.perform(get("/api/contas/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroConta", is("123456")))
                .andExpect(jsonPath("$.saldo", is(1000.00)));
    }

    @Test
    void deveDepositarNaConta() throws Exception {
            Conta contaAposDeposito = Conta.builder()
                .id(1L)
                .numeroConta("123456")
                .saldo(new BigDecimal("1500.00"))
                .tipo(TipoConta.CORRENTE)
                .ativo(true)
                .cliente(Cliente.builder().id(1L).nome("João Silva").cpf("12345678901").email("joao@email.com").build())
                .build();

            doNothing().when(contaService).depositar(eq(1L), eq(new BigDecimal("500.00")));
            when(contaRepository.findById(1L)).thenReturn(Optional.of(contaAposDeposito));

            mockMvc.perform(patch("/api/contas/1/depositar")
                .param("valor", "500.00")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo", is(1500.00)));
    }
}
