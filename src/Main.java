public class Main {

    public static void main(String[] args) {
        Board board = new Board();
        System.out.println(board);
        Move testMove = new Move(6, 4, 4, 4);
        System.out.println("Making move " + testMove);
        board.makeMove(testMove);
        System.out.println(board);
        System.out.println("Unmaking last move");
        board.unmakeMove();
        System.out.println(board);
    }
}
