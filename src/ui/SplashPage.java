package ui;

import game.Board;
import game.Move;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;

public class SplashPage extends Application {
    private static final int BOARD_DIM = 8;
    private static final int SQUARES = 64;
    private static final Color WHITE_COLOUR = Color.WHITE;
    private static final Color BLACK_COLOUR = Color.BLACK;
    private static final int WHITE = 1;
    private static final int BLACK = 2;
    private static final int WHITE_KING = 3;
    private static final int BLACK_KING = 4;
    private static final int PvP = 1;
    private static final int PvAI = 2;
    private static final int AIvAI = 3;
    private Circle selectedPiece = null;
    private Circle[] whitePieces = new Circle[12];
    private Circle[] blackPieces = new Circle[12];
    private GridPane gameBoard = new GridPane();
    private BorderPane layout = new BorderPane();
    private ArrayList<Board> gameStates;
    private ArrayList<Board> gameStatesRedo;
    private Board currentBoard;
    private int gameType;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        gameType = PvAI;
        gameStates = new ArrayList<>();
        gameStatesRedo = new ArrayList<>();
        this.currentBoard = new Board();
        paintBoard();
        Scene scene = new Scene(layout);
        scene.getStylesheets().add("game/style.css");
        primaryStage.setTitle("Checkers");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void restartGame() {
        this.currentBoard = new Board();
        gameStates = new ArrayList<>();
        gameStatesRedo = new ArrayList<>();
        paintBoard();
    }

    private void startPvP() {
        this.gameType = PvP;
        restartGame();
    }

    private void startPvAI() {
        this.gameType = PvAI;
        restartGame();
    }

    private void backOneTurn() {

        if (gameStatesRedo.isEmpty()) {
            gameStatesRedo.add(currentBoard.cloneBoard());
        }
        this.currentBoard = gameStates.get(gameStates.size()-1).cloneBoard();
        gameStatesRedo.add(0, gameStates.get(gameStates.size()-1).cloneBoard());
        if (gameStates.size() != 1) {
            gameStates.remove(gameStates.size()-1);
        }

        paintBoard();
    }

    private void forwardOneTurn() {
        gameStates.add(gameStatesRedo.get(0).cloneBoard());
        this.currentBoard = gameStatesRedo.get(0);
        gameStatesRedo.remove(0);
        paintBoard();
    }

    private void paintBoard() {
        buildBoard();
        drawSquares();
        drawPieces();
        System.out.println("Turn: " + currentBoard.getTotalTurns());
        System.out.println("Current player: " + (currentBoard.getCurrentPlayer() == WHITE ? "WHITE" : "BLACK"));
        currentBoard.printBoard();
        buildElements();
        for (Board b : gameStates) {
            System.out.println((b.getCurrentPlayer() == WHITE ? "White: " : "Black: ") + b.getTotalTurns());
        }
        System.out.println();
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
                        Move move = new Move(currentBoard.getCurrentPlayer(), GridPane.getRowIndex(selectedPiece), GridPane.getColumnIndex(selectedPiece), row, column);
                        ArrayList<Move> legalMoves = currentBoard.getLegalMoves(currentBoard.getCurrentPlayer());
                        if (legalMoves.contains(move)) {
                            gameStates.add(currentBoard.cloneBoard());
                            int state = currentBoard.makeMove(move);
                            gameStatesRedo.clear();
                            paintBoard();
                            if (move.isCapture()) {
                                System.out.println("Player captured " + (move.getPlayer() == WHITE ? "black piece" : "white piece "));
                            }
                            if (state != 0) {
                                calculateWinner(state);
                            }
                            if (gameType == PvAI && !move.isCapture()) {
                                doAiTurn();
                            }
                        }
                    }
                });
                gameBoard.add(rect, j, i);
            }
        }
    }

    private void calculateWinner(int winner) {
        System.out.println("The winner is " + (winner == WHITE ? "WHITE" : "BLACK"));
        currentBoard.printGameDetails();
    }

    private void resetPieceColours() {
        for (Circle c : whitePieces) {
            c.setFill(WHITE_COLOUR);
        }
        for (Circle c : blackPieces) {
            c.setFill(BLACK_COLOUR);
        }
    }

    private void doAiTurn() {
        ArrayList<Move> moves = currentBoard.getLegalMoves(currentBoard.getCurrentPlayer());
        Random r = new Random();
        int index = r.nextInt(moves.size());
        Move move = moves.get(index);
        currentBoard.makeMove(move);
        paintBoard();
        if (move.isCapture()) {
            System.out.println("AI Captured " + (move.getPlayer() == WHITE ? "Black piece" : "White piece "));
            doAiTurn();
        }
    }

    private void drawPieces() {
        int whiteCounter = 0;
        int blackCounter = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (currentBoard.getBoard()[i][j] == WHITE) {
                    final Circle piece = new Circle(SQUARES / 2 - 4, WHITE_COLOUR);
                    whitePieces[whiteCounter] = piece;
                    whitePieces[whiteCounter].setStroke(Color.BLACK);
                    gameBoard.add(piece, j, i);
                    piece.setOnMouseClicked(event -> {
                        if (currentBoard.getCurrentPlayer() == WHITE) {
                            resetPieceColours();
                            this.selectedPiece = piece;
                            piece.setFill(Color.MAROON);
                        }
                    });
                    whiteCounter++;
                }
                else if (currentBoard.getBoard()[i][j] == WHITE_KING) {
                    final Circle piece = new Circle(SQUARES / 2 - 4, WHITE_COLOUR);
                    whitePieces[whiteCounter] = piece;
                    whitePieces[whiteCounter].setStroke(Color.SADDLEBROWN);
                    whitePieces[whiteCounter].setStrokeWidth(5);
                    gameBoard.add(piece, j, i);
                    piece.setOnMouseClicked(event -> {
                        if (currentBoard.getCurrentPlayer() == WHITE) {
                            resetPieceColours();
                            this.selectedPiece = piece;
                            piece.setFill(Color.MAROON);
                        }
                    });
                    whiteCounter++;
                }
                else if (currentBoard.getBoard()[i][j] == BLACK) {
                    final Circle piece = new Circle(SQUARES / 2 - 4, BLACK_COLOUR);
                    blackPieces[blackCounter] = piece;
                    blackPieces[blackCounter].setStroke(Color.BLACK);
                    gameBoard.add(blackPieces[blackCounter], j, i);
                    blackPieces[blackCounter].setOnMouseClicked(event -> {
                        if (currentBoard.getCurrentPlayer() == BLACK) {
                            resetPieceColours();
                            this.selectedPiece = piece;
                            piece.setFill(Color.MAROON);
                        }
                    });
                    blackCounter++;
                }
                else if (currentBoard.getBoard()[i][j] == BLACK_KING) {
                    final Circle piece = new Circle(SQUARES / 2 - 4, BLACK_COLOUR);
                    blackPieces[blackCounter] = piece;
                    blackPieces[blackCounter].setStroke(Color.SADDLEBROWN);
                    blackPieces[blackCounter].setStrokeWidth(5);
                    gameBoard.add(blackPieces[blackCounter], j, i);
                    blackPieces[blackCounter].setOnMouseClicked(event -> {
                        if (currentBoard.getCurrentPlayer() == BLACK) {
                            resetPieceColours();
                            this.selectedPiece = piece;
                            piece.setFill(Color.MAROON);
                        }
                    });
                    blackCounter++;
                }
            }
        }
    }

    private void buildElements() {

        layout.getChildren().removeAll();
        VBox gameInfo = new VBox();
        HBox playerInfo = new HBox();
        HBox turnInfo = new HBox();
        VBox buttons = new VBox();
        Button startPlayerVsPlayer = new Button();
        Button startPlayerVsAI = new Button();
        Button undo = new Button();
        Button redo = new Button();
        Button restartGame = new Button();


        Text playerText = new Text("Current player: ");
        Text currentPlayerText = new Text(currentBoard.getCurrentPlayer() == WHITE ? "White" : "Black");
        Text currentTurn = new Text("Current turn: " + currentBoard.getTotalTurns());

        restartGame.setOnMouseClicked(event -> restartGame());

        undo.setOnMouseClicked(event -> {
            if (!gameStates.isEmpty()) {
                backOneTurn();
            } else {
                System.out.println("Make a turn if you wish to undo!");
            }
        });

        redo.setOnMouseClicked(event -> {
            if (!gameStatesRedo.isEmpty()) {
                forwardOneTurn();
            } else {
                System.out.println("No forward moves!");
            }
        });

        startPlayerVsPlayer.setOnMouseClicked(event -> startPvP());

        startPlayerVsAI.setOnMouseClicked(event -> {
            System.out.println("Starting AI game");
            startPvAI();
        });
        buttons.setPrefWidth(130);
        buttons.setStyle("-fx-spacing: 5;");

        startPlayerVsPlayer.setText("Start PvP Game");
        startPlayerVsPlayer.setMinWidth(buttons.getPrefWidth()-15);
        startPlayerVsAI.setText("Start PvAI Game");
        startPlayerVsAI.setMinWidth(buttons.getPrefWidth()-15);
        undo.setText("Undo last turn");
        undo.setMinWidth(buttons.getPrefWidth()-15);
        redo.setText("Redo last turn");
        redo.setMinWidth(buttons.getPrefWidth()-15);
        restartGame.setText("Restart Game");
        restartGame.setMinWidth(buttons.getPrefWidth()-15);
        gameBoard.setMinWidth(450);
        gameBoard.setMinHeight(450);

        undo.getStyleClass().add("button");
        gameInfo.getStyleClass().add("top");
        layout.getStyleClass().add("layout");
        gameBoard.getStyleClass().add("gameBoard");
        playerText.getStyleClass().add("turnText");
        currentTurn.getStyleClass().add("infoText");
        currentPlayerText.getStyleClass().add(currentBoard.getCurrentPlayer() == WHITE ? "textWhite" : "textBlack");
        gameBoard.setStyle("-fx-border-color: " + (currentBoard.getCurrentPlayer() == WHITE ? "white" : "black"));


        playerInfo.getChildren().addAll(playerText, currentPlayerText);
        buttons.getChildren().addAll(startPlayerVsPlayer, startPlayerVsAI, restartGame, undo, redo);
        turnInfo.getChildren().add(currentTurn);
        gameInfo.getChildren().addAll(playerInfo, turnInfo);

        layout.setLeft(buttons);
        layout.setCenter(gameBoard);
        layout.setTop(gameInfo);
    }
}