package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
}