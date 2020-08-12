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
import me.infuzion.chess.web.domain.User;
import me.infuzion.chess.web.domain.service.TokenService;
import me.infuzion.chess.web.listener.ChessAuthenticationHelper;
import me.infuzion.web.server.event.Event;
import me.infuzion.web.server.event.reflect.param.HasBody;
import me.infuzion.web.server.event.reflect.param.mapper.ParamMapper;

import java.lang.reflect.Method;

public class RequestUserParamMapper implements ParamMapper<RequestUser, HasBody, User> {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private final ChessAuthenticationHelper authenticationHelper;

    public RequestUserParamMapper(TokenService tokenService) {
        this.authenticationHelper = new ChessAuthenticationHelper(tokenService);
    }

    @Override
    public User map(RequestUser annotation, Method method, Class<?> parameterType, HasBody event) {
        return authenticationHelper.getUser(event);
    }

    @Override
    public boolean validate(RequestUser annotation, Method method, Class<?> parameterType, Class<? extends Event> event) {
        if (!HasBody.class.isAssignableFrom(event)) {
            logger.atSevere().log("Event does not support body");
            return false;
        }

        if (!User.class.isAssignableFrom(parameterType)) {
            logger.atSevere().log("Parameter must be of type User");
            return false;
        }

        return true;
    }
}
