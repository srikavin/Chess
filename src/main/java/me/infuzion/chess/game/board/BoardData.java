package me.infuzion.chess.game.board;

import me.infuzion.chess.game.piece.*;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumSet;

public class BoardData {

    public static BoardData fromPieceTypes(PieceType[][] pieceTypes, Color[][] pieceColors) {
        if (pieceTypes.length != 8) {
            throw new RuntimeException("Invalid input array.");
        }

        ChessPiece[][] pieces = new ChessPiece[8][8];

        for (int row = 7; row >= 0; row--) {
            PieceType[] curTypes = pieceTypes[row];
            Color[] curColors = pieceColors[row];

            if (curTypes.length != curColors.length) {
                throw new RuntimeException("Input arrays are not equal in length!");
            }

            for (int col = 0; col < 8; col++) {
                ChessPiece piece;
                ChessPosition curPosition = new ChessPosition(col, 7 - row);

                if (curTypes.length <= col) {
                    pieces[7 - row][col] = null;
                    continue;
                }

                if (curTypes[col] == null) {
                    continue;
                }

                switch (curTypes[col]) {
                    case BISHOP:
                        piece = new Bishop(curColors[col], curPosition);
                        break;
                    case KING:
                        piece = new King(curColors[col], curPosition);
                        break;
                    case KNIGHT:
                        piece = new Knight(curColors[col], curPosition);
                        break;
                    case PAWN:
                        piece = new Pawn(curColors[col], curPosition);
                        break;
                    case QUEEN:
                        piece = new Queen(curColors[col], curPosition);
                        break;
                    case ROOK:
                        piece = new Rook(curColors[col], curPosition);
                        break;
                    default:
                        if (curTypes[col] == null) {
                            piece = null;
                            break;
                        }
                        throw new RuntimeException(
                                "Unknown input piece type: " + curTypes[col].name());
                }
                pieces[7 - row][col] = piece;
            }
        }
        return new BoardData(pieces);
    }

    private final ChessPiece[][] pieces;

    @Nullable
    private ChessPosition enPassantSquare = null;

    private final EnumSet<CastlingAvailability> castlingAvailability = EnumSet.allOf(CastlingAvailability.class);

    public BoardData(ChessPiece[][] pieces) {
        this.pieces = pieces;
    }

    /**
     * Creates a copy of the given board data object and any references to objects contained within.
     *
     * @param data
     */
    public BoardData(BoardData data) {
        this.pieces = Arrays.stream(data.pieces).map(ChessPiece[]::clone).toArray(ChessPiece[][]::new);
        this.enPassantSquare = data.enPassantSquare;
    }

    public ChessPiece[][] getPieces() {
        return pieces;
    }

    public void setPiece(int rank, int file, ChessPiece piece) {
        pieces[file][rank] = piece;
    }

    public void setPiece(ChessPosition pos, ChessPiece piece) {
        setPiece(pos.getRank(), pos.getFile(), piece);
    }

    public ChessPiece getPiece(ChessPosition pos) {
        return getPiece(pos.getRank(), pos.getFile());
    }

    public void movePieceWithoutVerification(ChessPosition start, ChessPosition end) {
        ChessPiece piece = getPiece(start);
        setPiece(start, null);

        piece.setPosition(end);
        setPiece(end, piece);
    }

    public ChessPiece getPiece(int rank, int file) {
        return pieces[file][rank];
    }

    public @Nullable ChessPosition getEnPassantSquare() {
        return enPassantSquare;
    }

    public void setEnPassantSquare(@Nullable ChessPosition enPassantSquare) {
        this.enPassantSquare = enPassantSquare;
    }

    public static void main(String[] args) {
        System.out.println(ChessBoard.getDefaultBoard().toFen());
    }

    public void setCastlingAvailabilityFromFenString(String fen) {
        if (fen.contains("K")) {
            castlingAvailability.add(CastlingAvailability.WHITE_KING_SIDE);
        }
        if (fen.contains("Q")) {
            castlingAvailability.add(CastlingAvailability.WHITE_QUEEN_SIDE);
        }
        if (fen.contains("k")) {
            castlingAvailability.add(CastlingAvailability.BLACK_KING_SIDE);
        }
        if (fen.contains("q")) {
            castlingAvailability.add(CastlingAvailability.BLACK_QUEEN_SIDE);
        }
    }

    public String getCastlingAvailabilityAsFenString() {
        if (castlingAvailability.isEmpty()) {
            return "-";
        }
        String ret = "";
        if (castlingAvailability.contains(CastlingAvailability.WHITE_KING_SIDE)) {
            ret += 'K';
        }
        if (castlingAvailability.contains(CastlingAvailability.WHITE_QUEEN_SIDE)) {
            ret += 'Q';
        }
        if (castlingAvailability.contains(CastlingAvailability.BLACK_KING_SIDE)) {
            ret += 'k';
        }
        if (castlingAvailability.contains(CastlingAvailability.BLACK_QUEEN_SIDE)) {
            ret += 'q';
        }

        return ret;
    }


    public EnumSet<CastlingAvailability> getCastlingAvailability() {
        return castlingAvailability;
    }
}
