package zyx.oncade.element.rest;

import dev.getelements.elements.sdk.Element;
import dev.getelements.elements.sdk.ElementSupplier;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import zyx.oncade.element.service.OncadeService;

@Tag(name = "OncadeProxyResource")
@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
public class OncadeElementResource {

    private final Element element = ElementSupplier
            .getElementLocal(OncadeElementResource.class)
            .get();

    private final OncadeService oncadeService = element
            .getServiceLocator()
            .getInstance(OncadeService.class);

    @POST
    @Path("/users/link/initiate")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response initiateLink(@Context HttpHeaders headers,
                                 @Context UriInfo uriInfo,
                                 byte[] body) {

        return oncadeService.initiateLink(headers, uriInfo, body);
    }

    @GET
    @Path("/users/link/details")
    public Response getLinkDetails(@Context HttpHeaders headers,
                                   @Context UriInfo uriInfo) {
        return oncadeService.getLinkDetails(headers, uriInfo);
    }

}
