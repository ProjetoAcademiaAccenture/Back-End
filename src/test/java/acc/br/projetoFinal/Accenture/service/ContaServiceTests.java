package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.enums.TipoExtrato;
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

@DisplayName("ContaService - Testes Positivos")
class ContaServiceTests {

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
                .saldo(new BigDecimal("5000.00"))
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
    @DisplayName("✓ Deve depositar com sucesso")
    void testDepositarComSucesso() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);
        when(extratoRepository.save(any())).thenReturn(null);

        contaService.depositar(1L, new BigDecimal("500.00"));

        verify(contaRepository).findById(1L);
        verify(contaRepository).save(any(Conta.class));
        verify(extratoRepository).save(any());
        assertEquals(new BigDecimal("5500.00"), conta.getSaldo());
    }

    @Test
    @DisplayName("✓ Deve transferir com saldo suficiente")
    void testTransferirComSaldoSuficiente() {
        when(contaRepository.findByClienteId(1L)).thenReturn(Optional.of(conta));
        when(contaRepository.findByTipo(TipoConta.JURIDICA)).thenReturn(Optional.of(contaEmpresa));
        when(contaRepository.save(any(Conta.class))).thenReturn(null);
        when(extratoRepository.save(any())).thenReturn(null);

        contaService.transferir(1L, new BigDecimal("1000.00"), pedido);

        verify(contaRepository).findByClienteId(1L);
        verify(contaRepository).findByTipo(TipoConta.JURIDICA);
        verify(contaRepository, times(2)).save(any(Conta.class));
        verify(extratoRepository, times(2)).save(any());
        assertEquals(new BigDecimal("4000.00"), conta.getSaldo());
        assertEquals(new BigDecimal("11000.00"), contaEmpresa.getSaldo());
    }

    @Test
    @DisplayName("✓ Deve estornar com multa com sucesso")
    void testEstornarComMultaComSucesso() {
        when(contaRepository.findByClienteId(1L)).thenReturn(Optional.of(conta));
        when(contaRepository.findByTipo(TipoConta.JURIDICA)).thenReturn(Optional.of(contaEmpresa));
        when(contaRepository.save(any(Conta.class))).thenReturn(null);
        when(extratoRepository.save(any())).thenReturn(null);

        BigDecimal estorno = new BigDecimal("500.00");
        BigDecimal multa = new BigDecimal("50.00");

        contaService.estornarComMulta(1L, estorno, multa, pedido);

        verify(contaRepository).findByClienteId(1L);
        verify(contaRepository).findByTipo(TipoConta.JURIDICA);
        verify(contaRepository, times(2)).save(any(Conta.class));
        verify(extratoRepository, times(3)).save(any());
        // O saldo da conta cliente aumenta com o estorno
        assertEquals(new BigDecimal("5500.00"), conta.getSaldo());
        // O saldo da empresa diminui com o estorno (não com a multa)
        assertEquals(new BigDecimal("9500.00"), contaEmpresa.getSaldo());
    }
}
