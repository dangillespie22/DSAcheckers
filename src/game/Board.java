package game;

import java.util.ArrayList;

public class Board {

    private static int[][] board = new int[8][8];
    private static final int EMPTY = 0;
    private static final int RED = 1;
    private static final int BLACK = 2;
    private static final int BLACK_KING = 3;
    private static final int RED_KING = 4;

    public Board() {
        setupGame();
    }

    public static void main(String[] args) {
        Board board = new Board();
        Move move = new Move(0, 0, 4, 4);
        Move redmove = new Move(5, 3, 0, 0);
        board.makeMove(move);
        board.makeMove(redmove);
        board.printBoard();
        board.getLegalMoves(BLACK);
        ArrayList<Move> availableMoves = board.getLegalMoves(BLACK);
        for (Move m : availableMoves) {
            System.out.println(m.toString());
        }
    }

    public void makeMove(Move move) {
        board[move.toRow][move.toColumn] = board[move.fromRow][move.fromColumn];
        board[move.fromRow][move.fromColumn] = EMPTY;
        if (move.fromRow - move.fromColumn == 2 || move.fromRow - move.toRow == -2) {
            int jumpRow = (move.fromRow + move.toRow) / 2;
            int jumpCol = (move.fromColumn + move.toColumn) / 2;
            board[jumpRow][jumpCol] = EMPTY;
        }
        if (move.toRow == 0 && board[move.toRow][move.toColumn] == RED) {
            board[move.toRow][move.toColumn] = RED_KING;
        } else if (move.toRow == 7 && board[move.toRow][move.toColumn] == BLACK) {
            board[move.toRow][move.toColumn] = BLACK_KING;
        }
    }

    public ArrayList<Move> getLegalMoves(int player) {
        ArrayList<Move> legalMoves = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c] == player || board[r][c] == player + 2) {
                    if (isLegalMove(player, r, c, r + 1, c + 1)) {
                        legalMoves.add(new Move(r, c, r + 1, c + 1));
                    }
                    if (isLegalMove(player, r, c, r + 1, c - 1)) {
                        legalMoves.add(new Move(r, c, r + 1, c - 1));
                    }
                    if (isLegalMove(player, r, c, r - 1, c + 1)) {
                        legalMoves.add(new Move(r, c, r - 1, c + 1));
                    }
                    if (isLegalMove(player, r, c, r - 1, c + 1)) {
                        legalMoves.add(new Move(r, c, r - 1, c - 1));
                    }
                }
            }
        }
        return legalMoves;
    }

    public boolean isLegalCapture(int player, int rowFrom, int columnFrom, int rowTo, int columnTo, int rowCenter, int columnCenter) {

        if (rowTo < 0 || rowTo >= 8 || columnTo < 0 || columnTo >= 8) {
            return false;
        }
        if (board[rowTo][columnTo] != EMPTY) {
            return false;
        }
        if (player == RED) {
            if (rowTo > rowFrom && board[rowFrom][columnFrom] == RED) {
                return false;
            }
            return !(board[rowCenter][columnCenter] != BLACK || board[rowCenter][columnCenter] != BLACK_KING);
        } else {
            if (rowTo > rowFrom && board[rowFrom][columnFrom] == BLACK) {
                return false;
            }
            return !(board[rowCenter][columnCenter] != RED || board[rowCenter][columnCenter] != RED_KING);
        }
    }

    public boolean isLegalMove(int player, int rowFrom, int columnFrom, int rowTo, int columnTo) {

        if (rowTo < 0 || rowTo >= 8 || columnTo < 0 || columnTo >= 8) {
            return false;
        }
        if (board[rowTo][columnTo] != EMPTY) {
            return false;
        }
        if (player == RED) {
            return !(rowTo > rowFrom && board[rowFrom][columnFrom] == RED);
        } else return !(rowTo < rowFrom && board[rowFrom][columnFrom] == BLACK);
    }

    private static void setupGame() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ( row % 2 == col % 2 ) {
                    if (row < 3)
                        board[row][col] = BLACK;
                    else if (row > 4)
                        board[row][col] = RED;
                    else
                        board[row][col] = EMPTY;
                }
                else {
                    board[row][col] = EMPTY;
                }
            }
        }
    }

    public void printBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j]);
            }
            System.out.print("\n");
        }
    }
}
