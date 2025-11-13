package ui;

import model.AuthData;

import java.util.Scanner;

public class SignedInRepl {
    ServerFacade server;
    AuthData auth;

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
            System.out.println("Game Name: ");
            String gameName = scanner.nextLine();
            server.create(auth.authToken(), gameName);
            System.out.println("Game created");
        } catch (ResponseException e) {
            System.out.println("Failed to create game: " + e.getMessage());
        }

    }




}
