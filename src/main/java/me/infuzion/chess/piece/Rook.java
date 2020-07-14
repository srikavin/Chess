package me.infuzion.chess.piece;

import me.infuzion.chess.board.BoardData;
import me.infuzion.chess.board.ChessMove;
import me.infuzion.chess.board.ChessPosition;
import me.infuzion.chess.piece.movement.type.RowMovement;

public class Rook extends ChessPiece {
    public Rook(Color color, ChessPosition position) {
        super(color, position, PieceType.ROOK);
    }

    @Override
    public boolean isMoveAllowedIgnoringCheck(BoardData data, ChessMove move, boolean excludeCastling) {
        return RowMovement.allowed(data, this, move);
    }
}
