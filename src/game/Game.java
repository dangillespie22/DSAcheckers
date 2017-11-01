package game;

public class Game {

    public Board board;
    public int redPlayer = 1;
    public int blackPlayer = 2;


    public Game() {
        this.board = new Board();
    }

    public Game(Board board) {
        this.board = board;
    }


}
