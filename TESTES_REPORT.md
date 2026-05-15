# 🧪 Suite de Testes Automáticos - Sistema Loja + Banco

## Status Geral
✅ **BUILD SUCCESS** - 16 testes executados e passando  
**Cobertura:** Lógica de negócio, validações, estado das entidades  
**Estrutura:** @SpringBootTest com @Transactional (rollback automático)

---

## 📊 Resultado dos Testes

### SystemIntegrationTests (15 testes)
Testa camada de serviço do sistema: ClienteService, ProdutoService, ContaService

#### ✅ Testes Aprovados

1. **deveCriarClienteComContaAutomatica**
   - Valida criação de cliente com conta CORRENTE automática
   - Assertions: ID do cliente e tipo de conta

2. **deveDepositarNaContaDoCliente**
   - Valida depósito de R$ 5.000,00 na conta
   - Assertions: Saldo = R$ 5.000,00

3. **deveCriarProdutoComDebito**
   - Valida criação de produto com débito automático da empresa
   - Assertions: ID, nome, quantidade de estoque (5 unidades)

4. **deveListarTodosProdutos**
   - Valida listagem de produtos
   - Assertions: Quantidade >= 1

5. **deveBuscarProdutoPorId**
   - Valida busca de produto por ID
   - Assertions: Produto encontrado com nome "Mouse"

6. **deveAtualizarProduto**
   - Valida atualização de informações do produto
   - Assertions: Nome atualizado para "Mouse Atualizado"

7. **deveAjustarEstoqueDoProduto**
   - Valida ajuste de estoque
   - Assertions: Quantidade ajustada para 10 unidades

8. **deveBuscarClientePorCPF**
   - Valida busca de cliente por CPF
   - Assertions: Cliente encontrado com nome "João Silva"

9. **deveValidarSaldoComDepositos**
   - Valida múltiplos depósitos consecutivos
   - Assertions: Saldo final = R$ 6.000,00

10. **deveCriarMultiplosCLientes**
    - Valida criação de múltiplos clientes
    - Assertions: IDs diferentes

11. **deveValidarCPFDuplicado**
    - Valida rejeição de CPF duplicado
    - Assertions: Exceção lançada

12. **deveValidarEmailDuplicado**
    - Valida rejeição de email duplicado
    - Assertions: Exceção lançada

13. **deveDeletarProduto**
    - Valida delete de produto
    - Assertions: Exceção ao buscar produto deletado

14. **deveBuscarClientePorId**
    - Valida busca de cliente por ID
    - Assertions: Cliente encontrado

15. **deveListarTodosClientes**
    - Valida listagem de clientes
    - Assertions: Quantidade >= 1

### AccentureApplicationTests (1 teste)
Teste do contexto da aplicação Spring Boot

#### ✅ Teste Aprovado
- **contextLoads:** Valida carregamento do contexto da aplicação

---

## 🏗️ Estrutura dos Testes

### Setup (@BeforeEach)
```java
- Cria cliente "João Silva" (CPF: 10123456789)
- Cria conta CORRENTE automática
- Deposita R$ 5.000,00 na conta
- Cria produto "Mouse" (R$ 100.00, qtd: 5)
```

### Framework e Libs
- **JUnit 5 (Jupiter):** Framework de testes
- **Spring Test:** @SpringBootTest, @Transactional
- **AssertJ/Assertions:** Validações
- **H2 In-Memory:** Database de testes

### Transação
- Cada teste executa dentro de @Transactional
- Rollback automático após cada teste (estado limpo)
- Garante isolamento entre testes

---

## 🎯 Cobertura de Funcionalidades

### ClienteService ✅
- [x] Criação com geração de CPF/email únicos
- [x] Busca por CPF
- [x] Busca por ID
- [x] Listagem
- [x] Validação de duplicados

### ProdutoService ✅
- [x] Criação com débito automático
- [x] Busca por ID
- [x] Listagem
- [x] Atualização
- [x] Ajuste de estoque
- [x] Delete

### ContaService ✅
- [x] Depósito de valores
- [x] Múltiplos depósitos
- [x] Consulta de saldo
- [x] Conta CORRENTE automática

### Validações ✅
- [x] Rejeição de CPF duplicado
- [x] Rejeição de email duplicado
- [x] Gestão de estoque

---

## 📈 Como Executar os Testes

### Executar Todos os Testes
```bash
./mvnw test
```

### Executar com Saída Detalhada
```bash
./mvnw test -v
```

### Executar Apenas SystemIntegrationTests
```bash
./mvnw test -Dtest=SystemIntegrationTests
```

### Executar Teste Específico
```bash
./mvnw test -Dtest=SystemIntegrationTests#deveCriarClienteComContaAutomatica
```

### Gerar Relatório de Cobertura
```bash
./mvnw test jacoco:report
# Verificar em: target/site/jacoco/index.html
```

---

## 📋 Relatório de Execução

```
Tests run: 16
  ✓ Passed: 16
  ✗ Failed: 0
  ⊘ Errors: 0
  ⊘ Skipped: 0

Time: ~10-20 segundos
Database: H2 In-Memory (create-drop)
Build Status: SUCCESS ✅
```

---

## 🔄 Workflow de Testes

1. **Arrange:** Setup de dados (cliente, produto, conta)
2. **Act:** Execução da operação a testar
3. **Assert:** Validação do resultado

Exemplo - Teste de Depósito:
```java
// Arrange
Conta contaCliente = contaRepository.findByClienteId(cliente.getId())
contaService.depositar(contaCliente.getId(), new BigDecimal("1000.00"));

// Assert
var contaAtualizada = contaRepository.findById(contaCliente.getId())
assertEquals(new BigDecimal("6000.00"), contaAtualizada.getSaldo());
```

---

## ⚠️ Notas Importantes

### Isolamento de Testes
- Cada teste é executado em transação isolada
- Rollback automático garante que dados não persistem
- Portanto, testes podem rodar em qualquer ordem

### Database de Testes
- H2 em-memory criado/destruído a cada execução
- Sem dependência de banco externo
- Ideal para CI/CD

### Extensibilidade Futura
Próximos testes poderiam cobrir:
- Ordem lifecycle completo (CRIADO → RESERVADO → PAGO)
- Cálculo de multa 10% em cancelamento de pedidos PAGO
- Endereço recebido do frontend
- Testes de Boleto (geração de código, vencimento)
- Testes de Controllers REST (MockMvc)
- Testes de Exception Handling

---

## 🐛 Debugging

### Se Testes Falharem

1. **Verificar logs:**
   ```bash
   ./mvnw test -X 2>&1 | grep -E "ERROR|FAILED"
   ```

2. **Executar teste isolado:**
   ```bash
   ./mvnw test -Dtest=SystemIntegrationTests#nomeDoTeste
   ```

3. **Verificar relatórios:**
   ```bash
   cat target/surefire-reports/*.xml
   ```

---

## 📝 Histórico de Commits

- **3379e05** ✅ Testes automáticos: 16 testes de integração implementados

---

**Última Atualização:** 2026-05-05  
**Status:** ✅ TODOS OS TESTES PASSANDO
