/**
 * Represents an abstract chess move.
 */
public abstract class Move {
    protected final int r1;
    protected final int c1;
    protected final int r2;
    protected final int c2;
    protected final Piece piece;

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

    @Override
    public abstract String toString();
}
