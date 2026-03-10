package com.namazu.pongmulti.model.oncade.purchase;

import java.util.Map;

public class OncadePurchase {
    
    public static final String COLLECTION_NAME = "oncade_purchase";
    
    private String purchaseId;
    private String itemId;
    private String itemType;
    private String userEmail;
    private Integer amount;
    private String currency;
    private Map<String, Object> metadata;
    private String gameId;
    private String userRef;
    private String namazuUserId;

    public OncadePurchase() {
    }
    
    public OncadePurchase(String purchaseId, String itemId, String itemType, 
                         String userEmail, Integer amount, String currency, 
                         Map<String, Object> metadata, String gameId,
                         String userRef, String namazuUserId) {
        this.purchaseId = purchaseId;
        this.itemId = itemId;
        this.itemType = itemType;
        this.userEmail = userEmail;
        this.amount = amount;
        this.currency = currency;
        this.metadata = metadata;
        this.gameId = gameId;
        this.userRef = userRef;
        this.namazuUserId = namazuUserId;
    }
    
    public String getPurchaseId() {
        return purchaseId;
    }
    
    public String getItemId() {
        return itemId;
    }
    
    public String getItemType() {
        return itemType;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    public Integer getAmount() {
        return amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public String getGameId() {
        return gameId;
    }

    public String getUserRef() {
        return userRef;
    }

    public String getNamazuUserId() {
        return namazuUserId;
    }
    
    @Override
    public String toString() {
        return "OncadePurchase{" +
                "purchaseId='" + purchaseId + '\'' +
                ", itemId='" + itemId + '\'' +
                ", gameId='" + gameId + '\'' +
                ", itemType='" + itemType + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", metadata=" + metadata +
                ", namazuUserId='" + namazuUserId + '\'' +
                ", gameId='" + gameId + '\'' +
                '}';
    }
}

