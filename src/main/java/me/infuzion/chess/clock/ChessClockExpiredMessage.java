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

import me.infuzion.chess.game.piece.Color;
import me.infuzion.chess.game.util.Identifier;

public class ChessClockExpiredMessage {
    private final Identifier gameId;
    private final Color expiredColor;

    public ChessClockExpiredMessage(Identifier gameId, Color expiredColor) {
        this.gameId = gameId;
        this.expiredColor = expiredColor;
    }

    public Identifier getGameId() {
        return gameId;
    }

    public Color getExpiredColor() {
        return expiredColor;
    }
}
