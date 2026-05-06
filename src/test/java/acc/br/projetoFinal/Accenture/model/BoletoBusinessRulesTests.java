package acc.br.projetoFinal.Accenture.model;

import acc.br.projetoFinal.Accenture.enums.StatusBoleto;
import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Regras de Negócio - Boleto")
class BoletoBusinessRulesTests {

    private Boleto boleto;
    private Pedido pedido;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1L)
                .nome("João Silva")
                .cpf("12345678901")
                .email("joao@email.com")
                .build();

        pedido = Pedido.builder()
                .id(1L)
                .cliente(cliente)
                .status(StatusPedido.RESERVADO)
                .dataCriacao(LocalDateTime.now())
                .valorTotal(new BigDecimal("1000.00"))
                .build();

        boleto = Boleto.builder()
                .id(1L)
                .pedido(pedido)
                .codigoBarras("12345678901234567890123456789012345678901234")
                .valor(new BigDecimal("1000.00"))
                .dataVencimento(LocalDate.now().plusDays(3))
                .status(StatusBoleto.PENDENTE)
                .build();
    }

    @Test
    @DisplayName("Deve permitir pagamento de boleto PENDENTE")
    void devePermitirPagamentoBeletoPendente() {
        boleto.setStatus(StatusBoleto.PENDENTE);
        assertDoesNotThrow(boleto::pagar);
        assertEquals(StatusBoleto.PAGO, boleto.getStatus());
    }

    @Test
    @DisplayName("Não deve pagar boleto já PAGO")
    void naoDevePagarBoletoPago() {
        boleto.setStatus(StatusBoleto.PAGO);
        assertThrows(IllegalArgumentException.class, boleto::pagar);
    }

    @Test
    @DisplayName("Não deve pagar boleto CANCELADO")
    void naoDevePagarBoletoCancelado() {
        boleto.setStatus(StatusBoleto.CANCELADO);
        assertThrows(IllegalArgumentException.class, boleto::pagar);
    }

    @Test
    @DisplayName("Deve permitir cancelamento de boleto")
    void devePermitirCancelamentoBoleto() {
        boleto.setStatus(StatusBoleto.PENDENTE);
        assertDoesNotThrow(boleto::cancelar);
        assertEquals(StatusBoleto.CANCELADO, boleto.getStatus());
    }

    @Test
    @DisplayName("Não deve cancelar boleto já cancelado")
    void naoDeveCancelarBoletoCancelado() {
        boleto.setStatus(StatusBoleto.CANCELADO);
        assertThrows(IllegalArgumentException.class, boleto::cancelar);
    }

    @Test
    @DisplayName("Deve indicar boleto atrasado quando vencimento passou")
    void deveIndicarBoletoAtrasado() {
        boleto.setDataVencimento(LocalDate.now().minusDays(1));
        assertTrue(boleto.estaAtrasado());
    }

    @Test
    @DisplayName("Não deve indicar boleto atrasado quando hoje é o vencimento")
    void naoDeveIndicarBoletoAtrasadoNoVencimento() {
        boleto.setDataVencimento(LocalDate.now());
        assertFalse(boleto.estaAtrasado());
    }

    @Test
    @DisplayName("Não deve indicar boleto atrasado quando vencimento é futuro")
    void naoDeveIndicarBoletoAtrasadoComVencimentoFuturo() {
        boleto.setDataVencimento(LocalDate.now().plusDays(3));
        assertFalse(boleto.estaAtrasado());
    }

    @Test
    @DisplayName("Deve validar pagamento sem lançar exceção para status válido")
    void deveValidarPagamentoComSucessoParaStatusValido() {
        boleto.setStatus(StatusBoleto.PENDENTE);
        assertDoesNotThrow(boleto::validarPagamento);
    }

    @Test
    @DisplayName("Deve validar cancelamento sem lançar exceção para status válido")
    void deveValidarCancelamentoComSucessoParaStatusValido() {
        boleto.setStatus(StatusBoleto.PENDENTE);
        assertDoesNotThrow(boleto::validarCancelamento);
    }
}
