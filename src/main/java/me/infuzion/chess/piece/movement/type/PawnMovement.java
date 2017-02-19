package me.infuzion.chess.piece.movement.type;

import me.infuzion.chess.ChessBoard;
import me.infuzion.chess.ChessPiece;
import me.infuzion.chess.ChessPosition;
import me.infuzion.chess.piece.Color;
import me.infuzion.chess.piece.movement.MoveType;

public class PawnMovement implements MoveType {

    @Override
    public boolean allowed(ChessBoard board, ChessPiece piece, ChessPosition start, ChessPosition end) {
        int inFront;
        if (piece.getColor() == Color.WHITE) {
            inFront = start.getY() + 1;
        } else {
            inFront = start.getY() - 1;
        }

        if (Math.abs(end.getX() - start.getX()) == 1) {
            if (end.getY() == inFront) {
                return board.getPieces()[end.getX()][end.getY()] != null;
            }
        }

        return (end.getY() == inFront)
            && (board.getPieces()[end.getX()][end.getY()] == null);
    }
}
