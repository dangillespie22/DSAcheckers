package game;

public class Move {

    private int fromRow;
    private int fromColumn;
    private int toRow;
    private int toColumn;

    Move (int fromRow, int fromColumn, int toRow, int toColumn) {
        this.fromRow = fromRow;
        this.fromColumn = fromColumn;
        this.toRow = toRow;
        this.toColumn = toColumn;
    }

    @Override
    public String toString () {
       return "[" + fromRow + ", " + fromColumn + "] -> [" + toRow + ", " + toColumn + "]";
    }
}
