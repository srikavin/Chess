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

import me.infuzion.web.server.event.AbstractEvent;
import me.infuzion.web.server.event.reflect.param.HasBody;
import me.infuzion.web.server.http.parser.BodyData;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class PubSubMessage extends AbstractEvent implements HasBody {
    private final String channel;
    private final String message;

    public PubSubMessage(String channel, String message) {
        this.channel = channel;
        this.message = message;
    }

    public String getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String getRequestData() {
        return message;
    }

    @Override
    public ByteBuffer getRawRequestData() {
        return StandardCharsets.UTF_8.encode(message);
    }

    @Override
    public BodyData getBodyData() {
        return new BodyData(Collections.emptyMap());
    }
}
