package com.namazu.pongmulti.rest.webhook;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import com.google.inject.Inject;
import com.namazu.pongmulti.rest.webhook.events.OncadeWebhookEvent;
import com.namazu.pongmulti.service.OncadeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class OncadeWebhookHandler<E extends Enum<E> & OncadeWebhookEvent> {
    protected final Class<E> enumClass;
    private static final Logger logger =  LoggerFactory.getLogger(OncadeWebhookHandler.class);
    private E eventName;
    
    private OncadeService oncadeService;
    
    @Inject
    public void setOncadeService(final OncadeService oncadeService) {
        this.oncadeService = oncadeService;
    }

    @SuppressWarnings("unchecked")
    public OncadeWebhookHandler() {
        this.eventName = null;
        Type generic = getClass().getGenericSuperclass();
        Class<?> clazz = getClass();
        while (!(generic instanceof ParameterizedType) && clazz != Object.class) {
            clazz = clazz.getSuperclass();
            generic = clazz.getGenericSuperclass();
        }
        if (!(generic instanceof ParameterizedType)) {
            throw new IllegalStateException("Could not resolve generic type parameter E for " + getClass());
        }
        Type typeArg = ((ParameterizedType) generic).getActualTypeArguments()[0];
        this.enumClass = (Class<E>) typeArg;
        if (enumClass == null || !enumClass.isEnum()) {
            throw new IllegalStateException("Generic type E is not an enum: " + enumClass.getName());
        }
    }

    public Class<E> getEnumClass() {
        return enumClass;
    }

    public void setEventName(E eventName) {
        this.eventName = eventName;
    }

    public E getEventName() {
        return eventName;
    }

    public void handle(OncadeRawWebhookPayload payload) {
        if (payload == null) {
            return;
        }

        E event;
        try {
            event = OncadeWebhookEvent.fromString(enumClass, payload.getEvent().getValue());
            setEventName(event);

        } catch (IllegalArgumentException ex) {
            logger.warn("[Webhook] Unknown event type: {}", payload.getEvent().getValue());
            return;
        }

        try {
            String idempotencyKey = payload.getIdempotencyKey();
            if (idempotencyKey == null) {
                logger.warn("[Webhook] Idempotency key is null, skipping request data retrieval");
                return;
            }

            Map<String, String> requestData = oncadeService.getRequest(idempotencyKey);
            if (requestData == null) {
                logger.warn("[Webhook] Request data not found for idempotency key: {}", idempotencyKey);
                return;
            }

            handleImpl(payload, requestData);
            oncadeService.deleteRequest(payload.getIdempotencyKey());

        } catch (Exception e) {
            logger.error("[Webhook] Failed to handle webhook: {}", e.getMessage(), e);
        }
    }

    protected abstract void handleImpl(OncadeRawWebhookPayload payload, Map<String, String> requestData);
}