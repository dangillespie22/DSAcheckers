package ui;

import game.Board;
import game.Move;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

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

    /*
    Initialises the board and UI elements
     */
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

    /*
    Resets a board to its initial state
     */
    private void restartGame() {
        this.currentBoard = new Board();
        gameStates = new ArrayList<>();
        gameStatesRedo = new ArrayList<>();
        paintBoard();
    }

    /*
    Restarts a game with a player vs player game type
     */
    private void startPvP() {
        this.gameType = PvP;
        restartGame();
    }

    /*
    Restarts a game with a player vs AI game type
     */
    private void startPvAI() {
        this.gameType = PvAI;
        restartGame();
    }

    /*
    Allows a player to undo a turn
     */
    private void backOneTurn() {
        /*
        A board is only saved to the states list once a move has been carried out
        Therefore for the first time a move is undone, the current state has to also be added to the second list
         */
        if (gameStatesRedo.isEmpty()) {
            gameStatesRedo.add(currentBoard.cloneBoard());
        }
        this.currentBoard = gameStates.get(gameStates.size()-1).cloneBoard(); //sets the board to the new state
        gameStatesRedo.add(0, gameStates.get(gameStates.size()-1).cloneBoard());
        if (gameStates.size() != 1) {
            gameStates.remove(gameStates.size()-1); //Finally removes the state from the state list
        }
        paintBoard();
    }

    /*
    Allows a player to redo a turn
     */
    private void forwardOneTurn() {
        gameStates.add(gameStatesRedo.get(0).cloneBoard());
        this.currentBoard = gameStatesRedo.get(0);
        gameStatesRedo.remove(0);
        paintBoard();
    }

    /*
    Initialises all of the UI elements based on the new board state
     */
    private void paintBoard() {
        buildBoard();
        drawSquares();
        drawPieces();
        buildElements();
        currentBoard.printBoard();
    }

    /*
    Builds the board UI element based on the checkers layout
     */
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

    /*
    Draws the squares onto the board in the grid layout defined previously
    Each square has an on-click function that serves functionally as the player input when making moves
     */
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

    /*
    Handlers the end game logic, printing the entire game sequence and giving the option to the
    player to continue playing the game by rewinding turns
     */
    private void calculateWinner(int winner) {
        System.out.println("The winner is " + (winner == WHITE ? "WHITE" : "BLACK"));
        currentBoard.printGameDetails();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Over!");
        alert.setHeaderText("The winner is " + (winner == WHITE ? "WHITE" : "BLACK"));
        alert.setContentText("If you wish to continue playing this game press close, otherwise press OK to clear the board");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                restartGame();
            }
        });
    }

    /*
    Used to reset game pieces once a game piece goes out of selected focus (when making a move)
     */
    private void resetPieceColours() {
        for (Circle c : whitePieces) {
            c.setFill(WHITE_COLOUR);
        }
        for (Circle c : blackPieces) {
            c.setFill(BLACK_COLOUR);
        }
    }

    /*
    Handles the logic when an AI makes a turn
     */
    private void doAiTurn() {
        Move move = currentBoard.calculateBestMove(); //Evaluation function to calculate best move
        System.out.println("AI has selected: " + move.toString());

        int state = currentBoard.makeMove(move);
        paintBoard();
        if (move.isCapture()) { //If move is a capture the player gets another turn
            doAiTurn();
        }
        if (state != 0) {
            calculateWinner(state);
        }
    }

    /*
    Draws the pieces onto the board based on the current state of the in-scope board
     */
    private void drawPieces() {
        int whiteCounter = 0;
        int blackCounter = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (currentBoard.getBoard()[i][j] == WHITE) {
                    final Circle piece = new Circle(SQUARES / 2 - 4, WHITE_COLOUR);
                    whitePieces[whiteCounter] = piece;
                    whitePieces[whiteCounter].setStroke(Color.BLACK);
                    whitePieces[whiteCounter].getStyleClass().add("whitePiece");
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
                    whitePieces[whiteCounter].getStyleClass().add("whitePiece");
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
                    blackPieces[blackCounter].getStyleClass().add("blackPiece");
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
                    blackPieces[blackCounter].getStyleClass().add("blackPiece");
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

    /*
    Initialises all of the UI elements that serves as inputs/information
    Also sets all of the style sheet classes for UI elements
     */
    private void buildElements() {

        layout.getChildren().removeAll();
        VBox gameInfo = new VBox();
        HBox playerInfo = new HBox();
        HBox turnInfo = new HBox();
        VBox buttons = new VBox();
        VBox stateList = new VBox();
        Button startPlayerVsPlayer = new Button();
        Button startPlayerVsAI = new Button();
        Button undo = new Button();
        Button redo = new Button();
        Button restartGame = new Button();

        Text playerText = new Text("Current player: ");
        Text currentPlayerText = new Text(currentBoard.getCurrentPlayer() == WHITE ? "White" : "Black");
        Text currentTurn = new Text("Current turn: " + currentBoard.getTotalTurns());
        Text stateTitle = new Text("Last 25 Turns");
        stateTitle.getStyleClass().add("stateTitle");
        stateList.getChildren().add(stateTitle);
        stateList.getChildren().add(new Text(""));
        ArrayList<Move> moveSequence = currentBoard.getMoveSequence();

        int x = 0;
        for (int i = moveSequence.size()-1; i >= 0; i--) {
            Move m = moveSequence.get(i);
            Text turn = new Text(m.toString());
            turn.getStyleClass().add(m.getPlayer() == WHITE ? "stateListWhite" : "stateListBlack");
            stateList.getChildren().add(turn);
            x++;
            if (x==25) {
                break;
            }
        }

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
        stateList.setMinWidth(300);

        undo.getStyleClass().add("button");
        gameInfo.getStyleClass().add("top");
        layout.getStyleClass().add("layout");
        gameBoard.getStyleClass().add("gameBoard");
        playerText.getStyleClass().add("turnText");
        currentTurn.getStyleClass().add("infoText");
        stateList.getStyleClass().add("stateList");
        currentPlayerText.getStyleClass().add(currentBoard.getCurrentPlayer() == WHITE ? "textWhite" : "textBlack");
        gameBoard.setStyle("-fx-border-color: " + (currentBoard.getCurrentPlayer() == WHITE ? "white" : "black"));

        playerInfo.getChildren().addAll(playerText, currentPlayerText);
        buttons.getChildren().addAll(startPlayerVsPlayer, startPlayerVsAI, restartGame, undo, redo);
        turnInfo.getChildren().add(currentTurn);
        gameInfo.getChildren().addAll(playerInfo, turnInfo);

        layout.setLeft(buttons);
        layout.setCenter(gameBoard);
        layout.setTop(gameInfo);
        layout.setRight(stateList);
    }
}