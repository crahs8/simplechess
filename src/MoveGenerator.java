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
     * Generates a List of all the legal moves in the current position.
     *
     * @return all the legal moves in the position.
     */
    public List<Move> generateMoves() {
        List<Move> moves = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(board.getPiece(i, j).getColor() != board.getToMove()) continue;
                switch (board.getPiece(i, j).getType()) {
                    case PAWN:
                        generatePawnMoves(i, j, moves);
                        break;
                    case KNIGHT:
                        generateKnightMoves(i, j, moves);
                        break;
                    case BISHOP:
                        generateBishopMoves(i, j, moves);
                        break;
                    case ROOK:
                        generateRookMoves(i, j, moves);
                        break;
                    case QUEEN:
                        generateQueenMoves(i, j, moves);
                        break;
                    case KING:
                        generateKingMoves(i, j, moves);
                        break;
                }
            }
        }

        return moves;
    }

    /**
     * Returns whether a given square is attacked by the opponent of a given player.
     * @param r     The row of the square to check.
     * @param c     The column of the square to check.
     * @param color The color of the attacked player.
     * @return whether a square is attacked.
     */
    private boolean squareAttacked(int r, int c, Color color) {
        Piece pawn = new Piece(Piece.Type.PAWN, color.swap());
        Piece knight = new Piece(Piece.Type.PAWN, color.swap());
        Piece bishop = new Piece(Piece.Type.PAWN, color.swap());
        Piece rook = new Piece(Piece.Type.PAWN, color.swap());
        Piece queen = new Piece(Piece.Type.PAWN, color.swap());
        Piece[] pieces = {pawn, knight, bishop, rook, queen};

        List<List<Move>> allAttacks = new ArrayList<>(); // Using list cause Java gets cranky with generic arrays.
        allAttacks.add(generatePawnMoves(r, c, color));
        allAttacks.add(generateKnightMoves(r, c, color));
        allAttacks.add(generateBishopMoves(r, c, color));
        allAttacks.add(generateRookMoves(r, c, color));
        allAttacks.add(generateQueenMoves(r, c, color));

        for (int i = 0; i < pieces.length; i++) {
            for (Move m : allAttacks.get(i)) {
                if (board.squareHasPiece(m.getR2(), m.getC2(), pieces[i]))
                    return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the king of a given color is in check.
     *
     * @param color The color of the king.
     * @return whether the king is check.
     */
    private boolean isCheck(Color color) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board.squareHasPiece(r, c, new Piece(Piece.Type.KING, color)))
                    return squareAttacked(r, c, color);
            }
        }
        throw new RuntimeException("Somehow didn't find a king...");
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
        if (!isCheck(board.getToMove())) moves.add(move);
        board.unmakeMove();
    }

    private boolean sliderAddMove(int r1, int c1, int r2, int c2, List<Move> moves, Color toPlay) {
        if (board.getPiece(r2, c2) == Piece.EMPTY) moves.add(new Move(r1, c1, r2, c2));
        else if (board.getPiece(r1, r2).getColor() == toPlay) return false;
        else {
            moves.add(new Move(r1, c1, r2, c2));
            return false;
        }
        return true;
    }

    /**
     * Generates and returns a list with all pawn moves from a given square by a given player.
     *
     * @param r         The row of the square.
     * @param c         The column of the square.
     * @param toPlay    The color of the player.
     * @return a list of pawn moves from the square by the player.
     */
    private List<Move> generatePawnMoves(int r, int c, Color toPlay) {
        int secondRow, forward, forward2;
        if(toPlay == Color.WHITE) {
            secondRow = 6;
            forward = r - 1;
            forward2 = r - 2;
        } else {
            secondRow = 1;
            forward = r + 1;
            forward2 = r + 2;
        }

        List<Move> moves = new ArrayList<>();

        // 1 forward
        if (board.squareHasPiece(forward, c, Piece.EMPTY))

        // 2 forward
        if (r == secondRow && board.squareHasPiece(forward, c, Piece.EMPTY)
                && board.squareHasPiece(forward2, c, Piece.EMPTY))
            moves.add(new Move(r, c, forward2, c));
        // Diagonal left
        if (c > 0 && board.getPiece(forward, c - 1).getColor() == toPlay.swap())
            moves.add(new Move(r, c, forward, c - 1));
        // Diagonal right
        if (c < 7 && board.getPiece(forward, c + 1).getColor() == toPlay.swap())
            moves.add(new Move(r, c, forward, c + 1));

        return moves;
    }

    /**
     * Generates and returns a list with all knight moves from a given square by a given player.
     *
     * @param r         The row of the square.
     * @param c         The column of the square.
     * @param toPlay    The color of the player.
     * @return a list of pawn moves from the square by the player.
     */
    private List<Move> generateKnightMoves(int r, int c, Color toPlay) {
        List<Move> moves = new ArrayList<>();

        if (r < 7) {
            // down, right, right
            if (c < 6 && board.squareCapturableBy(r + 1, c + 2, toPlay))
                moves.add(new Move(r, c, r + 1, c + 2));
            // down, left, left
            if (c > 1 && board.squareCapturableBy(r + 1, c - 2, toPlay))
                moves.add(new Move(r, c, r + 1, c - 2));
            if (r < 6) {
                // down, down, right
                if (c < 7 && board.squareCapturableBy(r + 2, c + 1, toPlay))
                    moves.add(new Move(r, c, r + 2, c + 1));
                // down, down, left
                if (c > 0 && board.squareCapturableBy(r + 2, c - 1, toPlay))
                    moves.add(new Move(r, c, r + 2, c - 1));
            }
        }
        if (r > 0) {
            // up, right, right
            if (c < 6 && board.squareCapturableBy(r - 1, c + 2, toPlay))
                moves.add(new Move(r, c, r - 1, c + 2));
            //  up, left, left
            if (c > 1 && board.squareCapturableBy(r - 1, c - 2, toPlay))
                moves.add(new Move(r, c, r - 1, c - 2));
            if (r > 1) {
                // up, up, right
                if (c < 7 && board.squareCapturableBy(r - 2, c + 1, toPlay))
                    moves.add(new Move(r, c, r - 2, c + 1));
                // up, up, left
                if (c > 0 && board.squareCapturableBy(r - 2, c - 1, toPlay))
                    moves.add(new Move(r, c, r - 2, c - 1));
            }
        }

        return moves;
    }

    /**
     * Generates and returns a list with all bishop moves from a given square by a given player.
     *
     * @param r         The row of the square.
     * @param c         The column of the square.
     * @param toPlay    The color of the player.
     * @return a list of pawn moves from the square by the player.
     */
    private List<Move> generateBishopMoves(int r, int c, Color toPlay) {
        List<Move> moves = new ArrayList<>();
        
        // down, right
        for (int i = r + 1, j = c + 1; i < 8 && j < 8; i++, j++)
            if (!sliderAddMove(r, c, i, j, moves, toPlay)) break;
        // down, left
        for (int i = r + 1, j = c - 1; i < 8 && j >= 0; i++, j--)
            if (!sliderAddMove(r, c, i, j, moves, toPlay)) break;
        // up, right
        for (int i = r - 1, j = c + 1; i >= 0 && j < 8; i--, j++)
            if (!sliderAddMove(r, c, i, j, moves, toPlay)) break;
        // up, left
        for (int i = r - 1, j = c - 1; i >= 0 && j >= 0; i--, j--)
            if (!sliderAddMove(r, c, i, j, moves, toPlay)) break;

        return moves;
    }

    /**
     * Generates and returns a list with all rook moves from a given square by a given player.
     *
     * @param r         The row of the square.
     * @param c         The column of the square.
     * @param toPlay    The color of the player.
     * @return a list of pawn moves from the square by the player.
     */
    private List<Move> generateRookMoves(int r, int c, Color toPlay) {
        List<Move> moves = new ArrayList<>();

        // down
        for (int i = r + 1; i < 8; i++)
            if (!sliderAddMove(r, c, i, c, moves, toPlay)) break;
        // up
        for (int i = r - 1; i >= 0; i--)
            if (!sliderAddMove(r, c, i, c, moves, toPlay)) break;
        // right
        for (int i = c + 1; i < 8; i++)
            if (!sliderAddMove(r, c, i, c, moves, toPlay)) break;
        // left
        for (int i = c - 1; i >= 0; i--)
            if (!sliderAddMove(r, c, i, c, moves, toPlay)) break;

        return moves;
    }

    /**
     * Generates and returns a list with all queen moves from a given square by a given player.
     *
     * @param r         The row of the square.
     * @param c         The column of the square.
     * @param toPlay    The color of the player.
     * @return a list of pawn moves from the square by the player.
     */
    private List<Move> generateQueenMoves(int r, int c, Color toPlay) {
        List<Move> moves = generateBishopMoves(r, c, toPlay);
        moves.addAll(generateRookMoves(r, c, toPlay));

        return moves;
    }

    /**
     * Generates and returns a list with all king moves from a given square by a given player.
     *
     * @param r         The row of the square.
     * @param c         The column of the square.
     * @param toPlay    The color of the player.
     * @return a list of pawn moves from the square by the player.
     */
    private List<Move> generateKingMoves(int r, int c, Color toPlay) {
        List<Move> moves = new ArrayList<>();

        // down
        if (r < 7 && board.squareCapturableBy(r + 1, c, toPlay))
            addMove(r, c, r + 1, c, moves);
        // up
        if (r > 0 && board.squareCapturableBy(r - 1, c, toPlay))
            addMove(r, c, r - 1, c, moves);
        // right
        if (c < 7 && board.squareCapturableBy(r, c + 1, toPlay))
            addMove(r, c, r, c + 1, moves);
        // left
        if (c > 0 && board.squareCapturableBy(r, c - 1, toPlay))
            addMove(r, c, r, c - 1, moves);
        // down, right
        if (r < 7 && c < 7 && board.squareCapturableBy(r + 1, c + 1, toPlay))
            addMove(r, c, r + 1, c + 1, moves);
        // down, left
        if (r < 7 && c > 0 && board.squareCapturableBy(r + 1, c - 1, toPlay))
            addMove(r, c, r + 1, c - 1, moves);
        // up, right
        if (r > 0 && c < 7 && board.squareCapturableBy(r - 1, c + 1, toPlay))
            addMove(r, c, r - 1, c + 1, moves);
        // up, left
        if (r > 0 && c > 0 && board.squareCapturableBy(r - 1, c - 1, toPlay))
            addMove(r, c, r - 1, c - 1, moves);

        return moves;
    }
}
