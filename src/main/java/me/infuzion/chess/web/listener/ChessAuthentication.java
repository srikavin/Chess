package me.infuzion.chess.web.listener;

import com.google.gson.JsonObject;
import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.TokenHandler;
import me.infuzion.chess.web.event.ChessWebListener;
import me.infuzion.chess.web.game.User;
import me.infuzion.chess.web.record.source.UserDatabase;
import me.infuzion.web.server.event.def.PageRequestEvent;
import me.infuzion.web.server.event.reflect.EventHandler;
import me.infuzion.web.server.event.reflect.Route;
import me.infuzion.web.server.router.RouteMethod;
import me.infuzion.web.server.util.HttpParameters;

import java.util.Map;

public class ChessAuthentication implements ChessWebListener {
    private final UserDatabase database;
    private final TokenHandler tokenHandler;

    public ChessAuthentication(UserDatabase database, TokenHandler tokenHandler) {
        this.database = database;
        this.tokenHandler = tokenHandler;
    }

    @EventHandler
    @Route(path = "/api/v1/auth", methods = RouteMethod.POST)
    public void loginOrRegisterRequest(PageRequestEvent event, Map<String, String> map) {
        HttpParameters parameters = event.getBodyParameters();
        if (!parameters.contains("username") || !parameters.contains("password") || !parameters.contains("request")) {
            JsonObject object = new JsonObject();
            object.addProperty("error", "requires username, password, and request");
            setResponseJson(event, object, 400);
            return;
        }
        String username = parameters.get("username").get(0);
        String password = parameters.get("password").get(0);
        String request = parameters.get("request").get(0);
        if (request.equalsIgnoreCase("login")) {
            User user = database.checkLoginAndGetUser(username, password);
            setSuccess(event, user);
        } else if (request.equalsIgnoreCase("register")) {
            User user = database.addUser(new Identifier(), username, password);
            setSuccess(event, user);
        } else {
            JsonObject object = new JsonObject();
            object.addProperty("error", "invalid request; should be login/register");
            setResponseJson(event, object, 400);
        }
    }

    @EventHandler
    @Route(path = "/api/v1/token", methods = RouteMethod.POST)
    public void tokenCheck(PageRequestEvent event, Map<String, String> map) {
        HttpParameters post = event.getBodyParameters();
        if (post.contains("token")) {
            String token = post.get("token").get(0);
            if (token != null) {
                User user = tokenHandler.getUser(new Identifier(post.get("token").get(0)));
                if (user != null) {
                    JsonObject object = new JsonObject();
                    object.addProperty("success", true);
                    object.addProperty("username", user.getUsername());
                    object.addProperty("userid", user.getIdentifier().getId());
                    setResponseJson(event, object);
                    return;
                }
            }
        }
        setResponseJsonWrapped(event, false, "success");
    }

    @EventHandler
    @Route(path = "/api/v1/users", methods = RouteMethod.GET)
    public void onUsers(PageRequestEvent event, Map<String, String> map) {
        HttpParameters getParameters = event.getUrlParameters();
        if (getParameters.contains("username")) {
            String username = getParameters.get("username").get(0);
            User user = database.getUser(username);
            handleUserResponse(event, user);
        }
    }

    private void handleUserResponse(PageRequestEvent event, User user) {
        if (user != null) {
            setResponseJsonWrapped(event, user.toJson(), "user");
        } else {
            JsonObject object = new JsonObject();
            object.addProperty("error", "not found");
            setResponseJson(event, object, 404);
        }
    }

    @EventHandler
    @Route(path = "/api/v1/users/:user_id")
    public void onUserID(PageRequestEvent event, Map<String, String> str) {
        String id = str.get("user_id");
        User user = database.getUser(new Identifier(id));
        handleUserResponse(event, user);
    }

    private void setSuccess(PageRequestEvent event, User user) {
        Identifier token = tokenHandler.addUser(user);
        JsonObject object = new JsonObject();
        if (user != null) {
            object.addProperty("success", true);
            object.addProperty("userid", user.getIdentifier().getId());
            object.addProperty("username", user.getUsername());
            object.addProperty("token", token.getId());
        } else {
            object.addProperty("error", "user not found");
        }
        setResponseJson(event, object);
    }
}
