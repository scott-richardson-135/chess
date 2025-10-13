package service;

import model.requests.*;
import model.results.*;
import service.exceptions.BadRequestException;

public class UserService {
    public RegisterResult register(RegisterRequest request) throws BadRequestException {
        //do logic for registering using model and dao classes
        //check for bad request
        if (request.username() == null || request.username().isEmpty() || request.password() == null || request.password().isEmpty() || request.email() == null || request.email().isEmpty()) {
            throw new BadRequestException("bad request");
        }



        return new RegisterResult("placeholder", "placeholder");
    }
}
