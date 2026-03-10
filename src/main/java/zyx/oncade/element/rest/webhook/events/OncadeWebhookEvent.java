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

        /**
         * IMPORTANT NOTE:
         *
         * The biggest risk with the reflection‐inferring pattern is that if someone
         * subclasses your handler one level deeper (e.g., abstract class
         * BaseHandler<T extends Enum<T> & OncadeWebhookEvent> extends OncadeWebhookHandler<T>),
         * the logic to extract T might fail (because getGenericSuperclass() may return BaseHandler<T>
         * instead of OncadeWebhookHandler<T>). Make sure your system only uses direct subclasses,
         * or you implement a robust type‐resolution loop (as above).
         *
         * The generic bound <E extends Enum<E> & OncadeWebhookEvent> is exactly the right pattern
         * for enums implementing an interface. See generic bounds documentation.
         *
         * Ensure your static registry registration is reliably triggered (class loading issues) so you don't miss mapping certain enums.
         */
        // Loop through all known enum classes:
        for (Class<? extends OncadeWebhookEvent> enumClass : OncadeWebhookEventRegistry.getRegisteredEventTypes()) {
            // Enum types also implement Enum
            for (OncadeWebhookEvent constant : enumClass.getEnumConstants()) {
                if (constant.getValue().equals(value)) {
                    return constant;
                }
            }
        }
        throw new IllegalArgumentException("Unknown event type: " + value);
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
