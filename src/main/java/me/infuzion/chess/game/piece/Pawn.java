package me.infuzion.chess.game.piece;

import me.infuzion.chess.game.board.BoardData;
import me.infuzion.chess.game.board.ChessBoard;
import me.infuzion.chess.game.board.ChessMove;
import me.infuzion.chess.game.board.ChessPosition;

import static me.infuzion.chess.game.piece.Color.WHITE;

public class Pawn extends ChessPiece {
    public Pawn(Color color, ChessPosition position) {
        super(color, position, PieceType.PAWN);
    }

    @Override
    protected void executeMove(ChessBoard board, ChessMove move) {
        ChessPosition enPassantSquare = board.getData().getEnPassantSquare();
        if (move.getEnd().equals(enPassantSquare)) {
            if (getColor() == WHITE) {
                board.getData().setPiece(enPassantSquare.getRank(), enPassantSquare.getFile() - 1, null);
            } else {
                board.getData().setPiece(enPassantSquare.getRank(), enPassantSquare.getFile() + 1, null);
            }
            board.getData().setEnPassantSquare(null);
        } else {
            ChessPosition position = getEnPassantSquare(board, move.getSource(), move.getEnd());

            if (position != null) {
                board.getData().setEnPassantSquare(position);
            }
        }

        board.getData().setPiece(this.currentPosition(), null);
        board.getData().setPiece(move.getEnd(), this);
    }

    private ChessPosition getEnPassantSquare(ChessBoard board, ChessPosition start, ChessPosition end) {
        int forward;

        if (this.getColor() == WHITE) {
            forward = 1;
        } else {
            forward = -1;
        }

        int startRank = start.getRank();
        int startFile = start.getFile();
        int endRank = end.getRank();
        int endFile = end.getFile();


        ChessPiece endPiece = board.getData().getPiece(end);

        if (startRank == endRank) {
            if (endPiece != null) {
                return null;
            }

            if (startFile + forward == endFile) {
                return null;
            }

            boolean isFirstMove = (getColor() == WHITE ? startFile == 1 : startFile == 6);

            // 2 squares if isFirstMove
            if (isFirstMove && startFile + 2 * forward == endFile) {
                return new ChessPosition(endRank, endFile - forward);
            }

            return null;
        }

        return null;
    }

    @Override
    public boolean isMoveAllowedIgnoringCheck(BoardData data, ChessMove move, boolean excludeCastling) {
        ChessPosition start = move.getSource();
        ChessPosition end = move.getEnd();

        int startRank = start.getRank();
        int startFile = start.getFile();
        int endRank = end.getRank();
        int endFile = end.getFile();

        ChessPiece endPiece = data.getPiece(end);

        int forward;

        if (this.getColor() == WHITE) {
            forward = 1;
        } else {
            forward = -1;
        }

        // move forward 1 or 2 squares
        if (startRank == endRank) {
            if (endPiece != null) {
                return false;
            }

            if (startFile + forward == endFile) {
                return true;
            }

            boolean isFirstMove = (getColor() == WHITE ? startFile == 1 : startFile == 6);

            // 2 squares if isFirstMove
            return isFirstMove && startFile + 2 * forward == endFile;
        }

        // handle captures
        if (Math.abs(startRank - endRank) == 1 && startFile + forward == endFile && endPiece != null) {
            return true;
        }

        // handle en passant
        if (data.getEnPassantSquare() != null) {
            ChessPosition target = data.getEnPassantSquare();
            if (!target.equals(end)) {
                return false;
            }

            if (Math.abs(startRank - endRank) != 1) {
                return false;
            }

            return startFile + forward == endFile;
        }

        return false;
    }
}
