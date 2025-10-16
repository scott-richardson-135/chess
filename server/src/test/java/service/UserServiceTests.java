package service;

import dataaccess.DataAccessException;
import model.requests.LoginRequest;
import model.requests.LogoutRequest;
import model.requests.RegisterRequest;
import model.results.LoginResult;
import model.results.LogoutResult;
import model.results.RegisterResult;
import org.eclipse.jetty.util.log.Log;
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

    //Register tests

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

    //Login Tests
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

    //Logout tests
    @Test
    @DisplayName("Valid Logout")
    public void goodLogout() throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {
        //register and login
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        service.register(request);

        LoginRequest loginReq = new LoginRequest("username", "password");
        LoginResult loginRes = service.login(loginReq);

        //make sure we got token
        assertNotNull(loginRes.authToken());
        assertFalse(loginRes.authToken().isEmpty());

        //logout
        LogoutRequest logoutReq = new LogoutRequest(loginRes.authToken());
        service.logout(logoutReq);

        //make sure token was removed, throw an exception if we try a second time
        assertThrows(UnauthorizedException.class, () -> service.logout(logoutReq));

    }

    @Test
    @DisplayName("Logout with invalid token")
    public void badLogout() {
        LogoutRequest request = new LogoutRequest("faketoken");

        assertThrows(UnauthorizedException.class, () -> service.logout(request));

    }

}