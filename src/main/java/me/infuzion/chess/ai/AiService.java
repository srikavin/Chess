/*
 * Copyright 2021 Srikavin Ramkumar
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

package me.infuzion.chess.ai;

import me.infuzion.chess.data.PubSubChannel;
import me.infuzion.chess.data.PubSubMessage;
import me.infuzion.chess.game.board.ChessMove;
import me.infuzion.chess.game.piece.Color;
import me.infuzion.chess.game.util.RandomStringGenerator;
import me.infuzion.chess.web.dao.impl.UserDatabase;
import me.infuzion.chess.web.domain.Game;
import me.infuzion.chess.web.domain.UserRole;
import me.infuzion.chess.web.domain.service.GameService;
import me.infuzion.chess.web.domain.service.message.ChessGameMoveMessage;
import me.infuzion.web.server.EventListener;
import me.infuzion.web.server.event.reflect.EventHandler;
import me.infuzion.web.server.event.reflect.param.mapper.impl.BodyParam;

public class AiService implements EventListener {
    private final GameService gameService;
    private final UciEngine engine;

    public AiService(GameService gameService, UserDatabase userDatabase, UciEngine engine) {
        this.gameService = gameService;
        this.engine = engine;
        userDatabase.createUser(StockfishEngine.STOCKFISH_ID, "Stockfish 14",
                new RandomStringGenerator(32).nextString(), "A strong open source chess engine", UserRole.BOT);
    }

    @EventHandler
    @PubSubChannel(channel = "chess::game.move")
    private void onGameMove(PubSubMessage event, @BodyParam ChessGameMoveMessage message) {
        Game game = gameService.getGame(message.getGameId());

        if ((message.getMoveColor() == Color.WHITE && game.getPlayerBlack().equals(StockfishEngine.STOCKFISH_ID)) ||
                (message.getMoveColor() == Color.BLACK && game.getPlayerWhite().equals(StockfishEngine.STOCKFISH_ID))) {
            ChessMove move = engine.getBestMove(game);
            gameService.addMove(game.getId(), StockfishEngine.STOCKFISH_ID, move);
        }
    }
}
