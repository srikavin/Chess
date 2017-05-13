package me.infuzion.chess.piece.movement.type;

import me.infuzion.chess.board.BoardData;
import me.infuzion.chess.board.ChessPosition;
import me.infuzion.chess.piece.ChessPiece;
import me.infuzion.chess.piece.movement.MoveType;

public class KnightMovement implements MoveType {

    @Override
    public boolean allowed(BoardData board, ChessPiece piece, ChessPosition start,
                           ChessPosition end) {
        int startX = start.getRow();
        int startY = start.getCol();
        int endX = end.getRow();
        int endY = end.getCol();

        int differenceX = Math.abs(startX - endX);
        int differenceY = Math.abs(startY - endY);
        if (differenceX == 1) {
            if (differenceY == 2) {
                return true;
            }
        }
        if (differenceX == 2) {
            if (differenceY == 1) {
                return true;
            }
        }
        return false;
    }
}
