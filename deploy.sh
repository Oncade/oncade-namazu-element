#!/usr/bin/env bash

set -euo pipefail

echo -n "Building..."
mvn clean install -DskipTests
echo "OK"

cd deploy

echo -n "Removing old artifacts..."
git rm -fr lib 2>/dev/null || true
rm -f *.elm
echo "OK"

echo -n "Copy the deploy file..."
cp ../dev.getelements.element.attributes.properties .
echo "OK"

echo -n "Copying new ELM archive..."
cp ../element/target/*.elm .
echo "OK"

echo -n "Adding build to repo..."
git add *
echo "OK"

echo -n "Commit build..."
git commit --amend --no-edit
echo "OK"

echo -n "Push build..."
git push -f origin main
echo "OK"
