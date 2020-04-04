import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    private Board[] boards = new Board[5];

    @BeforeEach
    void setUp() {
        boards[0] = new Board();
        try {
            boards[1] = FENParser.parse("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
        } catch (FENParser.FENParseException e) {
            throw new RuntimeException("Bad FEN");
        }
        try {
            boards[2] = FENParser.parse("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10");
        } catch (FENParser.FENParseException e) {
            throw new RuntimeException("Bad FEN");
        }
        try {
            boards[3] = FENParser.parse("8/8/8/2k5/2pP4/8/B7/4K3 b - d3 5 3");
        } catch (FENParser.FENParseException e) {
            throw new RuntimeException("Bad FEN");
        }
        try {
            boards[4] = FENParser.parse("4k3/3p4/2P5/3r4/K7/8/8/8 b - - 0 3");
        } catch (FENParser.FENParseException e) {
            throw new RuntimeException("Bad FEN");
        }
    }

    @Test
    void unmakeMove() {
        for (Board b : boards) {
            List<Move> moves = b.getLegalMoves();
            System.out.println(b);
            System.out.println(moves);
            for (Move m : moves) {
                b.makeMove(m);
                b.unmakeMove();
                try {
                    assertEquals(moves.size(), b.getLegalMoves().size());
                } catch (AssertionFailedError e) {
                    System.out.println("FAILED ASSERTION - DIAGNOSTIC INFORMATION BELOW");
                    System.out.println(m);
                    System.out.println(b);
                    System.out.println(b.getLegalMoves());
                    throw e;
                }
            }
        }
    }
}