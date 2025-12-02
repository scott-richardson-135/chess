package websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private WebSocketSessions sessions = new WebSocketSessions();
    private final Gson gson = new Gson();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        //determine message type, call connect, makeMove, leaveGame, or resignGame
        System.out.println("received message");

        try {
            UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
            Session session = ctx.session;

            switch (command.getCommandType()) {
                case CONNECT -> handleConnectCommand(command, session);
                case MAKE_MOVE -> handleMakeMove(command);
                case LEAVE -> handleLeave(command, session);
                case RESIGN -> handleResign(command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void handleConnectCommand(UserGameCommand command, Session session) throws IOException {
        System.out.println("handling a connect command");
        sessions.addSessionToGame(command.getGameID(), session);
        var notification = new NotificationMessage("Player joined."); //TODO figure out how to send their name in the notif
        sessions.broadcast(command.getGameID(), notification, session);
    }

    private void handleMakeMove(UserGameCommand command) {
        System.out.println("handling a make move command");
    }

    private void handleLeave(UserGameCommand command, Session session) throws IOException {
        sessions.removeSessionFromGame(command.getGameID(), session);
        System.out.println("Player left");
        var notification = new NotificationMessage("Player left."); //TODO also figure out how to send name
        sessions.broadcast(command.getGameID(), notification, session);
    }

    private void handleResign(UserGameCommand command) {
        System.out.println("handling a resign command");
    }
}
