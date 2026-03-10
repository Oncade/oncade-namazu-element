package zyx.oncade.element.service;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.function.Supplier;

public interface OncadeHttpClient {

    Response forward(String method,
                     String upstreamPath,
                     HttpHeaders incomingHeaders,
                     UriInfo uriInfo,
                     Supplier<byte[]> bodySupplier,
                     String idempotencyKey);

    void setDebug(boolean debug);
}
