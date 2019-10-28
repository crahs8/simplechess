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

    private boolean sliderAddMove(int r1, int c1, int r2, int c2, List<Move> moves) {
        if (board.getPiece(r2, c2) == Piece.EMPTY) addMove(r1, c1, r2, c2, moves);
        else if (board.getPiece(r1, r2).getColor() == board.getToMove()) return false;
        else {
            addMove(r1, c1, r2, c2, moves);
            return false;
        }
        return true;
    }

    /**
     * Generates and adds all pawn moves from a given square to a list of moves.
     *
     * @param r The row of the square.
     * @param c The column of the square.
     * @param moves The list of moves to add to.
     */
    private void generatePawnMoves(int r, int c, List<Move> moves) {
        int secondRow, forward, forward2;
        if(board.getToMove() == Color.WHITE) {
            secondRow = 6;
            forward = r - 1;
            forward2 = r - 2;
        } else {
            secondRow = 1;
            forward = r + 1;
            forward2 = r + 2;
        }

        // 1 forward
        if (board.squareHasPiece(forward, c, Piece.EMPTY))
            addMove(r, c, forward, c, moves);
        // 2 forward
        if (r == secondRow && board.squareHasPiece(forward, c, Piece.EMPTY)
                           && board.squareHasPiece(forward2, c, Piece.EMPTY))
            addMove(r, c, forward2, c, moves);
        // Diagonal left
        if (c > 0 && board.getPiece(forward, c - 1).getColor() == board.getToMove().swap())
            addMove(r, c, forward, c - 1, moves);
        // Diagonal right
        if (c < 7 && board.getPiece(forward, c + 1).getColor() == board.getToMove().swap())
            addMove(r, c, forward, c + 1, moves);
    }

    /**
     * Generates and adds all knight moves from a given square to a list of moves.
     *
     * @param r The row of the square.
     * @param c The column of the square.
     * @param moves The list of moves to add to.
     */
    private void generateKnightMoves(int r, int c, List<Move> moves) {
        if (r < 7) {
            // right, up, up
            if (c < 6 && board.squareIsCapturable(r + 1, c + 2))
                addMove(r, c, r + 1, c + 2, moves);
            // right, down, down
            if (c > 1 && board.squareIsCapturable(r + 1, c - 2))
                addMove(r, c, r + 1, c - 2, moves);
            if (r < 6) {
                // right, right, up
                if (c < 7 && board.squareIsCapturable(r + 2, c + 1))
                    addMove(r, c, r + 2, c + 2, moves);
                // right, right, down
                if (c > 0 && board.squareIsCapturable(r + 2, c - 1))
                    addMove(r, c, r + 2, c - 2, moves);
            }
        }
        if (r > 0) {
            // left, up, up
            if (c < 6 && board.squareIsCapturable(r - 1, c + 2))
                addMove(r, c, r + 1, c + 2, moves);
            //  left, down, down
            if (c > 1 && board.squareIsCapturable(r - 1, c - 2))
                addMove(r, c, r + 1, c - 2, moves);
            if (r > 1) {
                // left, left, up
                if (c < 7 && board.squareIsCapturable(r - 2, c + 1))
                    addMove(r, c, r + 2, c + 2, moves);
                // left, left, down
                if (c > 0 && board.squareIsCapturable(r - 2, c - 1))
                    addMove(r, c, r + 2, c - 2, moves);
            }
        }
    }

    /**
     * Generates and adds all bishop moves from a given square to a list of moves.
     *
     * @param r The row of the square.
     * @param c The column of the square.
     * @param moves The list of moves to add to.
     */
    private void generateBishopMoves(int r, int c, List<Move> moves) {
        for (int i = r + 1, j = c + 1; i < 8 && j < 8; i++, j++) {
            if (sliderAddMove(r, c, i, j, moves)) break;
        }
        for (int i = r + 1, j = c - 1; i < 8 && j >= 0; i++, j--) {
            if (sliderAddMove(r, c, i, j, moves)) break;
        }
        for (int i = r - 1, j = c + 1; i >= 0 && j < 8; i--, j++) {
            if (sliderAddMove(r, c, i, j, moves)) break;
        }
        for (int i = r - 1, j = c - 1; i >= 0 && j >= 0; i--, j--) {
            if (sliderAddMove(r, c, i, j, moves)) break;
        }
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
                    case EMPTY:
                        break;
                    case PAWN:
                        generatePawnMoves(i, j, moves);
                        break;
                    case KNIGHT:
                        generateKnightMoves(i, j, moves);
                        break;
                    case BISHOP:
                        generateBishopMoves(i, j, moves);
                }
            }
        }

        return moves;
    }
}
