package me.infuzion.chess;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.infuzion.chess.piece.Color;
import me.infuzion.chess.util.GameIdentifier;

enum GameStatus {
    IN_PROGRESS_WHITE,
    IN_PROGRESS_BLACK,
    ENDED_DRAW,
    ENDED_WHITE,
    ENDED_BLACK,
    WAITING,
}

enum Visibility {
    PUBLIC,
    UNLISTED
}

public class Game {

    public static final Map<GameIdentifier, Game> idGameMap = new HashMap<>();

    private final GameIdentifier gameID;
    private final UUID whiteSide;
    private final ChessBoard board;
    private final Visibility visibility;
    private UUID blackSide;
    private GameStatus status;

    public Game(UUID whiteSide, Visibility visibility) {
        this.whiteSide = whiteSide;
        this.visibility = visibility;
        board = ChessBoard.getDefaultBoard();
        gameID = new GameIdentifier();
        System.out.println("created with " + gameID);
        status = GameStatus.WAITING;
        idGameMap.put(gameID, this);
    }

    public static Game fromID(GameIdentifier id) {
        return idGameMap.get(id);
    }

    public GameIdentifier getGameID() {
        return gameID;
    }

    public UUID getBlackSide() {
        return blackSide;
    }

    public void setBlackSide(UUID blackSide) {
        if (this.blackSide == null) {
            this.blackSide = blackSide;
            status = GameStatus.IN_PROGRESS_WHITE;
        }
    }

    public UUID getWhiteSide() {
        return whiteSide;
    }

    public boolean move(UUID uuid, ChessPosition start, ChessPosition end) {
        if (status == GameStatus.WAITING) {
            return false;
        }

        ChessPiece startPiece = board.getPieces()[start.getRow()][start.getCol()];
        if (startPiece == null) {
            System.out.println("null");
            return false;
        }
        if (!startPiece.allowed(board, end)) {
            return false;
        }
        if (startPiece.getColor() == Color.WHITE) {
            if (!uuid.equals(whiteSide)) {
                System.out.println("invalid uuid white");
                return false;
            } else if (status != GameStatus.IN_PROGRESS_WHITE) {
                System.out.println("not whites turn");
                return false;
            } else {
                status = GameStatus.IN_PROGRESS_BLACK;
            }
        } else {
            if (!uuid.equals(blackSide)) {
                System.out.println("invalid uuid black");
                return false;
            } else if (status != GameStatus.IN_PROGRESS_BLACK) {
                System.out.println("not blacks turn");
                return false;
            } else {
                status = GameStatus.IN_PROGRESS_WHITE;
            }
        }
        return startPiece.move(board, end);
    }

    @Override
    public boolean equals(Object o) {
        return this == o ||
            o instanceof Game &&
                this.gameID == ((Game) o).gameID;
    }

    public GameStatus getStatus() {
        return status;
    }

    public Visibility getVisibility() {
        return visibility;
    }
}
