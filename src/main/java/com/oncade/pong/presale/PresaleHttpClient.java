package com.oncade.pong.presale;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class PresaleHttpClient {
    static final String DEFAULT_BASE_URL = "https://oncade.gg";
    static final String BASE_URL_ENV = "BASE_URL";
    static final String CONNECT_TIMEOUT_ENV = "PRESALE_HTTP_CONNECT_TIMEOUT_MS";
    static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static Function<String, String> ENV_PROVIDER = System::getenv;
    private static final Set<String> EXCLUDED_REQUEST_HEADERS = Set.of(
            "host",
            "content-length"
    );
    private static final Set<String> EXCLUDED_RESPONSE_HEADERS = Set.of(
            "content-length",
            "transfer-encoding"
    );

    private final HttpClient client;
    private final String baseUrl;

    public PresaleHttpClient() {
        this(resolveBaseUrl(), resolveConnectTimeout());
    }

    PresaleHttpClient(String baseUrl, Duration connectTimeout) {
        this.baseUrl = sanitizeBaseUrl(baseUrl);
        this.client = HttpClient.newBuilder()
                .connectTimeout(connectTimeout)
                .build();
    }

    public Response forward(String method,
                            String upstreamPath,
                            HttpHeaders incomingHeaders,
                            UriInfo uriInfo,
                            Supplier<byte[]> bodySupplier) {
        try {
            HttpRequest request = buildRequest(method, upstreamPath, incomingHeaders, uriInfo, bodySupplier);
            HttpResponse<byte[]> upstream = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            return toResponse(upstream);
        } catch (UncheckedIOException e) {
            return Response.status(Response.Status.BAD_GATEWAY)
                    .entity(e.getMessage())
                    .build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Request interrupted")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_GATEWAY)
                    .entity(e.getMessage())
                    .build();
        }
    }

    private HttpRequest buildRequest(String method,
                                     String upstreamPath,
                                     HttpHeaders incomingHeaders,
                                     UriInfo uriInfo,
                                     Supplier<byte[]> bodySupplier) throws URISyntaxException {
        String query = uriInfo != null && uriInfo.getRequestUri() != null
                ? uriInfo.getRequestUri().getRawQuery()
                : null;
        StringBuilder target = new StringBuilder(baseUrl);
        if (!upstreamPath.startsWith("/")) {
            target.append('/');
        }
        target.append(upstreamPath);
        if (query != null && !query.isEmpty()) {
            target.append('?').append(query);
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI(target.toString()))
                .method(method, bodyPublisher(method, bodySupplier));

        if (incomingHeaders != null) {
            for (Map.Entry<String, List<String>> header : incomingHeaders.getRequestHeaders().entrySet()) {
                if (header.getKey() == null) {
                    continue;
                }
                String headerName = header.getKey();
                if (EXCLUDED_REQUEST_HEADERS.contains(headerName.toLowerCase(Locale.ROOT))) {
                    continue;
                }
                for (String value : header.getValue()) {
                    builder.header(headerName, value);
                }
            }
        }

        return builder.build();
    }

    private HttpRequest.BodyPublisher bodyPublisher(String method, Supplier<byte[]> bodySupplier) {
        if ("GET".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
            return HttpRequest.BodyPublishers.noBody();
        }
        if (bodySupplier == null) {
            return HttpRequest.BodyPublishers.noBody();
        }
        byte[] body = bodySupplier.get();
        if (body == null || body.length == 0) {
            return HttpRequest.BodyPublishers.noBody();
        }
        return HttpRequest.BodyPublishers.ofByteArray(body);
    }

    private Response toResponse(HttpResponse<byte[]> upstream) {
        Response.ResponseBuilder builder = Response.status(upstream.statusCode());
        byte[] body = upstream.body();
        if (body != null && body.length > 0) {
            builder.entity(body);
        }
        upstream.headers().map().forEach((name, values) -> {
            if (name == null) {
                return;
            }
            if (EXCLUDED_RESPONSE_HEADERS.contains(name.toLowerCase(Locale.ROOT))) {
                return;
            }
            for (String value : values) {
                builder.header(name, value);
            }
        });
        return builder.build();
    }

    static String resolveBaseUrl() {
        String env = ENV_PROVIDER.apply(BASE_URL_ENV);
        if (env == null || env.isBlank()) {
            return DEFAULT_BASE_URL;
        }
        return env;
    }

    static Duration resolveConnectTimeout() {
        String env = ENV_PROVIDER.apply(CONNECT_TIMEOUT_ENV);
        if (env == null || env.isBlank()) {
            return DEFAULT_CONNECT_TIMEOUT;
        }
        try {
            long millis = Long.parseLong(env.trim());
            if (millis <= 0) {
                return DEFAULT_CONNECT_TIMEOUT;
            }
            return Duration.ofMillis(millis);
        } catch (NumberFormatException e) {
            return DEFAULT_CONNECT_TIMEOUT;
        }
    }

    private static String sanitizeBaseUrl(String raw) {
        Objects.requireNonNull(raw, "baseUrl");
        if (raw.endsWith("/")) {
            return raw.substring(0, raw.length() - 1);
        }
        return raw;
    }

    static void setEnvProvider(Function<String, String> provider) {
        ENV_PROVIDER = provider == null ? System::getenv : provider;
    }

    static void resetEnvProvider() {
        ENV_PROVIDER = System::getenv;
    }
}
