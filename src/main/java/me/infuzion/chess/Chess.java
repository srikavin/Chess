package me.infuzion.chess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.infuzion.web.server.Server;
import me.infuzion.web.server.event.PageLoadEvent;

public class Chess {

    private final static Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder();
        gson = builder.setPrettyPrinting().create();
    }

    private final List<Game> waiting = new ArrayList<>();
    private final Map<UUID, Game> uuidGameMap = new HashMap<>();

    public Chess(Server server) {
        server.getEventManager().registerListener(this::onLoad);
    }

    private void onLoad(PageLoadEvent event) {
        UUID uuid = event.getSessionUuid();
        if (uuid == null) {
            return;
        }
        if (!uuidGameMap.containsKey(uuid)) {
            if (waiting.size() == 0) {
                Game game = new Game(uuid);
                waiting.add(game);
                uuidGameMap.put(uuid, game);
            } else {
                Game recentGame = waiting.get(0);
                if (recentGame != null && !recentGame.getWhiteSide()
                    .equals(uuid)) {
                    waiting.get(0).setBlackSide(uuid);
                    waiting.remove(0);
                    uuidGameMap.put(uuid, recentGame);
                }
            }
        } else if (event.getPage().equals("/chess.json")) {
            event.setResponseData(gson.toJson(uuidGameMap.get(uuid)));
            event.setFileEncoding("json");
            event.setHandled(true);
            event.setStatusCode(200);
        }
        if (event.getPage().equals("/submit.json")) {
            Game game = uuidGameMap.get(uuid);
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(event.getRequestData());
            JsonObject action = element.getAsJsonObject();
            int startX = action.get("startX").getAsInt();
            int startY = action.get("startY").getAsInt();
            int endX = action.get("endX").getAsInt();
            int endY = action.get("endY").getAsInt();

            JsonObject obj = new JsonObject();
            event.setFileEncoding("json");

            if (!(startX > 7 || startX < 0 || endX > 7 || endY < 0)) {
                game.move(uuid, new ChessPosition(startX, startY),
                    new ChessPosition(endX, endY));
                event.setStatusCode(200);
                obj.addProperty("status", 200);
            } else {
                event.setStatusCode(400);
                obj.addProperty("status", 400);
            }
            event.setHandled(true);

            event.setResponseData(gson.toJson(obj));
        }
    }
}
