package me.infuzion.chess.game.piece.movement;

import me.infuzion.chess.game.board.BoardData;
import me.infuzion.chess.game.board.ChessMove;
import me.infuzion.chess.game.piece.ChessPiece;

public interface MoveType {
    /**
     * @param data  Board data to use to calculate if the movement is possible
     * @param piece The piece to check the movement of
     * @param move  The move to check
     * @return Returns true if the movement is allowed; False otherwise.
     */
    boolean allowed(BoardData data, ChessPiece piece, ChessMove move);
}
