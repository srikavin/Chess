package me.infuzion.chess;

public class ChessPosition {

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        if (row > 8 || row < 0) {
            throw new RuntimeException("Invalid row value: " + row);
        }
        if (col > 8 || col < 0) {
            throw new RuntimeException("Invalid col value: " + col);
        }
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ChessPosition) {
            if (((ChessPosition) obj).getRow() == row) {
                if (((ChessPosition) obj).getCol() == col) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "[row] = " + row + ", [col] " + col;
    }
}
