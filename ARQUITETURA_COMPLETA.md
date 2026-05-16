# Documentação Completa da Arquitetura

## Índice
1. [Visão Geral](#visão-geral)
2. [Arquitetura](#arquitetura)
3. [Modelos (Entities)](#modelos)
4. [Serviços](#serviços)
5. [Controllers & Endpoints](#controllers--endpoints)
6. [DTOs](#dtos)
7. [Segurança](#segurança)

---

## Visão Geral

Sistema de E-commerce integrado com funcionalidades bancárias simuladas, utilizando:
- **Java 21** + **Spring Boot 3.2.5**
- **H2 Database** (em memória)
- **JPA/Hibernate** para persistência
- **Lombok** para reduzir boilerplate
- **Swagger/OpenAPI** para documentação de APIs
- **JWT** para autenticação
- **Spring Security** para autorização

### Stack Tecnológico

```
┌─────────────────────────┐
│    Cliente (React/Vue)  │
└────────────┬────────────┘
             │ HTTP/REST
┌────────────▼────────────┐
│  Spring Boot 3.2.5      │
│  ├─ Spring Web          │
│  ├─ Spring Data JPA     │
│  ├─ Spring Security     │
│  ├─ JWT (JJWT)          │
│  └─ Validation          │
└────────────┬────────────┘
             │ SQL
┌────────────▼────────────┐
│   H2 Database           │
│   (Em Memória)          │
└─────────────────────────┘
```

---

## Arquitetura

### Padrão MVC + Layered

```
┌─────────────────────────────────────────────────┐
│           Controller Layer                       │
│  (ClienteController, PedidoController, ...)     │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│           Service Layer                         │
│  (ClienteService, PedidoService, ...)          │
│  - Lógica de negócio                           │
│  - Transações (@Transactional)                │
│  - Validações                                   │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│         Repository Layer (Data Access)          │
│  (ClienteRepository, PedidoRepository, ...)    │
│  Spring Data JPA (CrudRepository)              │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│           Model Layer                           │
│  (Cliente, Pedido, Produto, ...)               │
│  - Entidades JPA (@Entity)                     │
│  - Mapeamento Objeto-Relacional                │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│           Banco de Dados H2                     │
│  - Tabelas relacionais                         │
│  - Índices para performance                    │
└─────────────────────────────────────────────────┘
```

### Fluxo de uma Requisição

```
1. Cliente envia HTTP Request
   ↓
2. DispatcherServlet (Spring) roteia para Controller apropriado
   ↓
3. Controller valida entrada (DTOs com @Valid)
   ↓
4. Controller chama método do Service
   ↓
5. Service:
   - Valida regras de negócio
   - Chama Repository para acesso a dados
   - Coordena múltiplas operações (transações)
   ↓
6. Repository:
   - Executa query (JPQL/SQL)
   - Persiste/recupera dados
   ↓
7. H2 Database processa SQL
   ↓
8. Resultado retorna em cadeia inversa
   ↓
9. Controller serializa resposta (DTO)
   ↓
10. Cliente recebe JSON/HTTP Response
```

---

## Modelos

### CLIENTE

```java
@Entity
@Table(name = "cliente")
public class Cliente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String cpf;           // Imutável, único
    
    @Column(nullable = false, unique = true)
    private String email;         // Identificador secundário
    
    @Column(nullable = false)
    private String senha;         // Hash (criptografado)
    
    // Relacionamentos
    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL)
    private Conta conta;          // 1:1 - todo cliente tem uma conta
    
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Endereco> enderecos;  // 1:N - múltiplos endereços
    
    @OneToMany(mappedBy = "cliente")
    private List<Pedido> pedidos;      // 1:N - múltiplos pedidos
}
```

**Validações no Modelo:**
- `validarCpf()`: 11 dígitos numéricos
- `validarEmail()`: padrão de email válido
- `validarNome()`: 3-100 caracteres

---

### CONTA

```java
@Entity
@Table(name = "conta")
public class Conta {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String numeroConta;    // Formato: "1234567-8"
    
    @Column(nullable = false)
    private String senhaTransacao; // Para operações sensíveis
    
    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal saldo = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    private TipoConta tipo;        // CORRENTE, POUPANCA, JURIDICA
    
    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal limiteCreditoDisponivel = BigDecimal.ZERO;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;       // Vinculado 1:1 ao cliente
    
    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL)
    private List<Extrato> extratos; // Histórico de movimentações
}
```

**Tipos de Conta:**
- `CORRENTE`: Padrão para pessoas físicas
- `POUPANCA`: Renderiza juros (simulado)
- `JURIDICA`: Para empresas

---

### PEDIDO

```java
@Entity
@Table(name = "pedido")
public class Pedido {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
    
    @Enumerated(EnumType.STRING)
    private StatusPedido status;   // RECEBIDO → RESERVADO → PAGADO → ENTREGUE
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorBruto;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal desconto;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorFinal;
    
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens; // Produtos do pedido
    
    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Pagamento pagamento;   // 1:1 - informações de pagamento
}
```

**Estados do Pedido:**
```
CRIADO → RESERVADO → PAGO → ENTREGUE
            ↓ (cancelamento sem multa)
          CANCELADO (com multa)
```

---

### PRODUTO

```java
@Entity
@Table(name = "produto")
public class Produto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(length = 500)
    private String descricao;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal preco;      // Sempre usa BigDecimal
    
    @Column(name = "url_imagem", length = 500)
    private String urlImagem;
    
    @Column(nullable = false)
    private Integer quantidadeEstoque;
    
    @Enumerated(EnumType.STRING)
    private Categoria categoria;
    
    @Version
    private Long version;          // Versionamento otimista para concorrência
}
```

**Controle de Concorrência:**
- Versionamento otimista com anotação `@Version`
- Previne race conditions em ajustes de estoque

---

### PAGAMENTO

```java
@Entity
@Table(name = "pagamento")
public class Pagamento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;
    
    @Enumerated(EnumType.STRING)
    private MetodoPagamento metodo; // BOLETO, CARTAO, PIX, ...
    
    @Enumerated(EnumType.STRING)
    private StatusPagamento status;  // PENDENTE, APROVADO, RECUSADO
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorBruto;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal desconto;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorFinal;
    
    @Column(nullable = false)
    private LocalDateTime dataCriacao;
    
    private LocalDateTime dataConclusao;
    
    @OneToMany(mappedBy = "pagamento", cascade = CascadeType.ALL)
    private List<TentativaPagamento> tentativas; // Histórico de tentativas
    
    @OneToOne(mappedBy = "pagamento", cascade = CascadeType.ALL)
    private Boleto boleto;         // Opcional: se houver boleto
}
```

---

### BOLETO

```java
@Entity
@Table(name = "boleto")
public class Boleto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "codigo_barras", nullable = false, unique = true, length = 44)
    private String codigoBarras;   // Formato padrão brasileiro
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;
    
    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;  // Predef. 3 dias a partir de hoje
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatusBoleto status = StatusBoleto.PENDENTE;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pagamento_id", nullable = false, unique = true)
    private Pagamento pagamento;
}
```

---

### EXTRATO

```java
@Entity
@Table(name = "extrato")
public class Extrato {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_id", nullable = false)
    private Conta conta;
    
    @Enumerated(EnumType.STRING)
    private TipoExtrato tipo;      // DEBITO, CREDITO, JUROS, TARIFA
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;      // Valor da movimentação
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoAntes;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoDepois;
    
    private String descricao;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;         // Opcional: referência ao pedido
    
    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();
}
```

---

## Serviços

### ClienteService

```java
@Service
@Transactional
public class ClienteService {
    
    // ✓ criar(ClienteRequestDTO dto)
    //   - Valida CPF/Email únicos
    //   - Cria cliente com conta CORRENTE automática
    //   - Cria endereço inicial
    
    // ✓ buscarPorId(Long id)
    //   - Retorna ClienteResponseDTO
    
    // ✓ buscarPorCpf(String cpf)
    //   - Busca por CPF único
    
    // ✓ listarTodos()
    //   - Retorna List<ClienteResponseDTO>
    
    // ✓ atualizar(Long id, ClienteRequestDTO dto)
    //   - Atualiza dados (exceto CPF)
    
    // ✓ deletar(Long id)
    //   - Remove cliente e referências (cascade)
    
    // ✓ adicionarEndereco(Long clienteId, EnderecoRequestDTO dto)
    //   - Integra com ViaCepService
    //   - Enriquece dados de endereço
    
    // ✓ removerEndereco(Long clienteId, Long enderecoId)
    //   - Valida propriedade do endereço
}
```

---

### PedidoService

```java
@Service
@Transactional
public class PedidoService {
    
    private static final BigDecimal PERCENTUAL_MULTA = new BigDecimal("0.10");
    
    // ✓ criar(PedidoRequestDTO dto)
    //   - Valida estoque
    //   - Define status = CRIADO
    //   - Calcula valorBruto e desconto
    
    // ✓ reservar(Long id)
    //   - Valida estoque novamente
    //   - Atualiza status = RESERVADO
    //   - Decrementa quantidadeEstoque
    
    // ✓ pagar(Long id)
    //   - Valida status RESERVADO
    //   - Debita cliente
    //   - Credita empresa
    //   - Registra Extrato
    //   - Status = PAGO
    
    // ✗ cancelar(Long id)
    //   if status == CRIADO || status == RESERVADO:
    //      - Sem multa
    //      - Devolve estoque
    //   else if status == PAGO:
    //      - Multa = 10% do valor
    //      - Estorna (valor - multa)
    
    // ✓ listarPorCliente(Long clienteId)
    //   - Busca pedidos do cliente
}
```

---

### PagamentoService

```java
@Service
@Transactional
public class PagamentoService {
    
    // ✓ criar(Long pedidoId, MetodoPagamento metodo)
    //   - Cria registro de pagamento
    //   - Status = PENDENTE
    
    // ✓ aprovar(Long id)
    //   - Valida saldo da conta do cliente
    //   - Status = APROVADO
    //   - Executa transferência para empresa
    
    // ✓ recusar(Long id)
    //   - Status = RECUSADO
    //   - Registra TentativaPagamento
    
    // ✓ registrarTentativa(Long pagamentoId, MetodoPagamento metodo, StatusPagamento status)
    //   - Cria TentativaPagamento
    //   - Registra timestamp e erro (se houver)
}
```

---

### ContaService

```java
@Service
@Transactional
public class ContaService {
    
    // ✓ criarEntidade(ContaRequestDTO dto)
    //   - Cria conta vinculada a cliente
    //   - Gera numeroConta único
    //   - Tipo baseado no DTO
    
    // ✓ buscarPorId(Long id)
    //   - Retorna conta com saldo atual
    
    // ✓ buscarSaldo(Long id)
    //   - Retorna apenas saldo
    
    // ✓ depositar(Long id, BigDecimal valor)
    //   - Valida valor > 0
    //   - Incrementa saldo
    //   - Registra Extrato (DEPOSITO)
    
    // ✓ sacar(Long id, BigDecimal valor)
    //   - Valida saldo suficiente
    //   - Decrementa saldo
    //   - Registra Extrato (SAQUE)
    
    // ✓ transferir(Long deId, Long paraId, BigDecimal valor)
    //   - Valida ambas as contas
    //   - Debita de + credita para
    //   - Dois lançamentos no Extrato
}
```

---

### BoletoService

```java
@Service
@Transactional
public class BoletoService {
    
    // ✓ gerar(Long pedidoId)
    //   - Valida pedido em status RESERVADO/PAGO
    //   - Gera código de barras (44 dígitos)
    //   - Data de vencimento = hoje + 3 dias
    //   - Status = PENDENTE
    
    // ✓ pagar(Long boletoId)
    //   - Valida status PENDENTE
    //   - Executa transferência bancária
    //   - Status = PAGO
    //   - Atualiza status do Pagamento/Pedido
    
    // ✓ cancelar(Long boletoId)
    //   - Valida status PENDENTE
    //   - Status = CANCELADO
    
    // ✓ buscarPorPedido(Long pedidoId)
    //   - Retorna boleto do pedido (se existir)
    
    // ✓ verificarAtraso(Long boletoId)
    //   - Compara dataVencimento com hoje
}
```

---

### ExtratoService

```java
@Service
@Transactional(readOnly = true)
public class ExtratoService {
    
    // ✓ listarPorConta(Long contaId)
    //   - Retorna List<Extrato> ordenado por data DESC
    
    // ✓ listarPorConta(Long contaId, LocalDateTime inicio, LocalDateTime fim)
    //   - Filtra por período
    
    // ✓ listarPorConta(Long contaId, TipoExtrato tipo)
    //   - Filtra por tipo (DEBITO, CREDITO, etc.)
    
    // ✓ criarExtrato(Conta conta, TipoExtrato tipo, BigDecimal valor, String descricao)
    //   - Uso interno: registra movimentação
    //   - Atualiza saldoAntes e saldoDepois automaticamente
}
```

---

### AuthService

```java
@Service
@Transactional
public class AuthService {
    
    // ✓ register(ClienteRequestDTO dto)
    //   - Delega para ClienteService.criarEntidade()
    //   - Gera JWT token
    //   - Retorna AuthResponseDTO com token
    
    // ✓ login(LoginRequestDTO dto)
    //   - Autenticado via AuthenticationManager + UserDetails
}
```

---

## Controllers & Endpoints

### ClienteController

```
GET    /api/clientes                    → Lista todos os clientes
GET    /api/clientes/{id}               → Busca cliente por ID
GET    /api/clientes/cpf/{cpf}          → Busca cliente por CPF
POST   /api/clientes                    → Cria novo cliente
PUT    /api/clientes/{id}               → Atualiza dados do cliente
DELETE /api/clientes/{id}               → Deleta cliente

POST   /api/clientes/{id}/enderecos     → Adiciona endereço
DELETE /api/clientes/{id}/enderecos/{enderecoId} → Remove endereço
```

---

### PedidoController

```
GET    /api/pedidos                     → Lista todos os pedidos
GET    /api/pedidos/{id}                → Busca pedido por ID
GET    /api/pedidos/cliente/{clienteId} → Lista pedidos do cliente
POST   /api/pedidos                     → Cria novo pedido

PATCH  /api/pedidos/{id}/reservar       → Reserva estoque
PATCH  /api/pedidos/{id}/pagar          → Paga pedido
PATCH  /api/pedidos/{id}/cancelar       → Cancela pedido
```

---

### ContaController

```
GET    /api/contas/{id}                      → Consulta saldo da conta
PATCH  /api/contas/{id}/depositar            → Deposita valor
GET    /api/contas/{id}/extrato              → Lista extrato completo
GET    /api/contas/{id}/extrato?tipo=DEBITO  → Filtra por tipo
GET    /api/contas/{id}/extrato?inicio=...&fim=...   → Filtra por período
```

---

### BoletoController

```
GET    /api/boletos/{id}               → Consulta boleto
GET    /api/boletos/pedido/{pedidoId}  → Busca boleto do pedido
POST   /api/boletos/gerar/{pedidoId}   → Gera novo boleto

PATCH  /api/boletos/{id}/pagar         → Paga boleto
PATCH  /api/boletos/{id}/cancelar      → Cancela boleto
```

---

### AuthController

```
POST   /auth/register                  → Cria novo cliente + JWT
POST   /auth/login                     → Autentica + JWT
POST   /auth/register-bank             → Cria conta bancária
POST   /auth/login-bank                → Autentica conta bancária
```

---

## DTOs

### Request DTOs

Serão recebidos do cliente (validados com Jakarta Validation):

```java
@Data
public class ClienteRequestDTO {
    @NotBlank private String nome;
    @NotBlank @Size(min=11, max=11) private String cpf;
    @NotBlank @Email private String email;
    @NotBlank private String senha;
    private String telefone;
    @NotNull private LocalDate dtNascimento;
    @NotNull private EnderecoRequestDTO endereco;
}

@Data
public class PedidoRequestDTO {
    @NotNull private Long clienteId;
    @NotEmpty private List<ItemPedidoRequestDTO> itens;
}

@Data
public class ProdutoRequestDTO {
    @NotBlank private String nome;
    @NotNull @DecimalMin("0.01") private BigDecimal preco;
    @NotNull @Min(1) private Integer quantidade;
    @NotNull private Categoria categoria;
}

@Data
public class ContaRequestDTO {
    @NotNull private Long clienteId;
    @NotBlank private String senhaTransacao;
    @NotNull private TipoConta tipoConta;
}
```

---

### Response DTOs

Serão retornados para o cliente (sem dados sensíveis):

```java
@Data
public class ClienteResponseDTO {
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;
    private List<EnderecoResponseDTO> enderecos;
    
    public static ClienteResponseDTO fromEntity(Cliente cliente) {
        return ClienteResponseDTO.builder()
            .id(cliente.getId())
            .nome(cliente.getNome())
            .cpf(cliente.getCpf())
            .email(cliente.getEmail())
            // ... mapeamento completo
            .build();
    }
}

@Data
public class PedidoResponseDTO {
    private Long id;
    private Long clienteId;
    private LocalDateTime dataCriacao;
    private StatusPedido status;
    private BigDecimal valorFinal;
    private List<ItemPedidoResponseDTO> itens;
}

@Data
public class ContaResponseDTO {
    private Long id;
    private String numeroConta;
    private BigDecimal saldo;
    private TipoConta tipo;
    private BigDecimal limiteCreditoDisponivel;
}
```

---

## Segurança

### Autenticação (JWT)

```
1. Cliente faz POST /auth/register ou /auth/login com credenciais
2. AuthController valida credenciais via AuthenticationManager
3. Se válido, JwtService gera JWT token:
   - Subject: email do cliente
   - Expiração: 24 horas (configurável)
   - Secret: string aleatória (em application.properties)
4. Token retornado ao cliente (no header Authorization)
5. Subsequentes requisições incluem:
   Authorization: Bearer <token>
6. JwtFilter valida token antes de processar requisição
```

### Autorização (Roles)

```
ROLE_USER        → Cliente normal (pode acessar dados próprios)
ROLE_ADMIN       → Administrador (acesso total)
ROLE_MODERATOR   → Moderador (acesso limitado)
```

### Proteção

- Senhas: hashed com BCrypt (PasswordEncoder)
- Transações sensíveis: requerem hesaTransacao
- CPF: campo imutável
- H2: console apenas em desenvolvimento

---

## Tratamento de Erros

### Exceções Customizadas

```java
// GlobalExceptionHandler mapeia:

RecursoNaoEncontradoException     → 404 NOT_FOUND
EstoqueInsuficienteException      → 422 UNPROCESSABLE_ENTITY
SaldoInsuficienteException        → 422 UNPROCESSABLE_ENTITY
CancelamentoException             → 400 BAD_REQUEST
IllegalArgumentException          → 400 BAD_REQUEST
MethodArgumentNotValidException   → 400 BAD_REQUEST (validação)
Exception (genérica)              → 500 INTERNAL_SERVER_ERROR
```

### ErrorResponse DTO

```java
{
  "timestamp": "2025-05-15T10:30:45.123456",
  "status": 422,
  "error": "Estoque Insuficiente",
  "message": "Quantidade solicitada (5) maior que disponível (3)"
}
```

---

## Performance & Otimizações

1. **Lazy Loading**: Relacionamentos usam `fetch = FetchType.LAZY`
2. **Índices**: Criados para campos frequentemente consultados
3. **Versionamento Otimista**: `@Version` em Produto (controle de concorrência)
4. **N+1 Query Prevention**: Use projections/DTOs em listagens
5. **Connection Pooling**: H2 gerencia pool de conexões automaticamente
6. **Paginação**: Controllers devem aceitar `@PageableDefault(size=20)`

---

## Configurações Importantes

### application.properties

```properties
# Servidor
server.port=8080
server.servlet.context-path=/

# H2 Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true

# JPA/Hibernate
spring.jpa.show-sql=false
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.properties.hibernate.format_sql=true

# Swagger/OpenAPI
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

# JWT
app.jwt.secret=<chave-secreta-aleatoria>
app.jwt.expiration=86400000  # 24 horas em milissegundos

# Logging
logging.level.root=INFO
logging.level.acc.br.projetoFinal=DEBUG
```

---

## Fluxos Principais

### Fluxo de Criação de Pedido

```
1. Cliente → POST /api/pedidos (ClienteRequestDTO)
2. PedidoController → valida DTO
3. PedidoService.criar() →
   - Busca cliente
   - Valida estoque
   - Cria Pedido (status = CRIADO)
   - Cria ItemPedido para cada item
4. ResponseEntity retorna PedidoResponseDTO (201 CREATED)
5. Cliente → PATCH /api/pedidos/{id}/reservar
6. PedidoService.reservar() →
   - Valida status CRIADO
   - Decrementa quantidadeEstoque
   - Status = RESERVADO
7. Cliente → PATCH /api/pedidos/{id}/pagar (OU gera boleto)
8. PedidoService.pagar() →
   - Debita cliente
   - Credita empresa
   - Registra 2 lançamentos em Extrato
   - Status = PAGO
```

### Fluxo de Cancelamento com Multa

```
1. Pedido está em PAGO
2. Cliente → PATCH /api/pedidos/{id}/cancelar
3. PedidoService.cancelar() →
   - Calcula multa = valorFinal * 0.10
   - Estorno = valorFinal - multa
   - Debita cliente (estorno)
   - Credita empresa (multa)
   - Devolve estoque
   - Status = CANCELADO
4. Extrato registra ambas movimentações
```

---

Documentação Completa da Arquitetura do Projeto Accenture

