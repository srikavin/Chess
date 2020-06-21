/*
 * Copyright 2020 Srikavin Ramkumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.infuzion.chess.web.event.helper;

import com.google.common.flogger.FluentLogger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.infuzion.chess.web.TokenHandler;
import me.infuzion.chess.web.game.User;
import me.infuzion.chess.web.listener.ChessAuthenticationHelper;
import me.infuzion.web.server.event.Event;
import me.infuzion.web.server.event.reflect.param.CanSetBody;
import me.infuzion.web.server.event.reflect.param.HasBody;
import me.infuzion.web.server.event.reflect.param.mapper.EventPredicate;
import me.infuzion.web.server.http.parser.BodyData;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class RequireAuthenticationPredicate implements EventPredicate<RequiresAuthentication, HasBody> {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private final Set<HasBody> failedEvents = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));
    private static final String authErrorResponse;
    private final ChessAuthenticationHelper authenticationHelper;

    static {
        JsonObject body = new JsonObject();
        body.addProperty("error", "invalid auth token");
        authErrorResponse = new Gson().toJson(body);
    }

    public RequireAuthenticationPredicate(TokenHandler tokenHandler) {
        this.authenticationHelper = new ChessAuthenticationHelper(tokenHandler);
    }

    @Override
    public boolean shouldCall(RequiresAuthentication annotation, HasBody event) {
        User user = authenticationHelper.getUser(event);

        if (user == null) {
            return false;
        }

        if (annotation.value() == AuthenticationChecks.REQUEST) {
            BodyData.BodyField field = event.getBodyData().getFields().get("request");

            if (field == null) {
                return false;
            }

            return field.getContent().equals(annotation.request());
        }

        return true;
    }

    @Override
    public void onCallPrevented(RequiresAuthentication annotation, HasBody event) {
        //ensure the body is only set once
        if (failedEvents.add(event)) {
            if (event instanceof CanSetBody) {
                ((CanSetBody) event).setResponseBody(authErrorResponse);
            }
        }
    }

    @Override
    public boolean validate(RequiresAuthentication annotation, Class<? extends Event> event) {
        if (!HasBody.class.isAssignableFrom(event)) {
            return false;
        }

        if (!annotation.request().equals("")) {
            if (annotation.value() == AuthenticationChecks.NONE) {
                logger.atSevere().log("Request set for method when AuthenticationChecks is NONE");
                return false;
            }
        }

        return true;
    }

    @Override
    public int executionOrder() {
        return 10;
    }
}
