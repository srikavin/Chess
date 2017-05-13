package me.infuzion.chess.piece;

import me.infuzion.chess.board.ChessBoard;
import me.infuzion.chess.board.ChessPosition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RookTest {

    @Test
    void getType() {
        Rook rook = new Rook(Color.BLACK, new ChessPosition(1, 1));
        assertEquals(rook.getType(), PieceType.ROOK);
    }

    @Test
    void allowed() {
        Rook rook = new Rook(Color.WHITE, new ChessPosition("e3"));
        assertTrue(rook.allowed(ChessBoard.getDefaultBoard(), new ChessPosition("f3")));
        assertTrue(rook.allowed(ChessBoard.getDefaultBoard(), new ChessPosition("e7")));
        assertTrue(rook.allowed(ChessBoard.getDefaultBoard(), new ChessPosition("e6")));
        assertTrue(rook.allowed(ChessBoard.getDefaultBoard(), new ChessPosition("e5")));
        assertTrue(rook.allowed(ChessBoard.getDefaultBoard(), new ChessPosition("e4")));

        assertFalse(rook.allowed(ChessBoard.getDefaultBoard(), new ChessPosition("e2")));
        assertFalse(rook.allowed(ChessBoard.getDefaultBoard(), new ChessPosition("d4")));
        assertFalse(rook.allowed(ChessBoard.getDefaultBoard(), new ChessPosition("a8")));
        assertFalse(rook.allowed(ChessBoard.getDefaultBoard(), new ChessPosition("a1")));
        assertFalse(rook.allowed(ChessBoard.getDefaultBoard(), new ChessPosition("f7")));

    }

}