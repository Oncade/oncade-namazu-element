package zyx.oncade.element.rest.webhook.events;

public enum OncadeWebhookTestEvent implements OncadeWebhookEvent {
    WEBHOOK_TEST("Webhook.Test");

    private final String value;

    OncadeWebhookTestEvent(String value) { this.value = value; }

    @Override public String getValue() { return value; }

    public static OncadeWebhookTestEvent fromString(String value) {
        return OncadeWebhookEvent.fromString(OncadeWebhookTestEvent.class, value);
    }

    static {
        OncadeWebhookEventRegistry.register(OncadeWebhookTestEvent.class);
    }
}
