package me.infuzion.chess.web.event;

import me.infuzion.chess.util.Identifier;
import me.infuzion.web.server.event.AbstractEvent;
import me.infuzion.web.server.event.def.PageRequestEvent;

public class ChessGameInfoEvent extends AbstractEvent {
    private final PageRequestEvent event;
    private final String fullRequest;
    private final String request;
    private final String idString;
    private final Identifier id;

    public ChessGameInfoEvent(PageRequestEvent event, String basePath) {
        this.event = event;
        fullRequest = event.getPath().substring(basePath.length());
        String[] split = fullRequest.split("/", 3);
        if (split.length < 2) {
            throw new RuntimeException("Size is invalid");
        }
        if (split.length == 2) {
            request = "/";
        } else {
            request = "/" + split[2];
        }
        idString = split[1];
        id = new Identifier(idString);
    }

    public String getFullRequest() {
        return fullRequest;
    }

    public String getIdString() {
        return idString;
    }

    public Identifier getId() {
        return id;
    }

    public PageRequestEvent getEvent() {
        return event;
    }

    public String getRequest() {
        return request;
    }
}
