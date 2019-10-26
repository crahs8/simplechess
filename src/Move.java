public class Move {
    private int r1;
    private int c1;
    private int r2;
    private int c2;
    // The char at the move's destination. Used to undo the move.
    private char destinationValue;

    public Move(int r1, int c1, int r2, int c2) {
        this.r1 = r1;
        this.c1 = c1;
        this.r2 = r2;
        this.c2 = c2;
        destinationValue = '?';
    }

    private static char columnToChar(int c) {
        if(c < 0 || c > 7) throw new IllegalArgumentException(c + " is not a valid column.");
        return (char)(97 + c);
    }

    public int getR1() {
        return r1;
    }

    public int getC1() {
        return c1;
    }

    public int getR2() {
        return r2;
    }

    public int getC2() {
        return c2;
    }

    public char getDestinationValue() {
        if(destinationValue == '?') throw new IllegalStateException("Move wasn't made yet.");
        return destinationValue;
    }

    public void setDestinationValue(char destinationValue) {
        this.destinationValue = destinationValue;
    }

    public String toString() {
        return columnToChar(c1) + "" + (8 - r1) + " to " + columnToChar(c2) + "" + (8 - r2);
    }
}
