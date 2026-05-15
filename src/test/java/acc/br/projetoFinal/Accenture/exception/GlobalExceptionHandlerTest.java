package acc.br.projetoFinal.Accenture.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Testes para GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    // -------------------------------------------------------------------------
    // RecursoNaoEncontradoException → 404 NOT FOUND
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("handleRecursoNaoEncontrado: deve retornar 404 com body correto")
    void handleRecursoNaoEncontrado_deveRetornar404() {
        RecursoNaoEncontradoException ex = new RecursoNaoEncontradoException("Produto não encontrado");

        ResponseEntity<ErrorResponse> response = handler.handleRecursoNaoEncontrado(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Recurso Não Encontrado", response.getBody().getError());
        assertEquals("Produto não encontrado", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
        assertTrue(response.getBody().getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("handleRecursoNaoEncontrado: deve preservar mensagem nula da exceção")
    void handleRecursoNaoEncontrado_comMensagemNula() {
        RecursoNaoEncontradoException ex = new RecursoNaoEncontradoException(null);

        ResponseEntity<ErrorResponse> response = handler.handleRecursoNaoEncontrado(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody().getMessage());
    }

    // -------------------------------------------------------------------------
    // EstoqueInsuficienteException → 422 UNPROCESSABLE ENTITY
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("handleEstoqueInsuficiente: deve retornar 422 com body correto")
    void handleEstoqueInsuficiente_deveRetornar422() {
        EstoqueInsuficienteException ex = new EstoqueInsuficienteException("Estoque zerado");

        ResponseEntity<ErrorResponse> response = handler.handleEstoqueInsuficiente(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(422, response.getBody().getStatus());
        assertEquals("Estoque Insuficiente", response.getBody().getError());
        assertEquals("Estoque zerado", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("handleEstoqueInsuficiente: deve preservar mensagem nula da exceção")
    void handleEstoqueInsuficiente_comMensagemNula() {
        EstoqueInsuficienteException ex = new EstoqueInsuficienteException(null);

        ResponseEntity<ErrorResponse> response = handler.handleEstoqueInsuficiente(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNull(response.getBody().getMessage());
    }

    // -------------------------------------------------------------------------
    // SaldoInsuficienteException → 422 UNPROCESSABLE ENTITY
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("handleSaldoInsuficiente: deve retornar 422 com body correto")
    void handleSaldoInsuficiente_deveRetornar422() {
        SaldoInsuficienteException ex = new SaldoInsuficienteException("Saldo insuficiente para a operação");

        ResponseEntity<ErrorResponse> response = handler.handleSaldoInsuficiente(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(422, response.getBody().getStatus());
        assertEquals("Saldo Insuficiente", response.getBody().getError());
        assertEquals("Saldo insuficiente para a operação", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("handleSaldoInsuficiente: deve preservar mensagem nula da exceção")
    void handleSaldoInsuficiente_comMensagemNula() {
        SaldoInsuficienteException ex = new SaldoInsuficienteException(null);

        ResponseEntity<ErrorResponse> response = handler.handleSaldoInsuficiente(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNull(response.getBody().getMessage());
    }

    // -------------------------------------------------------------------------
    // CancelamentoException → 400 BAD REQUEST
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("handleCancelamento: deve retornar 400 com body correto")
    void handleCancelamento_deveRetornar400() {
        CancelamentoException ex = new CancelamentoException("Prazo de cancelamento expirado");

        ResponseEntity<ErrorResponse> response = handler.handleCancelamento(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Erro ao Cancelar", response.getBody().getError());
        assertEquals("Prazo de cancelamento expirado", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("handleCancelamento: deve preservar mensagem nula da exceção")
    void handleCancelamento_comMensagemNula() {
        CancelamentoException ex = new CancelamentoException(null);

        ResponseEntity<ErrorResponse> response = handler.handleCancelamento(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody().getMessage());
    }

    // -------------------------------------------------------------------------
    // MethodArgumentNotValidException → 400 BAD REQUEST
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("handleMethodArgumentNotValid: deve retornar 400 com erros de campo")
    void handleMethodArgumentNotValid_deveRetornar400ComErrosDeCampo() {
        FieldError fieldError = new FieldError("objeto", "nome", "não pode ser vazio");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleMethodArgumentNotValid(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Validação de Entrada", response.getBody().get("error"));
        assertNotNull(response.getBody().get("timestamp"));

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertEquals("não pode ser vazio", errors.get("nome"));
    }

    @Test
    @DisplayName("handleMethodArgumentNotValid: deve usar string vazia quando defaultMessage for nulo")
    void handleMethodArgumentNotValid_comDefaultMessageNulo() {
        FieldError fieldError = new FieldError("objeto", "email", null, false, null, null, null);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleMethodArgumentNotValid(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertEquals("", errors.get("email"));
    }

    @Test
    @DisplayName("handleMethodArgumentNotValid: deve mapear múltiplos erros de campos distintos")
    void handleMethodArgumentNotValid_comMultiplosCampos() {
        FieldError erroNome = new FieldError("objeto", "nome", "obrigatório");
        FieldError erroEmail = new FieldError("objeto", "email", "formato inválido");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(erroNome, erroEmail));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleMethodArgumentNotValid(ex);

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertEquals(2, errors.size());
        assertEquals("obrigatório", errors.get("nome"));
        assertEquals("formato inválido", errors.get("email"));
    }

    @Test
    @DisplayName("handleMethodArgumentNotValid: deve retornar errors vazio quando não houver FieldErrors")
    void handleMethodArgumentNotValid_semFieldErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleMethodArgumentNotValid(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertTrue(errors.isEmpty());
    }

    // -------------------------------------------------------------------------
    // IllegalArgumentException → 400 BAD REQUEST
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("handleIllegalArgument: deve retornar 400 com body correto")
    void handleIllegalArgument_deveRetornar400() {
        IllegalArgumentException ex = new IllegalArgumentException("Argumento inválido fornecido");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Argumento Inválido", response.getBody().getError());
        assertEquals("Argumento inválido fornecido", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("handleIllegalArgument: deve preservar mensagem nula da exceção")
    void handleIllegalArgument_comMensagemNula() {
        IllegalArgumentException ex = new IllegalArgumentException((String) null);

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody().getMessage());
    }

    // -------------------------------------------------------------------------
    // Exception genérica → 500 INTERNAL SERVER ERROR
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("handleGenericException: deve retornar 500 com mensagem padrão")
    void handleGenericException_deveRetornar500() {
        Exception ex = new Exception("erro inesperado qualquer");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Erro Interno do Servidor", response.getBody().getError());
        assertEquals(
            "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.",
            response.getBody().getMessage()
        );
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("handleGenericException: deve retornar 500 mesmo com mensagem nula na exceção")
    void handleGenericException_comMensagemNula() {
        Exception ex = new Exception((String) null);

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(
            "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.",
            response.getBody().getMessage()
        );
    }

    // -------------------------------------------------------------------------
    // SenhaInvalidaException → 401 UNAUTHORIZED
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("handleSenhaInvalida: deve retornar 401 com body correto")
    void handleSenhaInvalida_deveRetornar401() {
        SenhaInvalidaException ex = new SenhaInvalidaException("Senha incorreta");

        ResponseEntity<ErrorResponse> response = handler.handleSenhaInvalida(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Não Autorizado", response.getBody().getError());
        assertEquals("Senha incorreta", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("handleSenhaInvalida: deve preservar mensagem nula da exceção")
    void handleSenhaInvalida_comMensagemNula() {
        SenhaInvalidaException ex = new SenhaInvalidaException(null);

        ResponseEntity<ErrorResponse> response = handler.handleSenhaInvalida(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody().getMessage());
    }

    // -------------------------------------------------------------------------
    // Timestamp — verificação de proximidade temporal
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Timestamp de todos os handlers deve estar próximo do momento atual")
    void todosOsHandlersDevemGerarTimestampRecente() {
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);

        assertAll(
            () -> assertTrue(handler.handleRecursoNaoEncontrado(
                new RecursoNaoEncontradoException("x")).getBody().getTimestamp().isAfter(antes)),
            () -> assertTrue(handler.handleEstoqueInsuficiente(
                new EstoqueInsuficienteException("x")).getBody().getTimestamp().isAfter(antes)),
            () -> assertTrue(handler.handleSaldoInsuficiente(
                new SaldoInsuficienteException("x")).getBody().getTimestamp().isAfter(antes)),
            () -> assertTrue(handler.handleCancelamento(
                new CancelamentoException("x")).getBody().getTimestamp().isAfter(antes)),
            () -> assertTrue(handler.handleIllegalArgument(
                new IllegalArgumentException("x")).getBody().getTimestamp().isAfter(antes)),
            () -> assertTrue(handler.handleGenericException(
                new Exception("x")).getBody().getTimestamp().isAfter(antes)),
            () -> assertTrue(handler.handleSenhaInvalida(
                new SenhaInvalidaException("x")).getBody().getTimestamp().isAfter(antes))
        );
    }
}