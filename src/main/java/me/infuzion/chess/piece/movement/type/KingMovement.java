package me.infuzion.chess.piece.movement.type;

import me.infuzion.chess.BoardData;
import me.infuzion.chess.ChessPiece;
import me.infuzion.chess.ChessPosition;
import me.infuzion.chess.piece.movement.MoveType;

public class KingMovement implements MoveType {

    @Override
    public boolean allowed(BoardData board, ChessPiece piece, ChessPosition start,
                           ChessPosition end) {
        int startX = start.getRow();
        int startY = start.getCol();
        int endX = end.getRow();
        int endY = end.getCol();

        int differenceX = Math.abs(startX - endX);
        int differenceY = Math.abs(startY - endY);

        return (differenceX == 1 && differenceY == 1)
            || (differenceX == 0 && differenceY == 1)
            || (differenceX == 1 && differenceY == 0);

    }
}
