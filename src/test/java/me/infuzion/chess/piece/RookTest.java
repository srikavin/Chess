package me.infuzion.chess.piece;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import me.infuzion.chess.ChessBoard;
import me.infuzion.chess.ChessPosition;
import org.junit.jupiter.api.Test;

class RookTest {

    @Test
    void getType() {
        Rook rook = new Rook(Color.BLACK, new ChessPosition(1, 1));
        assertTrue(rook.getType() == PieceType.ROOK);
    }

    @Test
    void allowed() {
        Rook rook = new Rook(Color.BLACK, new ChessPosition(5, 4));
        assertTrue(rook.allowed(ChessBoard.getDefaultBoard(), new ChessPosition(5, 5)));
        assertTrue(rook.allowed(ChessBoard.getDefaultBoard(), new ChessPosition(6, 4)));
        assertTrue(rook.allowed(ChessBoard.getDefaultBoard(), new ChessPosition(5, 5)));
        assertTrue(rook.allowed(ChessBoard.getDefaultBoard(), new ChessPosition(2, 4)));

        assertFalse(rook.allowed(ChessBoard.getDefaultBoard(), new ChessPosition(6, 5)));
        assertFalse(rook.allowed(ChessBoard.getDefaultBoard(), new ChessPosition(5, 0)));
        assertFalse(rook.allowed(ChessBoard.getDefaultBoard(), new ChessPosition(4, 5)));
        assertFalse(rook.allowed(ChessBoard.getDefaultBoard(), new ChessPosition(5, 7)));
        assertFalse(rook.allowed(ChessBoard.getDefaultBoard(), new ChessPosition(5, 4)));

    }

}