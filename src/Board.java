import java.util.Stack;

/**
 * Represents a chess board.
 */
public class Board {
    public static final int WHITE = 1;
    public static final int BLACK = -1;

    // The position of the pieces on the board.
    private char[][] position;
    private int toMove;
    private MoveGenerator moveGen;
    private Stack<Move> moveHistory;

    /**
     * Constructs a Board object.
     */
    Board() {
        position = new char[][] {
                {'r','n','b','q','k','b','n','r'},
                {'p','p','p','p','p','p','p','p'},
                {' ',' ',' ',' ',' ',' ',' ',' '},
                {' ',' ',' ',' ',' ',' ',' ',' '},
                {' ',' ',' ',' ',' ',' ',' ',' '},
                {' ',' ',' ',' ',' ',' ',' ',' '},
                {'P','P','P','P','P','P','P','P'},
                {'R','N','B','Q','K','B','N','R'}
        };
        toMove = WHITE;
        moveGen = new MoveGenerator(this);
        moveHistory = new Stack<>();
    }

    public int getToMove() {
        return toMove;
    }

    /**
     * Applies a move to the board.
     * @param m The move to apply.
     */
    public void makeMove(Move m) {
        m.setDestinationValue(position[m.getR2()][m.getC2()]);
        position[m.getR2()][m.getC2()] = position[m.getR1()][m.getC1()];
        position[m.getR1()][m.getC1()] = ' ';
        moveHistory.push(m);
        toMove = -toMove;   // flips to the other player
    }

    /**
     * Undoes the last move.
     */
    public void unmakeMove() {
        Move m = moveHistory.pop();
        position[m.getR1()][m.getC1()] = position[m.getR2()][m.getC2()];
        position[m.getR2()][m.getC2()] = m.getDestinationValue();
    }

    /**
     * Returns whether a given square has a given piece.
     * @param r The row of the square.
     * @param c The column of the square.
     * @param piece The piece to check for.
     * @return whether the piece is on the square.
     */
    public boolean squareHasPiece(int r, int c, char piece) {
        return position[r][c] == piece;
    }

    /**
     * Returns a string representation of the board state. Used for testing purposes.
     * @return string representation of the board.
     */
    public String toString() {
        String boardString = "";
        for(int i = 0; i < 8; i++) {
            boardString += "   --- --- --- --- --- --- --- --- \n";
            boardString += (8 - i) + " | ";
            for(int j = 0; j < 8; j++) {
                boardString += position[i][j] + " | ";
            }
            boardString += "\n";
        }
        boardString += "   --- --- --- --- --- --- --- --- \n";
        boardString += "    A   B   C   D   E   F   G   H  \n";
        return boardString;
    }
}
