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

package me.infuzion.chess.clock;

import me.infuzion.chess.data.PubSubChannel;
import me.infuzion.chess.data.PubSubMessage;
import me.infuzion.chess.data.PubSubSource;
import me.infuzion.chess.game.piece.Color;
import me.infuzion.chess.game.util.Identifier;
import me.infuzion.chess.web.domain.service.message.ChessGameEndMessage;
import me.infuzion.chess.web.domain.service.message.ChessGameMoveMessage;
import me.infuzion.web.server.EventListener;
import me.infuzion.web.server.event.reflect.EventHandler;
import me.infuzion.web.server.event.reflect.param.mapper.impl.BodyParam;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.*;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static me.infuzion.chess.game.piece.Color.BLACK;
import static me.infuzion.chess.game.piece.Color.WHITE;

public class ClockService implements EventListener {
    private static final String CLOCK_EXPIRING_KEY = "chess::clock.expiring";
    private static final String CLOCK_EXPIRED_KEY = "chess::clock.expired";

    private final JedisPool pool;
    private final PubSubSource pubSubSource;

    public ClockService(JedisPool pool, PubSubSource pubSubSource) {
        this.pool = pool;
        this.pubSubSource = pubSubSource;
    }

    public void runHandleExpiringGames() {
        try (Jedis jedis = pool.getResource()) {
            while (true) {
                Instant now = Instant.now();
                jedis.watch(CLOCK_EXPIRING_KEY);

                int minScore = 0;
                double maxScore = now.toEpochMilli() / 100.;

                Set<String> results = jedis.zrangeByScore(CLOCK_EXPIRING_KEY, minScore, maxScore);

                if (results.size() == 0) {
                    jedis.unwatch();
                    Thread.sleep(50);
                    continue;
                }

                Transaction transaction = jedis.multi();

                for (String key : results) {
                    transaction.rpush(CLOCK_EXPIRED_KEY, key);
                    transaction.zrem(CLOCK_EXPIRING_KEY, key);
                }

                transaction.exec();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void runClearExpiringGames() {
        try (Jedis jedis = pool.getResource()) {
            while (true) {
                // no need for a transaction as blpop is atomic
                List<String> results = jedis.blpop(15, CLOCK_EXPIRED_KEY);

                if (results == null || results.size() != 2) {
                    continue;
                }

                // results[0] contains the key of the list popped from
                // results[1] contains the value popped

                Identifier gameId = new Identifier(results.get(1));
                String gameKey = "chess::clock.active." + gameId.getId();

                if (jedis.exists(gameKey)) {
                    String colorStr = jedis.hget(gameKey, "current_active");

                    jedis.del(gameKey);

                    if (colorStr == null) {
                        continue;
                    }

                    publishTimeExpired(gameId, Color.valueOf(colorStr));
                }
            }
        }
    }

    private void publishTimeExpired(Identifier gameId, Color color) {
        pubSubSource.publish("chess::clock.expire", new ChessClockExpiredMessage(gameId, color));
        System.out.println("gameId = " + gameId + ", color = " + color);
        System.out.println("time expired");
    }

    public void startClockForGame(@NotNull Identifier gameId, int whiteTimeDeciSeconds, int blackTimeDeciSeconds) {
        String game = gameId.getId();
        String clockKey = "chess::clock.active." + game;

        Instant instant = Instant.now();

        Clock clock = new Clock(whiteTimeDeciSeconds, blackTimeDeciSeconds, instant.toEpochMilli(), WHITE);

        try (Jedis jedis = pool.getResource()) {
            jedis.watch(clockKey);

            if (jedis.exists(clockKey)) {
                // timer was already started, abort this transaction
                jedis.unwatch();
                return;
            }

            Transaction transaction = jedis.multi();

            transaction.zadd("chess::clock.expiring", (instant.toEpochMilli() / 100.) + clock.getWhiteTimeDeciSeconds(), game);

            transaction.hset(clockKey, "white_time", Integer.toString(clock.getWhiteTimeDeciSeconds()));
            transaction.hset(clockKey, "black_time", Integer.toString(clock.getBlackTimeDeciSeconds()));
            transaction.hset(clockKey, "current_active", WHITE.name());
            transaction.hset(clockKey, "last_move_time", DateTimeFormatter.ISO_INSTANT.format(instant));

            transaction.exec();
        }

        pubSubSource.publish("chess::clock.update", new ChessClockUpdateMessage(gameId, clock));
    }

    @Nullable
    public Clock getClockForGame(@NotNull Identifier gameId) {
        try (Jedis jedis = pool.getResource()) {
            Map<String, String> resps = jedis.hgetAll("chess::clock.active." + gameId.getId());

            if (resps == null || resps.size() == 0) {
                return null;
            }

            int whiteTimeDeciSeconds = Integer.parseInt(resps.get("white_time"));
            int blackTimeDeciSeconds = Integer.parseInt(resps.get("black_time"));
            Color color = Color.valueOf(resps.get("current_active"));
            Instant lastMoveTime = Instant.parse(resps.get("last_move_time"));

            Instant now = Instant.now();

            System.out.println(now);
            System.out.println(lastMoveTime);

            if (color == WHITE) {
                whiteTimeDeciSeconds -= (int) (Duration.between(lastMoveTime, now).toMillis() / 100);
            } else if (color == BLACK) {
                blackTimeDeciSeconds -= (int) (Duration.between(lastMoveTime, now).toMillis() / 100);
            }

            return new Clock(whiteTimeDeciSeconds, blackTimeDeciSeconds, lastMoveTime.toEpochMilli(), color);
        }
    }


    @EventHandler
    @PubSubChannel(channel = "chess::game.move")
    private void onGameMove(PubSubMessage event, @BodyParam ChessGameMoveMessage message) {
        // create the timestamp before acquiring a redis instance to minimize overhead added to move times
        Instant now = Instant.now();
        String gameId = message.getGameId().getId();
        String clockKey = "chess::clock.active." + gameId;

        try (Jedis jedis = pool.getResource()) {
            jedis.watch(clockKey);

            // ensure that the move has not already been processed

            Pipeline pipeline = jedis.pipelined();
            Response<String> colorStrResponse = pipeline.hget(clockKey, "current_active");
            Response<String> whiteTimeResponse = pipeline.hget(clockKey, "white_time");
            Response<String> blackTimeResponse = pipeline.hget(clockKey, "black_time");
            Response<String> lastMoveTimeResp = pipeline.hget(clockKey, "last_move_time");
            pipeline.sync();

            String colorStr = colorStrResponse.get();

            if (colorStr == null || Color.valueOf(colorStr) != message.getMoveColor()) {
                jedis.unwatch();
                return;
            }

            Transaction transaction = jedis.multi();

            int whiteTime = Integer.parseInt(whiteTimeResponse.get());
            int blackTime = Integer.parseInt(blackTimeResponse.get());
            Instant lastMoveTime = Instant.parse(lastMoveTimeResp.get());
            Color color = Color.valueOf(colorStr);

            if (color == WHITE) {
                whiteTime -= (int) (Duration.between(lastMoveTime, now).toMillis() / 100.);
                transaction.zadd("chess::clock.expiring", (now.toEpochMilli() / 100.) + whiteTime, gameId);
                transaction.hset(clockKey, "white_time", Integer.toString(whiteTime));
            } else if (color == BLACK) {
                blackTime -= (int) (Duration.between(lastMoveTime, now).toMillis() / 100.);
                transaction.zadd("chess::clock.expiring", (now.toEpochMilli() / 100.) + blackTime, gameId);
                transaction.hset(clockKey, "black_time", Integer.toString(blackTime));
            }

            transaction.hset(clockKey, "current_active", message.getMoveColor().invert().name());
            transaction.hset(clockKey, "last_move_time", DateTimeFormatter.ISO_INSTANT.format(now));

            transaction.exec();
            pubSubSource.publish("chess::clock.update", new ChessClockUpdateMessage(message.getGameId(), new Clock(whiteTime, blackTime, lastMoveTime.toEpochMilli(), color)));
        }
    }

    @EventHandler
    @PubSubChannel(channel = "chess::game.end")
    private void onGameEnd(PubSubMessage event, @BodyParam ChessGameEndMessage message) {
        String gameId = message.getGameId().getId();
        String clockKey = "chess::clock.active." + gameId;

        try (Jedis jedis = pool.getResource()) {
            // no need for a transaction because deletions can happen multiple time without side-effect
            jedis.del(clockKey);
        }
    }
}
