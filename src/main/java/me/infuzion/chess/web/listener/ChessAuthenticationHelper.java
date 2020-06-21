package me.infuzion.chess.web.listener;

import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.TokenHandler;
import me.infuzion.chess.web.game.User;
import me.infuzion.web.server.EventListener;
import me.infuzion.web.server.event.Event;
import me.infuzion.web.server.event.reflect.param.DefaultTypeConverter;
import me.infuzion.web.server.event.reflect.param.HasBody;
import me.infuzion.web.server.event.reflect.param.HasHeaders;
import me.infuzion.web.server.event.reflect.param.TypeConverter;
import me.infuzion.web.server.http.parser.BodyData;

public class ChessAuthenticationHelper implements EventListener {

    private final TokenHandler tokenHandler;
    private final TypeConverter converter = new DefaultTypeConverter();

    public ChessAuthenticationHelper(TokenHandler tokenHandler) {
        this.tokenHandler = tokenHandler;
    }

    private String checkBody(HasBody event) {
        var fields = event.getBodyData().getFields();

        BodyData.BodyField tokenField = fields.get("token");

        if (tokenField == null) {
            return null;
        }

        return converter.deserialize(tokenField.getContent(), String.class);
    }

    private String checkHeader(HasBody event) {
        if (!(event instanceof HasHeaders)) {
            return null;
        }

        return ((HasHeaders) event).getRequestHeaders().get("x-api-key");
    }

    public User getUser(Event event) {
        String token = null;
        if (event instanceof HasBody) {
            token = checkBody((HasBody) event);
        }

        if (token == null && event instanceof HasHeaders) {
            token = checkHeader((HasBody) event);
        }

        if (token == null) {
            return null;
        }

        System.out.println(token);

        return tokenHandler.getUser(new Identifier(token));
    }

//    @EventHandler
//    public void onPageRequest(PageRequestEvent event) throws Exception {
//        JsonObject response = new JsonObject();
//        if (event.getRequestHeaders().containsKey("x-api-key")) {
//            String token = event.getRequestHeaders().get("x-api-key");
//            Identifier identifier = new Identifier(token);
//            User user = tokenHandler.getUser(identifier);
//
//            if (user == null) {
//                response.addProperty("error", "invalid token");
//                event.setResponseBody(ChessUtilities.gson.toJson(response));
//                return;
//            }
//
//            AuthenticatedPageRequestEvent newEvent = new AuthenticatedPageRequestEvent(event, user);
//            manager.fireEvent(newEvent);
//        }
//    }
//
//    @EventHandler
//    public void onMessage(WebSocketMessageEvent event) {
//        JsonObject response = new JsonObject();
//        JsonObject object;
//        try {
//            object = JsonParser.parseString(event.getRequestData()).getAsJsonObject();
//        } catch (Exception e) {
//            return;
//        }
//
//        if (object.has("token")) {
//            String token = object.get("token").getAsString();
//            if (token == null) {
//                response.addProperty("error", "token not specified");
//                event.getClient().send(ChessUtilities.gson.toJson(response));
//                return;
//            }
//            Identifier identifier = new Identifier(token);
//            User user = tokenHandler.getUser(identifier);
//
//            if (user == null) {
//                response.addProperty("error", "invalid token");
//                event.getClient().send(ChessUtilities.gson.toJson(response));
//                return;
//            }
//
//            AuthenticatedWebSocketEvent newEvent = new AuthenticatedWebSocketEvent(event, user);
//            manager.fireEvent(newEvent);
//        }
//    }
}
