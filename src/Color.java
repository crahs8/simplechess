public enum Color {
    WHITE, BLACK;

    public Color swap() {
        if (this == WHITE) return BLACK;
        else return WHITE;
    }
}
