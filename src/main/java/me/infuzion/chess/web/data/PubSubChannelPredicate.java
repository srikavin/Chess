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

package me.infuzion.chess.web.data;

import me.infuzion.web.server.event.Event;
import me.infuzion.web.server.event.reflect.param.mapper.EventPredicate;

public class PubSubChannelPredicate implements EventPredicate<PubSubChannel, PubSubMessage> {
    @Override
    public boolean shouldCall(PubSubChannel annotation, PubSubMessage event) {
        return annotation.channel().equals("") || annotation.channel().equals(event.getChannel());
    }

    @Override
    public boolean validate(PubSubChannel annotation, Class<? extends Event> event) {
        return event.isAssignableFrom(PubSubMessage.class);
    }
}
