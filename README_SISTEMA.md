# Sistema Loja + Banco — Projeto Final

API REST com Java 21 + Spring Boot 3.2.5 + H2 para gerenciamento de clientes, produtos, pedidos e contas com fluxo bancário simulado.

## Tecnologias

- **Java 21** | **Spring Boot 3.2.5** | **Spring Data JPA** | **H2** (em memória)
- **Lombok** | **Swagger/OpenAPI** | **JUnit 5** | **Mockito**
- **Maven** | **Jakarta EE Validation**

## Arquitetura MVC

```
Controller (C) → recebe requisições HTTP, delega ao Service, retorna resposta
    ↓
Service (M) → lógica de negócio, validações, transações
    ↓
Repository (M) → acesso ao banco H2 via JPA
    ↓
Model (M) → entidades e DTOs
```

## Banco H2 (sem configuração extra)

O H2 sobe automaticamente com a aplicação, sem necessidade de instalação adicional.

- **Console H2**: http://localhost:8080/h2-console
- **Configuração**: `application.properties` (em memória, criado/dropado a cada inicialização)

## Como executar

```bash
git clone https://github.com/ProjetoAcademiaAccenture/Back-End.git
cd Back-End/Accenture

# Compilar
./mvnw clean compile

# Executar
./mvnw spring-boot:run
```

## Acessos

- **Swagger UI** (documentação interativa): http://localhost:8080/swagger-ui.html
- **API Docs (OpenAPI)**: http://localhost:8080/api-docs
- **H2 Console**: http://localhost:8080/h2-console

## Fluxo principal

### 1. Cadastrar cliente
```
POST /api/clientes
{
  "nome": "João Silva",
  "cpf": "12345678901",
  "email": "joao@email.com",
  "telefone": "11999999999",
  "cep": "01310100",  // ViaCEP busca automaticamente logradouro, bairro, cidade, uf
  "numero": "100",
  "complemento": "Apto 42"
}
```
- ✓ Cria cliente
- ✓ Busca endereço via ViaCEP
- ✓ Cria conta CORRENTE automaticamente (obrigatória)

### 2. Depositar saldo na conta
```
PATCH /api/contas/{id}/depositar?valor=1000.00
```
- Credita R$ 1.000 na conta do cliente

### 3. Empresa registra produto
```
POST /api/produtos
{
  "nome": "Notebook",
  "descricao": "Notebook Dell",
  "preco": 2500.00,
  "quantidade": 10,
  "metodoPgto": "PIX"
}
```
- ✓ Cria produto
- ✓ Debita automáticamente a conta JURIDICA da empresa

### 4. Criar pedido
```
POST /api/pedidos
{
  "clienteId": 1,
  "itens": [
    { "produtoId": 1, "quantidade": 2 }
  ]
}
```
- Status: `CRIADO`
- Valor total: R$ 5.000

### 5. Reservar pedido
```
PATCH /api/pedidos/{id}/reservar
```
- ✓ Valida estoque
- ✓ Reserva estoque (diminui quantidade)
- Status: `RESERVADO`

### 6. Pagar pedido (duas opções)

#### Opção A: Pagamento direto
```
PATCH /api/pedidos/{id}/pagar
```
- ✓ Debita R$ 5.000 da conta do cliente
- ✓ Credita R$ 5.000 na conta da empresa
- ✓ Registra movimentação no extrato
- Status: `PAGO`

#### Opção B: Pagamento via Boleto
```
POST /api/boletos/gerar/{pedidoId}
```
- Gera boleto com vencimento em 3 dias
- Status boleto: `PENDENTE`

```
PATCH /api/boletos/{id}/pagar
```
- Executa transferência bancária
- Status boleto: `PAGO`
- Status pedido: `PAGO`

### 7. Cancelar pedido (se necessário)

#### Pedido CRIADO/RESERVADO
```
PATCH /api/pedidos/{id}/cancelar
```
- ✓ Sem multa
- ✓ Devolve estoque
- ✓ Frontend deve exibir confirmação

#### Pedido PAGO
```
PATCH /api/pedidos/{id}/cancelar
```
- ⚠️ **Multa de 10% sobre o valor total**
- Exemplo: pedido de R$ 500,00 → multa = R$ 50,00 → estorno = R$ 450,00
- ✓ Empresa retém a multa
- ✓ Cliente recebe de volta o valor menos a multa
- ✓ Estoque devolvido
- **IMPORTANTE**: Frontend deve exibir modal de confirmação mostrando valores!

## Consultar extrato

```
GET /api/contas/{id}/extrato
```
Retorna histórico completo de movimentações (débitos, créditos, estornos, multas)

```
GET /api/contas/{id}/extrato?tipo=DEBITO
GET /api/contas/{id}/extrato?inicio=2025-01-01T00:00&fim=2025-12-31T23:59
GET /api/contas/{id}/extrato?tipo=ESTORNO&inicio=...&fim=...
```

## Regras de negócio

| Regra | Detalhe |
|-------|---------|
| **Cliente + N endereços** | Cliente pode ter vários endereços (residencial, comercial, entrega) |
| **Conta obrigatória** | Todo cliente deve ter uma conta ao ser criado |
| **Tipos de conta** | CORRENTE, POUPANCA ou JURIDICA |
| **Estoque ao criar pedido** | Validar quantidade disponível ao criar E ao reservar |
| **Pagar sem RESERVADO** | Status deve ser RESERVADO antes de pagar |
| **Cancelamento CRIADO/RESERVADO** | Sem multa, devolve estoque |
| **Cancelamento PAGO** | Multa de 10%, estorna o restante |
| **CPF imutável** | Não pode ser alterado após cadastro |
| **BigDecimal para dinheiro** | Nunca usar `double` para valores monetários |

## Endpoints resumo

### Clientes
```
GET    /api/clientes              # Lista todos
GET    /api/clientes/{id}         # Busca por ID
GET    /api/clientes/cpf/{cpf}    # Busca por CPF
POST   /api/clientes              # Cria cliente
PUT    /api/clientes/{id}         # Atualiza dados
DELETE /api/clientes/{id}         # Remove cliente
POST   /api/clientes/{id}/enderecos              # Adiciona endereço
DELETE /api/clientes/{id}/enderecos/{enderecoId} # Remove endereço
```

### Produtos
```
GET    /api/produtos              # Lista todos
GET    /api/produtos/{id}         # Busca por ID
POST   /api/produtos              # Cria produto
PUT    /api/produtos/{id}         # Atualiza produto
DELETE /api/produtos/{id}         # Remove produto
PATCH  /api/produtos/{id}/estoque # Ajusta estoque
```

### Pedidos
```
GET    /api/pedidos                    # Lista todos
GET    /api/pedidos/{id}               # Busca por ID
GET    /api/pedidos/cliente/{clienteId}# Lista por cliente
POST   /api/pedidos                    # Cria pedido
PATCH  /api/pedidos/{id}/reservar      # Reserva estoque
PATCH  /api/pedidos/{id}/pagar         # Paga pedido
PATCH  /api/pedidos/{id}/cancelar      # Cancela pedido
```

### Boletos
```
GET    /api/boletos/{id}                # Consulta boleto
GET    /api/boletos/pedido/{pedidoId}  # Busca por pedido
POST   /api/boletos/gerar/{pedidoId}   # Gera boleto
PATCH  /api/boletos/{id}/pagar         # Paga boleto
PATCH  /api/boletos/{id}/cancelar      # Cancela boleto
```

### Contas
```
GET    /api/contas/{id}                      # Consulta saldo
PATCH  /api/contas/{id}/depositar             # Deposita valor
GET    /api/contas/{id}/extrato               # Lista extrato
GET    /api/contas/{id}/extrato?tipo=DEBITO   # Filtra por tipo
GET    /api/contas/{id}/extrato?inicio=...&fim=... # Filtra por período
```

## Testes

```bash
# Executar todos os testes
./mvnw test

# Apenas testes de integração
./mvnw test -Dgroups=integration

# Com cobertura
./mvnw test jacoco:report
```

## Estrutura de diretórios

```
src/main/java/acc/br/projetoFinal/Accenture/
├── controller/           # Controladores REST
├── service/              # Serviços (lógica)
├── repository/           # Acesso a dados
├── model/                # Entidades JPA
├── dto/                  # Data Transfer Objects
│   ├── request/
│   └── response/
├── enums/                # Enumerações
├── exception/            # Exceções customizadas
└── config/               # Configurações

src/main/resources/
└── application.properties  # Configurações (H2, JPA, Swagger)
```

## Dados de teste

Ao iniciar, o sistema cria automaticamente:

- **Cliente**: EMPRESA
- **CPF**: 00000000000
- **Email**: empresa@loja.com
- **Conta**: EMPRESA-001 (JURIDICA)
- **Saldo inicial**: R$ 10.000,00

Use este cliente para cadastrar produtos (a empresa "compra" os produtos).

## Notas importantes

1. **H2 em memória**: Dados são perdidos ao reiniciar a aplicação
2. **ViaCEP**: Integração automática com API pública de CEP brasileiro
3. **Transações**: Todas as operações críticas usam `@Transactional`
4. **Validações**: Implementadas em DTOs com Jakarta Validation
5. **Multa 10%**: Configurável em `PedidoService.PERCENTUAL_MULTA`

## Contribuição

Projeto Final do Treinamento Accenture — 2025

---

**Desenvolvido com ❤️ em Java 21 + Spring Boot**
