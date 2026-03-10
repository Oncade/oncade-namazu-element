package zyx.oncade.element.rest.webhook.handlers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import zyx.oncade.element.rest.webhook.OncadeWebhookHandler;
import zyx.oncade.element.rest.webhook.events.OncadeWebhookEvent;

import dev.getelements.elements.sdk.annotation.ElementPublic;
import dev.getelements.elements.sdk.annotation.ElementServiceExport;

@ElementPublic
@ElementServiceExport
public class OncadeWebhookHandlerRegistry {
    private static final Logger logger = LoggerFactory.getLogger(OncadeWebhookHandlerRegistry.class);
    private final Map<String, OncadeWebhookHandler<?>> handlers = new HashMap<>();

    @Inject
    @SuppressWarnings("rawtypes")
    public OncadeWebhookHandlerRegistry(Set<OncadeWebhookHandler> handlerInstances) {
        for (OncadeWebhookHandler<?> handler : handlerInstances) {
            Class<? extends Enum<?>> enumClass = handler.getEnumClass();
            for (Enum<?> constant : enumClass.getEnumConstants()) {
                OncadeWebhookEvent eventConstant = (OncadeWebhookEvent) constant;
                String key = eventConstant.getValue();
                if (handlers.put(key, handler) != null) {
                    logger.warn("Duplicate handler mapping for event value '{}' – new handler {} overrides {}", key,
                            handler.getClass(), handlers.get(key).getClass());
                } else {
                    logger.debug("Registered handler {} for event value '{}'", handler.getClass(), key);
                }
            }
        }
    }

    public Optional<OncadeWebhookHandler<?>> findHandler(String eventName) {
        if (eventName == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(handlers.get(eventName));
    }

    public Map<String, OncadeWebhookHandler<?>> getHandlers() {
        return Collections.unmodifiableMap(handlers);
    }
}
