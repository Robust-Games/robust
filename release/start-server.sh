#!/usr/bin/env bash
set -e

# Starte den Robust-Server
# Erwartet: robust-server.jar im gleichen Ordner

DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$DIR"

if [ ! -f robust-server.jar ]; then
  echo "FEHLER: Datei 'robust-server.jar' nicht gefunden!"
  exit 1
fi

java -jar robust-server.jar


