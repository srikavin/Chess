package me.infuzion.chess.web.listener;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.infuzion.chess.util.ChessUtilities;
import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.event.ChessWebListener;
import me.infuzion.chess.web.game.Game;
import me.infuzion.chess.web.game.User;
import me.infuzion.chess.web.record.RecordSet;
import me.infuzion.chess.web.record.source.MatchDatabase;
import me.infuzion.chess.web.record.source.UserDatabase;
import me.infuzion.web.server.event.def.PageRequestEvent;
import me.infuzion.web.server.event.reflect.EventHandler;
import me.infuzion.web.server.event.reflect.EventPriority;
import me.infuzion.web.server.event.reflect.Route;

import java.util.*;

public class ChessGameInfoListener implements ChessWebListener {
    private final MatchDatabase database;
    private final UserDatabase userDatabase;

    public ChessGameInfoListener(MatchDatabase matchDatabase, UserDatabase userDatabase) {
        this.database = matchDatabase;
        this.userDatabase = userDatabase;
    }

    @EventHandler
    @Route(path = "/api/v1/games")
    public void onRequest(PageRequestEvent event, Map<String, String> map) {
        List<Game> games = database.getMatches(10);
        setResponseJson(event, ChessUtilities.gson.toJson(generateInfo(games)));
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
        JsonObject root2 = new JsonObject();
        root.add("games", array);
        root.add("users", users);

        return new RecordSet<>("games", database).toJson(10);
    }

    @EventHandler(priority = EventPriority.END)
    @Route(path = "/api/v1/games/:game_id/")
    private void singleGame(PageRequestEvent event, Map<String, String> dynSegs) {
        Game game = database.getMatch(new Identifier(dynSegs.get("game_id")));
        if (game == null) {
            JsonObject object = new JsonObject();
            object.addProperty("error", "invalid game id");
            setResponseJson(event, object);
        } else {
            setResponseJson(event, ChessUtilities.gson.toJson(generateInfo(Collections.singletonList(game))));
        }
    }
}
