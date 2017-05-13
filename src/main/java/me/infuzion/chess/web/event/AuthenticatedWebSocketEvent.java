package me.infuzion.chess.web.event;

import com.google.gson.JsonObject;
import me.infuzion.chess.util.ChessUtilities;
import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.game.User;
import me.infuzion.web.server.event.Event;
import me.infuzion.web.server.event.def.WebSocketMessageEvent;

public class AuthenticatedWebSocketEvent extends Event {
    private final WebSocketMessageEvent event;
    private final User user;
    private final Identifier userIdentifier;
    private final JsonObject parsed;
    private final String request;


    public AuthenticatedWebSocketEvent(WebSocketMessageEvent event, User user) {
        this.event = event;
        this.user = user;
        this.userIdentifier = user.getIdentifier();
        this.parsed = ChessUtilities.parser.parse(event.getPayload()).getAsJsonObject();
        this.request = parsed.get("request").getAsString();
    }

    public Identifier getUserIdentifier() {
        return userIdentifier;
    }

    public User getUser() {
        return user;
    }

    public WebSocketMessageEvent getEvent() {
        return event;
    }

    public JsonObject getParsed() {
        return parsed;
    }

    public String getRequest() {
        return request;
    }
}
