package me.infuzion.chess;

import java.util.Arrays;

public class BoardData {

    private final ChessPiece[][] pieces;

    public BoardData(ChessPiece[][] pieces) {
        this.pieces = pieces;
    }

    public BoardData(BoardData data) {
        this.pieces = data.getPieces();
    }

    public ChessPiece[][] getPieces() {
        return Arrays.stream(pieces).map(ChessPiece[]::clone).toArray(ChessPiece[][]::new);
    }

    public void setPiece(int x, int y, ChessPiece piece) {
        pieces[y][x] = piece;
    }

    public void setPiece(ChessPosition pos, ChessPiece piece) {
        setPiece(pos.getRow(), pos.getCol(), piece);
    }

    public ChessPiece getPiece(ChessPosition pos) {
        return getPiece(pos.getRow(), pos.getCol());
    }

    public ChessPiece getPiece(int row, int col) {
        return pieces[col][row];
    }
}
