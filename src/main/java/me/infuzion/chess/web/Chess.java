package me.infuzion.chess.web;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.dao.impl.MatchDatabase;
import me.infuzion.chess.web.dao.impl.UserDatabase;
import me.infuzion.chess.web.domain.service.GameService;
import me.infuzion.chess.web.event.helper.RequestUser;
import me.infuzion.chess.web.event.helper.RequestUserParamMapper;
import me.infuzion.chess.web.event.helper.RequireAuthenticationPredicate;
import me.infuzion.chess.web.event.helper.RequiresAuthentication;
import me.infuzion.chess.web.listener.ChessAuthenticationHelper;
import me.infuzion.chess.web.listener.ChessUserAuthenticationListener;
import me.infuzion.chess.web.listener.ChessUserProfileListener;
import me.infuzion.chess.web.listener.game.ChessGameListener;
import me.infuzion.chess.web.listener.game.ChessMoveListener;
import me.infuzion.web.server.EventListener;
import me.infuzion.web.server.Server;
import me.infuzion.web.server.event.EventManager;
import me.infuzion.web.server.event.def.PageRequestEvent;
import me.infuzion.web.server.event.reflect.EventHandler;

import java.io.IOException;

public class Chess implements EventListener {

    private final EventManager manager;
    private final MatchDatabase matchDatabase;
    private final UserDatabase userDatabase;

    public Chess(Server server) throws IOException {
        this.manager = server.getEventManager();
        try {
            String url = System.getenv("JDBC_DATABASE_URL");

            Class.forName("org.postgresql.Driver");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);

            config.setMaximumPoolSize(10);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            HikariDataSource ds = new HikariDataSource(config);

            userDatabase = new UserDatabase(ds);
            matchDatabase = new MatchDatabase(ds);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
            throw new RuntimeException(e);
        }

        GameService gameService = new GameService(matchDatabase);
        TokenHandler handler = new TokenHandler();

        userDatabase.createUser(new Identifier(), "testuser", "password");

        System.out.println(handler.addUser(userDatabase.getUser("testuser")));

        manager.registerAnnotation(RequiresAuthentication.class, new RequireAuthenticationPredicate(handler));
        manager.registerAnnotation(RequestUser.class, new RequestUserParamMapper(handler));

        manager.registerListener(this);
        manager.registerListener(new ChessAuthenticationHelper(handler));
        manager.registerListener(new ChessUserAuthenticationListener(userDatabase, handler));
        manager.registerListener(new ChessMoveListener(gameService, manager));
        manager.registerListener(new ChessUserProfileListener(userDatabase));
        manager.registerListener(new ChessGameListener(matchDatabase, gameService, userDatabase));

        manager.registerListener(new EventListener() {
            @EventHandler()
            private void test(PageRequestEvent event) {
                event.setResponseHeader("Access-Control-Allow-Origin", "*");
            }
        });

        server.start();
    }
}
