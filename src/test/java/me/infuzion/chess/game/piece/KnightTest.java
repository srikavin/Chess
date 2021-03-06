package me.infuzion.chess.game.piece;

import me.infuzion.chess.game.board.BoardData;
import me.infuzion.chess.game.board.ChessBoard;
import me.infuzion.chess.game.board.ChessPosition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KnightTest {

    @Test
    void getType() {
        Knight knight = new Knight(Color.WHITE, new ChessPosition(2, 3));
        assertEquals(knight.getType(), PieceType.KNIGHT);
    }

    @Test
    void allowed() {
        Knight knight = new Knight(Color.BLACK, new ChessPosition(3, 3));
        BoardData def = ChessBoard.getDefaultBoard().getData();

        def.setPiece(knight.currentPosition(), knight);

        assertTrue(knight.allowed(def, new ChessPosition(5, 4)));
        assertTrue(knight.allowed(def, new ChessPosition(5, 2)));
        assertTrue(knight.allowed(def, new ChessPosition(4, 1)));
        assertTrue(knight.allowed(def, new ChessPosition(2, 1)));
        assertTrue(knight.allowed(def, new ChessPosition(2, 5)));
        assertTrue(knight.allowed(def, new ChessPosition(4, 5)));

        assertFalse(knight.allowed(def, new ChessPosition(1, 1)));
        assertFalse(knight.allowed(def, new ChessPosition(1, 0)));
        assertFalse(knight.allowed(def, new ChessPosition(3, 5)));
        assertFalse(knight.allowed(def, new ChessPosition(5, 3)));
        assertFalse(knight.allowed(def, new ChessPosition(3, 3)));
    }

}