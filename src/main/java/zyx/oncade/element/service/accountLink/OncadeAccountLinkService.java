package zyx.oncade.element.service.accountLink;

import com.fasterxml.jackson.databind.JsonNode;
import dev.getelements.elements.sdk.annotation.ElementServiceExport;
import jakarta.ws.rs.core.Response;
import zyx.oncade.element.model.oncade.accountLink.OncadeAccountLink;

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
