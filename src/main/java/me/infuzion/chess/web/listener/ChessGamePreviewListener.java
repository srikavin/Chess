package me.infuzion.chess.web.listener;

import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.game.Game;
import me.infuzion.chess.web.record.source.MatchDatabase;
import me.infuzion.web.server.EventListener;
import me.infuzion.web.server.event.def.PageRequestEvent;
import me.infuzion.web.server.event.reflect.EventHandler;
import me.infuzion.web.server.event.reflect.Route;
import me.infuzion.web.server.event.reflect.param.mapper.impl.Response;
import me.infuzion.web.server.event.reflect.param.mapper.impl.UrlParam;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class ChessGamePreviewListener implements EventListener {
    private final MatchDatabase database;

    public ChessGamePreviewListener(MatchDatabase database) {
        this.database = database;
    }

    @EventHandler
    @Route("/api/v1/games/:game_id/preview")
    @Response(value = "image/png", raw = true)
    public byte[] onRequest(PageRequestEvent event, @UrlParam("game_id") String game_id) {
        Identifier id = new Identifier(game_id);
        Game game = database.getMatch(id);
        if (game != null) {
            return game.generateThumbnail();
        } else {
            try {
                return IOUtils.toByteArray(getClass().getResourceAsStream("/images/error/match-not-found.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
