package me.infuzion.chess.piece.movement;

import me.infuzion.chess.piece.movement.type.DiagonalMovement;
import me.infuzion.chess.piece.movement.type.KingMovement;
import me.infuzion.chess.piece.movement.type.KnightMovement;
import me.infuzion.chess.piece.movement.type.PawnMovement;
import me.infuzion.chess.piece.movement.type.RowMovement;

public enum MoveTypes {
    PAWN_MOVEMENT(new PawnMovement()),
    HORIZONTAL_MOVEMENT(new RowMovement()),
    DIAGONAL_MOVEMENT(new DiagonalMovement()),
    KNIGHT_MOVEMENT(new KnightMovement()),
    KING_MOVEMENT(new KingMovement());

    private final MoveType type;

    MoveTypes(MoveType type) {
        this.type = type;
    }

    public MoveType getType() {
        return type;
    }
}
