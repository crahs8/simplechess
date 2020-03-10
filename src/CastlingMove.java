/**
 * Represents a castling chess move.
 */
public class CastlingMove extends Move {
    /**
     * Constructs a CastlingMove from a destination column and a piece. Extends Move.
     *
     * @param c     The destination column.
     * @param piece The piece.
     */
    public CastlingMove(int c, Piece piece) {
        this(piece.getColor() == Color.WHITE ? 7 : 0, c, piece);
    }

    private CastlingMove(int r, int c, Piece p) {
        super(r, 4, r, c, p);
    }

    /**
     * Creates a string representation of the move. EG. "White KC" for kingside castling by white.
     *
     * @return the string representation of the move.
     */
    public String toString() {
        return piece.getColor() + " " + (c2 == 6 ? "KC" : "QC");
    }
}
