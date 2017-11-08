package game;

import java.util.ArrayList;
import java.util.Random;

public class Board {

    private int[][] board = new int[8][8];
    private ArrayList<Move> moveSequence;
    private int currentPlayer;
    private int totalTurns;
    private int whiteTurns;
    private int blackTurns;
    private int whitePieces;
    private int blackPieces;
    private int whiteKingPieces;
    private int blackKingPieces;
    private static final int EMPTY = 0;
    private static final int WHITE = 1;
    private static final int BLACK = 2;
    private static final int WHITE_KING = 3;
    private static final int BLACK_KING = 4;

    public Board() {
        moveSequence = new ArrayList<>();
        totalTurns = 1;
        whiteTurns = 0;
        blackTurns = 0;
        whitePieces = 0;
        blackPieces = 0;
        whiteKingPieces = 0;
        blackKingPieces = 0;
        setupGame();
        calculateBoardConditions();
    }

    private Board(int[][] board, ArrayList<Move> moveSequence, int currentPlayer, int totalTurns) {
        this.moveSequence = moveSequence;
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.totalTurns = totalTurns;
        whiteTurns = 0;
        blackTurns = 0;
        whitePieces = 0;
        blackPieces = 0;
        whiteKingPieces = 0;
        blackKingPieces = 0;
        calculateBoardConditions();
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int[][] getBoard() {
        return board;
    }

    private boolean handleCapture(Move move) {
        if (move.isCapture()) {
            int middleRow = (move.fromRow + move.targetRow) / 2;
            int middleColumn = (move.fromColumn + move.targetColumn) / 2;
            board[middleRow][middleColumn] = EMPTY;
            return true;
        }
        return false;
    }

    public int makeMove(Move move) {

        moveSequence.add(move);
        doMove(board, move);
        int state = calculateBoardConditions();

        if (!move.isCapture() && state == 0) {
            if (currentPlayer == WHITE) {
                whiteTurns++;
                currentPlayer = BLACK;
            } else if (currentPlayer == BLACK) {
                blackTurns++;
                currentPlayer = WHITE;
            }
        }
        totalTurns++;

        return state;
    }

    private void doMove(int[][] board, Move move) {
        board[move.targetRow][move.targetColumn] = board[move.fromRow][move.fromColumn];
        board[move.fromRow][move.fromColumn] = EMPTY;

        if (move.targetRow == 0)
            if (board[move.targetRow][move.targetColumn] == WHITE) {
                board[move.targetRow][move.targetColumn] = WHITE_KING;
            }
        if (move.targetRow == 7)
            if (board[move.targetRow][move.targetColumn] == BLACK) {
                board[move.targetRow][move.targetColumn] = BLACK_KING;
            }
        if (move.isCapture()) {
            int middleRow = (move.fromRow + move.targetRow) / 2;
            int middleColumn = (move.fromColumn + move.targetColumn) / 2;
            board[middleRow][middleColumn] = EMPTY;
        }
    }

    private int calculateBoardConditions() {
        this.whitePieces = 0;
        this.blackPieces = 0;
        this.whiteKingPieces = 0;
        this.blackKingPieces = 0;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                switch(board[r][c]) {
                    case WHITE:
                        whitePieces++;
                        break;
                    case BLACK:
                        blackPieces++;
                        break;
                    case WHITE_KING:
                        whiteKingPieces++;
                        break;
                    case BLACK_KING:
                        blackKingPieces++;
                        break;
                }
            }
        }
        if (whitePieces == 0 && whiteKingPieces == 0) {
            return BLACK;
        } else if (blackPieces == 0 && blackKingPieces == 0) {
            return WHITE;
        }
        return 0;
    }

    public ArrayList<Move> getLegalMoves(int player) {
        ArrayList<Move> legalMoves = new ArrayList<>();
        int playerKing = player == WHITE ? WHITE_KING : BLACK_KING;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c] == player || board[r][c] == playerKing) {
                    if (isLegalCapture(player, r, c, r+1, c+1, r+2, c+2))
                        legalMoves.add(new Move(player, r, c, r+2, c+2));
                    if (isLegalCapture(player, r, c, r-1, c+1, r-2, c+2))
                        legalMoves.add(new Move(player, r, c, r-2, c+2));
                    if (isLegalCapture(player, r, c, r+1, c-1, r+2, c-2))
                        legalMoves.add(new Move(player, r, c, r+2, c-2));
                    if (isLegalCapture(player, r, c, r-1, c-1, r-2, c-2))
                        legalMoves.add(new Move(player, r, c, r-2, c-2));
                    if (isLegalMove(player,r,c,r+1,c+1))
                        legalMoves.add(new Move(player, r,c,r+1,c+1));
                    if (isLegalMove(player,r,c,r-1,c+1))
                        legalMoves.add(new Move(player, r,c,r-1,c+1));
                    if (isLegalMove(player,r,c,r+1,c-1))
                        legalMoves.add(new Move(player, r,c,r+1,c-1));
                    if (isLegalMove(player,r,c,r-1,c-1))
                        legalMoves.add(new Move(player, r ,c,r-1,c-1));
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

        if (player == WHITE) {
            return board[fromRow][fromColumn] != WHITE || targetRow <= fromRow && (board[middleRow][middleColumn] == BLACK || board[middleRow][middleColumn] == BLACK_KING);
        }
        else {
            return board[fromRow][fromColumn] != BLACK || targetRow >= fromRow && (board[middleRow][middleColumn] == WHITE || board[middleRow][middleColumn] == WHITE_KING);
        }
    }

    private boolean isLegalMove(int player, int fromRow, int fromColumn, int middleRow, int middleColumn) {

        if (middleRow < 0 || middleRow >= 8 || middleColumn < 0 || middleColumn >= 8)
            return false;

        if (board[middleRow][middleColumn] != EMPTY)
            return false;

        if (player == WHITE) {
            return board[fromRow][fromColumn] != WHITE || middleRow <= fromRow;
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
                        board[row][col] = WHITE;
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
        System.out.println("Turn: " + totalTurns);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j]);
                System.out.print("   ");
            }
            System.out.print("\n");
        }
    }

    public Board cloneBoard() {
        return new Board(getCurrentBoardClone(), moveSequence, currentPlayer, totalTurns);
    }

    public int[][] getCurrentBoardClone() {
        int[][] copy = new int[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, 8);
        }
        return copy;
    }

    public Move calculateBestMove() {

        ArrayList<Move> legalMoves = getLegalMoves(currentPlayer);
        Move bestMove = legalMoves.get(0);
        int bestMoveScore = 0;
        for (Move m : legalMoves) {
            int moveScore = getMoveScore(m);
            if (moveScore > bestMoveScore) {
                System.out.println(m.toString());
                bestMoveScore = moveScore;
                bestMove = m;
            }
        }
        return bestMove;
    }

    public int getMoveScore(Move move) {
        int actingPlayer = move.getPlayer();
        int opponent = actingPlayer == WHITE ? BLACK : WHITE;
        int moveScore = 0;
        int[][] copy = getCurrentBoardClone();

        doMove(copy, move);

        int actingPlayerPieces = 0;
        int opponentPieces = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (copy[r][c] == actingPlayer) {
                    actingPlayerPieces++;
                }
                if (copy[r][c] == actingPlayer + 2) {
                    actingPlayerPieces += 2;
                }
                if (copy[r][c] == opponent) {
                    opponentPieces++;
                }
                if (copy[r][c] == opponent + 2) {
                    opponentPieces += 2;
                }
            }
        }
        System.out.println(move.toString() + " Score: " + (actingPlayerPieces - opponentPieces));
        return actingPlayerPieces - opponentPieces;
    }


    public void printGameDetails() {
        int i = 1;
        for (Move m : moveSequence) {
            System.out.println("Move " + i + ": " + m.toString());
            i++;
        }
    }
}
