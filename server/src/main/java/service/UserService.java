package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import model.requests.LoginRequest;
import model.requests.LogoutRequest;
import model.requests.RegisterRequest;
import model.results.LoginResult;
import model.results.LogoutResult;
import model.results.RegisterResult;
import service.exceptions.AlreadyTakenException;
import service.exceptions.BadRequestException;
import service.exceptions.UnauthorizedException;

import java.util.UUID;

public class UserService {

    private static final UserDao USER_DAO = new MemoryUserDao();
    private static final AuthDao AUTH_DAO = new MemoryAuthDao();

    public RegisterResult register(RegisterRequest request) throws BadRequestException, AlreadyTakenException, DataAccessException {
        //do logic for registering using model and dao classes
        //check for bad request
        if (request.username() == null || request.username().isEmpty()
                || request.password() == null || request.password().isEmpty()
                || request.email() == null || request.email().isEmpty()) {
            throw new BadRequestException("bad request");
        }

        //check if already taken
        if (USER_DAO.getUser(request.username()) != null) {
            throw new AlreadyTakenException("user already taken");
        }

        //create user with Dao
        UserData newUser = new UserData(request.username(), request.password(), request.email());
        USER_DAO.createUser(newUser);

        //create authtoken
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        AUTH_DAO.createAuth(authData);

        return new RegisterResult(request.username(), authToken);
    }


    public LoginResult login(LoginRequest request) throws BadRequestException, DataAccessException, UnauthorizedException {
        if (request.username() == null || request.username().isEmpty() || request.password() == null || request.password().isEmpty()) {
            throw new BadRequestException("bad request");
        }

        //find user by username, check if valid
        UserData user = USER_DAO.getUser(request.username());
        if (user == null || !user.password().equals(request.password())) {
            throw new UnauthorizedException("invalid username or password");
        }

        //create authToken
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        AUTH_DAO.createAuth(authData);

        return new LoginResult(request.username(), authToken);
    }

    public LogoutResult logout(LogoutRequest request) throws BadRequestException, DataAccessException, UnauthorizedException {
        if (request.authToken() == null || request.authToken().isEmpty()) {
            throw new BadRequestException("bad request");
        }

        //find AuthData by authtoken
        AuthData token = AUTH_DAO.getAuth(request.authToken());

        if (token == null) {
            throw new UnauthorizedException("invalid authtoken");
        }

        //delete authtoken
        AUTH_DAO.deleteAuth(request.authToken());

        return new LogoutResult();
    }
}
