public class RegularMove extends Move {
    // The piece at the move's destination. Used to undo the move.
    private Piece destinationPiece;

    public RegularMove(int r1, int c1, int r2, int c2, Piece p) {
        super(r1, c1, r2, c2, p);
        destinationPiece = null;
    }

    public Piece getDestinationPiece() {
        if (destinationPiece == null) throw new IllegalStateException("Move wasn't made yet.");
        return destinationPiece;
    }

    public void setDestinationPiece(Piece destinationValue) {
        this.destinationPiece = destinationValue;
    }

    /**
     * Converts a column index (0 to 7) to chess file (a - h)
     *
     * @param c The column to convert.
     * @return the chess file corresponding to the column.
     */
    private static char columnToChar(int c) {
        if (c < 0 || c > 7) throw new IllegalArgumentException(c + " is not a valid column.");
        return (char) (97 + c);
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
