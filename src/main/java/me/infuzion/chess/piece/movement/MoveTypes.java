package me.infuzion.chess.piece.movement;

import me.infuzion.chess.piece.movement.type.HorizontalMovement;
import me.infuzion.chess.piece.movement.type.PawnMovement;

public enum MoveTypes {
    PAWN_MOVEMENT(new PawnMovement()),
    HORIZONTAL_MOVEMENT(new HorizontalMovement());

    private final MoveType type;

    public MoveType getType() {
        return type;
    }

    MoveTypes(MoveType type) {
        this.type = type;
    }
}
