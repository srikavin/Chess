package me.infuzion.chess.piece;

import me.infuzion.chess.board.ChessBoard;
import me.infuzion.chess.board.ChessPosition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PawnTest {

    @Test
    void getType() {
        Pawn pawn = new Pawn(Color.WHITE, new ChessPosition(2, 2));
        assertEquals(pawn.getType(), PieceType.PAWN);
    }

    @Test
    void pawnForward() {
        ChessBoard board = ChessBoard.getDefaultBoard();

        Pawn blackPawn = new Pawn(Color.BLACK, new ChessPosition(1, 1));
        Pawn whitePawn = new Pawn(Color.WHITE, new ChessPosition(6, 1));

        //Moving forward 2
        assertTrue(blackPawn.allowed(board, new ChessPosition(3, 1)));
        //Moving forward 1
        assertTrue(blackPawn.allowed(board, new ChessPosition(2, 1)));

        assertFalse(blackPawn.allowed(board, new ChessPosition(2, 2)));
        assertFalse(blackPawn.allowed(board, new ChessPosition(0, 1)));

        //Moving forward 2
        assertTrue(whitePawn.allowed(board, new ChessPosition(4, 1)));
        //Moving forward 1
        assertTrue(whitePawn.allowed(board, new ChessPosition(5, 1)));

        assertFalse(whitePawn.allowed(board, new ChessPosition(5, 2)));
        assertFalse(whitePawn.allowed(board, new ChessPosition(5, 3)));
    }

    @Test
    void pawnCapturing() {
        ChessBoard board = ChessBoard.getDefaultBoard();

        Pawn blackPawn = new Pawn(Color.BLACK, new ChessPosition("c3"));
        Pawn whitePawn = new Pawn(Color.WHITE, new ChessPosition("b6"));

        //Attacking left
        assertTrue(whitePawn.allowed(board, new ChessPosition("a7")));
        //Attacking right
        assertTrue(whitePawn.allowed(board, new ChessPosition("c7")));

        assertFalse(whitePawn.allowed(board, new ChessPosition("a6")));
        assertFalse(whitePawn.allowed(board, new ChessPosition("b5")));
        assertFalse(whitePawn.allowed(board, new ChessPosition("c6")));

        //Attacking left
        assertTrue(blackPawn.allowed(board, new ChessPosition("b2")));
        //Attacking right
        assertTrue(blackPawn.allowed(board, new ChessPosition("d2")));

        assertFalse(blackPawn.allowed(board, new ChessPosition("c4")));
        assertFalse(blackPawn.allowed(board, new ChessPosition("d3")));
    }

}