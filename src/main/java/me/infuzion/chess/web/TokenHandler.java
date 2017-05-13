package me.infuzion.chess.web;

import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.game.User;

import java.util.HashMap;
import java.util.Map;

public class TokenHandler {
    private final Map<User, Identifier> userIdentifierMap;
    private final Map<Identifier, User> identifierUserMap;

    public TokenHandler() {
        userIdentifierMap = new HashMap<>();
        identifierUserMap = new HashMap<>();
    }

    public Identifier addUser(User user) {
        Identifier identifier = userIdentifierMap.get(user);

        if (identifier == null) {
            identifier = new Identifier();
        }

        userIdentifierMap.put(user, identifier);
        identifierUserMap.put(identifier, user);

        return identifier;
    }

    public User getUser(Identifier identifier) {
        return identifierUserMap.get(identifier);
    }

    public Identifier getIdentifier(User user) {
        return userIdentifierMap.get(user);
    }
}
