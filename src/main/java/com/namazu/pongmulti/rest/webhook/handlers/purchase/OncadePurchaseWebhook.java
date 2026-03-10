package com.namazu.pongmulti.rest.webhook.handlers.purchase;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.namazu.pongmulti.model.oncade.purchase.OncadePurchase;
import com.namazu.pongmulti.rest.webhook.OncadeWebhookPayload;

import jakarta.ws.rs.core.MultivaluedMap;

public class OncadePurchaseWebhook extends OncadeWebhookPayload<OncadePurchase> {

    public OncadePurchaseWebhook(String event,
                                 MultivaluedMap<String, String> headers,
                                 String rawBody,
                                 JsonNode jsonBody) {
        super(event, headers, rawBody, jsonBody);
    }

    @Override
    public OncadePurchase parseData(Map<String, String> requestData) {
        JsonNode body = getJsonBody();
        if (body == null || !body.has("data")) {
            return null;
        }

        JsonNode data = body.get("data");

        String purchaseId = OncadeWebhookPayload.getText(data, "purchaseId");
        String itemId     = OncadeWebhookPayload.getText(data, "itemId");
        String itemType   = OncadeWebhookPayload.getText(data, "itemType");
        String userEmail  = OncadeWebhookPayload.getText(data, "userEmail");
        Integer amount    = OncadeWebhookPayload.getInt(data, "amount");
        String currency   = OncadeWebhookPayload.getText(data, "currency");
        String userRef    = OncadeWebhookPayload.getText(data, "userId");
        String gameId     = OncadeWebhookPayload.getText(data, "gameId");

        Map<String, Object> metadata = parseMetadata(data.path("metadata"));

        return new OncadePurchase(
                purchaseId,
                itemId,
                itemType,
                userEmail,
                amount,
                currency,
                metadata,
                gameId,
                userRef,
                requestData.get("namazuUserId")
        );
    }

    private static Map<String, Object> parseMetadata(JsonNode metadataNode) {
        if (metadataNode == null || !metadataNode.isObject()) {
            return null;
        }

        Map<String, Object> metadataMap = new HashMap<>();
        metadataNode.fields().forEachRemaining(entry -> {
            JsonNode value = entry.getValue();
            Object parsedValue;
            if (value.isTextual())      parsedValue = value.asText();
            else if (value.isInt())     parsedValue = value.asInt();
            else if (value.isLong())    parsedValue = value.asLong();
            else if (value.isDouble())  parsedValue = value.asDouble();
            else if (value.isBoolean()) parsedValue = value.asBoolean();
            else if (value.isNull())    parsedValue = null;
            else                        parsedValue = value.toString();
            metadataMap.put(entry.getKey(), parsedValue);
        });
        return metadataMap;
    }
}
