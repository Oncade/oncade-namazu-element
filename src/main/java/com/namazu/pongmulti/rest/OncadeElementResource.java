package com.namazu.pongmulti.rest;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import dev.getelements.elements.sdk.Element;
import dev.getelements.elements.sdk.ElementSupplier;
import dev.getelements.elements.sdk.annotation.ElementDefaultAttribute;
import dev.getelements.elements.sdk.model.user.User;
import dev.getelements.elements.sdk.service.user.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

/*
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
 */

import com.namazu.pongmulti.service.OncadeService;

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

    /*
    @GET
    @Path("/users/{userId}")
    public Response getUser(@PathParam("userId") String userId,
                            @Context HttpHeaders headers,
                            @Context UriInfo uriInfo) {
        return forward("GET", ONCADE_PREFIX + "/users/" + encodeSegment(userId), headers, uriInfo, null);
    }

    @GET
    @Path("/users/{userId}/purchases")
    public Response getUserPurchases(@PathParam("userId") String userId,
                                     @Context HttpHeaders headers,
                                     @Context UriInfo uriInfo) {
        return forward("GET", ONCADE_PREFIX + "/users/" + encodeSegment(userId) + "/purchases", headers, uriInfo, null);
    }

    @GET
    @Path("/products/{userRef}")
    public Response getCreatorProducts(@PathParam("userRef") String userRef,
                                       @Context HttpHeaders headers,
                                       @Context UriInfo uriInfo) {
        return forward("GET", ONCADE_PREFIX + "/products/" + encodeSegment(userRef), headers, uriInfo, null);
    }

    @POST
    @Path("/products/{userRef}/{productId}/submit")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response submitProduct(@PathParam("userRef") String userRef,
                                  @PathParam("productId") String productId,
                                  @Context HttpHeaders headers,
                                  @Context UriInfo uriInfo,
                                  byte[] body) {
        return forward(
                "POST",
                ONCADE_PREFIX + "/products/" + encodeSegment(userRef) + "/" + encodeSegment(productId) + "/submit",
                headers,
                uriInfo,
                body
        );
    }

    @POST
    @Path("/products/{userRef}/{productId}/review")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response reviewProduct(@PathParam("userRef") String userRef,
                                  @PathParam("productId") String productId,
                                  @Context HttpHeaders headers,
                                  @Context UriInfo uriInfo,
                                  byte[] body) {
        return forward(
                "POST",
                ONCADE_PREFIX + "/products/" + encodeSegment(userRef) + "/" + encodeSegment(productId) + "/review",
                headers,
                uriInfo,
                body
        );
    }

    @POST
    @Path("/wallet/balance")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getWalletBalance(@Context HttpHeaders headers,
                                     @Context UriInfo uriInfo,
                                     byte[] body) {
        return forward("POST", ONCADE_PREFIX + "/wallet/balance", headers, uriInfo, body);
    }

    @POST
    @Path("/wallet/purchase")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response startPurchase(@Context HttpHeaders headers,
                                  @Context UriInfo uriInfo,
                                  byte[] body) {
        return forward("POST", ONCADE_PREFIX + "/wallet/purchase", headers, uriInfo, body);
    }

    @GET
    @Path("/wallet/purchase/{purchaseId}")
    public Response getPurchase(@PathParam("purchaseId") String purchaseId,
                                @Context HttpHeaders headers,
                                @Context UriInfo uriInfo) {
        return forward("GET", ONCADE_PREFIX + "/wallet/purchase/" + encodeSegment(purchaseId), headers, uriInfo, null);
    }

    private static String encodeSegment(String value) {
        Objects.requireNonNull(value, "value");
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
    */

}
