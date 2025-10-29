package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    @DisplayName("Auth Clear")
    void clearTest() throws DataAccessException {
        AuthData auth = new AuthData("fakeToken", "michael");
        authDao.createAuth(auth);

        assertDoesNotThrow(() -> authDao.clear());

        //maybe further testing for an empty table after get is implemented
    }

}