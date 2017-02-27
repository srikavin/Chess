package me.infuzion.chess.piece.movement.type;

import me.infuzion.chess.ChessBoard;
import me.infuzion.chess.ChessPiece;
import me.infuzion.chess.ChessPosition;
import me.infuzion.chess.piece.Pawn;
import me.infuzion.chess.piece.movement.MoveType;

public class PawnMovement implements MoveType {

    @Override
    public boolean allowed(ChessBoard board, ChessPiece piece, ChessPosition start,
        ChessPosition end) {
        if (!(piece instanceof Pawn)) {
            return false;
        }

        int front;
        boolean isInFront = false;
        Pawn pawn = (Pawn) piece;

        int startRow = start.getX();
        int startCol = start.getY();
        int endRow = end.getX();
        int endCol = end.getY();

        switch (piece.getColor()) {
            case BLACK:
                front = startRow - 1;
                isInFront = endRow <= front;
                break;
            case WHITE:
                front = startRow + 1;
                isInFront = endRow >= front;
                break;
        }

        if (!isInFront) {
            return false;
        }

        //Moving forward one
        if (Math.abs(startRow - endRow) == 1) {
            //Same column
            if (startCol == endCol) {
                return board.getPiece(end) == null;
            }
            //Attacking a piece to the left/right and one forward
            if (Math.abs(startCol - endCol) == 1) {
                if (board.getPiece(end) != null) {
                    return true;
                }
            }
            return false;
        }

        //First move two forward
        return startCol == endCol && pawn.isFirstMove() && Math.abs(startRow - endRow) == 2
            && board.getPiece(end) == null;
    }
}
