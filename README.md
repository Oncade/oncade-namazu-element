# Pong Multiplayer Presale Proxy
This project provides a lightweight Elements service that proxies a curated set of Oncade presale API endpoints for the Pong multiplayer demo. It is based on the official [Elements example](../element-example/) project and can be deployed to an Elements environment alongside the Pong example.

## Endpoints

The proxy exposes the following REST endpoints under the `/api` base path:

- `POST /api/v1/users/link/initiate`
- `GET /api/v1/users/link/details`
- `GET /api/v1/users/{userId}`
- `GET /api/v1/users/{userId}/purchases`
- `POST /api/v1/products`
- `GET /api/v1/products/{userRef}`
- `POST /api/v1/products/{userRef}/{productId}/submit`
- `POST /api/v1/products/{userRef}/{productId}/review`
- `POST /api/v1/wallet/balance`
- `POST /api/v1/wallet/purchase`
- `GET /api/v1/wallet/purchase/{purchaseId}`

Requests are transparently forwarded to the presale backend located at `${BASE_URL}` (default `https://oncade.gg`). Query strings and headers (except `Host` and `Content-Length`) are preserved so the upstream API receives the same payload the Elements client submitted.

## Configuration

| Environment Variable | Description | Default |
| --- | --- | --- |
| `BASE_URL` | Target presale host that receives proxied requests. | `https://oncade.gg` |
| `PRESALE_HTTP_CONNECT_TIMEOUT_MS` | Connect timeout in milliseconds for the upstream HTTP client. | `5000` |

## Development

1. Install Java 21 and Maven 3.9+.
2. From this directory run `mvn clean package` to build the proxy and copy dependencies into `target/element-libs`.
3. Deploy the generated JAR and libraries to an Elements deployment repo (see the main element example README for packaging guidance).

Run `mvn test` to execute the unit tests that cover environment resolution and request routing.
