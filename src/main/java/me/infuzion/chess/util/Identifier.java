package me.infuzion.chess.util;

public class Identifier {

    private final static RandomString generator = new RandomString(16);

    private final String id;

    public Identifier(String id) {
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
        return obj instanceof Identifier && id.equals(((Identifier) obj).getId());
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
