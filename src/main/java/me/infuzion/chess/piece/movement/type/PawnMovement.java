package me.infuzion.chess.piece.movement.type;

import me.infuzion.chess.ChessBoard;
import me.infuzion.chess.ChessPiece;
import me.infuzion.chess.ChessPosition;
import me.infuzion.chess.piece.Color;
import me.infuzion.chess.piece.movement.MoveType;

public class PawnMovement implements MoveType {

    @Override
    public boolean allowed(ChessBoard board, ChessPiece piece, ChessPosition start,
        ChessPosition end) {
        int inFront;
        if (piece.getColor() == Color.WHITE) {
            inFront = start.getY() + 1;
        } else {
            inFront = start.getY() - 1;
        }

        if (Math.abs(end.getX() - start.getX()) == 1) {
            System.out.println(1);
            return end.getY() == inFront && board.getPiece(end) != null;
        }

        int difference = Math.abs(end.getX() - inFront);
        if (difference <= 2) {
            if (difference == 2) {
                if (piece.getColor() == Color.BLACK) {
                    if (start.getY() != 6) {
                        System.out.println(start.getY());
                        return false;
                    }
                } else {
                    if (start.getY() != 1) {
                        System.out.println(start.getY());
                        return false;
                    }
                }
            }
            System.out.println(start);
            System.out.println(end);
            return start.getY() == end.getY()
                && (board.getPiece(end) == null);
        }
        System.out.println(5);
        return false;
    }
}
