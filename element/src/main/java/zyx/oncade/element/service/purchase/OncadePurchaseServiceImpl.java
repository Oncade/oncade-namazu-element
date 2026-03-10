package zyx.oncade.element.service.purchase;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.getelements.elements.sdk.service.inventory.DistinctInventoryItemService;
import dev.getelements.elements.sdk.model.inventory.DistinctInventoryItem;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zyx.oncade.element.model.oncade.purchase.OncadePurchase;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class OncadePurchaseServiceImpl implements OncadePurchaseService {

    private static final Logger logger = LoggerFactory.getLogger(OncadePurchaseServiceImpl.class);

    private MongoDatabase database;

    private DistinctInventoryItemService distinctInventoryItemService;

    @Inject
    public void setDistinctInventoryItemService(final DistinctInventoryItemService distinctInventoryItemService) {
        this.distinctInventoryItemService = distinctInventoryItemService;
    }

    @Inject
    public void setMongoDependencies(final MongoClient mongoClient,
                                     @Named("dev.getelements.elements.mongo.uri") final String mongoUri) {
        Objects.requireNonNull(mongoClient, "Mongo client must not be null");
        Objects.requireNonNull(mongoUri, "Mongo URI must not be null");

        final ConnectionString connectionString = new ConnectionString(mongoUri);
        final String databaseName = connectionString.getDatabase();
        if (databaseName == null || databaseName.isBlank()) {
            throw new IllegalStateException("Mongo URI must include a database name.");
        }

        this.database = mongoClient.getDatabase(databaseName);
    }

    @Override
    public void insertPurchaseEvent(OncadePurchase purchase, Instant receivedAt) {
        Objects.requireNonNull(purchase, "Purchase must not be null");

        MongoCollection<Document> collection = database.getCollection(OncadePurchase.COLLECTION_NAME);

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

        String itemId = purchase.getItemId();
        String userId = purchase.getNamazuUserId();

        if (itemId == null || userId == null) {
            logger.warn("Cannot create receipt: itemId={}, namazuUserId={}", itemId, userId);
            return;
        }

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("purchaseId", purchase.getPurchaseId());
        metadata.put("gameId", purchase.getGameId());
        metadata.put("itemType", purchase.getItemType());
        metadata.put("amount", purchase.getAmount());
        metadata.put("currency", purchase.getCurrency());
        metadata.put("userRef", purchase.getUserRef());
        metadata.put("userEmail", purchase.getUserEmail());
        metadata.put("source", "oncade");
        metadata.put("createdAt", Instant.now().toEpochMilli());

        if (purchase.getMetadata() != null) {
            metadata.put("purchaseMetadata", purchase.getMetadata());
        }

        DistinctInventoryItem receipt = distinctInventoryItemService.createDistinctInventoryItem(
                itemId,
                null,
                userId,
                metadata
        );

        logger.info("Created receipt (DistinctInventoryItem) id={} for user={} item={}",
                receipt.getId(), userId, itemId);
    }

    @Override
    public void revokeReceipt(OncadePurchase purchase) {
        Objects.requireNonNull(purchase, "Purchase must not be null");

        String itemId = purchase.getItemId();
        String userId = purchase.getNamazuUserId();

        if (itemId == null || userId == null) {
            logger.warn("Cannot revoke receipt: itemId={}, namazuUserId={}", itemId, userId);
            return;
        }

        try {
            Optional<DistinctInventoryItem> existing =
                    Optional.ofNullable(distinctInventoryItemService.getDistinctInventoryItem(purchase.getPurchaseId()));

            existing.ifPresentOrElse(
                    item -> {
                        distinctInventoryItemService.deleteInventoryItem(item.getId());
                        logger.info("Revoked receipt id={} for user={} item={}", item.getId(), userId, itemId);
                    },
                    () -> logger.warn("No receipt found to revoke for purchaseId={}", purchase.getPurchaseId())
            );
        } catch (Exception e) {
            logger.error("Failed to revoke receipt for purchaseId={}: {}", purchase.getPurchaseId(), e.getMessage());
        }
    }
}
