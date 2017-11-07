package game;

public class Move {

    int player;
    int fromRow;
    int fromColumn;
    int targetRow;
    int targetColumn;
    boolean isCapture;

    public Move (int player, int fromRow, int fromColumn, int targetRow, int targetColumn) {
        this.player = player;
        this.fromRow = fromRow;
        this.fromColumn = fromColumn;
        this.targetRow = targetRow;
        this.targetColumn = targetColumn;
        this.isCapture = false;
    }

    public Move (int player, int fromRow, int fromColumn, int targetRow, int targetColumn, boolean isCapture) {
        this.player = player;
        this.fromRow = fromRow;
        this.fromColumn = fromColumn;
        this.targetRow = targetRow;
        this.targetColumn = targetColumn;
        this.isCapture = isCapture;
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
       return "Player " + (player == 1 ? "White" : "Black") + " - [" + fromRow + ", " + fromColumn + "] -> [" + targetRow + ", " + targetColumn + "]";
    }
}
