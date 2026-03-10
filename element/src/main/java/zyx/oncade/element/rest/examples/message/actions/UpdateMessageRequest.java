package zyx.oncade.element.rest.examples.message.actions;

import zyx.oncade.element.model.message.Message;

public class UpdateMessageRequest {

    private Message message;

    public UpdateMessageRequest(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return this.message;
    }

}
