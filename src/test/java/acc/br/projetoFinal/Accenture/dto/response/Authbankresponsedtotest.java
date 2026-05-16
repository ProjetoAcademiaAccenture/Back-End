package acc.br.projetoFinal.Accenture.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthBankResponseDTOTest {

    // -----------------------------------------------------------------------
    // Dados de apoio
    // -----------------------------------------------------------------------
    private AuthBankResponseDTO buildSample() {
        return AuthBankResponseDTO.builder()
                .token("eyJhbGciOiJIUzI1NiJ9.sample")
                .clienteId(1L)
                .contaId(42L)
                .numeroConta("0001-2")
                .saldo("1500.00")
                .limiteCeditoDisponivel("5000.00")
                .tipoConta("CORRENTE")
                .build();
    }

    // -----------------------------------------------------------------------
    // Builder + Getters
    // -----------------------------------------------------------------------
    @Test
    void deveCriarDTOComTodosOsCamposViaBuilder() {
        AuthBankResponseDTO dto = buildSample();

        assertEquals("eyJhbGciOiJIUzI1NiJ9.sample", dto.getToken());
        assertEquals(1L,          dto.getClienteId());
        assertEquals(42L,         dto.getContaId());
        assertEquals("0001-2",    dto.getNumeroConta());
        assertEquals("1500.00",   dto.getSaldo());
        assertEquals("5000.00",   dto.getLimiteCeditoDisponivel());
        assertEquals("CORRENTE",  dto.getTipoConta());
    }

    // -----------------------------------------------------------------------
    // Setters  (@Data gera setters para todos os campos)
    // -----------------------------------------------------------------------
    @Test
    void deveAtualizarCamposViaSetters() {
        AuthBankResponseDTO dto = buildSample();

        dto.setToken("novo-token");
        dto.setClienteId(99L);
        dto.setContaId(7L);
        dto.setNumeroConta("9999-0");
        dto.setSaldo("200.50");
        dto.setLimiteCeditoDisponivel("1000.00");
        dto.setTipoConta("POUPANCA");

        assertEquals("novo-token", dto.getToken());
        assertEquals(99L,         dto.getClienteId());
        assertEquals(7L,          dto.getContaId());
        assertEquals("9999-0",    dto.getNumeroConta());
        assertEquals("200.50",    dto.getSaldo());
        assertEquals("1000.00",   dto.getLimiteCeditoDisponivel());
        assertEquals("POUPANCA",  dto.getTipoConta());
    }

    // -----------------------------------------------------------------------
    // equals / hashCode
    // -----------------------------------------------------------------------
    @Test
    void doisDTOsComMesmosDadosDevemSerIguais() {
        AuthBankResponseDTO dto1 = buildSample();
        AuthBankResponseDTO dto2 = buildSample();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void doisDTOsComDadosDiferentesNaoDevemSerIguais() {
        AuthBankResponseDTO dto1 = buildSample();
        AuthBankResponseDTO dto2 = buildSample();
        dto2.setToken("token-diferente");

        assertNotEquals(dto1, dto2);
        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void dtoNaoDeveSerIgualANulo() {
        AuthBankResponseDTO dto = buildSample();
        assertNotEquals(null, dto);
    }

    @Test
    void dtoDeveSerIgualASiMesmo() {
        AuthBankResponseDTO dto = buildSample();
        assertEquals(dto, dto);
    }

    @Test
    void dtoNaoDeveSerIgualAObjetoDeOutroTipo() {
        AuthBankResponseDTO dto = buildSample();
        assertNotEquals("string", dto);
    }

    // -----------------------------------------------------------------------
    // toString
    // -----------------------------------------------------------------------
    @Test
    void toStringDeveConterOsValoresDoCampo() {
        AuthBankResponseDTO dto = buildSample();
        String str = dto.toString();

        assertTrue(str.contains("eyJhbGciOiJIUzI1NiJ9.sample"));
        assertTrue(str.contains("1"));          // clienteId
        assertTrue(str.contains("42"));         // contaId
        assertTrue(str.contains("0001-2"));
        assertTrue(str.contains("1500.00"));
        assertTrue(str.contains("5000.00"));
        assertTrue(str.contains("CORRENTE"));
    }

    // -----------------------------------------------------------------------
    // Builder com campos nulos (garante cobertura dos branches de null)
    // -----------------------------------------------------------------------
    @Test
    void deveCriarDTOComCamposNulos() {
        AuthBankResponseDTO dto = AuthBankResponseDTO.builder().build();

        assertNull(dto.getToken());
        assertNull(dto.getClienteId());
        assertNull(dto.getContaId());
        assertNull(dto.getNumeroConta());
        assertNull(dto.getSaldo());
        assertNull(dto.getLimiteCeditoDisponivel());
        assertNull(dto.getTipoConta());
    }

    @Test
    void doisDTOsNulosDevemSerIguais() {
        AuthBankResponseDTO dto1 = AuthBankResponseDTO.builder().build();
        AuthBankResponseDTO dto2 = AuthBankResponseDTO.builder().build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void dtoComCampoNuloNaoDeveSerIgualADtoComCampoPreenchido() {
        AuthBankResponseDTO comNulo     = AuthBankResponseDTO.builder().build();
        AuthBankResponseDTO semNulo     = buildSample();

        assertNotEquals(comNulo, semNulo);
    }
}