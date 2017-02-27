package me.infuzion.chess.util;

public class GameIdentifier {

    private final static RandomString generator = new RandomString(16);

    private final String id;

    public GameIdentifier(String id) {
        this.id = id;
    }

    public GameIdentifier() {
        id = generator.nextString();
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GameIdentifier) {
            return id.equals(((GameIdentifier) obj).getId());
        }
        return false;
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
