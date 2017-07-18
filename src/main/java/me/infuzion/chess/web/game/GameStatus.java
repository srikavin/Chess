package me.infuzion.chess.web.game;

public enum GameStatus {
    IN_PROGRESS_WHITE(0),
    IN_PROGRESS_BLACK(1),
    ENDED_DRAW(2),
    ENDED_WHITE(3),
    ENDED_BLACK(4),
    WAITING(5);
    public static final GameStatus[] values = values();
    private final int value;

    GameStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public GameStatus valueOf(int value) {
        for (GameStatus e : values) {
            if (e.value == value) {
                return e;
            }
        }
        return null;
    }
}
