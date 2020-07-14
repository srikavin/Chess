package me.infuzion.chess.piece;

import me.infuzion.chess.board.BoardData;
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
        BoardData board = ChessBoard.getDefaultBoard().getData();

        Pawn blackPawn = new Pawn(Color.BLACK, new ChessPosition("e7"));
        Pawn whitePawn = new Pawn(Color.WHITE, new ChessPosition("e2"));

        //Moving forward 2
        blackPawn.allowed(board, new ChessPosition("e5"));
        //Moving forward 1
        assertTrue(blackPawn.allowed(board, new ChessPosition("e6")));

        assertFalse(blackPawn.allowed(board, new ChessPosition("e3")));
        assertFalse(blackPawn.allowed(board, new ChessPosition("f6")));

        //Moving forward 2
        assertTrue(whitePawn.allowed(board, new ChessPosition("e4")));
        //Moving forward 1
        assertTrue(whitePawn.allowed(board, new ChessPosition("e3")));

        assertFalse(whitePawn.allowed(board, new ChessPosition("f3")));
        assertFalse(whitePawn.allowed(board, new ChessPosition("e5")));
    }

    @Test
    void pawnCapturing() {
        BoardData board = ChessBoard.getDefaultBoard().getData();

        Pawn blackPawn = new Pawn(Color.BLACK, new ChessPosition("c3"));
        Pawn whitePawn = new Pawn(Color.WHITE, new ChessPosition("b6"));

        board.setPiece(blackPawn.currentPosition(), blackPawn);
        board.setPiece(whitePawn.currentPosition(), whitePawn);

        //Attacking left
        assertTrue(whitePawn.allowed(board, new ChessPosition("a7")));
        //Attacking right
        whitePawn.allowed(board, new ChessPosition("c7"));
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

    @Test
    void enPassant() {
        BoardData board = ChessBoard.getDefaultBoard().getData();

        Pawn blackPawn = new Pawn(Color.BLACK, new ChessPosition("c4"));
        Pawn whitePawn = new Pawn(Color.WHITE, new ChessPosition("b5"));

        board.setPiece(blackPawn.currentPosition(), blackPawn);
        board.setPiece(whitePawn.currentPosition(), whitePawn);

        //en passant left
        board.setEnPassantSquare(new ChessPosition("c6"));
        assertTrue(whitePawn.allowed(board, new ChessPosition("c6")));
        //en passant right
        board.setEnPassantSquare(new ChessPosition("a6"));
        assertTrue(whitePawn.allowed(board, new ChessPosition("a6")));

        board.setEnPassantSquare(null);
        assertFalse(whitePawn.allowed(board, new ChessPosition("a6")));

        board.setEnPassantSquare(new ChessPosition("b7"));
        assertFalse(whitePawn.allowed(board, new ChessPosition("b5")));
        assertFalse(whitePawn.allowed(board, new ChessPosition("c6")));

        //en passant left
        board.setEnPassantSquare(new ChessPosition("d3"));
        assertTrue(blackPawn.allowed(board, new ChessPosition("d3")));
        //en passant right
        board.setEnPassantSquare(new ChessPosition("b3"));
        assertTrue(blackPawn.allowed(board, new ChessPosition("b3")));

        board.setEnPassantSquare(new ChessPosition("e3"));
        assertFalse(blackPawn.allowed(board, new ChessPosition("e3")));
    }
}