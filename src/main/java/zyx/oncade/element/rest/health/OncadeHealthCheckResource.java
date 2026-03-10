package zyx.oncade.element.rest.health;

import dev.getelements.elements.sdk.Element;
import dev.getelements.elements.sdk.ElementSupplier;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import zyx.oncade.element.service.OncadeService;

@Tag(name = "Healthcheck")
@Path("/healthcheck")
public class OncadeHealthCheckResource {

    private final Element element = ElementSupplier
        .getElementLocal(OncadeHealthCheckResource.class)
        .get();

    private final OncadeService oncadeService = element
        .getServiceLocator()
        .getInstance(OncadeService.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Health check probe", description = "Returns a simple http 200.")
    public String healthCheck() {
        return "{\"success\": true, \"debug\": " + oncadeService.isDebug() + "}";
    }
}
