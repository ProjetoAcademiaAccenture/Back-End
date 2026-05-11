package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.enums.TipoConta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("ContaResponseDTO - Testes Positivos")
class ContaResponseDTOTests {

    @Test
    @DisplayName("Deve criar ContaResponseDTO com construtor padrão")
    void deveCriarComConstrutorPadrao() {
        ContaResponseDTO dto = new ContaResponseDTO();

        assertNotNull(dto);
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
