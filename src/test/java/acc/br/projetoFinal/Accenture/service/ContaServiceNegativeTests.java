package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.enums.TipoConta;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.exception.SaldoInsuficienteException;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.model.Pedido;
import acc.br.projetoFinal.Accenture.repository.ContaRepository;
import acc.br.projetoFinal.Accenture.repository.ExtratoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("ContaService - Testes Negativos")
class ContaServiceNegativeTests {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private ExtratoRepository extratoRepository;

    @InjectMocks
    private ContaService contaService;

    private Conta conta;
    private Conta contaEmpresa;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        conta = Conta.builder()
                .id(1L)
                .numeroConta("123456")
                .saldo(new BigDecimal("100.00"))
                .build();

        contaEmpresa = Conta.builder()
                .id(2L)
                .numeroConta("999999")
                .saldo(new BigDecimal("10000.00"))
                .tipo(TipoConta.JURIDICA)
                .build();

        pedido = Pedido.builder()
                .id(1L)
                .valorTotal(new BigDecimal("1000.00"))
                .build();
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao depositar em conta inexistente")
    void testDepositarContaInexistente() {
        when(contaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () ->
                contaService.depositar(99L, new BigDecimal("500.00")));

        verify(contaRepository).findById(99L);
        verify(extratoRepository, never()).save(any());
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao transferir com conta cliente inexistente")
    void testTransferirContaClienteInexistente() {
        when(contaRepository.findByClienteId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () ->
                contaService.transferir(99L, new BigDecimal("1000.00"), pedido));

        verify(contaRepository).findByClienteId(99L);
        verify(extratoRepository, never()).save(any());
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao transferir com conta empresa inexistente")
    void testTransferirContaEmpresaInexistente() {
        when(contaRepository.findByClienteId(1L)).thenReturn(Optional.of(conta));
        when(contaRepository.findByTipo(TipoConta.JURIDICA)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () ->
                contaService.transferir(1L, new BigDecimal("1000.00"), pedido));

        verify(contaRepository).findByClienteId(1L);
        verify(contaRepository).findByTipo(TipoConta.JURIDICA);
        verify(extratoRepository, never()).save(any());
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao transferir com saldo insuficiente")
    void testTransferirSaldoInsuficiente() {
        when(contaRepository.findByClienteId(1L)).thenReturn(Optional.of(conta));
        when(contaRepository.findByTipo(TipoConta.JURIDICA)).thenReturn(Optional.of(contaEmpresa));

        assertThrows(SaldoInsuficienteException.class, () ->
                contaService.transferir(1L, new BigDecimal("1000.00"), pedido));

        verify(contaRepository).findByClienteId(1L);
        verify(contaRepository).findByTipo(TipoConta.JURIDICA);
        verify(contaRepository, never()).save(any());
        verify(extratoRepository, never()).save(any());
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao estornar com conta cliente inexistente")
    void testEstornarContaClienteInexistente() {
        when(contaRepository.findByClienteId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () ->
                contaService.estornarComMulta(99L, new BigDecimal("500.00"), new BigDecimal("50.00"), pedido));

        verify(contaRepository).findByClienteId(99L);
        verify(extratoRepository, never()).save(any());
    }

    @Test
    @DisplayName("✗ Deve lançar exceção ao estornar com conta empresa inexistente")
    void testEstornarContaEmpresaInexistente() {
        when(contaRepository.findByClienteId(1L)).thenReturn(Optional.of(conta));
        when(contaRepository.findByTipo(TipoConta.JURIDICA)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () ->
                contaService.estornarComMulta(1L, new BigDecimal("500.00"), new BigDecimal("50.00"), pedido));

        verify(contaRepository).findByClienteId(1L);
        verify(contaRepository).findByTipo(TipoConta.JURIDICA);
        verify(extratoRepository, never()).save(any());
    }
}
