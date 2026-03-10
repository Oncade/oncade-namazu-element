package com.namazu.pongmulti.service.purchase;

import dev.getelements.elements.sdk.annotation.ElementServiceExport;

import java.time.Instant;

import com.namazu.pongmulti.model.oncade.purchase.OncadePurchase;

@ElementServiceExport
public interface OncadePurchaseService {
    /**
     * Inserts a purchase event into the MongoDB collection.
     *
     * @param purchase the purchase event to insert
     */
    void insertPurchaseEvent(OncadePurchase purchase, Instant receivedAt);
}
