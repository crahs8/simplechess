/**
 * Represents a chess move.
 */
public class Move {
    private final int r1;
    private final int c1;
    private final int r2;
    private final int c2;
    // The char at the move's destination. Used to undo the move.
    private Piece destinationPiece;

    /**
     * Constructs a Move from a starting square to an ending square.
     *
     * @param r1 The row of the starting square.
     * @param c1 The column of the starting square.
     * @param r2 row of the ending square.
     * @param c2 The column of the ending square.
     */
    public Move(int r1, int c1, int r2, int c2) {
        this.r1 = r1;
        this.c1 = c1;
        this.r2 = r2;
        this.c2 = c2;
        destinationPiece = null;
    }

    /**
     * Converts a column index (0 to 7) to chess file (a - h)
     *
     * @param c The column to convert.
     * @return the chess file of the corresponding to the column.
     */
    private static char columnToChar(int c) {
        if (c < 0 || c > 7) throw new IllegalArgumentException(c + " is not a valid column.");
        return (char) (97 + c);
    }

    public int getR1() {
        return r1;
    }

    public int getC1() {
        return c1;
    }

    public int getR2() {
        return r2;
    }

    public int getC2() {
        return c2;
    }

    public Piece getDestinationPiece() {
        if (destinationPiece == null) throw new IllegalStateException("Move wasn't made yet.");
        return destinationPiece;
    }

    public void setDestinationPiece(Piece destinationValue) {
        this.destinationPiece = destinationValue;
    }

    /**
     * Creates a string representation of the move. EG. "e2 to e4".
     *
     * @return the string representation of the move.
     */
    public String toString() {
        return columnToChar(c1) + "" + (8 - r1) + " to " + columnToChar(c2) + "" + (8 - r2);
    }
}
