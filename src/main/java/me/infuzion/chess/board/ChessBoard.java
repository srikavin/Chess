package me.infuzion.chess.board;

import me.infuzion.chess.piece.*;
import me.infuzion.chess.util.PGNParser;

import java.util.ArrayList;
import java.util.List;

import static me.infuzion.chess.piece.Color.BLACK;
import static me.infuzion.chess.piece.Color.WHITE;
import static me.infuzion.chess.piece.PieceType.*;

public class ChessBoard {

    private final Color[][] boardColors;
    private final List<String> moves = new ArrayList<>();
    private final BoardData data;

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
        ChessPiece[][] pieces = new ChessPiece[8][8];
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
        data = new BoardData(pieces);
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
                {BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK},
                {BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK},
                {},
                {},
                {},
                {},
                {WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE},
                {WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE}
        };
        return new ChessBoard(pieces, boardColor, pieceColors);
    }

    public static ChessBoard getDefaultBoard() {
        return getChessBoard();
    }

    public static boolean isUnderCheck(Color side, BoardData data) {
        return isUnderCheck(side, data.getPieces());
    }

    public static boolean isUnderCheck(Color side, ChessPiece[][] pieces) {
        King king = getKing(side, pieces);
        return king != null && piecesThatCanMoveTo(king.currentPosition(), new BoardData(pieces), getOppositeColor(side)).size() > 0;
    }

    public static List<ChessPiece> piecesThatCanMoveTo(ChessPosition position, BoardData data, Color color) {
        boolean skipColor = color == null;
        List<ChessPiece> piecesToRet = new ArrayList<>();
        for (ChessPiece[] pieceArr : data.getPieces()) {
            for (ChessPiece piece : pieceArr) {
                if (piece == null) {
                    continue;
                }
                if (skipColor || piece.getColor() == color) {
                    if (piece.allowed(data, position, true)) {
                        piecesToRet.add(piece);
                    }
                }
            }
        }
        return piecesToRet;
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

    private static Color getOppositeColor(Color color) {
        if (color == WHITE) {
            return BLACK;
        }
        if (color == BLACK) {
            return WHITE;
        }
        return null;

    }

    public static ChessBoard fromPGNString(String pgn) {
        ChessBoard board = getDefaultBoard();
        PGNParser.executeMoves(board, pgn, WHITE);
        return board;
    }

    public static boolean checkAfterMove(BoardData data, Color side, ChessPosition start,
                                         ChessPosition end) {

        ChessPiece piece = data.getPiece(start).clone();
        ChessPiece[][] pieceClone = data.getPieces();
        pieceClone[start.getCol()][start.getRow()] = null;
        piece.setPosition(end);
        pieceClone[end.getCol()][end.getRow()] = piece;
        return isUnderCheck(side, pieceClone);
    }

    public static void main(String[] a) {
        System.out.println(ChessBoard.getDefaultBoard().toFen());
    }

    public String toFen() {
        StringBuilder builder = new StringBuilder();
        ChessPiece[][] pieces = data.getPieces();
        for (int i = 0; i < pieces.length; i++) {
            int counter = 0;
            for (int j = 0; j < pieces[i].length; j++) {
                ChessPiece piece = pieces[j][i];
                if (piece == null) {
                    counter++;
                    continue;
                }
                if (counter > 0) {
                    builder.append(counter);
                    counter = 0;
                }
                char abbr = piece.getType().getAbbreviation();
                char p = piece.getColor() == WHITE ? Character.toUpperCase(abbr) : Character.toLowerCase(abbr);
                builder.append(p);
            }
            if (counter == 8) {
                builder.append(counter);
            }
            builder.append('/');
        }
        return builder.deleteCharAt(builder.lastIndexOf("/")).toString();
    }

    public BoardData getData() {
        return data;
    }

    public boolean isUnderCheck(Color side) {
        return isUnderCheck(side, data.getPieces());
    }

    public Color[][] getBoardColors() {
        return boardColors;
    }

    public void move(ChessPiece piece, ChessPosition start, ChessPosition end) {
        char abr = piece.getType().getAbbreviation();
        List<ChessPiece> movable = piecesThatCanMoveTo(end, data, piece.getColor());
        boolean ambiguous;

        if (movable.size() == 0) {
            throw new RuntimeException("INVALID MOVE");
        }

        if (movable.size() == 1) {
            ambiguous = false;
        } else {
            int counter = 0;
            for (ChessPiece e : movable) {
                if (e.getType() == piece.getType()) {
                    counter++;
                }
            }
            ambiguous = counter > 1;
        }
        moves.add(generateAlgebraicNotation(abr, start, end, ambiguous));
    }

    /**
     * @param move Move in algebraic notation
     * @return Whether or not the move was executed successfully
     */
    public boolean executeMove(String move, Color color) {
        return PGNParser.executeMove(this, move, color);
    }

    //
    private String generateAlgebraicNotation(char abbr, ChessPosition start,
                                             ChessPosition end, boolean ambiguous) {
        String capt = data.getPiece(end) != null ? "x" : "";

        if (!ambiguous) {
            return abbr + capt + end.getPosition();
        } else {
            return abbr + start.getPosition() + capt + end.getPosition();
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = data.getPiece(i, j);
                if (piece == null) {
                    builder.append(' ');
                } else {
                    builder.append(data.getPiece(i, j).getType().getAbbreviation());
                }
                builder.append(' ');
            }
            builder.append('\n');
        }
        return builder.toString();
    }

    public String toPGNString() {
        StringBuilder builder = new StringBuilder();
        int count = 1;
        boolean writing = false;
        for (String move : moves) {
            if (!writing) {
                builder.append(count).append(". ").append(move);
                writing = true;
            } else {
                builder.append(' ').append(move).append(' ');
                writing = false;
                count++;
            }
        }
        return builder.toString();
    }
}
