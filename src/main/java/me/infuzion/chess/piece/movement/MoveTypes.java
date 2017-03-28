package me.infuzion.chess.piece.movement;

import me.infuzion.chess.piece.movement.type.*;

public enum MoveTypes {
    PAWN_MOVEMENT(new PawnMovement()),
    HORIZONTAL_MOVEMENT(new RowMovement()),
    DIAGONAL_MOVEMENT(new DiagonalMovement()),
    KNIGHT_MOVEMENT(new KnightMovement()),
    KING_MOVEMENT(new KingMovement()),
    NO_FRIENDLY_CAPTURES(new NoFriendlyFireMovement()),
    CHECK_MOVEMENT(new CheckMovement());

    private final MoveType type;

    MoveTypes(MoveType type) {
        this.type = type;
    }

    public MoveType getType() {
        return type;
    }
}
