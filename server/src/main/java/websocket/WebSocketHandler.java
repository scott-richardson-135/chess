package websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
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
                case MAKE_MOVE -> handleMakeMove(command, session);
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
            String username = userService.getUsernameFromToken(command.getAuthToken());

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

    private void handleMakeMove(UserGameCommand command, Session session) throws IOException {
        System.out.println("handling a make move command");

        try {
            String username = userService.getUsernameFromToken(command.getAuthToken());

            GameData game = gameService.getGame(command.getGameID());
            if (game == null) {
                sendError(session, "Error: game not found");
            }

            String color = gameService.getPlayerColor(command.getGameID(), username);
            if (color == null) {
                sendError(session, "Error: Observers can't make moves.");
                return;
            }


            ChessGame chessGame = game.game();
            if (!chessGame.getTeamTurn().toString().equals(color)) {
                sendError(session, "Error: Not your turn");
                return;
            }

            //try to make the move, and then we'll save it later if it works
            try {
                chessGame.makeMove(command.getMove());
            } catch (InvalidMoveException e) {
                sendError(session, "Invalid move: " + e.getMessage());
                return;
            }

            GameData updated = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), chessGame);
            gameService.updateGame(updated);

            LoadGameMessage loadGameMessage = new LoadGameMessage(updated);
            sessions.broadcast(command.getGameID(), loadGameMessage, null);

            NotificationMessage notification = new NotificationMessage(username + " made a move: " + command.getMove().toString());
            sessions.broadcast(command.getGameID(), notification, session);

        } catch (Exception ex) {
            sendError(session, "Error: " + ex.getMessage());
        }
    }

    private void handleLeave(UserGameCommand command, Session session) throws IOException {
        try {
            String username = userService.getUsernameFromToken(command.getAuthToken());

            GameData game = gameService.getGame(command.getGameID());
            if (game == null) {
                sendError(session, "Error: game does not exist");
                return;
            }

            sessions.removeSessionFromGame(command.getGameID(), session);

            NotificationMessage notification = new NotificationMessage(username + " left the game");
            sessions.broadcast(command.getGameID(), notification, session);

        } catch (Exception ex) {
            sendError(session, "Error: " + ex.getMessage());
        }
    }

    private void handleResign(UserGameCommand command) {
        System.out.println("handling a resign command");
    }

    private void sendError(Session session, String message) throws IOException {
        ErrorMessage error = new ErrorMessage(message);
        session.getRemote().sendString(gson.toJson(error));
    }
}
