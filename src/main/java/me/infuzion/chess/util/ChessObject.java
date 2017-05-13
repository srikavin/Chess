package me.infuzion.chess.util;

public abstract class ChessObject {
    private Object id;
    private transient Identifier identifier;

    public final Identifier getIdentifier() {
        return identifier;
    }

    protected final void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
        this.id = identifier.getId();
    }
}
