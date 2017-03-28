package me.infuzion.chess;

public class ChessPosition {

    private final static char[] columns = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

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

    public ChessPosition(int row, char col) {
        this(row, col - 97);
    }

    public static int colCharToInt(char col) {
        col = Character.toLowerCase(col);
        for (int i = 0; i < columns.length; i++) {
            if (col == columns[i]) {
                return i;
            }
        }
        throw new RuntimeException("Invalid Input: " + col);
    }

    public int getRow() {
        return row;
    }

    public char getColChar() {
        return columns[col];
    }

    public String getPosition() {
        return columns[col] + "" + (8 - row);
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
