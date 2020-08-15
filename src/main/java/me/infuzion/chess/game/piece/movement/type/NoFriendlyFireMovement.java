package me.infuzion.chess.game.piece.movement.type;

import me.infuzion.chess.game.board.BoardData;
import me.infuzion.chess.game.board.ChessPosition;
import me.infuzion.chess.game.piece.ChessPiece;

public class NoFriendlyFireMovement {
    public boolean allowed(BoardData board, ChessPiece piece, ChessPosition start,
                           ChessPosition end) {
        return board.getPiece(end) == null || !(board.getPiece(end).getColor() == piece.getColor());
    }
}
