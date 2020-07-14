package me.infuzion.chess.piece.movement;

import me.infuzion.chess.board.BoardData;
import me.infuzion.chess.board.ChessMove;
import me.infuzion.chess.piece.ChessPiece;

public interface MoveType {
    /**
     * @param data  Board data to use to calculate if the movement is possible
     * @param piece The piece to check the movement of
     * @param move  The move to check
     * @return Returns true if the movement is allowed; False otherwise.
     */
    boolean allowed(BoardData data, ChessPiece piece, ChessMove move);
}
