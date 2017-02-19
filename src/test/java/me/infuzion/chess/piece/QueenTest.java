package me.infuzion.chess.piece;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import me.infuzion.chess.ChessBoard;
import me.infuzion.chess.ChessPosition;
import org.junit.jupiter.api.Test;

class QueenTest {

    @Test
    void getType() {
        Queen queen = new Queen(Color.WHITE, new ChessPosition(2, 2));
        assertEquals(queen.getType(), PieceType.QUEEN);
    }

    @Test
    void allowed() {
        Queen queen = new Queen(Color.WHITE, new ChessPosition(2, 2));
        ChessBoard def = ChessBoard.getDefaultBoard();
        assertTrue(queen.allowed(def, new ChessPosition(3, 3)));
        assertTrue(queen.allowed(def, new ChessPosition(3, 2)));
        assertTrue(queen.allowed(def, new ChessPosition(2, 3)));
        assertTrue(queen.allowed(def, new ChessPosition(2, 6)));

        assertFalse(queen.allowed(def, new ChessPosition(4, 6)));
        assertFalse(queen.allowed(def, new ChessPosition(2, 7)));
        assertFalse(queen.allowed(def, new ChessPosition(2, 7)));
        assertFalse(queen.allowed(def, new ChessPosition(2, 2)));


    }

}