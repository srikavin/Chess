/*
 * Copyright 2020 Srikavin Ramkumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.infuzion.chess.web.listener.game;

import com.google.gson.JsonObject;
import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.dao.impl.MatchDatabase;
import me.infuzion.chess.web.dao.impl.UserDatabase;
import me.infuzion.chess.web.domain.Game;
import me.infuzion.chess.web.domain.GamePreviewGenerator;
import me.infuzion.chess.web.domain.service.GameService;
import me.infuzion.chess.web.record.RecordSet;
import me.infuzion.web.server.EventListener;
import me.infuzion.web.server.event.def.PageRequestEvent;
import me.infuzion.web.server.event.reflect.EventHandler;
import me.infuzion.web.server.event.reflect.Route;
import me.infuzion.web.server.event.reflect.param.mapper.impl.QueryParam;
import me.infuzion.web.server.event.reflect.param.mapper.impl.Response;
import me.infuzion.web.server.event.reflect.param.mapper.impl.UrlParam;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;

public class ChessGameListener implements EventListener {
    private final MatchDatabase matchDatabase;
    private final GameService gameService;
    private final UserDatabase userDatabase;
    private final RecordSet<Game> recordSet;

    private final JsonObject invalidGameIdError;

    {
        invalidGameIdError = new JsonObject();
        invalidGameIdError.addProperty("error", "invalid game id");
    }

    public ChessGameListener(MatchDatabase matchDatabase, GameService gameService, UserDatabase userDatabase) {
        this.matchDatabase = matchDatabase;
        this.gameService = gameService;
        this.userDatabase = userDatabase;
        recordSet = new RecordSet<>("games", this.matchDatabase);
    }

    @EventHandler
    @Route("/api/v1/games/")
    @Response("application/json")
    public List<Game> multipleGames(PageRequestEvent event, @QueryParam("limit") Integer limit, @QueryParam("user") String user) {

        if (limit == null) {
            limit = 100;
        }

        if (user != null) {
            return gameService.getRecentGameForUser(new Identifier(user), limit);
        }

        return gameService.getActiveGames(limit);
    }

    @EventHandler(PageRequestEvent.class)
    @Route("/api/v1/games/:game_id/preview")
    @Response(value = "image/png", raw = true)
    public byte[] gamePreview(@UrlParam("game_id") String game_id) {
        Identifier id = new Identifier(game_id);
        Game game = gameService.getGame(id);
        if (game != null) {
            return GamePreviewGenerator.generateThumbnail(game);
        }

        try {
            return IOUtils.toByteArray(getClass().getResourceAsStream("/images/error/match-not-found.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler(PageRequestEvent.class)
    @Route("/api/v1/games/:game_id/")
    @Response("application/json")
    private Game singleGame(@UrlParam("game_id") String game_id) {
        return gameService.getGame(new Identifier(game_id));
    }
}
