package com.namazu.pongmulti.rest.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.namazu.pongmulti.rest.webhook.events.OncadeWebhookEvent;

import jakarta.ws.rs.core.MultivaluedMap;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class OncadeWebhookPayload<T> {
    private final OncadeWebhookEvent event;
    private final MultivaluedMap<String, String> headers;
    private final String rawBody;
    private final JsonNode jsonBody;
    private final Instant receivedAt = Instant.now();

    protected OncadeWebhookPayload(String event,
                                   MultivaluedMap<String, String> headers,
                                   String rawBody,
                                   JsonNode jsonBody) {
        this.event = OncadeWebhookEvent.fromString(event);
        this.headers = headers;
        this.rawBody = rawBody;
        this.jsonBody = jsonBody;
    }

    public OncadeWebhookEvent getEvent() {
        return event;
    }

    public MultivaluedMap<String, String> getHeaders() {
        return headers;
    }

    public String getIdempotencyKey() {
        String idempotencyKey = headers.getFirst("Idempotency-Key");
        if (idempotencyKey == null) {
            if (jsonBody != null && jsonBody.has("data")) {
                JsonNode data = jsonBody.get("data");
                if (data != null && data.has("metadata")) {
                    JsonNode metadata = data.get("metadata");
                    if (metadata != null && metadata.hasNonNull("idempotencyKey")) {
                        idempotencyKey = metadata.get("idempotencyKey").asText();
                    }
                }
            }
        }
        return idempotencyKey;
    }

    public String getRawBody() {
        return rawBody;
    }

    public JsonNode getJsonBody() {
        return jsonBody;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public abstract T parseData(Map<String, String> requestData);

    protected static String getText(JsonNode node, String field) {
        return node.hasNonNull(field) ? node.get(field).asText() : null;
    }

    protected static Integer getInt(JsonNode node, String field) {
        return node.hasNonNull(field) ? node.get(field).asInt() : null;
    }

    public String formattedHeaders() {
        if (headers == null || headers.isEmpty()) {
            return "{}";
        }
        return headers.entrySet()
                .stream()
                .map(this::formatHeaderEntry)
                .collect(Collectors.joining(", ", "{", "}"));
    }

    private String formatHeaderEntry(Map.Entry<String, List<String>> entry) {
        String value = entry.getValue() == null ? "[]" : entry.getValue().toString();
        return entry.getKey() + "=" + value;
    }

    public String formattedBody() {
        if (jsonBody != null) {
            return jsonBody.toPrettyString();
        }
        return rawBody;
    }

}