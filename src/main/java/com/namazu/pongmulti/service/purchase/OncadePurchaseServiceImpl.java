package com.namazu.pongmulti.service.purchase;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.namazu.pongmulti.model.oncade.purchase.OncadePurchase;


import java.time.Instant;
import java.util.Objects;
import org.bson.Document;

public class OncadePurchaseServiceImpl implements OncadePurchaseService {

    private MongoDatabase database;

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
}
