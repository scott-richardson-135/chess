package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.requests.CreateRequest;
import model.requests.JoinRequest;
import model.requests.ListRequest;
import model.results.CreateResult;
import model.results.JoinResult;
import model.results.ListResult;
import service.exceptions.AlreadyTakenException;
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

    public JoinResult join(JoinRequest request) throws BadRequestException, UnauthorizedException, DataAccessException, AlreadyTakenException {
        if (request.authToken() == null || request.authToken().isEmpty() ||
                request.playerColor() == null || request.playerColor().isEmpty() || request.ID() == 0) {
            throw new BadRequestException("bad request");
        }

        checkAuth(request.authToken());
        AuthData token = authDao.getAuth(request.authToken());
        String username = token.username();


        GameData requestedGame = gameDao.getGame(request.ID());
        if (requestedGame == null) {
            throw new BadRequestException("game does not exist");
        }

        String color = request.playerColor();

        if (color.equals("WHITE")) {
            //check if whiteUsername has a value yet, if so it is already taken
            if (requestedGame.whiteUsername() != null && !requestedGame.whiteUsername().isEmpty()) {
                throw new AlreadyTakenException("White already taken");
            }

            //create new game with updated info
            requestedGame = new GameData(
                    requestedGame.gameID(),
                    username,
                    requestedGame.blackUsername(),
                    requestedGame.gameName(),
                    requestedGame.game()
            );
        }
        else if (color.equals("BLACK")) {
            //check if blackUsername has a value yet, same deal as above
            if (requestedGame.blackUsername() != null && !requestedGame.blackUsername().isEmpty()) {
                throw new AlreadyTakenException("Black already taken");
            }

            requestedGame = new GameData(
                    requestedGame.gameID(),
                    requestedGame.whiteUsername(),
                    username,
                    requestedGame.gameName(),
                    requestedGame.game()
            );

        }
        else {
            throw new BadRequestException("invalid color");
        }

        gameDao.updateGame(requestedGame);

        return new JoinResult();
    }

    private void checkAuth(String authToken) throws UnauthorizedException, DataAccessException {
        AuthData token = authDao.getAuth(authToken);

        if (token == null) {
            throw new UnauthorizedException("invalid authtoken");
        }

    }
}
