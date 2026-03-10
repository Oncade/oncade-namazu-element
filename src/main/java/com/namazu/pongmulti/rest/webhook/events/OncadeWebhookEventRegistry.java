package com.namazu.pongmulti.rest.webhook.events;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple registry to keep track of all enums implementing WebhookEventType.
 */
public final class OncadeWebhookEventRegistry {
    private static final Set<Class<? extends OncadeWebhookEvent>> events = new HashSet<Class<? extends OncadeWebhookEvent>>();

    private OncadeWebhookEventRegistry() {
        // hide constructor
    }

    /** Register an enum type. */
    public static void register(Class<? extends OncadeWebhookEvent> enumClass) {
        if (!enumClass.isEnum()) {
            throw new IllegalArgumentException(enumClass + " is not an enum");
        }
        events.add(enumClass);
    }

    /** Get the list of registered event‐type enum classes. */
    public static Set<Class<? extends OncadeWebhookEvent>> getRegisteredEventTypes() {
        return Collections.unmodifiableSet(events);
    }
}
