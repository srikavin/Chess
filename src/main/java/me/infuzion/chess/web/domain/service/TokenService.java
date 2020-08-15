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

package me.infuzion.chess.web.domain.service;

import me.infuzion.chess.game.util.Identifier;
import me.infuzion.chess.web.dao.UserDao;
import me.infuzion.chess.web.domain.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class TokenService {
    private final JedisPool pool;
    private final UserDao userDao;

    public TokenService(JedisPool pool, UserDao userDao) {
        this.userDao = userDao;
        this.pool = pool;
    }

    public Identifier addUser(@NotNull User user) {
        Identifier identifier = new Identifier();

        try (Jedis client = pool.getResource()) {
            client.set("chess.auth.token." + identifier.getId(), user.getIdentifier().getId());
        }

        return identifier;
    }

    public @Nullable User getUser(@NotNull Identifier token) {
        try (Jedis client = pool.getResource()) {
            String val = client.get("chess.auth.token." + token.getId());

            if (val == null) {
                return null;
            }

            return userDao.getUser(new Identifier(val));
        }
    }
}
