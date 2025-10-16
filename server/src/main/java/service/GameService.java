package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.requests.ListRequest;
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

        AuthData token = authDao.getAuth(request.authToken());

        if (token == null) {
            throw new UnauthorizedException("invalid authtoken");
        }

        Collection<GameData> games = gameDao.listGames();

        return new ListResult(games);

    }
}
