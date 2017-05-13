package me.infuzion.chess.piece.movement;

import me.infuzion.chess.board.BoardData;
import me.infuzion.chess.board.ChessPosition;
import me.infuzion.chess.piece.ChessPiece;

public interface MoveType {

    /**
     *
     * @param data Board data to use to calculate if the movement is possible
     * @param piece The piece to check the movement of
     * @param start The start position of the piece being moved
     * @param end The end position of the piece being moved
     * @return Returns true if the movement is allowed; False otherwise.
     */
    boolean allowed(BoardData data, ChessPiece piece, ChessPosition start, ChessPosition end);
}
