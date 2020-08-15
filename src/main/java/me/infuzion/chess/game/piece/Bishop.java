package me.infuzion.chess.game.piece;

import me.infuzion.chess.game.board.BoardData;
import me.infuzion.chess.game.board.ChessMove;
import me.infuzion.chess.game.board.ChessPosition;
import me.infuzion.chess.game.piece.movement.type.DiagonalMovement;

public class Bishop extends ChessPiece {
    public Bishop(Color color, ChessPosition position) {
        super(color, position, PieceType.BISHOP);
    }

    @Override
    public boolean isMoveAllowedIgnoringCheck(BoardData data, ChessMove move, boolean excludeCastling) {
        return DiagonalMovement.allowed(data, this, move);
    }
}
