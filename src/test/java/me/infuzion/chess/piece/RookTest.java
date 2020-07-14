package me.infuzion.chess.piece;

import me.infuzion.chess.board.BoardData;
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
        ChessBoard board = ChessBoard.getDefaultBoard();
        BoardData def = board.getData();

        def.setPiece(rook.currentPosition(), rook);

        assertTrue(rook.allowed(def, new ChessPosition("f3")));
        assertTrue(rook.allowed(def, new ChessPosition("e7")));
        assertTrue(rook.allowed(def, new ChessPosition("e6")));
        assertTrue(rook.allowed(def, new ChessPosition("e5")));
        assertTrue(rook.allowed(def, new ChessPosition("e4")));

        assertFalse(rook.allowed(def, new ChessPosition("e2")));
        assertFalse(rook.allowed(def, new ChessPosition("d4")));
        assertFalse(rook.allowed(def, new ChessPosition("a8")));
        assertFalse(rook.allowed(def, new ChessPosition("a1")));
        assertFalse(rook.allowed(def, new ChessPosition("f7")));

        def.setPiece(new ChessPosition("g3"), new Pawn(Color.WHITE, new ChessPosition("g3")));

        assertFalse(rook.allowed(def, new ChessPosition("h3")));
    }

}