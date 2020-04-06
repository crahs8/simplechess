import java.io.IOException;

public class Main {
    public static void main(String[] rawArgs) throws IOException {
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

        if (args.divideDepth > 0) {
            Perft p = new Perft(board);
            p.diagPerft(args.divideDepth);
        } else if (args.testDepth > 0) {
            Search s = new Search(board);
            s.findBestMove(args.testDepth);
        } else {
            CLI c = new CLI(board);
            c.startCLI();
        }
    }

    private static class Args {
        private String FEN;
        private int divideDepth;
        private int testDepth;

        public Args(String[] args) {
            FEN = null;
            divideDepth = 0;
            testDepth = 0;

            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-f":
                        FEN = args[++i];
                        break;
                    case "-d":
                        try {
                            divideDepth = Integer.parseUnsignedInt(args[++i]);
                        } catch (NumberFormatException e) {
                            throw new ArgParseException("Argument -d " + args[i] + " not valid: " + e.getMessage());
                        }
                        break;
                    case "-t":
                        try {
                            testDepth = Integer.parseUnsignedInt(args[++i]);
                        } catch (NumberFormatException e) {
                            throw new ArgParseException("Argument -t " + args[i] + " not valid: " + e.getMessage());
                        }
                        break;
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
