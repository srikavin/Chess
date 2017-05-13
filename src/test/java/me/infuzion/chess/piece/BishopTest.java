package me.infuzion.chess.piece;

import me.infuzion.chess.board.ChessBoard;
import me.infuzion.chess.board.ChessPosition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BishopTest {

    @Test
    void getType() {
        Bishop bishop = new Bishop(Color.BLACK, new ChessPosition(4, 4));
        assertEquals(bishop.getType(), PieceType.BISHOP);

    }

    @Test
    void allowed() {
        Bishop bishop = new Bishop(Color.WHITE, new ChessPosition(3, 3));

        assertTrue(bishop.allowed(ChessBoard.getDefaultBoard(), new ChessPosition(4, 4)));
        assertTrue(bishop.allowed(ChessBoard.getDefaultBoard(), new ChessPosition(1, 5)));

        assertFalse(bishop.allowed(ChessBoard.getDefaultBoard(), new ChessPosition(4, 5)));
        assertFalse(bishop.allowed(ChessBoard.getDefaultBoard(), new ChessPosition(6, 6)));
        assertFalse(bishop.allowed(ChessBoard.getDefaultBoard(), new ChessPosition(3, 5)));
        assertFalse(bishop.allowed(ChessBoard.getDefaultBoard(), new ChessPosition(2, 1)));
        assertFalse(bishop.allowed(ChessBoard.getDefaultBoard(), new ChessPosition(6, 7)));
        assertFalse(bishop.allowed(ChessBoard.getDefaultBoard(), new ChessPosition(3, 3)));
    }

}