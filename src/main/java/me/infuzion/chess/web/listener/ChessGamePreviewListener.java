package me.infuzion.chess.web.listener;

import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.event.ChessWebListener;
import me.infuzion.chess.web.game.Game;
import me.infuzion.chess.web.record.source.MatchDatabase;
import me.infuzion.web.server.event.def.PageRequestEvent;
import me.infuzion.web.server.event.reflect.EventHandler;
import me.infuzion.web.server.event.reflect.Route;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Map;

public class ChessGamePreviewListener implements ChessWebListener {
    private final MatchDatabase database;

    public ChessGamePreviewListener(MatchDatabase database) {
        this.database = database;
    }

    @EventHandler
    @Route(path = "/api/v1/games/:game_id/preview")
    public void onRequest(PageRequestEvent event, Map<String, String> map) {
        Identifier id = new Identifier(map.get("game_id"));
        Game game = database.getMatch(id);
        if (game != null) {
            event.setResponseData(game.generateThumbnail());
            event.setStatusCode(200);
        } else {
            byte[] notFoundError;
            try {
                notFoundError = IOUtils.toByteArray(getClass().getResourceAsStream("/images/error/match-not-found.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            event.setResponseData(notFoundError);
            event.setStatusCode(404);
        }
        event.setContentType("image/png");
    }
}
