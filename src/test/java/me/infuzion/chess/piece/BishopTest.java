package me.infuzion.chess.piece;

import me.infuzion.chess.board.BoardData;
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
        BoardData board = ChessBoard.getDefaultBoard().getData();

        Bishop bishop = new Bishop(Color.WHITE, new ChessPosition("d4"));

        board.setPiece(bishop.currentPosition(), bishop);

        assertTrue(bishop.allowed(board, new ChessPosition("c3")));
        assertTrue(bishop.allowed(board, new ChessPosition("e5")));
        assertTrue(bishop.allowed(board, new ChessPosition("c5")));
        assertTrue(bishop.allowed(board, new ChessPosition("b6")));
        assertTrue(bishop.allowed(board, new ChessPosition("a7")));
        assertTrue(bishop.allowed(board, new ChessPosition("g7")));

        assertFalse(bishop.allowed(board, new ChessPosition("d3")));
        assertFalse(bishop.allowed(board, new ChessPosition("c4")));
        assertFalse(bishop.allowed(board, new ChessPosition("f2")));
        assertFalse(bishop.allowed(board, new ChessPosition("g1")));
        assertFalse(bishop.allowed(board, new ChessPosition("h8")));
        assertFalse(bishop.allowed(board, new ChessPosition("h8")));
    }
}