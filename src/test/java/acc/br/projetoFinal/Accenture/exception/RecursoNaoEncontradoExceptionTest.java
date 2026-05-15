package acc.br.projetoFinal.Accenture.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para RecursoNaoEncontradoException")
class RecursoNaoEncontradoExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem correta")
    void deveCriarExcecaoComMensagemCorreta() {
        String mensagem = "Recurso não encontrado";

        RecursoNaoEncontradoException exception = new RecursoNaoEncontradoException(mensagem);

        assertEquals(mensagem, exception.getMessage());
    }

    @Test
    @DisplayName("Deve ser instância de RuntimeException")
    void deveSerInstanciaDeRuntimeException() {
        RecursoNaoEncontradoException exception = new RecursoNaoEncontradoException("erro");

        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("Deve ser instância de Exception")
    void deveSerInstanciaDeException() {
        RecursoNaoEncontradoException exception = new RecursoNaoEncontradoException("erro");

        assertInstanceOf(Exception.class, exception);
    }

    @Test
    @DisplayName("Deve lançar e capturar a exceção corretamente")
    void deveLancarECapturarExcecaoCorretamente() {
        String mensagem = "Produto com id 10 não encontrado";

        RecursoNaoEncontradoException thrown = assertThrows(
            RecursoNaoEncontradoException.class,
            () -> { throw new RecursoNaoEncontradoException(mensagem); }
        );

        assertEquals(mensagem, thrown.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar mensagem nula sem lançar NullPointerException")
    void deveAceitarMensagemNula() {
        assertDoesNotThrow(() -> new RecursoNaoEncontradoException(null));
    }

    @Test
    @DisplayName("Deve retornar null quando mensagem for nula")
    void deveRetornarNullQuandoMensagemForNula() {
        RecursoNaoEncontradoException exception = new RecursoNaoEncontradoException(null);

        assertNull(exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar mensagem vazia")
    void deveAceitarMensagemVazia() {
        RecursoNaoEncontradoException exception = new RecursoNaoEncontradoException("");

        assertEquals("", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar mensagem com caracteres especiais")
    void deveAceitarMensagemComCaracteresEspeciais() {
        String mensagem = "Recurso não encontrado: id=42, entidade=Pedido às 10h!";

        RecursoNaoEncontradoException exception = new RecursoNaoEncontradoException(mensagem);

        assertEquals(mensagem, exception.getMessage());
    }

    @Test
    @DisplayName("Não deve ter causa por padrão")
    void naoDeveTerCausaPorPadrao() {
        RecursoNaoEncontradoException exception = new RecursoNaoEncontradoException("erro");

        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve ser exceção não verificada (unchecked)")
    void deveSerExcecaoNaoVerificada() {
        assertTrue(RuntimeException.class.isAssignableFrom(RecursoNaoEncontradoException.class));
    }
}