package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.AuthData;
import model.GameData;
import websocket.ChessGameHandler;
import websocket.GameHandler;
import websocket.WebSocketFacade;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

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
        System.out.print(auth.username() + " >>> ");
    }

    public void updateGame(GameData game) {
        ChessGame updated = game.game();
        currentGame = updated;
        BoardDrawer.drawBoard(updated, isWhite);
        System.out.print(auth.username() + " >>> ");
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
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Which piece to move? (i.e. e2) ");
            String startInput = scanner.nextLine().trim().toLowerCase();
            int startCol = startInput.charAt(0) - 'a' + 1; //pretty much magic converting letter to col number
            int startRow = Integer.parseInt(startInput.substring(1));

            System.out.print("Where to move to? (i.e. e4) ");
            String endInput = scanner.nextLine().trim().toLowerCase();
            int endCol = endInput.charAt(0) - 'a' + 1;
            int endRow = Integer.parseInt(endInput.substring(1));

            //TODO maybe check promotion here but only if I have to lol

            ChessPosition start = new ChessPosition(startRow, startCol);
            ChessPosition end = new ChessPosition(endRow, endCol);

            ChessMove move = new ChessMove(start, end, null);

            ws.makeMove(auth.authToken(), gameId, move);

        } catch (Exception e) {
            printMessage("Error making move: " + e.getMessage());
        }



    }

    private void handleResign() throws IOException {
        ws.resign(auth.authToken(), gameId);
    }

    private void handleHighlight() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter piece to highlight (i.e. e2) ");
        String pieceInput = scanner.nextLine().trim().toLowerCase();

        if (pieceInput.length() != 2) {
            printMessage("Error: invalid position format");
            return;
        }

        char colChar = pieceInput.charAt(0);
        char rowChar = pieceInput.charAt(1);

        if (colChar < 'a' || colChar > 'h') {
            printMessage("Error: column must be a-h");
            return;
        }

        if (rowChar < '1' || rowChar > '8') {
            printMessage("Error: row must be 1-8");
            return;
        }

        int pieceCol = colChar - 'a' + 1;
        int pieceRow = Character.getNumericValue(rowChar);

        ChessPosition position = new ChessPosition(pieceRow, pieceCol);

        highlightLegalMoves(position);
    }

    private void highlightLegalMoves(ChessPosition position) {
        Collection<ChessMove> legalMoves = currentGame.validMoves(position);

        Set<ChessPosition> endPositions = new HashSet<>();
        for (ChessMove move : legalMoves) {
            endPositions.add(move.getEndPosition());
        }

        BoardDrawer.drawBoard(currentGame, isWhite, endPositions);
    }

}
