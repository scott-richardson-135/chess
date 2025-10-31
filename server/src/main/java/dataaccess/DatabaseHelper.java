package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class DatabaseHelper {

    public static int executeUpdate(String statement, Object... params) throws DataAccessException {
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
                int rowsEffected = ps.executeUpdate();


                //only return generated keys if this is an insert
                if (statement.trim().toUpperCase().startsWith("INSERT")) {
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }

                //return number of rows changed for update
                return rowsEffected;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to update database: " + e.getMessage(), e);
        }

    }
}
