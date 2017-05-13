package me.infuzion.chess.web.event;

import me.infuzion.chess.web.game.Game;
import me.infuzion.web.server.event.Event;
import me.infuzion.web.server.event.def.PageRequestEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChessWebEvent extends Event {

    private final PageRequestEvent event;
    private final UUID publicUUID;
    private final Map<UUID, Game> uuidGameMap;

    public ChessWebEvent(PageRequestEvent event, UUID publicUUID, Map<UUID, Game> uuidGameMap) {
        this.event = event;
        this.publicUUID = publicUUID;
        this.uuidGameMap = new HashMap<>(uuidGameMap);
    }

    public UUID getSessionUUID() {
        return publicUUID;
    }

    public Map<UUID, Game> getUuidGameMap() {
        return uuidGameMap;
    }

    public PageRequestEvent getEvent() {
        return event;
    }
}
