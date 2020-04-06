/**
 * Represents a chess piece with a type and color.
 */
public class Piece {
    public static final Piece EMPTY = new Piece(Type.EMPTY, null);
    private static final int EMPTY_HASH_CODE = EMPTY.hashCode();

    private final Type type;
    private final Color color;

    /**
     * Returns a new Piece from a piece character. Eg. 'P' for a white pawn.
     *
     * @param p The piece character.
     * @return the new piece.
     */
    public static Piece fromChar(char p) {
        if (p == ' ') {
            return EMPTY;
        } else return new Piece(p);
    }

    /**
     * Constructs a Piece from a given type and color.
     *
     * @param type  The type.
     * @param color The color.
     */
    public Piece(Type type, Color color) {
        this.type = type;
        this.color = color;
    }
    
    private Piece(char p) {
        if (Character.isLowerCase(p)) {
            this.color = Color.BLACK;
        } else this.color = Color.WHITE;
        try {
            this.type = Type.fromChar(p);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Type getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Piece)) return false;
        return type == ((Piece) o).type && color == ((Piece) o).color;
    }

    @Override
    public String toString() {
        return color == Color.WHITE ? type.toString() : type.toString().toLowerCase();
    }

    @Override
    public int hashCode() {
        return color != null ? type.hashCode() ^ (13 * color.hashCode()) : EMPTY_HASH_CODE;
    }

    public enum Type {
        PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING, EMPTY;

        @Override
        public String toString() {
            switch (this) {
                case PAWN:
                    return "P";
                case KNIGHT:
                    return "N";
                case BISHOP:
                    return "B";
                case ROOK:
                    return "R";
                case QUEEN:
                    return "Q";
                case KING:
                    return "K";
                case EMPTY:
                    return " ";
                default:
                    return "?"; // Needed for the compiler to be happy.
            }
        }

        public static Type fromChar(char c) throws ParseException {
            char p = c;
            if (Character.isLowerCase(c)) p = Character.toUpperCase(c);
            switch (p) {
                case 'P':
                    return PAWN;
                case 'N':
                    return KNIGHT;
                case 'B':
                    return BISHOP;
                case 'R':
                    return ROOK;
                case 'Q':
                    return QUEEN;
                case 'K':
                    return KING;
                default:
                    throw new ParseException(c + " is not a valid piece character");
            }
        }
    }
}

