package me.infuzion.chess;

import static me.infuzion.chess.piece.Color.BLACK;
import static me.infuzion.chess.piece.Color.WHITE;
import static me.infuzion.chess.piece.PieceType.BISHOP;
import static me.infuzion.chess.piece.PieceType.KING;
import static me.infuzion.chess.piece.PieceType.KNIGHT;
import static me.infuzion.chess.piece.PieceType.PAWN;
import static me.infuzion.chess.piece.PieceType.QUEEN;
import static me.infuzion.chess.piece.PieceType.ROOK;

import me.infuzion.chess.piece.Bishop;
import me.infuzion.chess.piece.Color;
import me.infuzion.chess.piece.King;
import me.infuzion.chess.piece.Knight;
import me.infuzion.chess.piece.Pawn;
import me.infuzion.chess.piece.PieceType;
import me.infuzion.chess.piece.Queen;
import me.infuzion.chess.piece.Rook;

public class ChessBoard implements Cloneable {

    private static final ChessBoard DEFAULT_BOARD;

    static {
        final PieceType[][] pieces = {
            {ROOK, KNIGHT, BISHOP, KING, QUEEN, BISHOP, KNIGHT, ROOK},
            {PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN},
            {},
            {},
            {},
            {},
            {PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN},
            {ROOK, KNIGHT, BISHOP, KING, QUEEN, BISHOP, KNIGHT, ROOK}
        };
        final Color[][] boardColor = {
            {WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK},
            {BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE},
            {WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK},
            {BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE},
            {WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK},
            {BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE},
            {WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK},
            {BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE},
        };
        final Color[][] pieceColors = {
            {WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE},
            {WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE},
            {},
            {},
            {},
            {},
            {BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK},
            {BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK}
        };
        DEFAULT_BOARD = new ChessBoard(pieces, boardColor, pieceColors);
    }

    private final ChessPiece[][] pieces;
    private final Color[][] boardColors;

    public ChessBoard(PieceType[][] pieceTypes, Color[][] boardColor, Color[][] pieceColors) {
        if (pieceTypes.length == 8 && boardColor.length == 8) {
            for (Color[] e : boardColor) {
                if (e.length != 8) {
                    throw new RuntimeException("Invalid input array.");
                }
            }
        } else {
            throw new RuntimeException("Invalid input array.");
        }
        this.boardColors = boardColor;
        this.pieces = new ChessPiece[8][8];
        for (int i = 0; i < 8; i++) {
            PieceType[] curTypes = pieceTypes[i];
            Color[] curColors = pieceColors[i];
            if (curTypes.length != curColors.length) {
                throw new RuntimeException("Input arrays are not equal in length!");
            }
            for (int j = 0; j < 8; j++) {
                ChessPiece piece;
                ChessPosition curPosition = new ChessPosition(i, j);
                if (curTypes.length < 8) {
                    pieces[j][i] = null;
                    continue;
                }
                switch (curTypes[j]) {
                    case BISHOP:
                        piece = new Bishop(curColors[j], curPosition);
                        break;
                    case KING:
                        piece = new King(curColors[j], curPosition);
                        break;
                    case KNIGHT:
                        piece = new Knight(curColors[j], curPosition);
                        break;
                    case PAWN:
                        piece = new Pawn(curColors[j], curPosition);
                        break;
                    case QUEEN:
                        piece = new Queen(curColors[j], curPosition);
                        break;
                    case ROOK:
                        piece = new Rook(curColors[j], curPosition);
                        break;
                    default:
                        if (curTypes[j] == null) {
                            piece = null;
                            break;
                        }
                        throw new RuntimeException(
                            "Unknown input piece type: " + curTypes[i].name());
                }
                pieces[j][i] = piece;
            }
        }
    }

    public static void main(String[] str) {
        ChessBoard a = ChessBoard.DEFAULT_BOARD;
    }

    public static ChessBoard getDefaultBoard() {
        try {
            return (ChessBoard) DEFAULT_BOARD.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public ChessPiece[][] getPieces() {
        return pieces.clone();
    }

    public void setPiece(int x, int y, ChessPiece piece) {
        pieces[y][x] = piece;
    }

    public void setPiece(ChessPosition pos, ChessPiece piece) {
        setPiece(pos.getRow(), pos.getCol(), piece);
    }

    public ChessPiece getPiece(ChessPosition pos) {
        return getPiece(pos.getRow(), pos.getCol());
    }

    public ChessPiece getPiece(int row, int col) {
        return pieces[col][row];
    }

    public Color[][] getBoardColors() {
        return boardColors;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (ChessPiece[] e : pieces) {
            for (ChessPiece f : e) {
                builder.append(f == null ? "null" : f.getType()).append(' ');
            }
            builder.append('\n');
        }
        return builder.toString();
    }
}
