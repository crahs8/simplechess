import java.util.*;

/**
 * Generates legal moves in the board position.
 */
public class MoveGenerator {
    private Board board;

    private static final Piece.Type[] pawnPromotions = {Piece.Type.KNIGHT, Piece.Type.BISHOP, Piece.Type.ROOK, Piece.Type.QUEEN};

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
                generatePieceMoves(i, j, p, moves);
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
        Piece pawn   = new Piece(Piece.Type.PAWN, color);
        Piece knight = new Piece(Piece.Type.KNIGHT, color);
        Piece bishop = new Piece(Piece.Type.BISHOP, color);
        Piece rook   = new Piece(Piece.Type.ROOK, color);
        Piece queen  = new Piece(Piece.Type.QUEEN, color);
        Piece[] pieces = {pawn, knight, bishop, rook, queen};

        List<List<Move>> allAttacks = new ArrayList<>();
        for (Piece piece : pieces) {
            List<Move> pieceMoves = new ArrayList<>();
            generatePieceMoves(r, c, piece, pieceMoves);
            allAttacks.add(pieceMoves);
        }

        for (int i = 0; i < pieces.length; i++) {
            for (Move m : allAttacks.get(i)) {
                Piece dest = board.getPiece(m.getR2(), m.getC2());
                if (dest.getType() == pieces[i].getType() && dest.getColor().swap() == pieces[i].getColor())
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
     * Generates and adds to a list with all moves of a given piece from a given square.
     *
     * @param r         The row of the square.
     * @param c         The column of the square.
     * @param piece     The piece.
     * @param moves     The list of moves to add to.
     */
    private void generatePieceMoves(int r, int c, Piece piece, List<Move> moves) {
        switch (piece.getType()) {
            case PAWN:
                generatePawnMoves(r, c, piece, moves);
                break;
            case KNIGHT:
                generateKnightMoves(r, c, piece, moves);
                break;
            case BISHOP:
                generateBishopMoves(r, c, piece, moves);
                break;
            case ROOK:
                generateRookMoves(r, c, piece, moves);
                break;
            case QUEEN:
                generateQueenMoves(r, c, piece, moves);
                break;
            case KING:
                generateKingMoves(r, c, piece, moves);
                break;
            default:
                throw new IllegalArgumentException(piece + " is not a valid piece.");
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
     * Adds all non-sliding moves to a list of moves from matching arrays
     * indicating steps along the row and column with a given piece.
     *
     * @param r         The origin square's row.
     * @param c         The origin square's column.
     * @param rowSteps  The step size along the row.
     * @param colSteps  The step size along the column.
     * @param piece     The piece.
     * @param moves     THe list of moves to add to.
     */
    private void addMoves(int r, int c, int[] rowSteps, int[] colSteps, Piece piece, List<Move> moves) {
        for (int i = 0; i < rowSteps.length; i++) {
            int row = r + rowSteps[i];
            int col = c + colSteps[i];
            if (squareOnBoard(row, col) && board.squareCapturableBy(row, col, piece.getColor()))
                moves.add(new RegularMove(r, c, row, col, piece));
        }
    }

    /**
     * Adds all sliding moves to a list of moves from matching arrays
     * indicating directions along the row and column with a given piece.
     *
     * @param r         The origin square's row.
     * @param c         The origin square's column.
     * @param rowDir    The direction along the row.
     * @param colDir    The direction along the column.
     * @param piece     THe piece.
     * @param moves     THe list of moves to add to.
     */
    private void addMovesSliders(int r, int c, int[] rowDir, int[] colDir, Piece piece, List<Move> moves) {
        for (int i = 0; i < rowDir.length; i++) {
            for (int j = r + rowDir[i], k = c + colDir[i]; squareOnBoard(j, k); j += rowDir[i], k += colDir[i]) {
                Piece destPiece = board.getPiece(j, k);
                if (destPiece == Piece.EMPTY) moves.add(new RegularMove(r, c, j, k, piece));
                else if (destPiece.getColor() == piece.getColor()) break;
                else {
                    moves.add(new RegularMove(r, c, j, k, piece));
                    break;
                }
            }
        }
    }

    /**
     * Generates and adds to a list with all pawn moves from a given square with a given piece.
     *
     * @param r         The row of the square.
     * @param c         The column of the square.
     * @param piece     The piece.
     * @param moves     The list of moves to add to.
     */
    private void generatePawnMoves(int r, int c, Piece piece, List<Move> moves) {
        int secondRow, lastRow, forward, forward2;
        if(piece.getColor() == Color.WHITE) {
            forward = r - 1;
            forward2 = r - 2;
        } else {
            forward = r + 1;
            forward2 = r + 2;
        }
        secondRow = Board.getRow(1, piece.getColor());
        lastRow = Board.getRow(7, piece.getColor());

        // 1 forward
        if (board.squareHasPiece(forward, c, Piece.EMPTY)) {
            if(forward == lastRow)
                for (Piece.Type t : pawnPromotions) moves.add(new PromotionMove(r, c, forward, c, piece, t));
            else moves.add(new RegularMove(r, c, forward, c, piece));
        }
        // 2 forward
        if (r == secondRow && board.squareHasPiece(forward, c, Piece.EMPTY)
                && board.squareHasPiece(forward2, c, Piece.EMPTY))
            moves.add(new RegularMove(r, c, forward2, c, piece));
        // Diagonal left
        if (c > 0 && board.getPiece(forward, c - 1).getColor() == piece.getColor().swap()) {
            if(forward == lastRow)
                for (Piece.Type t : pawnPromotions) moves.add(new PromotionMove(r, c, forward, c - 1, piece, t));
            else moves.add(new RegularMove(r, c, forward, c - 1, piece));
        }
        // Diagonal right
        if (c < 7 && board.getPiece(forward, c + 1).getColor() == piece.getColor().swap()) {
            if(forward == lastRow)
                for (Piece.Type t : pawnPromotions) moves.add(new PromotionMove(r, c, forward, c + 1, piece, t));
            else moves.add(new RegularMove(r, c, forward, c + 1, piece));
        }
        // En passant
        Move lastMove = board.getLastMove();
        if (lastMove != null
                && lastMove.getPiece().getType() == Piece.Type.PAWN
                && lastMove.getR1() == Board.getRow(6, piece.getColor())
                && lastMove.getR2() == r) {
            // left
            if (lastMove.getC2() == c - 1) moves.add(new EnPassantMove(c, c-1, piece));
            //right
            else if (lastMove.getC2() == c + 1) moves.add(new EnPassantMove(c, c+1, piece));
        }
    }

    /**
     * Generates and adds to a list with all knight moves from a given square with a given piece.
     *
     * @param r         The row of the square.
     * @param c         The column of the square.
     * @param piece     The piece.
     * @param moves     The list of moves to add to.
     */
    private void generateKnightMoves(int r, int c, Piece piece, List<Move> moves) {
        int[] rowSteps = {1, 1, 2, 2, -1, -1, -2, -2};
        int[] colSteps = {2, -2, 1, -1, 2, -2, 1, -1};

        addMoves(r, c, rowSteps, colSteps, piece, moves);
    }

    /**
     * Generates and adds to a list with all bishop moves from a given square with a given piece.
     *
     * @param r         The row of the square.
     * @param c         The column of the square.
     * @param piece     The piece.
     * @param moves     The list of moves to add to.
     */
    private void generateBishopMoves(int r, int c, Piece piece , List<Move> moves) {
        int[] rowDir = {1, 1, -1, -1};
        int[] colDir = {1, -1, 1, -1};

        addMovesSliders(r, c, rowDir, colDir, piece, moves);
    }

    /**
     * Generates and adds to a list with all rook moves from a given square with a given piece.
     *
     * @param r         The row of the square.
     * @param c         The column of the square.
     * @param piece     The piece.
     * @param moves     The list of moves to add to.
     */
    private void generateRookMoves(int r, int c, Piece piece, List<Move> moves) {
        int[] rowDir = {1, -1, 0, 0};
        int[] colDir = {0, 0, 1, -1};

        addMovesSliders(r, c, rowDir, colDir, piece, moves);
    }

    /**
     * Generates and adds to a list with all queen moves from a given square with a given piece.
     *
     * @param r         The row of the square.
     * @param c         The column of the square.
     * @param piece     The piece.
     * @param moves     The list of moves to add to.
     */
    private void generateQueenMoves(int r, int c, Piece piece, List<Move> moves) {
        generateBishopMoves(r, c, piece, moves);
        generateRookMoves(r, c, piece, moves);
    }

    /**
     * Generates and adds to a list with all king moves from a given square with a given piece.
     *
     * @param r         The row of the square.
     * @param c         The column of the square.
     * @param piece     The piece.
     * @param moves     The list of moves to add to.
     */
    private void generateKingMoves(int r, int c, Piece piece, List<Move> moves) {
        int[] rowSteps = {1, -1, 0, 0, 1, 1, -1, -1};
        int[] colSteps = {0, 0, 1, -1, 1, -1, 1, -1};

        addMoves(r, c, rowSteps, colSteps, piece, moves);

        // castling (assumes (r, c) square actually contains the king)
        // kingside
        if (board.getKingCastlingRights(piece.getColor())   &&  // rook or king wasn't moved
                !squareAttacked(r, 4, piece.getColor())     &&  // king is not in check
                !squareAttacked(r, 5, piece.getColor())     &&  // square between origin and destination not attacked
                board.squareHasPiece(r, 5, Piece.EMPTY)     &&
                board.squareHasPiece(r, 6, Piece.EMPTY))        // squares between king and rook empty
            moves.add(new CastlingMove(6, piece));
        // queenside
        if (board.getQueenCastlingRights(piece.getColor())  &&  // rook or king wasn't moved
                !squareAttacked(r, 4, piece.getColor())     &&  // king is not in check
                !squareAttacked(r, 3, piece.getColor())     &&  // square between origin and destination not attacked
                board.squareHasPiece(r, 3, Piece.EMPTY)     &&
                board.squareHasPiece(r, 2, Piece.EMPTY)     &&
                board.squareHasPiece(r, 1, Piece.EMPTY))        // squares between king and rook empty
            moves.add(new CastlingMove(2, piece));
    }
}
