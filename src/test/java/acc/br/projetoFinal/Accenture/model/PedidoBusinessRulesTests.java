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
    private Produto produto1;
    private Produto produto2;
    private ItemPedido item1;
    private ItemPedido item2;

    @BeforeEach
    void setUp() {
        Cliente cliente = Cliente.builder()
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
                .valorBruto(BigDecimal.ZERO)
                .desconto(BigDecimal.ZERO)
                .valorFinal(BigDecimal.ZERO)
                .itens(new ArrayList<>())
                .build();

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
    // calcularValorBruto()
    // =========================================================

    @Test
    @DisplayName("Deve calcular valor bruto corretamente")
    void deveCalcularValorBruto() {
        // (100 * 2) + (50 * 3) = 200 + 150 = 350
        pedido.calcularValorBruto();
        assertEquals(new BigDecimal("350.00"), pedido.getValorBruto());
    }

    @Test
    @DisplayName("Deve calcular valor bruto de pedido vazio como ZERO")
    void deveCalcularValorBrutoPedidoVazio() {
        pedido.getItens().clear();
        pedido.calcularValorBruto();
        assertEquals(BigDecimal.ZERO, pedido.getValorBruto());
    }

    // =========================================================
    // aplicarDesconto()
    // =========================================================

    @Test
    @DisplayName("Deve aplicar desconto e calcular valorFinal corretamente")
    void deveAplicarDesconto() {
        pedido.setValorBruto(new BigDecimal("350.00"));
        pedido.aplicarDesconto(new BigDecimal("17.50")); // 5% de 350
        assertEquals(new BigDecimal("17.50"), pedido.getDesconto());
        assertEquals(new BigDecimal("332.50"), pedido.getValorFinal());
    }

    @Test
    @DisplayName("Deve aplicar desconto ZERO sem alterar valorFinal")
    void deveAplicarDescontoZero() {
        pedido.setValorBruto(new BigDecimal("200.00"));
        pedido.aplicarDesconto(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, pedido.getDesconto());
        assertEquals(new BigDecimal("200.00"), pedido.getValorFinal());
    }

    @Test
    @DisplayName("Não deve aplicar desconto negativo")
    void naoDeveAplicarDescontoNegativo() {
        pedido.setValorBruto(new BigDecimal("200.00"));
        assertThrows(IllegalArgumentException.class,
            () -> pedido.aplicarDesconto(new BigDecimal("-10.00")));
    }

    @Test
    @DisplayName("Não deve aplicar desconto nulo")
    void naoDeveAplicarDescontoNulo() {
        pedido.setValorBruto(new BigDecimal("200.00"));
        assertThrows(IllegalArgumentException.class,
            () -> pedido.aplicarDesconto(null));
    }

    @Test
    @DisplayName("Não deve aplicar desconto maior que o valor bruto")
    void naoDeveAplicarDescontoMaiorQueValorBruto() {
        pedido.setValorBruto(new BigDecimal("100.00"));
        assertThrows(IllegalArgumentException.class,
            () -> pedido.aplicarDesconto(new BigDecimal("150.00")));
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
    @DisplayName("Não deve pagar pedido já PAGO")
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
    @DisplayName("Deve cancelar pedido CRIADO")
    void deveCancelarPedidoCriado() {
        pedido.setStatus(StatusPedido.CRIADO);
        assertDoesNotThrow(() -> pedido.cancelar());
        assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
    }

    @Test
    @DisplayName("Deve cancelar pedido RESERVADO")
    void deveCancelarPedidoReservado() {
        pedido.setStatus(StatusPedido.RESERVADO);
        assertDoesNotThrow(() -> pedido.cancelar());
        assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
    }

    @Test
    @DisplayName("Deve cancelar pedido PAGO")
    void deveCancelarPedidoPago() {
        pedido.setStatus(StatusPedido.PAGO);
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