package ui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.Cell;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;

public class SplashPage extends Application {
    private static final int BOARD_DIM = 8;
    private static final int SQUARES = 64;
    private static final int PIECES = 12;
    private Cell[][] boardG = new Cell[8][8];
    private Circle[] p1p = new Circle[12];
    private Circle[] p2p = new Circle[12];
    private GridPane board = new GridPane();

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        buildBoard(board);
        drawSquares(board);
        drawPieces(board, p1p, p2p);
        intializeListeners();
        board.setPadding(new Insets(15, 15, 15, 15));
        Scene scene = new Scene(board, 700, 700);
        primaryStage.setTitle("Checkers");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void intializeListeners() {
        for (int i = 0; i < PIECES; i++) {
            p1p[i].setOnDragDetected(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    for (int i = 0; i < PIECES; i++) {
                        Dragboard db = p1p[i].startDragAndDrop(TransferMode.ANY);
                        ClipboardContent content = new ClipboardContent();
                        content.putString("Hello");
                        db.setContent(content);
                        System.out.println("setOnDragDetected");
                    }
                }
            });
            board.setOnDragOver(new EventHandler<DragEvent>() {
                @Override
                public void handle(DragEvent e) {
                    e.acceptTransferModes(TransferMode.ANY);
                    System.out.println("dragOverDetected");
                }
            });
            board.setOnDragDropped(new EventHandler<DragEvent>() {
                @Override
                public void handle(DragEvent e) {
                    e.acceptTransferModes(TransferMode.ANY);
                    System.out.println("dropDetected");
                }
            });
        }

    }

    private void buildBoard(GridPane board) {
        for (int i = 0; i < BOARD_DIM; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setMinHeight(SQUARES);
            rc.setMaxHeight(SQUARES);
            rc.setPrefHeight(SQUARES);
            rc.setValignment(VPos.CENTER);
            board.getRowConstraints().add(rc);

            ColumnConstraints cc = new ColumnConstraints();
            cc.setMinWidth(SQUARES);
            cc.setMaxWidth(SQUARES);
            cc.setPrefWidth(SQUARES);
            cc.setHalignment(HPos.CENTER);
            board.getColumnConstraints().add(cc);
        }
    }

    private void drawSquares(GridPane board) {
        Color[] sqColors = new Color[]{Color.BURLYWOOD, Color.CHOCOLATE};
        for (int i = 0; i < BOARD_DIM; i++) {
            for (int j = 0; j < BOARD_DIM; j++) {
                board.add(new Rectangle(SQUARES, SQUARES, sqColors[(i + j) % 2]), i, j);
            }
        }

    }

    private void drawPieces(GridPane board, Circle[] p1p, Circle[] p2p) {
        for (int i = 0; i < PIECES; i++) {
            p1p[i] = new Circle(SQUARES / 2 - 4, Color.FIREBRICK);
            p1p[i].setStroke(Color.BLACK);
            board.add(p1p[i], i % (BOARD_DIM / 2) * 2 + (2 * i / BOARD_DIM) % 2,
                    BOARD_DIM - 1 - (i * 2) / BOARD_DIM);

            p2p[i] = new Circle(SQUARES / 2 - 4, Color.SIENNA);
            p2p[i].setStroke(Color.BLACK);
            board.add(p2p[i], i % (BOARD_DIM / 2) * 2 + (1 + 2 * i / BOARD_DIM) % 2,
                    (i * 2) / BOARD_DIM);
        }
    }
}