package me.infuzion.chess;

import me.infuzion.chess.piece.Color;
import me.infuzion.chess.piece.PieceType;
import me.infuzion.chess.piece.movement.MoveType;
import me.infuzion.chess.piece.movement.MoveTypes;
import me.infuzion.chess.piece.movement.type.CheckMovement;

public abstract class ChessPiece implements Cloneable {

    private final Color color;
    private ChessPosition position;
    private transient MoveType[] moveTypes;
    private transient MoveType[] requiredTypes;
    private PieceType type;

    public ChessPiece(Color color, ChessPosition position) {
        this.color = color;
        this.position = position;
        setRequiredTypes(MoveTypes.NO_FRIENDLY_CAPTURES, MoveTypes.CHECK_MOVEMENT);
    }

    public final PieceType getType() {
        return type;
    }

    protected void setType(PieceType type) {
        this.type = type;
    }

    public boolean allowed(BoardData board, ChessPosition end, boolean ignoreCheck) {
        for (MoveType e : requiredTypes) {
            if (ignoreCheck && e instanceof CheckMovement) {
                continue;
            }
            if (!e.allowed(board, this, position, end)) {
                System.out.println(e.getClass() + " Returned false!");
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

    public boolean allowed(BoardData board, ChessPosition end) {
        return allowed(board, end, false);
    }

    public boolean allowed(ChessBoard board, ChessPosition end) {
        return allowed(board.getData(), end);
    }

    public boolean move(ChessBoard board, ChessPosition end) {
        if (allowed(board.getData(), end)) {
            board.move(this, position, end);
            board.getData().setPiece(position, null);
            board.getData().setPiece(end, this);
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

    public void setPosition(ChessPosition pos) {
        this.position = pos;
    }

    public ChessPiece clone() {
        try {
            return (ChessPiece) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
