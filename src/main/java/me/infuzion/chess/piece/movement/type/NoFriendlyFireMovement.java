package me.infuzion.chess.piece.movement.type;

import me.infuzion.chess.ChessBoard;
import me.infuzion.chess.ChessPiece;
import me.infuzion.chess.ChessPosition;
import me.infuzion.chess.piece.movement.MoveType;

public class NoFriendlyFireMovement implements MoveType {

    @Override
    public boolean allowed(ChessBoard board, ChessPiece piece, ChessPosition start,
        ChessPosition end) {
        return board.getPiece(end) == null || !(board.getPiece(end).getColor() == piece.getColor());
    }
}
