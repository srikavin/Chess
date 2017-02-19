package me.infuzion.chess.piece;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import me.infuzion.chess.ChessBoard;
import me.infuzion.chess.ChessPosition;
import org.junit.jupiter.api.Test;

class KingTest {

    @Test
    void getType() {
        King king = new King(Color.WHITE, new ChessPosition(4, 4));
        assertEquals(king.getType(), PieceType.KING);
    }

    @Test
    void allowed() {
        King king = new King(Color.WHITE, new ChessPosition(4, 4));
        ChessBoard def = ChessBoard.getDefaultBoard();

        assertTrue(king.allowed(def, new ChessPosition(5, 5)));
        assertTrue(king.allowed(def, new ChessPosition(5, 3)));
        assertTrue(king.allowed(def, new ChessPosition(3, 5)));
        assertTrue(king.allowed(def, new ChessPosition(5, 4)));
        assertTrue(king.allowed(def, new ChessPosition(4, 5)));

        assertFalse(king.allowed(def, new ChessPosition(6, 6)));
        assertFalse(king.allowed(def, new ChessPosition(6, 7)));
        assertFalse(king.allowed(def, new ChessPosition(2, 1)));
        assertFalse(king.allowed(def, new ChessPosition(2, 3)));
        assertFalse(king.allowed(def, new ChessPosition(4, 4)));
    }

}