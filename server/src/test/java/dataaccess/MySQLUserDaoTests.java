package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

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






}