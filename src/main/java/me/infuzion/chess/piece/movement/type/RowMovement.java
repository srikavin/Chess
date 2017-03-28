package me.infuzion.chess.piece.movement.type;

import me.infuzion.chess.BoardData;
import me.infuzion.chess.ChessPiece;
import me.infuzion.chess.ChessPosition;
import me.infuzion.chess.piece.movement.MoveType;

public class RowMovement implements MoveType {

    @Override
    public boolean allowed(BoardData board, ChessPiece piece, ChessPosition start,
                           ChessPosition end) {
        if (start.equals(end)) {
            return false;
        }
        ChessPiece[][] pieces = board.getPieces();
        int cRow = start.getRow();
        int cCol = start.getCol();
        int eRow = end.getRow();
        int eCol = end.getCol();
        if (cRow != eRow && cCol != eCol) {
            return false;
        }
        if (cRow != eRow) {
            int offset = getOffset(cRow, eRow);
            for (int i = cRow + offset; i != eRow; i += offset) {
                if (board.getPiece(i, cCol) != null) {
                    return false;
                }
            }
        }
        if (cCol != eCol) {
            int offset = getOffset(cCol, eCol);
            for (int i = cCol + offset; i != eCol; i += offset) {
                if (board.getPiece(cRow, i) != null) {
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
