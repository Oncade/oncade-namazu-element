package zyx.oncade.element.service;

import com.google.inject.name.Named;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import dev.getelements.elements.sdk.model.user.User;
import dev.getelements.elements.sdk.service.user.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zyx.oncade.element.model.oncade.accountLink.OncadeAccountLink;
import zyx.oncade.element.service.accountLink.OncadeAccountLinkService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

import static zyx.oncade.element.persistence.MongoSchemaConstants.REQUESTS_COLLECTION;

public class OncadeServiceImpl implements OncadeService {

    private final Logger logger = LoggerFactory.getLogger(OncadeServiceImpl.class);

    private MongoDatabase mongoDatabase;

    private MongoCollection<Document> requestsCollection;

    private OncadeHttpClient httpClient;

    private UserService userService;

    private OncadeAccountLinkService accountLinkService;

    @Inject
    public void setAccountLinkService(final OncadeAccountLinkService accountLinkService) {
        this.accountLinkService = accountLinkService;
    }

    private boolean debug;

    private String webhookSecret;

    @Inject
    public void setProperties(@Named("xyz.oncade.http.client.debug") String debugStr){
        this.debug = "true".equalsIgnoreCase(debugStr);
        httpClient.setDebug(this.debug);
    }

    @Inject
    public void setWebhookSecret(@Named("xyz.oncade.http.webhook.secret") String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    public boolean isDebug() {
        return debug;
    }

    @Override
    public String getWebhookSecret() {
        return webhookSecret;
    }

    public UserService getUserService() {
        return userService;
    }

    @Inject
    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public OncadeHttpClient getHttpClient() {
        return httpClient;
    }

    @Inject
    public void setHttpClient(final OncadeHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Inject
    public void setMongoDatabase(final MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
        this.requestsCollection = mongoDatabase.getCollection(REQUESTS_COLLECTION);
    }

    @Override
    public Response initiateLink(HttpHeaders headers, UriInfo uriInfo, byte[] body) {
        Objects.requireNonNull(headers, "Headers must not be null");
        Objects.requireNonNull(uriInfo, "URI info must not be null");
        Objects.requireNonNull(body, "Body must not be null");

        var currentUser = getCurrentUserAndCheckAuthentication(headers);
        if (currentUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        ForwardResult forwardResult = forward("POST", "/users/link/initiate", headers, uriInfo, body);
        Response response = forwardResult.response;
        String idempotencyKey = forwardResult.idempotencyKey;
        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            try {
                OncadeAccountLink accountLink = accountLinkService.parseAccountLinkBodyFromResponse(response);
                logger.info("Account link: {}", accountLink);
                accountLink.setNamazuUserId(currentUser.getId());
                accountLink.setLastIdempotencyKey(idempotencyKey);

                if (accountLink != null) {
                    accountLinkService.insertAccountLinkEvent(accountLink);
                }

            } catch (Exception e) {
                logger.info("Failed to parse body or insert account link event: {}", e.getMessage());
            }
        }
        return response;
    }

    @Override
    public Response getLinkDetails(HttpHeaders headers, UriInfo uriInfo) {
        Objects.requireNonNull(headers, "Headers must not be null");
        Objects.requireNonNull(uriInfo, "URI info must not be null");

        var currentUser = getCurrentUserAndCheckAuthentication(headers);
        if (currentUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        ForwardResult forwardResult = forward("GET", "/users/link/details", headers, uriInfo, null);
        return forwardResult.response;
    }

    private User getCurrentUserAndCheckAuthentication(HttpHeaders headers) {
        final User currentUser = userService.getCurrentUser();
        final boolean isLoggedIn = !User.Level.UNPRIVILEGED.equals(currentUser.getLevel());
        if (!isLoggedIn) {
            return null;
        }
        return currentUser;
    }

    private ForwardResult forward(String method,
                                  String upstreamPath,
                                  HttpHeaders headers,
                                  UriInfo uriInfo,
                                  byte[] body) {

        // Add idempotency-key header for POST and PUT requests
        String idempotencyKey = null;
        boolean insertRequest = false;
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
            idempotencyKey = generateUUID();
            insertRequest = true;
        }

        Supplier<byte[]> supplier = body == null ? null : () -> body;
        Response response = httpClient.forward(method, upstreamPath, headers, uriInfo, supplier, idempotencyKey);

        if (insertRequest) {
            storeRequest(idempotencyKey, headers);
        }

        return new ForwardResult(response, idempotencyKey);
    }

    public void storeRequest(String idempotencyKey, HttpHeaders incomingHeaders) {
        try {
            String namazuUserId = null;
            if (userService != null) {
                User currentUser = userService.getCurrentUser();
                if (currentUser != null) {
                    namazuUserId = currentUser.getId();
                }
            }

            String gameId = null;
            if (incomingHeaders != null) {
                gameId = incomingHeaders.getHeaderString("X-Game-Id");
            }

            Document document = new Document();
            document.put("idempotencyKey", idempotencyKey);
            if (namazuUserId != null) {
                document.put("namazuUserId", namazuUserId);
            }
            if (gameId != null && !gameId.trim().isEmpty()) {
                document.put("gameId", gameId);
            }
            requestsCollection.insertOne(document);
        } catch (Exception e) {
            logger.debug("Failed to insert request with idempotency key {}: {}", idempotencyKey, e.getMessage());
        }
    }

    @Override
    public Map<String, String> getRequest(final String idempotencyKey) {
        Objects.requireNonNull(idempotencyKey, "Idempotency key must not be null");

        Document document = requestsCollection.find(Filters.eq("idempotencyKey", idempotencyKey)).first();
        if (document == null) {
            return null;
        }

        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            result.put(entry.getKey(), entry.getValue() != null ? entry.getValue().toString() : null);
        }
        return result;
    }

    @Override
    public void deleteRequest(String idempotencyKey) {
        Objects.requireNonNull(idempotencyKey, "Idempotency key must not be null");

        requestsCollection.deleteOne(Filters.eq("idempotencyKey", idempotencyKey));
    }

    private static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private static class ForwardResult {

        final Response response;
        final String idempotencyKey;

        ForwardResult(Response response, String idempotencyKey) {
            this.response = response;
            this.idempotencyKey = idempotencyKey;
        }

    }

}
