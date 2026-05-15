package acc.br.projetoFinal.Accenture.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para ErrorResponse")
class ErrorResponseTest {

    private static final LocalDateTime TIMESTAMP = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
    private static final int STATUS = 400;
    private static final String ERROR = "Bad Request";
    private static final String MESSAGE = "Requisição inválida";

    // -------------------------------------------------------------------------
    // @NoArgsConstructor
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve criar objeto com construtor padrão e todos os campos nulos/zero")
    void deveCriarComConstrutorPadrao() {
        ErrorResponse response = new ErrorResponse();

        assertNull(response.getTimestamp());
        assertEquals(0, response.getStatus());
        assertNull(response.getError());
        assertNull(response.getMessage());
    }

    // -------------------------------------------------------------------------
    // @AllArgsConstructor
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve criar objeto com construtor completo")
    void deveCriarComConstrutorCompleto() {
        ErrorResponse response = new ErrorResponse(TIMESTAMP, STATUS, ERROR, MESSAGE);

        assertEquals(TIMESTAMP, response.getTimestamp());
        assertEquals(STATUS, response.getStatus());
        assertEquals(ERROR, response.getError());
        assertEquals(MESSAGE, response.getMessage());
    }

    // -------------------------------------------------------------------------
    // @Builder
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve criar objeto via builder com todos os campos")
    void deveCriarViaBuilderCompleto() {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(TIMESTAMP)
                .status(STATUS)
                .error(ERROR)
                .message(MESSAGE)
                .build();

        assertEquals(TIMESTAMP, response.getTimestamp());
        assertEquals(STATUS, response.getStatus());
        assertEquals(ERROR, response.getError());
        assertEquals(MESSAGE, response.getMessage());
    }

    @Test
    @DisplayName("Deve criar objeto via builder sem nenhum campo preenchido")
    void deveCriarViaBuilderVazio() {
        ErrorResponse response = ErrorResponse.builder().build();

        assertNull(response.getTimestamp());
        assertEquals(0, response.getStatus());
        assertNull(response.getError());
        assertNull(response.getMessage());
    }

    @Test
    @DisplayName("Deve criar objeto via builder com campos parciais")
    void deveCriarViaBuilderParcial() {
        ErrorResponse response = ErrorResponse.builder()
                .status(404)
                .message("Recurso não encontrado")
                .build();

        assertNull(response.getTimestamp());
        assertEquals(404, response.getStatus());
        assertNull(response.getError());
        assertEquals("Recurso não encontrado", response.getMessage());
    }

    // -------------------------------------------------------------------------
    // @Data — Setters
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve definir e obter timestamp via setter")
    void deveDefinirTimestamp() {
        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(TIMESTAMP);
        assertEquals(TIMESTAMP, response.getTimestamp());
    }

    @Test
    @DisplayName("Deve definir e obter status via setter")
    void deveDefinirStatus() {
        ErrorResponse response = new ErrorResponse();
        response.setStatus(STATUS);
        assertEquals(STATUS, response.getStatus());
    }

    @Test
    @DisplayName("Deve definir e obter error via setter")
    void deveDefinirError() {
        ErrorResponse response = new ErrorResponse();
        response.setError(ERROR);
        assertEquals(ERROR, response.getError());
    }

    @Test
    @DisplayName("Deve definir e obter message via setter")
    void deveDefinirMessage() {
        ErrorResponse response = new ErrorResponse();
        response.setMessage(MESSAGE);
        assertEquals(MESSAGE, response.getMessage());
    }

    // -------------------------------------------------------------------------
    // @Data — equals / hashCode
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Dois objetos com mesmos valores devem ser iguais")
    void doisObjetosComMesmosValoresDevemSerIguais() {
        ErrorResponse r1 = new ErrorResponse(TIMESTAMP, STATUS, ERROR, MESSAGE);
        ErrorResponse r2 = new ErrorResponse(TIMESTAMP, STATUS, ERROR, MESSAGE);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    @DisplayName("Objeto deve ser igual a si mesmo")
    void objetoDeveSerIgualASiMesmo() {
        ErrorResponse response = new ErrorResponse(TIMESTAMP, STATUS, ERROR, MESSAGE);

        assertEquals(response, response);
    }

    @Test
    @DisplayName("Objeto não deve ser igual a null")
    void objetoNaoDeveSerIgualANull() {
        ErrorResponse response = new ErrorResponse(TIMESTAMP, STATUS, ERROR, MESSAGE);

        assertNotEquals(null, response);
    }

    @Test
    @DisplayName("Objeto não deve ser igual a tipo diferente")
    void objetoNaoDeveSerIgualATipoDiferente() {
        ErrorResponse response = new ErrorResponse(TIMESTAMP, STATUS, ERROR, MESSAGE);

        assertNotEquals("string", response);
    }

    @Test
    @DisplayName("Objetos com status diferentes não devem ser iguais")
    void objetosComStatusDiferentesNaoDevemSerIguais() {
        ErrorResponse r1 = new ErrorResponse(TIMESTAMP, 400, ERROR, MESSAGE);
        ErrorResponse r2 = new ErrorResponse(TIMESTAMP, 500, ERROR, MESSAGE);

        assertNotEquals(r1, r2);
    }

    @Test
    @DisplayName("Objetos com error diferentes não devem ser iguais")
    void objetosComErrorDiferentesNaoDevemSerIguais() {
        ErrorResponse r1 = new ErrorResponse(TIMESTAMP, STATUS, "Bad Request", MESSAGE);
        ErrorResponse r2 = new ErrorResponse(TIMESTAMP, STATUS, "Not Found", MESSAGE);

        assertNotEquals(r1, r2);
    }

    @Test
    @DisplayName("Objetos com message diferentes não devem ser iguais")
    void objetosComMessageDiferentesNaoDevemSerIguais() {
        ErrorResponse r1 = new ErrorResponse(TIMESTAMP, STATUS, ERROR, "msg A");
        ErrorResponse r2 = new ErrorResponse(TIMESTAMP, STATUS, ERROR, "msg B");

        assertNotEquals(r1, r2);
    }

    @Test
    @DisplayName("Objetos com timestamp diferentes não devem ser iguais")
    void objetosComTimestampDiferentesNaoDevemSerIguais() {
        ErrorResponse r1 = new ErrorResponse(TIMESTAMP, STATUS, ERROR, MESSAGE);
        ErrorResponse r2 = new ErrorResponse(TIMESTAMP.plusHours(1), STATUS, ERROR, MESSAGE);

        assertNotEquals(r1, r2);
    }

    // -------------------------------------------------------------------------
    // @Data — toString
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("toString deve conter os valores dos campos")
    void toStringDeveConterValoresDosCampos() {
        ErrorResponse response = new ErrorResponse(TIMESTAMP, STATUS, ERROR, MESSAGE);
        String str = response.toString();

        assertTrue(str.contains(String.valueOf(STATUS)));
        assertTrue(str.contains(ERROR));
        assertTrue(str.contains(MESSAGE));
    }

    @Test
    @DisplayName("toString não deve ser nulo")
    void toStringNaoDeveSerNulo() {
        ErrorResponse response = new ErrorResponse();
        assertNotNull(response.toString());
    }

    // -------------------------------------------------------------------------
    // Casos de borda
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve aceitar status 0")
    void deveAceitarStatusZero() {
        ErrorResponse response = ErrorResponse.builder().status(0).build();
        assertEquals(0, response.getStatus());
    }

    @Test
    @DisplayName("Deve aceitar campos nulos via setter")
    void deveAceitarCamposNulosViaSetter() {
        ErrorResponse response = new ErrorResponse(TIMESTAMP, STATUS, ERROR, MESSAGE);
        response.setTimestamp(null);
        response.setError(null);
        response.setMessage(null);

        assertNull(response.getTimestamp());
        assertNull(response.getError());
        assertNull(response.getMessage());
    }

    @Test
    @DisplayName("Dois objetos padrão (sem args) devem ser iguais entre si")
    void doisObjetosPadraoDevemSerIguais() {
        ErrorResponse r1 = new ErrorResponse();
        ErrorResponse r2 = new ErrorResponse();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }
}