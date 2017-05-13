package me.infuzion.chess.util;

import me.infuzion.chess.web.game.Game;
import me.infuzion.chess.web.game.GameStatus;

import java.sql.*;

public class MatchDatabase extends Database {
    private Connection connection;

    @SuppressWarnings("SqlNoDataSourceInspection")
    public MatchDatabase(String file) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + file);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON;");
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS MATCHES"
                            + "(ID VARCHAR(12)  PRIMARY KEY NOT NULL, "
                            + "PLAYER_WHITE     VARCHAR(36),"
                            + "PLAYER_BLACK     VARCHAR(36),"
                            + "STATUS           VARCHAR(24),"
                            + "BOARD            TEXT,"
                            + "FOREIGN KEY (PLAYER_WHITE) REFERENCES USERS(ID),"
                            + "FOREIGN KEY (PLAYER_BLACK) REFERENCES USERS(ID))");
            statement.close();
        }
    }

    public Game getMatch(Identifier id) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM MATCHES WHERE ID=?")) {
            statement.setString(1, id.getId());
            try (ResultSet set = statement.executeQuery()) {
                if (set.next()) {
                    String pgn = set.getString("BOARD");
                    String white = set.getString("PLAYER_WHITE");
                    String black = set.getString("PLAYER_BLACK");
                    GameStatus status = GameStatus.valueOf(set.getString("STATUS"));
                    Identifier whiteUUID = white == null ? null : new Identifier(white);
                    Identifier blackUUID = black == null ? null : new Identifier(black);
                    return new Game(id, pgn, whiteUUID, blackUUID, status);
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
            updateStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void addMatch(Game game) {
        String sql = "INSERT OR IGNORE INTO MATCHES (ID, PLAYER_WHITE, PLAYER_BLACK, STATUS, BOARD)" +
                "VALUES (?,?,?,?,?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(sql)) {
            insertStatement.setString(1, game.getGameID().toString());
            insertStatement.setString(2,
                    game.getWhiteSide() == null ? null : game.getWhiteSide().toString());
            insertStatement.setString(3,
                    game.getBlackSide() == null ? null : game.getBlackSide().toString());
            insertStatement.setString(4, game.getStatus().name());
            insertStatement.setString(5, game.getBoard().toPGNString());
            insertStatement.execute();
            insertStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
