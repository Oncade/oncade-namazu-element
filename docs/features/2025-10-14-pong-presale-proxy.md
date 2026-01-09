# Pong Multiplayer Presale Proxy for Elements

## Summary
- Added a Maven-based Elements application in `examples/Namazu/Pong-Multiplayer/server-codex` that proxies required presale `/api/v1` endpoints to the Oncade backend.
- Configured environment-driven base URL and HTTP connect timeout handling for the proxy's upstream client.
- Documented build steps and added unit tests covering environment resolution and routing behaviours.

## Testing
- `mvn -f examples/Namazu/Pong-Multiplayer/server-codex/pom.xml clean package`
- `mvn -f examples/Namazu/Pong-Multiplayer/server-codex/pom.xml test`

## Manual Verification
1. Build the proxy with `mvn clean package` inside `examples/Namazu/Pong-Multiplayer/server-codex`.
2. Deploy the resulting artifact to an Elements environment configured with the presale API credentials.
3. Send a `POST /api/v1/users/link/initiate` request to the Elements instance and confirm it returns a 200/201 response from the presale backend.
4. Verify that `GET /api/v1/wallet/purchase/{purchaseId}` mirrors the upstream JSON payload and HTTP status.
