package com.namazu.pongmulti;

import com.namazu.pongmulti.rest.OncadeElementResource;
import com.namazu.pongmulti.rest.examples.content.ExampleResource;
import com.namazu.pongmulti.rest.examples.message.MessageResource;
import com.namazu.pongmulti.rest.health.OncadeHealthCheckResource;
import com.namazu.pongmulti.rest.webhook.OncadeWebhookResource;

import dev.getelements.elements.sdk.annotation.ElementDefaultAttribute;
import dev.getelements.elements.sdk.annotation.ElementServiceExport;
import dev.getelements.elements.sdk.annotation.ElementServiceImplementation;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.glassfish.jersey.server.ServerProperties;

@ApplicationPath("/")
@ElementServiceImplementation
@ElementServiceExport(Application.class)
public class OncadeApplicationElement extends Application {

    @ElementDefaultAttribute("oncade")
    public static final String APPLICATION_PREFIX = "dev.getelements.elements.app.serve.prefix";

    public static final String OPENAPI_TAG = "Oncade";

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
                CorsFilter.class,
                OncadeHealthCheckResource.class,
                OncadeWebhookResource.class,
                OncadeElementResource.class,
                MessageResource.class,
                ExampleResource.class,
                OpenApiResource.class
        );
    }

    @Override
    public Map<String,Object> getProperties() {
        final Map<String,Object> props = new HashMap<>();
        //We want to use Jackson for our JSON serialization (since it can handle the
        // Map<String, Object> type that we use for our example model metadata), so
        // we need to disable MOXy
        props.put(ServerProperties.MOXY_JSON_FEATURE_DISABLE, true);
        return props;
    }
}
