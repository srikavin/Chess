package me.infuzion.chess.piece;

import me.infuzion.chess.board.BoardData;
import me.infuzion.chess.board.ChessMove;
import me.infuzion.chess.board.ChessPosition;
import me.infuzion.chess.piece.movement.type.DiagonalMovement;
import me.infuzion.chess.piece.movement.type.RowMovement;

public class Queen extends ChessPiece {
    public Queen(Color color, ChessPosition position) {
        super(color, position, PieceType.QUEEN);
    }

    @Override
    public boolean isMoveAllowedIgnoringCheck(BoardData data, ChessMove move, boolean excludeCastling) {
        return RowMovement.allowed(data, this, move) || DiagonalMovement.allowed(data, this, move);
    }
}
