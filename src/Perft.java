import java.util.List;

public class Perft {
    public Board board;

    public Perft(Board board) {
        this.board = board;
    }

    public long perft(int depth) {
        long nodes = 0;

        if (depth == 0) return 1;

        List<Move> moves = board.getLegalMoves();
        for (Move m : moves) {
            board.makeMove(m);
            nodes += perft(depth - 1);
            board.unmakeMove();
        }

        return nodes;
    }
}
