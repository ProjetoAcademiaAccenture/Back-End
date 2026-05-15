package acc.br.projetoFinal.Accenture.controller;

import acc.br.projetoFinal.Accenture.dto.request.ProdutoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.ProdutoResponseDTO;
import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
import acc.br.projetoFinal.Accenture.service.ProdutoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
@WithMockUser(roles = "ADMIN")
class ProdutoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProdutoService produtoService;

    private ProdutoResponseDTO produtoResponse;
    private ProdutoRequestDTO produtoRequest;

    @BeforeEach
    void setup() {
        produtoRequest = ProdutoRequestDTO.builder()
        .nome("Mouse Gamer")
        .descricao("Mouse com sensor de alta precisão")
        .preco(new BigDecimal("150.00"))
        .quantidadeEstoque(10)
        .categoria(acc.br.projetoFinal.Accenture.enums.Categoria.ELETRONICOS) 
        .build();


        produtoResponse = ProdutoResponseDTO.builder()
                .id(1L)
                .nome("Mouse Gamer")
                .descricao("Mouse com sensor de alta precisão")
                .preco(new BigDecimal("150.00"))
                .quantidadeEstoque(10)
                .build();
    }

    @Test
    void deveListarTodosProdutos() throws Exception {
        when(produtoService.listarTodos()).thenReturn(List.of(produtoResponse));

        mockMvc.perform(get("/api/produtos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nome", is("Mouse Gamer")));
    }

    @Test
    void deveBuscarProdutoPorId() throws Exception {
        when(produtoService.buscarPorId(1L)).thenReturn(produtoResponse);

        mockMvc.perform(get("/api/produtos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Mouse Gamer")))
                .andExpect(jsonPath("$.preco", is(150.00)));
    }

    @Test
    void deveCriarNovoProduto() throws Exception {
        when(produtoService.criar(any(ProdutoRequestDTO.class))).thenReturn(produtoResponse);

        mockMvc.perform(post("/api/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(produtoRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Mouse Gamer")));
    }

    @Test
    void deveAtualizarProduto() throws Exception {
        ProdutoResponseDTO produtoAtualizado = ProdutoResponseDTO.builder()
                .id(1L)
                .nome("Mouse Gamer Pro")
                .descricao("Mouse com sensor de alta precisão - versão pro")
                .preco(new BigDecimal("200.00"))
                .quantidadeEstoque(10)
                .build();

        when(produtoService.atualizar(eq(1L), any(ProdutoRequestDTO.class)))
                .thenReturn(produtoAtualizado);

        mockMvc.perform(put("/api/produtos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(produtoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Mouse Gamer Pro")))
                .andExpect(jsonPath("$.preco", is(200.00)));
    }

    @Test
    void deveDeletarProduto() throws Exception {
        doNothing().when(produtoService).deletar(1L);

        mockMvc.perform(delete("/api/produtos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveAjustarEstoqueProduto() throws Exception {
        ProdutoResponseDTO produtoComEstoqueAjustado = ProdutoResponseDTO.builder()
                .id(1L)
                .nome("Mouse Gamer")
                .descricao("Mouse com sensor de alta precisão")
                .preco(new BigDecimal("150.00"))
                .quantidadeEstoque(20)
                .build();

        when(produtoService.ajustarEstoque(1L, 20))
                .thenReturn(produtoComEstoqueAjustado);

        mockMvc.perform(patch("/api/produtos/1/estoque")
                .param("novaQuantidade", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeEstoque", is(20)));
    }
}
