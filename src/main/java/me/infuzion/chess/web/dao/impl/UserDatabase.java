/*
 * Copyright 2020 Srikavin Ramkumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.infuzion.chess.web.dao.impl;

import me.infuzion.chess.util.Database;
import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.dao.UserDao;
import me.infuzion.chess.web.domain.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mindrot.jbcrypt.BCrypt;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneOffset;

public class UserDatabase extends Database implements UserDao {
    private final DataSource source;

    public UserDatabase(DataSource source) {
        this.source = source;
        DBHelper.executeStatement(source, "create table if not exists USERS"
                + "(ID varchar(16) primary key not null , "
                + "USERNAME varchar(36) unique , "
                + "PASSWORD varchar(60),"
                + "BIO text default 'nothing to see here',"
                + "ROLE varchar(64) default 'USER',"
                + "IMAGE_PATH bytea default null,"
                + "LAST_LOGIN timestamp default now())");

        createUser(new Identifier(), "testing", "abc");
    }

    private static long getCurrentEpoch() {
        return Instant.now().atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    private @NotNull User mapUser(ResultSet set) throws SQLException {
        String id = set.getString("ID");
        String username = set.getString("USERNAME");
        long lastLogin = set.getLong("LAST_LOGIN");
        String bio = set.getString("BIO");
        String imagePath = set.getString("IMAGE_PATH");

        return new User(new Identifier(id), username, lastLogin, bio, imagePath);
    }

    @Override
    public User createUser(Identifier id, String username, String password) {
        User existing = DBHelper.prepareStatement(source, "SELECT * FROM USERS WHERE USERNAME = ?", ps -> {
            ps.setString(1, username);

            return DBHelper.mapFirstElement(ps.executeQuery(), this::mapUser);
        });

        if (existing != null) {
            return null;
        }

        return DBHelper.prepareStatement(source, "INSERT INTO USERS(ID, USERNAME, PASSWORD, LAST_LOGIN) VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING", ps -> {
            String hashed = BCrypt.hashpw(password, BCrypt.gensalt(12));
            ps.setString(1, id.getId());
            ps.setString(2, username);
            ps.setString(3, hashed);
            ps.setLong(4, getCurrentEpoch());
            ps.executeUpdate();

            return getUser(id.getId());
        });
    }

    public void updateImagePath(Identifier user, String path) {
        try (Connection connection = source.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE USERS SET IMAGE_PATH = ? WHERE ID = ?")) {
                statement.setString(1, path);
                statement.setString(2, user.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public @Nullable User checkLoginAndGetUser(String username, String password) {
        return DBHelper.prepareStatement(source, "SELECT * FROM USERS WHERE USERNAME = ?", (ps) -> {
            ps.setString(1, username);

            return DBHelper.mapFirstElement(ps.executeQuery(), (rs) -> {
                String id = rs.getString("ID");
                String hashed = rs.getString("PASSWORD");
                String bio = rs.getString("BIO");
                String imagePath = rs.getString("IMAGE_PATH");

                if (!BCrypt.checkpw(password, hashed)) {
                    return null;
                }

                DBHelper.prepareStatement(source, "UPDATE USERS SET LAST_LOGIN = ? WHERE ID = ?", (lastSeenPS) -> {
                    // update user last login
                    lastSeenPS.setLong(1, getCurrentEpoch());
                    lastSeenPS.setString(2, id);
                    lastSeenPS.executeUpdate();
                });

                return new User(new Identifier(id), username, getCurrentEpoch(), bio, imagePath);
            });
        });
    }

    public @Nullable User getUser(String username) {
        return DBHelper.prepareStatement(source, "SELECT * FROM USERS WHERE USERNAME = ?", (ps -> {
            ps.setString(1, username);

            return DBHelper.mapFirstElement(ps.executeQuery(), this::mapUser);
        }));
    }

    public @Nullable User getUser(Identifier identifier) {
        return DBHelper.prepareStatement(source, "SELECT * FROM USERS WHERE ID = ?", (ps) -> {
            ps.setString(1, identifier.getId());

            return DBHelper.mapFirstElement(ps.executeQuery(), this::mapUser);
        });
    }

    @Override
    public void deleteUser(Identifier identifier) {
        DBHelper.prepareStatement(source, "DELETE FROM USERS WHERE id = ?", ps -> {
            ps.setString(1, identifier.getId());

            ps.execute();
        });
    }

}
