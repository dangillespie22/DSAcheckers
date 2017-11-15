package game;

import java.util.ArrayList;
import java.util.Random;

public class Board {

    private int[][] board = new int[8][8];
    private ArrayList<Move> moveSequence;
    private int currentPlayer;
    private int totalTurns;
    private int whitePieces;
    private int blackPieces;
    private int whiteKingPieces;
    private int blackKingPieces;
    private static final int EMPTY = 0;
    private static final int WHITE = 1;
    private static final int BLACK = 2;
    private static final int WHITE_KING = 3;
    private static final int BLACK_KING = 4;

    //Initialise board
    public Board() {
        moveSequence = new ArrayList<>();
        totalTurns = 1;
        whitePieces = 0;
        blackPieces = 0;
        whiteKingPieces = 0;
        blackKingPieces = 0;
        setupGame();
        calculateBoardConditions();
    }

    //Constructor used to when creating clones for evaluation function
    private Board(int[][] board, ArrayList<Move> moveSequence, int currentPlayer, int totalTurns) {
        this.moveSequence = moveSequence;
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.totalTurns = totalTurns;
        whitePieces = 0;
        blackPieces = 0;
        whiteKingPieces = 0;
        blackKingPieces = 0;
        calculateBoardConditions();
    }

    //Returns the current player
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    //Returns the current state of the board array
    public int[][] getBoard() {
        return board;
    }

    //Handles the game logic for when making a move
    public int makeMove(Move move) {

        moveSequence.add(move);
        doMove(board, move);
        int state = calculateBoardConditions();
        System.out.println((move.getPlayer() == 1 ? "White" : "Black") + " has played: " + move.toString());
        if (!move.isCapture() && state == 0) {
            if (currentPlayer == WHITE) {
                currentPlayer = BLACK;
            } else if (currentPlayer == BLACK) {
                currentPlayer = WHITE;
            }
        }
        totalTurns++;

        return state;
    }

    //Returns the list of moves carried out on the board
    public ArrayList<Move> getMoveSequence() {
        return moveSequence;
    }

    /*Carries out the changes to the board passed through the parameters
    //either the current board is passed through when a move is made, or evaluation function
    uses it to evaluate potential effect of moves
    */
    private void doMove(int[][] board, Move move) {
        board[move.targetRow][move.targetColumn] = board[move.fromRow][move.fromColumn];
        board[move.fromRow][move.fromColumn] = EMPTY;

        if (move.targetRow == 0) {
            if (board[move.targetRow][move.targetColumn] == WHITE) {
                board[move.targetRow][move.targetColumn] = WHITE_KING;
            }
        }
        if (move.targetRow == 7) {
            if (board[move.targetRow][move.targetColumn] == BLACK) {
                board[move.targetRow][move.targetColumn] = BLACK_KING;
            }
        }
        if (move.isCapture()) {
            int middleRow = (move.fromRow + move.targetRow) / 2;
            int middleColumn = (move.fromColumn + move.targetColumn) / 2;
            board[middleRow][middleColumn] = EMPTY;
        }
    }

    /*
    Calculates how many of each piece are on the board and returns the state of the board
    0 - Game ongoing
    1 - White has won the game
    2 - Black has won the game
     */
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
        if (getLegalMoves(getCurrentPlayer()).size() == 0) {  //Returns a win condition if a player has no valid moves
            return getCurrentPlayer() == WHITE ? BLACK : WHITE;
        }
        return 0;
    }

    //Method used to simplify method call for current board
    public ArrayList<Move> getLegalMoves(int player) {
        return getLegalMoves(this.board, player);
    }

    /*Returns the legal moves that a player has given a board array, used by evaluation
    function and by the previous method
    */
    private ArrayList<Move> getLegalMoves(int[][] boardState, int player) {
        ArrayList<Move> legalMoves = new ArrayList<>();
        int playerKing = player == WHITE ? WHITE_KING : BLACK_KING;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (boardState[r][c] == player || boardState[r][c] == playerKing) {
                    if (isLegalCapture(boardState, player, r, c, r+1, c+1, r+2, c+2))
                        legalMoves.add(new Move(player, r, c, r+2, c+2));
                    if (isLegalCapture(boardState, player, r, c, r-1, c+1, r-2, c+2))
                        legalMoves.add(new Move(player, r, c, r-2, c+2));
                    if (isLegalCapture(boardState, player, r, c, r+1, c-1, r+2, c-2))
                        legalMoves.add(new Move(player, r, c, r+2, c-2));
                    if (isLegalCapture(boardState, player, r, c, r-1, c-1, r-2, c-2))
                        legalMoves.add(new Move(player, r, c, r-2, c-2));
                    if (isLegalMove(boardState, player,r,c,r+1,c+1))
                        legalMoves.add(new Move(player, r,c,r+1,c+1));
                    if (isLegalMove(boardState, player,r,c,r-1,c+1))
                        legalMoves.add(new Move(player, r,c,r-1,c+1));
                    if (isLegalMove(boardState, player,r,c,r+1,c-1))
                        legalMoves.add(new Move(player, r,c,r+1,c-1));
                    if (isLegalMove(boardState, player,r,c,r-1,c-1))
                        legalMoves.add(new Move(player, r ,c,r-1,c-1));
                }
            }
        }
        return legalMoves;
    }

    /*
    Used to evaluate whether a potential move abides by the rules of a capture
     */
    private boolean isLegalCapture(int[][] boardState, int player, int fromRow, int fromColumn, int middleRow, int middleColumn, int targetRow, int targetColumn) {

        if (targetRow < 0 || targetRow >= 8 || targetColumn < 0 || targetColumn >= 8) {
            return false;
        }
        if (boardState[middleRow][middleColumn] == EMPTY) { //There must be a piece to capture
            return false;
        }
        if (boardState[targetRow][targetColumn] != EMPTY) { //The space behind the target capture must be empty
            return false;
        }
        if (player == WHITE) {
            if (boardState[fromRow][fromColumn] == WHITE_KING) { //King pieces can move in any direction
                return true;
            }
            if (targetRow <= fromRow) { //Normal game pieces are limited in movement
                if (boardState[middleRow][middleColumn] == BLACK || boardState[middleRow][middleColumn] == BLACK_KING) {
                    return true;
                }
            }
            return false;
        }
        else {
            if (boardState[fromRow][fromColumn] == BLACK_KING) { //King pieces can move in any direction
                return true;
            }
            if (targetRow >= fromRow) { //Normal game pieces are limited in movement
                if (boardState[middleRow][middleColumn] == WHITE || boardState[middleRow][middleColumn] == WHITE_KING) {
                    return true;
                }
            }
            return false;
        }
    }

    /*
    Used to evaluate whether a potential move abides by the rules of the game
     */
    private boolean isLegalMove(int[][] boardState, int player, int fromRow, int fromColumn, int targetRow, int targetColumn) {

        if (targetRow < 0 || targetRow >= 8 || targetColumn < 0 || targetColumn >= 8) {
            return false;
        }
        if (boardState[targetRow][targetColumn] != EMPTY) {
            return false;
        }
        if (player == WHITE) {
            return boardState[fromRow][fromColumn] != WHITE || targetRow <= fromRow;
        }
        else {
            return boardState[fromRow][fromColumn] != BLACK || targetRow >= fromRow;
        }
    }

    /*
    Initialises the board positions of a starting game and randomises the starting player
     */
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

    /*
    Returns the number of turns so far in the game
     */
    public int getTotalTurns() {
        return totalTurns;
    }

    /*
    Prints to the console the current game board array formatted to appear like a game board
     */
    public void printBoard() {
        System.out.println("\nTurn: " + totalTurns);
        System.out.println("Game board:");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j]);
                System.out.print("   ");
            }
            System.out.print("\n");
        }
    }

    /*
    Returns a clone of the current board for evaluation function
     */
    public Board cloneBoard() {
        return new Board(getCurrentBoardClone(), getMoveSequenceClone(), currentPlayer, totalTurns);
    }

    /*
    Returns a clone of the move sequences array
     */
    private ArrayList<Move> getMoveSequenceClone() {
        return new ArrayList<>(this.moveSequence);
    }

    /*
    Carries out a deep copy of the current board array state for use in cloning
     */
    private int[][] getCurrentBoardClone() {
        int[][] copy = new int[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, 8);
        }
        return copy;
    }

    /*
    Parent evaluation function
    Obtains list of legal moves for the current player
    Calculates which move scores the best from the evaluation function getMoveScore
     */
    public Move calculateBestMove() {
        ArrayList<Move> legalMoves = getLegalMoves(currentPlayer);
        Move bestMove = legalMoves.get(0);
        int bestMoveScore = getMoveScore(legalMoves.get(0));
        System.out.println("Legal moves for AI turn: ");
        for (Move m : legalMoves) {
            int moveScore = getMoveScore(m);
            System.out.println(m.toString() + " Score: " + moveScore);
            if (moveScore > bestMoveScore) {
                bestMoveScore = moveScore;
                bestMove = m;
            }
        }
        return bestMove;
    }

    /*
    The evaluation function that uses heuristics to score a potential move

     */
    private int getMoveScore(Move move) {
        int actingPlayer = move.getPlayer();
        int opponent = actingPlayer == WHITE ? BLACK : WHITE;
        int moveScore = 0;
        int[][] copy = getCurrentBoardClone();

        doMove(copy, move);

        for (int r = 0; r < 8; r++) {       //Scores each game piece on the board based on factors
            for (int c = 0; c < 8; c++) {
                if (copy[r][c] == actingPlayer) {
                    moveScore = moveScore + 10;
                    if (actingPlayer == WHITE && r < 4) {
                        moveScore = moveScore + 5; //Rewards white pieces that are in the opponents side of the board
                    }
                    else if (actingPlayer == BLACK && r > 3) {
                        moveScore = moveScore + 5; //Rewards black pieces that are in the opponents side of the board
                    }
                }
                if (copy[r][c] == actingPlayer + 2) {
                    moveScore = moveScore + 20;
                }
                if (copy[r][c] == opponent) {
                    moveScore = moveScore - 10;
                    if (opponent == WHITE && r < 4) {
                        moveScore = moveScore - 5;
                    }
                    else if (opponent == BLACK && r > 3) {
                        moveScore = moveScore - 5;
                    }
                }
                if (copy[r][c] == opponent + 2) {
                    moveScore  = moveScore - 20;
                }
            }
        }

        /*
        Adds value if future moves are captures and removes value if the move would endanger players game pieces
         */
        if (move.isCapture()) {
            ArrayList<Move> legalMoves = getLegalMoves(copy, currentPlayer);
            for (Move m : legalMoves) {
                if (m.isCapture()) {
                    moveScore = moveScore + 20;
                }
            }
        } else {
            ArrayList<Move> legalMoves = getLegalMoves(copy, opponent);
            for (Move m : legalMoves) {
                if (m.isCapture()) {
                    moveScore = moveScore - 30;
                }
            }
        }
        if (becomesKing(move)) {
            moveScore = moveScore + 20;
        }

        return moveScore;
    }

    /*
    Checks if a potential move, moves a game piece into a position that upgrades it to a king
     */
    private boolean becomesKing(Move move) {
        if (move.targetRow == 0) {
            if (board[move.targetRow][move.targetColumn] == WHITE) {
                return true;
            }
        }
        if (move.targetRow == 7) {
            if (board[move.targetRow][move.targetColumn] == BLACK) {
                return true;
            }
        }
        return false;
    }

    /*
    Prints the game details post game
     */
    public void printGameDetails() {
        int i = 1;
        for (Move m : moveSequence) {
            System.out.println("Move " + i + ": " + m.toString());
            i++;
        }
    }
}
