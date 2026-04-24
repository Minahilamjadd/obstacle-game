package example.com.finalgameproject;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Ball Dash");
        primaryStage.setResizable(false);
        new GameManager(primaryStage).showMainMenu();
    }

    public static void main(String[] args) {
        launch(args);
    }
}