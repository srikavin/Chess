package me.infuzion.chess.piece;

import me.infuzion.chess.board.ChessPosition;
import me.infuzion.chess.piece.movement.MoveTypes;

public class Rook extends ChessPiece {

    public Rook(Color color, ChessPosition position) {
        super(color, position);
        setMovementTypes(MoveTypes.HORIZONTAL_MOVEMENT);
        setType(PieceType.ROOK);
    }
}
