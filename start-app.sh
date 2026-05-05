#!/bin/bash

# Script para iniciar/parar a aplicação Sistema Loja + Banco

APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_LOG="$APP_DIR/app.log"
APP_PID_FILE="$APP_DIR/.app.pid"

case "${1:-start}" in
  start)
    echo "🚀 Iniciando aplicação..."
    cd "$APP_DIR"
    
    # Kill processo anterior se existir
    if [ -f "$APP_PID_FILE" ]; then
      OLD_PID=$(cat "$APP_PID_FILE")
      if kill -0 "$OLD_PID" 2>/dev/null; then
        echo "⚠️  Parando processo anterior (PID: $OLD_PID)..."
        kill "$OLD_PID"
        sleep 2
      fi
    fi
    
    # Iniciar com nohup
    nohup ./mvnw spring-boot:run -q > "$APP_LOG" 2>&1 &
    APP_PID=$!
    echo $APP_PID > "$APP_PID_FILE"
    
    # Aguardar inicialização
    echo "⏳ Aguardando inicialização..."
    for i in {1..30}; do
      if curl -s http://localhost:8080/api/clientes > /dev/null 2>&1; then
        echo "✅ Aplicação ONLINE (PID: $APP_PID)"
        echo "   🌐 Swagger: http://localhost:8080/swagger-ui.html"
        exit 0
      fi
      sleep 1
    done
    echo "❌ Falha ao iniciar. Verifique $APP_LOG"
    exit 1
    ;;
    
  stop)
    echo "🛑 Parando aplicação..."
    if [ -f "$APP_PID_FILE" ]; then
      PID=$(cat "$APP_PID_FILE")
      kill "$PID" 2>/dev/null && echo "✅ Aplicação parada (PID: $PID)" || echo "❌ Processo não encontrado"
      rm "$APP_PID_FILE"
    else
      pkill -f "spring-boot:run" && echo "✅ Aplicação parada" || echo "❌ Nenhuma aplicação rodando"
    fi
    ;;
    
  status)
    if [ -f "$APP_PID_FILE" ]; then
      PID=$(cat "$APP_PID_FILE")
      if kill -0 "$PID" 2>/dev/null; then
        if curl -s http://localhost:8080/api/clientes > /dev/null 2>&1; then
          echo "✅ ONLINE (PID: $PID)"
        else
          echo "⚠️  Processo rodando mas API não responde"
        fi
      else
        echo "❌ OFFLINE (PID salvo: $PID)"
      fi
    else
      echo "❌ OFFLINE"
    fi
    ;;
    
  logs)
    tail -f "$APP_LOG"
    ;;
    
  *)
    echo "Uso: $0 {start|stop|status|logs}"
    exit 1
    ;;
esac
