package me.infuzion.chess.web.record.source;

import me.infuzion.chess.util.Database;
import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.game.User;
import org.apache.commons.lang3.StringEscapeUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.Instant;
import java.time.ZoneOffset;

public class UserDatabase extends Database {
    private final Connection connection;

    public UserDatabase(Connection url) throws SQLException {
        connection = url;
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS USERS"
                    + "(ID varchar(16) PRIMARY KEY NOT NULL, "
                    + "USERNAME varchar(36) UNIQUE, "
                    + "PASSWORD varchar(60),"
                    + "BIO TEXT DEFAULT 'nothing to see here',"
                    + "RANK varchar(64) DEFAULT 'USER',"
                    + "IMAGE_PATH TEXT DEFAULT '/images/unknown.png',"
                    + "LAST_LOGIN bigint)");
        }
        addUser(new Identifier(), "testing", "abc");
    }

    private static User getUserHTMLEscaped(Identifier id, String username, long epoch, String bio, String imagePath) {
        return new User(id, StringEscapeUtils.escapeHtml4(username), epoch, StringEscapeUtils.escapeHtml4(bio), imagePath);
    }

    private static long getCurrentEpoch() {
        return Instant.now().atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    public User addUser(Identifier id, String username, String password) {
        try (PreparedStatement checkAvailability = connection.prepareStatement(
                "SELECT * FROM USERS WHERE USERNAME = ?")) {
            checkAvailability.setString(1, username);
            try (ResultSet resultSet = checkAvailability.executeQuery()) {
                if (resultSet.next()) {
                    return null;
                }
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

            try (ResultSet resultSet = statement.getResultSet()) {
                return null;
//                return getUserHTMLEscaped(id, username, getCurrentEpoch(),
//                        resultSet.getString("BIO"),
//                        resultSet.getString("IMAGE_PATH"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateImagePath(Identifier user, String path) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE USERS SET IMAGE_PATH = ? WHERE ID = ?")) {
            statement.setString(1, path);
            statement.setString(2, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User checkLoginAndGetUser(String username, String password) {
        try (PreparedStatement checkAvailability = connection.prepareStatement(
                "SELECT * FROM USERS WHERE USERNAME = ?")) {
            checkAvailability.setString(1, username);
            try (ResultSet resultSet = checkAvailability.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
//                String hashed = resultSet.getString("PASSWORD");
//            if (!BCrypt.checkpw(password, hashed)) {
//                return null;
//            }

                String id = resultSet.getString("ID");
                String bio = resultSet.getString("BIO");
                String imagePath = resultSet.getString("IMAGE_PATH");

                try (PreparedStatement updateLastSeen = connection.prepareStatement(
                        "UPDATE USERS SET LAST_LOGIN = ? WHERE ID = ?")) {
                    updateLastSeen.setLong(1, getCurrentEpoch());
                    updateLastSeen.setString(2, id);
                    updateLastSeen.executeUpdate();
                }


                return getUserHTMLEscaped(new Identifier(id), username, getCurrentEpoch(), bio, imagePath);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public User getUser(String username) {
        try (PreparedStatement checkAvailability = connection.prepareStatement(
                "SELECT * FROM USERS WHERE USERNAME = ?")) {
            checkAvailability.setString(1, username);
            try (ResultSet resultSet = checkAvailability.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                String id = resultSet.getString("ID");
                long lastLogin = resultSet.getLong("LAST_LOGIN");
                String bio = resultSet.getString("BIO");
                String imagePath = resultSet.getString("IMAGE_PATH");

                return getUserHTMLEscaped(new Identifier(id), username, lastLogin, bio, imagePath);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public User getUser(Identifier identifier) {
        try (PreparedStatement checkAvailability = connection.prepareStatement(
                "SELECT * FROM USERS WHERE ID = ?")) {
            checkAvailability.setString(1, identifier.getId());
            try (ResultSet resultSet = checkAvailability.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                String id = resultSet.getString("ID");
                String username = resultSet.getString("USERNAME");
                long lastLogin = resultSet.getLong("LAST_LOGIN");
                String bio = resultSet.getString("BIO");
                String imagePath = resultSet.getString("IMAGE_PATH");

                return getUserHTMLEscaped(new Identifier(id), username, lastLogin, bio, imagePath);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
