package me.infuzion.chess.web.listener;

import com.google.gson.JsonObject;
import me.infuzion.chess.board.ChessBoard;
import me.infuzion.chess.util.ChessUtilities;
import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.util.MatchDatabase;
import me.infuzion.chess.web.TokenHandler;
import me.infuzion.chess.web.event.AuthenticatedWebSocketEvent;
import me.infuzion.chess.web.game.Game;
import me.infuzion.chess.web.game.Visibility;
import me.infuzion.web.server.EventListener;
import me.infuzion.web.server.event.reflect.EventHandler;

public class ChessGameCreateListener implements EventListener {
    private final MatchDatabase matchDatabase;
    private final TokenHandler tokenHandler;

    public ChessGameCreateListener(MatchDatabase matchDatabase, TokenHandler tokenHandler) {
        this.matchDatabase = matchDatabase;
        this.tokenHandler = tokenHandler;
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
