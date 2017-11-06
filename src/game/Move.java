package game;

public class Move {

    int fromRow;
    int fromColumn;
    int targetRow;
    int targetColumn;

    public Move (int fromRow, int fromColumn, int targetRow, int targetColumn) {
        this.fromRow = fromRow;
        this.fromColumn = fromColumn;
        this.targetRow = targetRow;
        this.targetColumn = targetColumn;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Move) {
            Move move = (Move) obj;
            return move.fromRow == fromRow && move.fromColumn == fromColumn && move.targetRow == targetRow && move.targetColumn == targetColumn;
        }
        return false;
    }

    @Override
    public String toString () {
       return "[" + fromRow + ", " + fromColumn + "] -> [" + targetRow + ", " + targetColumn + "]";
    }
}
