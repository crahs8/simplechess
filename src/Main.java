public class Main {
    public static void main(String[] rawArgs) {
        Args args;
        try {
            args = new Args(rawArgs);
        } catch (Args.ArgParseException e) {
            System.out.println(e.getMessage());
            return;
        }

        Board board;
        try {
            board = args.FEN == null ? new Board() : FENParser.parse(args.FEN);
        } catch (FENParser.FENParseException e) {
            System.out.println(e.getMessage());
            return;
        }

        if (args.perftDepth > 0) {
            Perft p = new Perft(board);
            p.diagPerft(args.perftDepth);
        } else {
            System.out.println(board);
            System.out.println("Legal Moves: " + board.getLegalMoves());
        }
    }

    private static class Args {
        private String FEN;
        private int perftDepth;

        public Args(String[] args) {
            FEN = null;
            perftDepth = 0;

            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-f":
                        FEN = args[++i];
                        break;
                    case "-p":
                        try {
                            perftDepth = Integer.parseUnsignedInt(args[++i]);
                        } catch (NumberFormatException e) {
                            throw new ArgParseException("Argument -p " + args[i] + " not valid: " + e.getMessage());
                        }

                }
            }
        }

        public static class ArgParseException extends RuntimeException {
            public ArgParseException(String message) {
                super(message);
            }
        }
    }
}
