package me.infuzion.chess.piece.movement.type;

import me.infuzion.chess.ChessBoard;
import me.infuzion.chess.ChessPiece;
import me.infuzion.chess.ChessPosition;
import me.infuzion.chess.piece.movement.MoveType;

public class DiagonalMovement implements MoveType {

    /**
     * @inheritDoc
     */
    @Override
    public boolean allowed(ChessBoard board, ChessPiece piece, ChessPosition start, ChessPosition end) {
        int startX = start.getX();
        int startY = start.getY();
        int endX = end.getX();
        int endY = end.getY();

        if(Math.abs(startX - endX) != Math.abs(startY - endY)){
            return false;
        }


        return false;
    }
}
