package me.infuzion.chess.piece;

import me.infuzion.chess.board.BoardData;
import me.infuzion.chess.board.ChessMove;
import me.infuzion.chess.board.ChessPosition;
import me.infuzion.chess.piece.movement.type.DiagonalMovement;

public class Bishop extends ChessPiece {
    public Bishop(Color color, ChessPosition position) {
        super(color, position, PieceType.BISHOP);
    }

    @Override
    public boolean isMoveAllowedIgnoringCheck(BoardData data, ChessMove move, boolean excludeCastling) {
        return DiagonalMovement.allowed(data, this, move);
    }
}
