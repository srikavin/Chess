package me.infuzion.chess.game.piece.movement.type;

import me.infuzion.chess.game.board.BoardData;
import me.infuzion.chess.game.board.ChessMove;
import me.infuzion.chess.game.board.ChessPosition;
import me.infuzion.chess.game.piece.ChessPiece;

public class DiagonalMovement {
    public static boolean allowed(BoardData board, ChessPiece piece, ChessMove move) {
        ChessPosition start = move.getSource();
        ChessPosition end = move.getEnd();

        int startX = start.getRank();
        int startY = start.getFile();
        int endX = end.getRank();
        int endY = end.getFile();

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
            if (curX >= 8 || curX < 0 || curY >= 8 || curY < 0) {
                return false;
            }

            // Checks if the current piece is null. If it isn't return false because a piece is in
            // the way of movement
            if (board.getPiece(curX, curY) != null) {
                if (curX != endX || curY != endY) {
                    return false;
                }

                //ensure captured piece is of opposite color
                return board.getPiece(curX, curY).getColor() != piece.getColor();
            }

            // Returns true if the current x and y match the ending x and y
            if (curX == endX && curY == endY) {
                return true;
            }
            curX += xMovement;
            curY += yMovement;
        }
    }
}
