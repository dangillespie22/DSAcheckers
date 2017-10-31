package ui;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;


public class SplashPage extends Application implements EventHandler {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Title of the window");

        Button button = new Button();
        button.setText("Click Me!");
        button.setOnAction(this);

        StackPane layout = new StackPane();
        layout.getChildren().add(button);

        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void handle(Event event) {
        if (event.getSource() instanceof Button) {
            System.out.println("Nice man");
        }
    }
}
