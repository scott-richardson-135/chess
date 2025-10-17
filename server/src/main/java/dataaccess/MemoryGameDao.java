package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDao implements GameDao {
    private static final HashMap<Integer, GameData> games = new HashMap<>();
    private static int nextId = 1;


    @Override
    public GameData createGame(GameData game) {
        int id = nextId++;
        GameData newGame = new GameData(id, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        games.put(id, game);
        return newGame;
    }

    @Override
    public GameData getGame(int gameId) {
        return games.get(gameId);
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public void updateGame(GameData game) {
        games.put(game.gameID(), game);
    }

    public void clear() {
        games.clear();
        nextId = 1;
    }

}
