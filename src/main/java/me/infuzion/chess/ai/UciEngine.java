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

import me.infuzion.chess.game.board.ChessMove;
import me.infuzion.chess.game.board.ChessPosition;
import me.infuzion.chess.game.piece.PieceType;
import me.infuzion.chess.web.domain.Game;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class UciEngine {
    protected volatile AtomicBoolean inUse = new AtomicBoolean();

    protected abstract void sendUciCommand(String command);

    protected abstract String readUciResponse();

    protected abstract void disconnect();

    protected String readUntil(String key) {
        while (true) {
            String response = readUciResponse();
            System.out.println(response);
            if (response.startsWith(key)) {
                return response;
            }
        }
    }

    protected void waitUntilReady() {
        sendUciCommand("isready");
        readUntil("readyok");
    }

    public ChessMove getBestMove(Game state) {
        while (!inUse.compareAndSet(false, true)) {
            Thread.onSpinWait();
        }
        sendUciCommand("ucinewgame");
        waitUntilReady();

        sendUciCommand("position fen " + state.getCurrentFen());
        sendUciCommand("go movetime 5");

        String bestMove = readUntil("bestmove").split(" ")[1];

        inUse.set(false);

        PieceType promotion = null;
        if (bestMove.length() == 5) {
            promotion = PieceType.fromAbbreviation(bestMove.charAt(4));
        }

        return new ChessMove(new ChessPosition(bestMove.substring(0, 2)), new ChessPosition(bestMove.substring(2, 4)), promotion);
    }
}
