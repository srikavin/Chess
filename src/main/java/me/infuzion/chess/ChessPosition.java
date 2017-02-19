package me.infuzion.chess;

public class ChessPosition {

    private final int x;
    private final int y;

    public ChessPosition(int x, int y) {
        if (x > 8 || x < 1) {
            throw new RuntimeException("Invalid x value: " + x);
        }
        if (y > 8 || y < 1) {
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
}
