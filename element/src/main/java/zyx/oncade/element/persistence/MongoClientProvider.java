package zyx.oncade.element.persistence;

import com.google.inject.Provider;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.SslSettings;
import dev.getelements.elements.sdk.mongo.MongoConfigurationService;
import dev.getelements.elements.sdk.mongo.MongoSslConfiguration;
import dev.getelements.elements.sdk.util.ShutdownHooks;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Guice {@link Provider} that constructs and configures a {@link MongoClient} for the Oncade element.
 *
 * <p>On first provision the client is built from the connection string and optional SSL settings
 * obtained via {@link MongoConfigurationService}. A shutdown hook is registered so the client is
 * closed cleanly when the JVM exits.
 */
public class MongoClientProvider implements Provider<MongoClient> {

    private static final ShutdownHooks shutdownHooks = new ShutdownHooks(MongoDatabaseProvider.class);

    private final Logger logger = LoggerFactory.getLogger(MongoClientProvider.class);

    private Provider<MongoConfigurationService> mongoConfigurationService;

    /**
     * Creates and returns a configured {@link MongoClient}.
     *
     * @return a fully initialised {@link MongoClient}
     */
    @Override
    public MongoClient get() {

        final var configuration = getMongoConfigurationService()
                .get()
                .getMongoConfiguration();

        final var connectionString = new ConnectionString(configuration.connectionString());

        final var sslSettings = configuration.findSslConfiguration()
                        .map(MongoSslConfiguration::newSslContext)
                        .map(sslContext -> SslSettings.builder()
                                .context(sslContext)
                                .applyConnectionString(connectionString)
                                .build()
                        )
                        .orElseGet(() -> SslSettings.builder().enabled(false).build());

        final var mongoClientSettings = MongoClientSettings.builder()
                        .applyConnectionString(connectionString)
                        .applyToSslSettings(builder -> builder.applySettings(sslSettings))
                        .build();

        final var client = MongoClients.create(mongoClientSettings);
        shutdownHooks.add(client::close);
        logger.info("MongoProvider initialized successfully for OnCade");

        return client;

    }

    /**
     * Returns the provider for {@link MongoConfigurationService}.
     *
     * @return the {@link MongoConfigurationService} provider
     */
    public Provider<MongoConfigurationService> getMongoConfigurationService() {
        return mongoConfigurationService;
    }

    /**
     * Sets the provider for {@link MongoConfigurationService}.
     *
     * @param mongoConfigurationService the provider to inject
     */
    @Inject
    public void setMongoConfigurationService(Provider<MongoConfigurationService> mongoConfigurationService) {
        this.mongoConfigurationService = mongoConfigurationService;
    }


}
