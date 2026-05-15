package acc.br.projetoFinal.Accenture.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para EstoqueInsuficienteException")
class EstoqueInsuficienteExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem correta")
    void deveCriarExcecaoComMensagemCorreta() {
        String mensagem = "Estoque insuficiente para o produto";

        EstoqueInsuficienteException exception = new EstoqueInsuficienteException(mensagem);

        assertEquals(mensagem, exception.getMessage());
    }

    @Test
    @DisplayName("Deve ser instância de RuntimeException")
    void deveSerInstanciaDeRuntimeException() {
        EstoqueInsuficienteException exception = new EstoqueInsuficienteException("erro");

        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("Deve ser instância de Exception")
    void deveSerInstanciaDeException() {
        EstoqueInsuficienteException exception = new EstoqueInsuficienteException("erro");

        assertInstanceOf(Exception.class, exception);
    }

    @Test
    @DisplayName("Deve lançar e capturar a exceção corretamente")
    void deveLancarECapturarExcecaoCorretamente() {
        String mensagem = "Quantidade solicitada maior que o estoque disponível";

        EstoqueInsuficienteException thrown = assertThrows(
            EstoqueInsuficienteException.class,
            () -> { throw new EstoqueInsuficienteException(mensagem); }
        );

        assertEquals(mensagem, thrown.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar mensagem nula sem lançar NullPointerException")
    void deveAceitarMensagemNula() {
        assertDoesNotThrow(() -> new EstoqueInsuficienteException(null));
    }

    @Test
    @DisplayName("Deve retornar null quando mensagem for nula")
    void deveRetornarNullQuandoMensagemForNula() {
        EstoqueInsuficienteException exception = new EstoqueInsuficienteException(null);

        assertNull(exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar mensagem vazia")
    void deveAceitarMensagemVazia() {
        EstoqueInsuficienteException exception = new EstoqueInsuficienteException("");

        assertEquals("", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar mensagem com caracteres especiais")
    void deveAceitarMensagemComCaracteresEspeciais() {
        String mensagem = "Estoque insuficiente: apenas 3 unidades disponíveis às 09h!";

        EstoqueInsuficienteException exception = new EstoqueInsuficienteException(mensagem);

        assertEquals(mensagem, exception.getMessage());
    }

    @Test
    @DisplayName("Não deve ter causa por padrão")
    void naoDeveTerCausaPorPadrao() {
        EstoqueInsuficienteException exception = new EstoqueInsuficienteException("erro");

        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve ser exceção não verificada (unchecked)")
    void deveSerExcecaoNaoVerificada() {
        assertTrue(RuntimeException.class.isAssignableFrom(EstoqueInsuficienteException.class));
    }
}