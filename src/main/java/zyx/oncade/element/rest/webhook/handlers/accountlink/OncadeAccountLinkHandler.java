package zyx.oncade.element.rest.webhook.handlers.accountlink;

import jakarta.inject.Inject;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zyx.oncade.element.model.oncade.accountLink.OncadeAccountLink;
import zyx.oncade.element.rest.webhook.OncadeRawWebhookPayload;
import zyx.oncade.element.rest.webhook.OncadeWebhookHandler;
import zyx.oncade.element.rest.webhook.events.OncadeAccountLinkEvent;
import zyx.oncade.element.service.accountLink.OncadeAccountLinkService;

public class OncadeAccountLinkHandler extends OncadeWebhookHandler<OncadeAccountLinkEvent> {
    private static final Logger logger = LoggerFactory.getLogger(OncadeAccountLinkHandler.class);

    private OncadeAccountLinkService accountLinkService;

    public OncadeAccountLinkHandler() {
        super();
    }

    @Inject
    public void setAccountLinkService(final OncadeAccountLinkService accountLinkService) {
        this.accountLinkService = accountLinkService;
    }

    @Override
    public void handleImpl(OncadeRawWebhookPayload rawPayload, Map<String, String> requestData) {
        OncadeAccountLink accountLink = accountLinkService.parseAccountLinkBodyFromJsonNode(rawPayload.getJsonBody());
        if (accountLink == null) {
            logger.warn("[Webhook] Failed to parse account link data from webhook payload");
            return;
        }

        accountLink.setNamazuUserId(requestData.get("namazuUserId"));
        accountLink.setLastIdempotencyKey(requestData.get("idempotencyKey"));

        try {
            accountLinkService.insertAccountLinkEvent(accountLink);
            logger.info("[Webhook] Successfully stored OncadeAccountLink for session {}, userRef {}", accountLink.getSessionKey(), accountLink.getUserRef());

        } catch (Exception ex) {
            logger.error("[Webhook] Failed to store OncadeAccountLink for session {}, userRef {}: {}",
                    accountLink.getSessionKey(), accountLink.getUserRef(), ex.getMessage(), ex);
        }
    }
}
