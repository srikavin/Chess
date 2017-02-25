package me.infuzion.chess.piece;

import me.infuzion.chess.ChessPiece;
import me.infuzion.chess.ChessPosition;
import me.infuzion.chess.piece.movement.MoveTypes;

public class Queen extends ChessPiece {

    public Queen(Color color, ChessPosition position) {
        super(color, position);
        setMovementTypes(MoveTypes.DIAGONAL_MOVEMENT, MoveTypes.HORIZONTAL_MOVEMENT);
        setType(PieceType.QUEEN);
    }

}
