package me.infuzion.chess.web;

import com.google.gson.Gson;
import me.infuzion.chess.util.ChessUtilities;
import me.infuzion.chess.util.MatchDatabase;
import me.infuzion.chess.util.UserDatabase;
import me.infuzion.chess.web.event.ChessWebEvent;
import me.infuzion.chess.web.listener.*;
import me.infuzion.web.server.EventListener;
import me.infuzion.web.server.Server;
import me.infuzion.web.server.event.EventHandler;
import me.infuzion.web.server.event.EventManager;
import me.infuzion.web.server.event.PageRequestEvent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Chess implements EventListener {

    private final Map<UUID, Game> uuidGameMap = new HashMap<>();
    private final Map<UUID, UUID> publicUUIDMap = new HashMap<>();
    private final EventManager manager;
    private final MatchDatabase matchDatabase;
    private final UserDatabase userDatabase;
    private Gson gson = ChessUtilities.gson;

    public Chess(Server server) {
        this.manager = server.getEventManager();
        try {
            userDatabase = new UserDatabase("chess.db");
            matchDatabase = new MatchDatabase("chess.db");
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
            throw new RuntimeException(e);
        }

        manager.registerEvent(ChessWebEvent.class);
        manager.registerListener(this);
        manager.registerListener(new ChessAuthentication(userDatabase));
        manager.registerListener(new ChessDataListener(matchDatabase));
        manager.registerListener(new ChessMoveListener(matchDatabase));
        manager.registerListener(new ChessGameListener());
        manager.registerListener(new ChessEndpointListener());
        server.init();
    }

    @EventHandler
    private void onEvent(PageRequestEvent event) {
        UUID privateUUID = event.getSessionUuid();

        if (privateUUID == null) {
            return;
        }
        event.addHeader("Access-Control-Allow-Origin", "*");
        UUID publicUUID = publicUUIDMap.getOrDefault(privateUUID, UUID.randomUUID());
        publicUUIDMap.put(privateUUID, publicUUID);

        ChessWebEvent a = new ChessWebEvent(event, publicUUID, uuidGameMap);
        manager.fireEvent(a);
    }

    private void setResponseJson(PageRequestEvent event, String json) {
        setResponseJson(event, json, 200);
    }

    private void setResponseJson(PageRequestEvent event, String json, int status) {
        event.setResponseData(json);
        event.setStatusCode(status);
        event.setFileEncoding("json");
        event.setHandled(true);
    }

    private void setResponseJson(PageRequestEvent event, Object element) {
        setResponseJson(event, gson.toJson(element));
    }

    private void setResponseJson(PageRequestEvent event, Object element, int status) {
        setResponseJson(event, gson.toJson(element), status);
    }

}
