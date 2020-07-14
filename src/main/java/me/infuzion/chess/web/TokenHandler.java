package me.infuzion.chess.web;

import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.domain.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            identifier = new Identifier("VkpCLJmaVDYi3f7D");
        }

        userIdentifierMap.put(user, identifier);
        identifierUserMap.put(identifier, user);

        return identifier;
    }

    public @Nullable User getUser(@NotNull Identifier identifier) {
        return identifierUserMap.get(identifier);
    }

    public @NotNull Identifier getIdentifier(@NotNull User user) {
        return userIdentifierMap.get(user);
    }
}
