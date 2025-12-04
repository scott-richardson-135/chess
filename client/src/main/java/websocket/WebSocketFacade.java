package websocket;

import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Session;

public class WebSocketFacade extends Endpoint {

    public WebSocketFacade(String url, GameHandler handler) {

    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {}
}
