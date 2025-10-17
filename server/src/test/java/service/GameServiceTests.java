package service;

import dataaccess.DataAccessException;
import model.requests.CreateRequest;
import model.requests.ListRequest;
import model.requests.RegisterRequest;
import model.results.CreateResult;
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
    private GameService GameService;
    private UserService UserService;

    @BeforeEach
    public void setup() {
        new ClearService().clear();
        GameService = new GameService();
        UserService = new UserService();
    }

    //list tests
    @Test
    @DisplayName("Valid list")
    public void goodList() throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        RegisterResult regResult = UserService.register(request);
        String authToken = regResult.authToken();


        CreateRequest create1 = new CreateRequest(authToken, "game1");
        CreateRequest create2 = new CreateRequest(authToken, "game2");

        GameService.create(create1);
        GameService.create(create2);

        ListRequest listRequest = new ListRequest(authToken);
        ListResult listResult = GameService.list(listRequest);

        assertNotNull(listResult);
        assertNotNull(listResult.games());
        assertEquals(2, listResult.games().size());
    }

    @Test
    @DisplayName("List with invalid token")
    public void badList() {
        ListRequest request = new ListRequest("faketoken");

        assertThrows(UnauthorizedException.class, () -> GameService.list(request));

    }

    //create tests
    @Test
    @DisplayName("Valid create")
    public void goodCreate() throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        RegisterResult regResult = UserService.register(request);
        String authToken = regResult.authToken();

        CreateRequest createRequest = new CreateRequest(authToken, "testGame");
        CreateResult result = GameService.create(createRequest);

        assertNotNull(result);
        assertTrue(result.gameID() > 0);
    }

    @Test
    @DisplayName("Create with invalid token")
    public void badCreate() {
        CreateRequest request = new CreateRequest("faketoken", "cooked");

        assertThrows(UnauthorizedException.class, () -> GameService.create(request));

    }


}