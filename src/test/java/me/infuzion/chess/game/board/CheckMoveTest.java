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

package me.infuzion.chess.game.board;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckMoveTest {
    @Test
    void sameColorCheck() {
        //should be allowed
        ChessBoard board = ChessBoard.fromFen("rn1P1b1r/p3k3/7n/B7/3p4/7p/PPP1P2P/R2QK2R w KQ - 0 18");

        System.out.println(board);
        Arrays.stream(board.getData().getPieces()).forEach(e -> System.out.println(Arrays.toString(e)));

        assertTrue(board.move(new ChessMove("h1", "g1")));

        board = ChessBoard.fromFen("rn1P1b1r/p3k3/7n/B7/3p4/7p/PPP1P2P/R2QK2R w KQ - 0 18");
        assertTrue(board.move(new ChessMove("h1", "f1")));
    }

    @Test
    void checkOpponent() {
        ChessBoard board = ChessBoard.fromFen("r6r/p6P/3K4/8/3k4/8/B7/R6R w - - 57 73");

        assertTrue(board.move(new ChessMove("h1", "d1")));
    }
}
