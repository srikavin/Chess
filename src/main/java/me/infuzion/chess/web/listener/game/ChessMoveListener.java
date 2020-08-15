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

package me.infuzion.chess.web.listener.game;

import com.google.gson.Gson;
import me.infuzion.chess.game.board.ChessMove;
import me.infuzion.chess.game.piece.Color;
import me.infuzion.chess.game.util.ChessUtilities;
import me.infuzion.chess.game.util.Identifier;
import me.infuzion.chess.web.data.PubSubChannel;
import me.infuzion.chess.web.data.PubSubMessage;
import me.infuzion.chess.web.domain.Game;
import me.infuzion.chess.web.domain.User;
import me.infuzion.chess.web.domain.Variants;
import me.infuzion.chess.web.domain.service.GameService;
import me.infuzion.chess.web.domain.service.message.ChessGameMoveMessage;
import me.infuzion.chess.web.domain.service.message.ChessGamePlayerJoinMessage;
import me.infuzion.chess.web.event.helper.AuthenticationChecks;
import me.infuzion.chess.web.event.helper.RequestUser;
import me.infuzion.chess.web.event.helper.RequiresAuthentication;
import me.infuzion.web.server.EventListener;
import me.infuzion.web.server.event.EventManager;
import me.infuzion.web.server.event.def.WebSocketTextMessageEvent;
import me.infuzion.web.server.event.reflect.EventHandler;
import me.infuzion.web.server.event.reflect.Route;
import me.infuzion.web.server.event.reflect.param.mapper.impl.BodyParam;
import me.infuzion.web.server.event.reflect.param.mapper.impl.Response;
import me.infuzion.web.server.websocket.WebsocketRoom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChessMoveListener implements EventListener {
    private static final Gson gson = ChessUtilities.gson;
    private final GameService gameService;
    private final EventManager eventManager;
    private final Map<Identifier, WebsocketRoom> gameListeners = new ConcurrentHashMap<>();
    private final Object genericSuccess = new Object() {
        @SuppressWarnings("unused")
        final String status = "success";
    };

    public ChessMoveListener(GameService service, EventManager eventManager) {
        gameService = service;
        this.eventManager = eventManager;
    }

    @EventHandler
    @RequiresAuthentication(value = AuthenticationChecks.REQUEST, request = "join_game")
    @Route("/api/v1/games/")
    private void onJoinRequest(WebSocketTextMessageEvent event, @RequestUser User user, @BodyParam("id") String id) {
        Identifier gameId = new Identifier(id);

        if (!gameService.addPlayerToGame(gameId, user.getIdentifier())) {
            event.getClient().send(ChessUtilities.gson.toJson(new JoinResponse("failed to join game")));
        }
    }

    @EventHandler
    @RequiresAuthentication(value = AuthenticationChecks.REQUEST, request = "listen", requireLoggedIn = false)
    @Route("/api/v1/games/")
    private Object onListenRequest(WebSocketTextMessageEvent event, @BodyParam("id") String id) {
        Identifier gameId = new Identifier(id);

        if (gameService.getGame(gameId) != null) {
            gameListeners.computeIfAbsent(gameId, (gId) -> new WebsocketRoom(eventManager));
            WebsocketRoom room = gameListeners.get(gameId);
            room.addClient(event.getClient());
        }

        return genericSuccess;
    }

    @EventHandler
    @RequiresAuthentication(value = AuthenticationChecks.REQUEST, request = "stop_listen", requireLoggedIn = false)
    @Route("/api/v1/games/")
    private Object onStopListenRequest(WebSocketTextMessageEvent event, @BodyParam("id") String id) {
        Identifier gameId = new Identifier(id);

        if (gameService.getGame(gameId) != null) {
            gameListeners.computeIfAbsent(gameId, (gId) -> new WebsocketRoom(eventManager));
            WebsocketRoom room = gameListeners.get(gameId);
            room.removeClient(event.getClient());
        }

        return genericSuccess;
    }

    @EventHandler
    @RequiresAuthentication(value = AuthenticationChecks.REQUEST, request = "create")
    @Route("/api/v1/games/")
    @Response
    private JoinResponse onCreateRequest(WebSocketTextMessageEvent event, @RequestUser User user) {
        Game game = gameService.createGame(Variants.STANDARD_FEN, user.getIdentifier(), Color.WHITE);

        WebsocketRoom room = new WebsocketRoom(eventManager);
        gameListeners.put(game.getId(), room);
        room.addClient(event.getClient());

        return new JoinResponse(game.getId(), user.getIdentifier(), game);
    }

    @EventHandler
    @PubSubChannel(channel = "chess.game.move")
    private void onGameUpdateMove(PubSubMessage event, @BodyParam ChessGameMoveMessage message) {
        Identifier gameId = message.getGameId();

        WebsocketRoom room = gameListeners.get(gameId);

        if (room == null) {
            return;
        }

        Game game = gameService.getGame(gameId);

        MoveResponse response = new MoveResponse(gameId, message.getPlayerId(), message.getMove(), game);

        room.sendToAll(ChessUtilities.gson.toJson(response));
    }

    @EventHandler
    @PubSubChannel(channel = "chess.game.player_join")
    private void onGameUpdateJoin(PubSubMessage event, @BodyParam ChessGamePlayerJoinMessage message) {
        Identifier gameId = message.getGameId();

        WebsocketRoom room = gameListeners.get(gameId);

        if (room == null) {
            return;
        }

        Game game = gameService.getGame(gameId);

        JoinResponse response = new JoinResponse(gameId, message.getPlayerId(), game);

        room.sendToAll(ChessUtilities.gson.toJson(response));
    }

    @EventHandler(WebSocketTextMessageEvent.class)
    @Route("/api/v1/games/")
    @RequiresAuthentication(value = AuthenticationChecks.REQUEST, request = "make_move")
    @Response
    private MoveResponse onMoveRequest(WebSocketTextMessageEvent event, @RequestUser User user, @BodyParam MoveWebsocketMessage message) {
        System.out.println("move start " + System.currentTimeMillis());
        if (message.move.getSource() == null || message.move.getEnd() == null || message.id == null) {
            return new MoveResponse("move requires source, end, and id");
        }

        Identifier gameId = new Identifier(message.id);
        ChessMove move = message.move;


        if (!gameService.addMove(gameId, user.getIdentifier(), move)) {
            return new MoveResponse("game not found or invalid move");
        }

        return null;
    }

    static class MoveWebsocketMessage {
        String id;
        ChessMove move;
    }

    private static class JoinResponse extends ChessWebsocketResponse {
        final String game_id;
        final String player_id;
        final Game state;

        protected JoinResponse(Identifier game_id, Identifier player_id, Game state) {
            super("player_join");
            this.game_id = game_id.getId();
            this.player_id = player_id.getId();
            this.state = state;
        }

        protected JoinResponse(String error) {
            super("player_join", error);
            game_id = null;
            player_id = null;
            state = null;
        }
    }

    private static class MoveResponse extends ChessWebsocketResponse {
        final String game_id;
        final String player_id;
        final ChessMove move;
        final Game state;

        protected MoveResponse(Identifier game_id, Identifier player_id, ChessMove move, Game state) {
            super("player_move");
            this.game_id = game_id.getId();
            this.player_id = player_id.getId();
            this.move = move;
            this.state = state;
        }

        protected MoveResponse(String error) {
            super("player_move", error);
            game_id = null;
            player_id = null;
            move = null;
            state = null;
        }
    }
}
