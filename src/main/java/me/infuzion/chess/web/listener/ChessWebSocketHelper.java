package me.infuzion.chess.web.listener;

import com.google.gson.JsonObject;
import me.infuzion.chess.util.ChessUtilities;
import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.TokenHandler;
import me.infuzion.chess.web.event.AuthenticatedWebSocketEvent;
import me.infuzion.chess.web.game.User;
import me.infuzion.web.server.EventListener;
import me.infuzion.web.server.event.EventManager;
import me.infuzion.web.server.event.def.WebSocketMessageEvent;
import me.infuzion.web.server.event.reflect.EventHandler;

public class ChessWebSocketHelper implements EventListener {

    private final TokenHandler tokenHandler;
    private final EventManager manager;

    public ChessWebSocketHelper(TokenHandler tokenHandler, EventManager manager) {
        this.tokenHandler = tokenHandler;
        this.manager = manager;
    }

    @EventHandler
    public void onMessage(WebSocketMessageEvent event) {
        JsonObject response = new JsonObject();
        JsonObject object;
        try {
            object = ChessUtilities.parser.parse(event.getPayload()).getAsJsonObject();
        } catch (Exception e) {
            return;
        }

        if (object.has("token")) {
            String token = object.get("token").getAsString();
            if (token == null) {
                response.addProperty("error", "token not specified");
                event.addMessage(ChessUtilities.gson.toJson(response));
                return;
            }
            Identifier identifier = new Identifier(token);
            User user = tokenHandler.getUser(identifier);

            if (user == null) {
                response.addProperty("error", "invalid token");
                event.addMessage(ChessUtilities.gson.toJson(response));
                return;
            }

            AuthenticatedWebSocketEvent newEvent = new AuthenticatedWebSocketEvent(event, user);
            manager.fireEvent(newEvent);
        }
    }
}
