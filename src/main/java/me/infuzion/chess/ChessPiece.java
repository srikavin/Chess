package me.infuzion.chess;

import me.infuzion.chess.piece.Color;
import me.infuzion.chess.piece.PieceType;
import me.infuzion.chess.piece.movement.MoveType;
import me.infuzion.chess.piece.movement.MoveTypes;

public abstract class ChessPiece {

    private final Color color;
    private final ChessPosition position;
    private MoveType[] moveTypes;

    public ChessPiece(Color color, ChessPosition position) {
        this.color = color;
        this.position = position;
    }

    public abstract PieceType getType();

    public boolean allowed(ChessBoard board, ChessPosition end) {
        for (MoveType e : moveTypes) {
            if (e.allowed(board, this, position, end)) {
                return true;
            }
        }
        return false;
    }

    protected void setMovementTypes(MoveTypes... types){
        MoveType[] converted = new MoveType[types.length];

        for(int i = 0; i < types.length; i++){
            converted[i] = types[i].getType();
        }
        setMovementTypes(converted);
    }

    protected void setMovementTypes(MoveType... types) {
        this.moveTypes = types;
    }

    public Color getColor() {
        return color;
    }

    public ChessPosition currentPosition() {
        return position;
    }
}
