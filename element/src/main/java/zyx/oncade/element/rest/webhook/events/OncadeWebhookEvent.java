package zyx.oncade.element.rest.webhook.events;


public interface OncadeWebhookEvent {
    String getValue();

    /**
     * Perform a lookup across all registered enums implementing WebhookEventType.
     * @param value the string value to lookup
     * @return the matching WebhookEventType instance
     * @throws IllegalArgumentException if not found
     */
    static OncadeWebhookEvent fromString(String value) {
        if (value == null) {
            return null;
        }

        for (Class<? extends OncadeWebhookEvent> enumClass : OncadeWebhookEventRegistry.getRegisteredEventTypes()) {
            for (OncadeWebhookEvent constant : enumClass.getEnumConstants()) {
                if (constant.getValue().equals(value)) {
                    return constant;
                }
            }
        }
        return null;
    }

    /**
     * (Optional) Generic version to return a specific enum type.
     * @param <E> the enum type
     * @param enumClass the enum class to search
     * @param value the string value
     * @return the matching enum constant of type E
     */
    static <E extends Enum<E> & OncadeWebhookEvent> E fromString(Class<E> enumClass, String value) {
        if (value == null) {
            return null;
        }
        for (E constant : enumClass.getEnumConstants()) {
            if (constant.getValue().equals(value)) {
                return constant;
            }
        }
        throw new IllegalArgumentException("Unknown event type: " + value + " for enum class " + enumClass);
    }
}
