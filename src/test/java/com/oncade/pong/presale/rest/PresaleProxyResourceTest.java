package com.oncade.pong.presale.rest;

import com.oncade.pong.presale.PresaleHttpClient;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PresaleProxyResourceTest {

    @Test
    void encodesPathSegmentsForUserEndpoint() {
        CapturingClient client = new CapturingClient();
        PresaleProxyResource resource = new PresaleProxyResource(client);

        Response response = resource.getUser("user/with/slash", null, null);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("/api/v1/users/user%2Fwith%2Fslash", client.upstreamPath);
        assertEquals("GET", client.method);
    }

    @Test
    void forwardsBodyForPurchaseRequest() {
        CapturingClient client = new CapturingClient();
        PresaleProxyResource resource = new PresaleProxyResource(client);
        byte[] payload = "{\"itemId\":\"123\"}".getBytes(StandardCharsets.UTF_8);

        Response response = resource.startPurchase(null, null, payload);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("/api/v1/wallet/purchase", client.upstreamPath);
        assertEquals("POST", client.method);
        assertArrayEquals(payload, client.body);
    }

    private static final class CapturingClient extends PresaleHttpClient {
        private String method;
        private String upstreamPath;
        private byte[] body;

        @Override
        public Response forward(String method, String upstreamPath, HttpHeaders incomingHeaders, UriInfo uriInfo, Supplier<byte[]> bodySupplier) {
            this.method = method;
            this.upstreamPath = upstreamPath;
            this.body = bodySupplier == null ? null : bodySupplier.get();
            return Response.ok().build();
        }
    }
}
