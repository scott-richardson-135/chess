package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.requests.CreateRequest;
import model.requests.ListRequest;
import model.results.CreateResult;
import model.results.ListResult;
import service.exceptions.BadRequestException;
import service.exceptions.UnauthorizedException;

import java.util.Collection;

public class GameService {
    private static final GameDao gameDao = new MemoryGameDao();
    private static final AuthDao authDao = new MemoryAuthDao();

    public ListResult list(ListRequest request) throws UnauthorizedException, BadRequestException, DataAccessException {
        if (request.authToken() == null || request.authToken().isEmpty()) {
            throw new BadRequestException("bad request");
        }

        checkAuth(request.authToken());

        Collection<GameData> games = gameDao.listGames();

        return new ListResult(games);
    }


    public CreateResult create(CreateRequest request) throws BadRequestException, UnauthorizedException, DataAccessException {
        if (request.authToken() == null || request.authToken().isEmpty() || request.gameName() == null || request.gameName().isEmpty()) {
            throw new BadRequestException("bad request");
        }

        checkAuth(request.authToken());

        GameData newGame = new GameData(0, null, null, request.gameName(), null);

        GameData storedGame = gameDao.createGame(newGame);


        return new CreateResult(storedGame.gameID());
    }

    private void checkAuth(String authToken) throws UnauthorizedException, DataAccessException {
        AuthData token = authDao.getAuth(authToken);

        if (token == null) {
            throw new UnauthorizedException("invalid authtoken");
        }

    }
}
