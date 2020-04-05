import java.util.*;

/**
 * Represents a chess board.
 */
public class Board {
    private static final Piece[][] DEFAULT_POSITION = parseSetup(new char[][]{
            {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
            {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
            {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
    });

    // The position of the pieces on the board.
    private final Piece[][] position;
    private final MoveGenerator moveGen;
    private final Stack<Move> moveHistory;
    // Hashes of all previous positions.
    private final Set<Integer> previousPositions;
    private CastlingRights castlingRights;
    private Color toMove;
    private int fiftyMoveClock;
    private int moveNumber;

    /**
     * Constructs a Board object of the normal chess stating position.
     */
    public Board() {
        this(DEFAULT_POSITION, Color.WHITE, null, new CastlingRights(), 0, 1);
    }

    /**
     * Constructs a Board object from a given Piece array position,
     * player to move, optional last move and given castling rights.
     *
     * @param position          The position.
     * @param toMove            The player to move.
     * @param lastMove          Optional last move, can be null if no move is provided.
     * @param castlingRights    The castling rights.
     */
    public Board(Piece[][] position, Color toMove, Move lastMove, CastlingRights castlingRights, int fiftyMoveClock, int moveNumber) {
        this.position = position;
        moveGen = new MoveGenerator(this);
        moveHistory = new Stack<>();
        previousPositions = new HashSet<>();
        previousPositions.add(Arrays.hashCode(position));
        this.toMove = toMove;
        if (lastMove != null) moveHistory.push(lastMove);
        this.castlingRights = castlingRights;
        this.fiftyMoveClock = fiftyMoveClock;
        this.moveNumber = moveNumber;
    }

    /**
     * Converts a relative row corresponding to a given player to an absolute row.
     *
     * @param r The relative row.
     * @param c The color.
     * @return the absolute row corresponding to the relative row.
     */
    public static int getRow(int r, Color c) {
        if (c == Color.WHITE) {
            return 7 - r;
        }
        return  r;
    }

    /**
     * Converts a 2d array of chars to a 2d array of Piece-objects.
     *
     * @param setup The 2d char array.
     * @return A 2d array of Piece objects.
     */
    private static Piece[][] parseSetup(char[][] setup) {
        Piece[][] pieces = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pieces[i][j] = Piece.fromChar(setup[i][j]);
            }
        }
        return pieces;
    }

    /**
     * Get all legal moves in the position.
     *
     * @return all legal moves in the position.
     */
    public List<Move> getLegalMoves() {
        return moveGen.generateMoves();
    }

    public Move getLastMove() {
        return moveHistory.empty() ? null : moveHistory.peek();
    }

    public Color getToMove() {
        return toMove;
    }

    public CastlingRights getCastlingRights() {
        return castlingRights;
    }

    public CastlingRights getCastlingRightsClone() {
        try {
            return (CastlingRights) castlingRights.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Cloning not properly implemented.");
        }
    }

    public int getFiftyMoveClock() {
        return fiftyMoveClock;
    }

    /**
     * Applies a Move to the board.
     *
     * @param m The move to apply.
     */
    public void makeMove(Move m) {
        previousPositions.add(Arrays.hashCode(position));
        if (m instanceof RegularMove) makeMove((RegularMove) m);
        else if (m instanceof CastlingMove) makeMove((CastlingMove) m);
        else if (m instanceof PromotionMove) makeMove((PromotionMove) m);
        else if (m instanceof EnPassantMove) makeMove((EnPassantMove) m);
        else throw new UnsupportedOperationException("Move type " + m.getClass().getSimpleName() + " not implemented.");
        moveHistory.push(m);
        toMove = toMove.swap();
        if (toMove == Color.WHITE) moveNumber++;
    }

    /**
     * Applies a RegularMove to the board.
     *
     * @param m The move to apply.
     */
    private void makeMove(RegularMove m) {
        m.setDestinationPiece(position[m.getR2()][m.getC2()]);
        position[m.getR2()][m.getC2()] = position[m.getR1()][m.getC1()];
        position[m.getR1()][m.getC1()] = Piece.EMPTY;
        // remove castling rights if necessary
        if (m.getPiece().getType() == Piece.Type.KING) {
            castlingRights.removeKingside(m.getPiece().getColor());
            castlingRights.removeQueenside(m.getPiece().getColor());
        } else if (m.getPiece().getType() == Piece.Type.ROOK) {
            if (m.getC1() == 7) castlingRights.removeKingside(m.getPiece().getColor());
            else if (m.getC1() == 0) castlingRights.removeQueenside(m.getPiece().getColor());
        }
        if (m.getR2() == Board.getRow(7, m.getPiece().getColor())) {
            if (m.getC2() == 7) castlingRights.removeKingside(m.getPiece().getColor().swap());
            else if (m.getC2() == 0) castlingRights.removeQueenside(m.getPiece().getColor().swap());
        }
        // increment or reset fifty move rule clock
        if (m.getPiece().getType() == Piece.Type.PAWN || m.getDestinationPiece() != Piece.EMPTY)
            fiftyMoveClock = 0;
        else fiftyMoveClock ++;
    }

    /**
     * Applies a CastlingMove to the board.
     *
     * @param m The move to apply.
     */
    private void makeMove(CastlingMove m) {
        position[m.getR2()][m.getC2()] = position[m.getR1()][m.getC1()];
        position[m.getR1()][m.getC1()] = Piece.EMPTY;
        if (m.getC2() == 6) { // kingside
            position[m.getR2()][5] = position[m.getR2()][7];
            position[m.getR2()][7] = Piece.EMPTY;
        } else {
            position[m.getR2()][3] = position[m.getR2()][0];
            position[m.getR2()][0] = Piece.EMPTY;
        }
        castlingRights.removeKingside(m.getPiece().getColor());
        castlingRights.removeQueenside(m.getPiece().getColor());
        fiftyMoveClock++;
    }

    /**
     * Applies a PromotionMove to the board.
     *
     * @param m The move to apply.
     */
    private void makeMove(PromotionMove m) {
        m.setDestinationPiece(position[m.getR2()][m.getC2()]);
        position[m.getR2()][m.getC2()] = new Piece(m.getPromotion(), m.getPiece().getColor());
        position[m.getR1()][m.getC1()] = Piece.EMPTY;
        // remove castling rights if necessary
        if (m.getR2() == Board.getRow(7, m.getPiece().getColor())) {
            if (m.getC2() == 7) castlingRights.removeKingside(m.getPiece().getColor().swap());
            else if (m.getC2() == 0) castlingRights.removeQueenside(m.getPiece().getColor().swap());
        }
        fiftyMoveClock = 0;
    }

    /**
     * Applies a EnPassantMove to the board.
     *
     * @param m The move to apply.
     */
    private void makeMove(EnPassantMove m) {
        int captureRow = Board.getRow(4, m.getPiece().getColor());
        m.setDestinationPiece(position[captureRow][m.getC2()]);
        position[m.getR2()][m.getC2()] = position[m.getR1()][m.getC1()];
        position[m.getR1()][m.getC1()] = Piece.EMPTY;
        position[captureRow][m.getC2()] = Piece.EMPTY;
        fiftyMoveClock = 0;
    }

    /**
     * Undoes the last move.
     */
    public void unmakeMove() {
        Move m = moveHistory.pop();
        position[m.getR1()][m.getC1()] = m.getPiece();

        if (m instanceof RegularMove || m instanceof PromotionMove)
            position[m.getR2()][m.getC2()] = m.getDestinationPiece();
        else if (m instanceof CastlingMove) {
            position[m.getR2()][m.getC2()] = Piece.EMPTY;
            if (m.getC2() == 6) {   // kingside
                position[m.getR2()][7] = position[m.getR2()][5];
                position[m.getR2()][5] = Piece.EMPTY;
            } else {                // queenside
                position[m.getR2()][0] = position[m.getR2()][3];
                position[m.getR2()][3] = Piece.EMPTY;
            }
        }
        else if (m instanceof EnPassantMove) {
            position[m.getR2()][m.getC2()] = Piece.EMPTY;
            position[Board.getRow(4, m.getPiece().getColor())][m.getC2()] = m.getDestinationPiece();
        }
        else throw new UnsupportedOperationException("Move type " + m.getClass().getSimpleName() + " not implemented.");

        try {
            castlingRights = (CastlingRights) m.getCastlingRights().clone();
        } catch (CloneNotSupportedException ignored) { }
        fiftyMoveClock = m.getFiftyMoveClock();
        toMove = toMove.swap();
        if (toMove == Color.BLACK) moveNumber--;
        previousPositions.remove(Arrays.hashCode(position));
    }

    /**
     * Returns the piece at a given square.
     *
     * @param r The row of the square.
     * @param c The column of the square.
     * @return the piece at the given square.
     */
    public Piece getPiece(int r, int c) {
        return position[r][c];
    }

    /**
     * Returns whether a given square has a given piece.
     *
     * @param r The row of the square.
     * @param c The column of the square.
     * @param p The piece to check for.
     * @return whether the piece is on the square.
     */
    public boolean squareHasPiece(int r, int c, Piece p) {
        return position[r][c].equals(p);
    }

    /**
     * Returns whether a given square is empty or the opponent of a given color.
     *
     * @param r     The row of the square.
     * @param c     The column of the square.
     * @param color The color that captures.
     * @return whether the square is capturable for the color.
     */
    public boolean squareCapturableBy(int r, int c, Color color) {
        return position[r][c].getColor() != color;
    }

    /**
     * Returns whether the player to move is in check.
     *
     * @return whether the player to move is in check.
     */
    public boolean isCheck() {
        return moveGen.isCheck(toMove);
    }

    /**
     * Returns whether the current position has been repeated before.
     *
     * @return whether the current position has been repeated before.
     */
    public boolean positionRepeated() {
        return previousPositions.contains(Arrays.hashCode(position));
    }

    /**
     * Returns a string representation of the board state. Used for testing purposes.
     *
     * @return string representation of the board.
     */
    @Override
    public String toString() {
        StringBuilder boardString = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            boardString.append("   --- --- --- --- --- --- --- --- \n");
            boardString.append(8 - i).append(" | ");
            for (int j = 0; j < 8; j++) {
                boardString.append(position[i][j]).append(" | ");
            }
            boardString.append("\n");
        }
        boardString.append("   --- --- --- --- --- --- --- --- \n");
        boardString.append("    A   B   C   D   E   F   G   H  \n");
        return boardString.toString();
    }

    public static class CastlingRights implements Cloneable {
        private boolean wKingCastlingRights;
        private boolean wQueenCastlingRights;
        private boolean bKingCastlingRights;
        private boolean bQueenCastlingRights;

        public CastlingRights() {
            wKingCastlingRights = true;
            wQueenCastlingRights = true;
            bKingCastlingRights = true;
            bQueenCastlingRights = true;
        }

        public CastlingRights(boolean wK, boolean wQ, boolean bK, boolean bQ) {
            wKingCastlingRights = wK;
            wQueenCastlingRights = wQ;
            bKingCastlingRights = bK;
            bQueenCastlingRights = bQ;
        }

        /**
         * Returns whether a given player has kingside castling rights.
         *
         * @param c The color of the player.
         * @return whether the given player has kingside castling rights.
         */
        public boolean getKingside(Color c) {
            if (c == Color.WHITE) return wKingCastlingRights;
            else if (c == Color.BLACK) return bKingCastlingRights;
            else throw new IllegalArgumentException("Illegal color: " + c);
        }

        /**
         * Returns whether a given player has queenside castling rights.
         *
         * @param c The color of the player.
         * @return whether the given player has queenside castling rights.
         */
        public Boolean getQueenside(Color c) {
            if (c == Color.WHITE) return wQueenCastlingRights;
            else if (c == Color.BLACK) return bQueenCastlingRights;
            else throw new IllegalArgumentException("Illegal color: " + c);
        }

        private void removeKingside(Color c) {
            if (c == Color.WHITE) wKingCastlingRights = false;
            else if (c == Color.BLACK) bKingCastlingRights = false;
            else throw new IllegalArgumentException("Illegal color: " + c);
        }

        private void removeQueenside(Color c) {
            if (c == Color.WHITE) wQueenCastlingRights = false;
            else if (c == Color.BLACK) bQueenCastlingRights = false;
            else throw new IllegalArgumentException("Illegal color: " + c);
        }

        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}
