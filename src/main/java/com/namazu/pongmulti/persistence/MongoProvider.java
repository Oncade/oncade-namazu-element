package com.namazu.pongmulti.persistence;

import jakarta.inject.Inject;
import com.google.inject.Provider;
import jakarta.inject.Named;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MongoProvider implements Provider<MongoClient> {

    public static final String ACCOUNTLINK_COLLECTION = "oncade_account_link";
    public static final String PURCHASE_COLLECTION = "oncade_purchase";
    public static final String REQUESTS_COLLECTION = "oncade_requests";

    private final Logger logger = LoggerFactory.getLogger(MongoProvider.class);

    private String mongoDbUri;

    private MongoClient client;
    private ConnectionString connectionString;
    private String databaseName;

    @Override
    public MongoClient get() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    initializeClient();
                }
            }
        }
        return client;
    }

    private void initializeClient() {
        connectionString = new ConnectionString(mongoDbUri);
        databaseName = connectionString.getDatabase();

        final var settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        client = MongoClients.create(settings);
        
        ensureMetadataCollectionExists(client, connectionString);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                client.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }));

        logger.info("MongoProvider initialized successfully for Example Element");
        logger.info("Connected to MongoDB database: {}", databaseName);
        logger.info("Collections: '{}', '{}', '{}' are ready", ACCOUNTLINK_COLLECTION, PURCHASE_COLLECTION, REQUESTS_COLLECTION);
    }

    private void ensureMetadataCollectionExists(final MongoClient client, final ConnectionString connectionString) {
        final String dbName = connectionString.getDatabase();
        if (dbName == null || dbName.isBlank()) {
            throw new IllegalStateException("Mongo connection string must include a database name to create collections.");
        }

        final MongoDatabase database = client.getDatabase(dbName);
        final MongoIterable<String> collectionNames = database.listCollectionNames();
        // Get the list of existing collection names into a set for easy lookup
        final Set<String> existingCollections = new HashSet<String>();
        for (final String existingName : collectionNames) {
            logger.debug("Found collection: {}", existingName);
            existingCollections.add(existingName);
        }

        // Check for each required collection, create if not present
        if (!existingCollections.contains(ACCOUNTLINK_COLLECTION)) {
            database.createCollection(ACCOUNTLINK_COLLECTION);
            logger.info("Created collection '{}'", ACCOUNTLINK_COLLECTION);
        } else {
            logger.info("Account link collection '{}' already exists", ACCOUNTLINK_COLLECTION);
        }

        if (!existingCollections.contains(PURCHASE_COLLECTION)) {
            database.createCollection(PURCHASE_COLLECTION);
            logger.info("Created collection '{}'", PURCHASE_COLLECTION);
        } else {
            logger.info("Purchase collection '{}' already exists", PURCHASE_COLLECTION);
        }

        if (!existingCollections.contains(REQUESTS_COLLECTION)) {
            database.createCollection(REQUESTS_COLLECTION);
            logger.info("Created collection '{}'", REQUESTS_COLLECTION);
        } else {
            logger.info("Requests collection '{}' already exists", REQUESTS_COLLECTION);
        }
    }

    @Inject
    public void setMongoDbUri(@Named("dev.getelements.elements.mongo.uri") final String mongoDbUri) {
        this.mongoDbUri = mongoDbUri;
    }

}