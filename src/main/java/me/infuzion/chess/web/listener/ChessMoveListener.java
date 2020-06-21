package me.infuzion.chess.web.listener;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.infuzion.chess.board.ChessPosition;
import me.infuzion.chess.util.ChessUtilities;
import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.event.helper.AuthenticationChecks;
import me.infuzion.chess.web.event.helper.RequestUser;
import me.infuzion.chess.web.event.helper.RequiresAuthentication;
import me.infuzion.chess.web.game.Game;
import me.infuzion.chess.web.game.User;
import me.infuzion.chess.web.record.source.MatchDatabase;
import me.infuzion.web.server.EventListener;
import me.infuzion.web.server.event.EventManager;
import me.infuzion.web.server.event.def.WebSocketMessageEvent;
import me.infuzion.web.server.event.def.WebSocketTextMessageEvent;
import me.infuzion.web.server.event.reflect.EventHandler;
import me.infuzion.web.server.event.reflect.EventPriority;
import me.infuzion.web.server.event.reflect.Route;
import me.infuzion.web.server.event.reflect.param.mapper.impl.BodyParam;
import me.infuzion.web.server.event.reflect.param.mapper.impl.UrlParam;
import me.infuzion.web.server.websocket.WebsocketClient;
import me.infuzion.web.server.websocket.WebsocketRoom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChessMoveListener implements EventListener {

    private static final Gson gson = ChessUtilities.gson;
    private final MatchDatabase matchDatabase;
    private final EventManager eventManager;
    private final Map<Identifier, WebsocketRoom> gameListeners = new ConcurrentHashMap<>();

    public ChessMoveListener(MatchDatabase database, EventManager eventManager) {
        this.matchDatabase = database;
        this.eventManager = eventManager;
    }

    @EventHandler(priority = EventPriority.END)
    private void unknownRequest(WebSocketMessageEvent event) {
//        if (event.getToSendToAll().size() > 0 || event.getToSend().size() > 0) {
//            return;
//        }
//        JsonObject response = new JsonObject();
//        response.addProperty("error", "unknown request");
//        event.addMessage(gson.toJson(response));
    }

    @EventHandler
    @RequiresAuthentication(value = AuthenticationChecks.REQUEST, request = "join")
    @Route("/games/:id")
    private JsonObject onJoinRequest(WebSocketTextMessageEvent event, @RequestUser User user, @UrlParam("id") String id) {
        JsonObject response = new JsonObject();

        Game game = matchDatabase.getMatch(new Identifier(id));

        if (game == null) {
            response.addProperty("error", "not found");
            return response;
        }

        gameListeners.computeIfAbsent(game.getGameID(), k -> new WebsocketRoom(eventManager));
        WebsocketRoom room = gameListeners.get(game.getGameID());

        if (game.addPlayer(user.getIdentifier())) {
            response.addProperty("added", game.getGameID().getId());
            room.addClient(event.getClient());
            room.sendToAll(gson.toJson(response));
        } else {
            response.addProperty("error", "no slots");
            event.getClient().send(gson.toJson(response));
        }

        matchDatabase.updateMatch(game);

        response.addProperty("error", false);
        return response;
    }

    @EventHandler(WebSocketTextMessageEvent.class)
    @Route("/game/:id")
    @RequiresAuthentication(value = AuthenticationChecks.REQUEST, request = "submit")
    private void onSubmitRequest(WebSocketTextMessageEvent event, @RequestUser User user, @BodyParam("source") String source, @BodyParam("end") String end, @UrlParam("id") String id) {
        JsonObject response = new JsonObject();
        WebsocketClient client = event.getClient();

        Game game = matchDatabase.getMatch(new Identifier(id));
        if (game == null) {
            response.addProperty("error", "not found");
            client.send(gson.toJson(response));
            return;
        }
        if (source.length() != 2 || end.length() != 2) {
            return;
        }

        WebsocketRoom room = gameListeners.get(game.getGameID());

        ChessPosition startPos = new ChessPosition(source);
        ChessPosition endPos = new ChessPosition(end);

        if (!game.move(user.getIdentifier(), startPos, endPos)) {
            response.addProperty("move", "invalid");
        }

        client.send(gson.toJson(response));
        room.sendToAll(gson.toJson(generateResponse(game)));

        matchDatabase.updateMatch(game);
    }

    private JsonObject generateResponse(Game game) {

        JsonObject response = new JsonObject();
        response.addProperty("game", game.getBoard().toFen());

        Identifier white = game.getWhiteSide();
        JsonObject whiteSide = new JsonObject();
        whiteSide.addProperty("present", white != null);
        if (white != null) {
            whiteSide.addProperty("id", white.getId());
        }

        JsonObject blackSide = new JsonObject();
        Identifier black = game.getBlackSide();

        blackSide.addProperty("present", black != null);
        if (black != null) {
            blackSide.addProperty("id", black.getId());
        }

        JsonObject players = new JsonObject();
        players.add("white", whiteSide);
        players.add("black", blackSide);

        response.add("players", players);
        return response;
    }

    @EventHandler
    @Route("/games/:id")
    private void onMessage(WebSocketMessageEvent event, @UrlParam("id") String id) {
        JsonObject object;
        JsonObject response = new JsonObject();

        WebsocketClient client = event.getClient();

        try {
            object = JsonParser.parseString(event.getRequestData()).getAsJsonObject();
        } catch (Exception e) {
            response.addProperty("error", "invalid json");
            client.remove();
            client.send(gson.toJson(response));
            return;
        }

        Identifier identifier = new Identifier(id);

        Game game = matchDatabase.getMatch(identifier);
        if (game == null) {
            response.addProperty("error", "not found");
            client.send(gson.toJson(response));
            return;
        }

        WebsocketRoom room = gameListeners.get(game.getGameID());

        String request = object.get("request").getAsString();
        if (request.equals("update")) {
            room.sendToAll(gson.toJson(generateResponse(game)));
        }
    }
}
