package acc.br.projetoFinal.Accenture.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Regras de Negócio - Cliente")
class ClienteBusinessRulesTests {

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1L)
                .nome("João Silva")
                .cpf("12345678901")
                .email("joao@email.com")
                .telefone("11999999999")
                .build();
    }

    @Test
    @DisplayName("Deve validar CPF válido com sucesso")
    void deveValidarCpfValidoComSucesso() {
        cliente.setCpf("12345678901");
        assertDoesNotThrow(cliente::validarCpf);
    }

    @Test
    @DisplayName("Não deve validar CPF vazio")
    void naoDeveValidarCpfVazio() {
        cliente.setCpf("");
        assertThrows(IllegalArgumentException.class, cliente::validarCpf);
    }

    @Test
    @DisplayName("Não deve validar CPF nulo")
    void naoDeveValidarCpfNulo() {
        cliente.setCpf(null);
        assertThrows(IllegalArgumentException.class, cliente::validarCpf);
    }

    @Test
    @DisplayName("Não deve validar CPF com menos de 11 dígitos")
    void naoDeveValidarCpfComMenosDe11Digitos() {
        cliente.setCpf("1234567890");
        assertThrows(IllegalArgumentException.class, cliente::validarCpf);
    }

    @Test
    @DisplayName("Não deve validar CPF com mais de 11 dígitos")
    void naoDeveValidarCpfComMaisDe11Digitos() {
        cliente.setCpf("123456789012");
        assertThrows(IllegalArgumentException.class, cliente::validarCpf);
    }

    @Test
    @DisplayName("Não deve validar CPF com caracteres não numéricos")
    void naoDeveValidarCpfComCaracteresNaoNumericos() {
        cliente.setCpf("1234567890a");
        assertThrows(IllegalArgumentException.class, cliente::validarCpf);
    }

    @Test
    @DisplayName("Deve validar email válido com sucesso")
    void deveValidarEmailValidoComSucesso() {
        cliente.setEmail("joao@email.com");
        assertDoesNotThrow(cliente::validarEmail);
    }

    @Test
    @DisplayName("Deve validar email com domínio complexo")
    void deveValidarEmailComDominioComplexo() {
        cliente.setEmail("joao.silva@empresa.com.br");
        assertDoesNotThrow(cliente::validarEmail);
    }

    @Test
    @DisplayName("Não deve validar email vazio")
    void naoDeveValidarEmailVazio() {
        cliente.setEmail("");
        assertThrows(IllegalArgumentException.class, cliente::validarEmail);
    }

    @Test
    @DisplayName("Não deve validar email nulo")
    void naoDeveValidarEmailNulo() {
        cliente.setEmail(null);
        assertThrows(IllegalArgumentException.class, cliente::validarEmail);
    }

    @Test
    @DisplayName("Não deve validar email sem @")
    void naoDeveValidarEmailSemArroba() {
        cliente.setEmail("joaoemail.com");
        assertThrows(IllegalArgumentException.class, cliente::validarEmail);
    }

    @Test
    @DisplayName("Não deve validar email sem domínio")
    void naoDeveValidarEmailSemDominio() {
        cliente.setEmail("joao@");
        assertThrows(IllegalArgumentException.class, cliente::validarEmail);
    }

    @Test
    @DisplayName("Deve validar nome válido com sucesso")
    void deveValidarNomeValidoComSucesso() {
        cliente.setNome("João Silva");
        assertDoesNotThrow(cliente::validarNome);
    }

    @Test
    @DisplayName("Deve validar nome com 3 caracteres (mínimo)")
    void deveValidarNomeComTresCaracteres() {
        cliente.setNome("Joã");
        assertDoesNotThrow(cliente::validarNome);
    }

    @Test
    @DisplayName("Não deve validar nome vazio")
    void naoDeveValidarNomeVazio() {
        cliente.setNome("");
        assertThrows(IllegalArgumentException.class, cliente::validarNome);
    }

    @Test
    @DisplayName("Não deve validar nome nulo")
    void naoDeveValidarNomeNulo() {
        cliente.setNome(null);
        assertThrows(IllegalArgumentException.class, cliente::validarNome);
    }

    @Test
    @DisplayName("Não deve validar nome com apenas espaços")
    void naoDeveValidarNomeComApenasEspacos() {
        cliente.setNome("   ");
        assertThrows(IllegalArgumentException.class, cliente::validarNome);
    }

    @Test
    @DisplayName("Não deve validar nome com menos de 3 caracteres")
    void naoDeveValidarNomeComMenosDeTresCaracteres() {
        cliente.setNome("Jo");
        assertThrows(IllegalArgumentException.class, cliente::validarNome);
    }

    @Test
    @DisplayName("Não deve validar nome com mais de 100 caracteres")
    void naoDeveValidarNomeComMaisDe100Caracteres() {
        String nomeLongo = "J".repeat(101);
        cliente.setNome(nomeLongo);
        assertThrows(IllegalArgumentException.class, cliente::validarNome);
    }

    @Test
    @DisplayName("Deve validar nome com exatamente 100 caracteres")
    void deveValidarNomeComExatamente100Caracteres() {
        String nome100 = "J".repeat(100);
        cliente.setNome(nome100);
        assertDoesNotThrow(cliente::validarNome);
    }
}