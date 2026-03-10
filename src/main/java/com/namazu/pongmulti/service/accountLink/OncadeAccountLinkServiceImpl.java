package com.namazu.pongmulti.service.accountLink;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.namazu.pongmulti.model.oncade.accountLink.OncadeAccountLink;

import jakarta.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.Objects;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OncadeAccountLinkServiceImpl implements OncadeAccountLinkService {


    private final Logger logger = LoggerFactory.getLogger(OncadeAccountLinkServiceImpl.class);

    private MongoCollection<Document> accountLinkCollection;

    @Inject
    public void setMongoDependencies(final MongoClient mongoClient,
                                     @Named("dev.getelements.elements.mongo.uri") final String mongoUri) {
        Objects.requireNonNull(mongoClient, "Mongo client must not be null");
        Objects.requireNonNull(mongoUri, "Mongo URI must not be null");

        final ConnectionString connectionString = new ConnectionString(mongoUri);
        final String databaseName = connectionString.getDatabase();
        if (databaseName == null || databaseName.isBlank()) {
            throw new IllegalStateException("Mongo URI must include a database name.");
        }

        MongoDatabase database = mongoClient.getDatabase(databaseName);
        this.accountLinkCollection = database.getCollection(OncadeAccountLink.COLLECTION_NAME);
    }

    private Document findDocument(String key, String value) {
        Document query = null; query = new Document(key, value);
        if (query != null) {
            Document existing = accountLinkCollection.find(query).first();
            if (existing != null) {
                return existing;
            }
        }
        return null;
    }

    @Override
    public OncadeAccountLink parseAccountLinkBodyFromJsonNode(JsonNode jsonNode) {
        if (jsonNode == null) {
            return null;
        }
        
        // Check if there's a "data" field (webhook format) or use root node directly
        JsonNode dataNode = jsonNode.has("data") ? jsonNode.get("data") : jsonNode;
        
        // Extract fields, defaulting to empty string if null or not found
        String url = extractFieldAsString(dataNode, "url");
        // Handle both "user_ref" (webhook format) and "userRef" (camelCase)
        String userRef = extractFieldAsString(dataNode, "user_ref");
        if (userRef.isEmpty()) {
            userRef = extractFieldAsString(dataNode, "userRef");
        }
        String sessionKey = extractFieldAsString(dataNode, "sessionKey");
        String namazuUserId = extractFieldAsString(dataNode, "namazuUserId");
        
        // Extract idempotencyKey from metadata if present, otherwise try lastIdempotencyKey
        String lastIdempotencyKey = "";
        if (dataNode.has("metadata") && !dataNode.get("metadata").isNull()) {
            JsonNode metadataNode = dataNode.get("metadata");
            lastIdempotencyKey = extractFieldAsString(metadataNode, "idempotencyKey");
        }
        if (lastIdempotencyKey.isEmpty()) {
            lastIdempotencyKey = extractFieldAsString(dataNode, "lastIdempotencyKey");
        }
        
        return new OncadeAccountLink(url, userRef, sessionKey, namazuUserId, lastIdempotencyKey);
    }

    @Override
    public OncadeAccountLink parseAccountLinkBodyFromResponse(Response response) {
        try {
            if (response.hasEntity()) {
                Object entity = response.getEntity();
                String bodyStr = null;
    
                if (entity instanceof String) {
                    bodyStr = (String) entity;
                } else if (entity instanceof byte[]) {
                    bodyStr = new String((byte[]) entity);
                } else if (entity != null) {
                    bodyStr = entity.toString();
                }
    
                // Attempt to parse as JSON
                if (bodyStr != null && !bodyStr.isEmpty()) {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode rootNode = mapper.readTree(bodyStr);
                        return parseAccountLinkBodyFromJsonNode(rootNode);

                    } catch (JsonProcessingException e) {
                        logger.debug("Response body is not valid JSON: {}", e.getMessage());
                    }
                }
            }
            return null;
        } catch (Exception e) {
            logger.info("Error parsing account link body: {}", e.getMessage());
            return null;
        }
    }
    
    private String extractFieldAsString(JsonNode node, String fieldName) {
        if (node == null || !node.has(fieldName)) {
            return "";
        }
        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode == null || fieldNode.isNull()) {
            return "";
        }
        return fieldNode.asText("");
    }

    @Override
    public void insertAccountLinkEvent(OncadeAccountLink accountLink) {
        Objects.requireNonNull(accountLink, "Account link must not be null");

        // Check for existing document by order: sessionKey, then lastIdempotencyKey, then userRef
        
        Document loadedDocument = null;

        if (accountLink.getSessionKey() != null) {
            loadedDocument = findDocument("sessionKey", accountLink.getSessionKey());
        }

        if (loadedDocument == null && accountLink.getLastIdempotencyKey() != null) {
            loadedDocument = findDocument("lastIdempotencyKey", accountLink.getLastIdempotencyKey());
        }

        if (loadedDocument == null && accountLink.getUserRef() != null) {
            loadedDocument = findDocument("userRef", accountLink.getUserRef());
        }

        Document newDocument = new Document();
        
        if (accountLink.getUrl() != null) {
            newDocument.append("url", accountLink.getUrl());
        }

        if (accountLink.getUserRef() != null) {
            newDocument.append("userRef", accountLink.getUserRef());
        }
        
        if (accountLink.getSessionKey() != null) {
            newDocument.append("sessionKey", accountLink.getSessionKey());
        }

        if (accountLink.getNamazuUserId() != null) {
            newDocument.append("namazuUserId", accountLink.getNamazuUserId());
        }

        if (accountLink.getLastIdempotencyKey() != null) {
            newDocument.append("lastIdempotencyKey", accountLink.getLastIdempotencyKey());
        }

        if (loadedDocument == null) {
            accountLinkCollection.insertOne(newDocument);

        } else {

            Document filter = new Document("_id", loadedDocument.get("_id"));
            Document update = new Document("$set", newDocument);
            accountLinkCollection.updateOne(filter, update);
        }
    }
}
