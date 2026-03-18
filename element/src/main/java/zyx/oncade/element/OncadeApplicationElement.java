package zyx.oncade.element;

import dev.getelements.elements.sdk.annotation.ElementDefaultAttribute;
import dev.getelements.elements.sdk.annotation.ElementServiceExport;
import dev.getelements.elements.sdk.annotation.ElementServiceImplementation;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import zyx.oncade.element.rest.OncadeElementResource;
import zyx.oncade.element.rest.examples.content.ExampleResource;
import zyx.oncade.element.rest.examples.message.MessageResource;
import zyx.oncade.element.rest.health.OncadeHealthCheckResource;
import zyx.oncade.element.rest.webhook.OncadeWebhookResource;

import java.util.Set;

@ApplicationPath("/")
@ElementServiceImplementation
@ElementServiceExport(Application.class)
public class OncadeApplicationElement extends Application {

    @ElementDefaultAttribute("oncade")
    public static final String APPLICATION_PREFIX = "dev.getelements.elements.app.serve.prefix";

    @ElementDefaultAttribute("false")
    public static final String ONCADE_DEBUG = "xyz.oncade.http.client.debug";

    @ElementDefaultAttribute("http://localhost:3000/api/v1")
    public static final String ONCADE_CLIENT_URL = "xyz.oncade.http.client.url";

    @ElementDefaultAttribute(value = "30000")
    public static final String ONCADE_CLIENT_TIMEOUT = "xyz.oncade.http.client.timeout";

    @ElementDefaultAttribute(value = "changeme", sensitive = true)
    public static final String ONCADE_API_KEY = "xyz.oncade.http.client.apiKey";

    @ElementDefaultAttribute(value = "changeme", sensitive = true)
    public static final String ONCADE_WEBHOOK_SECRET = "xyz.oncade.http.webhook.secret";

    public static final String OPENAPI_TAG = "Oncade";

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
                CorsFilter.class,
                OncadeHealthCheckResource.class,
                OncadeWebhookResource.class,
                OncadeElementResource.class,
                MessageResource.class,
                ExampleResource.class
        );
    }

}
