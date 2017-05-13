package me.infuzion.chess.piece.movement.type;

import me.infuzion.chess.board.BoardData;
import me.infuzion.chess.board.ChessPosition;
import me.infuzion.chess.piece.ChessPiece;
import me.infuzion.chess.piece.movement.MoveType;

public class DiagonalMovement implements MoveType {

    /**
     * @inheritDoc
     */
    @Override
    public boolean allowed(BoardData board, ChessPiece piece, ChessPosition start,
                           ChessPosition end) {
        int startX = start.getRow();
        int startY = start.getCol();
        int endX = end.getRow();
        int endY = end.getCol();

        if (start.equals(end)) {
            return false;
        }

        if (Math.abs(startX - endX) != Math.abs(startY - endY)) {
            return false;
        }

        // Determines the direction of the movement
        int xMovement = (startX > endX) ? -1 : 1;
        int yMovement = (startY > endY) ? -1 : 1;

        int curX = startX;
        int curY = startY;
        curX += xMovement;
        curY += yMovement;
        while (true) {
            // Returns false if out of bounds
            if (curX > 8 || curX < 0 || curY > 8 || curY < 0) {
                return false;
            }
            // Returns true if the current x and y match the ending x and y
            if (curX == endX && curY == endY) {
                return true;
            }
            // Checks if the current piece is null. If it isn't return false because a piece is in
            // the way of movement
            if (board.getPiece(curX, curY) != null) {
                return false;
            }
            curX += xMovement;
            curY += yMovement;
        }
    }
}
