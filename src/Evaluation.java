/**
 * Used for evaluating a chess position.
 */
public class Evaluation {
    private Board board;

    public Evaluation(Board board) {
        this.board = board;
    }

    private int pieceValue(Piece p) {
        int c = p.getColor() == board.getToMove() ? 1 : -1;
        switch (p.getType()) {
            case EMPTY:
                return 0;
            case PAWN:
                return c;
            case KNIGHT:
            case BISHOP:
                return 3 * c;
            case ROOK:
                return 5 * c;
            case QUEEN:
                return 9 * c;
            case KING:
                return 200 * c;
            default:
                throw new IllegalArgumentException("Null piece");
        }
    }

    /**
     * Evaluates the current position relative to the player to move.
     *
     * @return The evaluation for the current position;
     */
    public int evaluate() {
        int eval = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                eval += pieceValue(board.getPiece(r, c));
            }
        }
        return eval;
    }
}
