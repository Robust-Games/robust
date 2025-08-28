#!/usr/bin/env bash
set -euo pipefail

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
SERVER_BIN="$BASE_DIR/robustServer/build/image/bin/app"
LOG_DIR="$BASE_DIR/logs"
mkdir -p "$LOG_DIR"

# Falls systemd-Service existiert, diesen nutzen
if command -v systemctl >/dev/null 2>&1 && systemctl list-unit-files | grep -q '^robust-server\.service'; then
  sudo systemctl restart robust-server
  exit 0
fi

# Direktstart (nohup, im Hintergrund)
if [[ ! -x "$SERVER_BIN" ]]; then
  echo "Server-Binary nicht gefunden: $SERVER_BIN"
  echo "Baue zuerst: (cd robustServer && ./gradlew jlink)"
  exit 1
fi

nohup "$SERVER_BIN" >>"$LOG_DIR/server.log" 2>&1 &
echo $! > "$LOG_DIR/server.pid"
echo "Server gestartet (PID $(cat "$LOG_DIR/server.pid")). Logs: $LOG_DIR/server.log"

