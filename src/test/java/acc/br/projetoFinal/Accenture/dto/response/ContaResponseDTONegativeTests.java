package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.enums.TipoConta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("ContaResponseDTO - Testes Negativos")
class ContaResponseDTONegativeTests {

    @Test
    @DisplayName("Deve criar ContaResponseDTO com todos os valores null")
    void deveCriarComTodosValoresNull() {
        ContaResponseDTO dto = ContaResponseDTO.builder()
                .id(null)
                .numeroConta(null)
                .saldo(null)
                .tipo(null)
                .ativo(false)
                .build();

        assertNull(dto.getId());
        assertNull(dto.getNumeroConta());
        assertNull(dto.getSaldo());
        assertNull(dto.getTipo());
        assertFalse(dto.isAtivo());
    }

    @Test
    @DisplayName("Deve criar ContaResponseDTO com id null")
    void deveCriarComIdNull() {
        ContaResponseDTO dto = ContaResponseDTO.builder()
                .id(null)
                .numeroConta("CONTA-001")
                .saldo(new BigDecimal("1000.00"))
                .tipo("CORRENTE")
                .ativo(true)
                .build();

        assertNull(dto.getId());
    }

    @Test
    @DisplayName("Deve criar ContaResponseDTO com numeroConta null")
    void deveCriarComNumeroContaNull() {
        ContaResponseDTO dto = ContaResponseDTO.builder()
                .id(1L)
                .numeroConta(null)
                .saldo(new BigDecimal("1000.00"))
                .tipo("CORRENTE")
                .ativo(true)
                .build();

        assertNull(dto.getNumeroConta());
    }

    @Test
    @DisplayName("Deve criar ContaResponseDTO com saldo null")
    void deveCriarComSaldoNull() {
        ContaResponseDTO dto = ContaResponseDTO.builder()
                .id(1L)
                .numeroConta("CONTA-001")
                .saldo(null)
                .tipo("CORRENTE")
                .ativo(true)
                .build();

        assertNull(dto.getSaldo());
    }

    @Test
    @DisplayName("Deve criar ContaResponseDTO com tipo null")
    void deveCriarComTipoNull() {
        ContaResponseDTO dto = ContaResponseDTO.builder()
                .id(1L)
                .numeroConta("CONTA-001")
                .saldo(new BigDecimal("1000.00"))
                .tipo(null)
                .ativo(true)
                .build();

        assertNull(dto.getTipo());
    }

    @Test
    @DisplayName("Deve criar ContaResponseDTO com id negativo")
    void deveCriarComIdNegativo() {
        ContaResponseDTO dto = ContaResponseDTO.builder()
                .id(-1L)
                .numeroConta("CONTA-001")
                .saldo(new BigDecimal("1000.00"))
                .tipo("CORRENTE")
                .ativo(true)
                .build();

        assertTrue(dto.getId() < 0);
    }

    @Test
    @DisplayName("Deve criar ContaResponseDTO com saldo negativo")
    void deveCriarComSaldoNegativo() {
        ContaResponseDTO dto = ContaResponseDTO.builder()
                .id(1L)
                .numeroConta("CONTA-001")
                .saldo(new BigDecimal("-500.00"))
                .tipo("CORRENTE")
                .ativo(true)
                .build();

        assertEquals(new BigDecimal("-500.00"), dto.getSaldo());
        assertTrue(dto.getSaldo().signum() < 0);
    }

    @Test
    @DisplayName("Deve criar ContaResponseDTO com saldo muito negativo")
    void deveCriarComSaldoMuitoNegativo() {
        BigDecimal saldoMuitoNegativo = new BigDecimal("-999999999.99");
        
        ContaResponseDTO dto = ContaResponseDTO.builder()
                .id(1L)
                .numeroConta("ENDIVIDADA-001")
                .saldo(saldoMuitoNegativo)
                .tipo("CORRENTE")
                .ativo(true)
                .build();

        assertEquals(saldoMuitoNegativo, dto.getSaldo());
    }

    @Test
    @DisplayName("Deve criar ContaResponseDTO com saldo zero")
    void deveCriarComSaldoZero() {
        ContaResponseDTO dto = ContaResponseDTO.builder()
                .id(1L)
                .numeroConta("VAZIA-001")
                .saldo(BigDecimal.ZERO)
                .tipo("CORRENTE")
                .ativo(false)
                .build();

        assertEquals(BigDecimal.ZERO, dto.getSaldo());
    }

    @Test
    @DisplayName("Deve criar ContaResponseDTO com numeroConta vazio")
    void deveCriarComNumeroContaVazio() {
        ContaResponseDTO dto = ContaResponseDTO.builder()
                .id(1L)
                .numeroConta("")
                .saldo(new BigDecimal("1000.00"))
                .tipo("CORRENTE")
                .ativo(true)
                .build();

        assertEquals("", dto.getNumeroConta());
    }

    @Test
    @DisplayName("Deve criar ContaResponseDTO com tipo vazio")
    void deveCriarComTipoVazio() {
        ContaResponseDTO dto = ContaResponseDTO.builder()
                .id(1L)
                .numeroConta("CONTA-001")
                .saldo(new BigDecimal("1000.00"))
                .tipo("")
                .ativo(true)
                .build();

        assertEquals("", dto.getTipo());
    }

    @Test
    @DisplayName("Deve criar ContaResponseDTO com tipo inválido")
    void deveCriarComTipoInvalido() {
        ContaResponseDTO dto = ContaResponseDTO.builder()
                .id(1L)
                .numeroConta("CONTA-001")
                .saldo(new BigDecimal("1000.00"))
                .tipo("TIPO_INEXISTENTE")
                .ativo(true)
                .build();

        assertEquals("TIPO_INEXISTENTE", dto.getTipo());
    }

    @Test
    @DisplayName("Deve criar ContaResponseDTO inativa com saldo positivo")
    void deveCriarInativaComSaldoPositivo() {
        ContaResponseDTO dto = ContaResponseDTO.builder()
                .id(1L)
                .numeroConta("INATIVA-001")
                .saldo(new BigDecimal("5000.00"))
                .tipo("CORRENTE")
                .ativo(false)
                .build();

        assertFalse(dto.isAtivo());
        assertTrue(dto.getSaldo().signum() > 0);
    }

    @Test
    @DisplayName("Deve converter Conta inativa para ContaResponseDTO")
    void deveConverterContaInativaParaDTO() {
        Conta conta = new Conta();
        conta.setId(5L);
        conta.setNumeroConta("FECHADA-001");
        conta.setSaldo(new BigDecimal("0.00"));
        conta.setTipo(TipoConta.POUPANCA);
        conta.setAtivo(false);

        ContaResponseDTO dto = ContaResponseDTO.fromEntity(conta);

        assertEquals(5L, dto.getId());
        assertEquals("FECHADA-001", dto.getNumeroConta());
        assertEquals(new BigDecimal("0.00"), dto.getSaldo());
        assertEquals("POUPANCA", dto.getTipo());
        assertFalse(dto.isAtivo());
    }

    @Test
    @DisplayName("Deve aceitar numeroConta muito grande")
    void deveAceitarNumeroContaMuitoGrande() {
        String numeroContaGrande = "A".repeat(1000);
        
        ContaResponseDTO dto = ContaResponseDTO.builder()
                .id(1L)
                .numeroConta(numeroContaGrande)
                .saldo(new BigDecimal("1000.00"))
                .tipo("CORRENTE")
                .ativo(true)
                .build();

        assertEquals(numeroContaGrande, dto.getNumeroConta());
    }

    @Test
    @DisplayName("Deve aceitar tipo muito grande")
    void deveAceitarTipoMuitoGrande() {
        String tipoGrande = "A".repeat(1000);
        
        ContaResponseDTO dto = ContaResponseDTO.builder()
                .id(1L)
                .numeroConta("CONTA-001")
                .saldo(new BigDecimal("1000.00"))
                .tipo(tipoGrande)
                .ativo(true)
                .build();

        assertEquals(tipoGrande, dto.getTipo());
    }

    @Test
    @DisplayName("Deve ter diferença entre DTOs com dados diferentes")
    void deveTerDiferencaEntreDTOs() {
        ContaResponseDTO dto1 = ContaResponseDTO.builder()
                .id(1L)
                .numeroConta("CONTA-001")
                .saldo(new BigDecimal("1000.00"))
                .tipo("CORRENTE")
                .ativo(true)
                .build();

        ContaResponseDTO dto2 = ContaResponseDTO.builder()
                .id(2L)
                .numeroConta("CONTA-002")
                .saldo(new BigDecimal("2000.00"))
                .tipo("POUPANCA")
                .ativo(false)
                .build();

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Deve aceitar saldo com muitas casas decimais")
    void deveAceitarSaldoComMuitasCasasDecimais() {
        BigDecimal saldoPreciso = new BigDecimal("9999.999999999");
        
        ContaResponseDTO dto = ContaResponseDTO.builder()
                .id(1L)
                .numeroConta("PRECISA-001")
                .saldo(saldoPreciso)
                .tipo("CORRENTE")
                .ativo(true)
                .build();

        assertEquals(saldoPreciso, dto.getSaldo());
    }
}
