package me.infuzion.chess.web;

import me.infuzion.chess.util.Identifier;

public class User {
    private final String username;
    private final Identifier identifier;
    private final long lastSeen;
    private final String bio;


    public User(Identifier identifier, String username, long lastSeen, String bio) {
        this.identifier = identifier;
        this.username = username;
        this.lastSeen = lastSeen;
        this.bio = bio == null ? "" : bio;
    }

    public User(Identifier identifier, String username, long currentEpoch) {
        this(identifier, username, currentEpoch, null);
    }

    public Identifier getIdentifier() {
        return identifier;
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
