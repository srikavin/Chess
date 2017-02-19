package me.infuzion.chess.piece.movement.type;

import me.infuzion.chess.ChessBoard;
import me.infuzion.chess.ChessPiece;
import me.infuzion.chess.ChessPosition;
import me.infuzion.chess.piece.movement.MoveType;

public class KingMovement implements MoveType {

    @Override
    public boolean allowed(ChessBoard board, ChessPiece piece, ChessPosition start,
        ChessPosition end) {
        int startX = start.getX();
        int startY = start.getY();
        int endX = end.getX();
        int endY = end.getY();

        int differenceX = Math.abs(startX - endX);
        int differenceY = Math.abs(startY - endY);

        return (differenceX == 1 && differenceY == 1)
            || (differenceX == 0 && differenceY == 1)
            || (differenceX == 1 && differenceY == 0);

    }
}
