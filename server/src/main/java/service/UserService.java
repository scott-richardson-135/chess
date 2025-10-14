package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import model.requests.*;
import model.results.*;
import service.exceptions.*;

import java.util.UUID;

public class UserService {

    private static final UserDao userDao = new MemoryUserDao();
    private static final AuthDao authDao = new MemoryAuthDao();

    public RegisterResult register(RegisterRequest request) throws BadRequestException, AlreadyTakenException, DataAccessException {
        //do logic for registering using model and dao classes
        //check for bad request
        if (request.username() == null || request.username().isEmpty() || request.password() == null || request.password().isEmpty() || request.email() == null || request.email().isEmpty()) {
            throw new BadRequestException("bad request");
        }

        //check if already taken
        if (userDao.getUser(request.username()) != null) {
            throw new AlreadyTakenException("user already taken");
        }

        //create user with Dao
        UserData newUser = new UserData(request.username(), request.password(), request.email());
        userDao.createUser(newUser);

        //create authtoken
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        authDao.createAuth(authData);

        return new RegisterResult(request.username(), authToken);


    }
}
