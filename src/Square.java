import java.util.Set;

/**
 * Helper class for square-related stuff.
 */
public class Square {
    public static int columnFromChar(char c) throws ParseException {
        Set<Character> validColumns = Set.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h');
        if (!validColumns.contains(c)) throw new ParseException("Column " + c + " not valid");
        return c - 97;
    }

    public static int rowFromChar(char c) throws ParseException {
        int r = 8 - (c - '0');
        if (r < 0 || r > 7) throw new ParseException("Row " + c + " not valid");
        return r;
    }
}
