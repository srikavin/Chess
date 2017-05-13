package me.infuzion.chess.util;

import me.infuzion.chess.board.ChessBoard;
import me.infuzion.chess.piece.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PGNParserTest {
    @Test
    void executeMoves() {
        ChessBoard board = ChessBoard.getDefaultBoard();
        String test = "1. Pe4 Pf5";
        PGNParser.executeMoves(board, test, Color.WHITE);

        assertEquals(test, board.toPGNString().trim());

        board = ChessBoard.getDefaultBoard();
        test = "1. e4 e5 2. Nf3 Nc6 3. Bb5 a6";
        PGNParser.executeMoves(board, test, Color.WHITE);

        assertEquals("r1bqkbnr/1ppp1ppp/p1n/1B2p/4P/5N/PPPP1PPP/RNBQK2R", board.toFen());
    }

}