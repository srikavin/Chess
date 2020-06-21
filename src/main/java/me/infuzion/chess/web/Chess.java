package me.infuzion.chess.web;

import me.infuzion.chess.web.event.helper.RequestUser;
import me.infuzion.chess.web.event.helper.RequestUserParamMapper;
import me.infuzion.chess.web.event.helper.RequireAuthenticationPredicate;
import me.infuzion.chess.web.event.helper.RequiresAuthentication;
import me.infuzion.chess.web.game.User;
import me.infuzion.chess.web.listener.*;
import me.infuzion.chess.web.record.source.MatchDatabase;
import me.infuzion.chess.web.record.source.UserDatabase;
import me.infuzion.web.server.EventListener;
import me.infuzion.web.server.Server;
import me.infuzion.web.server.event.EventManager;
import me.infuzion.web.server.event.def.WebSocketTextMessageEvent;
import me.infuzion.web.server.event.reflect.EventHandler;
import me.infuzion.web.server.event.reflect.param.mapper.impl.BodyParam;
import me.infuzion.web.server.event.reflect.param.mapper.impl.Response;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

public class Chess implements EventListener {

    private final EventManager manager;
    private final MatchDatabase matchDatabase;
    private final UserDatabase userDatabase;

    public Chess(Server server) throws IOException {
        this.manager = server.getEventManager();
        try {
            Class.forName("org.postgresql.Driver");
            String url = System.getenv("JDBC_DATABASE_URL");
            Connection c = DriverManager.getConnection(url);
            userDatabase = new UserDatabase(c);
            matchDatabase = new MatchDatabase(c);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
            throw new RuntimeException(e);
        }

//        userDatabase.addUser(new Identifier(), "testuser", "password");

        TokenHandler handler = new TokenHandler();

        System.out.println(handler.addUser(userDatabase.getUser("testuser")));

        manager.registerAnnotation(RequiresAuthentication.class, new RequireAuthenticationPredicate(handler));
        manager.registerAnnotation(RequestUser.class, new RequestUserParamMapper(handler));

        manager.registerListener(this);
        manager.registerListener(new ChessAuthenticationHelper(handler));
        manager.registerListener(new ChessGamePreviewListener(matchDatabase));
        manager.registerListener(new ChessAuthentication(userDatabase, handler));
        manager.registerListener(new ChessMoveListener(matchDatabase, manager));
        manager.registerListener(new ChessGameCreateListener(matchDatabase));
        manager.registerListener(new ChessUserListener(userDatabase));
        manager.registerListener(new ChessGameInfoListener(matchDatabase, userDatabase));

        manager.registerListener(new EventListener() {
            @EventHandler(WebSocketTextMessageEvent.class)
            @RequiresAuthentication
            @Response
            private User test(@BodyParam("request") String request, @RequestUser User user) {
                System.out.println(request);
                return user;
            }
        });

        server.start();
    }
}
