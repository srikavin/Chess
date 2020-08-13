package me.infuzion.chess.piece;

import me.infuzion.chess.board.BoardData;
import me.infuzion.chess.board.ChessBoard;
import me.infuzion.chess.board.ChessMove;
import me.infuzion.chess.board.ChessPosition;

public class King extends ChessPiece {
    public King(Color color, ChessPosition position) {
        super(color, position, PieceType.KING);
    }

    private final ChessPosition WHITE_KING_SIDE_CASTLE_TARGET = new ChessPosition("g1");
    private final ChessPosition WHITE_KING_SIDE_CASTLE_SQUARE1 = new ChessPosition("f1");
    private final ChessPosition WHITE_KING_SIDE_CASTLE_SQUARE2 = new ChessPosition("g1");
    private final ChessPosition WHITE_KING_SIDE_CASTLE_ROOK_START = new ChessPosition("h1");
    private final ChessPosition WHITE_KING_SIDE_CASTLE_ROOK_END = new ChessPosition("f1");

    private final ChessPosition WHITE_QUEEN_SIDE_CASTLE_TARGET = new ChessPosition("c1");
    private final ChessPosition WHITE_QUEEN_SIDE_CASTLE_SQUARE1 = new ChessPosition("d1");
    private final ChessPosition WHITE_QUEEN_SIDE_CASTLE_SQUARE2 = new ChessPosition("c1");
    private final ChessPosition WHITE_QUEEN_SIDE_CASTLE_SQUARE3 = new ChessPosition("b1");
    private final ChessPosition WHITE_QUEEN_SIDE_CASTLE_ROOK_START = new ChessPosition("a1");
    private final ChessPosition WHITE_QUEEN_SIDE_CASTLE_ROOK_END = new ChessPosition("d1");

    private final ChessPosition BLACK_KING_SIDE_CASTLE_TARGET = new ChessPosition("g8");
    private final ChessPosition BLACK_KING_SIDE_CASTLE_SQUARE1 = new ChessPosition("f8");
    private final ChessPosition BLACK_KING_SIDE_CASTLE_SQUARE2 = new ChessPosition("g8");
    private final ChessPosition BLACK_KING_SIDE_CASTLE_ROOK_START = new ChessPosition("h8");
    private final ChessPosition BLACK_KING_SIDE_CASTLE_ROOK_END = new ChessPosition("f8");

    private final ChessPosition BLACK_QUEEN_SIDE_CASTLE_TARGET = new ChessPosition("c8");
    private final ChessPosition BLACK_QUEEN_SIDE_CASTLE_SQUARE1 = new ChessPosition("d8");
    private final ChessPosition BLACK_QUEEN_SIDE_CASTLE_SQUARE2 = new ChessPosition("c8");
    private final ChessPosition BLACK_QUEEN_SIDE_CASTLE_SQUARE3 = new ChessPosition("b8");
    private final ChessPosition BLACK_QUEEN_SIDE_CASTLE_ROOK_START = new ChessPosition("a8");
    private final ChessPosition BLACK_QUEEN_SIDE_CASTLE_ROOK_END = new ChessPosition("d8");

    @Override
    public void executeMove(ChessBoard board, ChessMove move) {
        BoardData data = board.getData();

        if (isValidWhiteKingSideCastle(data, move)) {
            board.getData().movePieceWithoutVerification(WHITE_KING_SIDE_CASTLE_ROOK_START, WHITE_KING_SIDE_CASTLE_ROOK_END);
        } else if (isValidWhiteQueenSideCastle(data, move)) {
            board.getData().movePieceWithoutVerification(WHITE_QUEEN_SIDE_CASTLE_ROOK_START, WHITE_QUEEN_SIDE_CASTLE_ROOK_END);
        } else if (isValidBlackKingSideCastle(data, move)) {
            board.getData().movePieceWithoutVerification(BLACK_KING_SIDE_CASTLE_ROOK_START, BLACK_KING_SIDE_CASTLE_ROOK_END);
        } else if (isValidBlackQueenSideCastle(data, move)) {
            board.getData().movePieceWithoutVerification(BLACK_QUEEN_SIDE_CASTLE_ROOK_START, BLACK_QUEEN_SIDE_CASTLE_ROOK_END);
        }

        // moving the king (even to castle) removes all castling possibilities
        if (this.getColor() == Color.WHITE &&
                (data.getCastlingAvailability().contains(CastlingAvailability.WHITE_KING_SIDE) ||
                        data.getCastlingAvailability().contains(CastlingAvailability.WHITE_QUEEN_SIDE))) {
            data.getCastlingAvailability().remove(CastlingAvailability.WHITE_QUEEN_SIDE);
            data.getCastlingAvailability().remove(CastlingAvailability.WHITE_KING_SIDE);
        }
        if (this.getColor() == Color.BLACK &&
                (data.getCastlingAvailability().contains(CastlingAvailability.BLACK_KING_SIDE) ||
                        data.getCastlingAvailability().contains(CastlingAvailability.BLACK_QUEEN_SIDE))) {
            data.getCastlingAvailability().remove(CastlingAvailability.BLACK_QUEEN_SIDE);
            data.getCastlingAvailability().remove(CastlingAvailability.BLACK_KING_SIDE);
        }

        super.executeMove(board, move);
    }

    private boolean isEmptyAndSafe(ChessPosition position, BoardData data, Color color) {
        return data.getPiece(position) == null && !ChessBoard.canPieceAttack(position, data, color.invert());
    }

    private boolean isValidWhiteKingSideCastle(BoardData data, ChessMove move) {
        if (move.getEnd().equals(WHITE_KING_SIDE_CASTLE_TARGET) && data.getCastlingAvailability().contains(CastlingAvailability.WHITE_KING_SIDE)) {
            // castling is allowed unless the king travels through a check
            return isEmptyAndSafe(WHITE_KING_SIDE_CASTLE_SQUARE1, data, Color.WHITE)
                    && isEmptyAndSafe(WHITE_KING_SIDE_CASTLE_SQUARE2, data, Color.WHITE);
        }

        return false;
    }

    private boolean isValidWhiteQueenSideCastle(BoardData data, ChessMove move) {
        if (move.getEnd().equals(WHITE_QUEEN_SIDE_CASTLE_TARGET) && data.getCastlingAvailability().contains(CastlingAvailability.WHITE_QUEEN_SIDE)) {
            // castling is allowed unless the king travels through a check
            return isEmptyAndSafe(WHITE_QUEEN_SIDE_CASTLE_SQUARE1, data, Color.WHITE)
                    && isEmptyAndSafe(WHITE_QUEEN_SIDE_CASTLE_SQUARE2, data, Color.WHITE)
                    && data.getPiece(WHITE_QUEEN_SIDE_CASTLE_SQUARE3) == null;
        }

        return false;
    }

    private boolean isValidBlackKingSideCastle(BoardData data, ChessMove move) {
        if (move.getEnd().equals(BLACK_KING_SIDE_CASTLE_TARGET) && data.getCastlingAvailability().contains(CastlingAvailability.BLACK_KING_SIDE)) {
            // castling is allowed unless the king travels through a check
            return isEmptyAndSafe(BLACK_KING_SIDE_CASTLE_SQUARE1, data, Color.BLACK)
                    && isEmptyAndSafe(BLACK_KING_SIDE_CASTLE_SQUARE2, data, Color.BLACK);
        }

        return false;
    }

    private boolean isValidBlackQueenSideCastle(BoardData data, ChessMove move) {
        if (move.getEnd().equals(BLACK_QUEEN_SIDE_CASTLE_TARGET) && data.getCastlingAvailability().contains(CastlingAvailability.BLACK_QUEEN_SIDE)) {
            // castling is allowed unless the king travels through a check
            return isEmptyAndSafe(BLACK_QUEEN_SIDE_CASTLE_SQUARE1, data, Color.BLACK)
                    && isEmptyAndSafe(BLACK_QUEEN_SIDE_CASTLE_SQUARE2, data, Color.BLACK)
                    && data.getPiece(BLACK_QUEEN_SIDE_CASTLE_SQUARE3) == null;
        }

        return false;
    }

    private boolean isValidCastleMove(BoardData data, ChessMove move) {
        return isValidWhiteKingSideCastle(data, move) || isValidWhiteQueenSideCastle(data, move) ||
                isValidBlackKingSideCastle(data, move) || isValidBlackQueenSideCastle(data, move);
    }

    @Override
    public boolean isMoveAllowedIgnoringCheck(BoardData data, ChessMove move, boolean excludeCastling) {
        ChessPosition start = move.getSource();
        ChessPosition end = move.getEnd();

        int startX = start.getRank();
        int startY = start.getFile();
        int endX = end.getRank();
        int endY = end.getFile();

        int differenceX = Math.abs(startX - endX);
        int differenceY = Math.abs(startY - endY);

        if ((differenceX == 1 && differenceY == 1)
                || (differenceX == 0 && differenceY == 1)
                || (differenceX == 1 && differenceY == 0)) {
            return true;
        }

        return !excludeCastling && isValidCastleMove(data, move);
    }

}
