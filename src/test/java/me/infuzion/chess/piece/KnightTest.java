package me.infuzion.chess.piece;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import me.infuzion.chess.ChessBoard;
import me.infuzion.chess.ChessPosition;
import org.junit.jupiter.api.Test;

class KnightTest {

    @Test
    void getType() {
        Knight knight = new Knight(Color.WHITE, new ChessPosition(2, 3));
        assertEquals(knight.getType(), PieceType.KNIGHT);
    }

    @Test
    void allowed() {
        Knight knight = new Knight(Color.WHITE, new ChessPosition(3, 3));
        ChessBoard def = ChessBoard.getDefaultBoard();

        assertTrue(knight.allowed(def, new ChessPosition(5, 4)));
        assertTrue(knight.allowed(def, new ChessPosition(5, 2)));
        assertTrue(knight.allowed(def, new ChessPosition(4, 1)));
        assertTrue(knight.allowed(def, new ChessPosition(2, 1)));
        assertTrue(knight.allowed(def, new ChessPosition(1, 2)));
        assertTrue(knight.allowed(def, new ChessPosition(2, 5)));
        assertTrue(knight.allowed(def, new ChessPosition(4, 5)));

        assertFalse(knight.allowed(def, new ChessPosition(1, 1)));
        assertFalse(knight.allowed(def, new ChessPosition(1, 0)));
        assertFalse(knight.allowed(def, new ChessPosition(3, 5)));
        assertFalse(knight.allowed(def, new ChessPosition(5, 3)));
        assertFalse(knight.allowed(def, new ChessPosition(3, 3)));
    }

}