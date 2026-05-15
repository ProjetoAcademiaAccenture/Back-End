# Guia do Desenvolvedor

## Sumário
1. [Setup Inicial](#setup-inicial)
2. [Estrutura de Pastas](#estrutura-de-pastas)
3. [Convenções de Código](#convenções-de-código)
4. [Adicionando Novo Recurso](#adicionando-novo-recurso)
5. [Boas Práticas](#boas-práticas)
6. [Debugging](#debugging)
7. [Testes](#testes)

---

## Setup Inicial

### Pré-requisitos
- **Java 21+** (JDK, não JRE)
- **Maven 3.9+** (ou usar `mvnw`)
- **Git**
- **Visual Studio Code** ou **IntelliJ IDEA** (recomendado)

### Configuração do Ambiente

#### Windows (PowerShell)

```powershell
# 1. Verificar Java
java -version
# Esperado: openjdk 21.0.x

# 2. Verificar Maven
mvn -v
# Esperado: Apache Maven 3.9.x

# 3. Clonar repositório
git clone https://github.com/ProjetoAcademiaAccenture/Back-End.git
cd Back-End

# 4. Compilar projeto
./mvnw.cmd clean compile

# 5. Executar testes
./mvnw.cmd test

# 6. Rodar aplicação
./mvnw.cmd spring-boot:run
```

#### macOS/Linux

```bash
# 1. Verificar Java
java -version

# 2. Clonar e compilar
git clone https://github.com/ProjetoAcademiaAccenture/Back-End.git
cd Back-End

./mvnw clean compile
./mvnw test
./mvnw spring-boot:run
```

### IntelliJ Setup

1. Abrir `Back-End` como projeto
2. Ir para `File → Project Structure`
3. **Project SDK**: Selecionar Java 21
4. **Project Language Level**: 21
5. Abrir `pom.xml` e deixar Maven sincronizar
6. Ativar Lombok: `File → Settings → Plugins → Instalar Lombok`

---

## Estrutura de Pastas

```
Back-End/
├── src/
│   ├── main/
│   │   ├── java/acc/br/projetoFinal/Accenture/
│   │   │   ├── controller/          # REST Controllers
│   │   │   │   ├── ClienteController.java
│   │   │   │   ├── PedidoController.java
│   │   │   │   └── ...
│   │   │   ├── service/             # Lógica de Negócio
│   │   │   │   ├── ClienteService.java
│   │   │   │   ├── PedidoService.java
│   │   │   │   └── ...
│   │   │   ├── repository/          # Data Access
│   │   │   │   ├── ClienteRepository.java
│   │   │   │   ├── PedidoRepository.java
│   │   │   │   └── ...
│   │   │   ├── model/               # Entidades JPA
│   │   │   │   ├── Cliente.java
│   │   │   │   ├── Pedido.java
│   │   │   │   └── ...
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── request/
│   │   │   │   │   ├── ClienteRequestDTO.java
│   │   │   │   │   ├── PedidoRequestDTO.java
│   │   │   │   │   └── ...
│   │   │   │   └── response/
│   │   │   │       ├── ClienteResponseDTO.java
│   │   │   │       ├── PedidoResponseDTO.java
│   │   │   │       └── ...
│   │   │   ├── enums/               # Constantes Enumeradas
│   │   │   │   ├── StatusPedido.java
│   │   │   │   ├── MetodoPagamento.java
│   │   │   │   └── ...
│   │   │   ├── exception/           # Exceções Customizadas
│   │   │   │   ├── RecursoNaoEncontradoException.java
│   │   │   │   ├── EstoqueInsuficienteException.java
│   │   │   │   └── ...
│   │   │   ├── config/              # Configurações Spring
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── SwaggerConfig.java
│   │   │   │   └── ...
│   │   │   ├── security/            # JWT & Autenticação
│   │   │   │   ├── JwtService.java
│   │   │   │   ├── JwtFilter.java
│   │   │   │   └── ...
│   │   │   └── AccentureApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/acc/br/projetoFinal/Accenture/
│           ├── controller/
│           ├── service/
│           ├── dto/
│           └── ...
├── pom.xml
├── README_SISTEMA.md
├── ARQUITETURA_COMPLETA.md
├── DIAGRAMA_ER.md
├── ENDPOINTS.md
└── .gitignore
```

---

## Convenções de Código

### Nomenclatura

```java
// Classes
public class ClienteService { }           // PascalCase

// Métodos
public void criarCliente() { }            // camelCase
public boolean ehValido() { }             // eh* para boolean

// Constantes
private static final BigDecimal TAXA = new BigDecimal("0.10");  // UPPER_SNAKE_CASE

// Variáveis
private String nomeCliente;               // camelCase
private Cliente cliente;                  // camelCase (singular quando possível)

// Packages
package acc.br.projetoFinal.Accenture.service;  // lower_case, inverso de domínio
```

### Documentação (JavaDoc)

```java
/**
 * Calcula o valor total do pedido com desconto.
 * 
 * @param valorBruto valor base do pedido sem desconto
 * @param percentualDesconto desconto a aplicar (0-100)
 * @return valor final após aplicação do desconto
 * @throws IllegalArgumentException se percentualDesconto > 100
 */
public BigDecimal calcularValorFinal(BigDecimal valorBruto, BigDecimal percentualDesconto) {
    // ...
}
```

### Organização do Código

```java
@Service
@RequiredArgsConstructor
public class ClienteService {
    
    // 1. Campos/Dependências injetadas
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    
    // 2. Constantes
    private static final int TAMANHO_MINIMO_NOME = 3;
    
    // 3. Métodos públicos (por ordem alfabética ou lógica)
    @Transactional
    public Cliente criar(ClienteRequestDTO dto) { }
    
    public Cliente buscarPorId(Long id) { }
    
    public List<Cliente> listarTodos() { }
    
    // 4. Métodos privados auxiliares
    private void validarCpf(String cpf) { }
    
    private void validarEmail(String email) { }
}
```

### Anotações

```java
// Controllers
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {
    
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listar() { }
    
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscar(@PathVariable Long id) { }
    
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> criar(@RequestBody @Valid ClienteRequestDTO dto) { }
    
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(
        @PathVariable Long id,
        @RequestBody @Valid ClienteRequestDTO dto
    ) { }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) { }
}

// Services
@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService {
    
    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) { }
    
    @Transactional
    public Cliente criar(ClienteRequestDTO dto) { }
}

// DTOs (Request)
@Data
public class ClienteRequestDTO {
    
    @NotBlank(message = "Nome não pode ser vazio")
    @Size(min = 3, max = 100)
    private String nome;
    
    @NotBlank
    @Size(min = 11, max = 11)
    private String cpf;
    
    @NotBlank
    @Email
    private String email;
}

// Entidades
@Entity
@Table(name = "cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;
}
```

---

## Adicionando Novo Recurso

Exemplo: Criar endpoint para **Categoria de Produtos**

### Passo 1: Criar a Entidade (Model)

```java
// src/main/java/.../model/CategoriaEntity.java
@Entity
@Table(name = "categoria")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoriaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String nome;
    
    @Column(length = 255)
    private String descricao;
    
    @OneToMany(mappedBy = "categoria")
    private List<Produto> produtos = new ArrayList<>();
}
```

### Passo 2: Criar DTOs

```java
// .../dto/request/CategoriaRequestDTO.java
@Data
public class CategoriaRequestDTO {
    @NotBlank private String nome;
    private String descricao;
}

// .../dto/response/CategoriaResponseDTO.java
@Data
public class CategoriaResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    
    public static CategoriaResponseDTO fromEntity(CategoriaEntity entity) {
        return CategoriaResponseDTO.builder()
            .id(entity.getId())
            .nome(entity.getNome())
            .descricao(entity.getDescricao())
            .build();
    }
}
```

### Passo 3: Criar Repository

```java
// .../repository/CategoriaRepository.java
@Repository
public interface CategoriaRepository extends JpaRepository<CategoriaEntity, Long> {
    Optional<CategoriaEntity> findByNome(String nome);
}
```

### Passo 4: Criar Service

```java
// .../service/CategoriaService.java
@Service
@RequiredArgsConstructor
public class CategoriaService {
    
    private final CategoriaRepository repository;
    
    @Transactional
    public CategoriaResponseDTO criar(CategoriaRequestDTO dto) {
        if (repository.findByNome(dto.getNome()).isPresent()) {
            throw new IllegalArgumentException("Categoria já existe");
        }
        
        CategoriaEntity entity = CategoriaEntity.builder()
            .nome(dto.getNome())
            .descricao(dto.getDescricao())
            .build();
        
        CategoriaEntity salva = repository.save(entity);
        return CategoriaResponseDTO.fromEntity(salva);
    }
    
    @Transactional(readOnly = true)
    public CategoriaResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
            .map(CategoriaResponseDTO::fromEntity)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada"));
    }
    
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarTodas() {
        return repository.findAll().stream()
            .map(CategoriaResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO dto) {
        CategoriaEntity entity = repository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada"));
        
        entity.setNome(dto.getNome());
        entity.setDescricao(dto.getDescricao());
        
        return CategoriaResponseDTO.fromEntity(repository.save(entity));
    }
    
    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Categoria não encontrada");
        }
        repository.deleteById(id);
    }
}
```

### Passo 5: Criar Controller

```java
// .../controller/CategoriaController.java
@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {
    
    private final CategoriaService service;
    
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
    
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> criar(@RequestBody @Valid CategoriaRequestDTO dto) {
        CategoriaResponseDTO criada = service.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(criada.getId()).toUri();
        return ResponseEntity.created(location).body(criada);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> atualizar(
        @PathVariable Long id,
        @RequestBody @Valid CategoriaRequestDTO dto
    ) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
```

### Passo 6: Testes (Opcional mas Recomendado)

```java
// .../service/CategoriaServiceTest.java
@SpringBootTest
class CategoriaServiceTest {
    
    @Autowired
    private CategoriaService service;
    
    @Autowired
    private CategoriaRepository repository;
    
    @BeforeEach
    void limpar() {
        repository.deleteAll();
    }
    
    @Test
    void deveCriarCategoria() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNome("Eletrônicos");
        dto.setDescricao("Produtos eletrônicos em geral");
        
        CategoriaResponseDTO resultado = service.criar(dto);
        
        assertNotNull(resultado.getId());
        assertEquals("Eletrônicos", resultado.getNome());
    }
    
    @Test
    void deveNãoCriarCategoriaComMesmoNome() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNome("Eletrônicos");
        
        service.criar(dto);
        
        assertThrows(IllegalArgumentException.class, () -> service.criar(dto));
    }
}
```

---

## Boas Práticas

### 1. Use BigDecimal para Dinheiro

```java
// ✓ CORRETO
@Column(precision = 15, scale = 2)
private BigDecimal valor = BigDecimal.ZERO;

BigDecimal desconto = new BigDecimal("10.50");
BigDecimal resultado = valor.multiply(desconto);

// ✗ ERRADO
private Double valor;                    // Pode perder precisão
private float valor;                     // Pior ainda

BigDecimal resultado = new BigDecimal(10.5);  // Evitar doubles
```

### 2. Use Optional ao invés de null checks

```java
// ✓ CORRETO
return repository.findById(id)
    .orElseThrow(() -> new RecursoNaoEncontradoException("Recurso não encontrado"));

// ✗ ERRADO
Cliente cliente = repository.findById(id).get();  // Pode lançar NoSuchElementException
```

### 3. Use @Transactional apropriadamente

```java
// ✓ CORRETO
@Service
@Transactional
public class PedidoService {
    
    @Transactional(readOnly = true)  // Somente leitura
    public PedidoResponseDTO buscar(Long id) { }
    
    // Herda @Transactional da classe
    public void criar(PedidoRequestDTO dto) { }
}

// ✗ ERRADO
@Transactional
public class DadosService {
    public List<Dados> buscar() { }  // Não precisa de @Transactional full
}
```

### 4. Implemente Validações Customizadas

```java
// DTOs
@Data
public class PedidoRequestDTO {
    @NotEmpty(message = "Pedido deve ter pelo menos um item")
    private List<ItemPedidoRequestDTO> itens;
    
    @NotNull
    @Min(value = 1, message = "Cliente ID deve ser maior que 0")
    private Long clienteId;
}

// Services
@Service
public class PedidoService {
    
    @Transactional
    public Pedido criar(PedidoRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new IllegalArgumentException("Cliente não existe"));
        
        // Validar estoque
        for (ItemPedidoRequestDTO item : dto.getItens()) {
            Produto produto = produtoRepository.findById(item.getProdutoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não existe"));
            
            if (produto.getQuantidadeEstoque() < item.getQuantidade()) {
                throw new EstoqueInsuficienteException(
                    "Produto " + produto.getNome() + " com estoque insuficiente"
                );
            }
        }
        
        // ... criar pedido
    }
}
```

### 5. Use Lazy Loading para Relacionamentos Grandes

```java
// ✓ CORRETO
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "cliente_id")
private Cliente cliente;

// ✗ ERRADO (causa N+1 queries)
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "cliente_id")
private Cliente cliente;
```

### 6. Implemente ToString/Equals com Cuidado

```java
// ✓ CORRETO (com Lombok)
@Entity
@Getter
@Setter
@ToString(exclude = {"pedidos", "enderecos"})  // Evita ciclos
@EqualsAndHashCode(exclude = {"pedidos", "enderecos"})
public class Cliente {
    // ...
}

// ✗ ERRADO
@Entity
@Data  // Gera toString que inclui tudo
public class Cliente {
    @OneToMany
    private List<Pedido> pedidos;  // Ciclo infinito!
}
```

---

## Debugging

### Via IntelliJ IDEA

1. **Breakpoint**: Clicar na margem esquerda da linha de código
2. **Run → Debug 'AccentureApplication'**
3. Na console, usar:
   - Step Into (F7)
   - Step Over (F8)
   - Resume Program (F9)
   - Evaluate Expression (Ctrl+F8)

### Via Logs

```properties
# application.properties
logging.level.acc.br.projetoFinal=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### Exemplo de Debugging com Logs

```java
@Service
public class PedidoService {
    
    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);
    
    @Transactional
    public Pedido criar(PedidoRequestDTO dto) {
        logger.info("Iniciando criação de pedido para cliente: {}", dto.getClienteId());
        
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
            .orElseThrow(() -> {
                logger.error("Cliente não encontrado: {}", dto.getClienteId());
                return new RecursoNaoEncontradoException("Cliente não encontrado");
            });
        
        logger.debug("Cliente encontrado: {} {}", cliente.getId(), cliente.getNome());
        
        // ... resto da lógica
        
        logger.info("Pedido criado com sucesso: {}", pedido.getId());
        return pedido;
    }
}
```

---

## Testes

### Estrutura de Testes

```
src/test/java/acc/br/projetoFinal/Accenture/
├── controller/
│   ├── ClienteControllerTest.java
│   └── PedidoControllerTest.java
├── service/
│   ├── ClienteServiceTest.java
│   ├── PedidoServiceTest.java
│   └── PagamentoServiceTest.java
└── model/
    ├── ClienteTest.java
    └── PedidoTest.java
```

### Teste de Service

```java
@SpringBootTest
class PedidoServiceTest {
    
    @Autowired
    private PedidoService service;
    
    @Autowired
    private PedidoRepository repository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @BeforeEach
    void setup() {
        repository.deleteAll();
        clienteRepository.deleteAll();
    }
    
    @Test
    @DisplayName("Deve criar um pedido com sucesso")
    void deveCriarPedido() {
        // Arrange
        Cliente cliente = Cliente.builder()
            .nome("João Silva")
            .cpf("12345678901")
            .email("joao@test.com")
            .build();
        clienteRepository.save(cliente);
        
        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setClienteId(cliente.getId());
        // ... mais setup
        
        // Act
        PedidoResponseDTO resultado = service.criar(dto);
        
        // Assert
        assertNotNull(resultado.getId());
        assertEquals(StatusPedido.CRIADO, resultado.getStatus());
    }
    
    @Test
    @DisplayName("Deve lançar exceção se cliente não existe")
    void deveErroSemCliente() {
        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setClienteId(999L);
        
        assertThrows(RecursoNaoEncontradoException.class, () -> service.criar(dto));
    }
}
```

### Teste de Controller

```java
@SpringBootTest
@AutoConfigureMockMvc
class PedidoControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PedidoService service;
    
    @Test
    @DisplayName("GET /api/pedidos/{id} retorna pedido")
    void deveBuscarPedidoPorId() throws Exception {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(1L);
        
        when(service.buscarPorId(1L)).thenReturn(dto);
        
        mockMvc.perform(MockMvcRequestBuilders.get("/api/pedidos/1"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
        
        verify(service).buscarPorId(1L);
    }
}
```

### Executar Testes

```bash
# Todos os testes
./mvnw test

# Apenas um arquivo
./mvnw test -Dtest=PedidoServiceTest

# Apenas um método
./mvnw test -Dtest=PedidoServiceTest#deveCriarPedido

# Com cobertura
./mvnw test jacoco:report
# Resultado: target/site/jacoco/index.html
```

---

## Checklist para Pull Request

- [ ] Código compila sem erros
- [ ] Testes passam (./mvnw test)
- [ ] Cobertura de testes > 80%
- [ ] Sem warnings do Sonar/Lint
- [ ] Sem valores hardcoded (use properties)
- [ ] Sem System.out.println() (use logger)
- [ ] Strings usam message bundles/i18n
- [ ] Documentação (JavaDoc) completa
- [ ] Nomes de variáveis/métodos seguem convenções
- [ ] DTOs têm validações apropriadas
- [ ] Services têm @Transactional
- [ ] Repositories usam JPA corretamente
- [ ] Endpoints retornam status HTTP corretos
- [ ] Global Exception Handler cobre todos os casos

---

## Recursos Úteis

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Spring Data JPA Docs](https://spring.io/projects/spring-data-jpa)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Lombok Features](https://projectlombok.org/features/all)


