package client;

import model.AuthData;
import model.GameData;
import model.UserData;
import model.requests.LoginRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.Server;
import ui.ResponseException;
import ui.ServerFacade;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    @DisplayName("Server Facade valid register")
    public void goodRegisterFacade() throws ResponseException {
        UserData user = new UserData("julio", "abcdefg", "julio@gmail.com");
        AuthData auth = assertDoesNotThrow(() -> facade.register(user));

        assertNotNull(auth);
        assertNotNull(auth.authToken());
        assertEquals("julio", auth.username());
    }

    @Test
    @DisplayName("Bad server facade register")
    public void badRegisterFacade() throws ResponseException {
        UserData badUser = new UserData(null, null, null);

        assertThrows(ResponseException.class, () -> facade.register(badUser));
    }

    @Test
    @DisplayName("Server Facade valid login")
    public void goodLoginFacade() throws ResponseException {
        UserData user = new UserData("Steve", "herobrine", "steve@gmail.com");
        facade.register(user);

        LoginRequest request = new LoginRequest("Steve", "herobrine");

        AuthData auth = assertDoesNotThrow(() -> facade.login(request));
        assertNotNull(auth);
        assertNotNull(auth.authToken());
        assertEquals("Steve", auth.username());
    }

    @Test
    @DisplayName("Server Facade bad login")
    public void badLoginFacade() {
        LoginRequest request = new LoginRequest("fakeUser", "fakePassword");
        assertThrows(ResponseException.class, () -> facade.login(request));
    }

    @Test
    @DisplayName("Server Facade valid logout")
    public void goodLogoutFacade() throws ResponseException {
        UserData user = new UserData("luka", "676767", "luka@gmail.com");
        AuthData auth = facade.register(user);

        assertDoesNotThrow(() -> facade.logout(auth.authToken()));

    }

    @Test
    @DisplayName("Server Facade bad logout")
    public void badLogoutFacade() {
        assertThrows(ResponseException.class, () -> facade.logout("fakeToken"));
    }

    @Test
    @DisplayName("Server Facade valid create")
    public void goodCreateFacade() throws ResponseException {
        UserData user = new UserData("Michael", "blink182", "mikey@gmail.com");
        AuthData auth = facade.register(user);

        String authToken = auth.authToken();

        int gameId = assertDoesNotThrow(() -> facade.create(authToken, "testGame"));

        assertTrue(gameId > 0);
    }

    @Test
    @DisplayName("Server Facade bad create")
    public void badCreateFacade() {
        assertThrows(ResponseException.class, () -> facade.create("fakeToken", "badGame"));
    }

    @Test
    @DisplayName("Server Facade valid list")
    public void goodListFacade() throws ResponseException {
        UserData user = new UserData("benny", "5678987", "benj@gmail.com");
        AuthData auth = facade.register(user);

        String authToken = auth.authToken();

        facade.create(authToken, "testGame1");
        facade.create(authToken, "testGame2");

        Collection<GameData> games = assertDoesNotThrow(() -> facade.list(authToken));

        assertNotNull(games);
    }

    @Test
    @DisplayName("Server Facade bad list")
    public void badListFacade() {
        assertThrows(ResponseException.class, () -> facade.list("fakeToken"));
    }

}
