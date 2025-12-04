package websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.net.URI;

public class WebSocketFacade extends Endpoint {
    private final GameHandler handler;
    private Session session;
    private final Gson gson = new Gson();


    public WebSocketFacade(String url, GameHandler handler) throws Exception {
        this.handler = handler;

        url = url.replace("http", "ws");
        URI uri = new URI(url + "/ws");

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler((MessageHandler.Whole<String>) this::onMessage);
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {}

    private void onMessage(String jsonMessage) {
        ServerMessage message = gson.fromJson(jsonMessage, ServerMessage.class);

        switch (message.getServerMessageType()) {
            case NOTIFICATION -> {
                NotificationMessage notification = gson.fromJson(jsonMessage, NotificationMessage.class);
                handler.printMessage(notification.message);
            }
            case ERROR -> {
                ErrorMessage error = gson.fromJson(jsonMessage, ErrorMessage.class);
                handler.printMessage(error.errorMessage);
            }
            case LOAD_GAME -> {
                LoadGameMessage load = gson.fromJson(jsonMessage, LoadGameMessage.class);
                handler.updateGame(load.game);
            }
        }
    }
}
