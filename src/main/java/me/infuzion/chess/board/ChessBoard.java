package me.infuzion.chess.board;

import me.infuzion.chess.piece.ChessPiece;
import me.infuzion.chess.piece.Color;
import me.infuzion.chess.piece.King;
import me.infuzion.chess.piece.PieceType;
import me.infuzion.chess.util.PGNParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static me.infuzion.chess.piece.Color.BLACK;
import static me.infuzion.chess.piece.Color.WHITE;
import static me.infuzion.chess.piece.PieceType.*;

public class ChessBoard {
    private final List<ChessMove> moves = new ArrayList<>();

    private final BoardData data;
    private Color currentTurn = WHITE;
    private int halfMoveClock = 0;

    public ChessBoard(BoardData data) {
        this.data = data;
    }

    private static ChessBoard getChessBoard() {
        final PieceType[][] pieces = {
                {ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK},
                {PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN},
                {},
                {},
                {},
                {},
                {PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN},
                {ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK}
        };
        final Color[][] pieceColors = {
                {BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK},
                {BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK},
                {},
                {},
                {},
                {},
                {WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE},
                {WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE}
        };
        return new ChessBoard(BoardData.fromPieceTypes(pieces, pieceColors));
    }

    public static ChessBoard getDefaultBoard() {
        return getChessBoard();
    }

    public static boolean isUnderCheck(@NotNull Color side, BoardData data) {
        King king = getKing(side, data.getPieces());
        return king != null && canPieceAttack(king.currentPosition(), data, side.invert());
    }

    public static boolean canPieceAttack(@NotNull ChessPosition position, @NotNull BoardData data, @NotNull Color color) {
        for (ChessPiece[] pieceArr : data.getPieces()) {
            for (ChessPiece piece : pieceArr) {
                if (piece == null) {
                    continue;
                }
                if (piece.getColor() == color) {
                    if (piece.isMoveAllowedIgnoringCheck(data, new ChessMove(piece.currentPosition(), position), true)) {
                        System.out.println(position.toString() + piece.currentPosition());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static List<ChessPiece> piecesThatCanMoveTo(ChessPosition position, BoardData data, @Nullable Color color) {
        boolean skipColor = color == null;
        List<ChessPiece> piecesToRet = new ArrayList<>();
        for (ChessPiece[] pieceArr : data.getPieces()) {
            for (ChessPiece piece : pieceArr) {
                if (piece == null) {
                    continue;
                }
                if (skipColor || piece.getColor() == color) {
                    if (piece.isMoveAllowedIgnoringCheck(data, new ChessMove(piece.currentPosition(), position), true)) {
                        piecesToRet.add(piece);
                    }
                }
            }
        }
        return piecesToRet;
    }

    public static boolean isUnderCheckAfterMove(@NotNull BoardData data, @NotNull Color side, @NotNull ChessMove move) {
        ChessPosition start = move.getSource();
        ChessPosition end = move.getEnd();

        ChessPiece piece = data.getPiece(start).clone();

        ChessBoard clone = new ChessBoard(new BoardData(data));
        piece.executeMoveWithoutValidation(clone, move);
        piece.setPosition(end);
        return isUnderCheck(side, clone.data);
    }

    private static King getKing(Color color, ChessPiece[][] pieces) {
        for (ChessPiece[] pieceArr : pieces) {
            for (ChessPiece piece : pieceArr) {
                if (piece == null) {
                    continue;
                }
                if (piece.getColor() == color && piece instanceof King) {
                    return (King) piece;
                }
            }
        }
        return null;
    }

    public static ChessBoard fromPGNString(String pgn) {
        ChessBoard board = getDefaultBoard();
        PGNParser.executeMoves(board, pgn, WHITE);
        return board;
    }

    public static ChessBoard fromFen(String fen) {
        String[] split = fen.split(" ", 6);

        if (split.length != 6) {
            throw new IllegalArgumentException("invalid fen");
        }


        final PieceType[][] pieces = new PieceType[8][8];
        final Color[][] pieceColors = new Color[8][8];

        String[] fenPieces = split[0].split("/");

        for (int i = 0; i < 8; i++) {
            String fenRank = fenPieces[i];

            int count = 0;
            for (int j = 0; count < 8; j++) {
                char cur = fenRank.charAt(j);
                if (Character.isDigit(cur)) {
                    count += Character.getNumericValue(cur);
                    continue;
                }
                pieceColors[i][count] = Character.isUpperCase(cur) ? WHITE : BLACK;
                pieces[i][count] = PieceType.fromAbbreviation(cur);
                count++;
            }
        }

        BoardData data = BoardData.fromPieceTypes(pieces, pieceColors);

        data.setCastlingAvailabilityFromFenString(split[2]);
        data.setEnPassantSquare(split[3].equals("-") ? null : new ChessPosition(split[3]));

        ChessBoard board = new ChessBoard(data);

        board.currentTurn = split[1].equals("w") ? WHITE : BLACK;
        board.halfMoveClock = Integer.parseInt(split[4]);

        return board;
    }

    public static ChessBoard fromInitialFen(String fen, List<ChessMove> moves) {
        ChessBoard board = ChessBoard.fromFen(fen);

        for (ChessMove move : moves) {
            if (!board.move(move)) {
                throw new IllegalArgumentException("invalid move list");
            }
        }

        return board;
    }

    public static void main(String[] args) {
        System.out.println(fromInitialFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", List.of(new ChessMove("e2", "e4"))));

    }

    public int getCurrentPly() {
        return moves.size();
    }

    public BoardData getData() {
        return data;
    }

    public boolean move(ChessMove move) {
        ChessPiece from = data.getPiece(move.getSource());
        ChessPiece to = data.getPiece(move.getEnd());

        if (from == null) {
            return false;
        }

        boolean valid = from.move(this, move);

        if (!valid) {
            return false;
        }

        halfMoveClock++;

        // if move is capture or pawn move, reset half move clock
        if (from.getType() == PAWN || to != null) {
            halfMoveClock = 0;
        }

        return true;
    }

    public void recordMove(ChessMove move) {
        moves.add(move);
        currentTurn = currentTurn.invert();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = data.getPiece(j, i);
                if (piece == null) {
                    builder.append(' ');
                } else {
                    char abbr = piece.getType().getAbbreviation();
                    builder.append(piece.getColor() == WHITE ? Character.toUpperCase(abbr) : Character.toLowerCase(abbr));
                }
                builder.append(' ');
            }
            builder.append('\n');
        }
        return builder.toString();
    }

    public String toFen() {
        StringJoiner joiner = new StringJoiner("/");
        for (int i = data.getPieces().length - 1; i >= 0; i--) {
            ChessPiece[] rank = data.getPieces()[i];
            StringBuilder builder = new StringBuilder();
            int counter = 0;

            for (ChessPiece piece : rank) {
                if (piece == null) {
                    counter++;
                    continue;
                }

                if (counter != 0) {
                    builder.append(counter);
                    counter = 0;
                }

                char abbr = piece.getType().getAbbreviation();
                char p = piece.getColor() == WHITE ? Character.toUpperCase(abbr) : Character.toLowerCase(abbr);
                builder.append(p);
            }

            if (counter != 0) {
                builder.append(counter);
            }

            joiner.add(builder.toString());
        }

        String pieces = joiner.toString();
        String activeColor = currentTurn == WHITE ? "w" : "b";
        String castlingAvailability = data.getCastlingAvailabilityAsFenString();
        String enPassantTarget = data.getEnPassantSquare() == null ? "-" : data.getEnPassantSquare().getPosition();

        return String.format("%s %s %s %s %d %d", pieces, activeColor, castlingAvailability, enPassantTarget, halfMoveClock, (moves.size() / 2) + 1);
    }
}
