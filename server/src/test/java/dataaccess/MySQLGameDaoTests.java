package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class MySQLGameDaoTests {
    GameDao gameDao;

    @BeforeEach
    void setup() throws DataAccessException {
        gameDao = new MySQLGameDao();
        gameDao.clear();
    }

    @Test
    @DisplayName("Successful create game")
    void goodCreateGame() throws DataAccessException {
        GameData game = new GameData(0, "whiteUser", "blackUser", "boringGame", new ChessGame());

        GameData result = gameDao.createGame(game);

        assertNotNull(result);
        assertTrue(result.gameID() > 0);
        assertEquals("whiteUser", result.whiteUsername());
        assertEquals("blackUser", result.blackUsername());
        assertEquals("boringGame", result.gameName());
    }

    @Test
    @DisplayName("Null game name fails")
    void badCreateGame() throws DataAccessException {
        GameData game = new GameData(0, "whiteUser", null, null, new ChessGame());
        assertThrows(DataAccessException.class, () -> gameDao.createGame(game));
    }

    @Test
    @DisplayName("Successful game query")
    void goodGetGame() throws DataAccessException {
        GameData game = new GameData(0, "whiteUser", "blackUser", "extraSpecialGame", new ChessGame());
        GameData createResult = gameDao.createGame(game);
        int gameId = createResult.gameID();

        GameData getResult = assertDoesNotThrow(() -> gameDao.getGame(gameId));
        assertNotNull(getResult);
        assertEquals("whiteUser", getResult.whiteUsername());
        assertEquals("blackUser", getResult.blackUsername());
        assertEquals("extraSpecialGame", getResult.gameName());
        assertEquals(createResult.game(), getResult.game());
    }

    @Test
    @DisplayName("Search for invalid game")
    void badGetGame() throws DataAccessException {
        GameData result = gameDao.getGame(67);
        assertNull(result);
    }

    @Test
    @DisplayName("Valid list games")
    void goodListGame() throws DataAccessException {
        Collection<GameData> expected = new ArrayList<>();

        expected.add(gameDao.createGame(new GameData(0, "luffy", "blackbeard", "game1", new ChessGame())));
        expected.add(gameDao.createGame(new GameData(0, "nami", "arlong", "game2", new ChessGame())));
        expected.add(gameDao.createGame(new GameData(0, "zoro", "mihawk", "game3", new ChessGame())));

        Collection<GameData> actual = gameDao.listGames();


        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("List with no games")
    void badListGame() throws DataAccessException {
        Collection<GameData> result = gameDao.listGames();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Successful update")
    void goodUpdateGame() throws DataAccessException {
        GameData game = new GameData(0, "frodo", "sauron", "ringWar", new ChessGame());
        GameData createdGame = gameDao.createGame(game);

        GameData updatedGame = new GameData(createdGame.gameID(), "frodo+sam", "sauron+saruman", "ringWar", new ChessGame());

        assertDoesNotThrow(() -> gameDao.updateGame(updatedGame));

        GameData result = gameDao.getGame(updatedGame.gameID());

        assertNotNull(result);
        assertEquals("frodo+sam", result.whiteUsername());
        assertEquals("sauron+saruman", result.blackUsername());
        assertEquals("ringWar", result.gameName());
        assertEquals(updatedGame.game(), result.game());

    }

    @Test
    @DisplayName("Unsuccessful update")
    void badUpdateGame() {
        GameData invalidGame = new GameData(67, "nobody", "loser", "fools_errand", new ChessGame());
        assertThrows(DataAccessException.class, () -> gameDao.updateGame(invalidGame));
    }
}