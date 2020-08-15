package me.infuzion.chess.game.piece;

import me.infuzion.chess.game.board.BoardData;
import me.infuzion.chess.game.board.ChessBoard;
import me.infuzion.chess.game.board.ChessMove;
import me.infuzion.chess.game.board.ChessPosition;
import me.infuzion.chess.game.piece.movement.type.RowMovement;

public class Rook extends ChessPiece {
    private final ChessPosition WHITE_KING_SIDE_CASTLE_ROOK = new ChessPosition("h1");
    private final ChessPosition WHITE_QUEEN_SIDE_CASTLE_ROOK = new ChessPosition("a1");
    private final ChessPosition BLACK_KING_SIDE_CASTLE_ROOK = new ChessPosition("h8");
    private final ChessPosition BLACK_QUEEN_SIDE_CASTLE_ROOK = new ChessPosition("a8");

    public Rook(Color color, ChessPosition position) {
        super(color, position, PieceType.ROOK);
    }

    @Override
    public boolean move(ChessBoard board, ChessMove move) {
        boolean ret = super.move(board, move);

        if (!ret) {
            return false;
        }

        if (currentPosition().equals(WHITE_KING_SIDE_CASTLE_ROOK)) {
            board.getData().getCastlingAvailability().remove(CastlingAvailability.WHITE_KING_SIDE);
        } else if (currentPosition().equals(WHITE_QUEEN_SIDE_CASTLE_ROOK)) {
            board.getData().getCastlingAvailability().remove(CastlingAvailability.WHITE_QUEEN_SIDE);
        } else if (currentPosition().equals(BLACK_KING_SIDE_CASTLE_ROOK)) {
            board.getData().getCastlingAvailability().remove(CastlingAvailability.BLACK_KING_SIDE);
        } else if (currentPosition().equals(BLACK_QUEEN_SIDE_CASTLE_ROOK)) {
            board.getData().getCastlingAvailability().remove(CastlingAvailability.BLACK_QUEEN_SIDE);
        }

        return true;
    }

    @Override
    public boolean isMoveAllowedIgnoringCheck(BoardData data, ChessMove move, boolean excludeCastling) {
        return RowMovement.allowed(data, this, move);
    }
}
