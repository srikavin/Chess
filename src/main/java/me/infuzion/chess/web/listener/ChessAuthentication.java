package me.infuzion.chess.web.listener;

import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.util.UserDatabase;
import me.infuzion.chess.web.User;
import me.infuzion.chess.web.event.ChessWebEvent;
import me.infuzion.chess.web.event.ChessWebListener;
import me.infuzion.chess.web.event.EndPointURL;
import me.infuzion.web.server.event.EventHandler;
import me.infuzion.web.server.util.HttpParameters;

import java.util.List;

public class ChessAuthentication implements ChessWebListener {
    private final UserDatabase database;

    public ChessAuthentication(UserDatabase database) {
        this.database = database;
    }

    @EventHandler
    public void onRequest(ChessWebEvent event) {
        if (event.getEvent().getPostParameters().isEmpty()) {
            if (event.getEvent().getPage().toLowerCase().startsWith(EndPointURL.USER_BASE_URL)) {
                HttpParameters parameters = event.getEvent().getGetParameters();
                if (parameters.get("format") != null && parameters.get("format").size() > 0) {
                    List<String> idList = parameters.get("id");
                    List<String> nameList = parameters.get("name");
                    User user;
                    if (idList.size() > 0) {
                        String id = idList.get(0);
                        user = database.getUser(new Identifier(id));
                    } else if (nameList.size() > 0) {
                        String name = nameList.get(0);
                        user = database.getUser(name);
                    } else {
                        setResponse(event.getEvent(), "Bad Request", 400, "text");
                        return;
                    }
                    setResponseJson(event.getEvent(), user);
                } else {
                    setResponse(event.getEvent(), getClass().getResourceAsStream("/web/user.html"), 200, "html");
                }
            }
            return;
        }

        HttpParameters parameters = event.getEvent().getPostParameters();
        String username = parameters.get("username").get(0);
        String password = parameters.get("password").get(0);
        if (username == null || password == null) {
            event.getEvent().setStatusCode(500);
            return;
        }
        if (event.getEvent().getPage().equalsIgnoreCase(EndPointURL.LOGIN_URL)) {
            User user = database.checkLoginAndGetUser(username, password);
            setResponseJson(event.getEvent(), user);
        }
        if (event.getEvent().getPage().equalsIgnoreCase(EndPointURL.REGISTER_URL)) {
            User user = database.addUser(new Identifier(), username, password);
            setResponseJson(event.getEvent(), user);
        }

    }
}
