package me.infuzion.chess.piece.movement;

import me.infuzion.chess.ChessPiece;
import me.infuzion.chess.ChessPosition;

public class ChessMove {

    private final ChessPosition start;
    private final ChessPosition end;
    private final ChessPiece startPiece;
    private final ChessPiece endPiece;

    public ChessMove(ChessPosition start, ChessPosition end,
                     ChessPiece startPiece, ChessPiece endPiece) {
        this.start = start;
        this.end = end;
        this.startPiece = startPiece;
        this.endPiece = endPiece;
    }

    public ChessPosition getStart() {
        return start;
    }

    public ChessPosition getEnd() {
        return end;
    }

    public ChessPiece getStartPiece() {
        return startPiece;
    }

    public ChessPiece getEndPiece() {
        return endPiece;
    }
}
