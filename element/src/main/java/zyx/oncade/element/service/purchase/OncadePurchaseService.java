package zyx.oncade.element.service.purchase;

import zyx.oncade.element.model.oncade.purchase.OncadePurchase;

import java.time.Instant;

public interface OncadePurchaseService {

    String ONCADE_RECEIPT_SCHEMA = "com.oncade.platform";

    void insertPurchaseEvent(OncadePurchase purchase, Instant receivedAt);

    void createReceipt(OncadePurchase purchase);

    void revokeReceipt(OncadePurchase purchase);
}
