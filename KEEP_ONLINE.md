# 🚀 Como Manter a Aplicação Online

## Problema Resolvido
A aplicação agora está configurada para **manter-se online** mesmo se você fechar o terminal ou reiniciar.

---

## ⚡ Comandos Rápidos

```bash
cd "Accenture"

# Iniciar aplicação (mantém rodando)
./start-app.sh start

# Parar aplicação
./start-app.sh stop

# Verificar status
./start-app.sh status

# Ver logs em tempo real
./start-app.sh logs
```

---

## 🔄 O que mudou?

| Antes | Depois |
|-------|--------|
| App desligava ao fechar terminal | App continua rodando com `nohup` |
| Sem gerenciamento fácil | Script automatizado `start-app.sh` |
| Sem logs persistentes | Logs salvos em `app.log` |
| Sem PID tracking | PID rastreado em `.app.pid` |

---

## 📋 Como Usar

### 1️⃣ Iniciar a Aplicação
```bash
./start-app.sh start
```

**Output:**
```
🚀 Iniciando aplicação...
⏳ Aguardando inicialização...
✅ Aplicação ONLINE (PID: 38357)
   🌐 Swagger: http://localhost:8080/swagger-ui.html
```

### 2️⃣ Verificar Status
```bash
./start-app.sh status
```

**Output:**
```
✅ ONLINE (PID: 38357)
```

### 3️⃣ Ver Logs
```bash
./start-app.sh logs
```

Mostra os últimos logs em tempo real. Pressione `Ctrl+C` para sair.

### 4️⃣ Parar a Aplicação
```bash
./start-app.sh stop
```

---

## 🔍 Arquivos Criados

| Arquivo | Propósito |
|---------|-----------|
| `start-app.sh` | Script de controle (start/stop/status/logs) |
| `.app.pid` | Rastreia o PID da aplicação |
| `app.log` | Log persistente da aplicação |

---

## 📱 Endpoints Disponíveis

Uma vez iniciada, a aplicação responde em:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Clientes**: http://localhost:8080/api/clientes
- **H2 Console**: http://localhost:8080/h2-console
- **OpenAPI Spec**: http://localhost:8080/api-docs

---

## ✅ Status Atual

```bash
$ ./start-app.sh status
✅ ONLINE (PID: 38357)
```

**A aplicação está ONLINE e pronta para uso!** 🎉

---

## 💡 Dicas

### Manter rodando mesmo fechando terminal
```bash
./start-app.sh start
# Agora você pode fechar o terminal que a app continua rodando!
```

### Reiniciar após alterações no código
```bash
./start-app.sh stop
# Faça suas alterações...
mvnw clean compile
./start-app.sh start
```

### Monitorar logs em background
```bash
./start-app.sh start
./start-app.sh logs  # Em outro terminal
```

---

## 🐛 Troubleshooting

### App não inicia?
```bash
./start-app.sh logs
# Procure por ERROR nos logs
```

### Porta 8080 já em uso?
```bash
lsof -i :8080 | grep LISTEN | awk '{print $2}' | xargs kill -9
./start-app.sh start
```

### Limpar tudo e começar do zero?
```bash
./start-app.sh stop
rm -f .app.pid app.log
./mvnw clean
./start-app.sh start
```

---

**Sistema pronto para produção!** 🚀

