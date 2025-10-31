package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MySQLAuthDaoTest {
    private MySQLAuthDao authDao;

    @BeforeEach
    void setup() throws DataAccessException {
        authDao = new MySQLAuthDao();
        authDao.clear();
    }

    @Test
    @DisplayName("Create auth stores in database")
    void goodCreateAuth() {
        AuthData auth = new AuthData("fakeToken", "scott");

        assertDoesNotThrow(() -> authDao.createAuth(auth));
    }

    @Test
    @DisplayName("Duplicate insertion fails")
    void badCreateAuth() throws DataAccessException {
        AuthData auth = new AuthData("fakeToken", "steven");
        authDao.createAuth(auth);

        AuthData duplicate = new AuthData("fakeToken", "steven");
        assertThrows(DataAccessException.class, () -> authDao.createAuth(duplicate));
    }

    @Test
    @DisplayName("Successful query")
    void goodGetAuth() throws DataAccessException {
        AuthData auth = new AuthData("fakeToken", "gurt");
        authDao.createAuth(auth);

        AuthData result = assertDoesNotThrow(() -> authDao.getAuth("fakeToken"));

        assertNotNull(result);
        assertEquals("fakeToken", result.authToken());
        assertEquals("gurt", result.username());
    }

    @Test
    @DisplayName("Search invalid authToken")
    void badGetAuth() throws DataAccessException {
        AuthData result = authDao.getAuth("invalidToken");

        assertNull(result);
    }

    @Test
    @DisplayName("Valid delete")
    void goodDeleteAuth() throws DataAccessException {
        AuthData auth1 = new AuthData("fakeToken", "benny");
        AuthData auth2 = new AuthData("differentToken", "luffy");

        authDao.createAuth(auth1);
        authDao.createAuth(auth2);

        assertDoesNotThrow(() -> authDao.deleteAuth("fakeToken"));

        AuthData result = authDao.getAuth("fakeToken");

        assertNull(result);
    }

    @Test
    @DisplayName("Delete nonexistent auth")
    void badDeleteAuth() throws DataAccessException {
        AuthData auth = new AuthData("realToken", "zoro");
        authDao.createAuth(auth);

        assertDoesNotThrow(() -> authDao.deleteAuth("fakeToken"));

        //make sure our db didn't change
        AuthData result = authDao.getAuth("realToken");
        assertNotNull(result);
        assertEquals("zoro", result.username());
    }



    @Test
    @DisplayName("Auth Clear")
    void clearTest() throws DataAccessException {
        AuthData auth = new AuthData("fakeToken", "michael");
        authDao.createAuth(auth);

        assertDoesNotThrow(() -> authDao.clear());

        //maybe further testing for an empty table after get is implemented
    }

    @AfterAll
    static void teardown() throws DataAccessException {
        var gameDao = new MySQLAuthDao();
        gameDao.clear();
    }

}