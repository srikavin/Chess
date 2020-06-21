package me.infuzion.chess.web.record.source;

import me.infuzion.chess.util.Database;
import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.game.Game;
import me.infuzion.chess.web.game.GameStatus;
import me.infuzion.chess.web.record.RecordSource;
import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchDatabase extends Database implements RecordSource<Game> {
    private final Connection connection;

    public MatchDatabase(Connection url) throws SQLException {
        connection = url;
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS MATCH_STATUS" +
                            "(ID        SERIAL       PRIMARY KEY  NOT NULL," +
                            "NAME       VARCHAR(32)   UNIQUE)");
            try (PreparedStatement insert = connection.prepareStatement("INSERT INTO MATCH_STATUS (NAME) VALUES (?) ON CONFLICT DO NOTHING")) {
                for (GameStatus e : GameStatus.values) {
                    insert.setString(1, e.name());
                    insert.addBatch();
                }
                insert.executeBatch();
            }
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS MATCHES"
                            + "(ID              VARCHAR(16)  PRIMARY KEY NOT NULL, "
                            + "PLAYER_WHITE     VARCHAR(36),"
                            + "PLAYER_BLACK     VARCHAR(36),"
                            + "STATUS           INTEGER,"
                            + "BOARD            TEXT,"
                            + "FOREIGN KEY (STATUS)          REFERENCES MATCH_STATUS(ID),"
                            + "FOREIGN KEY (PLAYER_WHITE)    REFERENCES USERS(ID),"
                            + "FOREIGN KEY (PLAYER_BLACK)    REFERENCES USERS(ID))");
        }
    }

    public List<Game> getMatches(int limit) {
        List<Game> toRet = new ArrayList<>();
        String sql =
                "SELECT M.ID, PLAYER_WHITE, PLAYER_BLACK, MS.NAME AS STATUS_NAME, BOARD FROM MATCHES M " +
                        "JOIN MATCH_STATUS MS " +
                        "ON M.STATUS = MS.ID " +
                        "LIMIT ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                while (resultSet.next()) {
                    toRet.add(getMatch(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    private Game getMatch(ResultSet set) throws SQLException {
        Identifier id = new Identifier(set.getString("ID"));
        String pgn = set.getString("BOARD");
        String white = set.getString("PLAYER_WHITE");
        String black = set.getString("PLAYER_BLACK");
        GameStatus status = GameStatus.valueOf(set.getString("STATUS_NAME"));
        Identifier whiteUUID = white == null ? null : new Identifier(white);
        Identifier blackUUID = black == null ? null : new Identifier(black);
        return new Game(id, pgn, whiteUUID, blackUUID, status);
    }

    public Game getMatch(Identifier id) {
        String sql = "SELECT M.ID, PLAYER_WHITE, PLAYER_BLACK, MS.NAME AS STATUS_NAME, BOARD FROM MATCHES M " +
                "JOIN MATCH_STATUS MS " +
                "ON M.STATUS = MS.ID " +
                "WHERE M.id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id.getId());
            try (ResultSet set = statement.executeQuery()) {
                if (set.next()) {
                    return getMatch(set);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateMatch(Game game) {
        try (PreparedStatement updateStatement = connection.prepareStatement(
                "UPDATE MATCHES SET ID=?, PLAYER_WHITE=?, PLAYER_BLACK=?, STATUS=?, BOARD=? WHERE ID=?")) {
            updateStatement.setString(1, game.getGameID().toString());
            updateStatement.setString(2,
                    game.getWhiteSide() == null ? null : game.getWhiteSide().toString());
            updateStatement.setString(3,
                    game.getBlackSide() == null ? null : game.getBlackSide().toString());
            updateStatement.setString(4, game.getStatus().name());
            updateStatement.setString(5, game.getBoard().toPGNString());
            System.out.println(game.getBoard().toPGNString());
            updateStatement.setString(6, game.getGameID().toString());
            updateStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void addMatch(Game game) {
        String sql =
                "INSERT INTO MATCHES (ID, PLAYER_WHITE, PLAYER_BLACK, STATUS, BOARD)" +
                        "    VALUES (" +
                        "      ?, ?, ?, (SELECT ID FROM MATCH_STATUS WHERE NAME = ?) , ?" +
                        "    )";
        try (PreparedStatement insertStatement = connection.prepareStatement(sql)) {
            insertStatement.setString(1, game.getGameID().toString());
            insertStatement.setString(2,
                    game.getWhiteSide() == null ? null : game.getWhiteSide().toString());
            insertStatement.setString(3,
                    game.getBlackSide() == null ? null : game.getBlackSide().toString());
            insertStatement.setString(4, game.getStatus().name());
            insertStatement.setString(5, game.getBoard().toPGNString());
            insertStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Game> getRecords(int limit) {
        return getMatches(limit);
    }

    @Override
    public Game getRecord(Identifier id) {
        return getMatch(id);
    }
}
