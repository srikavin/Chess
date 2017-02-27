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
import me.infuzion.chess.util.GameIdentifier;
import me.infuzion.web.server.Server;
import me.infuzion.web.server.event.PageLoadEvent;
import me.infuzion.web.server.util.Utilities;

public class Chess {

    private final static Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder();
        gson = builder.setPrettyPrinting().create();
    }

    private final List<Game> waiting = new ArrayList<>();
    private final Map<UUID, Game> uuidGameMap = new HashMap<>();
    private final Map<UUID, UUID> publicUUIDMap = new HashMap<>();
    private final Server server;

    public Chess(Server server) {
        this.server = server;
        server.getEventManager().registerListener(this::onLoad);
    }

    private void onLoad(PageLoadEvent event) {
        UUID privateUUID = event.getSessionUuid();

        if (privateUUID == null) {
            return;
        }
        UUID publicUUID = publicUUIDMap.getOrDefault(privateUUID, UUID.randomUUID());
        publicUUIDMap.put(privateUUID, publicUUID);
        String pageName = event.getPage();
        if (pageName.startsWith("/game/")) {
            String id = pageName.substring(6).split("\\.")[0];
            Game game = Game.fromID(new GameIdentifier(id));
            if (game == null) {
                event.setStatusCode(404);
                return;
            }
            if (pageName.endsWith(".json")) {
                event.setFileEncoding("json");
                event.setResponseData(gson.toJson(game));
            } else {
                event.setFileEncoding("html");
                event.setResponseData(Utilities
                    .convertStreamToString(getClass().getResourceAsStream("/web/play.html")));
            }
            event.setStatusCode(200);
            event.setHandled(true);

        }

        if (pageName.equals("/chess.json")) {
            if (!uuidGameMap.containsKey(publicUUID)) {
                if (waiting.size() == 0) {
                    Game game = new Game(publicUUID, Visibility.PUBLIC);
                    waiting.add(game);
                    uuidGameMap.put(publicUUID, game);
                } else {
                    Game recentGame = waiting.get(0);
                    if (recentGame != null && !recentGame.getWhiteSide()
                        .equals(publicUUID)) {
                        waiting.get(0).setBlackSide(publicUUID);
                        waiting.remove(0);
                        uuidGameMap.put(publicUUID, recentGame);
                    }
                }
            } else {
                event.setResponseData(gson.toJson(uuidGameMap.get(publicUUID)));
                event.setFileEncoding("json");
                event.setHandled(true);
                event.setStatusCode(200);
            }
        }
        if (pageName.equals("/waiting.json")) {
            event.setResponseData(gson.toJson(waiting));
            event.setFileEncoding("json");
            event.setHandled(true);
            event.setStatusCode(200);
        }
        if (pageName.equals("/submit.json")) {
            Game game = uuidGameMap.get(publicUUID);
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(event.getRequestData());
            JsonObject action = element.getAsJsonObject();
            int startX = action.get("startX").getAsInt();
            int startY = action.get("startY").getAsInt();
            int endX = action.get("endX").getAsInt();
            int endY = action.get("endY").getAsInt();

            int status = 200;
            if (!(startX > 7 || startX < 0 || endX > 7 || endY < 0)) {
                game.move(publicUUID, new ChessPosition(startX, startY),
                    new ChessPosition(endX, endY));
            } else {
                status = 400;
            }

            JsonObject obj = new JsonObject();
            obj.addProperty("status", status);

            event.setStatusCode(status);
            event.setFileEncoding("json");
            event.setHandled(true);

            event.setResponseData(gson.toJson(obj));
        }
    }
}
