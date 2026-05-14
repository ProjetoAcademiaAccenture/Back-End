package acc.br.projetoFinal.Accenture.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("AuthResponseDTO - Testes unitários")
class AuthResponseDTOTest {

    // -----------------------------------------------------------------------
    // 1. Builder + Getters (gerados pelo @Data/@Builder do Lombok)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Deve construir o DTO com todos os campos via Builder")
    void deveConstruirComTodosOsCampos() {
        AuthResponseDTO dto = AuthResponseDTO.builder()
                .token("jwt-token-abc")
                .clienteId(42L)
                .nome("Maria Silva")
                .email("maria@email.com")
                .tipoCliente("PREMIUM")
                .build();

        assertAll("campos do DTO",
                () -> assertEquals("jwt-token-abc", dto.getToken()),
                () -> assertEquals(42L,             dto.getClienteId()),
                () -> assertEquals("Maria Silva",   dto.getNome()),
                () -> assertEquals("maria@email.com", dto.getEmail()),
                () -> assertEquals("PREMIUM",       dto.getTipoCliente())
        );
    }

    @Test
    @DisplayName("Deve construir o DTO com campos nulos quando não informados")
    void deveConstruirComCamposNulos() {
        AuthResponseDTO dto = AuthResponseDTO.builder().build();

        assertAll("campos nulos",
                () -> assertNull(dto.getToken()),
                () -> assertNull(dto.getClienteId()),
                () -> assertNull(dto.getNome()),
                () -> assertNull(dto.getEmail()),
                () -> assertNull(dto.getTipoCliente())
        );
    }

    // -----------------------------------------------------------------------
    // 2. Setters (gerados pelo @Data do Lombok)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Deve atualizar os campos via Setters")
    void deveAtualizarCamposViaSetters() {
        AuthResponseDTO dto = AuthResponseDTO.builder().build();

        dto.setToken("novo-token");
        dto.setClienteId(99L);
        dto.setNome("João Souza");
        dto.setEmail("joao@email.com");
        dto.setTipoCliente("COMUM");

        assertAll("setters",
                () -> assertEquals("novo-token",    dto.getToken()),
                () -> assertEquals(99L,              dto.getClienteId()),
                () -> assertEquals("João Souza",    dto.getNome()),
                () -> assertEquals("joao@email.com", dto.getEmail()),
                () -> assertEquals("COMUM",         dto.getTipoCliente())
        );
    }

    // -----------------------------------------------------------------------
    // 3. equals() e hashCode() (gerados pelo @Data do Lombok)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Dois DTOs com mesmos valores devem ser iguais")
    void doisDTOsIguaisDevemSerIguais() {
        AuthResponseDTO dto1 = construirDTO();
        AuthResponseDTO dto2 = construirDTO();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("DTOs com valores diferentes não devem ser iguais")
    void doisDTOsDiferentesNaoDevemSerIguais() {
        AuthResponseDTO dto1 = construirDTO();
        AuthResponseDTO dto2 = AuthResponseDTO.builder()
                .token("outro-token")
                .clienteId(1L)
                .nome("Ana")
                .email("ana@email.com")
                .tipoCliente("COMUM")
                .build();

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("DTO deve ser igual a si mesmo")
    void dtoDeveSerIgualASiMesmo() {
        AuthResponseDTO dto = construirDTO();
        assertEquals(dto, dto);
    }

    @Test
    @DisplayName("DTO não deve ser igual a null")
    void dtoNaoDeveSerIgualANull() {
        AuthResponseDTO dto = construirDTO();
        assertNotEquals(null, dto);
    }

    @Test
    @DisplayName("DTO não deve ser igual a objeto de outro tipo")
    void dtoNaoDeveSerIgualAOutroTipo() {
        AuthResponseDTO dto = construirDTO();
        assertNotEquals("uma string qualquer", dto);
    }

    @Test
    @DisplayName("Dois DTOs com clienteId diferente devem ter hashCode diferente")
    void hashCodeDeveDiferirComClienteIdDiferente() {
        AuthResponseDTO dto1 = construirDTO();
        AuthResponseDTO dto2 = AuthResponseDTO.builder()
                .token(dto1.getToken())
                .clienteId(999L)           // diferente
                .nome(dto1.getNome())
                .email(dto1.getEmail())
                .tipoCliente(dto1.getTipoCliente())
                .build();

        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }

    // -----------------------------------------------------------------------
    // 4. toString() (gerado pelo @Data do Lombok)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("toString deve conter os valores dos campos")
    void toStringDeveConterValoresDoCampos() {
        AuthResponseDTO dto = construirDTO();
        String result = dto.toString();

        assertAll("toString",
                () -> assertTrue(result.contains("jwt-token-xyz")),
                () -> assertTrue(result.contains("1")),
                () -> assertTrue(result.contains("Carlos")),
                () -> assertTrue(result.contains("carlos@email.com")),
                () -> assertTrue(result.contains("ADMIN"))
        );
    }

    // -----------------------------------------------------------------------
    // 5. Builder — métodos individuais e toString do builder
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Builder deve permitir sobrescrever valor antes do build()")
    void builderDeveSobrescreverValor() {
        AuthResponseDTO dto = AuthResponseDTO.builder()
                .token("token-inicial")
                .token("token-final")   // sobrescreve
                .clienteId(1L)
                .build();

        assertEquals("token-final", dto.getToken());
    }

    @Test
    @DisplayName("Builder.toString() não deve lançar exceção")
    void builderToStringNaoDeveLancarExcecao() {
        AuthResponseDTO.AuthResponseDTOBuilder builder = AuthResponseDTO.builder()
                .token("t")
                .clienteId(1L)
                .nome("N")
                .email("e@e.com")
                .tipoCliente("C");

        assertDoesNotThrow(() -> {
            String s = builder.toString();
            assertNotNull(s);
        });
    }

    // -----------------------------------------------------------------------
    // 6. Casos de borda
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Deve aceitar strings vazias nos campos de texto")
    void deveAceitarStringsVazias() {
        AuthResponseDTO dto = AuthResponseDTO.builder()
                .token("")
                .clienteId(0L)
                .nome("")
                .email("")
                .tipoCliente("")
                .build();

        assertAll("strings vazias",
                () -> assertEquals("", dto.getToken()),
                () -> assertEquals(0L,  dto.getClienteId()),
                () -> assertEquals("", dto.getNome()),
                () -> assertEquals("", dto.getEmail()),
                () -> assertEquals("", dto.getTipoCliente())
        );
    }

    @Test
    @DisplayName("Deve aceitar clienteId com valor Long.MAX_VALUE")
    void deveAceitarClienteIdMaximo() {
        AuthResponseDTO dto = AuthResponseDTO.builder()
                .clienteId(Long.MAX_VALUE)
                .build();

        assertEquals(Long.MAX_VALUE, dto.getClienteId());
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private AuthResponseDTO construirDTO() {
        return AuthResponseDTO.builder()
                .token("jwt-token-xyz")
                .clienteId(1L)
                .nome("Carlos")
                .email("carlos@email.com")
                .tipoCliente("ADMIN")
                .build();
    }
}