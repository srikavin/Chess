package me.infuzion.chess.web.game;

import me.infuzion.chess.util.ChessObject;
import me.infuzion.chess.util.Identifier;

public class User extends ChessObject {
    private final String username;
    private final long lastSeen;
    private final String bio;


    public User(Identifier identifier, String username, long lastSeen, String bio) {
        setIdentifier(identifier);
        this.username = username;
        this.lastSeen = lastSeen;
        this.bio = bio == null ? "" : bio;
    }

    public User(Identifier identifier, String username, long currentEpoch) {
        this(identifier, username, currentEpoch, null);
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
}
