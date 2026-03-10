package zyx.oncade.element.websockets;

import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint("/wss")
public class OncadeWebsocketServer {

    private static final Logger logger = LoggerFactory.getLogger(OncadeWebsocketServer.class);

    @OnOpen
    public void onOpen(final Session session) {
        logger.info("Opened {}", session.getId());
    }

    @OnMessage
    public String onMessage(final Session session, final String message) {
        logger.info("Received {}. Echoing.", message);
        return message;
    }

    @OnClose
    public void onClose(final Session session, final CloseReason closeReason) {
        logger.info("Closed {} - {}", session.getId(), closeReason);
    }
}
