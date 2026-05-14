package acc.br.projetoFinal.Accenture.model;

import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Regras de Negócio - Pedido")
class PedidoBusinessRulesTests {

    private Pedido pedido;
    private Cliente cliente;
    private Produto produto1;
    private Produto produto2;
    private ItemPedido item1;
    private ItemPedido item2;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1L)
                .nome("João Silva")
                .cpf("12345678901")
                .email("joao@email.com")
                .build();

        produto1 = Produto.builder()
                .id(1L)
                .nome("Produto 1")
                .preco(new BigDecimal("100.00"))
                .quantidadeEstoque(10)
                .build();

        produto2 = Produto.builder()
                .id(2L)
                .nome("Produto 2")
                .preco(new BigDecimal("50.00"))
                .quantidadeEstoque(20)
                .build();

        pedido = Pedido.builder()
                .id(1L)
                .cliente(cliente)
                .status(StatusPedido.CRIADO)
                .dataCriacao(LocalDateTime.now())
                // valorTotal e multaCancelamento ficam nulos/default via builder;
                // usamos setters nos testes que precisam de valores específicos.
                .itens(new ArrayList<>())
                .build();

        // Garante multaCancelamento = ZERO (campo tem @Builder.Default mas
        // builder explícito com .itens(...) pode resetar o default do Lombok)
        pedido.setMultaCancelamento(BigDecimal.ZERO);
        pedido.setValorTotal(BigDecimal.ZERO);

        item1 = ItemPedido.builder()
                .id(1L)
                .pedido(pedido)
                .produto(produto1)
                .quantidade(2)
                .precoUnitario(new BigDecimal("100.00"))
                .build();

        item2 = ItemPedido.builder()
                .id(2L)
                .pedido(pedido)
                .produto(produto2)
                .quantidade(3)
                .precoUnitario(new BigDecimal("50.00"))
                .build();

        pedido.getItens().add(item1);
        pedido.getItens().add(item2);
    }

    // =========================================================
    // calcularValorTotal()
    // =========================================================

    @Test
    @DisplayName("Deve calcular valor total do pedido corretamente")
    void deveCalcularValorTotal() {
        // (100 * 2) + (50 * 3) = 200 + 150 = 350
        pedido.calcularValorTotal();
        assertEquals(new BigDecimal("350.00"), pedido.getValorTotal());
    }

    @Test
    @DisplayName("Deve calcular valor total de pedido vazio como ZERO")
    void deveCalcularValorTotalPedidoVazio() {
        pedido.getItens().clear();
        pedido.calcularValorTotal();
        assertEquals(BigDecimal.ZERO, pedido.getValorTotal());
    }

    // =========================================================
    // reservar()
    // =========================================================

    @Test
    @DisplayName("Deve permitir reserva de pedido em CRIADO")
    void deveReservarPedidoCriado() {
        pedido.setStatus(StatusPedido.CRIADO);
        assertDoesNotThrow(() -> pedido.reservar());
        assertEquals(StatusPedido.RESERVADO, pedido.getStatus());
    }

    @Test
    @DisplayName("Não deve reservar pedido que já está RESERVADO")
    void naoDeveReservarPedidoReservado() {
        pedido.setStatus(StatusPedido.RESERVADO);
        assertThrows(IllegalArgumentException.class, () -> pedido.reservar());
    }

    @Test
    @DisplayName("Não deve reservar pedido PAGO")
    void naoDeveReservarPedidoPago() {
        pedido.setStatus(StatusPedido.PAGO);
        assertThrows(IllegalArgumentException.class, () -> pedido.reservar());
    }

    @Test
    @DisplayName("Não deve reservar pedido CANCELADO")
    void naoDeveReservarPedidoCancelado() {
        pedido.setStatus(StatusPedido.CANCELADO);
        assertThrows(IllegalArgumentException.class, () -> pedido.reservar());
    }

    // =========================================================
    // pagar()
    // =========================================================

    @Test
    @DisplayName("Deve permitir pagamento de pedido RESERVADO")
    void devePagarPedidoReservado() {
        pedido.setStatus(StatusPedido.RESERVADO);
        assertDoesNotThrow(() -> pedido.pagar());
        assertEquals(StatusPedido.PAGO, pedido.getStatus());
    }

    @Test
    @DisplayName("Não deve pagar pedido em CRIADO")
    void naoDevePagarPedidoCriado() {
        pedido.setStatus(StatusPedido.CRIADO);
        assertThrows(IllegalArgumentException.class, () -> pedido.pagar());
    }

    @Test
    @DisplayName("Não deve pagar pedido PAGO")
    void naoDevePagarPedidoPago() {
        pedido.setStatus(StatusPedido.PAGO);
        assertThrows(IllegalArgumentException.class, () -> pedido.pagar());
    }

    @Test
    @DisplayName("Não deve pagar pedido CANCELADO")
    void naoDevePagarPedidoCancelado() {
        pedido.setStatus(StatusPedido.CANCELADO);
        assertThrows(IllegalArgumentException.class, () -> pedido.pagar());
    }

    // =========================================================
    // cancelar()
    // =========================================================

    @Test
    @DisplayName("Deve permitir cancelamento de pedido CRIADO")
    void deveCancelarPedidoCriado() {
        pedido.setStatus(StatusPedido.CRIADO);
        assertDoesNotThrow(() -> pedido.cancelar());
        assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
    }

    @Test
    @DisplayName("Deve permitir cancelamento de pedido RESERVADO")
    void deveCancelarPedidoReservado() {
        pedido.setStatus(StatusPedido.RESERVADO);
        assertDoesNotThrow(() -> pedido.cancelar());
        assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
    }

    @Test
    @DisplayName("Deve permitir cancelamento de pedido PAGO")
    void deveCancelarPedidoPago() {
        pedido.setStatus(StatusPedido.PAGO);
        pedido.setValorTotal(new BigDecimal("500.00"));
        assertDoesNotThrow(() -> pedido.cancelar());
        assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
    }

    @Test
    @DisplayName("Não deve cancelar pedido já CANCELADO")
    void naoDeveCancelarPedidoCancelado() {
        pedido.setStatus(StatusPedido.CANCELADO);
        assertThrows(IllegalArgumentException.class, () -> pedido.cancelar());
    }

    // =========================================================
    // calcularMultaCancelamento()
    // =========================================================

    @Test
    @DisplayName("Deve calcular multa de cancelamento em 10% do valor total")
    void deveCalcularMultaCancelamento() {
        pedido.setValorTotal(new BigDecimal("1000.00"));
        BigDecimal multa = pedido.calcularMultaCancelamento();
        assertEquals(new BigDecimal("100.00"), multa);
    }

    @Test
    @DisplayName("Deve aplicar multa ao cancelar pedido PAGO")
    void deveAplicarMultaAoCancelarPago() {
        pedido.setStatus(StatusPedido.PAGO);
        pedido.setValorTotal(new BigDecimal("500.00"));

        pedido.cancelar();

        assertEquals(new BigDecimal("50.00"), pedido.getMultaCancelamento());
        assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
    }

    @Test
    @DisplayName("Não deve aplicar multa ao cancelar pedido CRIADO")
    void naoDeveAplicarMultaAoCancelarCriado() {
        pedido.setStatus(StatusPedido.CRIADO);
        pedido.setValorTotal(new BigDecimal("500.00"));

        pedido.cancelar();

        assertEquals(BigDecimal.ZERO, pedido.getMultaCancelamento());
        assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
    }

    @Test
    @DisplayName("Não deve aplicar multa ao cancelar pedido RESERVADO")
    void naoDeveAplicarMultaAoCancelarReservado() {
        pedido.setStatus(StatusPedido.RESERVADO);
        pedido.setValorTotal(new BigDecimal("500.00"));

        pedido.cancelar();

        assertEquals(BigDecimal.ZERO, pedido.getMultaCancelamento());
        assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
    }

    // =========================================================
    // deveDevolverEstoque()
    // =========================================================

    @Test
    @DisplayName("Deve devolver estoque para pedido RESERVADO")
    void deveIndicarDevolucaoEstoqueParaReservado() {
        pedido.setStatus(StatusPedido.RESERVADO);
        assertTrue(pedido.deveDevolverEstoque());
    }

    @Test
    @DisplayName("Deve devolver estoque para pedido PAGO")
    void deveIndicarDevolucaoEstoqueParaPago() {
        pedido.setStatus(StatusPedido.PAGO);
        assertTrue(pedido.deveDevolverEstoque());
    }

    @Test
    @DisplayName("Não deve devolver estoque para pedido CRIADO")
    void naoDeveIndicarDevolucaoEstoqueParaCriado() {
        pedido.setStatus(StatusPedido.CRIADO);
        assertFalse(pedido.deveDevolverEstoque());
    }

    @Test
    @DisplayName("Não deve devolver estoque para pedido CANCELADO")
    void naoDeveIndicarDevolucaoEstoqueParaCancelado() {
        pedido.setStatus(StatusPedido.CANCELADO);
        assertFalse(pedido.deveDevolverEstoque());
    }
}