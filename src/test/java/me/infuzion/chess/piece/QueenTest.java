package me.infuzion.chess.piece;

import me.infuzion.chess.board.BoardData;
import me.infuzion.chess.board.ChessBoard;
import me.infuzion.chess.board.ChessPosition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueenTest {

    @Test
    void getType() {
        Queen queen = new Queen(Color.WHITE, new ChessPosition(2, 2));
        assertEquals(queen.getType(), PieceType.QUEEN);
    }

    @Test
    void allowed() {
        Queen queen = new Queen(Color.WHITE, new ChessPosition(2, 2));
        BoardData def = ChessBoard.getDefaultBoard().getData();

        def.setPiece(queen.currentPosition(), queen);

        assertTrue(queen.allowed(def, new ChessPosition(3, 3)));
        assertTrue(queen.allowed(def, new ChessPosition(3, 2)));
        assertTrue(queen.allowed(def, new ChessPosition(2, 3)));
        assertTrue(queen.allowed(def, new ChessPosition(2, 6)));
        assertTrue(queen.allowed(def, new ChessPosition(7, 2)));

        assertEquals(new ChessPosition(2, 2), queen.currentPosition());
        assertFalse(queen.allowed(def, new ChessPosition(4, 6)));
        assertFalse(queen.allowed(def, new ChessPosition(2, 1)));
    }
}