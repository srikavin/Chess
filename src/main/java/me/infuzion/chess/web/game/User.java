package me.infuzion.chess.web.game;

import com.google.gson.JsonObject;
import me.infuzion.chess.util.ChessObject;
import me.infuzion.chess.util.Identifier;

public class User extends ChessObject {
    private final String username;
    private final long lastSeen;
    private final String bio;
    private final String imagePath;

    public User(Identifier identifier, String username, long lastSeen, String bio, String imagePath) {
        this.imagePath = imagePath;
        setIdentifier(identifier);
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
