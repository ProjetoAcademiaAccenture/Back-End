# 📑 ÍNDICE DE DOCUMENTAÇÃO - Projeto Accenture

## 🎯 Para Começar Aqui

Bem-vindo à documentação do **Sistema de Gerenciamento de Vendas e Contas**! Este projeto apresenta uma implementação completa de um e-commerce com integração bancária, desenvolvido em **Spring Boot** com **testes automatizados**.

Se você é um **avaliador** ou quer **entender o projeto**, siga o caminho recomendado abaixo:

---

## 📖 Documentos Disponíveis

### 1. 📄 **SUMARIO_EXECUTIVO.md** (13 KB)
**Para: Leitura Rápida (5-10 min)**

Comece aqui! Contém:
- ⚡ Overview do projeto
- 📊 Estatísticas gerais (452+ testes)
- 🏛️ Arquitetura visual
- 🗄️ Modelo de dados simplificado
- 🎯 Casos de teste principais
- 🔒 Segurança implementada
- 📈 Cobertura de testes por módulo
- ✅ Checklist de requisitos

**Tempo de Leitura:** 5-10 minutos

---

### 2. 📦 **APRESENTACAO_PROJETO.md** (19 KB)
**Para: Compreensão Completa (20-30 min)**

Depois do sumário, estude este documento para:
- 🎓 Apresentação detalhada do projeto
- 🏗️ Arquitetura do projeto (estrutura de pastas)
- 📊 **Modelo Lógico Relacional completo**
  - 8 Entidades explicadas em detalhe
  - Relacionamentos (1:N, N:1, 1:1)
  - Constraints de banco de dados
- 🧪 **Cobertura de Testes em Profundidade**
  - Service Tests (130+)
  - Controller Tests (116+)
  - DTO Tests (73+)
  - Security Tests (63+)
  - Integration Tests (8)
- 🚀 Endpoints principais
- 📋 Como executar
- 🔍 Fluxo completo: Do pedido ao pagamento

**Tempo de Leitura:** 20-30 minutos

---

### 3. 📊 **DIAGRAMA_MODELO_DADOS.md** (15 KB)
**Para: Visualização Técnica (15-20 min)**

Use este para entender a estrutura de dados:
- 📊 Diagrama Entidade-Relacionamento (ASCII Art)
- 🔗 Relacionamentos explicados (8 diferentes)
- 📋 SQL Constraints detalhados
- 🔄 Estados e transições (PEDIDO, BOLETO, CONTA)
- 🔸 Tipos de movimentação (EXTRATO)
- 📈 Índices para performance
- ✓ Validações por entidade
- 🔄 Fluxo de dados (Criar Pedido, Pagar Boleto)

**Tempo de Leitura:** 15-20 minutos

---

## 🛤️ Caminhos Recomendados por Perfil

### 👨‍💼 Para o Avaliador/Professor
```
1. SUMARIO_EXECUTIVO.md        (5 min)  ← Comece aqui
2. APRESENTACAO_PROJETO.md     (25 min) ← Detalhes completos
3. DIAGRAMA_MODELO_DADOS.md    (15 min) ← Validação técnica
                               ─────────
                               Total: ~45 min
```

### 👨‍💻 Para o Desenvolvedor
```
1. DIAGRAMA_MODELO_DADOS.md    (15 min) ← Entenda o BD
2. APRESENTACAO_PROJETO.md     (25 min) ← Arquitetura
3. SUMARIO_EXECUTIVO.md        (5 min)  ← Resumo
                               ─────────
                               Total: ~45 min
```

### 🧪 Para Tester/QA
```
1. SUMARIO_EXECUTIVO.md        (5 min)  ← Visão geral
2. APRESENTACAO_PROJETO.md     (20 min) ← Foco em testes
3. Executar testes: mvn test   (5-10 min)
                               ─────────
                               Total: ~40 min
```

---

## 📍 Onde Encontrar

Todos os documentos estão na **raiz do projeto**:

```
Back-End/Accenture/
├── SUMARIO_EXECUTIVO.md           ← Leia primeiro
├── APRESENTACAO_PROJETO.md         ← Leia segundo
├── DIAGRAMA_MODELO_DADOS.md        ← Leia terceiro
├── README.md                       ← Instruções técnicas
├── ENDPOINTS.md                    ← API endpoints
├── TESTES_REPORT.md                ← Relatório de testes
└── src/                            ← Código-fonte
```

---

## 🎯 Seções Chave por Documento

### SUMARIO_EXECUTIVO.md
| Seção | Página |
|---|---|
| Overview Rápido | Início |
| Estatísticas do Projeto | 2 |
| Arquitetura Implementada | 2-3 |
| Modelo de Dados (Resumido) | 3 |
| Estratégia de Testes | 4-5 |
| Casos de Teste Principais | 5-6 |
| Segurança Implementada | 6-7 |
| Como Usar O Sistema | 7-8 |
| Checklist de Requisitos | 8 |

### APRESENTACAO_PROJETO.md
| Seção | Página |
|---|---|
| Visão Geral | Início |
| Arquitetura do Projeto | 1-2 |
| Modelo Lógico Relacional | 2 |
| 8 Entidades Detalhadas | 3-8 |
| Segurança (JWT, Autorização) | 8-9 |
| Cobertura de Testes (452+) | 9-14 |
| Endpoints Principais | 14-15 |
| Como Executar | 15-16 |
| Métricas de Qualidade | 16 |
| Fluxo Completo | 16-17 |
| Resumo Executivo | 17-18 |

### DIAGRAMA_MODELO_DADOS.md
| Seção | Página |
|---|---|
| Diagrama ER (ASCII) | Início |
| Relacionamentos Explicados | 2-3 |
| Constraints SQL | 3-4 |
| Estados e Transições | 4-6 |
| Tipos de Movimentação | 6 |
| Índices para Performance | 6-7 |
| Validações por Entidade | 7 |
| Fluxos de Dados | 7-8 |
| Resumo Técnico | 8 |

---

## 🔍 Buscando Informações Específicas?

### "Quero entender os testes"
→ **SUMARIO_EXECUTIVO.md** → Seção "Estratégia de Testes"
→ **APRESENTACAO_PROJETO.md** → Seção "Cobertura de Testes"

### "Quero ver o modelo de dados"
→ **DIAGRAMA_MODELO_DADOS.md** → Diagrama ER (início)
→ **APRESENTACAO_PROJETO.md** → Seção "8 Entidades do Modelo"

### "Quero entender a arquitetura"
→ **SUMARIO_EXECUTIVO.md** → Seção "Arquitetura Implementada"
→ **APRESENTACAO_PROJETO.md** → Seção "Arquitetura do Projeto"

### "Quero ver os endpoints"
→ **APRESENTACAO_PROJETO.md** → Seção "Endpoints Principais"
→ **SUMARIO_EXECUTIVO.md** → Seção "Como Usar O Sistema"

### "Como rodo o projeto?"
→ **APRESENTACAO_PROJETO.md** → Seção "Como Executar"
→ **SUMARIO_EXECUTIVO.md** → Seção "Como Usar O Sistema"

### "Qual é a cobertura de testes?"
→ **SUMARIO_EXECUTIVO.md** → Seção "Cobertura de Testes por Módulo"
→ **APRESENTACAO_PROJETO.md** → Seção "Cobertura de Testes"

---

## 📊 Estatísticas Rápidas

| Métrica | Valor |
|---|---|
| **Arquivos Java** | 35+ |
| **Linhas de Código** | 5.000+ |
| **Entidades** | 8 |
| **Controllers** | 8 |
| **Services** | 8 |
| **Arquivos de Teste** | 56 |
| **Total de Testes** | 452+ |
| **Taxa de Sucesso** | 100% ✅ |
| **Cobertura Média** | ~52% |
| **Endpoints** | ~80 |
| **Tipos de Relacionamento** | 3 (1:N, N:1, 1:1) |

---

## ✅ Checklist do Avaliador

Use isto para verificar se o projeto atende aos requisitos:

### Arquitetura
- ✅ Camadas bem definidas (Controller → Service → Repository)
- ✅ DTOs para transferência de dados
- ✅ Enums para estados/tipos
- ✅ Injeção de dependência
- ✅ Tratamento de exceções

### Banco de Dados
- ✅ 8 entidades mapeadas
- ✅ Relacionamentos corretos (1:N, N:1, 1:1)
- ✅ Constraints em nível de BD
- ✅ Validações de integridade
- ✅ Cascade delete apropriado

### Segurança
- ✅ JWT com expiração
- ✅ Senhas com BCrypt
- ✅ Autorização por papéis
- ✅ Validação em múltiplas camadas
- ✅ Endpoints protegidos

### Testes
- ✅ 452+ casos de teste
- ✅ Testes unitários (Service)
- ✅ Testes de integração (Controller)
- ✅ Testes de validação (DTO)
- ✅ Testes de segurança

### Código
- ✅ Nomes descritivos
- ✅ Sem código duplicado
- ✅ Princípios SOLID
- ✅ Clean Code practices
- ✅ Documentação presente

---

## 🚀 Próximos Passos

Se quiser:

1. **Entender melhor o código**
   → Examine as classes em `src/main/java/`
   → Estude os testes em `src/test/java/`

2. **Rodar o projeto**
   ```bash
   cd /home/henriquefurtado/Área de Trabalho/Accenture/projeto/Back-End/Accenture
   mvn spring-boot:run
   ```

3. **Executar testes**
   ```bash
   mvn test
   # Ou com cobertura:
   mvn clean test jacoco:report
   ```

4. **Ver documentação interativa**
   - Acesse: http://localhost:8080/swagger-ui.html

5. **Consultar banco de dados**
   - H2 Console: http://localhost:8080/h2-console
   - URL: jdbc:h2:mem:loja_db

---

## 📞 Dúvidas Frequentes

### P: Por onde começo?
R: Leia o **SUMARIO_EXECUTIVO.md** (5 min). Se quiser mais detalhes, passe para **APRESENTACAO_PROJETO.md**.

### P: Quero entender o modelo de dados
R: Leia **DIAGRAMA_MODELO_DADOS.md** na íntegra. Tem ASCII diagrams e explicações.

### P: Quantos testes tem?
R: 452+ testes, 100% de sucesso. Veja detalhes em **APRESENTACAO_PROJETO.md** → "Cobertura de Testes".

### P: Como rodo os testes?
R: `mvn test` na pasta do projeto. Veja **APRESENTACAO_PROJETO.md** → "Como Executar".

### P: Qual é a cobertura?
R: ~52% média. Breakdown por módulo em **SUMARIO_EXECUTIVO.md** → "Cobertura de Testes por Módulo".

### P: Como autentico no sistema?
R: Via JWT. POST /api/auth/login. Veja endpoints em **APRESENTACAO_PROJETO.md** → "Endpoints Principais".

### P: Qual é o fluxo de um pedido?
R: Veja **APRESENTACAO_PROJETO.md** → "Fluxo Completo: Do Pedido ao Pagamento".

---

## 📝 Documentação Complementar

Além dos 3 documentos principais, você pode consultar:

- **README.md** - Instruções técnicas gerais
- **ENDPOINTS.md** - Lista completa de endpoints com exemplos cURL
- **TESTES_REPORT.md** - Relatório detalhado de testes
- **Código-fonte** - Comentários nas classes principais

---

## 🎓 Conceitos Cobertos

### Padrões de Design
- MVC/REST
- DTO Pattern
- Service Locator
- Singleton
- Factory

### Princípios SOLID
- Single Responsibility
- Open/Closed
- Liskov Substitution
- Interface Segregation
- Dependency Inversion

### Tecnologias
- Spring Boot
- JPA/Hibernate
- JWT
- Lombok
- JUnit 5 + Mockito
- AssertJ

---

## 📈 Evolução Esperada

Este projeto pode ser estendido com:
- [ ] Autenticação OAuth2
- [ ] Microserviços
- [ ] Cache com Redis
- [ ] Message Queue (RabbitMQ)
- [ ] CI/CD (GitHub Actions)
- [ ] Docker + Kubernetes
- [ ] Frontend React/Angular
- [ ] Mobile App (Flutter/React Native)

---

## ✨ Resumo Final

```
🎯 Projeto: Sistema E-commerce com Contas Bancárias
📦 Tecnologia: Spring Boot 3.2.5 + Java 21
🧪 Testes: 452+ casos (100% sucesso)
📊 Cobertura: ~52%
🔐 Segurança: JWT + BCrypt
📚 Documentação: 3 documentos + código comentado
✅ Status: Pronto para Avaliação
```

---

**Documentação Gerada em:** 11 de Maio de 2026  
**Versão:** 1.0.0  
**Autor:** [Seu Nome]  
**Instituição:** Accenture Academy

---

## 🔗 Links Rápidos

- [SUMARIO_EXECUTIVO.md](./SUMARIO_EXECUTIVO.md) - Leitura rápida
- [APRESENTACAO_PROJETO.md](./APRESENTACAO_PROJETO.md) - Documentação completa
- [DIAGRAMA_MODELO_DADOS.md](./DIAGRAMA_MODELO_DADOS.md) - Modelo técnico
- [README.md](./README.md) - Instruções técnicas
- [ENDPOINTS.md](./ENDPOINTS.md) - API endpoints

---

**Bom estudo! 📚**
