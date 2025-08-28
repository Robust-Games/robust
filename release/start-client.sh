#!/usr/bin/env bash
set -e

# Starte den Robust-Client
# Erwartet: robust-client.jar im gleichen Ordner

DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$DIR"

if [ ! -f robustClient.jar
 ]; then
  echo "FEHLER: Datei 'robust-client.jar' nicht gefunden!"
  exit 1
fi

java -jar robustClient.jar


