package com.namazu.pongmulti.service.accountLink;

import dev.getelements.elements.sdk.annotation.ElementServiceExport;

import com.fasterxml.jackson.databind.JsonNode;
import com.namazu.pongmulti.model.oncade.accountLink.OncadeAccountLink;

import jakarta.ws.rs.core.Response;

@ElementServiceExport
public interface OncadeAccountLinkService {
    /**
     * Inserts an account link event into the MongoDB collection.
     *
     * @param accountLink the account link event to insert
     */
    void insertAccountLinkEvent(OncadeAccountLink accountLink);

    OncadeAccountLink parseAccountLinkBodyFromResponse(Response response);

    OncadeAccountLink parseAccountLinkBodyFromJsonNode(JsonNode jsonNode);
}
