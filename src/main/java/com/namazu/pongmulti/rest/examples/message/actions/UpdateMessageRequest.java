package com.namazu.pongmulti.rest.examples.message.actions;

import com.namazu.pongmulti.model.message.Message;

public class UpdateMessageRequest {

    private Message message;

    public UpdateMessageRequest(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return this.message;
    }

}
