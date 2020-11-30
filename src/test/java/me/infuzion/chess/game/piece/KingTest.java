package me.infuzion.chess.game.piece;

import me.infuzion.chess.game.board.BoardData;
import me.infuzion.chess.game.board.ChessBoard;
import me.infuzion.chess.game.board.ChessMove;
import me.infuzion.chess.game.board.ChessPosition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KingTest {

    @Test
    void getType() {
        King king = new King(Color.WHITE, new ChessPosition(4, 4));
        assertEquals(king.getType(), PieceType.KING);
    }

    @Test
    void allowed() {
        King king = new King(Color.WHITE, new ChessPosition(4, 4));
        BoardData def = ChessBoard.getDefaultBoard().getData();

        def.setPiece(king.currentPosition(), king);

        assertTrue(king.allowed(def, new ChessPosition(5, 5)));
        assertTrue(king.allowed(def, new ChessPosition(5, 3)));
        assertTrue(king.allowed(def, new ChessPosition(3, 5)));
        assertTrue(king.allowed(def, new ChessPosition(5, 4)));
        assertTrue(king.allowed(def, new ChessPosition(4, 5)));

        assertFalse(king.allowed(def, new ChessPosition(6, 6)));
        assertFalse(king.allowed(def, new ChessPosition(6, 7)));
        assertFalse(king.allowed(def, new ChessPosition(2, 1)));
        assertFalse(king.allowed(def, new ChessPosition(2, 3)));
        assertFalse(king.allowed(def, new ChessPosition(4, 4)));
    }

    @Test
    void whiteQueenSideCastling() {
        King king = new King(Color.WHITE, new ChessPosition("e1"));
        ChessBoard board = ChessBoard.getDefaultBoard();
        BoardData def = board.getData();
        def.setPiece(king.currentPosition(), king);

        assertFalse(king.allowed(def, new ChessPosition("c1")));

        def.setPiece(new ChessPosition("b1"), null);
        def.setPiece(new ChessPosition("c1"), null);
        def.setPiece(new ChessPosition("d1"), null);


        // queen side castle
        assertTrue(king.allowed(def, new ChessPosition("c1")));
        assertFalse(king.allowed(def, new ChessPosition("g1")));
        assertFalse(king.allowed(def, new ChessPosition("c8")));
        assertFalse(king.allowed(def, new ChessPosition("g8")));

        // check rook position
        assertTrue(board.move(new ChessMove("e1", "c1")));
        assertEquals(PieceType.ROOK, def.getPiece(new ChessPosition("d1")).getType());

        def.getCastlingAvailability().remove(CastlingAvailability.WHITE_QUEEN_SIDE);
        assertFalse(king.allowed(def, new ChessPosition("c1")));
        assertFalse(king.allowed(def, new ChessPosition("g1")));
        assertFalse(king.allowed(def, new ChessPosition("c8")));
        assertFalse(king.allowed(def, new ChessPosition("g8")));
    }

    @Test
    void whiteKingSideCastling() {
        King king = new King(Color.WHITE, new ChessPosition("e1"));
        ChessBoard board = ChessBoard.getDefaultBoard();
        BoardData def = board.getData();
        def.setPiece(king.currentPosition(), king);

        assertFalse(king.allowed(def, new ChessPosition("g1")));

        def.setPiece(new ChessPosition("f1"), null);
        def.setPiece(new ChessPosition("g1"), null);


        // queen side castle
        assertFalse(king.allowed(def, new ChessPosition("c1")));
        assertTrue(king.allowed(def, new ChessPosition("g1")));
        assertFalse(king.allowed(def, new ChessPosition("c8")));
        assertFalse(king.allowed(def, new ChessPosition("g8")));

        // check rook position
        assertTrue(board.move(new ChessMove("e1", "g1")));
        assertEquals(PieceType.ROOK, def.getPiece(new ChessPosition("f1")).getType());

        def.getCastlingAvailability().remove(CastlingAvailability.WHITE_KING_SIDE);
        assertFalse(king.allowed(def, new ChessPosition("c1")));
        assertFalse(king.allowed(def, new ChessPosition("g1")));
        assertFalse(king.allowed(def, new ChessPosition("c8")));
        assertFalse(king.allowed(def, new ChessPosition("g8")));
    }

    @Test
    void blackQueenSideCastling() {
        King king = new King(Color.BLACK, new ChessPosition("e8"));
        ChessBoard board = ChessBoard.getDefaultBoard();
        BoardData def = board.getData();
        def.setPiece(king.currentPosition(), king);

        assertFalse(king.allowed(def, new ChessPosition("c8")));

        def.setPiece(new ChessPosition("b8"), null);
        def.setPiece(new ChessPosition("c8"), null);
        def.setPiece(new ChessPosition("d8"), null);


        // queen side castle
        assertFalse(king.allowed(def, new ChessPosition("c1")));
        assertFalse(king.allowed(def, new ChessPosition("g1")));
        assertTrue(king.allowed(def, new ChessPosition("c8")));
        assertFalse(king.allowed(def, new ChessPosition("g8")));

        // make move to change current turn color
        assertTrue(board.move(new ChessMove("a2", "a3")));

        // check rook position
        assertTrue(board.move(new ChessMove("e8", "c8")));
        assertEquals(PieceType.ROOK, def.getPiece(new ChessPosition("d8")).getType());

        def.getCastlingAvailability().remove(CastlingAvailability.BLACK_QUEEN_SIDE);
        assertFalse(king.allowed(def, new ChessPosition("c1")));
        assertFalse(king.allowed(def, new ChessPosition("g1")));
        assertFalse(king.allowed(def, new ChessPosition("c8")));
        assertFalse(king.allowed(def, new ChessPosition("g8")));
    }

    @Test
    void blackKingSideCastling() {
        King king = new King(Color.BLACK, new ChessPosition("e8"));
        ChessBoard board = ChessBoard.getDefaultBoard();
        BoardData def = board.getData();
        def.setPiece(king.currentPosition(), king);

        assertFalse(king.allowed(def, new ChessPosition("g8")));

        def.setPiece(new ChessPosition("f8"), null);
        def.setPiece(new ChessPosition("g8"), null);

        // queen side castle
        assertFalse(king.allowed(def, new ChessPosition("c1")));
        assertFalse(king.allowed(def, new ChessPosition("g1")));
        assertFalse(king.allowed(def, new ChessPosition("c8")));
        assertTrue(king.allowed(def, new ChessPosition("g8")));

        // make move to change current turn color
        assertTrue(board.move(new ChessMove("a2", "a3")));

        // check rook position
        assertTrue(board.move(new ChessMove("e8", "g8")));
        assertEquals(PieceType.ROOK, def.getPiece(new ChessPosition("f8")).getType());

        def.getCastlingAvailability().remove(CastlingAvailability.BLACK_KING_SIDE);
        assertFalse(king.allowed(def, new ChessPosition("c1")));
        assertFalse(king.allowed(def, new ChessPosition("g1")));
        assertFalse(king.allowed(def, new ChessPosition("c8")));
        assertFalse(king.allowed(def, new ChessPosition("g8")));
    }
}