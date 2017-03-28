package me.infuzion.chess.piece;

public enum Color {
    BLACK,
    WHITE;

    public Color invert() {
        if (this == BLACK) {
            return WHITE;
        } else {
            return BLACK;
        }
    }
}
