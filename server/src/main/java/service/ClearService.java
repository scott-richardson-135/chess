package service;

import dataaccess.*;

public class ClearService {
    private final UserDao userDao;
    private final AuthDao authDao;
    private final GameDao gameDao;

    public ClearService() {
        try {
            this.userDao = new MySQLUserDao();
            this.authDao = new MySQLAuthDao();
            this.gameDao = new MySQLGameDao();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to initialize DAOs", e);
        }


    }

    public void clear() {
        try {
            userDao.clear();
            authDao.clear();
            gameDao.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException("Database Clear Failed", e);
        }

    }
}
