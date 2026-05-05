package acc.br.projetoFinal.Accenture.enums;

public enum TipoExtrato {
    DEBITO,   // saiu dinheiro da conta (pagamento de pedido)
    CREDITO,  // entrou dinheiro na conta (deposito, venda)
    ESTORNO,  // devolução de pagamento cancelado
    MULTA     // retenção de multa por cancelamento
}
