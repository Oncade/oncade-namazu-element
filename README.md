# Oncade Namazu Element

A custom [Namazu Elements](https://namazustudios.com/) Element that bridges the Oncade platform with the Elements runtime. It receives Oncade webhook events (purchases, account links), creates SDK receipts, and exposes REST/WebSocket endpoints for connected games.

Built against **Namazu Elements SDK 3.7.17** using the ELM archive format.

[![](https://jitpack.io/v/Oncade/oncade-namazu-element.svg)](https://jitpack.io/#Oncade/oncade-namazu-element)

## Project Structure

```
oncade-namazu-element/
├── api/       ← Interfaces exported to other Elements (classified API jar)
├── element/   ← Main element implementation (produces .elm archive)
├── debug/     ← Local runner using ElementsLocalBuilder
└── pom.xml    ← Parent POM with sdk-bom import
```

## Requirements

- Java 21+
- Maven 3.9+
- Elements SDK 3.7.17 installed locally (see below)

## Building the Elements SDK

The Elements SDK 3.7.17 must be installed in your local Maven repository before building:

```bash
git clone --depth 1 --branch 3.7.17 https://github.com/NamazuStudios/elements.git /tmp/elements-sdk
cd /tmp/elements-sdk
mvn install -DskipTests
```

## Build

```bash
mvn clean install -DskipTests
```

The ELM archive is produced at `element/target/zyx.oncade.element-1.0-SNAPSHOT.elm`.

## Local Development

Run the `debug` module's `run` class from your IDE with the working directory set to the project root. It uses `ElementsLocalBuilder.withSourceRoot()` to automatically build and load the element.

Make sure MongoDB is running locally (see `services-dev/` in the element-example repo).

## JitPack

This project is published via [JitPack](https://jitpack.io/#Oncade/oncade-namazu-element). Add JitPack as a repository and reference the release tag as the version.

## Deployment

Run `deploy.sh` to build the ELM archive and push it to the deployment repository.
