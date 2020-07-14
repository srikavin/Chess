package me.infuzion.chess.board;

/**
 * rank 0 and file 0 is the position a1
 */
public class ChessPosition {

    private final static Character[] rankLetters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

    private final int rank;
    private final int file;

    public ChessPosition(int rank, int file) {
        if (rank >= 8 || rank < 0) {
            throw new IllegalArgumentException("Invalid rank value: " + rank);
        }
        if (file >= 8 || file < 0) {
            throw new IllegalArgumentException("Invalid file value: " + file);
        }
        this.rank = rank;
        this.file = file;
    }

    public ChessPosition(int row, char col) {
        this(rankCharToRank(col), row - 1);
    }

    public ChessPosition(String algebraicNotation) {
        this(Integer.parseInt(algebraicNotation.substring(1)), algebraicNotation.charAt(0));
    }

    public static int rankCharToRank(char file) {
        char colLowerCase = Character.toLowerCase(file);
        for (int i = 0; i < rankLetters.length; i++) {
            if (colLowerCase == rankLetters[i]) {
                return i;
            }
        }
        throw new IllegalArgumentException("Invalid Input: " + colLowerCase);
    }

    public int getRank() {
        return rank;
    }

    public char getFileChar() {
        return rankLetters[file];
    }

    public String getPosition() {
        return rankLetters[rank] + String.valueOf(file + 1);
    }

    public int getFile() {
        return file;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ChessPosition) {
            if (((ChessPosition) obj).rank == rank) {
                return ((ChessPosition) obj).file == file;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return getPosition();
    }
}
