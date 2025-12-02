package websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private WebSocketSessions sessions = new WebSocketSessions();
    private final Gson gson = new Gson();
    private final GameService gameService = new GameService();
    private final UserService userService = new UserService();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        //determine message type, call connect, makeMove, leaveGame, or resignGame

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
        try {
            var username = userService.getUsernameFromToken(command.getAuthToken());

            //get game and check if it is real
            GameData game = gameService.getGame(command.getGameID());
            if (game == null) {
                sendError(session, "Error: game does not exist");
                return;
            }

            sessions.addSessionToGame(command.getGameID(), session);

            //send load game message to root client
            LoadGameMessage loadGameMessage = new LoadGameMessage(game);
            session.getRemote().sendString(gson.toJson(loadGameMessage));

            String color = gameService.getPlayerColor(command.getGameID(), username);
            String role = (color == null) ? "observer" : color;

            NotificationMessage notification = new NotificationMessage(username + " joined as " + role);
            sessions.broadcast(command.getGameID(), notification, session);

        } catch (Exception ex) {
            sendError(session, "Error: " + ex.getMessage());
        }
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

    private void sendError(Session session, String message) throws IOException {
        ErrorMessage error = new ErrorMessage(message);
        session.getRemote().sendString(gson.toJson(error));
    }
}
