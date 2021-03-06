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

package me.infuzion.chess.web.dao;

import me.infuzion.chess.game.util.Identifier;
import me.infuzion.chess.web.domain.User;
import me.infuzion.chess.web.domain.UserRole;
import org.jetbrains.annotations.Nullable;

public interface UserDao {
    @Nullable
    User getUser(Identifier id);

    @Nullable
    User getUser(String username);

    @Nullable
    User checkLoginAndGetUser(String username, String password);

    @Nullable
    User createUser(Identifier id, String username, String password, String bio, UserRole role);

    void deleteUser(Identifier identifier);
}
