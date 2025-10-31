package service;

import dataaccess.*;

public class ClearService {
    private final UserDao USER_DAO;
    private final AuthDao AUTH_DAO;
    private final GameDao GAME_DAO;

    public ClearService() {
        try {
            this.USER_DAO = new MySQLUserDao();
            this.AUTH_DAO = new MySQLAuthDao();
            this.GAME_DAO = new MySQLGameDao();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to initialize DAOs", e);
        }


    }

    public void clear() {
        try {
            USER_DAO.clear();
            AUTH_DAO.clear();
            GAME_DAO.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException("Database Clear Failed", e);
        }

    }
}
