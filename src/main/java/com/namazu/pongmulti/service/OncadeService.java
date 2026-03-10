package com.namazu.pongmulti.service;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.Map;

import dev.getelements.elements.sdk.annotation.ElementPublic;
import dev.getelements.elements.sdk.annotation.ElementServiceExport;

// @ElementPublic
// @ElementServiceExports({@ElementServiceExport, @ElementServiceExport(
//    name = "dev.getelements.elements.service.unscoped"
// )})
@ElementPublic
@ElementServiceExport
public interface OncadeService {

    /**
     * Retrieves a request by its idempotency key.
     *
     * @param key the metadata key to look up
     * @return the stored value if present
     */
    Map<String, String> getRequest(String idempotencyKey);

    /**
     * Inserts a request into the storage.
     *
     * @param request the request to insert
     */
    void storeRequest(String idempotencyKey, HttpHeaders incomingHeaders);

    /**
     * Deletes a request from the storage.
     *
     * @param idempotencyKey the idempotency key of the request to delete
     */
    void deleteRequest(String idempotencyKey);

    /**
     * Initiates a link with the Oncade API.
     *
     * @param headers the headers of the request
     * @param uriInfo the URI info of the request
     * @param body the body of the request
     */
    abstract Response initiateLink(HttpHeaders headers, UriInfo uriInfo, byte[] body);

    /**
     * Retrieves the details of a link with the Oncade API.
     * 
     * @param headers
     * @param uriInfo
     * @return
     */
    Response getLinkDetails(HttpHeaders headers, UriInfo uriInfo);

    boolean isDebug();
}
