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

    public int makeMove(Move move) {
        board[move.toRow][move.toColumn] = board[move.fromRow][move.fromColumn];
        board[move.fromRow][move.fromColumn] = EMPTY;
        if (move.fromRow - move.fromColumn == 2 || move.fromRow - move.toRow == -2) {
            System.out.println("called");
            int jumpRow = (move.fromRow + move.toRow) / 2;
            int jumpCol = (move.fromColumn + move.toColumn) / 2;
            board[jumpRow][jumpCol] = EMPTY;
        }
        if (move.toRow == 0 && board[move.toRow][move.toColumn] == RED) {
            board[move.toRow][move.toColumn] = RED_KING;
        } else if (move.toRow == 7 && board[move.toRow][move.toColumn] == BLACK) {
            board[move.toRow][move.toColumn] = BLACK_KING;
        }
        totalTurns++;
        int state = calculateBoardConditions();
        if (currentPlayer == RED && state == 0) {
            redTurns++;
            currentPlayer = BLACK;
        } else if (currentPlayer == BLACK && state == 0) {
            blackTurns++;
            currentPlayer = RED;
        }
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
            if (rowFrom > rowTo && board[rowFrom][columnFrom] == RED) {
                return false;
            }
            return !(board[rowCenter][columnCenter] != BLACK && board[rowCenter][columnCenter] != BLACK_KING);
        }
        else if (player == BLACK) {
            if (rowFrom > rowTo && board[rowFrom][columnFrom] == BLACK) {
                return false;
            }
            return !(board[rowCenter][columnCenter] != RED && board[rowCenter][columnCenter] != RED_KING);
        }
        return false;
    }

    public boolean isLegalMove(int player, int rowFrom, int columnFrom, int rowTo, int columnTo) {

        if (rowTo < 0 || rowTo >= 8 || columnTo < 0 || columnTo >= 8) {
            return false;
        }
        if (board[rowTo][columnTo] != EMPTY) {
            return false;
        }
        if (player == RED && currentPlayer == RED) {
            return !(rowTo > rowFrom && board[rowFrom][columnFrom] == RED);
        } else if (player == BLACK && currentPlayer == BLACK) {
            return !(rowTo < rowFrom && board[rowFrom][columnFrom] == BLACK);
        }
        return false;
    }

    public boolean isLegalMove(int player, Move move) {

        if (move.toRow < 0 || move.toRow >= 8 || move.toColumn < 0 || move.toColumn >= 8)
            return false;  // (r2,c2) is off the board.

        if (board[move.toRow][move.toColumn] != EMPTY)
            return false;  // (r2,c2) already contains a piece.

        if (player == RED) {
            if (board[move.fromRow][move.fromColumn] == RED && move.toRow > move.fromRow)
                return false;  // Regular red piece can only move down.
            return true;  // The move is legal.
        }
        else {
            if (board[move.fromRow][move.fromColumn] == BLACK && move.toRow < move.fromRow)
                return false;  // Regular black piece can only move up.
            return true;  // The move is legal.
        }
    }

    public void setupGame() {
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
