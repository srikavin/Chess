package me.infuzion.chess.util;

import me.infuzion.chess.web.game.User;
import org.apache.commons.lang3.StringEscapeUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.Instant;
import java.time.ZoneOffset;

public class UserDatabase extends Database {
    private Connection connection;

    public UserDatabase(String file) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + file);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON;");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS USERS"
                    + "(ID VARCHAR(12) PRIMARY KEY NOT NULL, "
                    + "USERNAME VARCHAR(36) UNIQUE, "
                    + "PASSWORD BINARY(60),"
                    + "BIO TEXT DEFAULT 'nothing to see here',"
                    + "RANK VARCHAR(64) DEFAULT 'USER',"
                    + "LAST_LOGIN INTEGER)");
            statement.close();
        }
        addUser(new Identifier(), "testing", "abc");
    }

    public User addUser(Identifier id, String username, String password) {
        try (PreparedStatement checkAvailability = connection.prepareStatement(
                "SELECT * FROM USERS WHERE USERNAME = ?")) {
            checkAvailability.setString(1, username);
            ResultSet resultSet = checkAvailability.executeQuery();
            if (resultSet.next()) {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO USERS(ID, USERNAME, PASSWORD, LAST_LOGIN) VALUES (?, ?, ?, ?)")) {
            String hashed = BCrypt.hashpw(password, BCrypt.gensalt(12));
            statement.setString(1, id.getId());
            statement.setString(2, username);
            statement.setString(3, hashed);
            statement.setLong(4, getCurrentEpoch());
            statement.executeUpdate();
            return getUserHTMLEscaped(id, username, getCurrentEpoch(), null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static User getUserHTMLEscaped(Identifier id, String username, long epoch, String bio) {
        return new User(id, StringEscapeUtils.escapeHtml4(username), epoch, bio);
    }

    private static long getCurrentEpoch() {
        return Instant.now().atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    public User checkLoginAndGetUser(String username, String password) {
        try (PreparedStatement checkAvailability = connection.prepareStatement(
                "SELECT * FROM USERS WHERE USERNAME = ?")) {
            checkAvailability.setString(1, username);
            ResultSet resultSet = checkAvailability.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            String hashed = resultSet.getString("PASSWORD");
            if (!BCrypt.checkpw(password, hashed)) {
                return null;
            }
            try (PreparedStatement updateLastSeen = connection.prepareStatement(
                    "UPDATE USERS SET LAST_LOGIN = ? WHERE USERNAME = ?")) {
                updateLastSeen.setLong(1, getCurrentEpoch());
                updateLastSeen.setString(2, username);
            }
            String id = resultSet.getString("ID");
            String bio = resultSet.getString("BIO");
            return getUserHTMLEscaped(new Identifier(id), username, getCurrentEpoch(), bio);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public User getUser(String username) {
        try (PreparedStatement checkAvailability = connection.prepareStatement(
                "SELECT * FROM USERS WHERE USERNAME = ?")) {
            checkAvailability.setString(1, username);
            ResultSet resultSet = checkAvailability.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            String id = resultSet.getString("ID");
            long lastLogin = resultSet.getLong("LAST_LOGIN");
            String bio = resultSet.getString("BIO");
            return getUserHTMLEscaped(new Identifier(id), username, lastLogin, bio);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public User getUser(Identifier identifier) {
        try (PreparedStatement checkAvailability = connection.prepareStatement(
                "SELECT * FROM USERS WHERE ID = ?")) {
            checkAvailability.setString(1, identifier.getId());
            ResultSet resultSet = checkAvailability.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            String id = resultSet.getString("ID");
            String username = resultSet.getString("USERNAME");
            long lastLogin = resultSet.getLong("LAST_LOGIN");
            String bio = resultSet.getString("BIO");
            return getUserHTMLEscaped(new Identifier(id), username, lastLogin, bio);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
