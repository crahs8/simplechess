/**
 * Represents a regular chess move.
 */
public class RegularMove extends Move {
    public RegularMove(int r1, int c1, int r2, int c2, Piece piece, Board.CastlingRights castlingRights, int fiftyMoveClock) {
        super(r1, c1, r2, c2, piece, castlingRights, fiftyMoveClock);
    }

    /**
     * Creates a string representation of the move. EG. "e2-e4".
     *
     * @return the string representation of the move.
     */
    public String toString() {
        return columnToChar(c1) + "" + (8 - r1) + "-" + columnToChar(c2) + "" + (8 - r2);
    }

}
