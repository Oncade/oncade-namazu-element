package com.namazu.pongmulti.rest.examples.message.actions;

import com.namazu.pongmulti.model.message.Message;

public class CreateMessageRequest {
    
    private Message message;

    public CreateMessageRequest(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return this.message;
    }
}
