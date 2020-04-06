import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Command line interface for playing with the computer.
 */
public class CLI {
    private static final int SEARCH_DEPTH = 4;

    private Board board;
    private Search search;
    private BufferedReader reader;
    private Color playerColor;

    /**
     * Constructs a CLI object based on a given board.
     *
     * @param board the board.
     * @throws IOException if readLine fails.
     */
    public CLI(Board board) throws IOException {
        this.board = board;
        search = new Search(board);
        reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("Which color do you want to play? (w/b)");
            String ans = reader.readLine();
            if (ans.equals("w")) {
                playerColor = Color.WHITE;
                break;
            } else if (ans.equals("b")) {
                playerColor = Color.BLACK;
                break;
            }
        }
    }

    /**
     * Starts the CLI.
     *
     * @throws IOException if readLine fails.
     */
    public void startCLI() throws IOException {
        boolean gameNotEnded = true;
        while (gameNotEnded) {
            if (board.getToMove() == playerColor) {
                while (true) {
                    try {
                        Move m = parseMove(reader.readLine());
                        board.makeMove(m);
                        gameNotEnded = !board.gameEnded();
                        break;
                    } catch (MoveParseException e) {
                        System.out.println(e.getMessage());
                    }
                }
            } else {
                Move m = search.findBestMove(SEARCH_DEPTH);
                board.makeMove(m);
                System.out.println(m);
                gameNotEnded = !board.gameEnded();
            }
        }
        System.out.println("Game over.");
    }

    private Move parseMove(String m) throws MoveParseException {
        List<Move> legalMoves = board.getLegalMoves();
        if (m.equals("KC")) {
            return legalMoves.stream()
                    .filter(lM -> lM instanceof CastlingMove && lM.getC2() == 6)
                    .findAny().orElseThrow(MoveParseException::new);
        } else if (m.equals("QC")) {
            return legalMoves.stream()
                    .filter(lM -> lM instanceof CastlingMove && lM.getC2() == 2)
                    .findAny().orElseThrow(MoveParseException::new);
        } else if (m.length() == 4 || m.length() == 5) { // regular move, promotion move or en passant
            String origin = m.substring(0, 2);
            String destination = m.substring(2);
            try {
                int r1 = Square.rowFromChar(origin.charAt(1));
                int c1 = Square.columnFromChar(origin.charAt(0));
                int r2 = Square.rowFromChar(destination.charAt(1));
                int c2 = Square.columnFromChar(destination.charAt(0));
                if (m.length() == 4) {
                    return legalMoves.stream()
                            .filter(lM -> (lM instanceof RegularMove || lM instanceof EnPassantMove) &&
                                    lM.getR1() == r1 && lM.getC1() == c1 && lM.getR2() == r2 && lM.getC2() == c2)
                            .findAny().orElseThrow(MoveParseException::new);
                } else {
                    Piece.Type promotion = Piece.Type.fromChar(m.charAt(4));
                    return legalMoves.stream()
                            .filter(lM -> lM instanceof PromotionMove && lM.getR1() == r1 && lM.getC1() == c1 &&
                                    lM.getR2() == r2 && lM.getC2() == c2 && ((PromotionMove) lM).getPromotion() == promotion)
                            .findAny().orElseThrow(MoveParseException::new);
                }
            } catch (ParseException e) {
                throw new MoveParseException(e.getMessage());
            }
        } else throw new MoveParseException("Invalid move");
    }

    private static class MoveParseException extends ParseException {
        public MoveParseException() {
            super("Not legal");
        }
        public MoveParseException(String message) {
            super(message);
        }
    }
}
