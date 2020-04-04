import java.util.List;

public class Perft {
    public final Board board;

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

    public void diagPerft(int depth) {
        long total = 0;

        List<Move> moves = board.getLegalMoves();
        System.out.format("%-10s %-10s %-10s\n", "move", "tot", "#");
        for (int i = 0; i < moves.size(); i++) {
            board.makeMove(moves.get(i));
            long d = perft(depth - 1);
            total += d;
            System.out.format("%-10s %-10d %-10d\n", moves.get(i), d, i);
            board.unmakeMove();
        }
        System.out.println("Total: " + total);
    }
}
