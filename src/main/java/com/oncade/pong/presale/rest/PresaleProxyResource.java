package com.oncade.pong.presale.rest;

import com.oncade.pong.presale.PresaleHttpClient;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Supplier;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class PresaleProxyResource {

    private static final String PRESALE_PREFIX = "/api/v1";

    private final PresaleHttpClient client;

    public PresaleProxyResource() {
        this(new PresaleHttpClient());
    }

    PresaleProxyResource(PresaleHttpClient client) {
        this.client = client;
    }

    @POST
    @Path("/v1/users/link/initiate")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response initiateLink(@Context HttpHeaders headers,
                                 @Context UriInfo uriInfo,
                                 byte[] body) {
        return forward("POST", PRESALE_PREFIX + "/users/link/initiate", headers, uriInfo, body);
    }

    @GET
    @Path("/v1/users/link/details")
    public Response getLinkDetails(@Context HttpHeaders headers,
                                   @Context UriInfo uriInfo) {
        return forward("GET", PRESALE_PREFIX + "/users/link/details", headers, uriInfo, null);
    }

    @GET
    @Path("/v1/users/{userId}")
    public Response getUser(@PathParam("userId") String userId,
                            @Context HttpHeaders headers,
                            @Context UriInfo uriInfo) {
        return forward("GET", PRESALE_PREFIX + "/users/" + encodeSegment(userId), headers, uriInfo, null);
    }

    @GET
    @Path("/v1/users/{userId}/purchases")
    public Response getUserPurchases(@PathParam("userId") String userId,
                                     @Context HttpHeaders headers,
                                     @Context UriInfo uriInfo) {
        return forward("GET", PRESALE_PREFIX + "/users/" + encodeSegment(userId) + "/purchases", headers, uriInfo, null);
    }

    @POST
    @Path("/v1/products")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createProduct(@Context HttpHeaders headers,
                                  @Context UriInfo uriInfo,
                                  byte[] body) {
        return forward("POST", PRESALE_PREFIX + "/products", headers, uriInfo, body);
    }

    @GET
    @Path("/v1/products/{userRef}")
    public Response getCreatorProducts(@PathParam("userRef") String userRef,
                                       @Context HttpHeaders headers,
                                       @Context UriInfo uriInfo) {
        return forward("GET", PRESALE_PREFIX + "/products/" + encodeSegment(userRef), headers, uriInfo, null);
    }

    @POST
    @Path("/v1/products/{userRef}/{productId}/submit")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response submitProduct(@PathParam("userRef") String userRef,
                                  @PathParam("productId") String productId,
                                  @Context HttpHeaders headers,
                                  @Context UriInfo uriInfo,
                                  byte[] body) {
        return forward(
                "POST",
                PRESALE_PREFIX + "/products/" + encodeSegment(userRef) + "/" + encodeSegment(productId) + "/submit",
                headers,
                uriInfo,
                body
        );
    }

    @POST
    @Path("/v1/products/{userRef}/{productId}/review")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response reviewProduct(@PathParam("userRef") String userRef,
                                  @PathParam("productId") String productId,
                                  @Context HttpHeaders headers,
                                  @Context UriInfo uriInfo,
                                  byte[] body) {
        return forward(
                "POST",
                PRESALE_PREFIX + "/products/" + encodeSegment(userRef) + "/" + encodeSegment(productId) + "/review",
                headers,
                uriInfo,
                body
        );
    }

    @POST
    @Path("/v1/wallet/balance")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getWalletBalance(@Context HttpHeaders headers,
                                     @Context UriInfo uriInfo,
                                     byte[] body) {
        return forward("POST", PRESALE_PREFIX + "/wallet/balance", headers, uriInfo, body);
    }

    @POST
    @Path("/v1/wallet/purchase")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response startPurchase(@Context HttpHeaders headers,
                                  @Context UriInfo uriInfo,
                                  byte[] body) {
        return forward("POST", PRESALE_PREFIX + "/wallet/purchase", headers, uriInfo, body);
    }

    @GET
    @Path("/v1/wallet/purchase/{purchaseId}")
    public Response getPurchase(@PathParam("purchaseId") String purchaseId,
                                @Context HttpHeaders headers,
                                @Context UriInfo uriInfo) {
        return forward("GET", PRESALE_PREFIX + "/wallet/purchase/" + encodeSegment(purchaseId), headers, uriInfo, null);
    }

    private Response forward(String method,
                             String upstreamPath,
                             HttpHeaders headers,
                             UriInfo uriInfo,
                             byte[] body) {
        Supplier<byte[]> supplier = body == null ? null : () -> body;
        return client.forward(method, upstreamPath, headers, uriInfo, supplier);
    }

    private static String encodeSegment(String value) {
        Objects.requireNonNull(value, "value");
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
