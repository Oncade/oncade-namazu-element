package com.namazu.pongmulti.guice;

import com.google.inject.PrivateModule;
import com.google.inject.multibindings.Multibinder;
import com.mongodb.client.MongoClient;
import com.namazu.pongmulti.persistence.MongoProvider;
import com.namazu.pongmulti.rest.OncadeElementResource;
import com.namazu.pongmulti.rest.health.OncadeHealthCheckResource;
import com.namazu.pongmulti.rest.webhook.OncadeWebhookHandler;
import com.namazu.pongmulti.rest.webhook.OncadeWebhookResource;
import com.namazu.pongmulti.rest.webhook.handlers.OncadeWebhookHandlerRegistry;
import com.namazu.pongmulti.rest.webhook.handlers.accountlink.OncadeAccountLinkHandler;
import com.namazu.pongmulti.rest.webhook.handlers.purchase.OncadePurchaseHandler;
import com.namazu.pongmulti.service.OncadeHttpClient;
import com.namazu.pongmulti.service.OncadeHttpClientImpl;
import com.namazu.pongmulti.service.OncadeService;
import com.namazu.pongmulti.service.OncadeServiceImpl;
import com.namazu.pongmulti.service.accountLink.OncadeAccountLinkService;
import com.namazu.pongmulti.service.accountLink.OncadeAccountLinkServiceImpl;
import com.namazu.pongmulti.service.purchase.OncadePurchaseService;
import com.namazu.pongmulti.service.purchase.OncadePurchaseServiceImpl;

public class PongMultiModule extends PrivateModule {

    @Override
    protected void configure() {
        bind(MongoClient.class).toProvider(MongoProvider.class).asEagerSingleton();

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
