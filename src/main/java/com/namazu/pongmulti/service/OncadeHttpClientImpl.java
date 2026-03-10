package com.namazu.pongmulti.service;

import jakarta.inject.Named;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Set;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import java.net.URI;
import java.util.function.Supplier;

import java.util.concurrent.TimeUnit;

import dev.getelements.elements.sdk.annotation.ElementDefaultAttribute;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OncadeHttpClientImpl implements OncadeHttpClient {

    private final Logger logger = LoggerFactory.getLogger(OncadeHttpClientImpl.class);

    private Client client;

    private String url;
    private Duration timeout;
    private String apiKey;
    private boolean debug;

    @Inject
    public void setProperties(
            @Named("xyz.oncade.http.client.url") String url,
            @Named("xyz.oncade.http.client.timeout") String timeoutStr,
            @Named("xyz.oncade.http.client.apiKey") String apiKey) {
        this.url = sanitizeBaseUrl(url);
        this.timeout = resolveConnectTimeout(timeoutStr);
        this.apiKey = apiKey;
        
        ensureRestrictedHeadersAllowed();
        this.client = ClientBuilder.newBuilder()
                .connectTimeout(this.timeout.toMillis(), TimeUnit.MILLISECONDS)
                .readTimeout(this.timeout.toMillis(), TimeUnit.MILLISECONDS)
                .build();
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public Response forward(String method,
                            String upstreamPath,
                            HttpHeaders incomingHeaders,
                            UriInfo uriInfo,
                            Supplier<byte[]> bodySupplier,
                            String idempotencyKey) {
        try {
            return executeRequest(method, upstreamPath, incomingHeaders, uriInfo, bodySupplier, idempotencyKey);

        } catch (ProcessingException e) {
            return Response.status(Response.Status.BAD_GATEWAY)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_GATEWAY)
                    .entity(e.getMessage())
                    .build();
        }
    }

    private static byte[] resolveBody(String method, Supplier<byte[]> bodySupplier) {
        if ("GET".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
            return null;
        }
        if (bodySupplier == null) {
            return null;
        }
        byte[] body = bodySupplier.get();
        if (body == null || body.length == 0) {
            return null;
        }
        return body;
    }

    public Response executeRequest(String method,
                                    String upstreamPath,
                                    HttpHeaders incomingHeaders,
                                    UriInfo uriInfo,
                                    Supplier<byte[]> bodySupplier) {
        return executeRequest(method, upstreamPath, incomingHeaders, uriInfo, bodySupplier, null);
    }

    public Response executeRequest(String method,
                                    String upstreamPath,
                                    HttpHeaders incomingHeaders,
                                    UriInfo uriInfo,
                                    Supplier<byte[]> bodySupplier,
                                    String idempotencyKey) {
        String query = uriInfo != null && uriInfo.getRequestUri() != null
                ? uriInfo.getRequestUri().getRawQuery()
                : null;

        StringBuilder target = new StringBuilder(this.url);
        if (!upstreamPath.startsWith("/")) {
            target.append('/');
        }
        target.append(upstreamPath);
        if (query != null && !query.isEmpty()) {
            target.append('?').append(query);
        }
        
        URI targetUri = null;
        try {
            targetUri = new URI(target.toString());

        } catch (URISyntaxException e) {
            logger.info("Failed to create target URI for request: {}", target.toString(), e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Failed to create target URI for request to the upstream service")
                    .build();
        }

        Invocation.Builder requestBuilder = client.target(targetUri).request();

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            requestBuilder.header("idempotency-key", idempotencyKey);
        }
        
        // Collect headers for logging
        MultivaluedMap<String, String> requestHeadersMap = null;
        if (incomingHeaders != null) {
            requestHeadersMap = incomingHeaders.getRequestHeaders();
            for (Map.Entry<String, List<String>> header : requestHeadersMap.entrySet()) {
                if (header.getKey() == null) {
                    continue;
                }
                String headerName = header.getKey();
                if (EXCLUDED_REQUEST_HEADERS.contains(headerName.toLowerCase(Locale.ROOT))) {
                    continue;
                }
                for (String value : header.getValue()) {
                    requestBuilder.header(headerName, value);
                }
            }
            if (apiKey != null && !apiKey.isBlank()) {
                requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
            }
        }

        byte[] body = resolveBody(method, bodySupplier);
        
        logRequest(method, targetUri.toString(), apiKey, requestBuilder, requestHeadersMap, body);
        
        Response upstreamResponse;
        if (body == null) {
            upstreamResponse = requestBuilder.method(method);
        } else {
            // Always set Content-Type to application/json
            upstreamResponse = requestBuilder.method(method, Entity.entity(body, MediaType.APPLICATION_JSON));
        }

        try (Response response = upstreamResponse) {
            Response.ResponseBuilder builder = Response.status(response.getStatus());

            byte[] responseBody = null;
            if (response.hasEntity()) {
                responseBody = response.readEntity(byte[].class);
                if (responseBody != null && responseBody.length > 0) {
                    builder.entity(responseBody);
                }
            }

            MultivaluedMap<String, String> headers = response.getStringHeaders();
            if (headers != null) {
                for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                    String name = entry.getKey();
                    if (name == null) {
                        continue;
                    }
                    if (EXCLUDED_RESPONSE_HEADERS.contains(name.toLowerCase(Locale.ROOT))) {
                        continue;
                    }
                    for (String value : entry.getValue()) {
                        builder.header(name, value);
                    }
                }
            }

            logResponse(response.getStatus(), headers, responseBody);

            return builder.build();
        }
    }

    private final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(30);

    private final Set<String> EXCLUDED_REQUEST_HEADERS = Set.of(
            "host",
            "content-length",
            "content-type",
            "authorization"
    );
    
    private final Set<String> EXCLUDED_RESPONSE_HEADERS = Set.of(
            "content-length",
            "transfer-encoding"
    );

    private static String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() <= 8) {
            return "***";
        }
        return apiKey.substring(0, 4) + "..." + apiKey.substring(apiKey.length() - 4);
    }

    private Duration resolveConnectTimeout(String timeoutStr) {
        if (timeoutStr == null || timeoutStr.isBlank()) {
            return DEFAULT_CONNECT_TIMEOUT;
        }
        try {
            long millis = Long.parseLong(timeoutStr.trim());
            if (millis <= 0) {
                return DEFAULT_CONNECT_TIMEOUT;
            }
            return Duration.ofMillis(millis);
        } catch (NumberFormatException e) {
            return DEFAULT_CONNECT_TIMEOUT;
        }
    }

    private static String sanitizeBaseUrl(String url) {
        Objects.requireNonNull(url, "url");
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }

        try {
            java.net.URI.create(url).toURL();

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid base URL provided: " + url, e);
        }

        return url;
    }

    private static void ensureRestrictedHeadersAllowed() {
        System.setProperty("sun.net.http.allowRestrictedHeaders", Boolean.TRUE.toString());
    }
    
    private void logRequest(String method, String url, String apiKey, Invocation.Builder requestBuilder, 
                           MultivaluedMap<String, String> incomingHeaders, byte[] body) {
        
        // TODO: use logger.debug, but set the log level of the logger to debub if this.debug is true.
        if (!debug) {
            return;
        }
        logger.info("=== HTTP Request ===");
        logger.info("Method: {}", method);
        logger.info("URL: {}", url);
        
        logger.info("Headers:");
        if (apiKey != null && !apiKey.isBlank()) {
            logger.info("  Authorization: Bearer {}", maskApiKey(apiKey));
        }
        if (incomingHeaders != null) {
            for (Map.Entry<String, List<String>> entry : incomingHeaders.entrySet()) {
                String headerName = entry.getKey();
                if (headerName != null && !EXCLUDED_REQUEST_HEADERS.contains(headerName.toLowerCase(Locale.ROOT))) {
                    for (String value : entry.getValue()) {
                        logger.info("  {}: {}", headerName, value);
                    }
                }
            }
        }
        
        logger.info("Body:");
        if (body == null || body.length == 0) {
            logger.info("  <empty>");
        } else {
            try {
                String bodyStr = new String(body, StandardCharsets.UTF_8);
                logger.info("  {}", bodyStr);
            } catch (Exception e) {
                logger.info("  <binary data, {} bytes>", body.length);
            }
        }
        logger.info("===================");
    }

    private void logResponse(int status, MultivaluedMap<String, String> headers, byte[] body) {
        // TODO: use logger.debug, but set the log level of the logger to debub if this.debug is true.
        if (!debug) {
            return;
        }

        logger.info("=== HTTP Response ===");
        logger.info("Status: {}", status);
        
        logger.info("Headers:");
        if (headers != null) {
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                String name = entry.getKey();
                if (name != null && !EXCLUDED_RESPONSE_HEADERS.contains(name.toLowerCase(Locale.ROOT))) {
                    for (String value : entry.getValue()) {
                        logger.info("  {}: {}", name, value);
                    }
                }
            }
        }
        
        logger.info("Body:");
        if (body == null || body.length == 0) {
            logger.info("  <empty>");
        } else {
            try {
                String bodyStr = new String(body, StandardCharsets.UTF_8);
                logger.info("  {}", bodyStr);
            } catch (Exception e) {
                logger.info("  <binary data, {} bytes>", body.length);
            }
        }
        logger.info("====================");
    }

}
