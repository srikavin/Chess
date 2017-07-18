package me.infuzion.chess.web.listener;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.infuzion.chess.board.ChessPosition;
import me.infuzion.chess.util.ChessUtilities;
import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.event.AuthenticatedWebSocketEvent;
import me.infuzion.chess.web.event.EndPointURL;
import me.infuzion.chess.web.game.Game;
import me.infuzion.chess.web.game.User;
import me.infuzion.chess.web.record.source.MatchDatabase;
import me.infuzion.web.server.EventListener;
import me.infuzion.web.server.event.def.WebSocketMessageEvent;
import me.infuzion.web.server.event.reflect.EventHandler;
import me.infuzion.web.server.event.reflect.EventPriority;

@SuppressWarnings("UnnecessaryReturnStatement")
public class ChessMoveListener implements EventListener {

    private static final Gson gson = ChessUtilities.gson;
    private final MatchDatabase matchDatabase;

    public ChessMoveListener(MatchDatabase database) {
        this.matchDatabase = database;
    }

    @EventHandler(priority = EventPriority.END)
    private void unknownRequest(WebSocketMessageEvent event) {
        if (event.getToSendToAll().size() > 0 || event.getToSend().size() > 0) {
            return;
        }
        JsonObject response = new JsonObject();
        response.addProperty("error", "unknown request");
        event.addMessage(gson.toJson(response));
    }

    @EventHandler
    private void onJoinRequest(AuthenticatedWebSocketEvent event) {
        JsonObject response = new JsonObject();
        String page = event.getEvent().getPage();
        if (!page.startsWith(EndPointURL.WEBSOCKET_GAME_URL)
                || !event.getRequest().equals("join")) {
            return;
        }
        String id = page.substring(EndPointURL.WEBSOCKET_GAME_URL.length());

        User user = event.getUser();
        Game game = matchDatabase.getMatch(new Identifier(id));
        if (game == null) {
            response.addProperty("error", "not found");
            event.getEvent().addMessage(gson.toJson(response));
            return;
        } else {
            if (game.addPlayer(user.getIdentifier())) {
                response.addProperty("added", game.getGameID().getId());
                event.getEvent().addSendToAll(gson.toJson(response));
            } else {
                response.addProperty("error", "no slots");
                event.getEvent().addMessage(gson.toJson(response));
            }
            matchDatabase.updateMatch(game);
        }
    }

    @EventHandler
    private void onSubmitRequest(AuthenticatedWebSocketEvent event) {
        if (!event.getParsed().get("request").getAsString().equals("submit")) {
            return;
        }
        JsonObject response = new JsonObject();
        String source = event.getParsed().get("source").getAsString();
        String end = event.getParsed().get("end").getAsString();
        String id = event.getEvent().getPage().substring(EndPointURL.WEBSOCKET_GAME_URL.length());

        Game game = matchDatabase.getMatch(new Identifier(id));
        if (game == null) {
            response.addProperty("error", "not found");
            event.getEvent().addMessage(gson.toJson(response));
            return;
        }
        if (source.length() != 2 || end.length() != 2) {
            return;
        }

        ChessPosition startPos = new ChessPosition(source);
        ChessPosition endPos = new ChessPosition(end);

        if (!game.move(event.getUserIdentifier(), startPos, endPos)) {
            response.addProperty("move", "invalid");
        }
        event.getEvent().addMessage(gson.toJson(response));
        event.getEvent().addSendToAll(gson.toJson(generateResponse(game)));
        matchDatabase.updateMatch(game);
        return;
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
    private void onMessage(WebSocketMessageEvent event) {
        JsonObject object;
        JsonObject response = new JsonObject();
        try {
            object = ChessUtilities.parser.parse(event.getPayload()).getAsJsonObject();
        } catch (Exception e) {
            response.addProperty("error", "invalid json");
            event.addMessage(gson.toJson(response));
            return;
        }
        String page = event.getPage();
        if (!page.startsWith(EndPointURL.WEBSOCKET_GAME_URL)) {
            return;
        }
        String id = page.substring(EndPointURL.WEBSOCKET_GAME_URL.length());
        Identifier identifier = new Identifier(id);

        Game game = matchDatabase.getMatch(identifier);
        if (game == null) {
            response.addProperty("error", "not found");
            event.addMessage(gson.toJson(response));
            return;
        }

        String request = object.get("request").getAsString();
        if (request.equals("update")) {
            event.addSendToAll(gson.toJson(generateResponse(game)));
            return;
        }
    }
}
