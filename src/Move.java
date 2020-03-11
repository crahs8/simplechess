/**
 * Represents an abstract chess move.
 */
public abstract class Move {
    protected final int r1;
    protected final int c1;
    protected final int r2;
    protected final int c2;
    protected final Piece piece;
    // The piece at the move's destination. Used to undo the move.
    private Piece destinationPiece;

    /**
     * Constructs a Move from a starting square to an ending square with a given piece.
     *
     * @param r1    The row of the starting square.
     * @param c1    The column of the starting square.
     * @param r2    row of the ending square.
     * @param c2    The column of the ending square.
     * @param piece The piece.
     */
    protected Move(int r1, int c1, int r2, int c2, Piece piece) {
        this.r1 = r1;
        this.c1 = c1;
        this.r2 = r2;
        this.c2 = c2;
        this.piece = piece;
        destinationPiece = null;
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

    public Piece getPiece() {
        return piece;
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
    protected static char columnToChar(int c) {
        if (c < 0 || c > 7) throw new IllegalArgumentException(c + " is not a valid column.");
        return (char) (97 + c);
    }

    @Override
    public abstract String toString();
}
