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
        List<Move> moves = new LinkedList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = board.getPiece(i, j);
                if(p.getColor() != board.getToMove()) continue;
                generatePieceMoves(i, j, p.getType(), board.getToMove(), moves);
            }
        }

        removeIllegalMoves(moves);

        return moves;
    }

    /**
     * Returns whether a given move places the playing player's king in check.
     *
     * @param m The move to check.
     * @return whether the move places the king in check.
     */
    private boolean moveLegal(Move m) {
        boolean legal;
        board.makeMove(m);
        legal = !isCheck(board.getToMove());
        board.unmakeMove();
        return legal;
    }

    /**
     * Removes all moves that place the playing player's king in check from a given list.
     *
     * @param moves The list of moves to remove from.
     */
    private void removeIllegalMoves(List<Move> moves) {
        moves.removeIf(move -> !moveLegal(move));
    }

    /**
     * Returns whether a given square is attacked by the opponent of a given player.
     *
     * @param r     The row of the square to check.
     * @param c     The column of the square to check.
     * @param color The color of the attacked player.
     * @return whether a square is attacked.
     */
    private boolean squareAttacked(int r, int c, Color color) {
        Color opColor = color.swap();
        Piece pawn = new Piece(Piece.Type.PAWN, opColor);
        Piece knight = new Piece(Piece.Type.PAWN, opColor);
        Piece bishop = new Piece(Piece.Type.PAWN, opColor);
        Piece rook = new Piece(Piece.Type.PAWN, opColor);
        Piece queen = new Piece(Piece.Type.PAWN, opColor);
        Piece[] pieces = {pawn, knight, bishop, rook, queen};

        List<List<Move>> allAttacks = new ArrayList<>();
        for (Piece piece : pieces) {
            List<Move> pieceMoves = new ArrayList<>();
            generatePieceMoves(r, c, piece.getType(), color, pieceMoves);
            allAttacks.add(pieceMoves);
        }

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
     * Generates and adds to a list with all moves of a given piece type from a given square by a given player.
     *
     * @param r         The row of the square.
     * @param c         The column of the square.
     * @param type      The type of piece to generate for.
     * @param toPlay    The color of the player.
     * @param moves     The list of moves to add to.
     */
    private void generatePieceMoves(int r, int c, Piece.Type type, Color toPlay, List<Move> moves) {
        switch (type) {
            case PAWN:
                generatePawnMoves(r, c, toPlay, moves);
                break;
            case KNIGHT:
                generateKnightMoves(r, c, toPlay, moves);
                break;
            case BISHOP:
                generateBishopMoves(r, c, toPlay, moves);
                break;
            case ROOK:
                generateRookMoves(r, c, toPlay, moves);
                break;
            case QUEEN:
                generateQueenMoves(r, c, toPlay, moves);
                break;
            case KING:
                generateKingMoves(r, c, toPlay, moves);
                break;
            default:
                throw new IllegalArgumentException(type + " is not a valid piece type.");
        }
    }

    /**
     * Returns whether a given square is withing the limits of the board.
     *
     * @param r The row of the square.
     * @param c The column of the square.
     * @return whether the square is on the board
     */
    private boolean squareOnBoard(int r, int c) {
        return r >= 0 && r <= 7 && c >= 0 && c <= 7;
    }

    /**
     * Adds all non-sliding moves to a list of moves from matching arrays indicating steps along the row and column.
     *
     * @param r         The origin square's row.
     * @param c         The origin square's column.
     * @param rowSteps  The step size along the row.
     * @param colSteps  The step size along the column.
     * @param toPlay    The color of the player.
     * @param moves     THe list of moves to add to.
     */
    private void addMoves(int r, int c, int[] rowSteps, int[] colSteps, Color toPlay, List<Move> moves) {
        for (int i = 0; i < rowSteps.length; i++) {
            int row = r + rowSteps[i];
            int col = c + colSteps[i];
            if (squareOnBoard(row, col) && board.squareCapturableBy(row, col, toPlay))
                moves.add(new Move(r, c, row, col));
        }
    }

    /**
     * Adds all sliding moves to a list of moves from matching arrays indicating directions along the row and column.
     *
     * @param r         The origin square's row.
     * @param c         The origin square's column.
     * @param rowDir    The direction along the row.
     * @param colDir    The direction along the column.
     * @param toPlay    The color of the player.
     * @param moves     THe list of moves to add to.
     */
    private void addMovesSliders(int r, int c, int[] rowDir, int[] colDir, Color toPlay, List<Move> moves) {
        for (int i = 0; i < rowDir.length; i++) {
            for (int j = r + rowDir[i], k = c + colDir[i]; squareOnBoard(j, k); j += rowDir[i], k += colDir[i]) {
                Piece destPiece = board.getPiece(j, k);
                if (destPiece == Piece.EMPTY) moves.add(new Move(r, c, j, k));
                else if (destPiece.getColor() == toPlay) break;
                else {
                    moves.add(new Move(r, c, j, k));
                    break;
                }
            }
        }
    }

    /**
     * Generates and adds to a list with all pawn moves from a given square by a given player.
     *
     * @param r         The row of the square.
     * @param c         The column of the square.
     * @param toPlay    The color of the player.
     * @param moves     The list of moves to add to.
     */
    private void generatePawnMoves(int r, int c, Color toPlay, List<Move> moves) {
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
    }

    /**
     * Generates and adds to a list with all knight moves from a given square by a given player.
     *
     * @param r         The row of the square.
     * @param c         The column of the square.
     * @param toPlay    The color of the player.
     * @param moves     The list of moves to add to.
     */
    private void generateKnightMoves(int r, int c, Color toPlay, List<Move> moves) {
        int[] rowSteps = {1, 1, 2, 2, -1, -1, -2, -2};
        int[] colSteps = {2, -2, 1, -1, 2, -2, 1, -1};

        addMoves(r, c, rowSteps, colSteps, toPlay, moves);
    }

    /**
     * Generates and adds to a list with all bishop moves from a given square by a given player.
     *
     * @param r         The row of the square.
     * @param c         The column of the square.
     * @param toPlay    The color of the player.
     * @param moves     The list of moves to add to.
     */
    private void generateBishopMoves(int r, int c, Color toPlay , List<Move> moves) {
        int[] rowDir = {1, 1, -1, -1};
        int[] colDir = {1, -1, 1, -1};

        addMovesSliders(r, c, rowDir, colDir, toPlay, moves);
    }

    /**
     * Generates and adds to a list with all rook moves from a given square by a given player.
     *
     * @param r         The row of the square.
     * @param c         The column of the square.
     * @param toPlay    The color of the player.
     * @param moves     The list of moves to add to.
     */
    private void generateRookMoves(int r, int c, Color toPlay, List<Move> moves) {
        int[] rowDir = {1, -1, 0, 0};
        int[] colDir = {0, 0, 1, -1};

        addMovesSliders(r, c, rowDir, colDir, toPlay, moves);
    }

    /**
     * Generates and adds to a list with all queen moves from a given square by a given player.
     *
     * @param r         The row of the square.
     * @param c         The column of the square.
     * @param toPlay    The color of the player.
     * @param moves     The list of moves to add to.
     */
    private void generateQueenMoves(int r, int c, Color toPlay, List<Move> moves) {
        generateBishopMoves(r, c, toPlay, moves);
        generateRookMoves(r, c, toPlay, moves);
    }

    /**
     * Generates and adds to a list with all king moves from a given square by a given player.
     *
     * @param r         The row of the square.
     * @param c         The column of the square.
     * @param toPlay    The color of the player.
     * @param moves     The list of moves to add to.
     */
    private void generateKingMoves(int r, int c, Color toPlay, List<Move> moves) {
        int[] rowSteps = {1, -1, 0, 0, 1, 1, -1, -1};
        int[] colSteps = {0, 0, 1, -1, 1, -1, 1, -1};

        addMoves(r, c, rowSteps, colSteps, toPlay, moves);

        // kingside castle
        /*if (toPlay == Color.WHITE) {
            if ()
        }*/
    }
}
