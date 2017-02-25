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
    void allowed() {
        Pawn whitePawn = new Pawn(Color.WHITE, new ChessPosition(3, 1));
        assertTrue(whitePawn.allowed(ChessBoard.getDefaultBoard(),
            new ChessPosition(3, 3)));
        assertFalse(whitePawn.allowed(ChessBoard.getDefaultBoard(),
            new ChessPosition(2,  1)));

        Pawn whitePawn2 = new Pawn(Color.WHITE, new ChessPosition(6, 5));
        assertTrue(whitePawn2.allowed(ChessBoard.getDefaultBoard(),
            new ChessPosition(7, 6)));

        Pawn blackPawn = new Pawn(Color.BLACK, new ChessPosition(2, 5));
        assertFalse(blackPawn.allowed(ChessBoard.getDefaultBoard(),
            new ChessPosition(2, 7)));

    }

}