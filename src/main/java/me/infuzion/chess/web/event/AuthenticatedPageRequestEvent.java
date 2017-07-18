package me.infuzion.chess.web.event;

import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.game.User;
import me.infuzion.web.server.event.Event;
import me.infuzion.web.server.event.def.PageRequestEvent;
import me.infuzion.web.server.router.Router;

public class AuthenticatedPageRequestEvent extends Event {
    private final PageRequestEvent event;
    private final User user;
    private final Identifier id;

    public AuthenticatedPageRequestEvent(PageRequestEvent event, User user) {
        this.event = event;
        this.user = user;
        this.id = user.getIdentifier();
    }

    public PageRequestEvent getEvent() {
        return event;
    }

    public Identifier getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Router getRouter() {
        return event.getRouter();
    }
}
