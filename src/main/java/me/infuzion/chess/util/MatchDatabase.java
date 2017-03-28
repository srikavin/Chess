package me.infuzion.chess.util;

import me.infuzion.chess.web.Game;

import java.sql.*;
import java.util.UUID;

public class MatchDatabase extends Database {
    private Connection connection;

    @SuppressWarnings("SqlNoDataSourceInspection")
    public MatchDatabase(String file) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + file);
        connection.createStatement().execute("PRAGMA foreign_keys = ON;");
        Statement statement = connection.createStatement();
        statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS MATCHES"
                        + "(ID VARCHAR(12)  PRIMARY KEY NOT NULL, "
                        + "PLAYER_WHITE     VARCHAR(36),"
                        + "PLAYER_BLACK     VARCHAR(36),"
                        + "USER_WHITE       VARCHAR(12),"
                        + "USER_BLACK       VARCHAR(12),"
                        + "BOARD            TEXT,"
                        + "FOREIGN KEY (USER_WHITE) REFERENCES USERS(ID),"
                        + "FOREIGN KEY (USER_BLACK) REFERENCES USERS(ID))");
        statement.close();
    }

    public Game getMatch(Identifier id) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM MATCHES WHERE ID=?")) {
            statement.setString(1, id.getId());
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                System.out.println(set);
                String string = set.getString("BOARD");
                String white = set.getString("PLAYER_WHITE");
                String black = set.getString("PLAYER_BLACK");
                UUID whiteUUID = white.equals("null") ? null : UUID.fromString(white);
                UUID blackUUID = white.equals("null") ? null : UUID.fromString(black);
                return new Game(id, string, whiteUUID, blackUUID);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addMatch(Game game) {
        String sql = "INSERT OR IGNORE INTO MATCHES (ID, PLAYER_WHITE, PLAYER_BLACK, BOARD)" +
                "VALUES (?,?,?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(sql)) {

            PreparedStatement updateStatement = connection.prepareStatement(
                    "UPDATE MATCHES SET ID=?, PLAYER_WHITE=?, PLAYER_BLACK=?, BOARD=? WHERE ID=?");
            insertStatement.setString(1, game.getGameID().toString());
            insertStatement.setString(2,
                    game.getWhiteSide() == null ? "null" : game.getWhiteSide().toString());
            insertStatement.setString(3,
                    game.getBlackSide() == null ? "null" : game.getBlackSide().toString());
            insertStatement.setString(4, game.getBlackSide().toString());
            insertStatement.execute();
            insertStatement.close();

            updateStatement.setString(1, game.getGameID().toString());
            updateStatement.setString(2, game.getWhiteSide().toString());
            updateStatement.setString(3, game.getBlackSide().toString());
            updateStatement.setString(4, game.getBoard().toString());
            updateStatement.setString(5, game.getGameID().toString());
            updateStatement.execute();
            updateStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
