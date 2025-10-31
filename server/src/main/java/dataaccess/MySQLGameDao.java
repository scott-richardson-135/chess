package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class MySQLGameDao implements GameDao {

    public MySQLGameDao() throws DataAccessException {
        DatabaseManager.configureDatabase();
    }

    @Override
    public GameData createGame(GameData game) throws DataAccessException {
        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?,?,?,?)";
        String gameJson = new Gson().toJson(game.game());

        int id = DatabaseHelper.executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), gameJson);
        return new GameData(id, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
    }

    @Override
    public GameData getGame(int gameId) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games WHERE gameId=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to get game: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> result = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get game: " + e.getMessage(), e);
        }

        return result;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        var statement = """
                UPDATE games
                SET whiteUsername = ?,
                    blackUsername = ?,
                    gameName = ?,
                    game = ?
                WHERE gameId = ?
                """;

        String gameJson = new Gson().toJson(game.game());

        int rowsUpdated = DatabaseHelper.executeUpdate(statement, game.whiteUsername(), game.blackUsername(),
                game.gameName(), gameJson, game.gameID());

        if (rowsUpdated == 0) {
            throw new DataAccessException("Game not found");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE games";
        DatabaseHelper.executeUpdate(statement);
    }

    private GameData readGame(ResultSet rs) throws SQLException {
       int id = rs.getInt("gameId");
       String white = rs.getString("whiteUsername");
       String black = rs.getString("blackUsername");
       String gameName = rs.getString("gameName");
       String jsonGame = rs.getString("game");

       ChessGame game = new Gson().fromJson(jsonGame, ChessGame.class);

       return new GameData(id, white, black, gameName, game);
    }
}
