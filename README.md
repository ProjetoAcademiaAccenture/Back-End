# Sistema Loja + Banco — Projeto Final Accenture

🏦 API REST completa com Java 21 + Spring Boot 3.2.5 + H2 para gerenciamento integrado de:
- Clientes e suas contas bancárias
- Catálogo de produtos com controle de estoque
- Pedidos com fluxo completo (criação → reserva → pagamento → entrega)
- Pagamentos com suporte a múltiplos métodos (Boleto, Cartão, PIX, Transferência)
- Extratos bancários e histórico completo

## 📚 Documentação

**Comece por aqui:**
- **[ARQUITETURA_COMPLETA.md](ARQUITETURA_COMPLETA.md)** - Visão geral, modelos, serviços, controllers
- **[DIAGRAMA_ER.md](DIAGRAMA_ER.md)** - Diagrama Entidade-Relacionamento do banco de dados
- **[GUIA_DESENVOLVEDOR.md](GUIA_DESENVOLVEDOR.md)** - Setup, convenções, como adicionar features
- **[ENDPOINTS.md](ENDPOINTS.md)** - Documentação detalhada de todas as rotas

---

## 🛠️ Tecnologias

| Tecnologia | Versão | Uso |
|-----------|--------|-----|
| **Java** | 21 | Linguagem principal |
| **Spring Boot** | 3.2.5 | Framework web |
| **Spring Data JPA** | 3.2.5 | Persistência ORM |
| **H2 Database** | 2.x | Banco em memória (dev) |
| **Lombok** | 1.18.30 | Reduz boilerplate |
| **Swagger/OpenAPI** | 2.5.0 | Documentação interativa |
| **JWT (JJWT)** | 0.13.0 | Autenticação |
| **Spring Security** | 3.2.5 | Autorização |
| **JUnit 5** | 5.x | Testes unitários |
| **Mockito** | 5.x | Mock em testes |
| **Maven** | 3.9+ | Build & dependências |

## 📊 Arquitetura MVC

```
┌─────────────────────────────────────────┐
│       Cliente (Frontend)                │
└────────────┬────────────────────────────┘
             │ HTTP REST (JSON)
┌────────────▼────────────────────────────┐
│     Controller Layer                    │
│  Recebe requisição, valida DTO,        │
│  delega ao Service, retorna resposta    │
└────────────┬────────────────────────────┘
             │
┌────────────▼────────────────────────────┐
│     Service Layer                       │
│  Lógica de negócio, validações,        │
│  transações, orquestração               │
└────────────┬────────────────────────────┘
             │
┌────────────▼────────────────────────────┐
│     Repository Layer                    │
│  Spring Data JPA, queries JPQL,        │
│  acesso a dados                         │
└────────────┬────────────────────────────┘
             │ SQL
┌────────────▼────────────────────────────┐
│     H2 Database (Em Memória)            │
│  Tabelas relacionais, índices,         │
│  constraints                            │
└─────────────────────────────────────────┘
```

**Detalhes em:** [ARQUITETURA_COMPLETA.md](ARQUITETURA_COMPLETA.md)

## 🗄️ Modelos (Entities)

O sistema possui **10 entidades** principais mapeadas para banco de dados:

| Entidade | Descrição | Relacionamentos |
|----------|-----------|-----------------|
| **CLIENTE** | Usuário do sistema | 1:1 Conta, 1:N Endereço, 1:N Pedido |
| **CONTA** | Conta bancária do cliente | 1:1 Cliente, 1:N Extrato |
| **ENDERECO** | Endereços (residencial/comercial) | N:1 Cliente |
| **PRODUTO** | Itens do catálogo | 1:N ItemPedido |
| **PEDIDO** | Ordem de compra | N:1 Cliente, 1:N ItemPedido, 1:1 Pagamento, 1:N Extrato |
| **ITEM_PEDIDO** | Linha do pedido | N:1 Pedido, N:1 Produto |
| **PAGAMENTO** | Informações de pagamento | 1:1 Pedido, 1:1 Boleto, 1:N TentativaPagamento |
| **BOLETO** | Boleto bancário | 1:1 Pagamento |
| **TENTATIVA_PAGAMENTO** | Histórico de tentativas | N:1 Pagamento |
| **EXTRATO** | Movimentação bancária | N:1 Conta (N:1 Pedido, N:1 Pagamento opcionais) |

**Diagrama Completo:** [DIAGRAMA_ER.md](DIAGRAMA_ER.md)

## 🔄 Fluxos Principais

### 1️⃣ Cadastro e Autenticação

```
POST /auth/register
├─ Valida CPF/Email únicos
├─ Cria Cliente
├─ Cria Conta CORRENTE automática
├─ Cria Endereco inicial
└─ Retorna JWT Token (24h)
```

### 2️⃣ Realizar Pedido

```
POST /api/pedidos
├─ Valida Cliente e Produtos
├─ Valida estoque disponível
├─ Cria Pedido (status: CRIADO)
└─ Retorna pedidoId

PATCH /api/pedidos/{id}/reservar
├─ Valida status CRIADO
├─ Reserva estoque (decrementa quantidade)
└─ Status: RESERVADO

PATCH /api/pedidos/{id}/pagar
├─ Debita cliente
├─ Credita empresa
├─ Registra Extrato
└─ Status: PAGO
```

### 3️⃣ Pagamento via Boleto

```
POST /api/boletos/gerar/{pedidoId}
├─ Gera código de barras (44 dígitos)
├─ Define vencimento (hoje + 3 dias)
└─ Status: PENDENTE

PATCH /api/boletos/{id}/pagar
├─ Valida não vencido
├─ Executa transferência
└─ Status: PAGO
```

### 4️⃣ Cancelamento (com/sem multa)

```
PATCH /api/pedidos/{id}/cancelar

Status CRIADO/RESERVADO:
├─ Sem multa
├─ Devolve estoque
└─ Status: CANCELADO

Status PAGO:
├─ Multa: 10% do valor
├─ Estorno: (valor - multa)
├─ Devolve estoque
└─ Status: CANCELADO
```

**Detalhes em:** [ARQUITETURA_COMPLETA.md](ARQUITETURA_COMPLETA.md)

## 🚀 Como Executar

### Windows (PowerShell)

```powershell
# 1. Clonar repositório
git clone https://github.com/ProjetoAcademiaAccenture/Back-End.git
cd Back-End

# 2. Compilar e rodar
.\mvnw.cmd clean spring-boot:run

# OU compilar apenas
.\mvnw.cmd clean compile

# OU executar testes
.\mvnw.cmd test
```

### macOS/Linux

```bash
git clone https://github.com/ProjetoAcademiaAccenture/Back-End.git
cd Back-End

# Rodar aplicação
./mvnw clean spring-boot:run

# OU via script de gerenciamento
./start-app.sh start
```

**Esperado:** Aplicação iniciada em `http://localhost:8080`

**Setup Detalhado:** [GUIA_DESENVOLVEDOR.md](GUIA_DESENVOLVEDOR.md)

## 💡 Notas Importantes

1. **H2 em Memória**: Dados são perdidos ao reiniciar (ideal para desenvolvimento)
2. **JWT Tokens**: Válidos por 24 horas, incluir em header `Authorization: Bearer <token>`
3. **BigDecimal**: Sempre usado para valores monetários (precisão)
4. **Transações**: `@Transactional` garante atomicidade em operações críticas
5. **Multa no Cancelamento**: 10% configurável em `PedidoService.PERCENTUAL_MULTA`
6. **CPF Imutável**: Não pode ser alterado após cadastro do cliente
7. **Validações**: Implementadas em DTOs com Jakarta Validation
8. **CORS**: Configure conforme necessário da aplicação frontend

## 📖 Guia Rápido para Desenvolvedores

Se você quer:

- **Entender a arquitetura** → Leia [ARQUITETURA_COMPLETA.md](ARQUITETURA_COMPLETA.md)
- **Ver o banco de dados** → Veja [DIAGRAMA_ER.md](DIAGRAMA_ER.md)
- **Adicionar novo recurso** → Siga [GUIA_DESENVOLVEDOR.md#adicionando-novo-recurso](GUIA_DESENVOLVEDOR.md#adicionando-novo-recurso)
- **Testar APIs** → Use [ENDPOINTS.md](ENDPOINTS.md)
- **Configurar ambiente** → Veja [GUIA_DESENVOLVEDOR.md#setup-inicial](GUIA_DESENVOLVEDOR.md#setup-inicial)
- **Boas práticas** → Consulte [GUIA_DESENVOLVEDOR.md#boas-práticas](GUIA_DESENVOLVEDOR.md#boas-práticas). Manter aplicação online**

A aplicação pode ser gerenciada com o script `start-app.sh`:

```bash
# Iniciar (mantém em background)
./start-app.sh start

# Status
./start-app.sh status

# Logs em tempo real
./start-app.sh logs

# Parar
./start-app.sh stop
```

**Detalhes:** [KEEP_ONLINE.md](KEEP_ONLINE.md)

## 🗄️ Banco de Dados H2

O H2 é um banco **em memória** que se cria automaticamente ao iniciar a aplicação.

- **Características**: 
  - Nenhuma instalação necessária
  - Dados são perdidos ao reiniciar (-ddl-auto=create-drop)
  - Perfeito para desenvolvimento e testes
  - Console SQL integrado

- **Console H2**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - User: `sa`
  - Password: (vazio)

**Definição das tabelas:** [DIAGRAMA_ER.md](DIAGRAMA_ER.md)

## 🔗 Acessos da Aplicação

| Recurso | URL | Descrição |
|---------|-----|-----------|
| **Swagger UI** | http://localhost:8080/swagger-ui.html | Documentação interativa das APIs |
| **OpenAPI JSON** | http://localhost:8080/api-docs | Spec OpenAPI 3.0 |
| **H2 Console** | http://localhost:8080/h2-console | Gerenciador SQL web |
| **Actuator** | http://localhost:8080/actuator | Health check e info da app |

## 🧪 Testes

O projeto inclui testes unitários e de integração:

```bash
# Rodar todos os testes
./mvnw test

# Rodar com cobertura (JaCoCo)
./mvnw test jacoco:report
# Resultado: target/site/jacoco/index.html

# Rodar teste específico
./mvnw test -Dtest=PedidoServiceTest
```

**Guia de Testes:** [GUIA_DESENVOLVEDOR.md#testes](GUIA_DESENVOLVEDOR.md#testes)

## Fluxo principal

### 1. Cadastrar cliente
```
POST /api/clientes
{
  "nome": "João Silva",
  "cpf": "12345678901",
  "email": "joao@email.com",
  "telefone": "11999999999",
  "cep": "01310100",
  "logradouro": "Avenida Paulista",
  "bairro": "Bela Vista",
  "cidade": "São Paulo",
  "uf": "SP",
  "numero": "100",
  "complemento": "Apto 42"
}
```
- ✓ Cria cliente
- ✓ Recebe endereço completo do frontend
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

## 🌐 Endpoints (API REST)

Todos os endpoints estão documentados no **Swagger UI**: http://localhost:8080/swagger-ui.html

### Resumo Rápido

**Autenticação:**
- `POST /auth/register` - Registrar novo cliente
- `POST /auth/login` - Logar e obter JWT

**Clientes:**
- `GET /api/clientes` - Listar todos
- `POST /api/clientes` - Criar cliente
- `GET /api/clientes/{id}` - Buscar por ID
- `PUT /api/clientes/{id}` - Atualizar
- `DELETE /api/clientes/{id}` - Deletar

**Contas:**
- `GET /api/contas/{id}` - Consultar saldo
- `PATCH /api/contas/{id}/depositar?valor=1000` - Depositar
- `GET /api/contas/{id}/extrato` - Histórico completo

**Pedidos:**
- `POST /api/pedidos` - Criar pedido
- `PATCH /api/pedidos/{id}/reservar` - Reservar estoque
- `PATCH /api/pedidos/{id}/pagar` - Pagar pedido
- `PATCH /api/pedidos/{id}/cancelar` - Cancelar pedido

**Boletos:**
- `POST /api/boletos/gerar/{pedidoId}` - Gerar boleto
- `PATCH /api/boletos/{id}/pagar` - Pagar boleto

**Documentação Completa:** [ENDPOINTS.md](ENDPOINTS.md)

## 📋 Regras de Negócio

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

## 📡 Serviços (Service Layer)

Os serviços encapsulam a lógica de negócio:

| Serviço | Responsabilidade |
|---------|------------------|
| **ClienteService** | Cadastro, validação, busca de clientes |
| **ContaService** | Operações bancárias (saque, depósito, transferência) |
| **PedidoService** | Ciclo de vida do pedido (criação, reserva, cancelamento) |
| **ProdutoService** | Gerenciamento de catálogo e estoque |
| **PagamentoService** | Processamento de pagamentos e tentativas |
| **BoletoService** | Geração e processamento de boletos |
| **ExtratoService** | Consulta de histórico de movimentações |
| **AuthService** | Registro e autenticação via JWT |

**Detalhes de cada serviço:** [ARQUITETURA_COMPLETA.md#serviços](ARQUITETURA_COMPLETA.md#serviços)

## ✅ Garantia de Qualidade

### Testes Automatizados

O projeto inclui testes em múltiplas camadas:

```bash
# Rodar todos os testes (unitários + integração)
./mvnw test

# Rodar com cobertura de código (JaCoCo)
./mvnw test jacoco:report
# Resultado em: target/site/jacoco/index.html

# Rodar teste específico
./mvnw test -Dtest=ClienteServiceTest

# Rodar método específico
./mvnw test -Dtest=ClienteServiceTest#deveCriarCliente
```

**Exemplos e Detalhes:** [GUIA_DESENVOLVEDOR.md#testes](GUIA_DESENVOLVEDOR.md#testes)

### Validação de Código

- ✅ Sem erros de compilação
- ✅ Sem warnings maiores
- ✅ Cobertura de tentes > 80%
- ✅ Validação de entradas (Jakarta Validation)
- ✅ Tratamento centralizado de erros
- ✅ Transações atômicas em operações críticas

## 📁 Estrutura de Diretórios

```
Back-End/
├── src/
│   ├── main/java/acc/br/projetoFinal/Accenture/
│   │   ├── controller/          # REST Controllers (7 arquivos)
│   │   ├── service/             # Serviços (10 arquivos)
│   │   ├── repository/          # Spring Data Repositories (10 arquivos)
│   │   ├── model/               # Entidades JPA (10 arquivos)
│   │   ├── dto/
│   │   │   ├── request/         # DTOs de entrada (validados)
│   │   │   └── response/        # DTOs de saída (sem dados sensíveis)
│   │   ├── enums/               # 9 enumeradores (Status, Tipo, Etc)
│   │   ├── exception/           # Exceções customizadas
│   │   ├── config/              # Configurações (Security, Swagger, etc)
│   │   ├── security/            # JWT & UserDetails
│   │   └── AccentureApplication.java  # Entry point
│   ├── test/java/...            # Testes unitários & integração
│   └── resources/
│       └── application.properties
├── pom.xml                       # Dependências Maven
├── mvnw / mvnw.cmd              # Maven Wrapper
├── start-app.sh                 # Script de gerenciamento
│
├── 📄 DOCUMENTAÇÃO
├── README_SISTEMA.md            # Este arquivo
├── ARQUITETURA_COMPLETA.md      # Visão técnica completa
├── DIAGRAMA_ER.md               # Banco de dados
├── GUIA_DESENVOLVEDOR.md        # Setup e convenções
├── ENDPOINTS.md                 # Referência de APIs
├── INDICE_DOCUMENTACAO.md       # Índice de todos os docs
└── ...
```

**Detalhes:** [GUIA_DESENVOLVEDOR.md#estrutura-de-pastas](GUIA_DESENVOLVEDOR.md#estrutura-de-pastas)

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
2. **Endereço completo**: O frontend deve enviar todos os campos do endereço (CEP, logradouro, bairro, cidade, UF)
3. **Transações**: Todas as operações críticas usam `@Transactional`
4. **Validações**: Implementadas em DTOs com Jakarta Validation
5. **Multa 10%**: Configurável em `PedidoService.PERCENTUAL_MULTA`

## 🔗 Recursos Úteis

- [Spring Boot Official Docs](https://spring.io/projects/spring-boot)
- [Spring Data JPA Guide](https://spring.io/projects/spring-data-jpa)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Docs](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Lombok Features](https://projectlombok.org/features/all)
- [Jakarta Validation](https://jakarta.ee/specifications/bean-validation/3.0/)

## 👥 Contribuição

Projeto Final do Treinamento Accenture — 2025

Desenvolvido como parte do programa de formação em desenvolvimento de software da Accenture.

---

**Status: ✅ Completo e Testado**

Última atualização: Maio de 2025

