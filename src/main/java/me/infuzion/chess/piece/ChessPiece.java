package me.infuzion.chess.piece;

import me.infuzion.chess.board.BoardData;
import me.infuzion.chess.board.ChessBoard;
import me.infuzion.chess.board.ChessMove;
import me.infuzion.chess.board.ChessPosition;

public abstract class ChessPiece implements Cloneable {

    private final Color color;
    private final PieceType type;
    private ChessPosition position;

    public ChessPiece(Color color, ChessPosition position, PieceType type) {
        this.color = color;
        this.position = position;
        this.type = type;
    }

    public final PieceType getType() {
        return type;
    }

    public abstract boolean isMoveAllowedIgnoringCheck(BoardData data, ChessMove move, boolean excludeCastling);

    /**
     * @param data Board data to use to calculate if the movement is possible
     * @param move The move to check
     * @return Returns true if the movement is allowed; False otherwise.
     */
    public boolean allowed(BoardData data, ChessMove move, boolean ignoreCheck) {
        if (!move.getSource().equals(this.position)) {
            throw new RuntimeException("move does not apply to this piece!");
        }

        if (move.getSource().equals(move.getEnd()) ||
                (data.getPiece(move.getEnd()) != null && data.getPiece(move.getEnd()).color == color)) {
            return false;
        }

        if (!isMoveAllowedIgnoringCheck(data, move, false)) {
            return false;
        }

        return ignoreCheck || !ChessBoard.isUnderCheckAfterMove(data, color, move);
    }

    public boolean allowed(BoardData data, ChessPosition end) {
        return allowed(data, new ChessMove(this.currentPosition(), end), false);
    }

    public boolean move(ChessBoard board, ChessMove move) {
        return move(board, move, false);
    }

    public void executeMoveWithoutValidation(ChessBoard board, ChessMove move) {
        board.recordMove(move);
        executeMove(board, move);
        this.position = move.getEnd();
    }

    protected void executeMove(ChessBoard board, ChessMove move) {
        board.getData().setPiece(position, null);
        board.getData().setPiece(move.getEnd(), this);
        board.getData().setEnPassantSquare(null);
    }

    public boolean move(ChessBoard board, ChessMove move, boolean ignoreCheck) {
        if (allowed(board.getData(), move, ignoreCheck)) {
            executeMoveWithoutValidation(board, move);
            return true;
        }

        return false;
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
