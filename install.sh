#!/usr/bin/env bash
set -euo pipefail

# Dieses Skript liegt im Ordner robust/
BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
CLIENT_DIR="$BASE_DIR/robustClient"
OS="$(uname -s)"

cd "$CLIENT_DIR"

# 1) Bauen mit Gradle jpackage
if [[ -x "./gradlew" ]]; then
  ./gradlew --no-daemon jpackage
else
  gradle --no-daemon jpackage
fi

# 2) Paket finden + installieren (plattformabhängig)
pkg=""
case "$OS" in
  Linux)
    # typische Pfade vom beryx-jlink-Plugin
    pkg=$(find build/jpackage -maxdepth 3 -type f -name "*.deb" | head -n 1 || true)
    [[ -n "$pkg" ]] || { echo "Kein .deb gefunden."; exit 1; }
    echo "Installiere: $pkg"
    # Bevorzugt apt (löst Abhängigkeiten automatisch), fallback dpkg+apt -f
    if command -v apt >/dev/null 2>&1; then
      sudo apt install -y "./$pkg" || { echo "apt install fehlgeschlagen"; exit 1; }
    else
      sudo dpkg -i "$pkg" || sudo apt-get -f install -y
    fi
    ;;
  Darwin)
    pkg=$(find build/jpackage -maxdepth 3 -type f -name "*.pkg" -o -name "*.dmg" | head -n 1 || true)
    [[ -n "$pkg" ]] || { echo "Kein .pkg/.dmg gefunden."; exit 1; }
    if [[ "$pkg" == *.pkg ]]; then
      echo "Installiere: $pkg"
      sudo installer -pkg "$pkg" -target /
    else
      echo "DMG gefunden: $pkg"
      echo "Bitte manuell mounten und in Applications ziehen."
    fi
    ;;
  MINGW*|MSYS*|CYGWIN*|Windows_NT)
    pkg=$(find build/jpackage -maxdepth 3 -type f -name "*.msi" -o -name "*.exe" | head -n 1 || true)
    [[ -n "$pkg" ]] || { echo "Kein .msi/.exe gefunden."; exit 1; }
    echo "Windows-Paket erstellt: $pkg"
    echo "Bitte Installation manuell starten."
    ;;
  *)
    echo "Unbekanntes OS: $OS"; exit 1;;
esac

# 3) Symlink zu app-Binary im robust/ Ordner erstellen
APP_BIN="$CLIENT_DIR/build/jpackage/app/bin/app"
TARGET="$BASE_DIR/app"

if [[ ! -f "$APP_BIN" ]]; then
    echo "❌ Binary nicht gefunden: $APP_BIN"
    exit 1
fi

# existierenden Symlink oder Datei entfernen
rm -f "$TARGET"

# Symlink anlegen
ln -s "$APP_BIN" "$TARGET"
chmod +x "$APP_BIN"

echo "✅ Symlink erstellt: $TARGET -> $APP_BIN"




