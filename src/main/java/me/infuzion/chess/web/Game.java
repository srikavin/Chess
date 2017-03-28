package me.infuzion.chess.web;

import me.infuzion.chess.ChessBoard;
import me.infuzion.chess.ChessPiece;
import me.infuzion.chess.ChessPosition;
import me.infuzion.chess.piece.Color;
import me.infuzion.chess.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

enum GameStatus {
    IN_PROGRESS_WHITE,
    IN_PROGRESS_BLACK,
    ENDED_DRAW,
    ENDED_WHITE,
    ENDED_BLACK,
    WAITING,
}

public class Game {

    private static final Map<Identifier, Game> idGameMap = new HashMap<>();
    private static final Random random = new Random();

    private final Identifier gameID;
    private final ChessBoard board;
    private final Visibility visibility;
    private transient final UUID[] players;
    private UUID whiteSide;
    private UUID blackSide;
    private GameStatus status;

    public Game(Identifier id, String pgn, UUID whiteSide, UUID blackSide) {
        this.gameID = id;
        this.whiteSide = whiteSide;
        this.blackSide = blackSide;
        this.board = ChessBoard.fromPGNString(pgn);
        players = new UUID[2];
        visibility = Visibility.PUBLIC;
        idGameMap.put(gameID, this);
    }

    public Game(UUID player, Visibility visibility) {
        this.visibility = visibility;
        gameID = new Identifier();
        idGameMap.put(gameID, this);

        board = ChessBoard.getDefaultBoard();
        status = GameStatus.WAITING;

        players = new UUID[2];
        players[0] = player;
    }

    public Game(Identifier id, ChessBoard board, Visibility visibility) {
        this.gameID = id;
        this.board = board;
        this.visibility = visibility;
        this.players = new UUID[2];
        idGameMap.put(gameID, this);
    }

    public static Game fromID(Identifier id) {
        return idGameMap.get(id);
    }

    public ChessBoard getBoard() {
        return board;
    }

    public Identifier getGameID() {
        return gameID;
    }

    public UUID getBlackSide() {
        return blackSide;
    }

    public void addPlayer(UUID player) {
        if (this.players[1] == null && !player.equals(players[0])) {
            this.players[1] = player;
            randomize();
            status = GameStatus.IN_PROGRESS_WHITE;
        }
    }

    private void randomize() {
        if (random.nextBoolean()) {
            whiteSide = players[0];
            blackSide = players[1];
        } else {
            blackSide = players[0];
            whiteSide = players[1];
        }
    }

    public UUID getWhiteSide() {
        return whiteSide;
    }

    public boolean move(UUID uuid, ChessPosition start, ChessPosition end) {
        if (status == GameStatus.WAITING) {
            return false;
        }

        ChessPiece startPiece = board.getData().getPiece(start);
        if (startPiece == null) {
            return false;
        }
        if (!startPiece.allowed(board.getData(), end)) {
            return false;
        }
        if (startPiece.getColor() == Color.WHITE) {
            if (!uuid.equals(whiteSide)) {
                return false;
            } else if (status != GameStatus.IN_PROGRESS_WHITE) {
                return false;
            } else {
                status = GameStatus.IN_PROGRESS_BLACK;
            }
        } else {
            if (!uuid.equals(blackSide)) {
                return false;
            } else if (status != GameStatus.IN_PROGRESS_BLACK) {
                return false;
            } else {
                status = GameStatus.IN_PROGRESS_WHITE;
            }
        }
//        ChessMove move = new ChessMove(start, end, board.getPiece(start), board.getPiece(end));
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
