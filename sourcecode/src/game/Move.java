package game;

public class Move {

    private int player;
    int fromRow;
    int fromColumn;
    int targetRow;
    int targetColumn;
    private boolean isCapture;

    public Move (int player, int fromRow, int fromColumn, int targetRow, int targetColumn) {
        this.player = player;
        this.fromRow = fromRow;
        this.fromColumn = fromColumn;
        this.targetRow = targetRow;
        this.targetColumn = targetColumn;
        this.isCapture = (fromRow - targetRow == 2 || fromRow - targetRow == -2);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Move) {
            Move move = (Move) obj;
            return move.fromRow == fromRow && move.fromColumn == fromColumn && move.targetRow == targetRow && move.targetColumn == targetColumn;
        }
        return false;
    }

    public boolean isCapture() {
        return isCapture;
    }

    public int getPlayer() {
        return player;
    }

    @Override
    public String toString () {
       return ("[") + fromRow + ", " + fromColumn + "] -> [" + targetRow + ", " + targetColumn + "]" +
               (isCapture ? " capture at [" + (targetRow+fromRow)/2 + ", " + (targetColumn+fromColumn)/2 + "]" : "");
    }
}
