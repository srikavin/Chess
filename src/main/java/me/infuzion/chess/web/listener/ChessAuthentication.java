package me.infuzion.chess.web.listener;

import com.google.gson.JsonObject;
import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.util.UserDatabase;
import me.infuzion.chess.web.TokenHandler;
import me.infuzion.chess.web.event.ChessWebEvent;
import me.infuzion.chess.web.event.ChessWebListener;
import me.infuzion.chess.web.event.EndPointURL;
import me.infuzion.chess.web.game.User;
import me.infuzion.web.server.event.reflect.EventHandler;
import me.infuzion.web.server.util.HTTPMethod;
import me.infuzion.web.server.util.HttpParameters;

public class ChessAuthentication implements ChessWebListener {
    private final UserDatabase database;
    private final TokenHandler tokenHandler;

    public ChessAuthentication(UserDatabase database, TokenHandler tokenHandler) {
        this.database = database;
        this.tokenHandler = tokenHandler;
    }

    @EventHandler
    public void onRequest(ChessWebEvent event) {
        if (event.getEvent().getPage().startsWith(EndPointURL.USER_API_BASE_URL)) {
            final User user;

            HttpParameters getParameters = event.getEvent().getUrlParameters();
            if (getParameters.contains("username")) {
                String username = getParameters.get("username").get(0);
                user = database.getUser(username);
            } else {
                String id = event.getEvent().getPage().substring(EndPointURL.USER_API_BASE_URL.length());
                user = database.getUser(new Identifier(id));
            }

            if (user != null) {
                setResponseJsonWrapped(event.getEvent(), user, "user");
                return;
            } else {
                setResponseJson(event.getEvent(), new JsonObject(), 404);
                return;
            }
        }

        if (event.getEvent().getPage().equalsIgnoreCase(EndPointURL.TOKEN_CHECK) && event.getEvent().getMethod() == HTTPMethod.POST) {
            HttpParameters post = event.getEvent().getBodyParameters();
            if (post.contains("token")) {
                String token = post.get("token").get(0);
                if (token != null) {
                    User user = tokenHandler.getUser(new Identifier(post.get("token").get(0)));
                    if (user != null) {
                        setResponseJsonWrapped(event.getEvent(), true, "success");
                        return;
                    }
                }
            }
            setResponseJsonWrapped(event.getEvent(), false, "success");
            return;
        }

        if (event.getEvent().getPage().equalsIgnoreCase(EndPointURL.LOGIN_URL)
                || event.getEvent().getPage().equalsIgnoreCase(EndPointURL.REGISTER_URL)) {
            HttpParameters parameters = event.getEvent().getBodyParameters();
            if (!parameters.contains("username") || !parameters.contains("password")) {
                JsonObject object = new JsonObject();
                object.addProperty("error", "requires username and password");
                setResponseJson(event.getEvent(), object, 400);
                return;
            }
            String username = parameters.get("username").get(0);
            String password = parameters.get("password").get(0);
            if (event.getEvent().getPage().equalsIgnoreCase(EndPointURL.LOGIN_URL)) {
                User user = database.checkLoginAndGetUser(username, password);
                setSuccess(event, user);
            }
            if (event.getEvent().getPage().equalsIgnoreCase(EndPointURL.REGISTER_URL)) {
                User user = database.addUser(new Identifier(), username, password);
                setSuccess(event, user);
            }
        }
    }

    private void setSuccess(ChessWebEvent event, User user) {
        Identifier token = tokenHandler.addUser(user);
        JsonObject object = new JsonObject();
        if (user != null) {
            object.addProperty("success", true);
            object.addProperty("id", user.getIdentifier().getId());
            object.addProperty("token", token.getId());
        } else {
            object.addProperty("error", "user not found");
        }
        setResponseJson(event.getEvent(), object);
    }
}
