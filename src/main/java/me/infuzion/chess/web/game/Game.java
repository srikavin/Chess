package me.infuzion.chess.web.game;

import com.google.gson.JsonObject;
import me.infuzion.chess.board.BoardData;
import me.infuzion.chess.board.ChessBoard;
import me.infuzion.chess.board.ChessPosition;
import me.infuzion.chess.piece.ChessPiece;
import me.infuzion.chess.piece.Color;
import me.infuzion.chess.piece.PieceType;
import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.record.Record;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Game implements Record {

    private static final Map<Identifier, Game> idGameMap = new HashMap<>();
    private static final Random random = new Random();
    private static final Map<String, BufferedImage> pieceToImageMap = new HashMap<>();
    private static final BufferedImage defaultBoard = new BufferedImage(640, 640, BufferedImage.TYPE_INT_ARGB);
    private static final int tileSize = 80;
    private static final Map<Identifier, byte[]> generatedThumbnails = new HashMap<>();
    private static final Map<Identifier, Boolean> thumbnailsDirty = new HashMap<>();

    static {
        try {
            String color = "w";
            for (int i = 0; i < 2; i++) {
                for (PieceType e : PieceType.values()) {
                    String name = color + e.getAbbreviation();
                    BufferedImage image = ImageIO.read(Game.class.getClassLoader().getResourceAsStream("images/pieces/" + name + ".png"));
                    pieceToImageMap.put(name, image);
                }
                color = "b";
            }

            ChessBoard board = ChessBoard.getDefaultBoard();
            final java.awt.Color white = new java.awt.Color(240, 217, 181);
            final java.awt.Color black = new java.awt.Color(181, 136, 99);
            Graphics graphics = defaultBoard.getGraphics();


            Color[][] boardColor = board.getBoardColors();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    graphics.setColor(boardColor[i][j] == Color.WHITE ? white : black);
                    graphics.fillRect(i * tileSize, j * tileSize, (i + 1) * tileSize, (j + 1) * tileSize);
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            System.exit(-1);
        }

    }

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

    public static void main(String[] args) throws IOException {
        Game game = new Game(new Identifier(),
                "1. d3 e5 2. e3 Nc6 3. f3 Bc5 4. g3 Nge7 5. h3 d6 6. f4 Be6 7. fxe5 Bd5 8. exd6 Qxd6", new Identifier(),
                new Identifier(), GameStatus.IN_PROGRESS_WHITE);
        byte[] image = game.generateThumbnail();
        File file = new File("img.png");
        file.createNewFile();
        IOUtils.write(image, new FileOutputStream(file));
    }

    public void onChange() {
        thumbnailsDirty.put(this.gameID, true);
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

    public synchronized boolean addPlayer(Identifier player) {
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
        if (startPiece.move(board, end)) {
            onChange();
            return true;
        }
        return false;
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

    public byte[] generateThumbnail() {
        if (thumbnailsDirty.getOrDefault(this.gameID, true)) {
            BufferedImage image = new BufferedImage(640, 640, BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = image.getGraphics();
            graphics.drawImage(defaultBoard, 0, 0, null);
            BoardData data = board.getData();
            ChessPiece[][] pieces = data.getPieces();

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    ChessPiece current = pieces[i][j];
                    if (current == null) {
                        continue;
                    }
                    String name = current.getColor() == Color.WHITE ? "w" : "b";
                    name += current.getType().getAbbreviation();
                    BufferedImage pieceImage = pieceToImageMap.get(name);
                    graphics.drawImage(pieceImage, i * tileSize, j * tileSize, null);
                }
            }

            byte[] img;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream(10000)) {
                ImageIO.write(image, "png", baos);
                img = baos.toByteArray();

            } catch (IOException e) {
                e.printStackTrace();
                return new byte[1];
            }

            generatedThumbnails.put(this.gameID, img);
            thumbnailsDirty.put(this.gameID, false);

            return img;
        } else {
            return generatedThumbnails.get(this.gameID);
        }
    }

    @Override
    public String getName() {
        return "game";
    }

    @Override
    public JsonObject toJson() {
        JsonObject toRet = new JsonObject();
        toRet.addProperty("id", getGameID().getId());
        toRet.addProperty("playerBlack", (getBlackSide() != null) ? getBlackSide().getId() : null);
        toRet.addProperty("playerWhite", (getWhiteSide() != null) ? getWhiteSide().getId() : null);
        toRet.addProperty("status", getStatus().name());
        return toRet;
    }
}
