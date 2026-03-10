package zyx.oncade.element.rest.examples.message.actions;

import zyx.oncade.element.model.message.Message;

public class CreateMessageRequest {

    private Message message;

    public CreateMessageRequest(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return this.message;
    }
}
