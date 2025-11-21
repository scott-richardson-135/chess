package ui;

import chess.ChessGame;
import model.AuthData;

public class GameplayRepl {

    private final ServerFacade server;
    private final AuthData auth;
    private final int gameId;
    private final boolean isWhite;

    GameplayRepl(ServerFacade server, AuthData auth, int gameId, boolean isWhite) {
        this.server = server;
        this.auth = auth;
        this.gameId = gameId;
        this.isWhite = isWhite;
    }

    public void run() {
        //somehow figure out how to draw the corresponding game instead of a new one
        ChessGame game = new ChessGame();
        BoardDrawer.drawBoard(game, isWhite);
    }
}
