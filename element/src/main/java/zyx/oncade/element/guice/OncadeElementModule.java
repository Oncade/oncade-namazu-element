package zyx.oncade.element.guice;

import com.google.inject.PrivateModule;
import com.google.inject.multibindings.Multibinder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import zyx.oncade.element.persistence.MongoClientProvider;
import zyx.oncade.element.persistence.MongoDatabaseProvider;
import zyx.oncade.element.rest.OncadeElementResource;
import zyx.oncade.element.rest.health.OncadeHealthCheckResource;
import zyx.oncade.element.rest.webhook.OncadeWebhookHandler;
import zyx.oncade.element.rest.webhook.OncadeWebhookResource;
import zyx.oncade.element.rest.webhook.handlers.OncadeWebhookHandlerRegistry;
import zyx.oncade.element.rest.webhook.handlers.accountlink.OncadeAccountLinkHandler;
import zyx.oncade.element.rest.webhook.handlers.purchase.OncadePurchaseHandler;
import zyx.oncade.element.service.OncadeHttpClient;
import zyx.oncade.element.service.OncadeHttpClientImpl;
import zyx.oncade.element.service.OncadeService;
import zyx.oncade.element.service.OncadeServiceImpl;
import zyx.oncade.element.service.accountLink.OncadeAccountLinkService;
import zyx.oncade.element.service.accountLink.OncadeAccountLinkServiceImpl;
import zyx.oncade.element.service.purchase.OncadePurchaseService;
import zyx.oncade.element.service.purchase.OncadePurchaseServiceImpl;

public class OncadeElementModule extends PrivateModule {

    @Override
    protected void configure() {

        bind(MongoClient.class).toProvider(MongoClientProvider.class).asEagerSingleton();
        bind(MongoDatabase.class).toProvider(MongoDatabaseProvider.class).asEagerSingleton();

        bind(OncadeAccountLinkService.class).to(OncadeAccountLinkServiceImpl.class);
        expose(OncadeAccountLinkService.class);

        bind(OncadePurchaseService.class).to(OncadePurchaseServiceImpl.class);
        expose(OncadePurchaseService.class);

        bind(OncadeHttpClient.class).to(OncadeHttpClientImpl.class).asEagerSingleton();
        expose(OncadeHttpClient.class);

        bind(OncadeElementResource.class);
        expose(OncadeElementResource.class);

        bind(OncadeWebhookResource.class);
        expose(OncadeWebhookResource.class);

        bind(OncadeHealthCheckResource.class);
        expose(OncadeHealthCheckResource.class);

        // Bind webhook handlers using Multibinder
        @SuppressWarnings("rawtypes")
        Multibinder<OncadeWebhookHandler> handlerBinder = Multibinder.newSetBinder(binder(), OncadeWebhookHandler.class);
        handlerBinder.addBinding().to(OncadeAccountLinkHandler.class);
        handlerBinder.addBinding().to(OncadePurchaseHandler.class);

        // Bind webhook handler registry
        bind(OncadeWebhookHandlerRegistry.class);
        expose(OncadeWebhookHandlerRegistry.class);

        bind(OncadeService.class).to(OncadeServiceImpl.class);
        expose(OncadeService.class);
    }
}
