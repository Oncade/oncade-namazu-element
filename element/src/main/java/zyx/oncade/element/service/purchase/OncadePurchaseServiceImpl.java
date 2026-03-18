package zyx.oncade.element.service.purchase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.getelements.elements.sdk.model.exception.NotFoundException;
import dev.getelements.elements.sdk.model.receipt.CreateReceiptRequest;
import dev.getelements.elements.sdk.model.receipt.Receipt;
import dev.getelements.elements.sdk.service.receipt.ReceiptService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zyx.oncade.element.model.oncade.purchase.OncadePurchase;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class OncadePurchaseServiceImpl implements OncadePurchaseService {

    private static final Logger logger = LoggerFactory.getLogger(OncadePurchaseServiceImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private MongoDatabase mongoDatabase;

    private ReceiptService receiptService;

    @Inject
    public void setReceiptService(final ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @Inject
    public void setMongoDatabase(final MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    @Override
    public void insertPurchaseEvent(OncadePurchase purchase, Instant receivedAt) {
        Objects.requireNonNull(purchase, "Purchase must not be null");

        MongoCollection<Document> collection = mongoDatabase.getCollection(OncadePurchase.COLLECTION_NAME);

        Document document = new Document();

        if (purchase.getNamazuUserId() != null) {
            document.append("namazuUserId", purchase.getNamazuUserId());
        }
        if (purchase.getPurchaseId() != null) {
            document.append("purchaseId", purchase.getPurchaseId());
        }
        if (purchase.getItemId() != null) {
            document.append("itemId", purchase.getItemId());
        }
        if (purchase.getGameId() != null) {
            document.append("gameId", purchase.getGameId());
        }
        if (purchase.getItemType() != null) {
            document.append("itemType", purchase.getItemType());
        }
        if (purchase.getUserRef() != null) {
            document.append("userRef", purchase.getUserRef());
        }
        if (purchase.getUserEmail() != null) {
            document.append("userEmail", purchase.getUserEmail());
        }
        if (purchase.getAmount() != null) {
            document.append("amount", purchase.getAmount());
        }
        if (purchase.getCurrency() != null) {
            document.append("currency", purchase.getCurrency());
        }
        if (purchase.getMetadata() != null) {
            document.append("metadata", new Document(purchase.getMetadata()));
        }

        document.append("timestamp", receivedAt.toEpochMilli());

        collection.insertOne(document);
    }

    @Override
    public void createReceipt(OncadePurchase purchase) {
        Objects.requireNonNull(purchase, "Purchase must not be null");

        String purchaseId = purchase.getPurchaseId();
        String userId = purchase.getNamazuUserId();

        if (purchaseId == null || userId == null) {
            logger.warn("Cannot create receipt: purchaseId={}, namazuUserId={}", purchaseId, userId);
            return;
        }

        CreateReceiptRequest request = new CreateReceiptRequest();
        request.setOriginalTransactionId(purchaseId);
        request.setSchema(ONCADE_RECEIPT_SCHEMA);
        request.setUserId(userId);
        request.setPurchaseTime(Instant.now().toEpochMilli());
        request.setBody(serializePurchaseBody(purchase));

        Receipt receipt = receiptService.createReceipt(request);

        logger.info("Created receipt id={} schema={} txn={} for user={}",
                receipt.getId(), ONCADE_RECEIPT_SCHEMA, purchaseId, userId);
    }

    @Override
    public void revokeReceipt(OncadePurchase purchase) {
        Objects.requireNonNull(purchase, "Purchase must not be null");

        String purchaseId = purchase.getPurchaseId();
        if (purchaseId == null) {
            logger.warn("Cannot revoke receipt: purchaseId is null");
            return;
        }

        try {
            Receipt existing = receiptService.getReceiptBySchemaAndTransactionId(
                    ONCADE_RECEIPT_SCHEMA, purchaseId);
            receiptService.deleteReceipt(existing.getId());
            logger.info("Revoked receipt id={} for purchaseId={}", existing.getId(), purchaseId);
        } catch (NotFoundException e) {
            logger.warn("No receipt found to revoke for schema={} purchaseId={}",
                    ONCADE_RECEIPT_SCHEMA, purchaseId);
        }
    }

    private String serializePurchaseBody(OncadePurchase purchase) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("purchaseId", purchase.getPurchaseId());
        body.put("itemId", purchase.getItemId());
        body.put("itemType", purchase.getItemType());
        body.put("gameId", purchase.getGameId());
        body.put("userRef", purchase.getUserRef());
        body.put("userEmail", purchase.getUserEmail());
        body.put("amount", purchase.getAmount());
        body.put("currency", purchase.getCurrency());
        if (purchase.getMetadata() != null) {
            body.put("metadata", purchase.getMetadata());
        }

        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            logger.warn("Failed to serialize purchase body, falling back to toString: {}", e.getMessage());
            return purchase.toString();
        }
    }
}
