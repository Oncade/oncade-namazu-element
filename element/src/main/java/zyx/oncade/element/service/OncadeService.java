package zyx.oncade.element.service;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.Map;

public interface OncadeService {

    Response initiateLink(HttpHeaders headers, UriInfo uriInfo, byte[] body);

    Response getLinkDetails(HttpHeaders headers, UriInfo uriInfo);

    Map<String, String> getRequest(String idempotencyKey);

    void deleteRequest(String idempotencyKey);

    boolean isDebug();

    String getWebhookSecret();
}
