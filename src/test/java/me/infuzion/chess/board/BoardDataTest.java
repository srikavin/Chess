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

package me.infuzion.chess.board;

import me.infuzion.chess.piece.ChessPiece;
import me.infuzion.chess.piece.Color;
import me.infuzion.chess.piece.Pawn;
import me.infuzion.chess.piece.PieceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoardDataTest {

    @Test
    void getPieces() {
    }

    @Test
    void setPiece() {
    }

    @Test
    void testSetPiece() {
        BoardData data = ChessBoard.getDefaultBoard().getData();

        ChessPiece piece = new Pawn(Color.WHITE, new ChessPosition("d6"));

        data.setPiece(piece.currentPosition(), piece);

        assertEquals(piece, data.getPiece(piece.currentPosition()));
    }

    @Test
    void getPiece() {
        BoardData data = ChessBoard.getDefaultBoard().getData();
        assertEquals(data.getPiece(new ChessPosition("d2")).getType(), PieceType.PAWN);
        assertEquals(data.getPiece(new ChessPosition("d2")).getColor(), Color.WHITE);

        assertEquals(data.getPiece(new ChessPosition("d1")).getType(), PieceType.QUEEN);
        assertEquals(data.getPiece(new ChessPosition("d1")).getColor(), Color.WHITE);

        assertEquals(data.getPiece(new ChessPosition("a2")).getType(), PieceType.PAWN);
        assertEquals(data.getPiece(new ChessPosition("a2")).getColor(), Color.WHITE);

        assertEquals(data.getPiece(new ChessPosition("d8")).getType(), PieceType.QUEEN);
        assertEquals(data.getPiece(new ChessPosition("d8")).getColor(), Color.BLACK);

        assertEquals(data.getPiece(new ChessPosition("h8")).getType(), PieceType.ROOK);
        assertEquals(data.getPiece(new ChessPosition("h8")).getColor(), Color.BLACK);
    }

    @Test
    void testGetPiece() {
    }

    @Test
    void toFen() {
        ChessBoard data = ChessBoard.getDefaultBoard();
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", data.toFen());
    }
}