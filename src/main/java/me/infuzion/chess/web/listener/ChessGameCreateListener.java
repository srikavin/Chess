package me.infuzion.chess.web.listener;

import com.google.gson.JsonObject;
import me.infuzion.chess.board.ChessBoard;
import me.infuzion.chess.util.ChessUtilities;
import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.event.AuthenticatedPageRequestEvent;
import me.infuzion.chess.web.event.AuthenticatedWebSocketEvent;
import me.infuzion.chess.web.game.Game;
import me.infuzion.chess.web.game.Visibility;
import me.infuzion.chess.web.record.source.MatchDatabase;
import me.infuzion.web.server.EventListener;
import me.infuzion.web.server.event.reflect.EventHandler;
import me.infuzion.web.server.event.reflect.Route;

public class ChessGameCreateListener implements EventListener {
    private final MatchDatabase matchDatabase;

    public ChessGameCreateListener(MatchDatabase matchDatabase) {
        this.matchDatabase = matchDatabase;
    }

    @EventHandler
    @Route(path = "/api/v1/games/create.json")
    public void onPageReq(AuthenticatedPageRequestEvent event) {
        JsonObject response = new JsonObject();
        System.out.println("called");
        Game created = new Game(new Identifier(), ChessBoard.getDefaultBoard(), Visibility.PUBLIC);
        created.addPlayer(event.getId());

        matchDatabase.addMatch(created);
        response.addProperty("created", created.getGameID().getId());
        event.getEvent().setResponseData(ChessUtilities.gson.toJson(response));
    }

    @EventHandler
    public void onPageRequest(AuthenticatedWebSocketEvent event) {
        JsonObject response = new JsonObject();
        if (event.getParsed().get("request").getAsString().equals("create")) {
            Game created = new Game(new Identifier(), ChessBoard.getDefaultBoard(), Visibility.PUBLIC);
            created.addPlayer(event.getUserIdentifier());

            matchDatabase.addMatch(created);
            response.addProperty("created", created.getGameID().getId());
            event.getEvent().addMessage(ChessUtilities.gson.toJson(response));
        }
    }
}
