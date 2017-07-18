package me.infuzion.chess.web.record;

import me.infuzion.chess.util.Identifier;

public class RecordIdentifier {
    private final String resourceName;
    private final Identifier identifier;

    public RecordIdentifier(String resourceName, Identifier identifier) {
        this.resourceName = resourceName;
        this.identifier = identifier;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public String getResourceName() {
        return resourceName;
    }
}
