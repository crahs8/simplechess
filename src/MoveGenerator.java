import java.util.*;

public class MoveGenerator {
    private Board board;

    public MoveGenerator(Board board) {
        this.board = board;
    }

    /**
     * Returns whether the king of a given color is in check.
     * @param color The color of the king.
     * @return whether the king is check.
     */
    private boolean isCheck(int color) {
        if(color == Board.WHITE) {
            char pawn = 'P', knight = 'N', bishop = 'B', rook = 'R', queen = 'Q', king = 'K';
        }
        else {
            char pawn = 'p', knight = 'n', bishop = 'b', rook = 'r', queen = 'q', king = 'k';
        }

        return false;
    }

    /**
     * Creates and adds a Move from a given starting square to a given
     * ending square to the given List of moves, if the move is legal.
     * @param r1 The row of the stating square.
     * @param c1 The column of the starting square.
     * @param r2 The row of the ending square.
     * @param c2 The column of the ending square.
     * @param moves The List of moves to add to.
     */
    private void addMove(int r1, int c1, int r2, int c2, List<Move> moves) {
        Move move = new Move(r1, c1, r2, c2);
        board.makeMove(move);
        if(isCheck(board.getToMove())) moves.add(move);
        board.unmakeMove();
    }

    private void generatePawnMoves(List<Move> moves) {
        char pawn; int secondRow;
        if(board.getToMove() == Board.WHITE) {
            pawn = 'P'; secondRow = 6;
        }
        else pawn = 'p'; secondRow = 1;

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(board.squareHasPiece(i, j, pawn)) {
                    // 1 forward
                    if(board.squareHasPiece(i - board.getToMove(), j, ' '))
                        addMove(i, j, i - 2 * board.getToMove(), j, moves);
                    // 2 forward
                    if(i == secondRow && board.squareHasPiece(i - board.getToMove(), j, ' ')
                            && board.squareHasPiece(i - 2 * board.getToMove(), j, ' '))
                        addMove(i, j, i - 2 * board.getToMove(), j, moves);
                    //if()
                }
            }
        }
    }

    /**
     * Generates a List of all the legal moves in the current position.
     * @return all the legal moves in the position.
     */
    public List<Move> generateMoves() {
        List<Move> moves = new ArrayList<>();

        generatePawnMoves(moves);

        return moves;
    }
}
