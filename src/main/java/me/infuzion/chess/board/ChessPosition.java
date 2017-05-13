package me.infuzion.chess.board;

public class ChessPosition {

    private final static Character[] columns = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        if (row >= 8 || row < 0) {
            throw new IllegalArgumentException("Invalid row value: " + row);
        }
        if (col >= 8 || col < 0) {
            throw new IllegalArgumentException("Invalid col value: " + col);
        }
        this.row = row;
        this.col = col;
    }

    public ChessPosition(int row, char col) {
        this(col + String.valueOf(row));
    }

    public ChessPosition(String algebraicNotation) {
        this(8 - Integer.parseInt(algebraicNotation.substring(1)), colCharToInt(algebraicNotation.charAt(0)));
    }

    public static int colCharToInt(char col) {
        char colLowerCase = Character.toLowerCase(col);
        for (int i = 0; i < columns.length; i++) {
            if (colLowerCase == columns[i]) {
                return i;
            }
        }
        throw new IllegalArgumentException("Invalid Input: " + colLowerCase);
    }

    public int getRow() {
        return row;
    }

    public char getColChar() {
        return columns[col];
    }

    public String getPosition() {
        return columns[col] + String.valueOf((8 - row));
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
            if (((ChessPosition) obj).row == row) {
                if (((ChessPosition) obj).col == col) {
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
