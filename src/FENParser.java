import java.util.Set;

/**
 * Parses FEN strings, which are a way of specifying a chess position.
 */
public class FENParser {
    /**
     * Parses a given FEN string and returns a Board object corresponding to that string.
     * The last two fields of the FEN string are optional.
     *
     * @param FEN The FEN string.
     * @return a Board corresponding to the FEN string.
     * @throws FENParseException if the FEN string is invalid.
     */
    public static Board parse(String FEN) throws FENParseException {
        String[] fields = FEN.split(" ");
        if (fields.length < 4) throw new FENParseException("Not enough fields");

        Piece[][] position = parseFirst(fields[0]);
        Color toMove = parseSecond(fields[1]);
        Board.CastlingRights castlingRights = parseThird(fields[2]);
        int fiftyMoveClock = 0;
        int moveNumber = 1;
        if (fields.length >= 5) {
            fiftyMoveClock = parseFifth(fields[4]);
            if (fields.length == 6) moveNumber = parseSixth(fields[5]);
            else if (fields.length > 6) throw new FENParseException("Too many fields");
        }
        Move enPassant = parseFourth(fields[3], toMove, position, castlingRights, fiftyMoveClock);

        return new Board(position, toMove, enPassant, castlingRights, fiftyMoveClock, moveNumber);
    }

    private static Piece[][] parseFirst(String field) throws FENParseException {
        Piece[][] position = new Piece[8][8];

        String[] rows = field.split("/");
        if (rows.length != 8) throw new FENParseException("Wrong number of rows " + rows.length);
        for (int r = 0; r < 8; r++) {
            int c = 0;
            for (int i = 0; i < rows[r].length(); i++) {
                char p = rows[r].charAt(i);
                if (Character.isDigit(p)) {
                    int n = p - '0';
                    if (c + n > 8) throw new FENParseException("Bad row: " + rows[r]);
                    for (int j = 0; j < n; j++, c++) position[r][c] = Piece.EMPTY;
                }
                else try {
                    if (c > 7) throw new FENParseException("Bad row: " + rows[r]);
                    position[r][c] = Piece.fromChar(p);
                    c++;
                } catch (IllegalArgumentException e) {
                    throw new FENParseException("Illegal piece character '" + p + "' in row " + rows[r]);
                }
            }
        }

        return position;
    }

    private static Color parseSecond(String field) throws FENParseException {
        if (field.equals("w")) return Color.WHITE;
        else if (field.equals("b")) return Color.BLACK;
        else throw new FENParseException("Second field must be either 'w' or 'b'");
    }

    private static Board.CastlingRights parseThird(String field) throws FENParseException {
        if (field.equals("-")) return new Board.CastlingRights(false, false, false, false);
        boolean wKingCastlingRights = false;
        boolean wQueenCastlingRights = false;
        boolean bKingCastlingRights = false;
        boolean bQueenCastlingRights = false;
        for (char c : field.toCharArray()) {
            switch (c) {
                case 'K':
                    wKingCastlingRights = true;
                    break;
                case 'Q':
                    wQueenCastlingRights = true;
                    break;
                case 'k':
                    bKingCastlingRights = true;
                    break;
                case 'q':
                    bQueenCastlingRights = true;
                    break;
                default:
                    throw new FENParseException("Illegal castling character '" + c + "'");
            }
        }
        return new Board.CastlingRights(wKingCastlingRights, wQueenCastlingRights, bKingCastlingRights, bQueenCastlingRights);
    }

    private static int columnFromChar(char c) throws FENParseException {
        Set<Character> validColumns = Set.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h');
        if (!validColumns.contains(c)) throw new FENParseException("Illegal en passant target square");
        return c - 97;
    }

    private static Move parseFourth(String field, Color toMove, Piece[][] position, Board.CastlingRights castlingRights, int fiftyMoveClock)
            throws FENParseException {
        if (field.equals("-")) return null;
        if (field.length() != 2 || !Character.isDigit(field.charAt(1))) throw new FENParseException("Illegal en passant target square");
        int c = columnFromChar(field.charAt(0));
        int r = 8 - (field.charAt(1) - '0');
        if (r > 7) throw new FENParseException("Illegal en passant target square");
        int r1 = r + (toMove == Color.WHITE ? -1 : 1);
        int r2 = r + (toMove == Color.WHITE ? 1 : -1);
        return new RegularMove(r1, c, r2, c, position[r2][c], castlingRights, fiftyMoveClock);
    }

    private static int parseFifth(String field) throws FENParseException {
        try {
            return Integer.parseUnsignedInt(field);
        } catch (NumberFormatException e) {
            throw new FENParseException("Fifty move clock invalid: " + e.getMessage());
        }
    }

    private static int parseSixth(String field) throws FENParseException {
        try {
            int n = Integer.parseInt(field);
            if (n < 1) throw new FENParseException("Move number must be at least 1");
            return n;
        } catch (NumberFormatException e) {
            throw new FENParseException("Move number invalid: " + e.getMessage());
        }
    }

    public static class FENParseException extends Exception {
        public FENParseException(String message) {
            super("FEN not valid: " + message);
        }
    }
}
