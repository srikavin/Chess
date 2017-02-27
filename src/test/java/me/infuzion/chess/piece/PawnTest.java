package me.infuzion.chess.piece;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import me.infuzion.chess.ChessBoard;
import me.infuzion.chess.ChessPosition;
import org.junit.jupiter.api.Test;

class PawnTest {

    @Test
    void getType() {
        Pawn pawn = new Pawn(Color.WHITE, new ChessPosition(2, 2));
        assertEquals(pawn.getType(), PieceType.PAWN);
    }

    @Test
    void pawnForward() {
        ChessBoard board = ChessBoard.getDefaultBoard();

        Pawn whitePawn = new Pawn(Color.WHITE, new ChessPosition(1, 1));

        //Moving forward 2
        assertTrue(whitePawn.allowed(board, new ChessPosition(3, 1)));
        //Moving forward 1
        assertTrue(whitePawn.allowed(board, new ChessPosition(2, 1)));

        assertFalse(whitePawn.allowed(board, new ChessPosition(2, 2)));
        assertFalse(whitePawn.allowed(board, new ChessPosition(0, 1)));

        Pawn blackPawn = new Pawn(Color.BLACK, new ChessPosition(6, 1));
        //Moving forward 2
        assertTrue(blackPawn.allowed(board, new ChessPosition(4, 1)));
        //Moving forward 1
        assertTrue(blackPawn.allowed(board, new ChessPosition(5, 1)));

        assertFalse(blackPawn.allowed(board, new ChessPosition(5, 2)));
        assertFalse(blackPawn.allowed(board, new ChessPosition(5, 3)));
    }

    @Test
    void pawnCapturing() {
        ChessBoard board = ChessBoard.getDefaultBoard();

        Pawn whitePawn = new Pawn(Color.WHITE, new ChessPosition(5, 2));
        //Attacking left
        assertTrue(whitePawn.allowed(board, new ChessPosition(6, 1)));
        //Attacking right
        assertTrue(whitePawn.allowed(board, new ChessPosition(6, 3)));

        assertFalse(whitePawn.allowed(board, new ChessPosition(6, 2)));
        assertFalse(whitePawn.allowed(board, new ChessPosition(7, 2)));

        Pawn blackPawn = new Pawn(Color.BLACK, new ChessPosition(2, 3));
        //Attacking left
        assertTrue(blackPawn.allowed(board, new ChessPosition(1, 2)));
        //Attacking right
        assertTrue(blackPawn.allowed(board, new ChessPosition(1, 4)));

        assertFalse(blackPawn.allowed(board, new ChessPosition(0, 3)));
        assertFalse(blackPawn.allowed(board, new ChessPosition(1, 3)));
    }

}