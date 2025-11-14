package ui;

import chess.ChessGame;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

public class SignedInRepl {
    ServerFacade server;
    AuthData auth;

    private final HashMap<Integer, Integer> gameNumberToId = new HashMap<>();

    SignedInRepl(ServerFacade server, AuthData auth) {
        this.server = server;
        this.auth = auth;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        printMenu();

        while (true) {
            System.out.print(auth.username() + " >>> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "1", "help" -> printHelp();
                case "2", "logout" -> {
                    try {
                        server.logout(auth.authToken());
                    } catch (ResponseException e) {
                        System.out.println("Logout failed: " + e.getMessage());

                    }
                    System.out.println("Logged out");
                    return;

                }
                case "3", "create game" -> createGame(scanner);
                case "4", "list games" -> listGames();
                case "5", "join game" -> joinGame(scanner);
                case "6", "observe game" -> observeGame(scanner);

                default -> System.out.println("Invalid command. Type 'help' for help.");
            }
        }


    }

    private void printMenu() {
        System.out.println("Enter a number to proceed.");
        System.out.println("1. Help");
        System.out.println("2. Logout");
        System.out.println("3. Create game");
        System.out.println("4. List games");
        System.out.println("5. Join game");
        System.out.println("6. Observe game");
    }

    private void printHelp() {
        System.out.println("Enter one of the following commands and follow the prompts: ");
        printMenu();
    }

    private void createGame(Scanner scanner) {
        try {
            System.out.print("Game Name: ");
            String gameName = scanner.nextLine();
            server.create(auth.authToken(), gameName);
            System.out.println("Game created");
        } catch (ResponseException e) {
            System.out.println("Failed to create game: " + e.getMessage());
        }
    }

    private void listGames() {
        Collection<GameData> games = new ArrayList<>();
        gameNumberToId.clear();
        try {
            games = server.list(auth.authToken());
        } catch (ResponseException e) {
            System.out.println("Failed to list games: " + e.getMessage());
        }


        if (games.isEmpty()) {
            System.out.println("No games found");
            return;
        }

        int index = 1;
        System.out.println("Games: ");
        for (GameData game : games) {
            gameNumberToId.put(index, game.gameID());
            String white = (game.whiteUsername() != null) ? game.whiteUsername() : "---";
            String black = (game.blackUsername() != null) ? game.blackUsername() : "---";

            System.out.printf("%d. %s- White: %s, Black: %s\n", index, game.gameName(), white, black);
            index++;
        }
    }

    private void joinGame(Scanner scanner) {
        try {
            System.out.print("Enter game number to join: ");
            int gameNumber = Integer.parseInt(scanner.nextLine());
            Integer gameId = gameNumberToId.get(gameNumber);

            if (gameId == null) {
                System.out.println("Invalid game number");
                return;
            }

            System.out.print("Enter 1 for white, 2 for black: ");
            String colorNumber = scanner.nextLine().trim();

            String colorString = switch (colorNumber) {
                case "1" -> "WHITE";
                case "2" -> "BLACK";
                default -> null;
            };
            if (colorString == null) {
                System.out.println("Invalid color choice");
                return;
            }

            server.join(auth.authToken(), gameId, colorString);
            System.out.printf("Joined game %d as %s\n", gameNumber, colorString.toLowerCase());


            boolean isWhite = colorString.equals("WHITE");
            ChessGame game = new ChessGame();
            BoardDrawer.drawBoard(game, isWhite);

        } catch (ResponseException e) {
            System.out.println("Failed to join game: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Unrecognized game number");
        }
    }

    private void observeGame(Scanner scanner) {
        System.out.print("Enter game number to observe: ");
        int gameNumber = 0;
        try {
            gameNumber = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid game number");
            return;
        }
        Integer gameId = gameNumberToId.get(gameNumber);

        if (gameId == null) {
            System.out.println("Invalid game number");
            return;
        }

        System.out.printf("Observing game %d\n", gameNumber);


        ChessGame game = new ChessGame();
        BoardDrawer.drawBoard(game, true);

    }






}
