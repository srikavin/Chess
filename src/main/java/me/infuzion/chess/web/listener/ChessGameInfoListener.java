package me.infuzion.chess.web.listener;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.game.Game;
import me.infuzion.chess.web.game.User;
import me.infuzion.chess.web.record.RecordSet;
import me.infuzion.chess.web.record.source.MatchDatabase;
import me.infuzion.chess.web.record.source.UserDatabase;
import me.infuzion.web.server.EventListener;
import me.infuzion.web.server.event.def.PageRequestEvent;
import me.infuzion.web.server.event.reflect.EventHandler;
import me.infuzion.web.server.event.reflect.Route;
import me.infuzion.web.server.event.reflect.param.mapper.impl.Response;
import me.infuzion.web.server.event.reflect.param.mapper.impl.UrlParam;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChessGameInfoListener implements EventListener {
    private final MatchDatabase database;
    private final UserDatabase userDatabase;
    private final RecordSet<Game> recordSet;

    public ChessGameInfoListener(MatchDatabase matchDatabase, UserDatabase userDatabase) {
        this.database = matchDatabase;
        this.userDatabase = userDatabase;
        recordSet = new RecordSet<>("games", database);
    }

    @EventHandler
    @Route("/api/v1/games/")
    @Response("application/json")
    public JsonObject onRequest(PageRequestEvent event) {
        return generateInfo(recordSet.get(10));
    }

    private JsonObject generateInfo(List<Game> games) {
        JsonObject root = new JsonObject();
        JsonArray array = new JsonArray();
        User[] players = new User[2];
        JsonArray users = new JsonArray();
        Set<Identifier> addedUsers = new HashSet<>();
        for (Game e : games) {
            JsonObject game = new JsonObject();
            game.addProperty("id", e.getGameID().getId());

            Identifier blackId;
            Identifier whiteId;
            if ((blackId = e.getBlackSide()) != null) {
                players[0] = userDatabase.getUser(blackId);
            } else {
                players[0] = null;
            }

            if ((whiteId = e.getWhiteSide()) != null) {
                players[1] = userDatabase.getUser(whiteId);
            } else {
                players[1] = null;
            }

            for (User user : players) {
                if (user != null) {
                    Identifier identifier = user.getIdentifier();
                    if (addedUsers.contains(identifier)) {
                        continue;
                    }
                    addedUsers.add(identifier);
                    users.add(user.toJson());
                }
            }

            game.addProperty("playerBlack", (e.getBlackSide() != null) ? e.getBlackSide().getId() : null);
            game.addProperty("playerWhite", (e.getWhiteSide() != null) ? e.getWhiteSide().getId() : null);
            game.addProperty("status", e.getStatus().name());
            array.add(game);
        }
        root.add("games", array);
        root.add("users", users);

        return root;
    }

    @EventHandler
    @Route("/api/v1/games/:game_id/")
    @Response("application/json")
    private JsonObject singleGame(PageRequestEvent event, @UrlParam("game_id") String game_id) {
        Game game = recordSet.get(new Identifier(game_id));
        if (game == null) {
            JsonObject object = new JsonObject();
            object.addProperty("error", "invalid game id");
            return object;
        } else {
            return generateInfo(Collections.singletonList(game));
        }
    }
}
