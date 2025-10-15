package service;

import dataaccess.DataAccessException;
import model.requests.RegisterRequest;
import model.results.RegisterResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.exceptions.AlreadyTakenException;
import service.exceptions.BadRequestException;

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



}