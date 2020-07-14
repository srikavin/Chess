package me.infuzion.chess.util;

import org.jetbrains.annotations.NotNull;

public class Identifier {

    private final static RandomStringGenerator generator = new RandomStringGenerator(16);

    private final String id;

    public Identifier(@NotNull String id) {
        this.id = id;
    }

    public Identifier() {
        id = generator.nextString();
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Identifier && id.equals(((Identifier) obj).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id;
    }
}
