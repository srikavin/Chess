package me.infuzion.chess.web.listener;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.infuzion.chess.util.ChessUtilities;
import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.util.MatchDatabase;
import me.infuzion.chess.web.Game;
import me.infuzion.chess.web.event.ChessWebEvent;
import me.infuzion.chess.web.event.ChessWebListener;
import me.infuzion.chess.web.event.EndPointURL;
import me.infuzion.web.server.event.EventHandler;

import java.util.Map;
import java.util.UUID;

public class ChessDataListener implements ChessWebListener {
    private final MatchDatabase database;

    public ChessDataListener(MatchDatabase database) {
        this.database = database;
    }

    private boolean shouldExecute(String pageName) {
        return pageName.startsWith(EndPointURL.BASE_GAME_URL) || pageName
                .equals(EndPointURL.CHESS_URL);
    }

    private void onChessURL(ChessWebEvent event, UUID publicUUID, Map<UUID, Game> uuidGameMap) {
        Game currentGame = uuidGameMap.get(publicUUID);
        if (currentGame == null) {
            setResponseJson(event.getEvent(), new JsonObject());
            return;
        }
        JsonElement e = ChessUtilities.gson.toJsonTree(uuidGameMap.get(publicUUID));
        e.getAsJsonObject().add("uuid", ChessUtilities.gson.toJsonTree(publicUUID));
        setResponseJson(event.getEvent(), e);
    }

    private void onGameURL(ChessWebEvent event, UUID publicUUID, Map<UUID, Game> uuidGameMap) {
        String pageName = event.getEvent().getPage();
        String id = pageName.substring(EndPointURL.BASE_GAME_URL.length()).split("\\.")[0];
        Identifier gameId = new Identifier(id);
        Game game = Game.fromID(gameId);
        if (game == null) {
            game = database.getMatch(gameId);
        }
        if (game == null) {
            setResponseJson(event.getEvent(), new JsonObject(), 404);
            return;
        }

        if (pageName.endsWith(EndPointURL.URL_SUFFIX)) {
            if (game.getWhiteSide() != publicUUID) {
                game.addPlayer(publicUUID);
                uuidGameMap.put(publicUUID, game);
            }
            setResponseJson(event.getEvent(), game);
        } else {
            setResponse(event.getEvent(),
                    getClass().getResourceAsStream("/web/play.html"), 200, "html");
        }
    }

    @EventHandler
    private void onEvent(ChessWebEvent event) {
        String pageName = event.getEvent().getPage();
        if (!shouldExecute(pageName)) {
            return;
        }

        UUID publicUUID = event.getPublicUUID();
        Map<UUID, Game> uuidGameMap = event.getUuidGameMap();

        if (pageName.equals(EndPointURL.CHESS_URL)) {
            onChessURL(event, publicUUID, uuidGameMap);
        } else if (pageName.startsWith(EndPointURL.BASE_GAME_URL)) {
            onGameURL(event, publicUUID, uuidGameMap);
        }
    }
}
