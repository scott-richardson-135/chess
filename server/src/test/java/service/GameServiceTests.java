package service;

import dataaccess.DataAccessException;
import dataaccess.GameDao;
import dataaccess.MySQLGameDao;
import model.GameData;
import model.requests.CreateRequest;
import model.requests.JoinRequest;
import model.requests.ListRequest;
import model.requests.RegisterRequest;
import model.results.CreateResult;
import model.results.JoinResult;
import model.results.ListResult;
import model.results.RegisterResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.exceptions.AlreadyTakenException;
import service.exceptions.BadRequestException;
import service.exceptions.UnauthorizedException;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTests {
    private static final GameService GAME_SERVICE = new GameService();
    private static final UserService USER_SERVICE = new UserService();
    private GameDao GAME_DAO;
    GameServiceTests() {
        try {
            this.GAME_DAO = new MySQLGameDao();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void setup() {
        new ClearService().clear();
    }

    //list tests
    @Test
    @DisplayName("Valid list")
    public void goodList() throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        RegisterResult regResult = USER_SERVICE.register(request);
        String authToken = regResult.authToken();


        CreateRequest create1 = new CreateRequest(authToken, "game1");
        CreateRequest create2 = new CreateRequest(authToken, "game2");

        GAME_SERVICE.create(create1);
        GAME_SERVICE.create(create2);

        ListRequest listRequest = new ListRequest(authToken);
        ListResult listResult = GAME_SERVICE.list(listRequest);

        assertNotNull(listResult);
        assertNotNull(listResult.games());
        assertEquals(2, listResult.games().size());
    }

    @Test
    @DisplayName("List with invalid token")
    public void badList() {
        ListRequest request = new ListRequest("faketoken");

        assertThrows(UnauthorizedException.class, () -> GAME_SERVICE.list(request));

    }

    //create tests
    @Test
    @DisplayName("Valid create")
    public void goodCreate() throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        RegisterResult regResult = USER_SERVICE.register(request);
        String authToken = regResult.authToken();

        CreateRequest createRequest = new CreateRequest(authToken, "testGame");
        CreateResult result = GAME_SERVICE.create(createRequest);

        assertNotNull(result);
        assertTrue(result.gameID() > 0);
    }

    @Test
    @DisplayName("Create with invalid token")
    public void badCreate() {
        CreateRequest request = new CreateRequest("faketoken", "cooked");

        assertThrows(UnauthorizedException.class, () -> GAME_SERVICE.create(request));

    }

    @Test
    @DisplayName("Valid join")
    public void goodJoin() throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        RegisterResult regResult = USER_SERVICE.register(request);
        String authToken = regResult.authToken();

        CreateRequest createRequest = new CreateRequest(authToken, "testGame");
        CreateResult createResult = GAME_SERVICE.create(createRequest);

        JoinRequest joinRequest = new JoinRequest(authToken, "WHITE", createResult.gameID());
        JoinResult joinResult = GAME_SERVICE.join(joinRequest);

        assertNotNull(joinRequest);

        GameData game = GAME_DAO.getGame(createResult.gameID());
        assertNotNull(game);

        assertEquals("username", game.whiteUsername());
        assertNull(game.blackUsername());
        assertEquals("testGame", game.gameName());

    }

    @Test
    @DisplayName("Join with invalid token")
    public void badJoin() {
        JoinRequest request = new JoinRequest("faketoken", "color", 1);

        assertThrows(UnauthorizedException.class, () -> GAME_SERVICE.join(request));
    }


}