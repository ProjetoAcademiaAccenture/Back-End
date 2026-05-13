package acc.br.projetoFinal.Accenture.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Cobertura - Exception Handler e Custom Exceptions")
class ExceptionHandlerTests {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Deve cobrir ErrorResponse e seu Builder (Lombok)")
    void testErrorResponseCoverage() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(now)
                .status(404)
                .error("Not Found")
                .message("Recurso inexistente")
                .build();

        // Testa getters (importante para o JaCoCo em classes @Data)
        assertEquals(now, response.getTimestamp());
        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getError());
        assertEquals("Recurso inexistente", response.getMessage());

        // Testa NoArgsConstructor e Setters
        ErrorResponse emptyResponse = new ErrorResponse();
        emptyResponse.setStatus(200);
        assertEquals(200, emptyResponse.getStatus());
    }

    @Test
    @DisplayName("Deve cobrir RecursoNaoEncontradoException")
    void handleRecursoNaoEncontradoTest() {
        String msg = "Usuário não encontrado";
        RecursoNaoEncontradoException ex = new RecursoNaoEncontradoException(msg);
        
        ResponseEntity<ErrorResponse> response = handler.handleRecursoNaoEncontrado(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(msg, response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Deve cobrir EstoqueInsuficienteException")
    void handleEstoqueInsuficienteTest() {
        EstoqueInsuficienteException ex = new EstoqueInsuficienteException("Faltam itens");
        ResponseEntity<ErrorResponse> response = handler.handleEstoqueInsuficiente(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("Estoque Insuficiente", response.getBody().getError());
    }

    @Test
    @DisplayName("Deve cobrir SaldoInsuficienteException")
    void handleSaldoInsuficienteTest() {
        SaldoInsuficienteException ex = new SaldoInsuficienteException("Saldo insuficiente");
        ResponseEntity<ErrorResponse> response = handler.handleSaldoInsuficiente(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("Saldo Insuficiente", response.getBody().getError());
    }

    @Test
    @DisplayName("Deve cobrir CancelamentoException")
    void handleCancelamentoTest() {
        CancelamentoException ex = new CancelamentoException("Erro ao cancelar pedido");
        ResponseEntity<ErrorResponse> response = handler.handleCancelamento(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Erro ao Cancelar", response.getBody().getError());
    }

    @Test
    @DisplayName("Deve cobrir SenhaInvalidaException")
    void handleSenhaInvalidaTest() {
        SenhaInvalidaException ex = new SenhaInvalidaException("Senha incorreta");
        ResponseEntity<ErrorResponse> response = handler.handleSenhaInvalida(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Não Autorizado", response.getBody().getError());
    }

    @Test
    @DisplayName("Deve cobrir IllegalArgumentException")
    void handleIllegalArgumentTest() {
        IllegalArgumentException ex = new IllegalArgumentException("Argumento inválido");
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve cobrir Exception Genérica (Fallback)")
    void handleGenericExceptionTest() {
        Exception ex = new RuntimeException("Erro interno desconhecido");
        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Deve cobrir MethodArgumentNotValidException (Validação de DTOs)")
    void handleMethodArgumentNotValidTest() {
        // Simula uma falha de validação no campo 'cpf'
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "requestDTO");
        bindingResult.addError(new FieldError("requestDTO", "cpf", "CPF deve ter 11 dígitos"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        
        ResponseEntity<Map<String, Object>> response = handler.handleMethodArgumentNotValid(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertEquals("CPF deve ter 11 dígitos", errors.get("cpf"));
    }
}