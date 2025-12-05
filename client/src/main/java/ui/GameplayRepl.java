package ui;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import websocket.ChessGameHandler;
import websocket.GameHandler;
import websocket.WebSocketFacade;

import java.io.IOException;
import java.util.Scanner;

public class GameplayRepl {

    private final ServerFacade server;
    private final AuthData auth;
    private final int gameId;
    private final boolean isWhite;
    ChessGame currentGame;

    private WebSocketFacade ws;

    GameplayRepl(ServerFacade server, AuthData auth, int gameId, boolean isWhite) {
        this.server = server;
        this.auth = auth;
        this.gameId = gameId;
        this.isWhite = isWhite;

        GameHandler handler = new ChessGameHandler(this);

        try {
            ws = new WebSocketFacade(server.getServerUrl(), handler);

            ws.connect(auth.authToken(), gameId);
        } catch (Exception e) {
            printMessage("Error: " + e.getMessage());
            ws = null;
        }
    }

    public void run() {
        ChessGame game = new ChessGame();
        currentGame = game;
        BoardDrawer.drawBoard(game, isWhite);

        Scanner scanner = new Scanner(System.in);
        printMenu();

        while (true) {
            System.out.print(auth.username() + " >>> ");
            String command = scanner.nextLine().trim().toLowerCase();

            try {
                switch (command) {
                    case "1", "help" -> printMenu();
                    case "2", "redraw" -> BoardDrawer.drawBoard(currentGame, isWhite);
                    case "3", "leave" -> {
                        handleLeave();
                        return;
                    }
                    case "4", "move" -> handleMakeMove();
                    case "5", "resign" -> handleResign();
                    case "6", "highlight" -> handleHighlight();
                    default -> System.out.println("Invalid command. Type 'help' for help.");
                }
            } catch (Exception e) {
                printMessage("Error: " + e.getMessage());
            }
        }
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    public void updateGame(GameData game) {
        ChessGame updated = game.game();
        currentGame = updated;
        BoardDrawer.drawBoard(updated, isWhite);
    }

    private void printMenu() {
        System.out.println("Enter a number to proceed");
        System.out.println("1. Help");
        System.out.println("2. Redraw board");
        System.out.println("3. Leave");
        System.out.println("4. Make move");
        System.out.println("5. Resign");
        System.out.println("6. Highlight legal moves");
    }

    private void handleLeave() throws IOException {
        ws.leave(auth.authToken(), gameId);
    }

    private void handleMakeMove() {
        System.out.println("handling make move...");
    }

    private void handleResign() {
        System.out.println("handling resign...");
    }

    private void handleHighlight() {
        System.out.println("handling highlight...");
    }

}
