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

package me.infuzion.chess.data;

import me.infuzion.web.server.event.EventManager;
import me.infuzion.web.server.event.reflect.param.TypeConverter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

public class RedisPubSubSource extends JedisPubSub implements PubSubSource {
    private final EventManager manager;
    private final JedisPool connectionPool;
    private final TypeConverter typeConverter;

    public RedisPubSubSource(EventManager manager, JedisPool connectionPool, TypeConverter typeConverter) {
        this.manager = manager;
        this.connectionPool = connectionPool;
        this.typeConverter = typeConverter;
    }

    @Override
    public void publishRaw(String channel, String data) {
        try (Jedis client = connectionPool.getResource()) {
            client.publish(channel, data);
        }
    }

    @Override
    public void publish(String channel, Object data) {
        try (Jedis client = connectionPool.getResource()) {
            client.publish(channel, typeConverter.serialize(data));
        }
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        //TODO: remove
        System.out.println(channel);
        System.out.println(message);
        manager.fireEvent(new PubSubMessage(channel, message));
    }
}
