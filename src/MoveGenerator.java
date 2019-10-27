import java.util.*;

/**
 * Generates legal moves in the board position.
 */
public class MoveGenerator {
    private Board board;

    /**
     * Constructs a MoveGenerator for the given board.
     *
     * @param board The board to generate moves for.
     */
    public MoveGenerator(Board board) {
        this.board = board;
    }

    /**
     * Returns whether the king of a given color is in check.
     *
     * @param color The color of the king.
     * @return whether the king is check.
     */
    private boolean isCheck(Color color) {
        return false;
    }

    /**
     * Creates and adds a Move from a given starting square to a given
     * ending square to the given List of moves, if the move is legal.
     *
     * @param r1    The row of the stating square.
     * @param c1    The column of the starting square.
     * @param r2    The row of the ending square.
     * @param c2    The column of the ending square.
     * @param moves The List of moves to add to.
     */
    private void addMove(int r1, int c1, int r2, int c2, List<Move> moves) {
        Move move = new Move(r1, c1, r2, c2);
        board.makeMove(move);
        if (isCheck(board.getToMove())) moves.add(move);
        board.unmakeMove();
    }

    /**
     * Generates and adds all pawn moves from a given square to a list of moves.
     *
     * @param r The row of the square.
     * @param c The column of the square.
     * @param moves The list of moves to add to.
     */
    private void generatePawnMoves(int r, int c, List<Move> moves) {
        int secondRow = board.getToMove() == Color.WHITE ? 6 : 1;
        int row1 = r + (board.getToMove() == Color.WHITE ? -1 : 1);
        int row2 = c + (board.getToMove() == Color.WHITE ? -2 : 2);
        // 1 forward
        if (board.squareHasPiece(row1, c, Piece.EMPTY))
            addMove(r, c, row1, c, moves);
        // 2 forward
        if (r == secondRow && board.squareHasPiece(row1, c, Piece.EMPTY)
                && board.squareHasPiece(row2, c, Piece.EMPTY))
            addMove(r, c, row2, c, moves);
        // Diagonal left
        //if()
    }

    /**
     * Generates a List of all the legal moves in the current position.
     *
     * @return all the legal moves in the position.
     */
    public List<Move> generateMoves() {
        List<Move> moves = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                switch (board.getPiece(i, j).getType()) {
                    case PAWN:
                        generatePawnMoves(i, j, moves);
                        break;
                }
            }
        }

        return moves;
    }
}
