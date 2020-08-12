package me.infuzion.chess.web.listener;

import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.domain.User;
import me.infuzion.chess.web.domain.service.TokenService;
import me.infuzion.web.server.EventListener;
import me.infuzion.web.server.event.Event;
import me.infuzion.web.server.event.reflect.param.DefaultTypeConverter;
import me.infuzion.web.server.event.reflect.param.HasBody;
import me.infuzion.web.server.event.reflect.param.HasHeaders;
import me.infuzion.web.server.event.reflect.param.TypeConverter;
import me.infuzion.web.server.http.parser.BodyData;

public class ChessAuthenticationHelper implements EventListener {

    private final TokenService tokenService;
    private final TypeConverter converter = new DefaultTypeConverter();

    public ChessAuthenticationHelper(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    private String checkBody(HasBody event) {
        var fields = event.getBodyData().getFields();

        BodyData.BodyField tokenField = fields.get("token");

        if (tokenField == null) {
            return null;
        }

        return converter.deserialize(tokenField.getContent(), String.class);
    }

    private String checkHeader(HasBody event) {
        if (!(event instanceof HasHeaders)) {
            return null;
        }

        return ((HasHeaders) event).getRequestHeaders().get("x-api-key");
    }

    public User getUser(Event event) {
        String token = null;
        if (event instanceof HasBody) {
            token = checkBody((HasBody) event);
        }

        if (token == null && event instanceof HasHeaders) {
            token = checkHeader((HasBody) event);
        }

        if (token == null) {
            return null;
        }

        return tokenService.getUser(new Identifier(token));
    }
}
