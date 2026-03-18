package zyx.oncade.element.persistence;

import com.google.inject.Provider;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Guice {@link Provider} that obtains the {@link MongoDatabase} and ensures all required
 * Oncade collections exist before returning it.
 *
 * <p>On first provision the provider connects to the database named by the
 * {@code dev.getelements.elements.mongo.database.name} binding and idempotently creates any
 * collections defined in {@link MongoSchemaConstants} that are not yet present.
 */
public class MongoDatabaseProvider implements Provider<MongoDatabase> {

    // TODO: In the current 3.7 build there is a problem where the Elements do not automatically inherit the
    // TODO: system configuration. This will need to be addressed in 3.8 and a bugfix for 3.7 For now we will hardcode.
    private static final String ELEMENTS_DATABASE_NAME = "elements";

    private static final Logger logger = LoggerFactory.getLogger(MongoDatabaseProvider.class);

    private String mongoDatabaseName;

    private Provider<MongoClient> mongoClientProvider;

    /**
     * Returns the {@link MongoDatabase}, creating any missing collections as a side-effect.
     *
     * @return the configured {@link MongoDatabase}
     */
    @Override
    public MongoDatabase get() {

        final var client = getMongoClientProvider().get();
        final var database = client.getDatabase(ELEMENTS_DATABASE_NAME);

        final MongoIterable<String> collectionNames = database.listCollectionNames();

        // TODO: There should probably be indexes in here as well.

        // Get the list of existing collection names into a set for easy lookup

        final Set<String> existingCollections = new HashSet<>();
        for (final String existingName : collectionNames) {
            logger.debug("Found collection: {}", existingName);
            existingCollections.add(existingName);
        }

        // Check for each required collection, create if not present
        if (!existingCollections.contains(MongoSchemaConstants.ACCOUNTLINK_COLLECTION)) {
            database.createCollection(MongoSchemaConstants.ACCOUNTLINK_COLLECTION);
            logger.info("Created collection '{}'", MongoSchemaConstants.ACCOUNTLINK_COLLECTION);
        } else {
            logger.info("Account link collection '{}' already exists", MongoSchemaConstants.ACCOUNTLINK_COLLECTION);
        }

        if (!existingCollections.contains(MongoSchemaConstants.PURCHASE_COLLECTION)) {
            database.createCollection(MongoSchemaConstants.PURCHASE_COLLECTION);
            logger.info("Created collection '{}'", MongoSchemaConstants.PURCHASE_COLLECTION);
        } else {
            logger.info("Purchase collection '{}' already exists", MongoSchemaConstants.PURCHASE_COLLECTION);
        }

        if (!existingCollections.contains(MongoSchemaConstants.REQUESTS_COLLECTION)) {
            database.createCollection(MongoSchemaConstants.REQUESTS_COLLECTION);
            logger.info("Created collection '{}'", MongoSchemaConstants.REQUESTS_COLLECTION);
        } else {
            logger.info("Requests collection '{}' already exists", MongoSchemaConstants.REQUESTS_COLLECTION);
        }

        return database;

    }

    /**
     * Returns the provider for {@link MongoClient}.
     *
     * @return the {@link MongoClient} provider
     */
    public Provider<MongoClient> getMongoClientProvider() {
        return mongoClientProvider;
    }

    /**
     * Sets the provider for {@link MongoClient}.
     *
     * @param mongoClientProvider the provider to inject
     */
    @Inject
    public void setMongoClientProvider(Provider<MongoClient> mongoClientProvider) {
        this.mongoClientProvider = mongoClientProvider;
    }

}


