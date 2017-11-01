package game;

import java.util.ArrayList;
import java.util.Scanner;

public class Game {

    public static Board board;

    public static void main(String[] args) {
        board = new Board();
        System.out.println("Welcome to a shit game of checkers");
        System.out.println("Current players turn: " + board.currentPlayer);
        board.printBoard();
        Scanner scan = new Scanner(System.in);
        while (true) {
            String myLine = scan.nextLine();
            switch (myLine) {
                case "exit":
                    return;
                case "move":
                    ArrayList<Move> availableMoves = board.getLegalMoves(board.currentPlayer);
                    System.out.println("Current moves for player: ");
                    for (Move m : availableMoves) {
                        System.out.println(m.toString());
                    }
                    System.out.println("Please enter a game pieces row");
                    int fromRow = Integer.parseInt(scan.nextLine());
                    System.out.println("Please enter a game pieces column");
                    int fromColumn = Integer.parseInt(scan.nextLine());
                    System.out.println("Please enter target location row");
                    int toRow = Integer.parseInt(scan.nextLine());
                    System.out.println("Please enter target location column");
                    int toColumn = Integer.parseInt(scan.nextLine());
                    Move move = new Move(fromRow, fromColumn, toRow, toColumn);
                    if (board.isLegalMove(board.currentPlayer, move)) {
                        board.makeMove(move);
                        board.currentPlayer = board.currentPlayer == 1 ? 2 : 1;
                        System.out.println("Moving game piece " + move.toString());
                        board.printBoard();
                        System.out.println("It is now player " + board.currentPlayer + "'s turn");
                    } else {
                        System.out.println("Please enter a valid move");
                    }
            }
        }
    }
}
