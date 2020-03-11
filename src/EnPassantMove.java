/**
 * Represents an en passant chess move.
 */
public class EnPassantMove extends Move {
    /**
     * Constructs an EnPassantMove object from a given origin column, destination column and piece.
     *
     * @param c1    The origin column.
     * @param c2    The destination column.
     * @param piece The piece.
     */
    public EnPassantMove(int c1, int c2, Piece piece) {
        super(Board.getRow(4, piece.getColor()), c1, Board.getRow(5, piece.getColor()), c2, piece);
    }

    /**
     * Creates a string representation of the move. EG. "e5 to d6: EP".
     *
     * @return the string representation of the move.
     */
    public String toString() {
        return columnToChar(c1) + "" + (8 - r1) + " to " + columnToChar(c2) + "" + (8 - r2) + ": EP";
    }
}
