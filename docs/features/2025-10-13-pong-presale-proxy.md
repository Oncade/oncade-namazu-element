# Pong Multiplayer Presale Proxy

## Summary
- Provide a Maven-based Namazu Elements REST server under `examples/Namazu/Pong-Multiplayer/server` that forwards the presale
  account linking, user, UGC product, and wallet purchase endpoints under `/api/v1`.
- Resolve the upstream presale origin from `BASE_URL` (default `https://oncade.gg`) and make the HTTP connect timeout
  configurable via `PRESALE_HTTP_CONNECT_TIMEOUT_SECONDS`.
- Allow only the documented endpoints (link initiate/details, user info/purchases, creator product CRUD, wallet balance/purchase)
  to mitigate accidental exposure of unrelated routes.

## Testing
- Screens: Not applicable — server-side proxy service.
- CLI:
  - `mvn clean package` inside `examples/Namazu/Pong-Multiplayer/server`.
  - `yarn build`
  - `yarn test`
- Manual API smoke (requires valid credentials): start the proxy jar and send `POST /api/v1/users/link/initiate` and
  `GET /api/v1/wallet/purchase/{purchaseId}` to confirm the upstream responses are returned with status and headers intact.
