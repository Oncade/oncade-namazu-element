package zyx.oncade.element.rest.webhook.events;

public enum OncadeAccountLinkEvent implements OncadeWebhookEvent {
    USER_ACCOUNT_LINK_STARTED("User.Account.Link.Started"),
    USER_ACCOUNT_LINK_SUCCEEDED("User.Account.Link.Succeeded"),
    USER_ACCOUNT_LINK_CANCELED("User.Account.Link.Canceled"),
    USER_ACCOUNT_LINK_FAILED("User.Account.Link.Failed"),
    USER_ACCOUNT_LINK_REMOVED("User.Account.Link.Removed");

    private final String value;

    OncadeAccountLinkEvent(String value) { this.value = value; }

    @Override public String getValue() { return value; }

    public static OncadeAccountLinkEvent fromString(String value) {
        return OncadeWebhookEvent.fromString(OncadeAccountLinkEvent.class, value);
    }

    static {
        OncadeWebhookEventRegistry.register(OncadeAccountLinkEvent.class);
    }
}
