package me.infuzion.chess;

import me.infuzion.chess.piece.Color;
import me.infuzion.chess.piece.PieceType;
import me.infuzion.chess.piece.movement.MoveType;
import me.infuzion.chess.piece.movement.MoveTypes;

public abstract class ChessPiece {

    private final Color color;
    private ChessPosition position;
    private transient MoveType[] moveTypes;
    private transient MoveType[] requiredTypes;
    private PieceType type;

    public ChessPiece(Color color, ChessPosition position) {
        this.color = color;
        this.position = position;
        setRequiredTypes(MoveTypes.NO_FRIENDLY_CAPTURES);
    }

    public final PieceType getType() {
        return type;
    }

    protected void setType(PieceType type) {
        this.type = type;
    }

    public boolean allowed(ChessBoard board, ChessPosition end) {
        for (MoveType e : requiredTypes) {
            if (!e.allowed(board, this, position, end)) {
                return false;
            }
        }
        for (MoveType e : moveTypes) {
            if (e.allowed(board, this, position, end)) {
                return true;
            }
        }
        return false;
    }

    public boolean move(ChessBoard board, ChessPosition end) {
        if (allowed(board, end)) {
            board.setPiece(position, null);
            board.setPiece(end, this);
            this.position = end;
            return true;
        }
        return false;
    }

    protected void setRequiredTypes(MoveTypes... types) {
        MoveType[] converted = new MoveType[types.length];

        for (int i = 0; i < types.length; i++) {
            converted[i] = types[i].getType();
        }
        setRequiredTypes(converted);
    }

    protected void setRequiredTypes(MoveType... types) {
        this.requiredTypes = types;
    }


    protected void setMovementTypes(MoveTypes... types) {
        MoveType[] converted = new MoveType[types.length];

        for (int i = 0; i < types.length; i++) {
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
