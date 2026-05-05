# 📡 ENDPOINTS REST — Sistema Loja + Banco

## 🔗 Base URL
```
http://localhost:8080
```

---

## 👥 CLIENTES (7 endpoints)

### Listar todos os clientes
```http
GET /api/clientes
```
**Response 200:**
```json
[
  {
    "id": 1,
    "nome": "EMPRESA",
    "cpf": "00000000000",
    "email": "empresa@loja.com",
    "telefone": "1140000000",
    "dataNascimento": null,
    "enderecos": []
  }
]
```

---

### Buscar cliente por ID
```http
GET /api/clientes/{id}
```
**Path:** `GET /api/clientes/1`  
**Response 200:** `ClienteResponseDTO`  
**Response 404:** Recurso Não Encontrado

---

### Buscar cliente por CPF
```http
GET /api/clientes/cpf/{cpf}
```
**Path:** `GET /api/clientes/cpf/12345678901`  
**Response 200:** `ClienteResponseDTO`  
**Response 404:** Recurso Não Encontrado

---

### Criar cliente
```http
POST /api/clientes
Content-Type: application/json

{
  "nome": "João Silva",
  "cpf": "12345678901",
  "email": "joao@email.com",
  "telefone": "11999999999",
  "cep": "01310100",
  "numero": "100",
  "complemento": "Apto 42"
}
```
**Response 201:** `ClienteResponseDTO` + header `Location`

---

### Atualizar cliente
```http
PUT /api/clientes/{id}
Content-Type: application/json

{
  "nome": "João Pedro",
  "cpf": "12345678901",
  "email": "joao.novo@email.com",
  "telefone": "11988888888",
  "cep": "01310100",
  "numero": "100",
  "complemento": "Apto 42"
}
```
**Response 200:** `ClienteResponseDTO`

---

### Deletar cliente
```http
DELETE /api/clientes/{id}
```
**Response 204:** No Content

---

### Adicionar endereço ao cliente
```http
POST /api/clientes/{id}/enderecos
Content-Type: application/json

{
  "cep": "04543140",
  "tipoEndereco": "COMERCIAL",
  "numero": "200",
  "complemento": "Sala 10"
}
```
**Response 201:** No Content

---

### Remover endereço do cliente
```http
DELETE /api/clientes/{id}/enderecos/{enderecoId}
```
**Response 204:** No Content

---

## 📦 PRODUTOS (6 endpoints)

### Listar todos os produtos
```http
GET /api/produtos
```
**Response 200:** Lista de `ProdutoResponseDTO`

---

### Buscar produto por ID
```http
GET /api/produtos/{id}
```
**Response 200:** `ProdutoResponseDTO`  
**Response 404:** Recurso Não Encontrado

---

### Criar produto
```http
POST /api/produtos
Content-Type: application/json

{
  "nome": "Notebook Dell",
  "descricao": "Intel i7 16GB",
  "preco": 2500.00,
  "quantidade": 10,
  "metodoPgto": "PIX"
}
```
**Response 201:** `ProdutoResponseDTO` + header `Location`

⚠️ **Debita automaticamente a conta JURIDICA da empresa!**

---

### Atualizar produto
```http
PUT /api/produtos/{id}
Content-Type: application/json

{
  "nome": "Notebook Dell Atualizado",
  "descricao": "Intel i7 16GB Ram",
  "preco": 2600.00,
  "quantidade": 15,
  "metodoPgto": "PIX"
}
```
**Response 200:** `ProdutoResponseDTO`

---

### Deletar produto
```http
DELETE /api/produtos/{id}
```
**Response 204:** No Content

---

### Ajustar estoque de produto
```http
PATCH /api/produtos/{id}/estoque?novaQuantidade=20
```
**Response 200:** `ProdutoResponseDTO`

---

## 🛒 PEDIDOS (7 endpoints)

### Listar todos os pedidos
```http
GET /api/pedidos
```
**Response 200:** Lista de `PedidoResponseDTO`

---

### Buscar pedido por ID
```http
GET /api/pedidos/{id}
```
**Response 200:** `PedidoResponseDTO` com itens  
**Response 404:** Recurso Não Encontrado

---

### Listar pedidos de um cliente
```http
GET /api/pedidos/cliente/{clienteId}
```
**Response 200:** Lista de `PedidoResponseDTO`

---

### Criar pedido
```http
POST /api/pedidos
Content-Type: application/json

{
  "clienteId": 2,
  "itens": [
    { "produtoId": 1, "quantidade": 2 },
    { "produtoId": 2, "quantidade": 1 }
  ]
}
```
**Response 201:** `PedidoResponseDTO` + header `Location`

**Status inicial:** `CRIADO`

---

### Reservar pedido
```http
PATCH /api/pedidos/{id}/reservar
```
**Response 200:** `PedidoResponseDTO`

**Mudanças:**
- ✓ Valida estoque
- ✓ Decrementa estoque dos produtos
- ✓ Status: `CRIADO` → `RESERVADO`

---

### Pagar pedido
```http
PATCH /api/pedidos/{id}/pagar
```
**Response 200:** `PedidoResponseDTO`

**Mudanças:**
- ✓ Débita conta do cliente
- ✓ Credita conta da empresa
- ✓ Registra extrato para ambas
- ✓ Status: `RESERVADO` → `PAGO`

---

### Cancelar pedido
```http
PATCH /api/pedidos/{id}/cancelar
```
**Response 200:** `PedidoResponseDTO`

**Cenários:**
- **Status CRIADO**: sem multa, sem alteração de estoque
- **Status RESERVADO**: sem multa, devolve estoque
- **Status PAGO**: 
  - ⚠️ Multa 10% retida pela empresa
  - Estorno de 90% ao cliente
  - Devolve estoque

**Exemplo:**
- Valor: R$ 500,00
- Multa: R$ 50,00 (10%)
- Estorno: R$ 450,00
- Registra 2 extratos: ESTORNO + MULTA

---

## 💰 CONTAS (4 endpoints)

### Consultar saldo da conta
```http
GET /api/contas/{id}
```
**Response 200:**
```json
{
  "id": 1,
  "numeroConta": "CLI-2",
  "saldo": 500.00,
  "tipo": "CORRENTE",
  "ativo": true
}
```

---

### Depositar na conta
```http
PATCH /api/contas/{id}/depositar?valor=1000.00
```
**Response 200:** `ContaResponseDTO` com novo saldo

**Mudanças:**
- ✓ Credita o valor
- ✓ Registra extrato tipo `CREDITO`

---

### Listar extrato completo
```http
GET /api/contas/{id}/extrato
```
**Response 200:**
```json
[
  {
    "id": 3,
    "tipo": "ESTORNO",
    "valor": 450.00,
    "saldoAntes": 300.00,
    "saldoDepois": 750.00,
    "descricao": "Estorno pedido #12",
    "pedidoId": 12,
    "dataHora": "2025-05-04T14:32:00"
  },
  {
    "id": 2,
    "tipo": "DEBITO",
    "valor": 500.00,
    "saldoAntes": 800.00,
    "saldoDepois": 300.00,
    "descricao": "Pagamento pedido #12",
    "pedidoId": 12,
    "dataHora": "2025-05-03T10:15:00"
  }
]
```

---

### Filtrar extrato por tipo
```http
GET /api/contas/{id}/extrato?tipo=DEBITO
```
**Query params:**
- `tipo`: `DEBITO`, `CREDITO`, `ESTORNO`, ou `MULTA`

---

### Filtrar extrato por período
```http
GET /api/contas/{id}/extrato?inicio=2025-05-01T00:00:00&fim=2025-05-31T23:59:59
```
**Query params:**
- `inicio`: Data/hora ISO 8601
- `fim`: Data/hora ISO 8601

---

### Filtrar extrato por período + tipo
```http
GET /api/contas/{id}/extrato?tipo=DEBITO&inicio=2025-05-01T00:00:00&fim=2025-05-31T23:59:59
```

---

## 📄 BOLETOS (5 endpoints)

### Consultar boleto
```http
GET /api/boletos/{id}
```
**Response 200:**
```json
{
  "id": 1,
  "codigoBarras": "12345678901234567890123456789012345678901234",
  "valor": 5000.00,
  "dataVencimento": "2025-05-08",
  "status": "PENDENTE",
  "pedidoId": 1
}
```

---

### Buscar boleto por pedido
```http
GET /api/boletos/pedido/{pedidoId}
```
**Response 200:** `BoletoResponseDTO`  
**Response 404:** Boleto não encontrado

---

### Gerar boleto
```http
POST /api/boletos/gerar/{pedidoId}
```
**Response 201:** `BoletoResponseDTO` + header `Location`

**Pré-requisitos:**
- Pedido deve estar em status `RESERVADO`

**Resultado:**
- Código de barras: 44 dígitos
- Vencimento: hoje + 3 dias
- Status: `PENDENTE`

---

### Pagar boleto
```http
PATCH /api/boletos/{id}/pagar
```
**Response 200:** `BoletoResponseDTO`

**Mudanças:**
- ✓ Débita cliente (via ContaService.transferir)
- ✓ Credita empresa
- ✓ Registra extratos
- ✓ Status boleto: `PENDENTE` → `PAGO`
- ✓ Status pedido: `RESERVADO` → `PAGO`

---

### Cancelar boleto
```http
PATCH /api/boletos/{id}/cancelar
```
**Response 204:** No Content

---

## ⚡ Códigos de Status HTTP

| Status | Significado |
|--------|-------------|
| **200** | OK — requisição bem-sucedida |
| **201** | Created — recurso criado |
| **204** | No Content — sem corpo de resposta |
| **400** | Bad Request — erro de validação |
| **404** | Not Found — recurso não encontrado |
| **422** | Unprocessable Entity — erro de negócio (estoque, saldo) |
| **500** | Internal Server Error — erro do servidor |

---

## 🔐 Exemplo de fluxo completo com cURL

```bash
# 1. Criar cliente
CLIENT=$(curl -s -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "cpf": "12345678901",
    "email": "joao@email.com",
    "telefone": "11999999999",
    "cep": "01310100",
    "numero": "100"
  }' | jq -r '.id')
echo "Cliente criado: $CLIENT"

# 2. Depositar R$ 1000
curl -s -X PATCH "http://localhost:8080/api/contas/2/depositar?valor=1000.00" | jq

# 3. Criar produto
PRODUTO=$(curl -s -X POST http://localhost:8080/api/produtos \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Notebook",
    "preco": 2500.00,
    "quantidade": 5,
    "metodoPgto": "PIX"
  }' | jq -r '.id')
echo "Produto criado: $PRODUTO"

# 4. Criar pedido
PEDIDO=$(curl -s -X POST http://localhost:8080/api/pedidos \
  -H "Content-Type: application/json" \
  -d "{
    \"clienteId\": $CLIENT,
    \"itens\": [{ \"produtoId\": $PRODUTO, \"quantidade\": 1 }]
  }" | jq -r '.id')
echo "Pedido criado: $PEDIDO"

# 5. Reservar
curl -s -X PATCH "http://localhost:8080/api/pedidos/$PEDIDO/reservar" | jq

# 6. Pagar
curl -s -X PATCH "http://localhost:8080/api/pedidos/$PEDIDO/pagar" | jq

# 7. Ver extrato
curl -s "http://localhost:8080/api/contas/2/extrato" | jq
```

---

## 📚 Documentação

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/api-docs
- **H2 Console**: http://localhost:8080/h2-console

---

**29 endpoints | 7 Controllers | MVC Pattern | Spring Boot 3.2.5 | Java 21**
