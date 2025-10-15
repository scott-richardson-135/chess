package service;

import dataaccess.DataAccessException;
import model.requests.LoginRequest;
import model.requests.RegisterRequest;
import model.results.LoginResult;
import model.results.RegisterResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.exceptions.AlreadyTakenException;
import service.exceptions.BadRequestException;
import service.exceptions.UnauthorizedException;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTests {
    private UserService service;

    @BeforeEach
    public void setup() {
        new ClearService().clear();
        service = new UserService();
    }


    @Test
    @DisplayName("Valid Register")
    public void goodRegister() throws BadRequestException, AlreadyTakenException, DataAccessException {
        RegisterRequest request = new RegisterRequest("username", "password", "email");

        RegisterResult result = service.register(request);

        assertNotNull(result);
        assertEquals("username", result.username());
        assertNotNull(result.authToken());
        assertFalse(result.authToken().isEmpty());

    }


    @Test
    @DisplayName("Duplicate Username Register")
    public void badRegister() throws BadRequestException, AlreadyTakenException, DataAccessException {
        RegisterRequest request = new RegisterRequest("username", "passwrod", "email");

        service.register(request);

        assertThrows(AlreadyTakenException.class, () -> {
            service.register(request);
        });

    }

    @Test
    @DisplayName("Valid Login")
    public void goodLogin() throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        service.register(request);

        LoginRequest loginReq = new LoginRequest("username", "password");
        LoginResult loginRes = service.login(loginReq);

        assertNotNull(loginRes);
        assertEquals("username", loginRes.username());
        assertNotNull(loginRes.authToken());
        assertFalse(loginRes.authToken().isEmpty());
    }

    @Test
    @DisplayName("User Doesn't Exist")
    public void badLogin() throws BadRequestException, AlreadyTakenException, DataAccessException {
        LoginRequest loginReq = new LoginRequest("username", "password");
        assertThrows(UnauthorizedException.class, () -> service.login(loginReq));
    }

}