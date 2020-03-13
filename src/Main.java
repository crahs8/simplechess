public class Main {
    public static void main(String[] rawArgs) {
        Args args = new Args(rawArgs);

        Board board;
        try {
            board = args.FEN == null ? new Board() : FENParser.parse(args.FEN);
        } catch (FENParser.FENParseException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println(board);
        System.out.println("Legal Moves: " + board.getLegalMoves());
    }

    private static class Args {
        private String FEN;

        public Args(String[] args) {
            FEN = null;

            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-f")) {
                    FEN = args[++i];
                }
            }
        }

        public String getFEN() {
            return FEN;
        }
    }
}
