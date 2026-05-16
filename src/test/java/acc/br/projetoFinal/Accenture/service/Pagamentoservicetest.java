package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.PagamentoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.PagamentoResponseDTO;
import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
import acc.br.projetoFinal.Accenture.enums.StatusBoleto;
import acc.br.projetoFinal.Accenture.enums.StatusPagamento;
import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.exception.SaldoInsuficienteException;
import acc.br.projetoFinal.Accenture.model.*;
import acc.br.projetoFinal.Accenture.repository.BoletoRepository;
import acc.br.projetoFinal.Accenture.repository.PagamentoRepository;
import acc.br.projetoFinal.Accenture.repository.PedidoRepository;
import acc.br.projetoFinal.Accenture.repository.TentativaPagamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    // -----------------------------------------------------------------------
    // Mocks
    // -----------------------------------------------------------------------
    @Mock private PagamentoRepository        pagamentoRepository;
    @Mock private PedidoRepository           pedidoRepository;
    @Mock private TentativaPagamentoRepository tentativaPagamentoRepository;
    @Mock private BoletoRepository           boletoRepository;
    @Mock private ContaService               contaService;
    @Mock private EstoqueService             estoqueService;

    @InjectMocks
    private PagamentoService pagamentoService;

    // -----------------------------------------------------------------------
    // Fixtures
    // -----------------------------------------------------------------------
    private Cliente  cliente;
    private Conta    contaCliente;
    private Conta    contaEmpresa;
    private Pedido   pedido;
    private Pagamento pagamento;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);

        contaCliente = new Conta();
        contaCliente.setId(10L);
        contaCliente.setSaldo(new BigDecimal("1000.00"));

        contaEmpresa = new Conta();
        contaEmpresa.setId(99L);
        contaEmpresa.setSaldo(BigDecimal.ZERO);

        pedido = new Pedido();
        pedido.setId(5L);
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.RESERVADO);
        pedido.setValorBruto(new BigDecimal("200.00"));
        pedido.setDesconto(new BigDecimal("10.00"));
        pedido.setValorFinal(new BigDecimal("190.00"));
        pedido.setItens(List.of());

        pagamento = Pagamento.builder()
                .id(1L)
                .pedido(pedido)
                .status(StatusPagamento.PENDENTE)
                .metodo(MetodoPagamento.PIX)
                .valorBruto(pedido.getValorBruto())
                .desconto(pedido.getDesconto())
                .valorFinal(pedido.getValorFinal())
                .dataCriacao(LocalDateTime.now())
                .build();
    }

    // helper para evitar repetição
    private PagamentoRequestDTO dto(Long pagId, MetodoPagamento metodo, String senha) {
        PagamentoRequestDTO d = new PagamentoRequestDTO();
        d.setPagamentoId(pagId);
        d.setMetodoPagamento(metodo);
        d.setSenhaTransacao(senha);
        return d;
    }

    // =======================================================================
    // criarParaPedido
    // =======================================================================

    @Test
    void criarParaPedido_deveSalvarERetornarDTO() {
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamento);

        PagamentoResponseDTO response = pagamentoService.criarParaPedido(pedido, "PIX");

        assertNotNull(response);
        verify(pagamentoRepository).save(any(Pagamento.class));
    }

    @Test
    void criarParaPedido_deveUsarMetodoInformado() {
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamento);

        pagamentoService.criarParaPedido(pedido, "CREDITO");

        ArgumentCaptor<Pagamento> captor = ArgumentCaptor.forClass(Pagamento.class);
        verify(pagamentoRepository).save(captor.capture());
        assertEquals(MetodoPagamento.CREDITO, captor.getValue().getMetodo());
    }

    // =======================================================================
    // processar — pagamento não encontrado
    // =======================================================================

    @Test
    void processar_deveLancarExcecao_quandoPagamentoNaoEncontrado() {
        when(pagamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> pagamentoService.processar(dto(99L, MetodoPagamento.PIX, "senha")));
    }

    // =======================================================================
    // processar — pedido não RESERVADO
    // =======================================================================

    @Test
    void processar_deveLancarExcecao_quandoPedidoNaoReservado() {
        pedido.setStatus(StatusPedido.PAGO);
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));

        assertThrows(IllegalArgumentException.class,
                () -> pagamentoService.processar(dto(1L, MetodoPagamento.PIX, "senha")));
    }

    // =======================================================================
    // processar — BOLETO com boleto já existente → delega pagarBoleto
    // =======================================================================

    @Test
    void processar_boleto_comBoletoExistente_deveDelegarParaPagarBoleto() {
        Boleto boleto = Boleto.builder()
                .id(7L)
                .pagamento(pagamento)
                .status(StatusBoleto.PENDENTE)
                .valor(pagamento.getValorFinal())
                .dataVencimento(LocalDate.now().plusDays(1))
                .build();

        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));
        when(boletoRepository.findByPagamentoId(1L)).thenReturn(Optional.of(boleto));
        when(boletoRepository.findById(7L)).thenReturn(Optional.of(boleto));

        // pagarBoleto interno
        when(contaService.buscarContaDoCliente(1L)).thenReturn(contaCliente);
        when(contaService.buscarContaEmpresa()).thenReturn(contaEmpresa);
        doNothing().when(contaService).validarSenhaTransacao(any(), any());
        doNothing().when(contaService).debitarSaldo(any(), any(), any(), any(), any());
        doNothing().when(contaService).creditarSaldo(any(), any(), any(), any(), any());
        when(boletoRepository.save(any())).thenReturn(boleto);
        when(pagamentoRepository.save(any())).thenReturn(pagamento);
        when(pedidoRepository.save(any())).thenReturn(pedido);

        PagamentoResponseDTO response = pagamentoService.processar(dto(1L, MetodoPagamento.BOLETO, "senha"));
        assertNotNull(response);
    }

    // =======================================================================
    // processar — PIX (débito de saldo) aprovado
    // =======================================================================

    @Test
    void processar_pix_deveAprovar_quandoSaldoSuficiente() {
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));
        when(contaService.buscarContaDoCliente(1L)).thenReturn(contaCliente);
        when(contaService.buscarContaEmpresa()).thenReturn(contaEmpresa);
        doNothing().when(contaService).validarSenhaTransacao(any(), any());
        doNothing().when(contaService).debitarSaldo(any(), any(), any(), any(), any());
        doNothing().when(contaService).creditarSaldo(any(), any(), any(), any(), any());
        when(pagamentoRepository.save(any())).thenReturn(pagamento);
        when(pedidoRepository.save(any())).thenReturn(pedido);

        PagamentoResponseDTO response = pagamentoService.processar(dto(1L, MetodoPagamento.PIX, "senha"));

        assertNotNull(response);
        assertEquals(StatusPagamento.APROVADO, pagamento.getStatus());
        assertEquals(StatusPedido.PAGO, pedido.getStatus());
    }

    // =======================================================================
    // processar — PIX com saldo insuficiente → RECUSADO
    // =======================================================================

    @Test
    void processar_pix_deveRecusar_quandoSaldoInsuficiente() {
        contaCliente.setSaldo(new BigDecimal("1.00")); // menor que 190.00
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));
        when(contaService.buscarContaDoCliente(1L)).thenReturn(contaCliente);
        when(contaService.buscarContaEmpresa()).thenReturn(contaEmpresa);
        doNothing().when(contaService).validarSenhaTransacao(any(), any());
        when(pagamentoRepository.save(any())).thenReturn(pagamento);
        when(pedidoRepository.save(any())).thenReturn(pedido);

        PagamentoResponseDTO response = pagamentoService.processar(dto(1L, MetodoPagamento.PIX, "senha"));

        assertNotNull(response);
        assertEquals(StatusPagamento.RECUSADO, pagamento.getStatus());
    }

    // =======================================================================
    // processar — CREDITO aprovado
    // =======================================================================

    @Test
    void processar_credito_deveAprovar() {
        pagamento.setMetodo(MetodoPagamento.CREDITO);
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));
        when(contaService.buscarContaDoCliente(1L)).thenReturn(contaCliente);
        when(contaService.buscarContaEmpresa()).thenReturn(contaEmpresa);
        doNothing().when(contaService).validarSenhaTransacao(any(), any());
        doNothing().when(contaService).debitarLimiteCredito(any(), any(), any(), any(), any());
        doNothing().when(contaService).creditarSaldo(any(), any(), any(), any(), any());
        when(pagamentoRepository.save(any())).thenReturn(pagamento);
        when(pedidoRepository.save(any())).thenReturn(pedido);

        PagamentoResponseDTO response = pagamentoService.processar(dto(1L, MetodoPagamento.CREDITO, "senha"));

        assertNotNull(response);
        assertEquals(StatusPagamento.APROVADO, pagamento.getStatus());
    }

    // =======================================================================
    // processar — CREDITO lança exceção → RECUSADO
    // =======================================================================

    @Test
    void processar_credito_deveRecusar_quandoDebitarLancaExcecao() {
        pagamento.setMetodo(MetodoPagamento.CREDITO);
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));
        when(contaService.buscarContaDoCliente(1L)).thenReturn(contaCliente);
        when(contaService.buscarContaEmpresa()).thenReturn(contaEmpresa);
        doNothing().when(contaService).validarSenhaTransacao(any(), any());
        doThrow(new RuntimeException("Limite excedido"))
                .when(contaService).debitarLimiteCredito(any(), any(), any(), any(), any());
        when(pagamentoRepository.save(any())).thenReturn(pagamento);
        when(pedidoRepository.save(any())).thenReturn(pedido);

        PagamentoResponseDTO response = pagamentoService.processar(dto(1L, MetodoPagamento.CREDITO, "senha"));

        assertEquals(StatusPagamento.RECUSADO, pagamento.getStatus());
    }

    // =======================================================================
    // processar — BOLETO sem boleto existente (boleto == empty, metodo != BOLETO)
    //             Cobre o branch: metodo == BOLETO && boletoExistente.isEmpty()
    //             Nesse caso o fluxo cai no processamento normal (PIX/débito)
    // =======================================================================

    @Test
    void processar_boleto_semBoletoExistente_deveProcessarComoDebito() {
        pagamento.setMetodo(MetodoPagamento.BOLETO);
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));
        when(boletoRepository.findByPagamentoId(1L)).thenReturn(Optional.empty());
        when(contaService.buscarContaDoCliente(1L)).thenReturn(contaCliente);
        when(contaService.buscarContaEmpresa()).thenReturn(contaEmpresa);
        doNothing().when(contaService).validarSenhaTransacao(any(), any());
        doNothing().when(contaService).debitarSaldo(any(), any(), any(), any(), any());
        doNothing().when(contaService).creditarSaldo(any(), any(), any(), any(), any());
        when(pagamentoRepository.save(any())).thenReturn(pagamento);
        when(pedidoRepository.save(any())).thenReturn(pedido);

        PagamentoResponseDTO response = pagamentoService.processar(dto(1L, MetodoPagamento.BOLETO, "senha"));
        assertNotNull(response);
    }

    // =======================================================================
    // pagarBoleto — boleto não encontrado
    // =======================================================================

    @Test
    void pagarBoleto_deveLancarExcecao_quandoBoletoNaoEncontrado() {
        when(boletoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> pagamentoService.pagarBoleto(99L, "senha"));
    }

    // =======================================================================
    // pagarBoleto — boleto já PAGO
    // =======================================================================

    @Test
    void pagarBoleto_deveLancarExcecao_quandoBoletoJaPago() {
        Boleto boleto = Boleto.builder().id(1L).status(StatusBoleto.PAGO).build();
        when(boletoRepository.findById(1L)).thenReturn(Optional.of(boleto));

        assertThrows(IllegalArgumentException.class,
                () -> pagamentoService.pagarBoleto(1L, "senha"));
    }

    // =======================================================================
    // pagarBoleto — boleto CANCELADO
    // =======================================================================

    @Test
    void pagarBoleto_deveLancarExcecao_quandoBoletoCancelado() {
        Boleto boleto = Boleto.builder().id(1L).status(StatusBoleto.CANCELADO).build();
        when(boletoRepository.findById(1L)).thenReturn(Optional.of(boleto));

        assertThrows(IllegalArgumentException.class,
                () -> pagamentoService.pagarBoleto(1L, "senha"));
    }

    // =======================================================================
    // pagarBoleto — dentro do prazo, saldo suficiente → PAGO
    // =======================================================================

    @Test
    void pagarBoleto_deveAprovar_quandoNoPrazoESaldoSuficiente() {
        Boleto boleto = Boleto.builder()
                .id(1L)
                .pagamento(pagamento)
                .status(StatusBoleto.PENDENTE)
                .valor(new BigDecimal("190.00"))
                .dataVencimento(LocalDate.now().plusDays(2)) // dentro do prazo
                .build();

        when(boletoRepository.findById(1L)).thenReturn(Optional.of(boleto));
        when(contaService.buscarContaDoCliente(1L)).thenReturn(contaCliente);
        when(contaService.buscarContaEmpresa()).thenReturn(contaEmpresa);
        doNothing().when(contaService).validarSenhaTransacao(any(), any());
        doNothing().when(contaService).debitarSaldo(any(), any(), any(), any(), any());
        doNothing().when(contaService).creditarSaldo(any(), any(), any(), any(), any());
        when(boletoRepository.save(any())).thenReturn(boleto);
        when(pagamentoRepository.save(any())).thenReturn(pagamento);
        when(pedidoRepository.save(any())).thenReturn(pedido);

        PagamentoResponseDTO response = pagamentoService.pagarBoleto(1L, "senha");

        assertNotNull(response);
        assertEquals(StatusBoleto.PAGO, boleto.getStatus());
        assertEquals(StatusPagamento.APROVADO, pagamento.getStatus());
        assertEquals(StatusPedido.PAGO, pedido.getStatus());
    }

    // =======================================================================
    // pagarBoleto — vencido → aplica multa, saldo suficiente → PAGO
    // =======================================================================

    @Test
    void pagarBoleto_deveAplicarMulta_quandoVencido() {
        BigDecimal valorOriginal = new BigDecimal("100.00");
        // multa 2% = 2.00 → valorCobrado = 102.00
        contaCliente.setSaldo(new BigDecimal("200.00"));

        Boleto boleto = Boleto.builder()
                .id(2L)
                .pagamento(pagamento)
                .status(StatusBoleto.PENDENTE)
                .valor(valorOriginal)
                .dataVencimento(LocalDate.now().minusDays(1)) // vencido
                .build();

        when(boletoRepository.findById(2L)).thenReturn(Optional.of(boleto));
        when(contaService.buscarContaDoCliente(1L)).thenReturn(contaCliente);
        when(contaService.buscarContaEmpresa()).thenReturn(contaEmpresa);
        doNothing().when(contaService).validarSenhaTransacao(any(), any());
        doNothing().when(contaService).debitarSaldo(any(), any(), any(), any(), any());
        doNothing().when(contaService).creditarSaldo(any(), any(), any(), any(), any());
        when(boletoRepository.save(any())).thenReturn(boleto);
        when(pagamentoRepository.save(any())).thenReturn(pagamento);
        when(pedidoRepository.save(any())).thenReturn(pedido);

        pagamentoService.pagarBoleto(2L, "senha");

        // valor do boleto deve ter sido atualizado para 102.00
        assertEquals(new BigDecimal("102.00"), boleto.getValor());
    }

    // =======================================================================
    // pagarBoleto — saldo insuficiente → SaldoInsuficienteException
    // =======================================================================

    @Test
    void pagarBoleto_deveLancarExcecao_quandoSaldoInsuficiente() {
        contaCliente.setSaldo(new BigDecimal("1.00"));

        Boleto boleto = Boleto.builder()
                .id(3L)
                .pagamento(pagamento)
                .status(StatusBoleto.PENDENTE)
                .valor(new BigDecimal("500.00"))
                .dataVencimento(LocalDate.now().plusDays(1))
                .build();

        when(boletoRepository.findById(3L)).thenReturn(Optional.of(boleto));
        when(contaService.buscarContaDoCliente(1L)).thenReturn(contaCliente);
        when(contaService.buscarContaEmpresa()).thenReturn(contaEmpresa);
        doNothing().when(contaService).validarSenhaTransacao(any(), any());

        assertThrows(SaldoInsuficienteException.class,
                () -> pagamentoService.pagarBoleto(3L, "senha"));
    }

    // =======================================================================
    // pagarBoleto — exceção interna no débito → RuntimeException
    // =======================================================================

    @Test
    void pagarBoleto_deveLancarRuntimeException_quandoDebitarFalha() {
        Boleto boleto = Boleto.builder()
                .id(4L)
                .pagamento(pagamento)
                .status(StatusBoleto.PENDENTE)
                .valor(new BigDecimal("190.00"))
                .dataVencimento(LocalDate.now().plusDays(1))
                .build();

        when(boletoRepository.findById(4L)).thenReturn(Optional.of(boleto));
        when(contaService.buscarContaDoCliente(1L)).thenReturn(contaCliente);
        when(contaService.buscarContaEmpresa()).thenReturn(contaEmpresa);
        doNothing().when(contaService).validarSenhaTransacao(any(), any());
        doThrow(new RuntimeException("Falha bancária"))
                .when(contaService).debitarSaldo(any(), any(), any(), any(), any());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pagamentoService.pagarBoleto(4L, "senha"));

        assertTrue(ex.getMessage().contains("Falha bancária"));
    }

    // =======================================================================
    // cancelar — pagamento não encontrado
    // =======================================================================

    @Test
    void cancelar_deveLancarExcecao_quandoPagamentoNaoEncontrado() {
        when(pagamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> pagamentoService.cancelar(99L));
    }

    // =======================================================================
    // cancelar — status APROVADO → estorno
    // =======================================================================

    @Test
    void cancelar_deveEstornar_quandoPagamentoAprovado() {
        pagamento.setStatus(StatusPagamento.APROVADO);
        pagamento.setValorFinal(new BigDecimal("190.00"));

        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));
        when(contaService.buscarContaDoCliente(1L)).thenReturn(contaCliente);
        when(contaService.buscarContaEmpresa()).thenReturn(contaEmpresa);
        doNothing().when(contaService).creditarSaldo(any(), any(), any(), any(), any());
        doNothing().when(contaService).debitarSaldo(any(), any(), any(), any(), any());
        when(boletoRepository.findByPagamentoId(1L)).thenReturn(Optional.empty());
        doNothing().when(estoqueService).devolverItens(any());
        when(pagamentoRepository.save(any())).thenReturn(pagamento);
        when(pedidoRepository.save(any())).thenReturn(pedido);

        pagamentoService.cancelar(1L);

        assertEquals(StatusPagamento.ESTORNADO, pagamento.getStatus());
        assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
    }

    // =======================================================================
    // cancelar — status PENDENTE → CANCELADO
    // =======================================================================

    @Test
    void cancelar_deveCancelar_quandoPagamentoPendente() {
        pagamento.setStatus(StatusPagamento.PENDENTE);

        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));
        when(boletoRepository.findByPagamentoId(1L)).thenReturn(Optional.empty());
        doNothing().when(estoqueService).devolverItens(any());
        when(pagamentoRepository.save(any())).thenReturn(pagamento);
        when(pedidoRepository.save(any())).thenReturn(pedido);

        pagamentoService.cancelar(1L);

        assertEquals(StatusPagamento.CANCELADO, pagamento.getStatus());
        assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
    }

    // =======================================================================
    // cancelar — boleto presente → deve cancelar boleto também
    // =======================================================================

    @Test
    void cancelar_deveCancelarBoleto_quandoBoletoExistente() {
        pagamento.setStatus(StatusPagamento.PENDENTE);

        Boleto boleto = Boleto.builder()
                .id(1L)
                .pagamento(pagamento)
                .status(StatusBoleto.PENDENTE)
                .valor(new BigDecimal("190.00"))
                .dataVencimento(LocalDate.now().plusDays(2))
                .build();

        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));
        when(boletoRepository.findByPagamentoId(1L)).thenReturn(Optional.of(boleto));
        when(boletoRepository.save(boleto)).thenReturn(boleto);
        doNothing().when(estoqueService).devolverItens(any());
        when(pagamentoRepository.save(any())).thenReturn(pagamento);
        when(pedidoRepository.save(any())).thenReturn(pedido);

        pagamentoService.cancelar(1L);

        assertEquals(StatusBoleto.CANCELADO, boleto.getStatus());
        verify(boletoRepository).save(boleto);
    }

    // =======================================================================
    // buscarPorId
    // =======================================================================

    @Test
    void buscarPorId_deveRetornarDTO_quandoEncontrado() {
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));

        PagamentoResponseDTO response = pagamentoService.buscarPorId(1L);
        assertNotNull(response);
    }

    @Test
    void buscarPorId_deveLancarExcecao_quandoNaoEncontrado() {
        when(pagamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> pagamentoService.buscarPorId(99L));
    }

    // =======================================================================
    // buscarPorPedidoId
    // =======================================================================

    @Test
    void buscarPorPedidoId_deveRetornarDTO_quandoEncontrado() {
        when(pagamentoRepository.findByPedidoId(5L)).thenReturn(Optional.of(pagamento));

        PagamentoResponseDTO response = pagamentoService.buscarPorPedidoId(5L);
        assertNotNull(response);
    }

    @Test
    void buscarPorPedidoId_deveLancarExcecao_quandoNaoEncontrado() {
        when(pagamentoRepository.findByPedidoId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> pagamentoService.buscarPorPedidoId(99L));
    }

    // =======================================================================
    // calcularDesconto
    // =======================================================================

    @Test
    void calcularDesconto_deveRetornarDesconto_paraPix() {
        BigDecimal valor = new BigDecimal("100.00");
        BigDecimal desconto = pagamentoService.calcularDesconto(MetodoPagamento.PIX, valor);
        assertTrue(desconto.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void calcularDesconto_deveRetornarDesconto_paraBoleto() {
        BigDecimal valor = new BigDecimal("100.00");
        BigDecimal desconto = pagamentoService.calcularDesconto(MetodoPagamento.BOLETO, valor);
        assertTrue(desconto.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void calcularDesconto_deveRetornarZero_paraCredito() {
        BigDecimal valor = new BigDecimal("100.00");
        BigDecimal desconto = pagamentoService.calcularDesconto(MetodoPagamento.CREDITO, valor);
        assertEquals(BigDecimal.ZERO, desconto);
    }

    @Test
    void calcularDesconto_deveRetornarZero_paraDebitoOuOutro() {
        BigDecimal valor = new BigDecimal("100.00");
        BigDecimal desconto = pagamentoService.calcularDesconto(MetodoPagamento.DEBITO, valor);
        assertEquals(BigDecimal.ZERO, desconto);
    }
}