package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {
    private UserDao userDao;
    private AuthDao authDao;
    private GameDao gameDao;
    private ClearService clearService;

    @BeforeEach
    public void setUp() throws DataAccessException {
        // initialize DAOs (these might be static or mock in-memory)
        userDao = new MySQLUserDao();
        authDao = new MySQLAuthDao();
        gameDao = new MySQLGameDao();
        clearService = new ClearService();

        // Add sample data
        userDao.createUser(new UserData("username", "password", "email"));
        authDao.createAuth(new AuthData("authtoken", "username"));
        gameDao.createGame(new GameData(1, "wUsername", "bUsername", "gameName", new ChessGame()));
    }


    @Test
    @DisplayName("Clear Test")
    public void clearTest() throws DataAccessException {
        assertNotNull(userDao.getUser("username"));
        assertNotNull(authDao.getAuth("authtoken"));
        assertNotNull(gameDao.getGame(1));

        // Call clear()
        assertDoesNotThrow(() -> clearService.clear());

        // Verify all data is gone
        assertNull(userDao.getUser("username"));
        assertNull(authDao.getAuth("authtoken"));
        assertNull(gameDao.getGame(1));

    }


}