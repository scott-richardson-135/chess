package client;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ResponseException;
import ui.ServerFacade;

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
    public void sampleTest() {
        Assertions.assertTrue(true);
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

}
