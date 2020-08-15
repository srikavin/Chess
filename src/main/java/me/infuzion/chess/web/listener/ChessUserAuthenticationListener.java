package me.infuzion.chess.web.listener;

import com.google.gson.JsonObject;
import me.infuzion.chess.game.util.Identifier;
import me.infuzion.chess.web.dao.UserDao;
import me.infuzion.chess.web.domain.User;
import me.infuzion.chess.web.domain.service.TokenService;
import me.infuzion.chess.web.event.helper.RequestUser;
import me.infuzion.chess.web.event.helper.RequiresAuthentication;
import me.infuzion.web.server.EventListener;
import me.infuzion.web.server.event.def.PageRequestEvent;
import me.infuzion.web.server.event.reflect.EventHandler;
import me.infuzion.web.server.event.reflect.Route;
import me.infuzion.web.server.event.reflect.param.mapper.impl.BodyParam;
import me.infuzion.web.server.event.reflect.param.mapper.impl.QueryParam;
import me.infuzion.web.server.event.reflect.param.mapper.impl.Response;
import me.infuzion.web.server.router.RouteMethod;

public class ChessUserAuthenticationListener implements EventListener {
    private final UserDao userDao;
    private final TokenService tokenService;

    public ChessUserAuthenticationListener(UserDao userDao, TokenService tokenService) {
        this.userDao = userDao;
        this.tokenService = tokenService;
    }

    @EventHandler
    @Route(value = "/api/v1/auth", methods = RouteMethod.POST)
    @Response("application/json")
    public JsonObject loginOrRegisterRequest(PageRequestEvent event, @BodyParam("username") String username,
                                             @BodyParam("password") String password, @BodyParam("request") String request) {
        if (username == null || password == null || request == null) {
            JsonObject object = new JsonObject();
            object.addProperty("error", "requires username, password, and request");
            event.getResponse().setStatusCode(400);
            return object;
        }

        if (request.equalsIgnoreCase("login")) {
            User user = userDao.checkLoginAndGetUser(username, password);
            return setSuccess(user);
        } else if (request.equalsIgnoreCase("register")) {
            User user = userDao.createUser(new Identifier(), username, password);
            return setSuccess(user);
        }

        JsonObject object = new JsonObject();
        object.addProperty("error", "invalid request; should be login/register");

        event.getResponse().setStatusCode(400);
        return object;
    }

    @EventHandler
    @Route(value = "/api/v1/token", methods = RouteMethod.POST)
    @Response("application/json")
    @RequiresAuthentication
    public JsonObject tokenCheck(PageRequestEvent event, @RequestUser User user) {
        JsonObject object = new JsonObject();

        object.addProperty("username", user.getUsername());
        object.addProperty("user_id", user.getIdentifier().getId());

        return object;
    }

    @EventHandler
    @Route(value = "/api/v1/users", methods = RouteMethod.GET)
    @Response("application/json")
    public JsonObject getUserByUsername(PageRequestEvent event, @QueryParam("username") String username) {
        User user = userDao.getUser(username);
        if (user == null) {
            return new JsonObject();
        }
        return user.toJson();
    }

    private JsonObject setSuccess(User user) {
        JsonObject object = new JsonObject();
        if (user != null) {
            Identifier token = tokenService.addUser(user);
            object.addProperty("success", true);
            object.addProperty("user_id", user.getIdentifier().getId());
            object.addProperty("username", user.getUsername());
            object.addProperty("token", token.getId());
        } else {
            object.addProperty("error", "incorrect username or password");
        }
        return object;
    }
}
