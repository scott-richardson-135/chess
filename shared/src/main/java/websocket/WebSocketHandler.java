package websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;

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
                case CONNECT -> handleConnectCommand(command);
                case MAKE_MOVE -> handleMakeMove(command);
                case LEAVE -> handleLeave(command);
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

    private void handleConnectCommand(UserGameCommand command) {
        System.out.println("handling a connect command");
    }

    private void handleMakeMove(UserGameCommand command) {
        System.out.println("handling a make move command");
    }

    private void handleLeave(UserGameCommand command) {
        System.out.println("handling a leave command");
    }

    private void handleResign(UserGameCommand command) {
        System.out.println("handling a resign command");
    }
}
