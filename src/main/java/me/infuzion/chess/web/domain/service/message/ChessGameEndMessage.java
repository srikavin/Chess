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

package me.infuzion.chess.web.domain.service.message;

import me.infuzion.chess.game.util.Identifier;
import me.infuzion.chess.web.domain.GameStatus;

public class ChessGameEndMessage {
    private final Identifier gameId;
    private final GameStatus finalState;

    public ChessGameEndMessage(Identifier gameId, GameStatus finalState) {
        this.gameId = gameId;
        this.finalState = finalState;
    }

    public GameStatus getFinalState() {
        return finalState;
    }

    public Identifier getGameId() {
        return gameId;
    }
}
