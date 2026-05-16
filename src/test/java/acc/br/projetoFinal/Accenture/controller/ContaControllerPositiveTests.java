package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.response.ExtratoResponseDTO;
import acc.br.projetoFinal.Accenture.enums.TipoExtrato;
import acc.br.projetoFinal.Accenture.enums.TipoConta;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.repository.ContaRepository;
import acc.br.projetoFinal.Accenture.service.ContaService;
import acc.br.projetoFinal.Accenture.service.ExtratoService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("ContaController - Cenários Positivos")
class ContaControllerPositiveTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContaRepository contaRepository;

    @MockBean
    private ContaService contaService;

    @MockBean
    private ExtratoService extratoService;

    @BeforeEach
    void setup() {
        // Não é necessário criar uma Conta real para testes de controller
        // O mock do repositório retornará uma Conta mock
    }

    @Test
    @DisplayName("Deve buscar conta por ID com sucesso")
    void deveBuscarContaPorIdComSucesso() throws Exception {
        // Criar uma Conta com todos os campos necessários
        Cliente cliente = Cliente.builder()
                .id(1L)
                .nome("Teste")
                .build();

        Conta conta = new Conta();
        conta.setId(1L);
        conta.setNumeroConta("123456");
        conta.setSaldo(new BigDecimal("5000.00"));
        conta.setTipo(TipoConta.CORRENTE);
        conta.setCliente(cliente);

        when(contaService.buscarPorId(1L)).thenReturn(conta);


        mockMvc.perform(get("/api/contas/1")
                .with(user("admin").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.saldo", is(5000.00)));
    }

    @Test
    @DisplayName("Deve depositar com sucesso")
    void deveDepositarComSucesso() throws Exception {
        Cliente cliente = Cliente.builder()
                .id(1L)
                .nome("Teste")
                .build();

        Conta conta = new Conta();
        conta.setId(1L);
        conta.setNumeroConta("123456");
        conta.setSaldo(new BigDecimal("5000.00"));
        conta.setTipo(TipoConta.CORRENTE);
        conta.setCliente(cliente);

        when(contaService.depositar(eq(1L), any(BigDecimal.class))).thenReturn(conta);


        mockMvc.perform(patch("/api/contas/1/depositar")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .param("valor", "500.00")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("Deve listar extrato sem parâmetros (todas as transações)")
    void deveListarExtratoSemParametros() throws Exception {
        List<ExtratoResponseDTO> extratos = List.of(
                ExtratoResponseDTO.builder().id(1L).build(),
                ExtratoResponseDTO.builder().id(2L).build()
        );

        when(extratoService.listarPorConta(1L)).thenReturn(extratos);

        mockMvc.perform(get("/api/contas/1/extrato")
                .with(user("admin").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    @DisplayName("Deve listar extrato por período")
    void deveListarExtratoComInicioDeFim() throws Exception {
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();

        List<ExtratoResponseDTO> extratos = List.of(
                ExtratoResponseDTO.builder().id(1L).build()
        );

        when(extratoService.listarPorPeriodo(eq(1L), any(), any()))
                .thenReturn(extratos);

        mockMvc.perform(get("/api/contas/1/extrato")
                .param("inicio", inicio.toString())
                .param("fim", fim.toString())
                .with(user("admin").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    @DisplayName("Deve listar extrato por tipo")
    void deveListarExtratoComTipo() throws Exception {
        List<ExtratoResponseDTO> extratos = List.of(
                ExtratoResponseDTO.builder().id(1L).build(),
                ExtratoResponseDTO.builder().id(2L).build()
        );

        when(extratoService.listarPorTipo(1L, TipoExtrato.DEBITO)).thenReturn(extratos);

        mockMvc.perform(get("/api/contas/1/extrato")
                .param("tipo", "DEBITO")
                .with(user("admin").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Deve listar extrato por período e tipo")
    void deveListarExtratoComPeriodoETipo() throws Exception {
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();

        List<ExtratoResponseDTO> extratos = List.of(
                ExtratoResponseDTO.builder().id(1L).build()
        );

        when(extratoService.listarPorPeriodoETipo(eq(1L), any(), any(), eq(TipoExtrato.CREDITO)))
                .thenReturn(extratos);

        mockMvc.perform(get("/api/contas/1/extrato")
                .param("inicio", inicio.toString())
                .param("fim", fim.toString())
                .param("tipo", "CREDITO")
                .with(user("admin").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
