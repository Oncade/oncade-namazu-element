package com.namazu.pongmulti.service;

import dev.getelements.elements.sdk.annotation.ElementServiceExport;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.HttpHeaders;

import jakarta.ws.rs.core.UriInfo;
import java.util.function.Supplier;

@ElementServiceExport
public interface OncadeHttpClient {

    Response forward(String method, String upstreamPath, HttpHeaders incomingHeaders, UriInfo uriInfo, Supplier<byte[]> bodySupplier, String idempotencyKey);

    Response executeRequest(String method, String upstreamPath, HttpHeaders incomingHeaders, UriInfo uriInfo, Supplier<byte[]> bodySupplier, String idempotencyKey);

    void setDebug(boolean debug);
}
