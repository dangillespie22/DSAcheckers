package game;

public class Move {

    private int player;
    int fromRow;
    int fromColumn;
    int targetRow;
    int targetColumn;
    private boolean isCapture;

    /*
    Constructor that sets the instance values of a Move
    Whether the move is a capture is calculated based on the inputs
     */
    public Move (int player, int fromRow, int fromColumn, int targetRow, int targetColumn) {
        this.player = player;
        this.fromRow = fromRow;
        this.fromColumn = fromColumn;
        this.targetRow = targetRow;
        this.targetColumn = targetColumn;
        this.isCapture = (fromRow - targetRow == 2 || fromRow - targetRow == -2);
    }

    /*
    Override function to allow Move objects to be compared in logical operators.
     */
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

    /*
    Allows .toString() to be called on a Move object resulting in a relevant output
     */
    @Override
    public String toString () {
       return ("[") + fromRow + ", " + fromColumn + "] -> [" + targetRow + ", " + targetColumn + "]" +
               (isCapture ? " capture at [" + (targetRow+fromRow)/2 + ", " + (targetColumn+fromColumn)/2 + "]" : "");
    }
}
