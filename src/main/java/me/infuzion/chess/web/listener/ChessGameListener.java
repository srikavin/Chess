package me.infuzion.chess.web.listener;

import com.google.gson.JsonObject;
import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.Game;
import me.infuzion.chess.web.Visibility;
import me.infuzion.chess.web.event.ChessWebEvent;
import me.infuzion.chess.web.event.ChessWebListener;
import me.infuzion.chess.web.event.EndPointURL;
import me.infuzion.web.server.event.EventCondition;
import me.infuzion.web.server.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class ChessGameListener implements ChessWebListener {

    private final List<Game> waiting = new ArrayList<>();

    @EventCondition(eventMethod = "getPage")
    private boolean shouldExecute(String pageName) {
        return pageName.equals(EndPointURL.CREATE_GAME_URL) ||
                pageName.equals(EndPointURL.JOINABLE_GAMES_URL) ||
                pageName.startsWith(EndPointURL.GAME_JOIN_PREFIX);
    }


    @EventHandler
    private void onEvent(ChessWebEvent event) {
        if (!shouldExecute(event.getEvent().getPage())) {
            return;
        }
        if (event.getEvent().getPage().equals(EndPointURL.JOINABLE_GAMES_URL)) {
            setResponseJson(event.getEvent(), waiting);
            return;
        }
        if (event.getEvent().getPage().startsWith(EndPointURL.GAME_JOIN_PREFIX)) {
            String page = event.getEvent().getPage();
            String stringId = page.substring(EndPointURL.BASE_GAME_URL.length(),
                    page.length() - EndPointURL.URL_SUFFIX.length());
            System.out.println(stringId);

            Identifier id = new Identifier(stringId);
            Game game = Game.fromID(id);


            if (game != null && game.getBlackSide() == null) {
                game.addPlayer(event.getPublicUUID());
                setResponseJson(event.getEvent(), game, 200);
                return;
            }
            setResponseJson(event.getEvent(), new JsonObject(), 400);
        } else if (event.getUuidGameMap().containsKey(event.getPublicUUID())) {
            JsonObject object = new JsonObject();
            setResponseJson(event.getEvent(), object);
        } else {
            Game game = new Game(event.getPublicUUID(), Visibility.PUBLIC);
            waiting.add(game);
            event.getUuidGameMap().put(event.getPublicUUID(), game);
            JsonObject e = new JsonObject();
            e.addProperty("id", game.getGameID().getId());
            setResponseJson(event.getEvent(), e);
        }
    }
}
