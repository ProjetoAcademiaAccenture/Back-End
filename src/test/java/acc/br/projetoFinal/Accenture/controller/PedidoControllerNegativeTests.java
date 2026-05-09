package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.request.ProdutoRequestDTO;
import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.service.ProdutoService;
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

import java.math.BigDecimal;

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
@DisplayName("PedidoController - Cenários Negativos")
class PedidoControllerNegativeTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProdutoService produtoService;

    private ProdutoRequestDTO produtoRequestInvalido;

    @BeforeEach
    void setup() {
        produtoRequestInvalido = ProdutoRequestDTO.builder()
                .nome("Produto Teste")
                .descricao("Descrição teste")
                .preco(new BigDecimal("10.00"))
                .quantidade(-1) // Quantidade inválida
                .metodoPgto(MetodoPagamento.PIX)
                .build();
    }

    @Test
    void deveRetornarBadRequestAoCriarProdutoComDadosInvalidos() throws Exception {
        mockMvc.perform(post("/api/produtos")
                .with(user("admin").roles("USER", "ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(produtoRequestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validação de Entrada")));
    }

    @Test
    void deveRetornarNotFoundAoBuscarProdutoInexistente() throws Exception {
        when(produtoService.buscarPorId(99L)).thenThrow(new RecursoNaoEncontradoException("Produto não encontrado"));

        mockMvc.perform(get("/api/produtos/99")
                .with(user("admin").roles("USER", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Produto não encontrado")));
    }
}