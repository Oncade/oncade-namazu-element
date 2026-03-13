#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

if [[ -z "${TAG:-}" ]]; then
    read -rp "Enter Elements version or git tag: " TAG
fi
export TAG

echo "Updating to TAG=${TAG}..."
docker compose stop
docker compose rm -f
docker compose build --pull
docker compose up -d
echo "Done — Elements ${TAG} is running at http://localhost:8080"
