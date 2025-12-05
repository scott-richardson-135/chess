package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
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

        System.out.println("Raw message: " + jsonMessage);
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> {
                NotificationMessage notification = gson.fromJson(jsonMessage, NotificationMessage.class);
                handler.printMessage(notification.getMessage());
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


    //outgoing messages
    public void connect(String auth, int gameId) throws IOException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, auth, gameId);
        send(command);
    }

    public void makeMove(String auth, int gameId, ChessMove move) throws IOException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, auth, gameId);
        command.setMove(move);
        send(command);
    }

    public void leave(String auth, int gameId) throws IOException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, auth, gameId);
        send(command);
    }

    public void resign(String auth, int gameId) throws IOException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, auth, gameId);
        send(command);
    }


    private void send(UserGameCommand command) throws IOException {
        session.getBasicRemote().sendText(gson.toJson(command));
    }
}
