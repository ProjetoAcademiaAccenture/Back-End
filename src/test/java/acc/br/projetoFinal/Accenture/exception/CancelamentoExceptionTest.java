package acc.br.projetoFinal.Accenture.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para CancelamentoException")
class CancelamentoExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem correta")
    void deveCriarExcecaoComMensagemCorreta() {
        String mensagem = "Cancelamento não permitido";

        CancelamentoException exception = new CancelamentoException(mensagem);

        assertEquals(mensagem, exception.getMessage());
    }

    @Test
    @DisplayName("Deve ser instância de RuntimeException")
    void deveSerInstanciaDeRuntimeException() {
        CancelamentoException exception = new CancelamentoException("erro");

        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("Deve ser instância de Exception")
    void deveSerInstanciaDeException() {
        CancelamentoException exception = new CancelamentoException("erro");

        assertInstanceOf(Exception.class, exception);
    }

    @Test
    @DisplayName("Deve lançar e capturar a exceção corretamente")
    void deveLancarECapturarExcecaoCorretamente() {
        String mensagem = "Pedido já cancelado";

        CancelamentoException thrown = assertThrows(
            CancelamentoException.class,
            () -> { throw new CancelamentoException(mensagem); }
        );

        assertEquals(mensagem, thrown.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar mensagem nula sem lançar NullPointerException")
    void deveAceitarMensagemNula() {
        assertDoesNotThrow(() -> new CancelamentoException(null));
    }

    @Test
    @DisplayName("Deve retornar null quando mensagem for nula")
    void deveRetornarNullQuandoMensagemForNula() {
        CancelamentoException exception = new CancelamentoException(null);

        assertNull(exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar mensagem vazia")
    void deveAceitarMensagemVazia() {
        CancelamentoException exception = new CancelamentoException("");

        assertEquals("", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar mensagem com caracteres especiais")
    void deveAceitarMensagemComCaracteresEspeciais() {
        String mensagem = "Cancelamento inválido: prazo expirado às 23h59!";

        CancelamentoException exception = new CancelamentoException(mensagem);

        assertEquals(mensagem, exception.getMessage());
    }

    @Test
    @DisplayName("Não deve ter causa por padrão")
    void naoDeveTerCausaPorPadrao() {
        CancelamentoException exception = new CancelamentoException("erro");

        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve ser exceção não verificada (unchecked)")
    void deveSerExcecaoNaoVerificada() {
        // RuntimeException é unchecked — verificamos via hierarquia
        assertTrue(RuntimeException.class.isAssignableFrom(CancelamentoException.class));
    }
}