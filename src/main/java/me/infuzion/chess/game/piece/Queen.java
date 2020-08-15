package me.infuzion.chess.game.piece;

import me.infuzion.chess.game.board.BoardData;
import me.infuzion.chess.game.board.ChessMove;
import me.infuzion.chess.game.board.ChessPosition;
import me.infuzion.chess.game.piece.movement.type.DiagonalMovement;
import me.infuzion.chess.game.piece.movement.type.RowMovement;

public class Queen extends ChessPiece {
    public Queen(Color color, ChessPosition position) {
        super(color, position, PieceType.QUEEN);
    }

    @Override
    public boolean isMoveAllowedIgnoringCheck(BoardData data, ChessMove move, boolean excludeCastling) {
        return RowMovement.allowed(data, this, move) || DiagonalMovement.allowed(data, this, move);
    }
}
