package zyx.oncade.element.rest.webhook.events;

public enum OncadePurchaseEvent implements OncadeWebhookEvent {
    PURCHASES_STARTED("Purchases.Started"),
    PURCHASES_FAILED("Purchases.Failed"),
    PURCHASES_CANCELED("Purchases.Canceled"),
    PURCHASES_COMPLETED("Purchases.Completed");

    private final String value;

    OncadePurchaseEvent(String value) { this.value = value; }

    @Override public String getValue() { return value; }

    public static OncadePurchaseEvent fromString(String value) {
        return OncadeWebhookEvent.fromString(OncadePurchaseEvent.class, value);
    }

    static {
        OncadeWebhookEventRegistry.register(OncadePurchaseEvent.class);
    }
}
