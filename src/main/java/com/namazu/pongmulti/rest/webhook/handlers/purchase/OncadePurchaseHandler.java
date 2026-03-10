package com.namazu.pongmulti.rest.webhook.handlers.purchase;

import com.google.inject.Inject;
import com.namazu.pongmulti.model.oncade.purchase.OncadePurchase;
import com.namazu.pongmulti.rest.webhook.OncadeRawWebhookPayload;
import com.namazu.pongmulti.rest.webhook.OncadeWebhookHandler;
import com.namazu.pongmulti.rest.webhook.events.OncadePurchaseEvent;
import com.namazu.pongmulti.service.purchase.OncadePurchaseService;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OncadePurchaseHandler extends OncadeWebhookHandler<OncadePurchaseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(OncadePurchaseHandler.class);

    private OncadePurchaseService purchaseService;

    public OncadePurchaseHandler() {
        super();
    }

    @Inject
    public void setPurchaseService(final OncadePurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @Override
    public void handleImpl(OncadeRawWebhookPayload rawPayload, Map<String, String> requestData) {
        OncadePurchaseWebhook purchaseWebhook = new OncadePurchaseWebhook(
                rawPayload.getEvent().getValue(),
                rawPayload.getHeaders(),
                rawPayload.getRawBody(),
                rawPayload.getJsonBody()
        );

        OncadePurchase purchase = purchaseWebhook.parseData(requestData);
        if (purchase == null) {
            logger.warn("[Webhook] Failed to parse purchase data from webhook payload");
            return;
        }

        try {
            purchaseService.insertPurchaseEvent(purchase, purchaseWebhook.getReceivedAt());
            logger.info("[Webhook] Successfully stored OncadePurchase for purchaseId {}", purchase.getPurchaseId());

        } catch (Exception ex) {
            logger.error("[Webhook] Failed to store OncadePurchase for purchaseId {}: {}", 
                    purchase.getPurchaseId(), ex.getMessage(), ex);
        }
    }
}