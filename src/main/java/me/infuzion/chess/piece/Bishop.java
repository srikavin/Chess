package me.infuzion.chess.piece;

import me.infuzion.chess.ChessBoard;
import me.infuzion.chess.ChessPiece;
import me.infuzion.chess.ChessPosition;

public class Bishop extends ChessPiece {

    public Bishop(Color color, ChessPosition position) {
        super(color, position);
    }

    public PieceType getType() {
        return PieceType.BISHOP;
    }

    public boolean allowed(ChessBoard board, ChessPosition start, ChessPosition end) {
        return false;
    }
}
