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

import me.infuzion.chess.game.util.Identifier;
import me.infuzion.chess.web.dao.UserDao;
import me.infuzion.chess.web.domain.User;
import me.infuzion.chess.web.domain.UserRole;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mindrot.jbcrypt.BCrypt;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

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
                + "IMAGE_PATH bytea default '/images/unknown.png',"
                + "LAST_LOGIN timestamp with time zone default now())");

        createUser(new Identifier(), "testing", "abc", null, UserRole.USER);
    }

    private @NotNull User mapUser(ResultSet set) throws SQLException {
        String id = set.getString("ID");
        String username = set.getString("USERNAME");
        Instant lastLogin = set.getTimestamp("LAST_LOGIN").toInstant();
        String bio = set.getString("BIO");
        String imagePath = set.getString("IMAGE_PATH");
        UserRole role = UserRole.valueOf(set.getString("ROLE"));

        return new User(new Identifier(id), username, lastLogin, bio, imagePath, role);
    }

    @Override
    public User createUser(Identifier id, String username, String password, String bio, UserRole role) {
        return DBHelper.prepareStatement(source, "INSERT INTO USERS(ID, USERNAME, PASSWORD, LAST_LOGIN, BIO, ROLE) VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING", ps -> {
            String hashed = BCrypt.hashpw(password, BCrypt.gensalt(12));
            ps.setString(1, id.getId());
            ps.setString(2, username);
            ps.setString(3, hashed);
            ps.setTimestamp(4, Timestamp.from(Instant.now()));
            ps.setString(5, bio);
            ps.setString(6, role.toString());

            if (ps.executeUpdate() == 0) {
                return null;
            }

            return getUser(id.getId());
        });
    }

    public void updateImagePath(Identifier user, String path) {
        DBHelper.prepareStatement(source, "UPDATE USERS SET IMAGE_PATH = ? WHERE ID = ?", ps -> {
            ps.setString(1, path);
            ps.setString(2, user.getId());

            ps.executeUpdate();
        });
    }

    @Override
    public @Nullable User checkLoginAndGetUser(String username, String password) {
        return DBHelper.transaction(source, conn -> {
            return DBHelper.prepareStatement(conn, "SELECT * FROM USERS WHERE USERNAME = ?", (ps) -> {
                ps.setString(1, username);

                return DBHelper.mapFirstElement(ps.executeQuery(), (rs) -> {
                    String id = rs.getString("ID");
                    String hashed = rs.getString("PASSWORD");
                    String bio = rs.getString("BIO");
                    String imagePath = rs.getString("IMAGE_PATH");
                    UserRole role = UserRole.valueOf(rs.getString("ROLE"));

                    if (!BCrypt.checkpw(password, hashed)) {
                        return null;
                    }

                    Instant now = Instant.now();

                    DBHelper.prepareStatement(conn, "UPDATE USERS SET LAST_LOGIN = ? WHERE ID = ?", (lastSeenPS) -> {
                        // update user last login
                        lastSeenPS.setTimestamp(1, Timestamp.from(now));
                        lastSeenPS.setString(2, id);
                        lastSeenPS.executeUpdate();
                    });

                    return new User(new Identifier(id), username, now, bio, imagePath, role);
                });
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
