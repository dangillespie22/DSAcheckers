package game;

import java.util.ArrayList;
import java.util.Random;

public class Board {

    private int[][] board = new int[8][8];
    private int currentPlayer;
    private int totalTurns;
    private int redTurns;
    private int blackTurns;
    private int redPieces;
    private int blackPieces;
    private int redKingPieces;
    private int blackKingPieces;
    private static final int EMPTY = 0;
    private static final int RED = 1;
    private static final int BLACK = 2;
    private static final int RED_KING = 3;
    private static final int BLACK_KING = 4;

    public Board() {
        totalTurns = 1;
        redTurns = 0;
        blackTurns = 0;
        redPieces = 0;
        blackPieces = 0;
        redKingPieces = 0;
        blackKingPieces = 0;
        setupGame();
        calculateBoardConditions();
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int[][] getBoard() {
        return board;
    }

    private boolean handleCapture(Move move) {
        if (move.fromRow - move.targetRow == 2 || move.fromRow - move.targetRow == -2) {
            int middleRow = (move.fromRow + move.targetRow) / 2;
            int middleColumn = (move.fromColumn + move.targetColumn) / 2;
            board[middleRow][middleColumn] = EMPTY;
            return true;
        }
        return false;
    }

    public int makeMove(Move move) {

        board[move.targetRow][move.targetColumn] = board[move.fromRow][move.fromColumn];
        board[move.fromRow][move.fromColumn] = EMPTY;

        if (move.targetRow == 0 && board[move.targetRow][move.targetColumn] == RED)
            board[move.targetRow][move.targetColumn] = RED_KING;
        if (move.targetRow == 7 && board[move.targetRow][move.targetColumn] == BLACK)
            board[move.targetRow][move.targetColumn] = BLACK_KING;

        boolean isCapture = handleCapture(move);
        int state = calculateBoardConditions();

        if (!isCapture && state == 0) {
            if (currentPlayer == RED) {
                redTurns++;
                currentPlayer = BLACK;
            } else if (currentPlayer == BLACK) {
                blackTurns++;
                currentPlayer = RED;
            }
        }
        totalTurns++;

        return state;
    }

    private int calculateBoardConditions() {
        this.redPieces = 0;
        this.blackPieces = 0;
        this.redKingPieces = 0;
        this.blackKingPieces = 0;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                switch(board[r][c]) {
                    case RED:
                        redPieces++;
                        break;
                    case BLACK:
                        blackPieces++;
                        break;
                    case RED_KING:
                        redKingPieces++;
                        break;
                    case BLACK_KING:
                        blackKingPieces++;
                        break;
                }
            }
        }
        if (redPieces == 0 && redKingPieces == 0) {
            return BLACK;
        } else if (blackPieces == 0 && blackKingPieces == 0) {
            return RED;
        }
        return 0;
    }

    public ArrayList<Move> getLegalMoves(int player) {
        ArrayList<Move> legalMoves = new ArrayList<>();
        int playerKing = player == RED ? RED_KING : BLACK_KING;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c] == player || board[r][c] == playerKing) {
                    if (isLegalCapture(player, r, c, r+1, c+1, r+2, c+2))
                        legalMoves.add(new Move(r, c, r+2, c+2));
                    if (isLegalCapture(player, r, c, r-1, c+1, r-2, c+2))
                        legalMoves.add(new Move(r, c, r-2, c+2));
                    if (isLegalCapture(player, r, c, r+1, c-1, r+2, c-2))
                        legalMoves.add(new Move(r, c, r+2, c-2));
                    if (isLegalCapture(player, r, c, r-1, c-1, r-2, c-2))
                        legalMoves.add(new Move(r, c, r-2, c-2));
                    if (isLegalMove(player,r,c,r+1,c+1))
                        legalMoves.add(new Move(r,c,r+1,c+1));
                    if (isLegalMove(player,r,c,r-1,c+1))
                        legalMoves.add(new Move(r,c,r-1,c+1));
                    if (isLegalMove(player,r,c,r+1,c-1))
                        legalMoves.add(new Move(r,c,r+1,c-1));
                    if (isLegalMove(player,r,c,r-1,c-1))
                        legalMoves.add(new Move(r,c,r-1,c-1));
                }
            }
        }
        return legalMoves;
    }

    private boolean isLegalCapture(int player, int fromRow, int fromColumn, int middleRow, int middleColumn, int targetRow, int targetColumn) {

        if (targetRow < 0 || targetRow >= 8 || targetColumn < 0 || targetColumn >= 8)
            return false;

        if (board[targetRow][targetColumn] != EMPTY)
            return false;

        if (player == RED) {
            return board[fromRow][fromColumn] != RED || targetRow <= fromRow && (board[middleRow][middleColumn] == BLACK || board[middleRow][middleColumn] == BLACK_KING);
        }
        else {
            return board[fromRow][fromColumn] != BLACK || targetRow >= fromRow && (board[middleRow][middleColumn] == RED || board[middleRow][middleColumn] == RED_KING);
        }
    }

    private boolean isLegalMove(int player, int fromRow, int fromColumn, int middleRow, int middleColumn) {

        if (middleRow < 0 || middleRow >= 8 || middleColumn < 0 || middleColumn >= 8)
            return false;

        if (board[middleRow][middleColumn] != EMPTY)
            return false;

        if (player == RED) {
            return board[fromRow][fromColumn] != RED || middleRow <= fromRow;
        }
        else {
            return board[fromRow][fromColumn] != BLACK || middleRow >= fromRow;
        }
    }

    private void setupGame() {
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
        Random r = new Random();
        this.currentPlayer = r.nextInt(2) + 1;
    }

    public int getTotalTurns() {
        return totalTurns;
    }

    public void printBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j]);
                System.out.print("   ");
            }
            System.out.print("\n");
        }
        System.out.println("Red pieces: " + redPieces);
        System.out.println("Black pieces: " + blackPieces);
    }
}
