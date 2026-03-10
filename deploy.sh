#!/usr/bin/env bash

set -euo pipefail

echo -n "Building..."
mvn clean package -DskipTest
echo "OK"

cd deploy

echo -n "Removing old libs..."
git rm -fr lib || true
echo "OK"

mkdir -p lib

echo -n "Copy the deploy file..."
cp ../dev.getelements.element.attributes.properties .
echo "OK"

echo -n "Copying new build..."
cp ../target/Oncade-1.0.jar ../target/element-libs/* lib/
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
