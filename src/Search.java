import java.util.List;

/**
 * Used for finding the best move in a position.
 */
public class Search {
    private Board board;
    private Evaluation eval;

    /**
     * Constructs a Search object from a given board.
     *
     * @param board The board.
     */
    public Search(Board board) {
        this.board = board;
        eval = new Evaluation(board);
    }

    private int negaMax(int depth) {
        if (depth == 0) return eval.evaluate();

        int max = Integer.MIN_VALUE;
        List<Move> moves = board.getLegalMoves();
        // check for stalemate
        if (moves.size() == 0 && !board.isCheck()) return 0;
        // check for fifty-move rule
        if (board.getFiftyMoveClock() == 50) return 0;
        // check for three-fold repetition
        if (board.positionRepeated()) return 0;

        for (Move m : moves)  {
            board.makeMove(m);
            int score = -negaMax(depth - 1);
            board.unmakeMove();
            if(score > max) max = score;
        }

        return max;
    }

    /**
     * Returns the best move in the position using the NegaMax search algorithm.
     *
     * @param depth the search depth.
     * @return the best move.
     */
    public Move findBestMove(int depth) {
        if (depth < 1) throw new IllegalArgumentException("Depth must be at least 1");

        Move bestMove = null;
        int max = Integer.MIN_VALUE;
        List<Move> moves = board.getLegalMoves();

        for (Move m : moves) {
            board.makeMove(m);
            int score = -negaMax(depth - 1);
            board.unmakeMove();
            if(score > max) {
                max = score;
                bestMove = m;
            }
        }

        return bestMove;
    }
}
