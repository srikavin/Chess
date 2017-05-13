package me.infuzion.chess.web;

import com.google.gson.Gson;
import me.infuzion.chess.util.ChessUtilities;
import me.infuzion.chess.util.MatchDatabase;
import me.infuzion.chess.util.UserDatabase;
import me.infuzion.chess.web.event.AuthenticatedWebSocketEvent;
import me.infuzion.chess.web.event.ChessWebEvent;
import me.infuzion.chess.web.game.Game;
import me.infuzion.chess.web.listener.ChessAuthentication;
import me.infuzion.chess.web.listener.ChessGameCreateListener;
import me.infuzion.chess.web.listener.ChessMoveListener;
import me.infuzion.chess.web.listener.ChessWebSocketHelper;
import me.infuzion.web.server.EventListener;
import me.infuzion.web.server.Server;
import me.infuzion.web.server.event.EventManager;
import me.infuzion.web.server.event.def.PageRequestEvent;
import me.infuzion.web.server.event.reflect.EventHandler;

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
        manager.registerEvent(AuthenticatedWebSocketEvent.class);
        TokenHandler handler = new TokenHandler();
        manager.registerListener(this);
        manager.registerListener(new ChessWebSocketHelper(handler, manager));

        manager.registerListener(new ChessAuthentication(userDatabase, handler));
        manager.registerListener(new ChessMoveListener(matchDatabase, handler));
        manager.registerListener(new ChessGameCreateListener(matchDatabase, handler));
        server.init();
    }

    @EventHandler
    private void onEvent(PageRequestEvent event) {
        event.addHeader("Access-Control-Allow-Origin", "*");
        UUID privateUUID = event.getSessionUuid();

        if (privateUUID == null) {
            return;
        }
        UUID publicUUID = publicUUIDMap.getOrDefault(privateUUID, UUID.randomUUID());
        publicUUIDMap.put(privateUUID, publicUUID);

        ChessWebEvent a = new ChessWebEvent(event, publicUUID, uuidGameMap);
        manager.fireEvent(a);
    }
}
