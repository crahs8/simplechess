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
    }

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

    public Color getToMove() {
        return toMove;
    }

    /**
     * Applies a move to the board.
     *
     * @param m The move to apply.
     */
    public void makeMove(Move m) {
        m.setDestinationPiece(position[m.getR2()][m.getC2()]);
        position[m.getR2()][m.getC2()] = position[m.getR1()][m.getC1()];
        position[m.getR1()][m.getC1()] = Piece.EMPTY;
        moveHistory.push(m);
        toMove = toMove.swap();   // flips to the other player
    }

    /**
     * Undoes the last move.
     */
    public void unmakeMove() {
        Move m = moveHistory.pop();
        position[m.getR1()][m.getC1()] = position[m.getR2()][m.getC2()];
        position[m.getR2()][m.getC2()] = m.getDestinationPiece();
        toMove = toMove.swap();   // flips to the other player
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
        return position[r][c] == p;
    }

    /**
     * Returns whether a given square is empty or the opposite color of the player to move.
     *
     * @param r The row of the square.
     * @param c The column of the square.
     * @return whether the square is capturable.
     */
    public boolean squareIsCapturable(int r, int c) {
        return position[r][c].getColor() != toMove;
    }

    /**
     * Returns a string representation of the board state. Used for testing purposes.
     *
     * @return string representation of the board.
     */
    public String toString() {
        String boardString = "";
        for (int i = 0; i < 8; i++) {
            boardString += "   --- --- --- --- --- --- --- --- \n";
            boardString += (8 - i) + " | ";
            for (int j = 0; j < 8; j++) {
                boardString += position[i][j] + " | ";
            }
            boardString += "\n";
        }
        boardString += "   --- --- --- --- --- --- --- --- \n";
        boardString += "    A   B   C   D   E   F   G   H  \n";
        return boardString;
    }
}
