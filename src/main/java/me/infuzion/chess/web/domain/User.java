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

package me.infuzion.chess.web.domain;

import com.google.gson.JsonObject;
import me.infuzion.chess.game.util.ChessObject;
import me.infuzion.chess.game.util.Identifier;

public class User extends ChessObject {
    private final String username;
    private final long lastSeen;
    private final String bio;
    private final String imagePath;

    public User(Identifier identifier, String username, long lastSeen, String bio, String imagePath) {
        setIdentifier(identifier);
        this.imagePath = imagePath;
        this.username = username;
        this.lastSeen = lastSeen;
        this.bio = bio == null ? "" : bio;
    }

    public User(Identifier identifier, String username, long currentEpoch, String imagePath) {
        this(identifier, username, currentEpoch, null, imagePath);
    }

    public String getUsername() {
        return username;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public String getBio() {
        return bio;
    }

    public String getImagePath() {
        return imagePath;
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("id", this.getIdentifier().getId());
        object.addProperty("username", this.getUsername());
        object.addProperty("bio", this.getBio());
        object.addProperty("lastSeen", this.getLastSeen());
        return object;
    }
}
