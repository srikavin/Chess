package me.infuzion.chess.web.game;

import me.infuzion.chess.board.ChessBoard;
import me.infuzion.chess.board.ChessPosition;
import me.infuzion.chess.piece.ChessPiece;
import me.infuzion.chess.piece.Color;
import me.infuzion.chess.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Game {

    private static final Map<Identifier, Game> idGameMap = new HashMap<>();
    private static final Random random = new Random();
    public final UUID uuid = UUID.randomUUID();

    private final Identifier gameID;
    private final ChessBoard board;
    private final Visibility visibility;
    private Identifier whiteSide;
    private Identifier blackSide;
    private GameStatus status = GameStatus.WAITING;

    public Game(Identifier id, String pgn, Identifier whiteSide, Identifier blackSide, GameStatus status) {
        this.gameID = id;
        this.whiteSide = whiteSide;
        this.blackSide = blackSide;
        this.board = ChessBoard.fromPGNString(pgn);
        this.status = status;
        visibility = Visibility.PUBLIC;
        idGameMap.put(gameID, this);
    }

    public Game(Identifier player, Visibility visibility) {
        this.visibility = visibility;
        gameID = new Identifier();
        idGameMap.put(gameID, this);

        board = ChessBoard.getDefaultBoard();
        status = GameStatus.WAITING;
    }

    public Game(Identifier id, ChessBoard board, Visibility visibility) {
        this.gameID = id;
        this.board = board;
        this.visibility = visibility;
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

    public Identifier getBlackSide() {
        return blackSide;
    }

    public boolean addPlayer(Identifier player) {
        if (player.equals(whiteSide) || player.equals(blackSide)) {
            return false;
        }
        if (random.nextBoolean()) {
            if (whiteSide == null) {
                whiteSide = player;
            } else if (blackSide == null) {
                blackSide = player;
            } else {
                return false;
            }
        } else {
            if (blackSide == null) {
                blackSide = player;
            } else if (whiteSide == null) {
                whiteSide = player;
            } else {
                return false;
            }
        }

        if (whiteSide != null && blackSide != null) {
            status = GameStatus.IN_PROGRESS_WHITE;
        }
        return true;
    }

    public Identifier getWhiteSide() {
        return whiteSide;
    }

    public boolean move(Identifier userID, ChessPosition start, ChessPosition end) {
//        if (status == GameStatus.WAITING) {
//            System.out.println("Not in waiting state!");
//            return false;
//        }

        ChessPiece startPiece = board.getData().getPiece(start);
        if (startPiece == null) {
            System.out.println("Start piece is null!");
            return false;
        }
        if (!startPiece.allowed(board.getData(), end)) {
            System.out.println("Move is not allowed");
            return false;
        }
        if (startPiece.getColor() == Color.WHITE) {
            if (!userID.equals(whiteSide)) {
                System.out.println(3);
                return false;
            } else if (status != GameStatus.IN_PROGRESS_WHITE) {
                System.out.println(4);
                return false;
            } else {
                status = GameStatus.IN_PROGRESS_BLACK;
            }
        } else {
            if (!userID.equals(blackSide)) {
                System.out.println(5);
                return false;
            } else if (status != GameStatus.IN_PROGRESS_BLACK) {
                System.out.println(6);
                return false;
            } else {
                status = GameStatus.IN_PROGRESS_WHITE;
            }
        }
//        ChessMove move = new ChessMove(start, end, board.getPiece(start), board.getPiece(end));
        System.out.println(7);
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
