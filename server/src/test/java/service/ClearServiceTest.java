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
    private UserDao USER_DAO;
    private AuthDao AUTH_DAO;
    private GameDao GAME_DAO;
    private ClearService clearService;

    @BeforeEach
    public void setUp() throws DataAccessException {
        // initialize DAOs (these might be static or mock in-memory)
        USER_DAO = new MySQLUserDao();
        AUTH_DAO = new MySQLAuthDao();
        GAME_DAO = new MySQLGameDao();
        clearService = new ClearService();

        // Add sample data
        USER_DAO.createUser(new UserData("username", "password", "email"));
        AUTH_DAO.createAuth(new AuthData("authtoken", "username"));
        GAME_DAO.createGame(new GameData(1, "wUsername", "bUsername", "gameName", new ChessGame()));
    }


    @Test
    @DisplayName("Clear Test")
    public void clearTest() throws DataAccessException {
        assertNotNull(USER_DAO.getUser("username"));
        assertNotNull(AUTH_DAO.getAuth("authtoken"));
        assertNotNull(GAME_DAO.getGame(1));

        // Call clear()
        assertDoesNotThrow(() -> clearService.clear());

        // Verify all data is gone
        assertNull(USER_DAO.getUser("username"));
        assertNull(AUTH_DAO.getAuth("authtoken"));
        assertNull(GAME_DAO.getGame(1));

    }


}