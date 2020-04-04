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
    public CastlingMove(int c, Piece piece, Board.CastlingRights castlingRights, int fiftyMoveClock) {
        this(Board.getRow(0, piece.getColor()), c, piece, castlingRights, fiftyMoveClock);
        if (c != 6 && c != 2) throw new IllegalArgumentException("Illegal destination column " + c);
    }

    private CastlingMove(int r, int c, Piece p, Board.CastlingRights castlingRights, int fiftyMoveClock) {
        super(r, 4, r, c, p, castlingRights, fiftyMoveClock);
        setDestinationPiece(Piece.EMPTY);
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
