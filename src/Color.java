public enum Color {
    WHITE("White"), BLACK("Black");

    final String name;

    Color(String name) {
        this.name = name;
    }

    public Color swap() {
        if (this == WHITE) return BLACK;
        else return WHITE;
    }

    @Override
    public String toString() {
        return name;
    }
}
