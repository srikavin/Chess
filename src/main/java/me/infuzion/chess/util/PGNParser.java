package me.infuzion.chess.util;

import me.infuzion.chess.ChessBoard;
import me.infuzion.chess.ChessPiece;
import me.infuzion.chess.ChessPosition;
import me.infuzion.chess.piece.Color;
import me.infuzion.chess.piece.PieceType;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class PGNParser {
    private final static String pgnMoveRegex = "((?:[a-z][a-z]*[0-9]+[a-z0-9]*))";
    private final static Pattern movePattern = Pattern.compile(pgnMoveRegex, Pattern.CASE_INSENSITIVE);

    public static void executeMoves(ChessBoard board, String moves, Color turn) {
        Matcher matcher = movePattern.matcher(moves);
        Color curTurn = turn;
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                executeMove(board, matcher.group(i), curTurn);
                curTurn = curTurn.invert();
            }
        }
    }

    public static boolean executeMove(ChessBoard board, String move, Color turn) {
        if (move.length() > 5) {
            return false;
        }
        Lexer lexer = new Lexer(move);
        ChessPosition pos1 = null;
        ChessPosition pos2 = null;
        int file = -1; //col
        int rank = -1; //row
        PieceType type = null;
        boolean lastWasFile = false;

        while (true) {
            Token token = lexer.getNextToken();
            if (token.type == Tokens.END) {
                break;
            }
            if (token.type == Tokens.FILE) {
                file = ChessPosition.colCharToInt(token.value.charAt(0));
                lastWasFile = true;
                continue;
            } else if (token.type == Tokens.PIECETYPE) {
                type = PieceType.fromAbrreviation(token.value.charAt(0));
            } else if (token.type == Tokens.RANK) {
                rank = Integer.parseInt(token.value);
                if (lastWasFile) {
                    if (pos1 == null) {
                        pos1 = new ChessPosition(rank, file);
                    } else {
                        pos2 = new ChessPosition(rank, file);
                    }
                }
                continue;
            }
            lastWasFile = false;
        }
        ChessPiece start;
        ChessPosition end;
        if (pos1 == null) {
            return false;
        }
        if (pos2 == null) {
            PieceType finalType = type;
            List<ChessPiece> possible = ChessBoard.piecesThatCanMoveTo(pos1, board.getData(), turn)
                    .stream().filter(piece -> piece.getType() == finalType)
                    .collect(Collectors.toList());
            if (possible.size() != 1) {
                return false;
            }
            start = possible.get(0);
            end = pos1;
        } else {
            start = board.getData().getPiece(pos1);
            end = pos2;
        }

        return start.move(board, end);
    }

    private enum Tokens {
        PIECETYPE,
        CAPTURED,
        FILE,
        RANK,
        END
    }

    private static class Lexer {

        private String input;
        private int index;
        private Character cur;

        public Lexer(String string) {
            this.input = string;
            index = -1;
        }

        private void advance() {
            try {
                cur = input.charAt(++index);
            } catch (Exception e) {
                cur = null;
            }
        }

        public Token getNextToken() {
            advance();
            if (cur == null) {
                return new Token(Tokens.END, "");
            }
            if (cur == 'x') {
                return new Token(Tokens.CAPTURED, "x");
            }
            if (Character.isDigit(cur)) {
                return new Token(Tokens.RANK, cur.toString());
            }
            if (PieceType.fromAbrreviation(cur) != null) {
                return new Token(Tokens.PIECETYPE, cur.toString());
            }
            if ('A' <= Character.toUpperCase(cur) && Character.toUpperCase(cur) <= 'H') {
                return new Token(Tokens.FILE, cur.toString());
            }
            throw new RuntimeException("Unknown input: " + cur);
        }

    }

    static class Token {

        private final Tokens type;
        private final String value;

        public Token(Tokens type, String value) {
            this.type = type;
            this.value = value;
        }
    }
}
