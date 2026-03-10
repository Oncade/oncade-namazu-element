package zyx.oncade.element;

import zyx.oncade.element.rest.OncadeElementResource;
import zyx.oncade.element.rest.examples.content.ExampleResource;
import zyx.oncade.element.rest.examples.message.MessageResource;
import zyx.oncade.element.rest.health.OncadeHealthCheckResource;
import zyx.oncade.element.rest.webhook.OncadeWebhookResource;

import dev.getelements.elements.sdk.annotation.ElementDefaultAttribute;
import dev.getelements.elements.sdk.annotation.ElementServiceExport;
import dev.getelements.elements.sdk.annotation.ElementServiceImplementation;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.Set;

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
}
