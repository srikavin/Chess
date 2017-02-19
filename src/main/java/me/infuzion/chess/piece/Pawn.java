package me.infuzion.chess.piece;

import me.infuzion.chess.ChessPiece;
import me.infuzion.chess.ChessPosition;
import me.infuzion.chess.piece.movement.MoveTypes;

public class Pawn extends ChessPiece {

    public Pawn(Color color, ChessPosition position) {
        super(color, position);
        setMovementTypes(MoveTypes.PAWN_MOVEMENT);
    }

    public PieceType getType() {
        return PieceType.PAWN;
    }
}
