package ui;

import model.AuthData;
import model.UserData;
import model.requests.LoginRequest;

import java.util.Scanner;

public class SignedOutRepl {
    private final ServerFacade server;

    public SignedOutRepl(String url) {
        server = new ServerFacade(url);
    }

    public void run() throws ResponseException {
        printMenu();
        Scanner scanner = new Scanner(System.in);


        while (true) {
            System.out.print(">>> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "1", "help" -> printHelp();
                case "2", "login" -> {
                    AuthData auth = login(scanner);
                    if (auth != null) {
                        System.out.println("Logged in as " + auth.username());
                    }
                }
                case "3", "register" -> {
                    AuthData auth = register(scanner);
                    if (auth != null) {
                        System.out.println("Logged in as " + auth.username());
                    }
                }
                case "4", "quit" -> {
                    return;
                }
                default -> System.out.println("Invalid command. Type 'help' for help.");
            }

        }
    }

    private void printMenu() {
        System.out.println("Chess Game Client");
        System.out.println("Enter a number to proceed.");
        System.out.println("1. Help \n" +
                "2. Login \n" +
                "3. Register \n" +
                "4. Quit");
    }

    private void printHelp() {
        System.out.println("Very Helpful Message");
    }

    private AuthData login(Scanner scanner) {
        try {
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            LoginRequest req = new LoginRequest(username, password);
            return server.login(req);
        } catch (ResponseException e) {
            System.out.println("Login failed: " + e.getMessage());
            return null;
        }

    }

    private AuthData register(Scanner scanner) {
        try {
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();

            UserData user = new UserData(username, password, email);
            return server.register(user);
        } catch (ResponseException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return null;
        }
    }


}
