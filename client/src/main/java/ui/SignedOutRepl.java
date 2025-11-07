package ui;

import model.UserData;

import java.util.Scanner;

public class SignedOutRepl {
    private final ServerFacade server;

    public SignedOutRepl(String url) {
        server = new ServerFacade(url);
    }

    public void run() throws ResponseException {
        System.out.println("testing thingy");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            String line = scanner.nextLine();
            switch (line) {
                case "1":
                    var user = new UserData("test", "1234", "test@gmail");
                    var auth = server.register(user);
                    System.out.println("authToken: " + auth.authToken());
            }

            result = "quit";
        }
    }

}
