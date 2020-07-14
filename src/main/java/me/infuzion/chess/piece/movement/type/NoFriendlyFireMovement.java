package me.infuzion.chess.piece.movement.type;

import me.infuzion.chess.board.BoardData;
import me.infuzion.chess.board.ChessPosition;
import me.infuzion.chess.piece.ChessPiece;

public class NoFriendlyFireMovement {
    public boolean allowed(BoardData board, ChessPiece piece, ChessPosition start,
                           ChessPosition end) {
        return board.getPiece(end) == null || !(board.getPiece(end).getColor() == piece.getColor());
    }
}
