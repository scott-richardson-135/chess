package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLAuthDao implements AuthDao {

    public MySQLAuthDao() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        var statement = "INSERT INTO auths (authToken, username) VALUES (?,?)";
        executeUpdate(statement, auth.authToken(), auth.username());
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {
        //
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE auths";
        executeUpdate(statement);
    }

    //maybe put configure and execute update in a helper class
    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    }
                    else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    }
                    else if (param == null) {
                        ps.setNull(i + 1, NULL);
                    }

                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to update database: " + e.getMessage(), e);
        }

    }

    //have to make a statement
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auths (
                authToken VARCHAR(255) PRIMARY KEY,
                username VARCHAR(255) NOT NULL
            )
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to configure database: " + e.getMessage(), e);
        }
    }
}
