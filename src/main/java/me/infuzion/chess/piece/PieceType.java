package me.infuzion.chess.piece;

public enum PieceType {
    BISHOP('B'),
    KING('K'),
    KNIGHT('N'),
    PAWN('P'),
    QUEEN('Q'),
    ROOK('R');

    private final char abbreviation;

    PieceType(char abbreviation) {
        this.abbreviation = abbreviation;
    }

    public static PieceType fromAbbreviation(char abbr) {
        char abbr1 = Character.toUpperCase(abbr);
        for (PieceType e : PieceType.values()) {
            if (abbr1 == e.abbreviation) {
                return e;
            }
        }
        return null;
    }

    public char getAbbreviation() {
        return abbreviation;
    }
}
