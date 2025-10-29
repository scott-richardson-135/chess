package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MySQLUserDaoTests {
    private MySQLUserDao userDao;

    @BeforeEach
    void setup() throws DataAccessException {
        userDao = new MySQLUserDao();
        userDao.clear();
    }

    @Test
    @DisplayName("Successful create user")
    void goodCreateUser() {
        UserData user = new UserData("nami", "hashedPassword", "nami@gmail.com");

        assertDoesNotThrow(() -> userDao.createUser(user));
    }

    @Test
    @DisplayName("Duplicate insertion fails: user")
    void badCreateUser() throws DataAccessException {
        UserData user = new UserData("sanji", "hashedPassword", "sanji@gmail.com");
        UserData duplicate = new UserData("sanji", "hashedPassword", "sanji@gmail.com");

        userDao.createUser(user);

        assertThrows(DataAccessException.class, () -> userDao.createUser(duplicate));

    }

    @Test
    @DisplayName("Successful user query")
    void goodGetUser() throws DataAccessException {
        UserData user = new UserData("usopp", "hashedPassword", "usopp@gmail.com");
        userDao.createUser(user);

        UserData result = assertDoesNotThrow(() -> userDao.getUser("usopp"));

        assertNotNull(result);
        assertEquals("usopp", result.username());
        assertEquals("hashedPassword", result.password());
        assertEquals("usopp@gmail.com", result.email());
    }

    @Test
    @DisplayName("Search for invalid user")
    void badGetUser() throws DataAccessException {
        UserData result = userDao.getUser("theOnePiece");
        assertNull(result);
    }

    @Test
    @DisplayName("Successful clear")
    void clearTest() throws DataAccessException {
        UserData user = new UserData("chopper", "hashedPassword", "chopper@gmail.com");
        userDao.createUser(user);

        assertDoesNotThrow(() -> userDao.clear());

    }




}