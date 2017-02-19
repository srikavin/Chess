package me.infuzion.chess.piece;

import javax.swing.text.Position;
import me.infuzion.chess.ChessBoard;
import me.infuzion.chess.ChessPiece;
import me.infuzion.chess.ChessPosition;

public class Queen extends ChessPiece {

    public Queen(Color color, ChessPosition position) {
        super(color, position);
    }

    public PieceType getType() {
        return PieceType.QUEEN;
    }

    public boolean allowed(ChessBoard board, ChessPosition start, ChessPosition end) {
        return false;
    }
}
