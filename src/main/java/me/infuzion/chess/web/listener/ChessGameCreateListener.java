package me.infuzion.chess.web.listener;

import com.google.gson.JsonObject;
import me.infuzion.chess.board.ChessBoard;
import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.event.helper.AuthenticationChecks;
import me.infuzion.chess.web.event.helper.RequestUser;
import me.infuzion.chess.web.event.helper.RequiresAuthentication;
import me.infuzion.chess.web.game.Game;
import me.infuzion.chess.web.game.User;
import me.infuzion.chess.web.game.Visibility;
import me.infuzion.chess.web.record.source.MatchDatabase;
import me.infuzion.web.server.EventListener;
import me.infuzion.web.server.event.def.PageRequestEvent;
import me.infuzion.web.server.event.def.WebSocketTextMessageEvent;
import me.infuzion.web.server.event.reflect.EventHandler;
import me.infuzion.web.server.event.reflect.Route;
import me.infuzion.web.server.event.reflect.param.mapper.impl.Response;
import me.infuzion.web.server.router.RouteMethod;

public class ChessGameCreateListener implements EventListener {
    private final MatchDatabase matchDatabase;

    public ChessGameCreateListener(MatchDatabase matchDatabase) {
        this.matchDatabase = matchDatabase;
    }

    @EventHandler(PageRequestEvent.class)
    @Route(value = "/api/v1/games/", methods = RouteMethod.POST)
    @Response("application/json")
    @RequiresAuthentication
    public JsonObject onPageReq(@RequestUser User user) {
        JsonObject response = new JsonObject();
        Game created = new Game(new Identifier(), ChessBoard.getDefaultBoard(), Visibility.PUBLIC);
        created.addPlayer(user.getIdentifier());

        matchDatabase.addMatch(created);
        response.addProperty("created", created.getGameID().getId());

        return response;
    }
}
