package me.infuzion.chess.game.piece;

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
