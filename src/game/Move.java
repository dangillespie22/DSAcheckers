package game;

public class Move {

    int fromRow;
    int fromColumn;
    int toRow;
    int toColumn;

    public Move (int fromRow, int fromColumn, int toRow, int toColumn) {
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
