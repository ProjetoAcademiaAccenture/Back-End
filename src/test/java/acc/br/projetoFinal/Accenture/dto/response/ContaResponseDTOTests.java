package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.enums.TipoConta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("ContaResponseDTO - Testes")
class ContaResponseDTOTests {

    @Test
    @DisplayName("Deve criar ContaResponseDTO com construtor padrão")
    void deveCriarComConstrutorPadrao() {
        ContaResponseDTO dto = new ContaResponseDTO();

        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getNumeroConta());
        assertNull(dto.getSaldo());
        assertNull(dto.getTipo());
        assertFalse(dto.isAtivo());
    }

    @Test
    @DisplayName("Deve criar ContaResponseDTO com construtor completo")
    void deveCriarComConstrutorCompleto() {
        Long id = 1L;
        String numeroConta = "EMPRESA-001";
        BigDecimal saldo = new BigDecimal("10000.00");
        String tipo = "CORRENTE";
        boolean ativo = true;

        ContaResponseDTO dto = new ContaResponseDTO(id, numeroConta, saldo, tipo, ativo);

        assertEquals(id, dto.getId());
        assertEquals(numeroConta, dto.getNumeroConta());
        assertEquals(saldo, dto.getSaldo());
        assertEquals(tipo, dto.getTipo());
        assertTrue(dto.isAtivo());
    }

    @Test
    @DisplayName("Deve criar ContaResponseDTO usando Builder")
    void deveCriarUsandoBuilder() {
        Long id = 2L;
        String numeroConta = "POUPANCA-001";
        BigDecimal saldo = new BigDecimal("5000.00");
        String tipo = "POUPANCA";
        boolean ativo = true;

        ContaResponseDTO dto = ContaResponseDTO.builder()
                .id(id)
                .numeroConta(numeroConta)
                .saldo(saldo)
                .tipo(tipo)
                .ativo(ativo)
                .build();

        assertEquals(id, dto.getId());
        assertEquals(numeroConta, dto.getNumeroConta());
        assertEquals(saldo, dto.getSaldo());
        assertEquals(tipo, dto.getTipo());
        assertTrue(dto.isAtivo());
    }

    @Test
    @DisplayName("Deve atualizar campos via setters")
    void deveAtualizarCamposViaSetters() {
        ContaResponseDTO dto = new ContaResponseDTO();

        dto.setId(5L);
        dto.setNumeroConta("INVESTIMENTO-001");
        dto.setSaldo(new BigDecimal("50000.00"));
        dto.setTipo("CORRENTE");
        dto.setAtivo(true);

        assertEquals(5L, dto.getId());
        assertEquals("INVESTIMENTO-001", dto.getNumeroConta());
        assertEquals(new BigDecimal("50000.00"), dto.getSaldo());
        assertEquals("CORRENTE", dto.getTipo());
        assertTrue(dto.isAtivo());
    }

    @Test
    @DisplayName("Deve criar ContaResponseDTO com conta inativa")
    void deveCriarComContaInativa() {
        ContaResponseDTO dto = ContaResponseDTO.builder()
                .id(10L)
                .numeroConta("INATIVA-001")
                .saldo(new BigDecimal("0.00"))
                .tipo("CORRENTE")
                .ativo(false)
                .build();

        assertEquals(10L, dto.getId());
        assertEquals("INATIVA-001", dto.getNumeroConta());
        assertEquals(new BigDecimal("0.00"), dto.getSaldo());
        assertFalse(dto.isAtivo());
    }

    @Test
    @DisplayName("Deve criar ContaResponseDTO com valores null")
    void deveCriarComValoresNull() {
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
    @DisplayName("Deve converter Conta para ContaResponseDTO")
    void deveConverterContaParaDTO() {
        // Criar uma Conta mock
        Conta conta = new Conta();
        conta.setId(1L);
        conta.setNumeroConta("EMPRESA-001");
        conta.setSaldo(new BigDecimal("10000.00"));
        conta.setTipo(TipoConta.CORRENTE);
        conta.setAtivo(true);

        // Converter para DTO
        ContaResponseDTO dto = ContaResponseDTO.fromEntity(conta);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("EMPRESA-001", dto.getNumeroConta());
        assertEquals(new BigDecimal("10000.00"), dto.getSaldo());
        assertEquals("CORRENTE", dto.getTipo());
        assertTrue(dto.isAtivo());
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
    @DisplayName("Deve preservar tipo de conta como string na conversão")
    void devePreservarTipoContaComoString() {
        Conta conta = new Conta();
        conta.setId(3L);
        conta.setNumeroConta("CORRENTE-003");
        conta.setSaldo(new BigDecimal("25000.00"));
        conta.setTipo(TipoConta.CORRENTE);
        conta.setAtivo(true);

        ContaResponseDTO dto = ContaResponseDTO.fromEntity(conta);

        assertNotNull(dto.getTipo());
        assertTrue(dto.getTipo() instanceof String);
        assertEquals("CORRENTE", dto.getTipo());
    }

    @Test
    @DisplayName("Deve testar igualdade entre DTOs com mesmo conteúdo")
    void deveTestarIgualdadeEntreDTOs() {
        ContaResponseDTO dto1 = ContaResponseDTO.builder()
                .id(1L)
                .numeroConta("EMPRESA-001")
                .saldo(new BigDecimal("10000.00"))
                .tipo("CORRENTE")
                .ativo(true)
                .build();

        ContaResponseDTO dto2 = ContaResponseDTO.builder()
                .id(1L)
                .numeroConta("EMPRESA-001")
                .saldo(new BigDecimal("10000.00"))
                .tipo("CORRENTE")
                .ativo(true)
                .build();

        assertEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Deve suportar saldos negativos")
    void deveSuportarSaldosNegativos() {
        ContaResponseDTO dto = ContaResponseDTO.builder()
                .id(7L)
                .numeroConta("NEGATIVA-001")
                .saldo(new BigDecimal("-500.00"))
                .tipo("CORRENTE")
                .ativo(true)
                .build();

        assertEquals(new BigDecimal("-500.00"), dto.getSaldo());
        assertTrue(dto.getSaldo().signum() < 0);
    }

    @Test
    @DisplayName("Deve suportar saldos muito grandes")
    void deveSuportarSaldosMuitoGrandes() {
        BigDecimal saldoGrande = new BigDecimal("999999999.99");
        
        ContaResponseDTO dto = ContaResponseDTO.builder()
                .id(8L)
                .numeroConta("MILIONARIA-001")
                .saldo(saldoGrande)
                .tipo("CORRENTE")
                .ativo(true)
                .build();

        assertEquals(saldoGrande, dto.getSaldo());
    }
}
