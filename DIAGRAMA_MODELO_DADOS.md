# 📊 DIAGRAMA DO MODELO DE DADOS

## Entidade-Relacionamento (ER)

```
┌─────────────────────┐
│      CLIENTE        │
├─────────────────────┤
│ id (PK)             │
│ nome (100)          │
│ cpf (11) UNIQUE     │
│ email (100) UNIQUE  │
│ senha (HASH)        │
│ tipoCliente (ENUM)  │
│ telefone (15)       │
│ dataNascimento      │
└────────┬────────────┘
         │
    ┌────┴─────────────────────────────────────┐
    │                                          │
  1:N                                         1:1
    │                                          │
    ▼                                          ▼
┌─────────────────┐                   ┌─────────────────┐
│   ENDERECO      │                   │     CONTA       │
├─────────────────┤                   ├─────────────────┤
│ id (PK)         │                   │ id (PK)         │
│ logradouro      │                   │ numeroConta     │
│ numero          │                   │ senhaTransacao  │
│ complemento     │                   │ saldo           │
│ cep             │                   │ limiteCredito   │
│ cidade          │                   │ tipo (ENUM)     │
│ uf              │                   │ ativo (bool)    │
│ cliente_id (FK) │                   │ cliente_id (FK) │
└─────────────────┘                   └────────┬────────┘
                                               │
                                             1:N
                                               │
                                               ▼
                                       ┌───────────────┐
                                       │   EXTRATO     │
                                       ├───────────────┤
                                       │ id (PK)       │
                                       │ tipo (ENUM)   │
                                       │ valor         │
                                       │ saldoAntes    │
                                       │ saldoDepois   │
                                       │ descricao     │
                                       │ dataHora      │
                                       │ conta_id (FK) │
                                       │ pedido_id (FK)│
                                       └───────────────┘

┌─────────────────┐
│    PEDIDO       │
├─────────────────┤
│ id (PK)         │
│ status (ENUM)   │
│ valorTotal      │
│ dataCriacao     │
│ multaCancelamento│
│ cliente_id (FK) │
└────────┬────────┘
         │
    ┌────┴─────────┐
    │              │
  1:N           1:1
    │              │
    ▼              ▼
┌──────────────┐  ┌──────────────┐
│ ITEM_PEDIDO  │  │    BOLETO    │
├──────────────┤  ├──────────────┤
│ id (PK)      │  │ id (PK)      │
│ quantidade   │  │ codigoBarras │
│ precoUnit.   │  │ valor        │
│ pedido_id(FK)│  │ dataVencto   │
│ produto_id(FK)  │ status(ENUM) │
└────────┬──────┘  │ pedido_id(FK)│
         │         └──────────────┘
       N:1
         │
         ▼
    ┌─────────────┐
    │   PRODUTO   │
    ├─────────────┤
    │ id (PK)     │
    │ nome (100)  │
    │ descricao   │
    │ preco       │
    │ estoque     │
    │ metodoPgto  │
    │ (ENUM)      │
    └─────────────┘
```

---

## Relacionamentos Explicados

### 1. CLIENTE ↔ ENDERECO (1:N)
```
Um cliente pode ter múltiplos endereços (residencial, comercial, etc)
Cascade: DELETE (ao deletar cliente, deleta endereços)
Fetch: EAGER (carregar endereços sempre)
```

### 2. CLIENTE ↔ CONTA (1:1)
```
Um cliente tem exatamente uma conta (ou mais de um tipo?)
Cascade: ALL (ao deletar cliente, deleta conta)
Fetch: LAZY (carregar só quando necessário)
```

### 3. CLIENTE ↔ PEDIDO (1:N)
```
Um cliente faz múltiplos pedidos
Cascade: ALL
Fetch: LAZY
```

### 4. CONTA ↔ EXTRATO (1:N)
```
Uma conta tem múltiplos extratos (histórico)
Cascade: DELETE
Fetch: LAZY
Ordenação: Por dataHora DESC
```

### 5. PEDIDO ↔ ITEM_PEDIDO (1:N)
```
Um pedido tem múltiplos itens
Cascade: ALL (ao deletar pedido, deleta itens)
Fetch: EAGER (sempre carregar itens com pedido)
```

### 6. ITEM_PEDIDO ↔ PRODUTO (N:1)
```
Múltiplos itens podem referenciar o mesmo produto
Fetch: LAZY
```

### 7. PEDIDO ↔ BOLETO (1:1)
```
Um pedido gera um boleto único
Cascade: ALL
Unique: Constraint no banco
Fetch: LAZY
```

### 8. EXTRATO ↔ PEDIDO (N:1 Opcional)
```
Um extrato pode estar vinculado a um pedido
Relacionamento bidirecional fraco
Fetch: LAZY
```

---

## Constraints de Banco de Dados

```sql
-- CLIENTE
ALTER TABLE cliente ADD CONSTRAINT uk_cpf UNIQUE(cpf);
ALTER TABLE cliente ADD CONSTRAINT uk_email UNIQUE(email);
ALTER TABLE cliente ADD CONSTRAINT check_tipo_cliente 
  CHECK (tipo_cliente IN ('ROLE_USER', 'ROLE_ADMIN'));

-- CONTA
ALTER TABLE conta ADD CONSTRAINT uk_numero_conta UNIQUE(numero_conta);
ALTER TABLE conta ADD CONSTRAINT fk_cliente_id 
  FOREIGN KEY (cliente_id) REFERENCES cliente(id);
ALTER TABLE conta ADD CONSTRAINT check_tipo_conta 
  CHECK (tipo IN ('CORRENTE', 'POUPANCA', 'EMPRESA'));

-- PEDIDO
ALTER TABLE pedido ADD CONSTRAINT fk_cliente_id 
  FOREIGN KEY (cliente_id) REFERENCES cliente(id);
ALTER TABLE pedido ADD CONSTRAINT check_status_pedido 
  CHECK (status IN ('CRIADO', 'RESERVADO', 'PAGO', 'CANCELADO'));

-- ITEM_PEDIDO
ALTER TABLE item_pedido ADD CONSTRAINT fk_pedido_id 
  FOREIGN KEY (pedido_id) REFERENCES pedido(id);
ALTER TABLE item_pedido ADD CONSTRAINT fk_produto_id 
  FOREIGN KEY (produto_id) REFERENCES produto(id);

-- BOLETO
ALTER TABLE boleto ADD CONSTRAINT uk_codigo_barras UNIQUE(codigo_barras);
ALTER TABLE boleto ADD CONSTRAINT uk_pedido_id UNIQUE(pedido_id);
ALTER TABLE boleto ADD CONSTRAINT fk_pedido_id 
  FOREIGN KEY (pedido_id) REFERENCES pedido(id);
ALTER TABLE boleto ADD CONSTRAINT check_status_boleto 
  CHECK (status IN ('PENDENTE', 'PAGO', 'CANCELADO'));

-- EXTRATO
ALTER TABLE extrato ADD CONSTRAINT fk_conta_id 
  FOREIGN KEY (conta_id) REFERENCES conta(id);
ALTER TABLE extrato ADD CONSTRAINT check_tipo_extrato 
  CHECK (tipo IN ('DEBITO', 'CREDITO', 'ESTORNO', 'MULTA'));

-- ENDERECO
ALTER TABLE endereco ADD CONSTRAINT fk_cliente_id 
  FOREIGN KEY (cliente_id) REFERENCES cliente(id);
```

---

## Estados e Transições

### Estado: PEDIDO

```
                    ┌─────────────────────────┐
                    │        CRIADO           │
                    │ (Recém-criado)          │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │       RESERVADO        │
                    │ (Estoque bloqueado)    │
                    │ (Pode gerar boleto)    │
                    └────────┬────────┬──────┘
                             │        │
                 ┌───────────┤        │
                 │           │        │
         ┌───────▼──┐  ┌─────▼─────┐
         │ CANCELADO│  │   PAGO    │
         │ (com     │  │ (processado)
         │  multa)  │  │           │
         └──────────┘  └───────────┘
```

**Transições Válidas:**
- CRIADO → RESERVADO (ao confirmar reserva)
- CRIADO → CANCELADO (sem processamento)
- RESERVADO → PAGO (ao pagar boleto)
- RESERVADO → CANCELADO (com multa automática)
- PAGO → PROCESSADO (após processamento)

---

### Estado: BOLETO

```
                ┌──────────────────┐
                │     PENDENTE     │
                │ (Aguardando pgt) │
                └────────┬─────────┘
                         │
              ┌──────────┴──────────┐
              │                     │
        ┌─────▼────┐          ┌────▼────┐
        │   PAGO   │          │CANCELADO│
        │ (Fechado)│          │(Anulado) │
        └──────────┘          └─────────┘
```

**Transições Válidas:**
- PENDENTE → PAGO (via transferência bancária)
- PENDENTE → CANCELADO (cancelamento de pedido)
- PAGO → ESTORNO (possível devolução futura)

---

### Estado: CONTA

```
                ┌──────────────┐
                │    ATIVA     │
                │ (Funcional)  │
                └────────┬─────┘
                         │
              ┌──────────┴──────────┐
              │                     │
        ┌─────▼────┐          ┌────▼─────┐
        │   BLOQUEADA    │          │FECHADA│
        │(Sem transações)│          │(Encerrada)
        └──────────┘          └─────────┘
```

---

## Tipos de Movimentação (EXTRATO)

```
TIPO_EXTRATO
├── DEBITO
│   └─ Sai dinheiro (pagamento de pedido)
│   └─ saldoDepois < saldoAntes
│
├── CREDITO
│   └─ Entra dinheiro (depósito)
│   └─ saldoDepois > saldoAntes
│
├── ESTORNO
│   └─ Devolução de pagamento
│   └─ Crédito após cancelamento
│   └─ saldoDepois > saldoAntes
│
└── MULTA
    └─ Retenção por cancelamento
    └─ Débito automático
    └─ saldoDepois < saldoAntes
```

---

## Índices para Performance

```sql
-- Pesquisas frequentes
CREATE INDEX idx_cliente_cpf ON cliente(cpf);
CREATE INDEX idx_cliente_email ON cliente(email);
CREATE INDEX idx_pedido_cliente_id ON pedido(cliente_id);
CREATE INDEX idx_pedido_status ON pedido(status);
CREATE INDEX idx_extrato_conta_id ON extrato(conta_id);
CREATE INDEX idx_extrato_data ON extrato(data_hora DESC);
CREATE INDEX idx_extrato_tipo ON extrato(tipo);
CREATE INDEX idx_boleto_codigo ON boleto(codigo_barras);
CREATE INDEX idx_boleto_status ON boleto(status);

-- Para ordenações
CREATE INDEX idx_extrato_conta_data ON extrato(conta_id, data_hora DESC);
CREATE INDEX idx_pedido_cliente_data ON pedido(cliente_id, data_criacao DESC);
```

---

## Validações por Entidade

### CLIENTE
- ✓ Nome: 3-100 caracteres, não-vazio
- ✓ CPF: 11 dígitos, válido (algoritmo mod 11)
- ✓ Email: formato válido, único
- ✓ Senha: mínimo 6 caracteres, hash BCrypt
- ✓ Telefone: formato brasileiro (11 dígitos)

### PRODUTO
- ✓ Nome: não-vazio, 3-100 caracteres
- ✓ Preço: > 0, até 2 casas decimais
- ✓ Estoque: ≥ 0, inteiro
- ✓ Método Pagamento: enum válido

### PEDIDO
- ✓ Status: transição válida
- ✓ Valor Total: calculado, >= 0
- ✓ Data Criação: timestamp válido
- ✓ Multa: aplicada apenas em cancelamento

### BOLETO
- ✓ Código: 44 dígitos, único
- ✓ Vencimento: >= hoje + 1 dia
- ✓ Valor: > 0, correspond ao pedido
- ✓ Status: transição válida

### CONTA
- ✓ Número: único
- ✓ Saldo: >= 0 (ou <= limiteCredito)
- ✓ Tipo: enum válido
- ✓ Cliente: relacionamento obrigatório

### EXTRATO
- ✓ Tipo: enum válido
- ✓ Valor: > 0
- ✓ Saldos: saldoAntes, saldoDepois válidos
- ✓ Data: não-nula, <= now

---

## Fluxo de Dados: Criar Pedido

```
1. POST /api/pedidos
   └─ ClienteRequestDTO validado
   
2. PedidoController
   └─ Injeta PedidoService
   
3. PedidoService.criar()
   ├─ Busca cliente (PedidoRepository)
   ├─ Busca produtos (ProdutoRepository)
   ├─ Valida estoque
   ├─ Cria ItemPedido para cada produto
   ├─ Calcula valorTotal = SUM(quantidade × preço)
   ├─ Salva Pedido (status = CRIADO)
   └─ Retorna PedidoResponseDTO
   
4. PedidoRepository.save()
   └─ JPA persiste no H2
   
5. @Transactional garante atomicidade
   └─ Tudo salvo ou tudo revertido
```

---

## Fluxo de Dados: Pagar Boleto

```
1. PATCH /api/boletos/{id}/pagar
   └─ Token JWT validado
   
2. BoletoController
   └─ Valida autenticação
   
3. BoletoService.pagarBoleto()
   ├─ Busca boleto (BoletoRepository)
   ├─ Valida status (PENDENTE)
   ├─ Busca pedido e cliente
   │
   ├─ Transferência bancária:
   │  ├─ Débito: cliente.conta.saldo -= valor
   │  ├─ Crédito: empresa.conta.saldo += valor
   │  └─ Ambas transações vinculadas
   │
   ├─ Cria Extrato (tipo=DEBITO, cliente)
   ├─ Cria Extrato (tipo=CREDITO, empresa)
   ├─ Atualiza Boleto.status = PAGO
   ├─ Atualiza Pedido.status = PAGO
   └─ Retorna BoletoResponseDTO
   
4. ContaRepository.save() x2
   └─ JPA persiste mudanças
   
5. @Transactional garante consistência
   └─ Se falhar, reverte tudo
```

---

## Resumo Técnico

| Aspecto | Detalhes |
|---|---|
| **Relacionamentos** | 8 (3 1:N, 2 1:1, 1 N:1) |
| **Entidades** | 8 classes `@Entity` |
| **Tabelas** | 8 no banco de dados |
| **Foreign Keys** | 7 (um para cada relacionamento) |
| **Unique Constraints** | 6 (cpf, email, numeroCC, codigoBarras, numeroConta, pedido_id) |
| **Enums** | 6 (TipoCliente, TipoConta, StatusPedido, StatusBoleto, TipoExtrato, MetodoPagamento) |
| **Cascades** | DELETE, ALL (conforme necessidade) |
| **Fetch Strategies** | LAZY e EAGER balanceados |
| **Validações BD** | CHECK constraints em status/tipos |
| **Índices** | 10+ para performance |

---

**Documento Gerado em:** 11 de Maio de 2026
