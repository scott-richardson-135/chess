package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDao implements GameDao {
    private static final HashMap<Integer, GameData> GAMES = new HashMap<>();
    private static int nextId = 1;


    @Override
    public GameData createGame(GameData game) {
        int id = nextId++;
        GameData newGame = new GameData(id, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        GAMES.put(id, newGame);
        return newGame;
    }

    @Override
    public GameData getGame(int gameId) {
        return GAMES.get(gameId);
    }

    @Override
    public Collection<GameData> listGames() {
        return GAMES.values();
    }

    @Override
    public void updateGame(GameData game) {
        GAMES.put(game.gameID(), game);
    }

    public void clear() {
        GAMES.clear();
        nextId = 1;
    }

}
