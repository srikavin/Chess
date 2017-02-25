package me.infuzion.chess;

public class ChessPosition {

    private final int x;
    private final int y;

    public ChessPosition(int x, int y) {
        if (x > 8 || x < 0) {
            throw new RuntimeException("Invalid x value: " + x);
        }
        if (y > 8 || y < 0) {
            throw new RuntimeException("Invalid y value: " + y);
        }
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ChessPosition) {
            if (((ChessPosition) obj).getX() == x) {
                if (((ChessPosition) obj).getY() == y) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "[x] = " + x + ", [y] " + y;
    }
}
