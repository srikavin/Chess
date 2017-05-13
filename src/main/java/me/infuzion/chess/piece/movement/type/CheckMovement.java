package me.infuzion.chess.piece.movement.type;

import me.infuzion.chess.board.BoardData;
import me.infuzion.chess.board.ChessBoard;
import me.infuzion.chess.board.ChessPosition;
import me.infuzion.chess.piece.ChessPiece;
import me.infuzion.chess.piece.movement.MoveType;

public class CheckMovement implements MoveType {

    @Override
    public boolean allowed(BoardData data, ChessPiece piece, ChessPosition start, ChessPosition end) {
        if (ChessBoard.isUnderCheck(piece.getColor(), data)) {
            return !ChessBoard.checkAfterMove(data, piece.getColor(), start, end);
        }
        return true;
    }
}
