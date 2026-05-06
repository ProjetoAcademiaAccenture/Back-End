package acc.br.projetoFinal.Accenture.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ExceptionHandlerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRetornarErroRecursoNaoEncontrado() throws Exception {
        // Tenta buscar um cliente que não existe
        mockMvc.perform(post("/api/clientes/999/enderecos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deveRetornarErroValidacao() throws Exception {
        // Envia dados inválidos (CPF vazio)
        String jsonInvalido = """
                {
                    "nome": "",
                    "cpf": "",
                    "email": "email-invalido",
                    "telefone": "",
                    "cep": "01310100",
                    "numero": "100"
                }
                """;

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonInvalido))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarErroEmailInvalido() throws Exception {
        String jsonComEmailInvalido = """
                {
                    "nome": "João Silva",
                    "cpf": "12345678901",
                    "email": "email-invalido",
                    "telefone": "11999999999",
                    "cep": "01310100",
                    "numero": "100"
                }
                """;

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonComEmailInvalido))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarErroCpfInvalido() throws Exception {
        // CPF com tamanho inválido
        String jsonComCpfInvalido = """
                {
                    "nome": "João Silva",
                    "cpf": "123",
                    "email": "joao@email.com",
                    "telefone": "11999999999",
                    "cep": "01310100",
                    "numero": "100"
                }
                """;

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonComCpfInvalido))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarErroNomeInvalido() throws Exception {
        // Nome com tamanho inválido
        String jsonComNomeInvalido = """
                {
                    "nome": "AB",
                    "cpf": "12345678901",
                    "email": "joao@email.com",
                    "telefone": "11999999999",
                    "cep": "01310100",
                    "numero": "100"
                }
                """;

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonComNomeInvalido))
                .andExpect(status().isBadRequest());
    }
}
