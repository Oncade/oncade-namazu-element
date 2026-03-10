#!/usr/bin/env bash
set -euo pipefail

# Usage: element-example.sh [build|dev|test]

REPO_ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")"/../../../.. && pwd)"
APP_DIR="${REPO_ROOT_DIR}/examples/Namazu/Pong-Multiplayer/server-codex"
DOC_HINT="${APP_DIR}/README.md"
ENV_FILE="${APP_DIR}/.env"

function ensure_example_exists() {
  if [[ ! -d "${APP_DIR}" ]]; then
    echo "Namazu/Oncade server side application not present; see ${DOC_HINT}" 1>&2
    exit 1
  fi
}

function ensure_maven() {
  if ! command -v mvn >/dev/null 2>&1; then
    echo "mvn not installed. See ${DOC_HINT}" 1>&2
    exit 1
  fi
}

function load_env() {
  if [[ -f "${ENV_FILE}" ]]; then
    set -a
    # shellcheck disable=SC1090
    source "${ENV_FILE}"
    set +a
  fi
}

function run_build() {
  (cd "${APP_DIR}" && mvn clean package -DskipTests)
}

function run_dev() {
  (
    cd "${APP_DIR}"
    if rg -n "<artifactId>spring-boot-maven-plugin</artifactId>" -S pom.xml >/dev/null 2>&1; then
      mvn spring-boot:run
    else
      # Fall back to running a test Main via exec plugin if not a Spring Boot project
      mvn -DskipTests -Dexec.mainClass=Main -Dexec.classpathScope=test exec:java
    fi
  )
}

function run_test() {
  (cd "${APP_DIR}" && mvn test)
}

function main() {
  if [[ $# -ne 1 ]]; then
    echo "Usage: $0 {build|dev|test}" 1>&2
    exit 2
  fi

  ensure_example_exists
  ensure_maven
  load_env

  case "$1" in
    build)
      run_build
      ;;
    dev)
      run_dev
      ;;
    test)
      run_test
      ;;
    *)
      echo "Unknown command: $1" 1>&2
      echo "Usage: $0 {build|dev|test}" 1>&2
      exit 2
      ;;
  esac
}

main "$@"
