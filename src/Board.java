import java.util.*;

/**
 * Represents a chess board.
 */
public class Board {
    // The position of the pieces on the board.
    private final Piece[][] position;
    private final MoveGenerator moveGen;
    private final Stack<Move> moveHistory;
    private Color toMove;
    private Boolean wKingCastlingRights;
    private Boolean wQueenCastlingRights;
    private Boolean bKingCastlingRights;
    private Boolean bQueenCastlingRights;

    /**
     * Constructs a Board object.
     */
    Board() {
        char[][] setup = new char[][]{
                {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
                {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
                {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
        };
        position = parseSetup(setup);
        moveGen = new MoveGenerator(this);
        moveHistory = new Stack<>();
        toMove = Color.WHITE;
        wKingCastlingRights = true;
        wQueenCastlingRights = true;
        bKingCastlingRights = true;
        bQueenCastlingRights = true;
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
    private Piece[][] parseSetup(char[][] setup) {
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

    /**
     * Returns whether a given player has kingside castling rights.
     *
     * @param c The color of the player.
     * @return whether the given player has kingside castling rights.
     */
    public Boolean getKingCastlingRights(Color c) {
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
    public Boolean getQueenCastlingRights(Color c) {
        if (c == Color.WHITE) return wQueenCastlingRights;
        else if (c == Color.BLACK) return bQueenCastlingRights;
        else throw new IllegalArgumentException("Illegal color: " + c);
    }

    private void removeKingCastlingRights(Color c) {
        if (c == Color.WHITE) wKingCastlingRights = false;
        else if (c == Color.BLACK) bKingCastlingRights = false;
        else throw new IllegalArgumentException("Illegal color: " + c);
    }

    private void removeQueenCastlingRights(Color c) {
        if (c == Color.WHITE) wQueenCastlingRights = false;
        else if (c == Color.BLACK) bQueenCastlingRights = false;
        else throw new IllegalArgumentException("Illegal color: " + c);
    }

    /**
     * Applies a Move to the board.
     *
     * @param m The move to apply.
     */
    public void makeMove(Move m) {
        if (m instanceof RegularMove) makeMove((RegularMove) m);
        else if (m instanceof CastlingMove) makeMove((CastlingMove) m);
        else if (m instanceof PromotionMove) makeMove((PromotionMove) m);
        else if (m instanceof EnPassantMove) makeMove((EnPassantMove) m);
        else throw new UnsupportedOperationException("Move type " + m.getClass().getSimpleName() + " not implemented.");
        moveHistory.push(m);
        toMove = toMove.swap();
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
        if (m.getPiece().getType() == Piece.Type.KING) {
            removeKingCastlingRights(m.getPiece().getColor());
            removeQueenCastlingRights(m.getPiece().getColor());
        } else if (m.getPiece().getType() == Piece.Type.ROOK && m.getR1() == getRow(0, m.getPiece().getColor())) {
            if (m.getC1() == 7) removeKingCastlingRights(m.getPiece().getColor());
            else if (m.getC1() == 0) removeQueenCastlingRights(m.getPiece().getColor());
        }
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
        removeKingCastlingRights(m.getPiece().getColor());
        removeQueenCastlingRights(m.getPiece().getColor());
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

        toMove = toMove.swap();
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
}
