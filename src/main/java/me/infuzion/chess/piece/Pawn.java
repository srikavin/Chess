package me.infuzion.chess.piece;

import me.infuzion.chess.ChessBoard;
import me.infuzion.chess.ChessPiece;
import me.infuzion.chess.ChessPosition;
import me.infuzion.chess.piece.movement.MoveTypes;

public class Pawn extends ChessPiece {

    private boolean firstMove = true;

    public Pawn(Color color, ChessPosition position) {
        super(color, position);
        setMovementTypes(MoveTypes.PAWN_MOVEMENT);
        setType(PieceType.PAWN);
    }

    @Override
    public boolean move(ChessBoard board, ChessPosition end) {
        boolean result = super.move(board, end);
        firstMove = false;
        return result;
    }

    public boolean isFirstMove() {
        return firstMove;
    }
}
