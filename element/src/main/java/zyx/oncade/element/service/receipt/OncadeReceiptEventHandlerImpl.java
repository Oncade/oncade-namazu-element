package zyx.oncade.element.service.receipt;

import dev.getelements.elements.sdk.annotation.ElementEventConsumer;
import dev.getelements.elements.sdk.annotation.ElementServiceReference;
import dev.getelements.elements.sdk.dao.ReceiptDao;
import dev.getelements.elements.sdk.model.receipt.Receipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zyx.oncade.element.service.purchase.OncadePurchaseService;

public class OncadeReceiptEventHandlerImpl implements OncadeReceiptEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(OncadeReceiptEventHandlerImpl.class);

    @ElementEventConsumer(
            value = ReceiptDao.RECEIPT_CREATED,
            via = @ElementServiceReference(OncadeReceiptEventHandler.class)
    )
    public void onReceiptCreated(Receipt receipt) {
        if (!OncadePurchaseService.ONCADE_RECEIPT_SCHEMA.equals(receipt.getSchema())) {
            return;
        }

        logger.info("[ReceiptEvent] Oncade receipt created: id={} txn={} user={}",
                receipt.getId(),
                receipt.getOriginalTransactionId(),
                receipt.getUser() != null ? receipt.getUser().getId() : "unknown");
    }
}
