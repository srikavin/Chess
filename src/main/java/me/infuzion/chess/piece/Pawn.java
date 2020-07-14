package me.infuzion.chess.piece;

import me.infuzion.chess.board.BoardData;
import me.infuzion.chess.board.ChessBoard;
import me.infuzion.chess.board.ChessMove;
import me.infuzion.chess.board.ChessPosition;

public class Pawn extends ChessPiece {
    public Pawn(Color color, ChessPosition position) {
        super(color, position, PieceType.PAWN);
    }

    @Override
    protected void executeMove(ChessBoard board, ChessMove move) {
        super.executeMove(board, move);

        ChessPosition position = getEnPassantSquare(board, move.getSource(), move.getEnd());

        if (position != null) {
            board.getData().setEnPassantSquare(position);
        }
    }

    private ChessPosition getEnPassantSquare(ChessBoard board, ChessPosition start, ChessPosition end) {
        int forward;

        if (this.getColor() == Color.WHITE) {
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

            boolean isFirstMove = (getColor() == Color.WHITE ? startFile == 1 : startFile == 6);

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

        if (this.getColor() == Color.WHITE) {
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

            boolean isFirstMove = (getColor() == Color.WHITE ? startFile == 1 : startFile == 6);

            // 2 squares if isFirstMove
            if (isFirstMove && startFile + 2 * forward == endFile) {
                data.setEnPassantSquare(new ChessPosition(endRank, endFile - forward));
                return true;
            }

            return false;
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
