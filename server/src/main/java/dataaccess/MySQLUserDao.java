package dataaccess;

import model.UserData;

public class MySQLUserDao implements UserDao {
    @Override
    public void createUser(UserData user) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        DatabaseHelper.executeUpdate(statement, user.username(), user.password(), user.email());
    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE users";
        DatabaseHelper.executeUpdate(statement);
    }
}
