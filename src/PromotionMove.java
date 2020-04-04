/**
 * Represents a promoting chess move.
 */
public class PromotionMove extends Move {
    private final Piece.Type promotion;

    public PromotionMove(int r1, int c1, int r2, int c2, Piece piece, Board.CastlingRights castlingRights, int fiftyMoveClock, Piece.Type promotion) {
        super(r1, c1, r2, c2, piece, castlingRights, fiftyMoveClock);
        this.promotion = promotion;
    }

    public Piece.Type getPromotion() {
        return promotion;
    }

    /**
     * Creates a string representation of the move. EG. "e7e8q".
     *
     * @return the string representation of the move.
     */
    public String toString() {
        return columnToChar(c1) + "" + (8 - r1) + columnToChar(c2) + "" + (8 - r2) + ": " + promotion;
    }
}
