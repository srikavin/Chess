package me.infuzion.chess.game.piece;

import me.infuzion.chess.game.board.BoardData;
import me.infuzion.chess.game.board.ChessMove;
import me.infuzion.chess.game.board.ChessPosition;

public class Knight extends ChessPiece {
    public Knight(Color color, ChessPosition position) {
        super(color, position, PieceType.KNIGHT);
    }

    @Override
    public boolean isMoveAllowedIgnoringCheck(BoardData data, ChessMove move, boolean excludeCastling) {
        ChessPosition start = move.getSource();
        ChessPosition end = move.getEnd();

        int startRank = start.getRank();
        int startFile = start.getFile();
        int endRank = end.getRank();
        int endFile = end.getFile();

        int differenceX = Math.abs(startRank - endRank);
        int differenceY = Math.abs(startFile - endFile);

        if (differenceX == 1 && differenceY == 2) {
            return true;
        }

        return differenceX == 2 && differenceY == 1;
    }
}
