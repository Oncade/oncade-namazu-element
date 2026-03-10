package zyx.oncade.element.rest.webhook;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.ws.rs.core.MultivaluedMap;

public class OncadeRawWebhookPayload extends OncadeWebhookPayload<Void> {

    public OncadeRawWebhookPayload(String event,
                                   MultivaluedMap<String, String> headers,
                                   String rawBody,
                                   JsonNode jsonBody) {
        super(event, headers, rawBody, jsonBody);
    }

    @Override
    public Void parseData(Map<String, String> requestData) {
        // Not used for generic raw payload
        return null;
    }
}
