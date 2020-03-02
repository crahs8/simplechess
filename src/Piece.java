public class Piece {
    public static final Piece EMPTY = new Piece(Type.EMPTY, null);

    private final Type type;
    private final Color color;

    public static Piece fromChar(char p) {
        if (p == ' ') {
            return EMPTY;
        } else return new Piece(p);
    }

    public Piece(Type type, Color color) {
        this.type = type;
        this.color = color;
    }
    
    private Piece(char p) {
        if (Character.isLowerCase(p)) {
            this.color = Color.BLACK;
            p = Character.toUpperCase(p);
        } else this.color = Color.WHITE;
        switch (p) {
            case 'P':
                this.type = Type.PAWN;
                break;
            case 'N':
                this.type = Type.KNIGHT;
                break;
            case 'B':
                this.type = Type.BISHOP;
                break;
            case 'R':
                this.type = Type.ROOK;
                break;
            case 'Q':
                this.type = Type.QUEEN;
                break;
            case 'K':
                this.type = Type.KING;
                break;
            default:
                throw new IllegalArgumentException(p + " is not a valid piece character.");
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
        return type == ((Piece) o).getType() && color == ((Piece) o).getColor();
    }

    @Override
    public String toString() {
        return color == Color.WHITE ? type.toString() : type.toString().toLowerCase();
    }

    public enum Type {
        PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING, EMPTY;

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
    }
}

