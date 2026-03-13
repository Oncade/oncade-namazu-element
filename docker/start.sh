#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

TAG="${TAG:-3.7.17}"
export TAG

echo "Starting Namazu Elements (TAG=${TAG}) + MongoDB..."
docker compose down --remove-orphans 2>/dev/null || true
docker compose build --pull
docker compose up "$@"
