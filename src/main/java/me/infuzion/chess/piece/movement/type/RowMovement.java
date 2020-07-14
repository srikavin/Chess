package me.infuzion.chess.piece.movement.type;

import me.infuzion.chess.board.BoardData;
import me.infuzion.chess.board.ChessMove;
import me.infuzion.chess.board.ChessPosition;
import me.infuzion.chess.piece.ChessPiece;

public class RowMovement {
    public static boolean allowed(BoardData board, ChessPiece piece, ChessMove move) {
        ChessPosition start = move.getSource();
        ChessPosition end = move.getEnd();

        int cRow = start.getRank();
        int cCol = start.getFile();
        int eRow = end.getRank();
        int eCol = end.getFile();


        int xOffset = getOffset(cRow, eRow);
        int yOffset = getOffset(cCol, eCol);

        if ((xOffset != 1 || yOffset != 0) && (yOffset != 1 || xOffset != 0)) {
            return false;
        }

        int curX = cRow;
        int curY = cCol;

        while (0 < curX && curX < 8 && 0 < curY && curY < 8) {
            curX += xOffset;
            curY += yOffset;

            if (curX == eRow && curY == eCol) {
                return true;
            }

            if (board.getPiece(curX, curY) != null) {
                System.out.println(3);
                return false;
            }
        }

        return curX == eRow && curY == eCol;
    }

    private static int getOffset(int current, int end) {
        return Integer.compare(end, current);
    }
}
