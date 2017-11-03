package ui;

import game.Board;
import game.Move;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;

public class SplashPage extends Application {
    private static final int BOARD_DIM = 8;
    private static final int SQUARES = 64;
    private static final Color RED_COLOUR = Color.WHITE;
    private static final Color BLACK_COLOUR = Color.BLACK;
    private static final int RED = 1;
    private static final int BLACK = 2;
    private static final int RED_KING = 3;
    private static final int BLACK_KING = 4;
    private Circle selectedPiece = null;
    private Circle[] redPieces = new Circle[12];
    private Circle[] blackPieces = new Circle[12];
    private GridPane gameBoard = new GridPane();
    private Board board;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        this.board = new Board();
        board.printBoard();
        paintBoard();
        gameBoard.setPadding(new Insets(15, 15, 15, 15));
        Scene scene = new Scene(gameBoard, 700, 700);
        primaryStage.setTitle("Checkers");
        primaryStage.setScene(scene);
        primaryStage.show();
        System.out.println(primaryStage.getWidth());
    }

    public void paintBoard() {
        buildBoard();
        drawSquares();
        drawPieces();
        System.out.println("Current player: " + (board.currentPlayer == RED ? "RED" : "BLACK"));
    }

    private void buildBoard() {
        for (int i = 0; i < BOARD_DIM; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setMinHeight(SQUARES);
            rc.setMaxHeight(SQUARES);
            rc.setPrefHeight(SQUARES);
            rc.setValignment(VPos.CENTER);
            gameBoard.getRowConstraints().add(rc);

            ColumnConstraints cc = new ColumnConstraints();
            cc.setMinWidth(SQUARES);
            cc.setMaxWidth(SQUARES);
            cc.setPrefWidth(SQUARES);
            cc.setHalignment(HPos.CENTER);
            gameBoard.getColumnConstraints().add(cc);
        }
    }

    private void drawSquares() {
        Color[] sqColors = new Color[]{Color.LIGHTSALMON, Color.SADDLEBROWN};
        for (int i = 0; i < BOARD_DIM; i++) {
            for (int j = 0; j < BOARD_DIM; j++) {
                Rectangle rect = new Rectangle(SQUARES, SQUARES, sqColors[(i + j) % 2]);
                final int row = i;
                final int column = j;
                rect.setOnMouseClicked(event -> {
                    if (selectedPiece != null) {
                        System.out.println(gameBoard.getRowIndex(selectedPiece) + ", " + gameBoard.getColumnIndex(selectedPiece));
                        Move move = new Move(gameBoard.getRowIndex(selectedPiece), gameBoard.getColumnIndex(selectedPiece), row, column);
                        if (board.isLegalMove(board.getCurrentPlayer(), move)) {
                            board.makeMove(move);
                            paintBoard();
                        }
                    }
                    System.out.println("Current location: [" + row + ", " + column + "]");
                });
                gameBoard.add(rect, j, i);
            }
        }
    }

    public void resetPieceColours() {
        for (Circle c : redPieces) {
            c.setFill(RED_COLOUR);
        }
        for (Circle c : blackPieces) {
            c.setFill(BLACK_COLOUR);
        }
    }

    private void drawPieces() {
        int redCounter = 0;
        int blackCounter = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.getBoard()[i][j] == RED) {
                    final Circle piece = new Circle(SQUARES / 2 - 4, RED_COLOUR);
                    redPieces[redCounter] = piece;
                    redPieces[redCounter].setStroke(Color.BLACK);
                    gameBoard.add(piece, j, i);
                    final int row = i;
                    final int column = j;
                    piece.setOnMouseClicked(event -> {
                        resetPieceColours();
                        this.selectedPiece = piece;
                        piece.setFill(Color.MAROON);
                        System.out.println("Current location: [" + row + ", " + column + "]");
                    });
                    redCounter++;
                }
                else if (board.getBoard()[i][j] == BLACK) {
                    final Circle piece = new Circle(SQUARES / 2 - 4, BLACK_COLOUR);
                    blackPieces[blackCounter] = piece;
                    blackPieces[blackCounter].setStroke(Color.BLACK);
                    gameBoard.add(blackPieces[blackCounter], j, i);
                    final int row = i;
                    final int column = j;
                    blackPieces[blackCounter].setOnMouseClicked(event -> {
                        resetPieceColours();
                        this.selectedPiece = piece;
                        piece.setFill(Color.MAROON);
                        System.out.println("Current location: [" + row + ", " + column + "]");
                    });
                    blackCounter++;
                }
            }
        }
    }
}