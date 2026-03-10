package zyx.oncade.element.rest.webhook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import zyx.oncade.element.rest.webhook.events.OncadeWebhookEvent;
import zyx.oncade.element.rest.webhook.handlers.OncadeWebhookHandlerRegistry;
import zyx.oncade.element.service.OncadeService;
import zyx.oncade.element.util.WebhookSignatureVerifier;

import dev.getelements.elements.sdk.Element;
import dev.getelements.elements.sdk.ElementSupplier;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MultivaluedMap;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/api/v1/webhook")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OncadeWebhookResource {

    private static final Logger logger = LoggerFactory.getLogger(OncadeWebhookResource.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String SIGNATURE_HEADER = "x-oncade-signature";

    private final Element element = ElementSupplier
        .getElementLocal(OncadeWebhookResource.class)
        .get();

    private final OncadeService oncadeService = element
        .getServiceLocator()
        .getInstance(OncadeService.class);

    private final OncadeWebhookHandlerRegistry handlerRegistry = element
        .getServiceLocator()
        .getInstance(OncadeWebhookHandlerRegistry.class);

    private final boolean debug = oncadeService.isDebug();

    @POST
    public Response handleWebhook(@Context HttpHeaders httpHeaders, String body) {
        if (body == null) {
            body = "";
        }

        MultivaluedMap<String, String> headers = httpHeaders.getRequestHeaders();

        if (debug) {
            logger.info("[Webhook] Headers: {}", headers);
            logger.info("[Webhook] Body: {}", body);
        }

        String signature = httpHeaders.getHeaderString(SIGNATURE_HEADER);
        String webhookSecret = oncadeService.getWebhookSecret();

        if (signature == null || signature.isBlank()) {
            logger.warn("[Webhook] Missing {} header", SIGNATURE_HEADER);
            return unauthorizedResponse("Missing signature header");
        }

        if (!WebhookSignatureVerifier.verify(body, signature, webhookSecret)) {
            logger.warn("[Webhook] Invalid signature");
            return unauthorizedResponse("Invalid signature");
        }

        JsonNode jsonBody;
        try {
            jsonBody = OBJECT_MAPPER.readTree(body);

        } catch (JsonProcessingException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Invalid JSON payload");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        String event = jsonBody.path("event").asText(null);

        OncadeRawWebhookPayload payload = new OncadeRawWebhookPayload(event, headers, body, jsonBody);

        if (debug) {
            logger.info("[Webhook] Received event: {}", event);
        }

        handlerRegistry.findHandler(event)
                .ifPresentOrElse(
                        handler -> handler.handle(payload),
                        () -> logUnhandledEvent(payload)
                );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("event", event);
        return Response.ok(response).build();
    }

    private Response unauthorizedResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        return Response.status(Response.Status.UNAUTHORIZED).entity(error).build();
    }

    private void logUnhandledEvent(OncadeRawWebhookPayload payload) {
        OncadeWebhookEvent event = payload.getEvent();
        if (event == null) {
            logger.info("[Webhook] Received payload without an event type");
        } else {
            logger.info("[Webhook] No handler registered for event {}", event);
        }
        logger.info("[Webhook] Headers: {}", payload.formattedHeaders());
        logger.info("[Webhook] Body:\n{}", payload.formattedBody());
    }
}
