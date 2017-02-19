package me.infuzion.chess.piece.movement.type;

import me.infuzion.chess.ChessBoard;
import me.infuzion.chess.ChessPiece;
import me.infuzion.chess.ChessPosition;
import me.infuzion.chess.piece.movement.MoveType;

public class RowMovement implements MoveType {

    @Override
    public boolean allowed(ChessBoard board, ChessPiece piece, ChessPosition start,
        ChessPosition end) {
        if (start.equals(end)) {
            return false;
        }
        ChessPiece[][] pieces = board.getPieces();
        int cX = start.getX();
        int cY = start.getY();
        int eX = end.getX();
        int eY = end.getY();
        if (cX != eX && cY != eY) {
            return false;
        }
        if (cX != eX) {
            int offset = getOffset(cX, eX);
            for (int i = cX; i != eX; i += offset) {
                if (pieces[i][cY] != null) {
                    return false;
                }
            }
        }
        if (cY != eY) {
            int offset = getOffset(cY, eY);
            for (int i = cY; i != eY; i += offset) {
                if (pieces[cX][i] != null) {
                    return false;
                }
            }
        }
        return true;
    }

    private int getOffset(int current, int end) {
        if (current < end) {
            return 1;
        } else {
            return -1;
        }
    }

}
